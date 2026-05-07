package sena.adso.core.audit;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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

@WebFilter(filterName = "AuditInterceptor", urlPatterns = "/*")
public class AuditInterceptor implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        long requestStart = System.currentTimeMillis();

        try {
            chain.doFilter(request, response);
        } finally {

            if (AuditContext.hasContext()) {
                AuditEntry entry = AuditContext.get();

                // Completar datos
                completeAuditEntry(entry, req, resp, requestStart);

                // Enviar a persistencia
                AsyncAuditWriter.submit(entry);
            } else {
            }

            // SIEMPRE limpiar
            AuditContext.clear();
        }
    }

    @Override
    public void destroy() {
    }

    // ============================================================
    // COMPLETAR ENTRY
    // ============================================================

    private void completeAuditEntry(AuditEntry entry, HttpServletRequest req,
            HttpServletResponse resp, long requestStart) {

        // Tiempo
        entry.setExecutionTimeMs(System.currentTimeMillis() - requestStart);

        // Status
        int status = resp.getStatus();
        if (status >= 400 && entry.getStatus() == null) {
            entry.setStatus("FAILED");
        } else if (entry.getStatus() == null) {
            entry.setStatus("SUCCESS");
        }

        // Request data
        if (entry.getRequestData() == null) {
            String reqData = buildRequestData(req);
            entry.setRequestData(reqData);
        }

        // Response data
        if (entry.getResponseData() == null) {
            String respData = buildResponseData(resp);
            entry.setResponseData(respData);
        }

        // IP y User-Agent
        if (entry.getIpAddress() == null) {
            entry.setIpAddress(extractClientIp(req));
        }
        if (entry.getUserAgent() == null) {
            entry.setUserAgent(req.getHeader("User-Agent"));
        }

        // Username fallback
        if (entry.getUsername() == null) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                Object user = session.getAttribute("usuario");
                if (user != null) {
                    entry.setUsername(user.toString());
                }
            }
        }
    }

    // ============================================================
    // BUILDERS
    // ============================================================

    private String buildRequestData(HttpServletRequest req) {

        Map<String, Object> data = new HashMap<>();

        // Parámetros
        Map<String, String> params = new HashMap<>();
        Enumeration<String> paramNames = req.getParameterNames();
        int paramCount = 0;
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            paramCount++;
            if (!isSensitiveParam(name)) {
                String value = req.getParameter(name);
                params.put(name, truncate(value, 100));
            } else {
                params.put(name, "***");
            }
        }

        if (!params.isEmpty()) {
            data.put("params", params);
        }

        // Metadata
        data.put("method", req.getMethod());
        data.put("uri", req.getRequestURI());

        String query = req.getQueryString();
        if (query != null) {
            data.put("query", query);
        }

        // Headers
        Map<String, String> headers = new HashMap<>();
        String contentType = req.getHeader("Content-Type");
        if (contentType != null)
            headers.put("Content-Type", contentType);
        String accept = req.getHeader("Accept");
        if (accept != null)
            headers.put("Accept", accept);

        if (!headers.isEmpty()) {
            data.put("headers", headers);
        }

        String result = toJson(data);
        return result;
    }

    private String buildResponseData(HttpServletResponse resp) {

        Map<String, Object> data = new HashMap<>();
        data.put("status", resp.getStatus());
        if (resp.getContentType() != null) {
            data.put("contentType", resp.getContentType());
        }

        String result = toJson(data);
        return result;
    }

    // ============================================================
    // UTILIDADES
    // ============================================================

    private boolean isSensitiveParam(String name) {
        String lower = name.toLowerCase();
        return lower.contains("password")
                || lower.contains("passwd")
                || lower.contains("otp")
                || lower.contains("token")
                || lower.contains("secret");
    }

    private String truncate(String value, int maxLength) {
        if (value == null)
            return null;
        return value.length() > maxLength ? value.substring(0, maxLength) + "..." : value;
    }

    private String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first)
                sb.append(",");
            first = false;
            sb.append("\"").append(escapeJson(entry.getKey())).append("\":");
            Object value = entry.getValue();
            if (value instanceof Map) {
                sb.append(toJson((Map<String, Object>) value));
            } else if (value instanceof Number || value instanceof Boolean) {
                sb.append(value);
            } else {
                sb.append("\"").append(escapeJson(value != null ? value.toString() : "")).append("\"");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String extractClientIp(HttpServletRequest req) {
        String xfHeader = req.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isBlank()) {
            return xfHeader.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }
}