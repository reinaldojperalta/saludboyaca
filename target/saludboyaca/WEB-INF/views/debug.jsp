<%-- /WEB-INF/views/debug.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, java.sql.*, sena.adso.modules.debug.model.ResultadoTest" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>DEBUG — SaludBoyacá</title>
    <style>
        body { font-family: monospace; background: #0d1117; color: #c9d1d9; padding: 20px; line-height: 1.6; }
        h2 { color: #58a6ff; border-bottom: 1px solid #30363d; padding-bottom: 5px; margin-top: 30px; }
        h3 { color: #7ee787; margin-top: 20px; }
        .section { background: #161b22; border: 1px solid #30363d; padding: 15px; margin: 10px 0; border-radius: 6px; }
        .ok { color: #3fb950; }
        .err { color: #f85149; }
        .warn { color: #d29922; }
        .info { color: #58a6ff; }
        .mono { font-family: 'Courier New', monospace; background: #0d1117; padding: 2px 6px; border-radius: 3px; }
        pre { background: #0d1117; border: 1px solid #30363d; padding: 12px; overflow-x: auto; border-radius: 6px; color: #e6edf3; }
        table { border-collapse: collapse; width: 100%; margin: 10px 0; font-size: 13px; }
        th, td { border: 1px solid #30363d; padding: 8px 12px; text-align: left; }
        th { background: #21262d; color: #7ee787; }
        tr:hover { background: #1c2128; }
        .test-pass { background: #0d2818; }
        .test-fail { background: #3d0d0d; }
        .badge { display: inline-block; padding: 2px 8px; border-radius: 12px; font-size: 11px; font-weight: bold; }
        .badge-ok { background: #238636; color: white; }
        .badge-err { background: #da3633; color: white; }
        .badge-warn { background: #9e6a03; color: white; }
        hr { border: none; border-top: 1px solid #30363d; margin: 20px 0; }
        .timestamp { color: #8b949e; font-size: 12px; }
    </style>
</head>
<body>

<h1 style="color:#f0883e;">DEBUG SUITE — SaludBoyacá</h1>
<p class="timestamp">Generado: <%= new java.util.Date() %> | Servlet: ${serverInfo} | API: ${servletApi} | Java: ${javaVersion}</p>

<%
    // Extraer atributos del request una sola vez para toda la página
    Boolean dbConnected = (Boolean) request.getAttribute("dbConnected");
    if (dbConnected == null) dbConnected = false;
    
    Integer totalTests = (Integer) request.getAttribute("totalTests");
    if (totalTests == null) totalTests = 0;
    
    Integer testsExitosos = (Integer) request.getAttribute("testsExitosos");
    if (testsExitosos == null) testsExitosos = 0;
    
    Integer testsFallidos = (Integer) request.getAttribute("testsFallidos");
    if (testsFallidos == null) testsFallidos = 0;
    
    List<ResultadoTest> resultados = (List<ResultadoTest>) request.getAttribute("resultados");
    if (resultados == null) resultados = new ArrayList<>();
    
    List<String[]> tablas = (List<String[]>) request.getAttribute("tablas");
    if (tablas == null) tablas = new ArrayList<>();
    
    String dbUrl = (String) request.getAttribute("dbUrl");
    String dbUser = (String) request.getAttribute("dbUser");
%>

<!-- ============================================================
     SECCION 1: RESUMEN EJECUTIVO
     ============================================================ -->
<h2>RESUMEN EJECUTIVO</h2>
<div class="section">
    <table>
        <tr>
            <th>Métrica</th>
            <th>Valor</th>
            <th>Estado</th>
        </tr>
        <tr>
            <td>Tests ejecutados</td>
            <td><%= totalTests %></td>
            <td><span class="badge badge-ok">OK</span></td>
        </tr>
        <tr>
            <td>Tests exitosos</td>
            <td class="<%= testsExitosos > 0 ? "ok" : "err" %>"><%= testsExitosos %></td>
            <td><%= testsExitosos == totalTests ? "<span class='badge badge-ok'>ALL PASS</span>" : "<span class='badge badge-warn'>PARCIAL</span>" %></td>
        </tr>
        <tr>
            <td>Tests fallidos</td>
            <td class="<%= testsFallidos > 0 ? "err" : "ok" %>"><%= testsFallidos %></td>
            <td><%= testsFallidos > 0 ? "<span class='badge badge-err'>REVISAR</span>" : "<span class='badge badge-ok'>OK</span>" %></td>
        </tr>
        <tr>
            <td>Conexión BD</td>
            <td class="<%= dbConnected ? "ok" : "err" %>"><%= dbConnected ? "CONECTADO" : "DESCONECTADO" %></td>
            <td><%= dbConnected ? "<span class='badge badge-ok'>OK</span>" : "<span class='badge badge-err'>CRITICO</span>" %></td>
        </tr>
        <tr>
            <td>Driver en Tomcat</td>
            <td class="mono">
                <%
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        out.print("<span class='ok'>com.mysql.cj.jdbc.Driver CARGADO</span>");
                    } catch (ClassNotFoundException e) {
                        out.print("<span class='err'>NO ENCONTRADO</span>");
                    }
                %>
            </td>
            <td>
                <%
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        out.print("<span class='badge badge-ok'>OK</span>");
                    } catch (ClassNotFoundException e) {
                        out.print("<span class='badge badge-err'>FALTA JAR</span>");
                    }
                %>
            </td>
        </tr>
        <tr>
            <td>Test directo DriverManager</td>
            <td class="mono">
                <%
                    String testUrl = "jdbc:mysql://localhost:3307/vacunasdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                    String testUser = "root";
                    String testPass = "";
                    try (Connection testConn = java.sql.DriverManager.getConnection(testUrl, testUser, testPass)) {
                        out.print("<span class='ok'>OK (" + testConn.getMetaData().getDatabaseProductVersion() + ")</span>");
                    } catch (Exception e) {
                        out.print("<span class='err'>" + e.getClass().getSimpleName() + ": " + e.getMessage() + "</span>");
                    }
                %>
            </td>
            <td>-</td>
        </tr>
        <tr>
            <td>URL BD</td>
            <td class="mono"><%= dbUrl != null ? dbUrl : "N/A" %></td>
            <td>-</td>
        </tr>
        <tr>
            <td>Usuario BD</td>
            <td class="mono"><%= dbUser != null ? dbUser : "N/A" %></td>
            <td>-</td>
        </tr>
    </table>
    
    <% if (!dbConnected) { %>
        <div style="background:#3d0d0d; border:1px solid #f85149; padding:15px; border-radius:6px; margin-top:10px;">
            <h3 class="err">CONEXION BD FALLIDA — ESTO BLOQUEA TODO</h3>
            <p>Sin conexión a la base de datos, NINGUN test puede pasar. Verificar:</p>
            <ul>
                <li>MySQL está corriendo?</li>
                <li>Variables de entorno DB_URL, DB_USER, DB_PASS están seteadas?</li>
                <li>Puerto correcto? (por defecto 3306, en tu config puede ser 3307)</li>
                <li>Base de datos 'saludboyaca' existe?</li>
            </ul>
            <p class="mono" style="margin-top:10px;">Variables esperadas: DB_URL, DB_USER, DB_PASS, EMAIL_HOST, EMAIL_PORT, EMAIL_USER, EMAIL_PASS</p>
        </div>
    <% } %>
</div>

<!-- ============================================================
     SECCION 2: TESTS FUNCIONALES
     ============================================================ -->
<h2>TESTS FUNCIONALES — DETALLE COMPLETO</h2>

<% 
    for (ResultadoTest t : resultados) { 
%>
<div class="section <%= t.isExito() ? "test-pass" : "test-fail" %>">
    <h3>
        <% if (t.isExito()) { %><span class="ok">[PASS]</span><% } else { %><span class="err">[FAIL]</span><% } %>
        <%= t.getNombre() %> — <%= t.getTiempoMs() %>ms
    </h3>
    
    <table>
        <tr><th width="20%">Campo</th><th>Valor</th></tr>
        <tr>
            <td>Estado</td>
            <td class="<%= t.isExito() ? "ok" : "err" %>"><%= t.isExito() ? "EXITOSO" : "FALLIDO" %></td>
        </tr>
        <tr>
            <td>Query/Operación</td>
            <td class="mono"><%= t.getQueryEjecutada() != null ? t.getQueryEjecutada() : "N/A" %></td>
        </tr>
        <% if (t.isExito()) { %>
        <tr>
            <td>Resultado obtenido</td>
            <td class="mono ok"><%= t.getResultadoObtenido() != null ? t.getResultadoObtenido() : "N/A" %></td>
        </tr>
        <% } else { %>
        <tr>
            <td>Error</td>
            <td class="mono err"><%= t.getError() != null ? t.getError() : "Sin mensaje de error" %></td>
        </tr>
        <tr>
            <td>Diagnóstico LLM</td>
            <td class="warn">
                <% 
                    if ("DAO FindById".equals(t.getNombre()) && !dbConnected) {
                        out.print("Fallo por conexión BD. Revisar DatabaseConfig y variables de entorno.");
                    } else if ("DAO FindById".equals(t.getNombre()) && dbConnected) {
                        out.print("La tabla no tiene datos de prueba (id=1 no existe) o el schema está incompleto. Ejecutar scripts SQL de datos de prueba.");
                    } else if ("RBAC Permission".equals(t.getNombre())) {
                        out.print("RBACCache no cargó correctamente desde AppInitializer. Revisar tablas roles, permissions, role_permissions.");
                    } else if ("i18n Bundles".equals(t.getNombre())) {
                        out.print("Faltan archivos messages.properties en src/main/resources/. Crear messages.properties, messages_en.properties, messages_it.properties.");
                    } else if ("OTP Ciclo".equals(t.getNombre())) {
                        out.print("Fallo en operaciones OTP. Revisar tabla otp_tokens y que el usuario id=1 exista (o id=0 si se usó fallback).");
                    } else if ("Audit Write".equals(t.getNombre())) {
                        out.print("AsyncAuditWriter no inicializado (AppInitializer falló) o tabla activity_logs no existe.");
                    } else if ("Transaction Cycle".equals(t.getNombre())) {
                        out.print("TransactionManager no funciona correctamente. Revisar que DatabaseConfig.getConnection() retorne conexión válida.");
                    } else {
                        out.print("Error no catalogado. Revisar stack trace en logs del servidor.");
                    }
                %>
            </td>
        </tr>
        <% } %>
    </table>
</div>
<% } %>

<!-- ============================================================
     SECCION 3: SCHEMA
     ============================================================ -->
<h2>SCHEMA — TABLAS Y REGISTROS</h2>
<div class="section">
    <table>
        <tr>
            <th>#</th>
            <th>Tabla</th>
            <th>Registros</th>
            <th>Estado</th>
        </tr>
        <% 
            int idx = 0;
            for (String[] t : tablas) { 
                idx++;
                String estadoBadge;
                if ("-2".equals(t[1])) {
                    estadoBadge = "<span class='badge badge-err'>SIN PERMISO</span>";
                } else if ("-1".equals(t[1])) {
                    estadoBadge = "<span class='badge badge-err'>ERROR SQL</span>";
                } else if ("0".equals(t[1])) {
                    estadoBadge = "<span class='badge badge-warn'>VACIA</span>";
                } else {
                    estadoBadge = "<span class='badge badge-ok'>OK</span>";
                }
                
                String countDisplay;
                if ("-2".equals(t[1])) {
                    countDisplay = "<span class='err'>DENIED</span>";
                } else if ("-1".equals(t[1])) {
                    countDisplay = "<span class='err'>ERROR</span>";
                } else {
                    countDisplay = "<span class='ok'>" + t[1] + "</span>";
                }
        %>
        <tr>
            <td><%= idx %></td>
            <td class="mono"><%= t[0] %></td>
            <td><%= countDisplay %></td>
            <td><%= estadoBadge %></td>
        </tr>
        <% } %>
    </table>
    
    <h3>Tablas críticas esperadas:</h3>
    <pre>
usuarios          (debe tener >=1 usuario de prueba con id=1)
pacientes         (debe tener >=1 paciente de prueba con id=1)
citas             (puede estar vacía al inicio)
roles             (debe tener: MEDICO, RECEPCIONISTA, ENFERMERO)
permissions       (debe tener permisos como dashboard:ver, cita:crear, etc.)
role_permissions  (debe tener mapeos N:M)
otp_tokens        (se crea dinámicamente durante tests)
activity_logs     (debe existir, se escribe durante tests)
especialidades    (debe tener datos de referencia)
horarios          (debe tener datos de referencia)
    </pre>
</div>

<!-- ============================================================
     SECCION 4: SERVLET CONTEXT
     ============================================================ -->
<h2>SERVLET CONTEXT — COMPONENTES</h2>
<div class="section">
    <h3>Atributos esperados (seteados por AppInitializer):</h3>
    <table>
        <tr><th>Atributo</th><th>Estado</th><th>Tipo</th><th>Diagnóstico</th></tr>
        <tr>
            <td class="mono">rbacCache</td>
            <td>
                <% 
                    Object rbac = application.getAttribute("rbacCache");
                    out.print(rbac != null ? "<span class='ok'>PRESENTE</span>" : "<span class='err'>AUSENTE</span>");
                %>
            </td>
            <td class="mono">
                <% 
                    out.print(rbac != null ? rbac.getClass().getName() : "null");
                %>
            </td>
            <td>
                <% 
                    out.print(rbac != null ? "OK" : "<span class='err'>AppInitializer falló al cargar RBAC. Revisar tablas roles/permissions.</span>");
                %>
            </td>
        </tr>
        <tr>
            <td class="mono">authenticator</td>
            <td>
                <% 
                    Object auth = application.getAttribute("authenticator");
                    out.print(auth != null ? "<span class='ok'>PRESENTE</span>" : "<span class='err'>AUSENTE</span>");
                %>
            </td>
            <td class="mono">
                <% 
                    out.print(auth != null ? auth.getClass().getName() : "null");
                %>
            </td>
            <td>
                <% 
                    out.print(auth != null ? "OK" : "<span class='err'>AppInitializer no inyectó authenticator.</span>");
                %>
            </td>
        </tr>
        <tr>
            <td class="mono">authorizer</td>
            <td>
                <% 
                    Object authz = application.getAttribute("authorizer");
                    out.print(authz != null ? "<span class='ok'>PRESENTE</span>" : "<span class='err'>AUSENTE</span>");
                %>
            </td>
            <td class="mono">
                <% 
                    out.print(authz != null ? authz.getClass().getName() : "null");
                %>
            </td>
            <td>
                <% 
                    out.print(authz != null ? "OK" : "<span class='err'>AppInitializer no inyectó authorizer.</span>");
                %>
            </td>
        </tr>
        <tr>
            <td class="mono">publicRoutes</td>
            <td>
                <% 
                    Object routes = application.getAttribute("publicRoutes");
                    out.print(routes != null ? "<span class='ok'>PRESENTE</span>" : "<span class='err'>AUSENTE</span>");
                %>
            </td>
            <td class="mono">
                <% 
                    out.print(routes != null ? routes.getClass().getName() : "null");
                %>
            </td>
            <td>
                <% 
                    out.print(routes != null ? "OK" : "<span class='err'>AppInitializer no inyectó RouteMatcher.</span>");
                %>
            </td>
        </tr>
    </table>
    
    <h3>Todos los atributos en contexto:</h3>
    <pre><%
        java.util.Enumeration<String> ctxNames = application.getAttributeNames();
        while (ctxNames.hasMoreElements()) {
            String k = ctxNames.nextElement();
            Object v = application.getAttribute(k);
            out.println(k + " = " + (v != null ? v.getClass().getName() : "null"));
        }
    %></pre>
</div>

<!-- ============================================================
     SECCION 5: SESION
     ============================================================ -->
<h2>SESION HTTP ACTUAL</h2>
<div class="section">
    <table>
        <tr><th>Propiedad</th><th>Valor</th></tr>
        <tr><td>Session ID</td><td class="mono"><%= session.getId() %></td></tr>
        <tr><td>Creada</td><td><%= new java.util.Date(session.getCreationTime()) %></td></tr>
        <tr><td>Ultimo acceso</td><td><%= new java.util.Date(session.getLastAccessedTime()) %></td></tr>
        <tr><td>Max inactive</td><td class="mono"><%= session.getMaxInactiveInterval() %>s</td></tr>
        <tr><td>Nueva?</td><td class="mono"><%= session.isNew() ? "SI" : "NO" %></td></tr>
    </table>
    
    <h3>Atributos de sesion:</h3>
    <pre><%
        java.util.Enumeration<String> sessNames = session.getAttributeNames();
        boolean hasSess = false;
        while (sessNames.hasMoreElements()) {
            hasSess = true;
            String k = sessNames.nextElement();
            Object v = session.getAttribute(k);
            out.println(k + " = " + v + " [" + (v != null ? v.getClass().getName() : "null") + "]");
        }
        if (!hasSess) {
            out.println("[SIN ATRIBUTOS] — Sesion vacia. Usuario no autenticado.");
        }
    %></pre>
    
    <% if (session.getAttribute("usuario") == null) { %>
        <p class="warn">No hay usuario en sesion. Esto es NORMAL para /debug (ruta publica).</p>
    <% } %>
</div>

<!-- ============================================================
     SECCION 6: REQUEST
     ============================================================ -->
<h2>REQUEST INFO</h2>
<div class="section">
    <table>
        <tr><th>Propiedad</th><th>Valor</th></tr>
        <tr><td>Method</td><td class="mono"><%= request.getMethod() %></td></tr>
        <tr><td>Request URI</td><td class="mono"><%= request.getRequestURI() %></td></tr>
        <tr><td>Context Path</td><td class="mono"><%= request.getContextPath() %></td></tr>
        <tr><td>Servlet Path</td><td class="mono"><%= request.getServletPath() %></td></tr>
        <tr><td>Path Info</td><td class="mono"><%= request.getPathInfo() %></td></tr>
        <tr><td>Query String</td><td class="mono"><%= request.getQueryString() %></td></tr>
        <tr><td>Remote Addr</td><td class="mono"><%= request.getRemoteAddr() %></td></tr>
        <tr><td>User Agent</td><td class="mono"><%= request.getHeader("User-Agent") %></td></tr>
    </table>
</div>

<!-- ============================================================
     SECCION 7: JVM
     ============================================================ -->
<h2>JVM Y SISTEMA</h2>
<div class="section">
    <table>
        <tr><th>Propiedad</th><th>Valor</th></tr>
        <tr><td>Java Version</td><td class="mono"><%= System.getProperty("java.version") %></td></tr>
        <tr><td>Java Vendor</td><td class="mono"><%= System.getProperty("java.vendor") %></td></tr>
        <tr><td>OS Name</td><td class="mono"><%= System.getProperty("os.name") %></td></tr>
        <tr><td>OS Arch</td><td class="mono"><%= System.getProperty("os.arch") %></td></tr>
        <tr><td>Max Memory (MB)</td><td class="mono"><%= Runtime.getRuntime().maxMemory() / 1024 / 1024 %></td></tr>
        <tr><td>Total Memory (MB)</td><td class="mono"><%= Runtime.getRuntime().totalMemory() / 1024 / 1024 %></td></tr>
        <tr><td>Free Memory (MB)</td><td class="mono"><%= Runtime.getRuntime().freeMemory() / 1024 / 1024 %></td></tr>
        <tr><td>Used Memory (MB)</td><td class="mono"><%= (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024 %></td></tr>
        <tr><td>Available Processors</td><td class="mono"><%= Runtime.getRuntime().availableProcessors() %></td></tr>
    </table>
</div>

<!-- ============================================================
     SECCION 8: CHECKLIST
     ============================================================ -->
<h2>CHECKLIST DE DIAGNOSTICO</h2>
<div class="section">
    <p>Para que yo (el LLM) pueda ayudarte, necesito que verifiques:</p>
    <ol>
        <li><span class="<%= dbConnected ? "ok" : "err" %>">[<%= dbConnected ? "OK" : "FAIL" %>]</span> MySQL está corriendo y accesible</li>
        <li><span class="<%= (testsExitosos >= 1) ? "ok" : "err" %>">[<%= (testsExitosos >= 1) ? "OK" : "FAIL" %>]</span> Al menos 1 test pasa (indica que la arquitectura base funciona)</li>
        <li><span class="<%= (testsExitosos == totalTests) ? "ok" : "warn" %>">[<%= (testsExitosos == totalTests) ? "OK" : "PENDIENTE" %>]</span> Todos los tests pasan (prerrequisito para frontend)</li>
        <li><span class="<%= (application.getAttribute("rbacCache") != null) ? "ok" : "err" %>">[<%= (application.getAttribute("rbacCache") != null) ? "OK" : "FAIL" %>]</span> RBACCache cargado en memoria</li>
        <li><span class="<%= (application.getAttribute("authenticator") != null) ? "ok" : "err" %>">[<%= (application.getAttribute("authenticator") != null) ? "OK" : "FAIL" %>]</span> Componentes de seguridad inyectados</li>
    </ol>
    
    <% if (testsFallidos > 0) { %>
        <div style="background:#3d0d0d; border:1px solid #f85149; padding:15px; border-radius:6px; margin-top:15px;">
            <h3 class="err">HAY TESTS FALLIDOS</h3>
            <p>Copia y pega esta página completa (o la sección de tests fallidos) en tu próximo mensaje para que pueda diagnosticar.</p>
            <p class="info">Información útil para mí:</p>
            <ul>
                <li>Qué test falló? (nombre exacto)</li>
                <li>Cuál fue el error? (mensaje completo)</li>
                <li>La conexión a BD está activa?</li>
                <li>Qué tablas aparecen en la sección Schema?</li>
                <li>Cuántos registros tiene cada tabla?</li>
            </ul>
        </div>
    <% } %>
</div>

<hr>
<p class="timestamp" style="text-align:center;">SaludBoyacá Debug Suite v2.0 — Para uso de desarrolladores y LLMs</p>

</body>
</html>