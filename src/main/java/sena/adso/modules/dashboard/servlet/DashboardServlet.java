package sena.adso.modules.dashboard.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sena.adso.core.dao.TransactionManager;
import sena.adso.core.rbac.RBACCache;
import sena.adso.core.servlet.BaseServlet;
import sena.adso.modules.auth.model.Usuario;
import sena.adso.modules.cita.dao.CitaDAO;
import sena.adso.modules.cita.model.Cita;
import sena.adso.modules.paciente.dao.PacienteDAO;

/**
 * Servlet del Dashboard principal.
 * 
 * Muestra métricas agregadas y últimas citas.
 * Requiere autenticación, OTP verificado y permiso dashboard:ver.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = { "/dashboard" })
public class DashboardServlet extends BaseServlet {

    private final CitaDAO citaDAO;
    private final PacienteDAO pacienteDAO;

    public DashboardServlet() {
        this.citaDAO = new CitaDAO();
        this.pacienteDAO = new PacienteDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requireAuth(req, resp))
            return;
        if (!requireOtpVerified(req, resp))
            return;

        String rol = getCurrentUserRol(req);

        // ===== DEBUG CRÍTICO =====
        Usuario u = getCurrentUser(req);
        String rolEnSesion = getCurrentUserRol(req);
        RBACCache cache = (RBACCache) req.getServletContext().getAttribute("rbacCache");

        if (cache != null) {
        }

        if (!requirePermission(req, resp, "dashboard:ver"))
            return;

        TransactionManager.begin();
        try {
            // Métricas
            Map<String, Object> metricas = calcularMetricas();
            req.setAttribute("metricas", metricas);

            // Últimas 5 citas
            List<Cita> todas = citaDAO.listarCompletas();
            List<Cita> ultimas = todas.size() > 5 ? todas.subList(0, 5) : todas;
            req.setAttribute("ultimasCitas", ultimas);

            // Flags de permisos para el menú (Respetando tus nombres de variables)
            req.setAttribute("hasPermissionCita", hasPermission(req, "cita:listar"));
            req.setAttribute("hasPermissionPaciente", hasPermission(req, "paciente:listar"));
            req.setAttribute("hasPermissionHorario", hasPermission(req, "horario:ver"));
            req.setAttribute("hasPermissionUsuario", hasPermission(req, "usuario:administrar"));
            req.setAttribute("hasPermissionReporte", hasPermission(req, "reporte:ver"));
            req.setAttribute("hasPermissionConfig", hasPermission(req, "configuracion:editar"));

            TransactionManager.commit();

            // Busca el archivo en WEB-INF/views/dashboard.jsp
            forward(req, resp, "dashboard");

        } catch (Exception e) {
            TransactionManager.rollback();
            e.printStackTrace();
            sendErrorLiteral(req, resp, "Error cargando dashboard: " + e.getMessage(), 500);
        } finally {
            TransactionManager.close();
        }
    }

    private Map<String, Object> calcularMetricas() {
        Map<String, Object> m = new HashMap<>();
        LocalDate hoy = LocalDate.now();

        List<Cita> todas = citaDAO.listarCompletas();

        // Citas hoy
        long citasHoy = todas.stream()
                .filter(c -> c.getFechaCita().equals(hoy))
                .count();
        m.put("citasHoy", citasHoy);

        // Pendientes (PROGRAMADA o CONFIRMADA)
        long pendientes = todas.stream()
                .filter(c -> c.getEstado() == Cita.Estado.PROGRAMADA
                        || c.getEstado() == Cita.Estado.CONFIRMADA)
                .count();
        m.put("pendientes", pendientes);

        // Citas del mes
        long citasMes = todas.stream()
                .filter(c -> c.getFechaCita().getMonth() == hoy.getMonth()
                        && c.getFechaCita().getYear() == hoy.getYear())
                .count();
        m.put("citasMes", citasMes);

        // Total pacientes
        m.put("totalPacientes", pacienteDAO.countAll());

        return m;
    }
}