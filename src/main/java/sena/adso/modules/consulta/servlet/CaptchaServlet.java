package sena.adso.modules.consulta.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sena.adso.core.util.SimpleCaptcha;

/**
 * Servlet que genera y sirve imágenes CAPTCHA para la consulta pública.
 * 
 * Ruta: /captcha — pública (excluida en RouteMatcher)
 * Retorna JSON con la imagen en Base64 para uso AJAX.
 */
@WebServlet(name = "CaptchaServlet", urlPatterns = { "/captcha" })
public class CaptchaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String captchaBase64 = SimpleCaptcha.generate(req);

        String json = "{\"success\":true,\"image\":\"" + captchaBase64 + "\"}";
        resp.getWriter().write(json);
    }
}