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
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;

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

        String accion = getParam(req, "accion", "");
        if ("exportar-pdf".equals(accion)) {
            exportarPdf(req, resp);
            return;
        }

        // Generar CAPTCHA para cada visita
        req.setAttribute("captchaImage", SimpleCaptcha.generate(req));
        forward(req, resp, "consulta");
    }

    private void exportarPdf(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String documento = getParam(req, "documento", "");
        if (documento.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/consulta");
            return;
        }

        TransactionManager.begin();
        try {
            Paciente paciente = consultaService.buscarPacientePorDocumento(documento);
            if (paciente == null) {
                resp.sendRedirect(req.getContextPath() + "/consulta");
                return;
            }

            List<Cita> citas = consultaService.obtenerCitasPorPaciente(paciente.getId());

            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=Reporte_SaludBoyaca_" + documento + ".pdf");

            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, resp.getOutputStream());

            document.open();

            // Fuentes
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(26, 82, 118)); // Azul Salud
            Font fontSubtitulo = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font fontNormal = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
            Font fontHeaderTabla = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);

            // Título
            Paragraph titulo = new Paragraph("SaludBoyacá - Sistema de Citas Médicas", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            
            document.add(new Paragraph("Reporte de Consultas del Paciente", fontSubtitulo));
            document.add(new Paragraph(" "));

            // Info Paciente
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(20);
            
            infoTable.addCell(new Phrase("Nombre:", fontSubtitulo));
            infoTable.addCell(new Phrase(paciente.getNombres() + " " + paciente.getApellidos(), fontNormal));
            
            infoTable.addCell(new Phrase("Documento:", fontSubtitulo));
            infoTable.addCell(new Phrase(paciente.getDocumento(), fontNormal));
            
            infoTable.addCell(new Phrase("EPS:", fontSubtitulo));
            infoTable.addCell(new Phrase(paciente.getEps(), fontNormal));
            
            document.add(infoTable);

            // Tabla de Citas
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 2, 4, 3});

            // Headers
            String[] headers = {"Fecha", "Hora", "Especialidad", "Estado"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, fontHeaderTabla));
                cell.setBackgroundColor(new BaseColor(26, 82, 118));
                cell.setPadding(8);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Datos
            for (Cita cita : citas) {
                table.addCell(new Phrase(cita.getFechaCita().toString(), fontNormal));
                table.addCell(new Phrase(cita.getHoraCita().toString(), fontNormal));
                table.addCell(new Phrase(cita.getNombreEspecialidad(), fontNormal));
                table.addCell(new Phrase(cita.getEstado().name(), fontNormal));
            }

            document.add(table);

            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Generado el: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), fontNormal);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();
            TransactionManager.commit();

        } catch (Exception e) {
            TransactionManager.rollback();
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar PDF");
        } finally {
            TransactionManager.close();
        }
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