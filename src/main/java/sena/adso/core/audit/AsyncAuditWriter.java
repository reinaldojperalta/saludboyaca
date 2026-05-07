package sena.adso.core.audit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Escritor asíncrono de logs de auditoría.
 * 
 * Usa un ExecutorService con pool fijo para insertar logs en la BD
 * sin bloquear el hilo principal del request HTTP.
 * 
 * El pool se inicializa en AppInitializer y se cierra gracefulmente
 * al detener la aplicación.
 */
public final class AsyncAuditWriter {

    private static final int THREAD_POOL_SIZE = 2;
    private static final int MAX_QUEUE_SIZE = 1000;

    private static ExecutorService executor;
    private static boolean initialized = false;

    private AsyncAuditWriter() {
        // Utilidad, no instanciable
    }

    /**
     * Inicializa el pool de threads para escritura async.
     * Debe llamarse una sola vez en AppInitializer.init().
     */
    public static synchronized void initialize() {
        if (initialized)
            return;

        // ThreadPoolExecutor con cola limitada para evitar OutOfMemory
        executor = new ThreadPoolExecutor(
                THREAD_POOL_SIZE,
                THREAD_POOL_SIZE,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(MAX_QUEUE_SIZE),
                new ThreadFactory() {
                    private int count = 0;

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "audit-writer-" + (++count));
                        t.setDaemon(true); // No bloquea shutdown de JVM
                        return t;
                    }
                },
                new ThreadPoolExecutor.DiscardPolicy() // Si la cola está llena, descarta el log
        );

        initialized = true;
        System.out.println("AsyncAuditWriter inicializado con " + THREAD_POOL_SIZE + " threads");
    }

    /**
     * Envía un AuditEntry para persistencia asíncrona.
     * No bloquea el hilo llamador.
     */
    public static void submit(AuditEntry entry) {
        if (!initialized || executor == null || executor.isShutdown()) {
            System.err.println("AsyncAuditWriter no inicializado o cerrado. Log descartado: " + entry);
            return;
        }

        executor.submit(() -> persist(entry));
    }

    /**
     * Persiste el AuditEntry en la tabla activity_logs.
     * Este método corre en un hilo del pool, NO en el hilo del request.
     */
    private static void persist(AuditEntry entry) {
        // Usamos conexión propia (no TransactionManager) porque es async
        // y no pertenece a la transacción del request
        String sql = "INSERT INTO activity_logs " +
                "(user_id, username, action_name, entity_type, entity_id, status, " +
                "request_data, response_data, error_message, execution_time_ms, " +
                "ip_address, user_agent) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = sena.adso.core.util.DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, entry.getUserId());
            ps.setString(2, entry.getUsername());
            ps.setString(3, entry.getActionName());
            ps.setString(4, entry.getEntityType());
            ps.setObject(5, entry.getEntityId());
            ps.setString(6, entry.getStatus());
            ps.setString(7, entry.getRequestData());
            ps.setString(8, entry.getResponseData());
            ps.setString(9, entry.getErrorMessage());
            ps.setObject(10, entry.getExecutionTimeMs());
            ps.setString(11, entry.getIpAddress());
            ps.setString(12, entry.getUserAgent());

            ps.executeUpdate();

        } catch (SQLException e) {
            // Log a stderr (no podemos usar el sistema de audit porque falló)
            System.err.println("ERROR al persistir audit: " + e.getMessage());
            System.err.println("Entry: " + entry);
        }
    }

    /**
     * Cierra gracefulmente el pool de threads.
     * Debe llamarse en AppInitializer.destroy().
     */
    public static synchronized void shutdown() {
        if (executor == null || executor.isShutdown())
            return;

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        initialized = false;
        System.out.println("AsyncAuditWriter cerrado");
    }

    /**
     * Verifica si el writer está activo.
     */
    public static boolean isInitialized() {
        return initialized;
    }
}