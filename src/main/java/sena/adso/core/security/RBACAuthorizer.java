package sena.adso.core.security;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sena.adso.core.rbac.RBACCache;

/**
 * Implementación de Authorizer basada en RBAC.
 * 
 * Mapea URIs a permission keys y consulta RBACCache para verificar
 * si el rol del usuario tiene el permiso requerido.
 * 
 * Si una ruta no tiene mapeo explícito, se permite por defecto
 * (la autenticación ya fue verificada por Authenticator).
 */
public class RBACAuthorizer implements Authorizer {

    private final RBACCache rbacCache;

    // Mapeo de patrones de URI a permission keys
    private final Map<String, String> uriPermissionMap;

    public RBACAuthorizer(RBACCache rbacCache) {
        this.rbacCache = rbacCache;
        this.uriPermissionMap = buildUriPermissionMap();
    }

    @Override
    public boolean authorize(HttpServletRequest req, HttpServletResponse resp) {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String relativeUri = uri.substring(contextPath.length());

        String rolName = extractRolFromSession(req);

        String requiredPermission = resolvePermissionKey(relativeUri);

        if (requiredPermission == null) {
            return true;
        }

        boolean tiene = rbacCache.hasPermission(rolName, requiredPermission);

        if (!tiene) {
        }

        return tiene;
    }

    @Override
    public String resolvePermissionKey(String uri) {
        // Buscar match exacto primero
        if (uriPermissionMap.containsKey(uri)) {
            return uriPermissionMap.get(uri);
        }

        // Buscar por prefijo (ej: /citas/crear -> /citas/*)
        for (Map.Entry<String, String> entry : uriPermissionMap.entrySet()) {
            String pattern = entry.getKey();
            if (pattern.endsWith("/*") && uri.startsWith(pattern.replace("/*", ""))) {
                return entry.getValue();
            }
        }

        return null;
    }

    // ============================================================
    // PRIVADOS
    // ============================================================

    private String extractRolFromSession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null)
            return null;

        Object rol = session.getAttribute("usuarioRol");
        return rol != null ? rol.toString() : null;
    }

    /**
     * Construye el mapeo de URIs a permission keys.
     * 
     * Este mapeo es estático por diseño. Si se necesita dinámico,
     * se cargaría desde BD o archivo de configuración.
     */
    private Map<String, String> buildUriPermissionMap() {
        Map<String, String> map = new HashMap<>();

        // Dashboard
        map.put("/dashboard", "dashboard:ver");

        // Pacientes
        map.put("/pacientes", "paciente:listar");
        map.put("/pacientes/crear", "paciente:crear");
        map.put("/pacientes/editar", "paciente:editar");
        map.put("/pacientes/eliminar", "paciente:eliminar");

        // Citas
        map.put("/citas", "cita:listar");
        map.put("/citas/crear", "cita:crear");
        map.put("/citas/editar", "cita:editar");
        map.put("/citas/eliminar", "cita:eliminar");
        map.put("/citas/cambiar-estado", "cita:cambiar_estado");

        // Horarios
        map.put("/horarios", "horario:ver");

        // Usuarios (solo admin)
        map.put("/usuarios", "usuario:administrar");

        return map;
    }
}