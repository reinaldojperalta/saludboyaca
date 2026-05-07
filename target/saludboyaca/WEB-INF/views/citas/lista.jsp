<%-- 
    citas/lista.jsp — Lista de citas médicas
    Ubicación: WEB-INF/views/citas/lista.jsp
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
    <title><fmt:message key="cita.lista.titulo"/> — SaludBoyacá</title>
    
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
</head>
<body class="bg-gris-hielo font-sans min-h-screen flex flex-col">

    <%@ include file="/WEB-INF/views/templates/header.jsp" %>
    
    <main class="flex-grow max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8 w-full">
        
        <%-- Header --%>
        <div class="mb-8 flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4">
            <div>
                <h1 class="text-3xl font-bold text-azul-salud">
                    <fmt:message key="cita.lista.titulo"/>
                </h1>
                <p class="text-gray-600 mt-1">
                    <fmt:message key="cita.lista.subtitulo"/>
                </p>
            </div>
            
            <c:if test="${hasPermissionCita || pCita}">
                <a href="${pageContext.request.contextPath}/citas?accion=nueva" 
                   class="inline-flex items-center px-4 py-2 bg-verde-sena hover:bg-opacity-90 text-white font-semibold rounded-lg transition-colors shadow-sm">
                    <i class="fa-solid fa-plus mr-2"></i>
                    <fmt:message key="cita.nueva"/>
                </a>
            </c:if>
        </div>

        <%-- Mensaje de error --%>
        <c:if test="${not empty error}">
            <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
                <i class="fa-solid fa-circle-exclamation mr-2"></i>
                ${error}
            </div>
        </c:if>

        <%-- Tabla de citas --%>
        <div class="bg-white rounded-xl shadow-sm overflow-hidden">
            <div class="overflow-x-auto">
                <table class="w-full text-left border-collapse">
                    <thead class="bg-gray-50 text-gray-600 text-sm">
                        <tr>
                            <th class="p-4 font-semibold"><fmt:message key="cita.paciente"/></th>
                            <th class="p-4 font-semibold"><fmt:message key="cita.medico"/></th>
                            <th class="p-4 font-semibold"><fmt:message key="cita.especialidad"/></th>
                            <th class="p-4 font-semibold"><fmt:message key="cita.fecha"/></th>
                            <th class="p-4 font-semibold text-center"><fmt:message key="cita.estado"/></th>
                            <th class="p-4 font-semibold text-center"><fmt:message key="cita.acciones"/></th>
                        </tr>
                    </thead>
                    <tbody class="text-sm">
                        <c:forEach var="cita" items="${citas}">
                            <tr class="border-b border-gray-50 hover:bg-gray-50 transition-colors">
                                <td class="p-4 font-medium text-gray-800">${cita.nombrePaciente}</td>
                                <td class="p-4 text-gray-600">${cita.nombreMedico}</td>
                                <td class="p-4 text-gray-600">${cita.nombreEspecialidad}</td>
                                <td class="p-4 text-gray-600">
                                    ${cita.fechaCita} <span class="text-gray-400">|</span> ${cita.horaCita}
                                </td>
                                <td class="p-4 text-center">
                                    <c:choose>
                                        <c:when test="${cita.estado.name() == 'PROGRAMADA'}">
                                            <span class="inline-flex items-center gap-1.5 bg-yellow-50 text-yellow-700 py-1.5 px-3 rounded-full text-xs font-bold border border-yellow-200">
                                                <i class="fa-solid fa-circle text-[8px]"></i>
                                                <fmt:message key="estado.programada"/>
                                            </span>
                                        </c:when>
                                        <c:when test="${cita.estado.name() == 'CONFIRMADA'}">
                                            <span class="inline-flex items-center gap-1.5 bg-green-50 text-verde-sena py-1.5 px-3 rounded-full text-xs font-bold border border-green-200">
                                                <i class="fa-solid fa-circle text-[8px]"></i>
                                                <fmt:message key="estado.confirmada"/>
                                            </span>
                                        </c:when>
                                        <c:when test="${cita.estado.name() == 'ATENDIDA'}">
                                            <span class="inline-flex items-center gap-1.5 bg-blue-50 text-celeste py-1.5 px-3 rounded-full text-xs font-bold border border-blue-200">
                                                <i class="fa-solid fa-circle text-[8px]"></i>
                                                <fmt:message key="estado.atendida"/>
                                            </span>
                                        </c:when>
                                        <c:when test="${cita.estado.name() == 'CANCELADA'}">
                                            <span class="inline-flex items-center gap-1.5 bg-red-50 text-red-600 py-1.5 px-3 rounded-full text-xs font-bold border border-red-200">
                                                <i class="fa-solid fa-circle text-[8px]"></i>
                                                <fmt:message key="estado.cancelada"/>
                                            </span>
                                        </c:when>
                                    </c:choose>
                                </td>
                                <td class="p-4 text-center">
                                    <div class="flex items-center justify-center gap-2">
                                        <a href="${pageContext.request.contextPath}/citas?accion=detalle&id=${cita.id}" 
                                           class="text-celeste hover:text-azul-salud p-1.5 rounded-lg hover:bg-blue-50 transition-colors"
                                           title="<fmt:message key='cita.ver'/>">
                                            <i class="fa-solid fa-eye"></i>
                                        </a>
                                        
                                        <c:if test="${hasPermissionCita || pCita}">
                                            <a href="${pageContext.request.contextPath}/citas?accion=cambiar-estado&id=${cita.id}" 
                                               class="text-yellow-600 hover:text-yellow-800 p-1.5 rounded-lg hover:bg-yellow-50 transition-colors"
                                               title="<fmt:message key='cita.cambiar.estado'/>">
                                                <i class="fa-solid fa-rotate"></i>
                                            </a>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty citas}">
                            <tr>
                                <td colspan="6" class="p-8 text-center text-gray-400">
                                    <i class="fa-solid fa-folder-open text-4xl mb-3 block"></i>
                                    <fmt:message key="cita.no.registradas"/>
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
        
    </main>
    
    <%@ include file="/WEB-INF/views/templates/footer.jsp" %>

</body>
</html>