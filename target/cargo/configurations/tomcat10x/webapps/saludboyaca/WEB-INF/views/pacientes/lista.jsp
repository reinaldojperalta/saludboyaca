<%-- 
    pacientes/lista.jsp — Lista de pacientes
    Ubicación: WEB-INF/views/pacientes/lista.jsp
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'es'}"/>
<fmt:setBundle basename="messages"/>

<!DOCTYPE html>
<html lang="${sessionScope.lang != null ? sessionScope.lang : 'es'}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="paciente.titulo"/> — SaludBoyacá</title>
    
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/saludboyaca.css">
    
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        'azul-salud': '#1A5276',
                        'verde-sena': '#39A900',
                        'celeste': '#2E86C1',
                        'gris-hielo': '#EAF0F7',
                        'morado-seguridad': '#6C3483'
                    }
                }
            }
        }
    </script>
    
    <style>
        body { background-color: #EAF0F7; }
        .metric-card { transition: transform 0.2s; }
        .metric-card:hover { transform: translateY(-5px); }
    </style>
</head>
<body class="bg-gris-hielo font-sans min-h-screen flex flex-col">

    <%@ include file="/WEB-INF/views/templates/header.jsp" %>
    
    <main class="flex-grow max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8 w-full">
        
        <%-- Header --%>
        <div class="mb-8 flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4">
            <div>
                <h1 class="text-3xl font-bold text-azul-salud">
                    <fmt:message key="paciente.titulo"/>
                </h1>
                <p class="text-gray-600 mt-1">
                    <fmt:message key="paciente.subtitulo"/>
                </p>
            </div>
            
            <c:if test="${hasPermissionPaciente}">
                <a href="${pageContext.request.contextPath}/pacientes?accion=nuevo" 
                   class="inline-flex items-center px-4 py-2 bg-verde-sena hover:bg-opacity-90 text-white font-semibold rounded-lg transition-colors shadow-sm">
                    <i class="fa-solid fa-plus mr-2"></i>
                    <fmt:message key="paciente.nuevo"/>
                </a>
            </c:if>
        </div>

        <%-- Métrica resumen --%>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <div class="metric-card bg-white rounded-xl p-6 shadow-sm border-l-4 border-verde-sena flex items-center">
                <div class="bg-green-50 p-4 rounded-full mr-4 text-verde-sena">
                    <i class="fa-solid fa-users text-2xl"></i>
                </div>
                <div>
                    <p class="text-sm text-gray-500 font-semibold">
                        <fmt:message key="paciente.total"/>
                    </p>
                    <h3 class="text-3xl font-bold text-gray-800">${pacientes.size()}</h3>
                </div>
            </div>
        </div>

        <%-- Mensaje de éxito/error --%>
        <c:if test="${not empty mensaje}">
            <div class="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg mb-6 flex items-center">
                <i class="fa-solid fa-check-circle mr-2"></i>
                ${mensaje}
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6 flex items-center">
                <i class="fa-solid fa-circle-exclamation mr-2"></i>
                ${error}
            </div>
        </c:if>

        <%-- Tabla de pacientes --%>
        <div class="bg-white rounded-xl shadow-sm overflow-hidden">
            <div class="overflow-x-auto">
                <table class="w-full text-left border-collapse">
                    <thead class="bg-gray-50 text-gray-600 text-sm">
                        <tr>
                            <th class="p-4 font-semibold"><fmt:message key="paciente.documento"/></th>
                            <th class="p-4 font-semibold"><fmt:message key="paciente.nombres"/></th>
                            <th class="p-4 font-semibold"><fmt:message key="paciente.eps"/></th>
                            <th class="p-4 font-semibold"><fmt:message key="paciente.telefono"/></th>
                            <th class="p-4 font-semibold"><fmt:message key="paciente.nacimiento"/></th>
                            <th class="p-4 font-semibold text-center"><fmt:message key="paciente.acciones"/></th>
                        </tr>
                    </thead>
                    <tbody class="text-sm">
                        <c:forEach var="paciente" items="${pacientes}">
                            <tr class="border-b border-gray-50 hover:bg-gray-50 transition-colors">
                                <td class="p-4 font-mono text-gray-700">${paciente.documento}</td>
                                <td class="p-4">
                                    <div class="font-medium text-gray-800">${paciente.nombres} ${paciente.apellidos}</div>
                                    <div class="text-xs text-gray-400">${paciente.email}</div>
                                </td>
                                <td class="p-4 text-gray-600">
                                    <span class="inline-flex items-center gap-1 bg-blue-50 text-azul-salud py-1 px-2 rounded text-xs font-medium">
                                        <i class="fa-solid fa-hospital text-[10px]"></i>
                                        ${paciente.eps}
                                    </span>
                                </td>
                                <td class="p-4 text-gray-600">
                                    <c:if test="${not empty paciente.telefono}">
                                        <span class="inline-flex items-center gap-1">
                                            <i class="fa-solid fa-phone text-gray-400 text-xs"></i>
                                            ${paciente.telefono}
                                        </span>
                                    </c:if>
                                </td>
                                <td class="p-4 text-gray-600">${paciente.fechaNacimiento}</td>
                                <td class="p-4 text-center">
                                    <div class="flex items-center justify-center gap-2">
                                        <c:if test="${hasPermissionEditar}">
                                            <a href="${pageContext.request.contextPath}/pacientes?accion=editar&id=${paciente.id}" 
                                               class="text-celeste hover:text-azul-salud p-1.5 rounded-lg hover:bg-blue-50 transition-colors"
                                               title="<fmt:message key='paciente.editar'/>">
                                                <i class="fa-solid fa-pen-to-square"></i>
                                            </a>
                                        </c:if>
                                        
                                        <c:if test="${hasPermissionEliminar}">
                                            <button onclick="confirmarEliminar(${paciente.id}, '${paciente.nombres} ${paciente.apellidos}')" 
                                                    class="text-red-500 hover:text-red-700 p-1.5 rounded-lg hover:bg-red-50 transition-colors"
                                                    title="<fmt:message key='paciente.eliminar'/>">
                                                <i class="fa-solid fa-trash-can"></i>
                                            </button>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty pacientes}">
                            <tr>
                                <td colspan="6" class="p-8 text-center text-gray-400">
                                    <i class="fa-solid fa-folder-open text-4xl mb-3 block"></i>
                                    <fmt:message key="paciente.no.registrados"/>
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
        
    </main>
    
    <%@ include file="/WEB-INF/views/templates/footer.jsp" %>

    <%-- Modal de confirmación para eliminar --%>
    <script>
        function confirmarEliminar(id, nombre) {
            if (confirm('<fmt:message key="paciente.confirmar"/>\\n\\n' + nombre)) {
                window.location.href = '${pageContext.request.contextPath}/pacientes?accion=eliminar&id=' + id;
            }
        }
    </script>

</body>
</html>