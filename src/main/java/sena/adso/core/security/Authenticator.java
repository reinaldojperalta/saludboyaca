package sena.adso.core.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interfaz para estrategias de autenticación.
 * 
 * Desacopla el AuthFilter de la implementación concreta de cómo se valida
 * que un usuario está autenticado.
 */
public interface Authenticator {

    /**
     * Verifica si el request actual pertenece a un usuario autenticado.
     * 
     * @param req  Request HTTP
     * @param resp Response HTTP (para redirecciones si es necesario)
     * @return true si el usuario está autenticado y su sesión es válida
     */
    boolean authenticate(HttpServletRequest req, HttpServletResponse resp);

    /**
     * Verifica si el usuario ha completado la verificación OTP.
     * 
     * @param req Request HTTP
     * @return true si el OTP fue verificado en esta sesión
     */
    boolean isOtpVerified(HttpServletRequest req);
}