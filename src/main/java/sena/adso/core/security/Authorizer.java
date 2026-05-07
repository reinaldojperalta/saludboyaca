package sena.adso.core.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interfaz para estrategias de autorización.
 * 
 * Desacopla el AuthFilter de la implementación concreta de cómo se verifica
 * que un usuario autenticado tiene permiso para acceder a un recurso.
 */
public interface Authorizer {

    /**
     * Verifica si el usuario autenticado tiene permiso para la ruta actual.
     * 
     * @param req  Request HTTP (contiene la URI y la sesión con el usuario)
     * @param resp Response HTTP
     * @return true si el usuario tiene permiso para acceder
     */
    boolean authorize(HttpServletRequest req, HttpServletResponse resp);

    /**
     * Obtiene el permission key requerido para una ruta específica.
     * 
     * @param uri URI del request (ej: /citas, /pacientes/crear)
     * @return permission key (ej: "cita:crear") o null si no hay mapeo definido
     */
    String resolvePermissionKey(String uri);
}