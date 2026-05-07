package sena.adso.modules.paciente.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sena.adso.core.dao.TransactionManager;
import sena.adso.core.exception.BusinessException;
import sena.adso.core.servlet.BaseServlet;
import sena.adso.modules.paciente.dao.PacienteDAO;
import sena.adso.modules.paciente.model.Paciente;

/**
 * Servlet CRUD para Pacientes.
 * 
 * Acciones soportadas vía parámetro ?accion=:
 * - listar: Muestra lista de pacientes
 * - nuevo: Muestra formulario vacío
 * - crear: Inserta nuevo paciente (POST)
 * - editar: Muestra formulario con datos
 * - actualizar: Actualiza paciente (POST)
 * - eliminar: Elimina paciente
 */
@WebServlet(name = "PacienteServlet", urlPatterns = { "/pacientes" })
public class PacienteServlet extends BaseServlet {

    private final PacienteDAO pacienteDAO;

    public PacienteServlet() {
        this.pacienteDAO = new PacienteDAO();
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
            case "nuevo" -> mostrarFormulario(req, resp, null);
            case "editar" -> mostrarEdicion(req, resp);
            case "eliminar" -> eliminar(req, resp);
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
            case "actualizar" -> actualizar(req, resp);
            default -> listar(req, resp);
        }
    }

    // ============================================================
    // ACCIONES GET
    // ============================================================

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requirePermission(req, resp, "paciente:listar"))
            return;

        TransactionManager.begin();
        try {
            List<Paciente> pacientes = pacienteDAO.findAll();
            req.setAttribute("pacientes", pacientes);
            TransactionManager.commit();

            // Setear flags de permisos para el JSP (header.jsp los usa)
            req.setAttribute("pPaciente", true);
            req.setAttribute("hasPermissionPaciente", hasPermission(req, "paciente:crear"));
            req.setAttribute("hasPermissionEditar", hasPermission(req, "paciente:editar"));
            req.setAttribute("hasPermissionEliminar", hasPermission(req, "paciente:eliminar"));

            forward(req, resp, "pacientes/lista");

        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        } finally {
            TransactionManager.close();
        }
    }

    private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp, Paciente paciente)
            throws ServletException, IOException {

        req.setAttribute("paciente", paciente);
        req.setAttribute("modoEdicion", paciente != null && paciente.getId() != null);
        req.setAttribute("pPaciente", true);
        forward(req, resp, "pacientes/formulario");
    }

    private void mostrarEdicion(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requirePermission(req, resp, "paciente:editar"))
            return;

        Integer id = getIntParam(req, "id");
        if (id == null) {
            sendError(req, resp, "error.requerido", 400);
            return;
        }

        TransactionManager.begin();
        try {
            Paciente paciente = pacienteDAO.findById(id)
                    .orElseThrow(() -> new BusinessException("Paciente no encontrado", "error.requerido"));

            req.setAttribute("paciente", paciente);
            req.setAttribute("modoEdicion", true);
            req.setAttribute("pPaciente", true);
            TransactionManager.commit();

            forward(req, resp, "pacientes/formulario");

        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        } finally {
            TransactionManager.close();
        }
    }

    // ============================================================
    // ACCIONES POST
    // ============================================================

    private void crear(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requirePermission(req, resp, "paciente:crear"))
            return;

        try {
            Paciente paciente = construirDesdeRequest(req);

            TransactionManager.begin();
            try {
                // Verificar duplicado de documento
                if (pacienteDAO.buscarPorDocumento(paciente.getDocumento()).isPresent()) {
                    throw new BusinessException("Ya existe un paciente con ese documento", "error.requerido");
                }

                Integer nuevoId = pacienteDAO.insert(paciente);
                audit(req, "PACIENTE_CREATE", "Paciente", nuevoId);
                TransactionManager.commit();

                resp.sendRedirect(req.getContextPath() + "/pacientes?accion=listar");

            } catch (Exception e) {
                TransactionManager.rollback();
                throw e;
            } finally {
                TransactionManager.close();
            }

        } catch (BusinessException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("paciente", construirParcialDesdeRequest(req));
            req.setAttribute("modoEdicion", false);
            req.setAttribute("pPaciente", true);
            forward(req, resp, "pacientes/formulario");
        }
    }

    private void actualizar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requirePermission(req, resp, "paciente:editar"))
            return;

        Integer id = getIntParam(req, "id");
        if (id == null) {
            sendError(req, resp, "error.requerido", 400);
            return;
        }

        try {
            Paciente paciente = construirDesdeRequest(req);
            paciente.setId(id);

            TransactionManager.begin();
            try {
                // Verificar duplicado de documento (excluyendo el propio paciente)
                pacienteDAO.buscarPorDocumento(paciente.getDocumento()).ifPresent(existente -> {
                    if (!existente.getId().equals(id)) {
                        throw new BusinessException("Ya existe otro paciente con ese documento", "error.requerido");
                    }
                });

                pacienteDAO.update(paciente);
                audit(req, "PACIENTE_UPDATE", "Paciente", id);
                TransactionManager.commit();

                resp.sendRedirect(req.getContextPath() + "/pacientes?accion=listar");

            } catch (Exception e) {
                TransactionManager.rollback();
                throw e;
            } finally {
                TransactionManager.close();
            }

        } catch (BusinessException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("paciente", construirParcialDesdeRequest(req));
            req.setAttribute("modoEdicion", true);
            req.setAttribute("pPaciente", true);
            forward(req, resp, "pacientes/formulario");
        }
    }

    private void eliminar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requirePermission(req, resp, "paciente:eliminar"))
            return;

        Integer id = getIntParam(req, "id");
        if (id == null) {
            sendError(req, resp, "error.requerido", 400);
            return;
        }

        TransactionManager.begin();
        try {
            pacienteDAO.delete(id);
            audit(req, "PACIENTE_DELETE", "Paciente", id);
            TransactionManager.commit();

            resp.sendRedirect(req.getContextPath() + "/pacientes?accion=listar");

        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        } finally {
            TransactionManager.close();
        }
    }

    // ============================================================
    // CONSTRUCTORES DESDE REQUEST
    // ============================================================

    private Paciente construirDesdeRequest(HttpServletRequest req) {
        try {
            return new Paciente.Builder()
                    .nombres(getParam(req, "nombres", "").trim())
                    .apellidos(getParam(req, "apellidos", "").trim())
                    .documento(getParam(req, "documento", "").trim())
                    .fechaNacimiento(LocalDate.parse(getParam(req, "fechaNacimiento", "")))
                    .telefono(getParam(req, "telefono", "").trim())
                    .email(getParam(req, "email", "").trim())
                    .eps(getParam(req, "eps", "").trim())
                    .veredaBarrio(getParam(req, "veredaBarrio", "").trim())
                    .build();

        } catch (DateTimeParseException e) {
            throw new BusinessException("Fecha de nacimiento inválida (formato: YYYY-MM-DD)", "error.requerido");
        } catch (IllegalStateException e) {
            throw new BusinessException(e.getMessage(), "error.requerido");
        }
    }

    private Paciente construirParcialDesdeRequest(HttpServletRequest req) {
        try {
            return construirDesdeRequest(req);
        } catch (Exception e) {
            // Devolver paciente con datos parciales para rellenar formulario
            return new Paciente.Builder()
                    .nombres(getParam(req, "nombres", "").trim())
                    .apellidos(getParam(req, "apellidos", "").trim())
                    .documento(getParam(req, "documento", "").trim())
                    .telefono(getParam(req, "telefono", "").trim())
                    .email(getParam(req, "email", "").trim())
                    .eps(getParam(req, "eps", "").trim())
                    .veredaBarrio(getParam(req, "veredaBarrio", "").trim())
                    .build();
        }
    }
}