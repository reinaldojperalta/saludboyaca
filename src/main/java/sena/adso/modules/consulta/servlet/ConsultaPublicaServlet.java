package sena.adso.modules.consulta.servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sena.adso.core.dao.TransactionManager;
import sena.adso.core.servlet.BaseServlet;
import sena.adso.core.util.SimpleCaptcha;
import sena.adso.modules.cita.dao.CitaDAO;
import sena.adso.modules.cita.model.Cita;
import sena.adso.modules.consulta.service.ConsultaService;
import sena.adso.modules.paciente.dao.PacienteDAO;
import sena.adso.modules.paciente.model.Paciente;

/**
 * Servlet para consulta pública de citas médicas.
 * 
 * Permite a cualquier persona (sin autenticación) consultar sus citas
 * ingresando número de documento + CAPTCHA de verificación.
 * 
 * Rutas:
 * - GET /consulta — Muestra formulario de consulta
 * - POST /consulta — Procesa documento + CAPTCHA, muestra resultados
 */
@WebServlet(name = "ConsultaPublicaServlet", urlPatterns = { "/consulta" })
public class ConsultaPublicaServlet extends BaseServlet {

    private final ConsultaService consultaService;
    private final PacienteDAO pacienteDAO;
    private final CitaDAO citaDAO;

    public ConsultaPublicaServlet() {
        this.pacienteDAO = new PacienteDAO();
        this.citaDAO = new CitaDAO();
        this.consultaService = new ConsultaService(pacienteDAO, citaDAO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Generar CAPTCHA para cada visita
        req.setAttribute("captchaImage", SimpleCaptcha.generate(req));
        forward(req, resp, "consulta");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String documento = getParam(req, "documento", "").trim();
        String captchaInput = getParam(req, "captcha", "").trim();
        String errorKey = null;

        // Validar CAPTCHA primero
        if (!SimpleCaptcha.validate(req, captchaInput)) {
            errorKey = "consulta.captcha.error";
        }
        // Validar documento vacío
        else if (documento.isBlank()) {
            errorKey = "error.requerido";
        }
        // Validar formato de documento
        else if (!consultaService.validarDocumento(documento)) {
            errorKey = "consulta.documento.invalido";
        } else {
            // Buscar paciente y sus citas
            TransactionManager.begin();
            try {
                Paciente paciente = consultaService.buscarPacientePorDocumento(documento);

                if (paciente == null) {
                    errorKey = "consulta.no.encontrado";
                } else {
                    List<Cita> citas = consultaService.obtenerCitasPorPaciente(paciente.getId());
                    req.setAttribute("paciente", paciente);
                    req.setAttribute("citas", citas);
                    req.setAttribute("documento", documento);
                    req.setAttribute("resultado", true);
                }

                TransactionManager.commit();

            } catch (Exception e) {
                TransactionManager.rollback();
                errorKey = "error.servidor";
            } finally {
                TransactionManager.close();
            }
        }

        // Setear error si hay
        if (errorKey != null) {
            req.setAttribute("error", getI18n(req, errorKey));
            req.setAttribute("documento", documento);
        }

        // Generar CAPTCHA UNA SOLA VEZ al final
        req.setAttribute("captchaImage", SimpleCaptcha.generate(req));
        forward(req, resp, "consulta");
    }
}