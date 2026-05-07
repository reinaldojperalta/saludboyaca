package sena.adso.core.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConfig {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3307/vacunasdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "";

    private DatabaseConfig() {
    }

    public static String getDbUrl() {
        return getEnv("DB_URL", DEFAULT_URL);
    }

    public static String getDbUser() {
        return getEnv("DB_USER", DEFAULT_USER);
    }

    public static String getDbPass() {
        return getEnv("DB_PASS", DEFAULT_PASS);
    }

    public static Connection getConnection() throws SQLException {
        String url = getDbUrl();
        String user = getDbUser();
        String pass = getDbPass();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado", e);
        }

        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            return conn;
        } catch (SQLException e) {
            throw e;
        }
    }

    public static boolean isConnectionValid() {
        try (Connection conn = getConnection()) {
            boolean valid = conn.isValid(5);
            return valid;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        String result = (value != null && !value.isBlank()) ? value : defaultValue;
        System.out.println("[DatabaseConfig] " + key + " = " +
                (value != null ? "(env)" : "(default)") + " → " +
                (key.contains("PASS") ? "****" : result));
        return result;
    }
}