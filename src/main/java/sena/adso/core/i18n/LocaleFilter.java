package sena.adso.core.i18n;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Filtro de internacionalización.
 * 
 * Debe ejecutarse ANTES de AuthFilter en web.xml.
 * Establece el idioma de la sesión basado en:
 * 1. Parámetro ?lang= en URL
 * 2. Atributo lang en sesión (previamente seleccionado)
 * 3. Default: es (español)
 */
@WebFilter(filterName = "LocaleFilter", urlPatterns = "/*")
public class LocaleFilter implements Filter {

    private static final Set<String> IDIOMAS_VALIDOS = Set.of("es", "en", "it");
    private static final String DEFAULT_LANG = "es";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nada que inicializar
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession();
        String lang = req.getParameter("lang");

        // 1. Si viene ?lang= válido, actualizar sesión
        if (lang != null && IDIOMAS_VALIDOS.contains(lang)) {
            session.setAttribute("lang", lang);
        }
        // 2. Si no hay lang en sesión, poner default
        else if (session.getAttribute("lang") == null) {
            session.setAttribute("lang", DEFAULT_LANG);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Nada que limpiar
    }
}