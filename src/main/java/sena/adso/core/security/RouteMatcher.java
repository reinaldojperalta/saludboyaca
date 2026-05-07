package sena.adso.core.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Define qué rutas son públicas (no requieren autenticación).
 * 
 * Se usa en AuthFilter para permitir paso libre a rutas como /login,
 * /consulta, recursos estáticos, etc.
 */
public final class RouteMatcher {

    private final Set<String> exactMatches;
    private final Set<String> prefixMatches;
    private final Set<String> suffixMatches;

    public RouteMatcher() {
        this.exactMatches = new HashSet<>();
        this.prefixMatches = new HashSet<>();
        this.suffixMatches = new HashSet<>();

        initializePublicRoutes();
    }

    /**
     * Verifica si una URI es pública (no requiere autenticación).
     */
    public boolean matches(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String relativeUri = uri.substring(contextPath.length());

        // Match exacto
        if (exactMatches.contains(relativeUri)) {
            return true;
        }

        // Match por prefijo
        for (String prefix : prefixMatches) {
            if (relativeUri.startsWith(prefix)) {
                return true;
            }
        }

        // Match por sufijo (extensiones de archivo)
        for (String suffix : suffixMatches) {
            if (relativeUri.endsWith(suffix)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Agrega una ruta pública exacta.
     */
    public void addExact(String route) {
        exactMatches.add(route);
    }

    /**
     * Agrega un prefijo de ruta pública.
     */
    public void addPrefix(String prefix) {
        prefixMatches.add(prefix);
    }

    /**
     * Agrega una extensión de archivo pública.
     */
    public void addSuffix(String suffix) {
        suffixMatches.add(suffix);
    }

    // ============================================================
    // PRIVADOS
    // ============================================================

    private void initializePublicRoutes() {
        // Rutas exactas públicas
        exactMatches.addAll(Arrays.asList(
                "/login",
                "/otp",
                "/locale",
                "/debug.jsp",
                "/captcha",
                "/debug",
                "/logout",
                "/consulta",
                "/consulta-cita",
                "/error"));

        // Prefijos públicos (recursos estáticos, APIs públicas)
        prefixMatches.addAll(Arrays.asList(
                "/resources/", // CSS, JS, imágenes
                "/api/public/", // APIs públicas (si las hay)
                "/views/templates/" // JSP templates incluidos directamente
        ));

        // Extensiones de archivo públicas (fallback)
        suffixMatches.addAll(Arrays.asList(
                ".css",
                ".js",
                ".png",
                ".jpg",
                ".jpeg",
                ".gif",
                ".svg",
                ".ico",
                ".woff",
                ".woff2",
                ".ttf"));
    }
}