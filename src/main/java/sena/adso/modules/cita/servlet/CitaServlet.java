package sena.adso.modules.cita.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sena.adso.core.dao.TransactionManager;
import sena.adso.core.exception.BusinessException;
import sena.adso.core.servlet.BaseServlet;
import sena.adso.modules.cita.dao.CitaDAO;
import sena.adso.modules.cita.dao.EspecialidadDAO;
import sena.adso.modules.cita.model.Cita;
import sena.adso.modules.cita.service.CitaService;

/**
 * Servlet CRUD para Citas médicas.
 */
@WebServlet(name = "CitaServlet", urlPatterns = { "/citas" })
public class CitaServlet extends BaseServlet {

    private final CitaDAO citaDAO;
    private final CitaService citaService;
    private final EspecialidadDAO especialidadDAO;

    public CitaServlet() {
        this.citaDAO = new CitaDAO();
        this.citaService = new CitaService();
        this.especialidadDAO = new EspecialidadDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requireAuth(req, resp))
            return;
        if (!requireOtpVerified(req, resp))
            return;

        String accion = getParam(req, "accion", "listar");

        switch (accion) {
            case "listar" -> listar(req, resp);
            case "nueva" -> mostrarFormulario(req, resp);
            case "detalle" -> mostrarDetalle(req, resp);
            case "cambiar-estado" -> mostrarCambioEstado(req, resp);
            default -> listar(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requireAuth(req, resp))
            return;
        if (!requireOtpVerified(req, resp))
            return;

        String accion = getParam(req, "accion", "crear");

        switch (accion) {
            case "crear" -> crear(req, resp);
            case "cambiar-estado" -> cambiarEstado(req, resp);
            default -> listar(req, resp);
        }
    }

    // ============================================================
    // ACCIONES
    // ============================================================

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requirePermission(req, resp, "cita:listar"))
            return;

        TransactionManager.begin();
        try {
            List<Cita> citas = citaDAO.listarCompletas();
            req.setAttribute("citas", citas);
            TransactionManager.commit();

            forward(req, resp, "citas/lista");

        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        } finally {
            TransactionManager.close();
        }
    }

    private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requirePermission(req, resp, "cita:crear"))
            return;

        TransactionManager.begin();
        try {
            req.setAttribute("especialidades", especialidadDAO.findAll());
            TransactionManager.commit();

            forward(req, resp, "citas/formulario");

        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        } finally {
            TransactionManager.close();
        }
    }

    private void mostrarDetalle(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Integer id = getIntParam(req, "id");
        if (id == null) {
            sendError(req, resp, "error.requerido", 400);
            return;
        }

        TransactionManager.begin();
        try {
            Cita cita = citaDAO.findById(id)
                    .orElseThrow(() -> new BusinessException("Cita no encontrada"));

            req.setAttribute("cita", cita);
            TransactionManager.commit();

            forward(req, resp, "citas/detalle");

        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        } finally {
            TransactionManager.close();
        }
    }

    private void crear(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requirePermission(req, resp, "cita:crear"))
            return;

        try {
            Cita cita = construirDesdeRequest(req);
            cita.setIdRegistradoPor(getCurrentUserId(req));

            TransactionManager.begin();
            try {
                citaService.agendarCita(cita);
                TransactionManager.commit();

                resp.sendRedirect(req.getContextPath() + "/citas?accion=listar");

            } catch (Exception e) {
                TransactionManager.rollback();
                throw e;
            } finally {
                TransactionManager.close();
            }

        } catch (BusinessException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("especialidades", especialidadDAO.findAll());
            forward(req, resp, "citas/formulario");
        }
    }

    private void cambiarEstado(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requirePermission(req, resp, "cita:cambiar_estado"))
            return;

        Integer id = getIntParam(req, "id");
        String estadoStr = getParam(req, "estado", "");

        if (id == null || estadoStr.isBlank()) {
            sendError(req, resp, "error.requerido", 400);
            return;
        }

        try {
            Cita.Estado nuevoEstado = Cita.Estado.valueOf(estadoStr);

            TransactionManager.begin();
            try {
                citaService.cambiarEstado(id, nuevoEstado, getCurrentUserId(req));
                audit(req, "CITA_ESTADO_CHANGE", "Cita", id);
                TransactionManager.commit();

                resp.sendRedirect(req.getContextPath() + "/citas?accion=listar");

            } catch (Exception e) {
                TransactionManager.rollback();
                throw e;
            } finally {
                TransactionManager.close();
            }

        } catch (IllegalArgumentException e) {
            sendError(req, resp, "error.requerido", 400);
        }
    }

    private void mostrarCambioEstado(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Integer id = getIntParam(req, "id");
        if (id == null) {
            sendError(req, resp, "error.requerido", 400);
            return;
        }

        TransactionManager.begin();
        try {
            Cita cita = citaDAO.findById(id)
                    .orElseThrow(() -> new BusinessException("Cita no encontrada"));

            req.setAttribute("cita", cita);
            TransactionManager.commit();

            forward(req, resp, "citas/cambiar_estado");

        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        } finally {
            TransactionManager.close();
        }
    }

    // ============================================================
    // PRIVADOS
    // ============================================================

    private Cita construirDesdeRequest(HttpServletRequest req) {
        try {
            return Cita.builder()
                    .idPaciente(getIntParam(req, "idPaciente"))
                    .idMedico(getIntParam(req, "idMedico"))
                    .idEspecialidad(getIntParam(req, "idEspecialidad"))
                    .fechaCita(LocalDate.parse(getParam(req, "fechaCita", "")))
                    .horaCita(LocalTime.parse(getParam(req, "horaCita", "")))
                    .motivo(getParam(req, "motivo", ""))
                    .estado(Cita.Estado.PROGRAMADA)
                    .build();

        } catch (DateTimeParseException e) {
            throw new BusinessException("Fecha u hora inválida", "error.requerido");
        } catch (IllegalStateException e) {
            throw new BusinessException(e.getMessage(), "error.requerido");
        }
    }
}