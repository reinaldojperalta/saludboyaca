package sena.adso.modules.debug.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sena.adso.core.audit.AsyncAuditWriter;
import sena.adso.core.audit.AuditEntry;
import sena.adso.core.dao.TransactionManager;
import sena.adso.core.exception.DataAccessException;
import sena.adso.core.rbac.RBACCache;
import sena.adso.core.servlet.BaseServlet;
import sena.adso.core.util.DatabaseConfig;
import sena.adso.modules.auth.dao.OTPTokenDAO;
import sena.adso.modules.auth.dao.UsuarioDAO;
import sena.adso.modules.auth.model.Usuario;
import sena.adso.modules.auth.service.OTPService;
import sena.adso.modules.cita.dao.CitaDAO;
import sena.adso.modules.cita.model.Cita;
import sena.adso.modules.debug.model.ResultadoTest;
import sena.adso.modules.paciente.dao.PacienteDAO;
import sena.adso.modules.paciente.model.Paciente;

/**
 * Servlet de debug/diagnóstico funcional.
 * Ruta pública: /debug (excluida en RouteMatcher)
 * Ejecuta tests reales contra la BD y reporta éxito/fracaso con semáforos.
 */
@WebServlet(name = "DebugServlet", urlPatterns = { "/debug" })
public class DebugServlet extends BaseServlet {

    // DAOs para tests
    private UsuarioDAO usuarioDAO;
    private PacienteDAO pacienteDAO;
    private CitaDAO citaDAO;
    private OTPTokenDAO otpTokenDAO;

    @Override
    public void init() throws ServletException {
        this.usuarioDAO = new UsuarioDAO();
        this.pacienteDAO = new PacienteDAO();
        this.citaDAO = new CitaDAO();
        this.otpTokenDAO = new OTPTokenDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<ResultadoTest> resultados = new ArrayList<>();

        // Ejecutar tests en orden
        resultados.add(testDAOFindById());
        resultados.add(testRBACPermission(req));
        resultados.add(testI18nBundles());
        resultados.add(testOTPCiclo());
        resultados.add(testAuditWrite(req));
        resultados.add(testTransactionCycle());

        // Info del servidor (no es test, es informativo)
        req.setAttribute("serverInfo", getServletContext().getServerInfo());
        req.setAttribute("servletApi",
                getServletContext().getMajorVersion() + "." + getServletContext().getMinorVersion());
        req.setAttribute("javaVersion", System.getProperty("java.version"));
        req.setAttribute("dbUrl", DatabaseConfig.getDbUrl());
        req.setAttribute("dbUser", DatabaseConfig.getDbUser());

        // Estado de conexión
        req.setAttribute("dbConnected", isDbConnected());

        // Tablas y row counts
        req.setAttribute("tablas", obtenerTablasYCounts());

        // Resultados de tests
        req.setAttribute("resultados", resultados);

        // Contadores
        // En DebugServlet.java, línea ~90
        long exitosos = resultados.stream().filter(ResultadoTest::isExito).count();
        req.setAttribute("testsExitosos", (int) exitosos); // Casteo explícito a int
        req.setAttribute("totalTests", resultados.size()); // size() ya devuelve int, así que está bien
        req.setAttribute("testsFallidos", resultados.size() - (int) exitosos);

        forward(req, resp, "debug");
    }

    // ============================================================
    // TEST 1: DAO FindById
    // ============================================================
    private ResultadoTest testDAOFindById() {
        ResultadoTest r = new ResultadoTest("DAO FindById");
        long inicio = System.currentTimeMillis();

        TransactionManager.begin();
        try {
            // Test UsuarioDAO
            Optional<Usuario> usuario = usuarioDAO.findById(1);
            if (usuario.isEmpty()) {
                r.marcarFallo("UsuarioDAO.findById(1) retornó vacío", "SELECT * FROM usuarios WHERE id = 1");
                return r;
            }

            // Test PacienteDAO
            Optional<Paciente> paciente = pacienteDAO.findById(1);
            if (paciente.isEmpty()) {
                r.marcarFallo("PacienteDAO.findById(1) retornó vacío", "SELECT * FROM pacientes WHERE id = 1");
                return r;
            }

            // Test CitaDAO
            Optional<Cita> cita = citaDAO.findById(1);
            // cita puede ser vacía si no hay datos de prueba, eso es OK
            String resultado = String.format("Usuario=%s, Paciente=%s, Cita=%s",
                    usuario.get().getUsername(),
                    paciente.get().getDocumento(),
                    cita.isPresent() ? "ID " + cita.get().getId() : "N/A (sin datos)");

            r.marcarExito(resultado,
                    "SELECT * FROM usuarios WHERE id = 1; SELECT * FROM pacientes WHERE id = 1; SELECT * FROM citas WHERE id = 1");
            TransactionManager.commit();

        } catch (Exception e) {
            TransactionManager.rollback();
            r.marcarFallo(e.getClass().getSimpleName() + ": " + e.getMessage(), "SELECT * FROM {tabla} WHERE id = 1");
        } finally {
            TransactionManager.close();
            r.setTiempoMs(System.currentTimeMillis() - inicio);
        }

        return r;
    }

