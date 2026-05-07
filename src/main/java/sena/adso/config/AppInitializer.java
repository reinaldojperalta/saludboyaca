package sena.adso.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import sena.adso.core.audit.AsyncAuditWriter;
import sena.adso.core.exception.DataAccessException;
import sena.adso.core.rbac.RBACCache;
import sena.adso.core.rbac.model.Permiso;
import sena.adso.core.rbac.model.Rol;
import sena.adso.core.security.Authenticator;
import sena.adso.core.security.Authorizer;
import sena.adso.core.security.RBACAuthorizer;
import sena.adso.core.security.RouteMatcher;
import sena.adso.core.security.SessionAuthenticator;
import sena.adso.core.util.DatabaseConfig;
import sena.adso.modules.auth.dao.PermisoDAO;
import sena.adso.modules.auth.dao.RolDAO;

/**
 * Inicializador de la aplicación.
 * 
 * Se ejecuta UNA VEZ al arrancar Tomcat (before any request).
 * Responsabilidades:
 * 1. Validar conexión a base de datos
 * 2. Cargar RBACCache desde BD (roles, permisos, mapeos)
 * 3. Inyectar componentes de seguridad en ServletContext
 * 4. Inicializar AsyncAuditWriter (pool de threads)
 * 5. Registrar información de startup en logs
 */
@WebListener
public class AppInitializer implements ServletContextListener {

    private static final String APP_NAME = "SaludBoyacá";
    private static final String APP_VERSION = "1.0.0";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        logStartup(ctx, "INICIANDO " + APP_NAME + " v" + APP_VERSION);

        try {
            // 1. Validar conexión a BD (fail-fast)
            validateDatabaseConnection();
            logStartup(ctx, "✅ Conexión a base de datos OK");

            // 2. Cargar RBAC y componentes de seguridad
            initializeSecurity(ctx);
            logStartup(ctx, "✅ Sistema de seguridad inicializado");

            // 3. Inicializar escritor de logs async
            AsyncAuditWriter.initialize();
            logStartup(ctx, "✅ AsyncAuditWriter inicializado");

            // 4. Guardar metadata de la app en contexto
            ctx.setAttribute("app.name", APP_NAME);
            ctx.setAttribute("app.version", APP_VERSION);
            ctx.setAttribute("app.startup", System.currentTimeMillis());

            logStartup(ctx, "🚀 " + APP_NAME + " listo para recibir requests");

        } catch (Exception e) {
            logStartup(ctx, "❌ ERROR FATAL al inicializar: " + e.getMessage());
            e.printStackTrace();
            // No lanzamos excepción para que Tomcat no aborte el deploy,
            // pero la app no funcionará correctamente
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        logStartup(ctx, "DETENIENDO " + APP_NAME);

        // Cerrar pool de threads de auditoría
        AsyncAuditWriter.shutdown();

        logStartup(ctx, "👋 " + APP_NAME + " detenido");
    }

    // ============================================================
    // INICIALIZACIÓN DE SEGURIDAD
    // ============================================================

    private void initializeSecurity(ServletContext ctx) throws SQLException {
        // Cargar datos RBAC desde BD
        RBACCache rbacCache = loadRBACCache();

        // Crear componentes de seguridad
        Authenticator authenticator = new SessionAuthenticator();
        Authorizer authorizer = new RBACAuthorizer(rbacCache);
        RouteMatcher routeMatcher = new RouteMatcher();

        // Inyectar en ServletContext para que AuthFilter los recupere
        ctx.setAttribute("rbacCache", rbacCache);
        ctx.setAttribute("authenticator", authenticator);
        ctx.setAttribute("authorizer", authorizer);
        ctx.setAttribute("publicRoutes", routeMatcher);
    }

    private RBACCache loadRBACCache() throws SQLException {
        // Usamos conexión directa (no TransactionManager) porque es startup
        try (Connection conn = DatabaseConfig.getConnection()) {

            // Crear DAOs temporales para cargar datos
            RolDAO rolDAO = new RolDAO() {
                @Override
                protected Connection getConnection() {
                    return conn;
                }
            };

            PermisoDAO permisoDAO = new PermisoDAO() {
                @Override
                protected Connection getConnection() {
                    return conn;
                }
            };

            List<Rol> roles = rolDAO.findAll();
            List<Permiso> permisos = permisoDAO.findAll();
            List<RBACCache.RolePermissionMapping> mappings = rolDAO.loadRolePermissionMappings();

            return new RBACCache(roles, permisos, mappings);
        }
    }

    // ============================================================
    // VALIDACIONES
    // ============================================================

    // En AppInitializer.java, método validateDatabaseConnection():
    private void validateDatabaseConnection() {
        try {
            boolean valid = DatabaseConfig.isConnectionValid();
            if (!valid) {
                throw new DataAccessException("VALIDATE", "Database",
                        "isConnectionValid() retornó false. ¿MySQL está corriendo en puerto 3307?");
            }
        } catch (Exception e) {
            // Imprimir el error REAL antes de envolverlo
            e.printStackTrace();
            throw new DataAccessException("VALIDATE", "Database",
                    "Error real: " + e.getMessage(), e);
        }
    }

    // ============================================================
    // LOGGING
    // ============================================================

    private void logStartup(ServletContext ctx, String message) {
        String logEntry = "[" + APP_NAME + "] " + message;
        System.out.println(logEntry);
        // También se podría usar SLF4J aquí si ya está disponible
        ctx.log(logEntry);
    }
}