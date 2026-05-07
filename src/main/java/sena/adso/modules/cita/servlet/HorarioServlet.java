package sena.adso.modules.cita.servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sena.adso.core.dao.TransactionManager;
import sena.adso.core.servlet.BaseServlet;
import sena.adso.modules.auth.dao.UsuarioDAO;
import sena.adso.modules.auth.model.Usuario;
import sena.adso.modules.cita.dao.HorarioDAO;
import sena.adso.modules.cita.model.Horario;

/**
 * Servlet para visualización de Horarios de atención médica.
 * 
 * Acciones soportadas vía parámetro ?accion=:
 * - listar: Muestra horarios (todos o del médico logueado según rol)
 * 
 * NOTA: CRUD de horarios no implementado aún. Solo lectura.
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

            TransactionManager.commit();

            forward(req, resp, "horarios/lista");

        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        } finally {
            TransactionManager.close();
        }
    }
}