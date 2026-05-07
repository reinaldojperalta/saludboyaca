package sena.adso.core.dao;

import java.sql.Connection;
import java.sql.SQLException;

import sena.adso.core.exception.DataAccessException;
import sena.adso.core.util.DatabaseConfig;

/**
 * Gestor de transacciones basado en ThreadLocal.
 *
 * Cada hilo de ejecución (request HTTP) obtiene su propia conexión privada,
 * permitiendo transacciones sin pasar Connection por parámetro entre capas.
 *
 * REGLA DE ORO: Siempre llamar close() en finally para evitar fugas de
 * conexión.
 * Tomcat recicla hilos del pool; si no se hace remove(), el siguiente request
 * en ese hilo verá una conexión cerrada o sucia.
 *
 * Ejemplo de uso en un Servlet:
 *
 * <pre>
 * TransactionManager.begin();
 * try {
 *     pacienteDAO.insert(paciente);
 *     citaDAO.insert(cita);
 *     logDAO.insert(log);
 *     TransactionManager.commit();
 * } catch (Exception e) {
 *     TransactionManager.rollback();
 *     throw new BusinessException("Error al crear cita", e);
 * } finally {
 *     TransactionManager.close(); // SIEMPRE
 * }
 * </pre>
 */
public final class TransactionManager {

    private static final ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> IN_TRANSACTION = new ThreadLocal<>();

    private TransactionManager() {
        // Utilidad, no instanciable
    }

    /**
     * Inicia una nueva transacción.
     * Obtiene conexión de DatabaseConfig, desactiva auto-commit y la guarda en
     * ThreadLocal.
     */
    public static void begin() {
        try {
            Connection conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            CONNECTION_HOLDER.set(conn);
            IN_TRANSACTION.set(true);
        } catch (SQLException e) {
            throw new DataAccessException("BEGIN", "Transaction", "No se pudo iniciar transacción", e);
        }
    }

    /**
     * Obtiene la conexión del hilo actual.
     * Si no hay transacción activa, lanza excepción (programación defensiva).
     */
    public static Connection get() {
        Connection conn = CONNECTION_HOLDER.get();
        if (conn == null) {
            throw new IllegalStateException(
                    "No hay transacción activa. Llame TransactionManager.begin() antes de operar con la BD.");
        }
        return conn;
    }

    /**
     * Verifica si hay una transacción activa en el hilo actual.
     */
    public static boolean isInTransaction() {
        return Boolean.TRUE.equals(IN_TRANSACTION.get());
    }

    /**
     * Confirma la transacción actual.
     */
    public static void commit() {
        Connection conn = CONNECTION_HOLDER.get();
        if (conn == null) {
            throw new IllegalStateException("No hay transacción para hacer commit");
        }
        try {
            conn.commit();
        } catch (SQLException e) {
            throw new DataAccessException("COMMIT", "Transaction", "Error al confirmar transacción", e);
        }
    }

    /**
     * Revierte la transacción actual.
     * Silencioso si ya está cerrada o no hay transacción.
     */
    public static void rollback() {
        Connection conn = CONNECTION_HOLDER.get();
        if (conn == null)
            return;
        try {
            if (!conn.isClosed()) {
                conn.rollback();
            }
        } catch (SQLException e) {
            // Log pero no lanzar — estamos en camino de recuperación
            System.err.println("Error en rollback: " + e.getMessage());
        }
    }

    /**
     * Cierra la conexión y limpia el ThreadLocal.
     * DEBE llamarse SIEMPRE en finally.
     */
    public static void close() {
        Connection conn = CONNECTION_HOLDER.get();
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            } finally {
                // CRÍTICO: Limpiar ThreadLocal para evitar que el hilo reciclado
                // por Tomcat reutilice una conexión cerrada
                CONNECTION_HOLDER.remove();
                IN_TRANSACTION.remove();
            }
        }
    }

    /**
     * Configura el nivel de aislamiento de la transacción actual.
     * Útil para operaciones críticas (ej: agendar cita — evitar race conditions).
     * 
     * @param level Connection.TRANSACTION_READ_COMMITTED, TRANSACTION_SERIALIZABLE,
     *              etc.
     */
    public static void setIsolationLevel(int level) {
        Connection conn = CONNECTION_HOLDER.get();
        if (conn == null) {
            throw new IllegalStateException("No hay transacción activa");
        }
        try {
            conn.setTransactionIsolation(level);
        } catch (SQLException e) {
            throw new DataAccessException("SET_ISOLATION", "Transaction",
                    "Error al configurar nivel de aislamiento", e);
        }
    }
}