package sena.adso.core.i18n;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sena.adso.core.servlet.BaseServlet;
import sena.adso.core.util.I18nHelper;

/**
 * Servlet para cambio de idioma vía AJAX sin recarga de página.
 * 
 * Endpoints:
 * - POST /locale?lang=es|en|it → Cambia idioma en sesión, devuelve JSON con
 * claves traducidas
 * - GET /locale?keys=login.titulo,login.usuario → Devuelve traducciones
 * específicas
 * 
 * Usado por el selector de idiomas en login.jsp y dashboard.jsp.
 */
@WebServlet(name = "LocaleServlet", urlPatterns = { "/locale" })
public class LocaleServlet extends BaseServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String lang = req.getParameter("lang");
        String[] keys = req.getParameterValues("keys");

        // Validar idioma
        if (!isValidLang(lang)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":\"Idioma no válido\"}");
            return;
        }

        // Cambiar idioma en sesión
        I18nHelper.setLocale(req, lang);

        // Construir respuesta JSON con las claves solicitadas
        Map<String, String> translations = new HashMap<>();

        if (keys != null && keys.length > 0) {
            for (String key : keys) {
                translations.put(key, I18nHelper.get(req, key));
            }
        }

        // Siempre incluir confirmación
        translations.put("_lang", lang);
        translations.put("_status", "ok");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(translations));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // GET también permite cambio de idioma (fallback) o solo consulta
        String lang = req.getParameter("lang");
        String keysParam = req.getParameter("keys");

        if (lang != null && isValidLang(lang)) {
            I18nHelper.setLocale(req, lang);
        }

        Map<String, String> translations = new HashMap<>();
        translations.put("_lang", I18nHelper.getLocale(req).getLanguage());
        translations.put("_status", "ok");

        if (keysParam != null && !keysParam.isBlank()) {
            String[] keys = keysParam.split(",");
            for (String key : keys) {
                translations.put(key.trim(), I18nHelper.get(req, key.trim()));
            }
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(translations));
    }

    private boolean isValidLang(String lang) {
        return lang != null && (lang.equals("es") || lang.equals("en") || lang.equals("it"));
    }
}