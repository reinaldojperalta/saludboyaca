package sena.adso.core.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestConexion {
    public static void main(String[] args) {
        String[] urls = {
                "jdbc:mysql://localhost:3307/vacunasdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                "jdbc:mysql://127.0.0.1:3307/vacunasdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
        };

        for (String url : urls) {
            try {
                Connection c = DriverManager.getConnection(url, "root", "");
                System.out.println("✅ OK: " + url);
                System.out.println("   Versión: " + c.getMetaData().getDatabaseProductVersion());
                c.close();
            } catch (Exception e) {
                System.out.println("❌ FAIL: " + url);
                System.out.println("   Error: " + e.getMessage());
            }
        }
    }
}
