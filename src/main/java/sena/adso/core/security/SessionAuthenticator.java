package sena.adso.core.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Implementación de Authenticator basada en sesiones HTTP.
 * 
 * Verifica:
 * 1. Que exista una sesión activa
 * 2. Que la sesión tenga el atributo "usuario"
 * 3. Que el OTP haya sido verificado (otpVerificado = true)
 */
public class SessionAuthenticator implements Authenticator {

    @Override
    public boolean authenticate(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession(false);

        if (session == null) {
            return false;
        }

        Object usuario = session.getAttribute("usuario");

        if (usuario == null) {
            return false;
        }

        boolean otpOk = isOtpVerified(req);

        if (!otpOk) {
        }

        return otpOk;
    }

    @Override
    public boolean isOtpVerified(HttpServletRequest req) {
        HttpSession session = req.getSession(false);

        if (session == null) {
            return false;
        }

        Object otpVerified = session.getAttribute("otpVerificado");
        return Boolean.TRUE.equals(otpVerified);
    }
}