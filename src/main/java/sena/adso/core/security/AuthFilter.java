package sena.adso.core.security;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sena.adso.core.util.I18nHelper;

/**
 * Filtro central de autenticación y autorización.
 * 
 * Orquesta los componentes de seguridad sin tener lógica de negocio propia:
 * 1. RouteMatcher: ¿Es ruta pública? → Permite
 * 2. Authenticator: ¿Usuario autenticado y OTP verificado? → Si no, redirect
 * login
 * 3. Authorizer: ¿Usuario tiene permiso para esta ruta? → Si no, 403
 * 
 * Los componentes se inyectan vía ServletContext (seteados por AppInitializer).
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = "/*")
public class AuthFilter implements Filter {

    private Authenticator authenticator;
    private Authorizer authorizer;
    private RouteMatcher publicRoutes;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext ctx = filterConfig.getServletContext();

        this.authenticator = (Authenticator) ctx.getAttribute("authenticator");
        this.authorizer = (Authorizer) ctx.getAttribute("authorizer");
        this.publicRoutes = (RouteMatcher) ctx.getAttribute("publicRoutes");

        // Validar que todo esté configurado
        if (authenticator == null || authorizer == null || publicRoutes == null) {
            throw new ServletException(
                    "AuthFilter no inicializado correctamente. " +
                            "Faltan componentes de seguridad en ServletContext. " +
                            "¿AppInitializer se ejecutó correctamente?");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();

        // 1. Rutas públicas pasan libres
        if (publicRoutes.matches(req)) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Autenticación
        boolean autenticado = authenticator.authenticate(req, resp);

        if (!autenticado) {
            redirectToLogin(req, resp);
            return;
        }

        // 3. Autorización
        boolean autorizado = authorizer.authorize(req, resp);

        if (!autorizado) {
            sendForbidden(req, resp);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Nada que limpiar
    }

    // ============================================================
    // PRIVADOS
    // ============================================================

    private void redirectToLogin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String loginUrl = req.getContextPath() + "/login";

        // Si es AJAX, devolver 401 en vez de redirect
        if (isAjaxRequest(req)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":\"Sesión no válida\"}");
        } else {
            resp.sendRedirect(loginUrl);
        }
    }

    private void sendForbidden(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String errorMessage = I18nHelper.get(req, "error.acceso");
        req.setAttribute("error", errorMessage);
        req.setAttribute("statusCode", HttpServletResponse.SC_FORBIDDEN);

        // Si es AJAX, devolver 403 JSON
        if (isAjaxRequest(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":\"" + errorMessage + "\"}");
        } else {
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }

    private boolean isAjaxRequest(HttpServletRequest req) {
        String requestedWith = req.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWith);
    }
}