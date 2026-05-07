<%-- 
    citas/detalle.jsp — Detalle de una cita médica
    Ubicación: WEB-INF/views/citas/detalle.jsp
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
    <title><fmt:message key="cita.detalle.titulo"/> — SaludBoyacá</title>
    
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
                        'gris-hielo': '#EAF0F7'
                    }
                }
            }
        }
    </script>
</head>
<body class="bg-gris-hielo font-sans min-h-screen flex flex-col">

    <%@ include file="/WEB-INF/views/templates/header.jsp" %>
    
    <main class="flex-grow max-w-3xl mx-auto py-8 px-4 sm:px-6 lg:px-8 w-full">
        
        <%-- Header --%>
        <div class="mb-8 flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4">
            <div>
                <h1 class="text-3xl font-bold text-azul-salud">
                    <fmt:message key="cita.detalle.titulo"/>
                </h1>
                <p class="text-gray-600 mt-1">
                    <fmt:message key="cita.detalle.subtitulo"/>
                </p>
            </div>
            <a href="${pageContext.request.contextPath}/citas" 
               class="inline-flex items-center px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold rounded-lg transition-colors">
                <i class="fa-solid fa-arrow-left mr-2"></i>
                <fmt:message key="cita.volver"/>
            </a>
        </div>

        <%-- Tarjeta de detalle --%>
        <div class="bg-white rounded-xl shadow-sm overflow-hidden">
            <%-- Estado banner --%>
            <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center">
                <span class="text-sm text-gray-500">
                    <fmt:message key="cita.id"/>: <strong class="text-gray-800">#${cita.id}</strong>
                </span>
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
            </div>

            <%-- Detalles --%>
            <div class="p-6 grid grid-cols-1 sm:grid-cols-2 gap-6">
                <div>
                    <p class="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1">
                        <fmt:message key="cita.paciente"/>
                    </p>
                    <p class="text-lg font-semibold text-gray-800">${cita.nombrePaciente}</p>
                </div>
                <div>
                    <p class="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1">
                        <fmt:message key="cita.medico"/>
                    </p>
                    <p class="text-lg font-semibold text-gray-800">${cita.nombreMedico}</p>
                </div>
                <div>
                    <p class="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1">
                        <fmt:message key="cita.especialidad"/>
                    </p>
                    <p class="text-lg font-semibold text-gray-800">${cita.nombreEspecialidad}</p>
                </div>
                <div>
                    <p class="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-1">
                        <fmt:message key="cita.fecha"/>
                    </p>
                    <p class="text-lg font-semibold text-gray-800">
                        ${cita.fechaCita} <span class="text-gray-400">|</span> ${cita.horaCita}
                    </p>
                </div>
            </div>

            <%-- Motivo --%>
            <c:if test="${not empty cita.motivo}">
                <div class="px-6 pb-6">
                    <p class="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-2">
                        <fmt:message key="cita.motivo"/>
                    </p>
                    <div class="bg-gray-50 rounded-lg p-4 text-gray-700">
                        ${cita.motivo}
                    </div>
                </div>
            </c:if>

            <%-- Observaciones --%>
            <c:if test="${not empty cita.observaciones}">
                <div class="px-6 pb-6">
                    <p class="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-2">
                        <fmt:message key="cita.observaciones"/>
                    </p>
                    <div class="bg-gray-50 rounded-lg p-4 text-gray-700">
                        ${cita.observaciones}
                    </div>
                </div>
            </c:if>

            <%-- Acciones --%>
            <div class="px-6 py-4 bg-gray-50 border-t border-gray-100 flex gap-3">
                <c:if test="${hasPermissionCita || pCita}">
                    <a href="${pageContext.request.contextPath}/citas?accion=cambiar-estado&id=${cita.id}" 
                       class="inline-flex items-center px-4 py-2 bg-yellow-500 hover:bg-yellow-600 text-white font-semibold rounded-lg transition-colors">
                        <i class="fa-solid fa-rotate mr-2"></i>
                        <fmt:message key="cita.cambiar.estado"/>
                    </a>
                </c:if>
            </div>
        </div>
        
    </main>
    
    <%@ include file="/WEB-INF/views/templates/footer.jsp" %>

</body>
</html>