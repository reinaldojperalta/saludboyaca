package sena.adso.core.exception;

public class BusinessException extends RuntimeException {

    private final String i18nKey;

    public BusinessException(String message) {
        super(message);
        this.i18nKey = "error.servidor";
    }

    public BusinessException(String message, String i18nKey) {
        super(message);
        this.i18nKey = i18nKey;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.i18nKey = "error.servidor";
    }

    public BusinessException(String message, String i18nKey, Throwable cause) {
        super(message, cause);
        this.i18nKey = i18nKey;
    }

    public String getI18nKey() {
        return i18nKey;
    }
}
