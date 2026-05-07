package sena.adso.core.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sena.adso.core.audit.AuditContext;
import sena.adso.core.audit.AuditEntry;
import sena.adso.core.rbac.RBACCache;
import sena.adso.core.util.I18nHelper;
import sena.adso.modules.auth.model.Usuario;

/**
 * Servlet base abstracto que proporciona utilidades comunes a todos los
 * Servlets.
 * 
 * NO impone un switch de acciones genérico — cada Servlet concreto maneja
 * su propio flujo doGet/doPost. En cambio, ofrece:
 * 
 * - forward(): Enviar a JSP dentro de WEB-INF/views/
 * - sendError(): Enviar error con mensaje i18n
 * - getCurrentUser(): Obtener usuario de la sesión
 * - requireAuth(): Verificar sesión (fallback si AuthFilter falla)
 * - hasPermission(): Verificar permiso RBAC
 * - audit(): Iniciar contexto de auditoría
 * - getI18n(): Obtener mensaje traducido
 * - redirect(): Redirección con context path
 */
public abstract class BaseServlet extends HttpServlet {

    // ============================================================
    // FORWARD Y REDIRECT
    // ============================================================

    /**
     * Forward a una vista JSP dentro de WEB-INF/views/.
     * 
     * @param view Ruta relativa sin .jsp (ej: "auth/login", "dashboard")
     */
    protected void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + view + ".jsp").forward(req, resp);
    }

    /**
     * Redirección HTTP con context path automático.
     */
    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    // ============================================================
    // ERRORES
    // ============================================================

    /**
     * Envía un error a la vista error.jsp con mensaje i18n.
     * 
     * @param i18nKey Clave del properties (ej: "error.acceso")
     * @param status  Código HTTP (400, 403, 500, etc.)
     */
    protected void sendError(HttpServletRequest req, HttpServletResponse resp,
            String i18nKey, int status) throws ServletException, IOException {

        req.setAttribute("error", getI18n(req, i18nKey));
        req.setAttribute("statusCode", status);
        resp.setStatus(status);
        forward(req, resp, "error");
    }

    /**
     * Envía un error con mensaje literal (no i18n).
     */
    protected void sendErrorLiteral(HttpServletRequest req, HttpServletResponse resp,
            String message, int status) throws ServletException, IOException {

        req.setAttribute("error", message);
        req.setAttribute("statusCode", status);
        resp.setStatus(status);
        forward(req, resp, "error");
    }

    // ============================================================
    // SESIÓN Y USUARIO
    // ============================================================

    /**
     * Obtiene el usuario autenticado de la sesión.
     * 
     * @return null si no hay sesión o no hay usuario
     */
    protected Usuario getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null)
            return null;

        Object usuario = session.getAttribute("usuario");
        return (usuario instanceof Usuario) ? (Usuario) usuario : null;
    }

    /**
     * Obtiene el ID del usuario autenticado.
     */
    protected Integer getCurrentUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null)
            return null;

        Object id = session.getAttribute("usuarioId");
        return (id instanceof Integer) ? (Integer) id : null;
    }

    /**
     * Obtiene el rol del usuario autenticado.
     */
    protected String getCurrentUserRol(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null)
            return null;

        Object rol = session.getAttribute("usuarioRol");
        return rol != null ? rol.toString() : null;
    }

    /**
     * Verifica si hay sesión activa. Si no, redirige a login.
     * 
     * @return true si hay sesión (el request puede continuar)
     */
    protected boolean requireAuth(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        if (getCurrentUser(req) == null) {
            redirect(req, resp, "/login");
            return false;
        }
        return true;
    }

    /**
     * Verifica si el OTP fue verificado. Si no, redirige a /otp.
     */
    protected boolean requireOtpVerified(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);
        if (session == null) {
            redirect(req, resp, "/login");
            return false;
        }

        Object otpVerificado = session.getAttribute("otpVerificado");
        if (!Boolean.TRUE.equals(otpVerificado)) {
            redirect(req, resp, "/otp");
            return false;
        }

        return true;
    }

    // ============================================================
    // PERMISOS RBAC
    // ============================================================

    /**
     * Verifica si el usuario actual tiene un permiso específico.
     * Requiere que RBACCache esté cargado en ServletContext.
     */
    protected boolean hasPermission(HttpServletRequest req, String permissionKey) {
        String rol = getCurrentUserRol(req);
        if (rol == null) {
            return false;
        }

        RBACCache cache = (RBACCache) req.getServletContext().getAttribute("rbacCache");
        if (cache == null) {
            return false;
        }

        boolean result = cache.hasPermission(rol, permissionKey);
        return result;
    }

    /**
     * Requiere un permiso. Si no lo tiene, envía 403.
     * 
     * @return true si tiene permiso
     */
    protected boolean requirePermission(HttpServletRequest req, HttpServletResponse resp,
            String permissionKey) throws ServletException, IOException {

        String rol = getCurrentUserRol(req);
        RBACCache cache = (RBACCache) req.getServletContext().getAttribute("rbacCache");

        if (!hasPermission(req, permissionKey)) {
            sendError(req, resp, "error.acceso", HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    // ============================================================
    // AUDITORÍA
    // ============================================================

    /**
     * Inicia el contexto de auditoría para esta operación.
     * Debe llamarse al inicio de cualquier operación que quiera ser auditada.
     * 
     * @param action     Nombre de la acción (ej: "CITA_CREATE", "USER_LOGIN")
     * @param entityType Tipo de entidad afectada (ej: "Cita", "Usuario")
     * @param entityId   ID de la entidad (puede ser null si aún no se genera)
     */
    protected void audit(HttpServletRequest req, String action, String entityType, Integer entityId) {
        Long longId = (entityId != null) ? entityId.longValue() : null;
        AuditEntry entry = new AuditEntry();
        entry.setUserId(getCurrentUserId(req) != null ? Long.valueOf(getCurrentUserId(req)) : null);
        entry.setUsername(getCurrentUser(req) != null ? getCurrentUser(req).getUsername() : "anonymous");
        entry.setActionName(action);
        entry.setEntityType(entityType);
        entry.setEntityId(longId);
        entry.setIpAddress(req.getRemoteAddr());
        entry.setUserAgent(req.getHeader("User-Agent"));

        AuditContext.set(entry);
    }

    /**
     * Inicia auditoría sin entityId (se actualizará después).
     */
    protected void audit(HttpServletRequest req, String action, String entityType) {
        audit(req, action, entityType, null);
    }

    // ============================================================
    // INTERNACIONALIZACIÓN
    // ============================================================

    /**
     * Obtiene un mensaje traducido según el idioma de la sesión.
     */
    protected String getI18n(HttpServletRequest req, String key) {
        return I18nHelper.get(req, key);
    }

    /**
     * Obtiene un mensaje traducido con parámetros.
     */
    protected String getI18n(HttpServletRequest req, String key, Object... params) {
        return I18nHelper.get(req, key, params);
    }

    // ============================================================
    // UTILIDADES HTTP
    // ============================================================

    /**
     * Verifica si el request es AJAX (XMLHttpRequest).
     */
    protected boolean isAjax(HttpServletRequest req) {
        return "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));
    }

    /**
     * Obtiene un parámetro del request con valor por defecto.
     */
    protected String getParam(HttpServletRequest req, String name, String defaultValue) {
        String value = req.getParameter(name);
        return (value != null && !value.isBlank()) ? value.trim() : defaultValue;
    }

    /**
     * Obtiene un parámetro entero del request.
     * 
     * @return null si no es válido
     */
    protected Integer getIntParam(HttpServletRequest req, String name) {
        try {
            return Integer.parseInt(req.getParameter(name));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}