package sena.adso.core.exception;

/**
 * Excepción de acceso a datos. Envuelve cualquier SQLException
 * para no exponer detalles de la base de datos a capas superiores.
 * 
 * HTTP Status: 500 Internal Server Error
 */
public class DataAccessException extends RuntimeException {

    private final String operation;
    private final String entity;

    public DataAccessException(String message) {
        super(message);
        this.operation = "UNKNOWN";
        this.entity = "UNKNOWN";
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
        this.operation = "UNKNOWN";
        this.entity = "UNKNOWN";
    }

    public DataAccessException(String operation, String entity, String message, Throwable cause) {
        super("Error en " + operation + " sobre " + entity + ": " + message, cause);
        this.operation = operation;
        this.entity = entity;
    }

    public DataAccessException(String operation, String entity, Throwable cause) {
        super("Error en " + operation + " sobre " + entity, cause);
        this.operation = operation;
        this.entity = entity;
    }

    public DataAccessException(String operation, String entity, String message) {
        super("Error en " + operation + " sobre " + entity + ": " + message);
        this.operation = operation;
        this.entity = entity;
    }

    public String getOperation() {
        return operation;
    }

    public String getEntity() {
        return entity;
    }
}