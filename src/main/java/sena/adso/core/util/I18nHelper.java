package sena.adso.core.util;

import java.util.Locale;
import java.util.ResourceBundle;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Helper para internacionalización (i18n).
 *
 * Lee el idioma de la sesión HTTP (seteado por LocaleFilter) y carga
 * el ResourceBundle correspondiente. Si no hay sesión o idioma, usa 'es'.
 *
 * Thread-safe: ResourceBundle se carga por llamada, no se cachea en variable
 * estática
 * para permitir cambio de idioma en runtime sin reiniciar.
 */
public final class I18nHelper {

    private static final String BUNDLE_NAME = "messages";
    private static final String DEFAULT_LANG = "es";

    private I18nHelper() {
        // Utilidad, no instanciable
    }

    /**
     * Obtiene un mensaje traducido según el idioma de la sesión.
     *
     * @param req Request actual para obtener la sesión
     * @param key Clave del properties (ej: "login.titulo")
     * @return Texto traducido, o la clave entre corchetes si no existe
     */
    public static String get(HttpServletRequest req, String key) {
        Locale locale = resolveLocale(req);
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        return bundle.containsKey(key) ? bundle.getString(key) : "[" + key + "]";
    }

    /**
     * Obtiene un mensaje con parámetros interpolados.
     *
     * @param req    Request actual
     * @param key    Clave del properties
     * @param params Parámetros para MessageFormat
     * @return Texto traducido con parámetros insertados
     */
    public static String get(HttpServletRequest req, String key, Object... params) {
        String pattern = get(req, key);
        return java.text.MessageFormat.format(pattern, params);
    }

    /**
     * Obtiene un mensaje directamente con un Locale explícito.
     * Útil cuando no hay request disponible (ej: en servicios de fondo).
     */
    public static String get(Locale locale, String key) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        return bundle.containsKey(key) ? bundle.getString(key) : "[" + key + "]";
    }

    /**
     * Obtiene el Locale activo de la sesión.
     */
    public static Locale getLocale(HttpServletRequest req) {
        return resolveLocale(req);
    }

    /**
     * Cambia el idioma de la sesión actual.
     */
    public static void setLocale(HttpServletRequest req, String lang) {
        HttpSession session = req.getSession();
        if (isValidLang(lang)) {
            session.setAttribute("lang", lang);
        }
    }

    // ============================================================
    // PRIVADOS
    // ============================================================

    private static Locale resolveLocale(HttpServletRequest req) {
        String lang = DEFAULT_LANG;

        HttpSession session = req.getSession(false);
        if (session != null) {
            Object sessionLang = session.getAttribute("lang");
            if (sessionLang instanceof String && isValidLang((String) sessionLang)) {
                lang = (String) sessionLang;
            }
        }

        return new Locale(lang);
    }

    private static boolean isValidLang(String lang) {
        return lang != null && (lang.equals("es") || lang.equals("en") || lang.equals("it"));
    }
}