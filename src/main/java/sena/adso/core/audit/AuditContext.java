package sena.adso.core.audit;

/**
 * Contexto de auditoría basado en ThreadLocal.
 *
 * Permite que cualquier capa (Servlet, Service, DAO) agregue información
 * de auditoría durante el procesamiento de un request, sin pasar objetos
 * por parámetro entre métodos.
 *
 * El ciclo de vida es:
 * 1. Servlet inicia: AuditContext.set(new AuditEntry())
 * 2. Cualquier capa actualiza: AuditContext.get().setEntityId(...)
 * 3. AuditInterceptor (filtro) finaliza: persiste y limpia
 *
 * REGLA DE ORO: AuditInterceptor debe llamar clear() en finally para evitar
 * fugas de memoria entre requests (Tomcat recicla hilos).
 */
public final class AuditContext {

    private static final ThreadLocal<AuditEntry> CONTEXT = new ThreadLocal<>();

    private AuditContext() {
        // Utilidad, no instanciable
    }

    /**
     * Inicia un nuevo contexto de auditoría para el hilo actual.
     * Llama típicamente desde BaseServlet.audit() al inicio de una operación.
     */
    public static void set(AuditEntry entry) {
        CONTEXT.set(entry);
    }

    /**
     * Obtiene el AuditEntry del hilo actual.
     * 
     * @return null si no hay contexto iniciado
     */
    public static AuditEntry get() {
        return CONTEXT.get();
    }

    /**
     * Actualiza el entityId del contexto actual.
     * Útil cuando el ID se genera después del INSERT (auto-increment).
     */
    public static void updateEntityId(Long entityId) {
        AuditEntry entry = CONTEXT.get();
        if (entry != null) {
            entry.setEntityId(entityId);
        }
    }

    /**
     * Actualiza el status del contexto actual.
     */
    public static void updateStatus(String status) {
        AuditEntry entry = CONTEXT.get();
        if (entry != null) {
            entry.setStatus(status);
        }
    }

    /**
     * Agrega datos de request al contexto.
     */
    public static void setRequestData(String data) {
        AuditEntry entry = CONTEXT.get();
        if (entry != null) {
            entry.setRequestData(data);
        }
    }

    /**
     * Agrega mensaje de error al contexto.
     */
    public static void setError(String error) {
        AuditEntry entry = CONTEXT.get();
        if (entry != null) {
            entry.setErrorMessage(error);
        }
    }

    /**
     * Calcula y actualiza el tiempo de ejecución.
     */
    public static void calculateExecutionTime() {
        AuditEntry entry = CONTEXT.get();
        if (entry != null) {
            entry.calculateExecutionTime();
        }
    }

    /**
     * Verifica si hay un contexto de auditoría activo.
     */
    public static boolean hasContext() {
        return CONTEXT.get() != null;
    }

    /**
     * Limpia el contexto del hilo actual.
     * DEBE llamarse SIEMPRE en finally del AuditInterceptor.
     */
    public static void clear() {
        CONTEXT.remove();
    }
}