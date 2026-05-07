package sena.adso.core.exception;

/**
 * Excepción de autenticación/autorización.
 *
 * Subtipos:
 * - Credenciales inválidas → 401 Unauthorized
 * - Permiso insuficiente → 403 Forbidden
 * - OTP inválido/expirado → 401 Unauthorized
 * - Sesión expirada → 401 Unauthorized
 *
 * HTTP Status: 401 o 403 según el tipo
 */
public class AuthException extends RuntimeException {

    public enum Type {
        CREDENTIALS_INVALID, // 401
        OTP_INVALID, // 401
        OTP_EXPIRED, // 401
        SESSION_EXPIRED, // 401
        INSUFFICIENT_PERMISSION // 403
    }

    private final Type type;
    private final String i18nKey;
    private final int httpStatus;

    public AuthException(Type type, String message) {
        super(message);
        this.type = type;
        this.i18nKey = resolveI18nKey(type);
        this.httpStatus = resolveHttpStatus(type);
    }

    public AuthException(Type type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
        this.i18nKey = resolveI18nKey(type);
        this.httpStatus = resolveHttpStatus(type);
    }

    private static String resolveI18nKey(Type type) {
        return switch (type) {
            case CREDENTIALS_INVALID -> "login.error.credenciales";
            case OTP_INVALID -> "otp.error";
            case OTP_EXPIRED -> "otp.error.expirado";
            case SESSION_EXPIRED -> "error.sesion.expirada";
            case INSUFFICIENT_PERMISSION -> "error.acceso";
        };
    }

    private static int resolveHttpStatus(Type type) {
        return switch (type) {
            case CREDENTIALS_INVALID, OTP_INVALID, OTP_EXPIRED, SESSION_EXPIRED -> 401;
            case INSUFFICIENT_PERMISSION -> 403;
        };
    }

    public Type getType() {
        return type;
    }

    public String getI18nKey() {
        return i18nKey;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