    // ============================================================
    // TEST 2: RBAC Permission
    // ============================================================
    private ResultadoTest testRBACPermission(HttpServletRequest req) {
        ResultadoTest r = new ResultadoTest("RBAC Permission");
        long inicio = System.currentTimeMillis();

        try {
            RBACCache cache = (RBACCache) getServletContext().getAttribute("rbacCache");
            if (cache == null) {
                r.marcarFallo("RBACCache no encontrado en ServletContext", "rbacCache.getAttribute()");
                return r;
            }

            // Test: MEDICO debe poder ver dashboard
            boolean medicoPuedeDashboard = cache.hasPermission("MEDICO", "dashboard:ver");
            // Test: RECEPCIONISTA debe poder crear pacientes
            boolean recepPuedeCrearPac = cache.hasPermission("RECEPCIONISTA", "paciente:crear");
            // Test: ENFERMERO debe poder listar citas
            boolean enferPuedeListar = cache.hasPermission("ENFERMERO", "cita:listar");
            // Test: Rol inexistente debe retornar false
            boolean fakeRol = cache.hasPermission("ADMIN_FAKE", "dashboard:ver");

            if (!medicoPuedeDashboard) {
                r.marcarFallo("MEDICO no tiene permiso dashboard:ver", "cache.hasPermission()");
                return r;
            }
            if (!recepPuedeCrearPac) {
                r.marcarFallo("RECEPCIONISTA no tiene permiso paciente:crear", "cache.hasPermission()");
                return r;
            }
            if (!enferPuedeListar) {
                r.marcarFallo("ENFERMERO no tiene permiso cita:listar", "cache.hasPermission()");
                return r;
            }
            if (fakeRol) {
                r.marcarFallo("Rol inexistente ADMIN_FAKE retornó true", "cache.hasPermission()");
                return r;
            }

            String resultado = String.format(
                    "MEDICO→dashboard:ver=%s, RECEP→paciente:crear=%s, ENFER→cita:listar=%s, FAKE→false=%s",
                    medicoPuedeDashboard, recepPuedeCrearPac, enferPuedeListar, !fakeRol);

            r.marcarExito(resultado, "RBACCache.hasPermission() en memoria (ConcurrentHashMap)");

        } catch (Exception e) {
            r.marcarFallo(e.getClass().getSimpleName() + ": " + e.getMessage(), "RBACCache.hasPermission()");
        } finally {
            r.setTiempoMs(System.currentTimeMillis() - inicio);
        }

        return r;
    }

    // ============================================================
    // TEST 3: i18n Bundles
    // ============================================================
    private ResultadoTest testI18nBundles() {
        ResultadoTest r = new ResultadoTest("i18n Bundles");
        long inicio = System.currentTimeMillis();

        String[] idiomas = { "es", "en", "it" };
        String[] clavesCriticas = { "login.titulo", "otp.titulo", "cita.titulo" };
        StringBuilder resultado = new StringBuilder();

        try {
            for (String lang : idiomas) {
                Locale locale = new Locale(lang);
                ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);

                for (String clave : clavesCriticas) {
                    if (!bundle.containsKey(clave)) {
                        r.marcarFallo("Falta clave '" + clave + "' en messages_" + lang + ".properties",
                                "ResourceBundle.getBundle('messages', " + lang + ")");
                        return r;
                    }
                }

                resultado.append(lang).append("={login=").append(bundle.getString("login.titulo"))
                        .append(", otp=").append(bundle.getString("otp.titulo"))
                        .append(", cita=").append(bundle.getString("cita.titulo")).append("} ");
            }

            r.marcarExito(resultado.toString().trim(), "ResourceBundle.getBundle() x3 idiomas, 3 claves cada uno");

        } catch (Exception e) {
            r.marcarFallo(e.getClass().getSimpleName() + ": " + e.getMessage(),
                    "ResourceBundle.getBundle('messages', {es|en|it})");
        } finally {
            r.setTiempoMs(System.currentTimeMillis() - inicio);
        }

