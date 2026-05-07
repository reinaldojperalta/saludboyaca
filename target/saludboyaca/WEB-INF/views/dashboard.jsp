
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
    <title>${pageTitle} — SaludBoyacá</title>
    
    <!-- Tailwind CSS CDN -->
    <script src="https://cdn.tailwindcss.com"></script>
    
    <!-- FontAwesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <!-- Custom CSS -->
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

    <%-- Header / Navbar --%>
    <%@ include file="/WEB-INF/views/templates/header.jsp" %>
    
    <%-- Contenido principal --%>
    <main class="flex-grow max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8 w-full">
        
        <%-- Header del Dashboard --%>
        <div class="mb-8">
            <h1 class="text-3xl font-bold text-azul-salud">
                <fmt:message key="dashboard.titulo"/>
            </h1>
            <p class="text-gray-600 mt-2">
                <fmt:message key="dashboard.bienvenido">
                    <fmt:param value="${sessionScope.usuarioNombre}"/>
                </fmt:message>
            </p>
        </div>

        <%-- Grid de 4 Métricas --%>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            
            <%-- Citas Hoy --%>
            <div class="metric-card bg-white rounded-xl p-6 shadow-sm border-l-4 border-celeste flex items-center">
                <div class="bg-blue-50 p-4 rounded-full mr-4 text-celeste">
                    <i class="fa-solid fa-calendar-day text-2xl"></i>
                </div>
                <div>
                    <p class="text-sm text-gray-500 font-semibold">
                        <fmt:message key="dashboard.citas.hoy"/>
                    </p>
                    <h3 class="text-3xl font-bold text-gray-800">${metricas.citasHoy}</h3>
                </div>
            </div>

            <%-- Pendientes --%>
            <div class="metric-card bg-white rounded-xl p-6 shadow-sm border-l-4 border-yellow-500 flex items-center">
                <div class="bg-yellow-50 p-4 rounded-full mr-4 text-yellow-500">
                    <i class="fa-solid fa-hourglass-half text-2xl"></i>
                </div>
                <div>
                    <p class="text-sm text-gray-500 font-semibold">
                        <fmt:message key="dashboard.pendientes"/>
                    </p>
                    <h3 class="text-3xl font-bold text-gray-800">${metricas.pendientes}</h3>
                </div>
            </div>

            <%-- Citas Mes --%>
            <div class="metric-card bg-white rounded-xl p-6 shadow-sm border-l-4 border-verde-sena flex items-center">
                <div class="bg-green-50 p-4 rounded-full mr-4 text-verde-sena">
                    <i class="fa-solid fa-calendar-days text-2xl"></i>
                </div>
                <div>
                    <p class="text-sm text-gray-500 font-semibold">
                        <fmt:message key="dashboard.citas.mes"/>
                    </p>
                    <h3 class="text-3xl font-bold text-gray-800">${metricas.citasMes}</h3>
                </div>
            </div>

            <%-- Total Pacientes --%>
            <div class="metric-card bg-white rounded-xl p-6 shadow-sm border-l-4 border-morado-seguridad flex items-center">
                <div class="bg-purple-50 p-4 rounded-full mr-4 text-morado-seguridad">
                    <i class="fa-solid fa-users text-2xl"></i>
                </div>
                <div>
                    <p class="text-sm text-gray-500 font-semibold">
                        <fmt:message key="dashboard.pacientes"/>
                    </p>
                    <h3 class="text-3xl font-bold text-gray-800">${metricas.totalPacientes}</h3>
                </div>
            </div>

        </div>

        <%-- Tabla de Citas Recientes --%>
        <div class="bg-white rounded-xl shadow-sm overflow-hidden">
            <div class="p-6 border-b border-gray-100 flex justify-between items-center">
                <h2 class="text-xl font-bold text-azul-salud">
                    <i class="fa-solid fa-list-check mr-2"></i>
                    <fmt:message key="dashboard.ultimas.citas"/>
                </h2>
                <a href="${pageContext.request.contextPath}/citas" 
                   class="text-celeste hover:text-azul-salud font-semibold text-sm transition-colors">
                    <fmt:message key="dashboard.ver.todas"/> &rarr;
                </a>
            </div>
            
            <div class="overflow-x-auto">
                <table class="w-full text-left border-collapse">
                    <thead class="bg-gray-50 text-gray-600 text-sm">
                        <tr>
                            <th class="p-4 font-semibold"><fmt:message key="cita.paciente"/></th>
                            <th class="p-4 font-semibold"><fmt:message key="cita.medico"/></th>
                            <th class="p-4 font-semibold"><fmt:message key="cita.especialidad"/></th>
                            <th class="p-4 font-semibold"><fmt:message key="cita.fecha"/></th>
                            <th class="p-4 font-semibold text-center"><fmt:message key="cita.estado"/></th>
                        </tr>
                    </thead>
                    <tbody class="text-sm">
                        <c:forEach var="cita" items="${ultimasCitas}">
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
                            </tr>
                        </c:forEach>
                        <c:if test="${empty ultimasCitas}">
                            <tr>
                                <td colspan="5" class="p-8 text-center text-gray-400">
                                    <i class="fa-solid fa-folder-open text-4xl mb-3 block"></i>
                                    <fmt:message key="dashboard.no.citas"/>
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
        
    </main>
    
    <%-- Footer --%>
    <%@ include file="/WEB-INF/views/templates/footer.jsp" %>

</body>