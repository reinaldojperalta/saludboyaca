package sena.adso.modules.auth.servlet;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sena.adso.core.dao.TransactionManager;
import sena.adso.core.servlet.BaseServlet;
import sena.adso.modules.auth.dao.UsuarioDAO;
import sena.adso.modules.auth.model.Usuario;
import sena.adso.modules.auth.service.OTPService;

/**
 * Servlet de autenticación.
 * 
 * Flujo corregido:
 * 1. GET: Muestra login.jsp
 * 2. POST: Valida credenciales → LIMPIA intentos previos → genera OTP → envía
 * email → redirect a /otp
 */
@WebServlet(name = "LoginServlet", urlPatterns = { "/login" })
public class LoginServlet extends BaseServlet {

    private final UsuarioDAO usuarioDAO;
    private final OTPService otpService;

    public LoginServlet() {
        this.usuarioDAO = new UsuarioDAO();
        this.otpService = new OTPService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Si ya está autenticado y OTP verificado, ir al dashboard
        if (isFullyAuthenticated(req)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        forward(req, resp, "auth/login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String lang = req.getParameter("lang");

        // Guardar idioma seleccionado en sesión
        if (lang != null && (lang.equals("es") || lang.equals("en") || lang.equals("it"))) {
            req.getSession().setAttribute("lang", lang);
        }

        // ============================================================
        // FIX 1: Transacción para validar credenciales
        // Antes: usuarioDAO.validarLogin() se llamaba SIN TransactionManager.begin()
        // Ahora: envuelto en begin() / commit() / rollback() / close()
        // ============================================================
        TransactionManager.begin();
        Optional<Usuario> usuarioOpt;
        try {
            usuarioOpt = usuarioDAO.validarLogin(username, password);
            TransactionManager.commit();
        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        } finally {
            TransactionManager.close();
        }
        // ============================================================

        // Credenciales incorrectas → volver al login con error
        if (usuarioOpt.isEmpty()) {
            req.setAttribute("error", getI18n(req, "login.error.credenciales"));
            forward(req, resp, "auth/login");
            return;
        }

        Usuario usuario = usuarioOpt.get();

        // ============================================================
        // FIX 2: LIMPIAR intentos fallidos previos ANTES de verificar el límite
        //
        // ¿Por qué? Cada vez que intentas loguear, se crea un token en otp_tokens.
        // Si no los limpias, se acumulan y "haExcedidoIntentos" dice que ya pasaste
        // el límite aunque nunca hayas fallado el OTP.
        //
        // Paso 2a: Limpiar tokens viejos del usuario
        // ============================================================
        TransactionManager.begin();
        try {
            otpService.limpiarIntentosFallidos(usuario.getId());
            TransactionManager.commit();
        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        } finally {
            TransactionManager.close();
        }
        // ============================================================

        // ============================================================
        // Paso 2b: Ahora SÍ verificar si excedió intentos (después de limpiar)
        // ============================================================
        TransactionManager.begin();
        boolean excedido;
        try {
            excedido = otpService.haExcedidoIntentos(usuario.getId(), 3);
            TransactionManager.commit();
        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        } finally {
            TransactionManager.close();
        }
        // ============================================================

        if (excedido) {
            req.setAttribute("error", "Demasiados intentos. Espere 5 minutos.");
            forward(req, resp, "auth/login");
            return;
        }

        String asunto = getI18n(req, "otp.email.asunto");
        // Pedimos la plantilla "cruda" (con el {0} sin llenar)
        String plantillaCuerpo = getI18n(req, "otp.email.cuerpo");

        // ============================================================
        // FIX 3: Transacción para guardar el OTP en BD y enviar email
        // otpService.generarYEnviar() usa otpTokenDAO.insertar() internamente
        // ============================================================
        TransactionManager.begin();
        try {
            otpService.generarYEnviar(usuario.getId(), usuario.getEmail(), asunto, plantillaCuerpo);
            TransactionManager.commit();
        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        } finally {
            TransactionManager.close();
        }
        // ============================================================

        // Guardar datos en sesión (OTP aún no verificado)
        HttpSession session = req.getSession();
        session.setAttribute("usuario", usuario);
        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("usuarioNombre", usuario.getNombreCompleto());
        session.setAttribute("usuarioRol", usuario.getRol());

        // FIX 4: Guardar email para que OTPServlet lo muestre enmascarado
        session.setAttribute("usuarioEmail", usuario.getEmail());
        session.setAttribute("otpVerificado", false);

        // Iniciar auditoría
        audit(req, "USER_LOGIN", "Usuario", usuario.getId());

        // Redirigir a verificación OTP
        resp.sendRedirect(req.getContextPath() + "/otp");
    }

    // ============================================================
    // MÉTODO NUEVO: Verifica si usuario ya pasó login + OTP
    // ============================================================
    private boolean isFullyAuthenticated(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null)
            return false;

        Object usuario = session.getAttribute("usuario");
        Object otpVerificado = session.getAttribute("otpVerificado");

        return usuario != null && Boolean.TRUE.equals(otpVerificado);
    }
}