        return r;
    }

    // ============================================================
    // TEST 4: OTP Ciclo
    // ============================================================
    private ResultadoTest testOTPCiclo() {
        ResultadoTest r = new ResultadoTest("OTP Ciclo");
        long inicio = System.currentTimeMillis();

        OTPService otpService = new OTPService();
        String codigo = null;

        TransactionManager.begin();
        try {
            // Generar OTP
            codigo = otpService.generarOTP();
            if (codigo == null || codigo.length() != 6 || !codigo.matches("\\d{6}")) {
                r.marcarFallo("OTP generado inválido: " + codigo, "OTPService.generarOTP()");
                return r;
            }

            // Insertar en BD (necesitamos un usuario con id=1)
            // Si no existe usuario 1, usamos id=0 como test
            int idUsuarioTest = 1;
            try {
                otpTokenDAO.insertar(idUsuarioTest, codigo, LocalDateTime.now().plusMinutes(5));
            } catch (DataAccessException e) {
                // Si falla por FK (usuario no existe), probamos con id=0
                idUsuarioTest = 0;
                otpTokenDAO.insertar(idUsuarioTest, codigo, LocalDateTime.now().plusMinutes(5));
            }

            // Validar
            boolean valido = otpTokenDAO.validar(idUsuarioTest, codigo);
            if (!valido) {
                r.marcarFallo("OTPTokenDAO.validar() retornó false después de insertar",
                        "SELECT * FROM otp_tokens WHERE...");
                return r;
            }

            // Marcar usado
            otpTokenDAO.marcarUsado(idUsuarioTest, codigo);

            // Validar de nuevo (debe ser false porque ya está usado)
            boolean usado = otpTokenDAO.validar(idUsuarioTest, codigo);
            if (usado) {
                r.marcarFallo("OTPTokenDAO.validar() retornó true después de marcar usado",
                        "SELECT * FROM otp_tokens WHERE usado=0");
                return r;
            }

            String resultado = String.format("Código=%s, Insert=OK, Validar=true, MarcarUsado=OK, Revalidar=false",
                    codigo);
            r.marcarExito(resultado, "INSERT otp_tokens → SELECT validar → UPDATE usado=1 → SELECT validar");

            TransactionManager.commit();

        } catch (Exception e) {
            TransactionManager.rollback();
            r.marcarFallo(e.getClass().getSimpleName() + ": " + e.getMessage(),
                    "INSERT/SELECT/UPDATE en otp_tokens");
        } finally {
            TransactionManager.close();
            r.setTiempoMs(System.currentTimeMillis() - inicio);
        }

        return r;
    }

    // ============================================================
    // TEST 5: Audit Write
    // ============================================================
    private ResultadoTest testAuditWrite(HttpServletRequest req) {
        ResultadoTest r = new ResultadoTest("Audit Write");
        long inicio = System.currentTimeMillis();

        if (!AsyncAuditWriter.isInitialized()) {
            r.marcarFallo("AsyncAuditWriter no está inicializado", "AsyncAuditWriter.isInitialized()");
            return r;
        }

        try {
            // Crear entry de prueba
            AuditEntry entry = new AuditEntry();
            entry.setUserId(0L);
            entry.setUsername("debug_test");
            entry.setActionName("DEBUG_TEST");
            entry.setEntityType("Test");
            entry.setEntityId(null);
            entry.setStatus("SUCCESS");
            entry.setRequestData("{\"test\":\"debug\"}");
            entry.setResponseData("{\"result\":\"ok\"}");
            entry.setErrorMessage(null);
            entry.setExecutionTimeMs(42L);
            entry.setIpAddress(req.getRemoteAddr());
            entry.setUserAgent("DebugSuite/1.0");

            // Enviar a async writer
            AsyncAuditWriter.submit(entry);

            // Esperar un momento para que el hilo async procese
            Thread.sleep(500);

            // Verificar que hay al menos 1 registro en activity_logs
            long countAntes = contarActivityLogs();
            // Esperar un poco más si es necesario
            if (countAntes == 0) {
                Thread.sleep(1000);
                countAntes = contarActivityLogs();
            }

            if (countAntes == 0) {
                r.marcarFallo("No se encontraron registros en activity_logs después de submit",
                        "SELECT COUNT(*) FROM activity_logs");
                return r;
            }

            r.marcarExito("Registros en activity_logs: " + countAntes,
                    "AsyncAuditWriter.submit() → INSERT activity_logs");

        } catch (Exception e) {
            r.marcarFallo(e.getClass().getSimpleName() + ": " + e.getMessage(), "AsyncAuditWriter.submit()");
        } finally {
            r.setTiempoMs(System.currentTimeMillis() - inicio);
        }

        return r;
    }

    private long contarActivityLogs() {
        String sql = "SELECT COUNT(*) FROM activity_logs";
        try (Connection conn = DatabaseConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            System.err.println("Error contando activity_logs: " + e.getMessage());
        }
        return -1;
    }

    // ============================================================
    // TEST 6: Transaction Cycle
    // ============================================================
    private ResultadoTest testTransactionCycle() {
        ResultadoTest r = new ResultadoTest("Transaction Cycle");
        long inicio = System.currentTimeMillis();

        try {
            // Test A: begin → operación → commit → close
            TransactionManager.begin();
            if (!TransactionManager.isInTransaction()) {
                r.marcarFallo("isInTransaction()=false después de begin()", "TransactionManager.begin()");
                return r;
            }

            // Operación simple
            Optional<Usuario> u = usuarioDAO.findById(1);
            if (u.isEmpty()) {
                TransactionManager.rollback();
                r.marcarFallo("findById(1) retornó vacío durante transacción", "SELECT * FROM usuarios WHERE id = 1");
                return r;
            }

            TransactionManager.commit();
            TransactionManager.close();

            if (TransactionManager.isInTransaction()) {
                r.marcarFallo("isInTransaction()=true después de close()", "TransactionManager.close()");
                return r;
            }

            // Test B: begin → rollback → close
            TransactionManager.begin();
            Optional<Paciente> p = pacienteDAO.findById(1);
            TransactionManager.rollback();
            TransactionManager.close();

            if (TransactionManager.isInTransaction()) {
                r.marcarFallo("isInTransaction()=true después de rollback+close",
                        "TransactionManager.rollback() → close()");
                return r;
            }

            r.marcarExito("begin→find→commit→close=OK, begin→find→rollback→close=OK",
                    "TransactionManager ciclo completo");

        } catch (Exception e) {
            // Asegurar limpieza
            try {
                TransactionManager.rollback();
            } catch (Exception ignored) {
            }
            try {
                TransactionManager.close();
            } catch (Exception ignored) {
            }
            r.marcarFallo(e.getClass().getSimpleName() + ": " + e.getMessage(),
                    "TransactionManager.begin/commit/rollback/close");
        } finally {
            r.setTiempoMs(System.currentTimeMillis() - inicio);
        }

        return r;
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private boolean isDbConnected() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return conn.isValid(5);
        } catch (Exception e) {
            return false;
        }
    }

    private List<String[]> obtenerTablasYCounts() {
        List<String[]> tablas = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
                ResultSet rs = conn.getMetaData().getTables(null, null, "%", new String[] { "TABLE" })) {
            while (rs.next()) {
                String t = rs.getString("TABLE_NAME");
                long rows = -1;
                try (Statement s = conn.createStatement();
                        ResultSet r = s.executeQuery("SELECT COUNT(*) FROM " + t)) {
                    if (r.next())
                        rows = r.getLong(1);
                } catch (Exception e) {
                    rows = -2;
                }
                tablas.add(new String[] { t, String.valueOf(rows) });
            }
        } catch (Exception e) {
            tablas.add(new String[] { "ERROR", e.getMessage() });
        }
        return tablas;
    }
}