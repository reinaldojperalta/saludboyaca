package sena.adso.modules.auth.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sena.adso.core.dao.TransactionManager;
import sena.adso.core.servlet.BaseServlet;
import sena.adso.modules.auth.service.OTPService;

/**
 * Servlet de verificación OTP.
 * 
 * Flujo:
 * 1. GET: Muestra otp.jsp con email enmascarado
 * 2. POST: Valida código → marca sesión como verificada → redirect a /dashboard
 */
@WebServlet(name = "OTPServlet", urlPatterns = { "/otp" })
public class OTPServlet extends BaseServlet {

    private final OTPService otpService;

    public OTPServlet() {
        this.otpService = new OTPService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        // Si no hay sesión o no hay OTP pendiente, ir al login
        if (session == null || session.getAttribute("usuarioId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Enmascarar email para mostrar en la vista
        String email = (String) session.getAttribute("usuarioEmail");
        if (email == null) {
            // Fallback: obtener del usuario en sesión
            Object usuario = session.getAttribute("usuario");
            if (usuario != null) {
                email = usuario.toString();
            }
        }

        req.setAttribute("emailMasked", enmascararEmail(email));
        forward(req, resp, "auth/otp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String codigoIngresado = req.getParameter("otpCodigo");
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // ===== FIX: Transacción para validar OTP =====
        boolean otpValido;
        TransactionManager.begin();
        try {
            otpValido = otpService.validarOTP(usuarioId, codigoIngresado);
            TransactionManager.commit();
        } catch (Exception e) {
            TransactionManager.rollback();
            throw e;
        } finally {
            TransactionManager.close();
        }
        // ===== FIN FIX =====

        if (otpValido) {
            // Éxito: marcar como usado y verificar sesión
            // ===== FIX: Transacción para marcar usado =====
            TransactionManager.begin();
            try {
                otpService.marcarUsado(usuarioId, codigoIngresado);
                TransactionManager.commit();
            } catch (Exception e) {
                TransactionManager.rollback();
                throw e;
            } finally {
                TransactionManager.close();
            }
            // ===== FIN FIX =====

            session.setAttribute("otpVerificado", true);

            // Actualizar auditoría
            audit(req, "OTP_VERIFY", "Usuario", usuarioId);

            resp.sendRedirect(req.getContextPath() + "/dashboard");

        } else {
            // Fallo: mostrar error
            req.setAttribute("error", getI18n(req, "otp.error"));
            req.setAttribute("emailMasked", enmascararEmail(
                    (String) session.getAttribute("usuarioEmail")));
            forward(req, resp, "auth/otp");
        }
    }

    // ============================================================
    // PRIVADOS
    // ============================================================

    private String enmascararEmail(String email) {
        if (email == null || !email.contains("@"))
            return "***";

        String[] partes = email.split("@");
        String local = partes[0];
        String dominio = partes[1];

        if (local.length() <= 3) {
            return local + "***@" + dominio;
        }

        return local.substring(0, 3) + "***@" + dominio;
    }
}