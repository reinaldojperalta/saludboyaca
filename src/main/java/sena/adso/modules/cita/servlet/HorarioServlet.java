package sena.adso.modules.cita.servlet;

import java.io.IOException;
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
import sena.adso.modules.auth.dao.UsuarioDAO;
import sena.adso.modules.auth.model.Usuario;
import sena.adso.modules.cita.dao.HorarioDAO;
import sena.adso.modules.cita.model.Horario;

/**
 * Servlet para CRUD de Horarios de atención médica.
 */
@WebServlet(name = "HorarioServlet", urlPatterns = { "/horarios" })
public class HorarioServlet extends BaseServlet {

    private final HorarioDAO horarioDAO;
    private final UsuarioDAO usuarioDAO;

    public HorarioServlet() {
        this.horarioDAO = new HorarioDAO();
        this.usuarioDAO = new UsuarioDAO();
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
            case "nuevo" -> mostrarFormulario(req, resp);
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
    // ACCIONES
    // ============================================================

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requirePermission(req, resp, "horario:ver"))
            return;

        TransactionManager.begin();
        try {
            Usuario usuario = getCurrentUser(req);
            String rol = getCurrentUserRol(req);
            List<Horario> horarios;
            List<Usuario> medicos = null;

            // MEDICO solo ve sus propios horarios
            if ("MEDICO".equals(rol)) {
                horarios = horarioDAO.listarPorMedico(usuario.getId());
            }
            // ADMIN y RECEPCIONISTA ven todos
            else {
                horarios = horarioDAO.findAll();
                medicos = usuarioDAO.findAll(); // Para mostrar nombre del médico
            }

            req.setAttribute("horarios", horarios);
            req.setAttribute("medicos", medicos);
            req.setAttribute("rolUsuario", rol);
            req.setAttribute("pHorario", true);
            req.setAttribute("hasPermissionEditar", hasPermission(req, "horario:editar") || "SUPERADMIN".equals(rol) || "ADMIN".equals(rol)); // Fallback
            req.setAttribute("hasPermissionEliminar", hasPermission(req, "horario:eliminar") || "SUPERADMIN".equals(rol) || "ADMIN".equals(rol));

            TransactionManager.commit();

            forward(req, resp, "horarios/lista");

        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        } finally {
            TransactionManager.close();
        }
    }

    private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requirePermission(req, resp, "horario:ver")) // Using ver since there's no horario:crear
            return;

        TransactionManager.begin();
        try {
            req.setAttribute("medicos", usuarioDAO.listarPorRol("MEDICO"));
            req.setAttribute("modoEdicion", false);
            req.setAttribute("pHorario", true);
            TransactionManager.commit();

            forward(req, resp, "horarios/formulario");

        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        } finally {
            TransactionManager.close();
        }
    }

    private void mostrarEdicion(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requirePermission(req, resp, "horario:editar") && !getCurrentUserRol(req).equals("ADMIN") && !getCurrentUserRol(req).equals("SUPERADMIN"))
            return;

        Integer id = getIntParam(req, "id");
        if (id == null) {
            sendError(req, resp, "error.requerido", 400);
            return;
        }

        TransactionManager.begin();
        try {
            Horario horario = horarioDAO.findById(id)
                    .orElseThrow(() -> new BusinessException("Horario no encontrado"));

            req.setAttribute("horario", horario);
            req.setAttribute("medicos", usuarioDAO.listarPorRol("MEDICO"));
            req.setAttribute("modoEdicion", true);
            req.setAttribute("pHorario", true);
            TransactionManager.commit();

            forward(req, resp, "horarios/formulario");

        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        } finally {
            TransactionManager.close();
        }
    }

    private void crear(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requirePermission(req, resp, "horario:ver"))
            return;

        try {
            Horario horario = construirDesdeRequest(req);

            TransactionManager.begin();
            try {
                horarioDAO.insert(horario);
                TransactionManager.commit();

                resp.sendRedirect(req.getContextPath() + "/horarios?accion=listar");

            } catch (Exception e) {
                TransactionManager.rollback();
                throw e;
            } finally {
                TransactionManager.close();
            }

        } catch (BusinessException e) {
            req.setAttribute("error", e.getMessage());
            TransactionManager.begin();
            try {
                req.setAttribute("medicos", usuarioDAO.listarPorRol("MEDICO"));
                TransactionManager.commit();
            } finally {
                TransactionManager.close();
            }
            req.setAttribute("modoEdicion", false);
            req.setAttribute("pHorario", true);
            forward(req, resp, "horarios/formulario");
        }
    }

    private void actualizar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requirePermission(req, resp, "horario:editar") && !getCurrentUserRol(req).equals("ADMIN") && !getCurrentUserRol(req).equals("SUPERADMIN"))
            return;

        Integer id = getIntParam(req, "id");
        if (id == null) {
            sendError(req, resp, "error.requerido", 400);
            return;
        }

        try {
            Horario horario = construirDesdeRequest(req);
            horario.setId(id);

            TransactionManager.begin();
            try {
                horarioDAO.update(horario);
                TransactionManager.commit();

                resp.sendRedirect(req.getContextPath() + "/horarios?accion=listar");

            } catch (Exception e) {
                TransactionManager.rollback();
                throw e;
            } finally {
                TransactionManager.close();
            }

        } catch (BusinessException e) {
            req.setAttribute("error", e.getMessage());
            TransactionManager.begin();
            try {
                req.setAttribute("medicos", usuarioDAO.listarPorRol("MEDICO"));
                TransactionManager.commit();
            } finally {
                TransactionManager.close();
            }
            req.setAttribute("modoEdicion", true);
            req.setAttribute("horario", construirDesdeRequest(req));
            req.setAttribute("pHorario", true);
            forward(req, resp, "horarios/formulario");
        }
    }

    private void eliminar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!requirePermission(req, resp, "horario:eliminar") && !getCurrentUserRol(req).equals("ADMIN") && !getCurrentUserRol(req).equals("SUPERADMIN"))
            return;

        Integer id = getIntParam(req, "id");
        if (id == null) {
            sendError(req, resp, "error.requerido", 400);
            return;
        }

        TransactionManager.begin();
        try {
            horarioDAO.delete(id);
            TransactionManager.commit();

            resp.sendRedirect(req.getContextPath() + "/horarios?accion=listar");

        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        } finally {
            TransactionManager.close();
        }
    }

    private Horario construirDesdeRequest(HttpServletRequest req) {
        try {
            return new Horario(
                    null,
                    getIntParam(req, "idMedico"),
                    getIntParam(req, "diaSemana"),
                    LocalTime.parse(getParam(req, "horaInicio", "")),
                    LocalTime.parse(getParam(req, "horaFin", "")),
                    getIntParam(req, "maxCitas")
            );
        } catch (DateTimeParseException e) {
            throw new BusinessException("Hora inválida", "error.requerido");
        } catch (IllegalStateException e) {
            throw new BusinessException(e.getMessage(), "error.requerido");
        }
    }
}