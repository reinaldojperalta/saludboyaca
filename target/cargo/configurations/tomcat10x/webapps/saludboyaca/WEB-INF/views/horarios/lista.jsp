<%-- 
    horarios/lista.jsp — Lista de horarios de atención médica
    Ubicación: WEB-INF/views/horarios/lista.jsp
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
    <title><fmt:message key="horario.titulo"/> — SaludBoyacá</title>
    
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
        .dia-badge {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 36px;
            height: 36px;
            border-radius: 50%;
            font-weight: 700;
            font-size: 0.875rem;
        }
    </style>
</head>
<body class="bg-gris-hielo font-sans min-h-screen flex flex-col">

    <%@ include file="/WEB-INF/views/templates/header.jsp" %>
    
    <main class="flex-grow max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8 w-full">
        
        <%-- Header --%>
        <div class="mb-8 flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4">
            <div>
                <h1 class="text-3xl font-bold text-azul-salud">
                    <i class="fa-solid fa-clock mr-2"></i>
                    <fmt:message key="horario.titulo"/>
                </h1>
                <p class="text-gray-600 mt-1">
                    <c:choose>
                        <c:when test="${rolUsuario == 'MEDICO'}">
                            <fmt:message key="horario.mis.horarios"/>
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="horario.todos"/>
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
            
            <c:if test="${hasPermissionEditar}">
                <a href="${pageContext.request.contextPath}/horarios?accion=nuevo" 
                   class="inline-flex items-center justify-center px-4 py-2 bg-verde-sena hover:bg-opacity-90 text-white text-sm font-semibold rounded-lg transition-colors shadow-sm">
                    <i class="fa-solid fa-plus mr-2"></i>
                    Nuevo Horario
                </a>
            </c:if>
        </div>

        <%-- Mensaje de error --%>
        <c:if test="${not empty error}">
            <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6 flex items-center">
                <i class="fa-solid fa-circle-exclamation mr-2"></i>
                ${error}
            </div>
        </c:if>

        <%-- Tabla de horarios --%>
        <div class="bg-white rounded-xl shadow-sm overflow-hidden">
            <div class="overflow-x-auto">
                <table class="w-full text-left border-collapse">
                    <thead class="bg-gray-50 text-gray-600 text-sm">
                        <tr>
                            <c:if test="${rolUsuario != 'MEDICO'}">
                                <th class="p-4 font-semibold"><fmt:message key="horario.medico"/></th>
                            </c:if>
                            <th class="p-4 font-semibold"><fmt:message key="horario.dia"/></th>
                            <th class="p-4 font-semibold"><fmt:message key="horario.hora.inicio"/></th>
                            <th class="p-4 font-semibold"><fmt:message key="horario.hora.fin"/></th>
                            <th class="p-4 font-semibold"><fmt:message key="horario.duracion"/></th>
                            <th class="p-4 font-semibold text-center"><fmt:message key="horario.max.citas"/></th>
                            <th class="p-4 font-semibold text-center"><fmt:message key="horario.estado"/></th>
                            <c:if test="${hasPermissionEditar || hasPermissionEliminar}">
                                <th class="p-4 font-semibold text-center">Acciones</th>
                            </c:if>
                        </tr>
                    </thead>
                    <tbody class="text-sm">
                        <c:forEach var="horario" items="${horarios}">
                            <tr class="border-b border-gray-50 hover:bg-gray-50 transition-colors">
                                <%-- Médico (solo para admin/recepcionista) --%>
                                <c:if test="${rolUsuario != 'MEDICO'}">
                                    <td class="p-4">
                                        <div class="flex items-center gap-3">
                                            <div class="w-8 h-8 bg-azul-salud bg-opacity-10 rounded-full flex items-center justify-center text-azul-salud">
                                                <i class="fa-solid fa-user-doctor text-sm"></i>
                                            </div>
                                            <span class="font-medium text-gray-800">
                                                <c:forEach var="medico" items="${medicos}">
                                                    <c:if test="${medico.id == horario.idMedico}">
                                                        ${medico.nombres} ${medico.apellidos}
                                                    </c:if>
                                                </c:forEach>
                                            </span>
                                        </div>
                                    </td>
                                </c:if>
                                
                                <%-- Día --%>
                                <td class="p-4">
                                    <div class="flex items-center gap-3">
                                        <div class="dia-badge 
                                            ${horario.diaSemana == 1 ? 'bg-red-100 text-red-700' : ''}
                                            ${horario.diaSemana == 2 ? 'bg-orange-100 text-orange-700' : ''}
                                            ${horario.diaSemana == 3 ? 'bg-yellow-100 text-yellow-700' : ''}
                                            ${horario.diaSemana == 4 ? 'bg-green-100 text-green-700' : ''}
                                            ${horario.diaSemana == 5 ? 'bg-blue-100 text-blue-700' : ''}">
                                            ${horario.diaSemana}
                                        </div>
                                        <span class="font-medium text-gray-800">${horario.nombreDia}</span>
                                    </div>
                                </td>
                                
                                <%-- Hora inicio --%>
                                <td class="p-4 text-gray-600 font-mono">${horario.horaInicio}</td>
                                
                                <%-- Hora fin --%>
                                <td class="p-4 text-gray-600 font-mono">${horario.horaFin}</td>
                                
                                <%-- Duración --%>
                                <td class="p-4 text-gray-600">
                                    <span class="inline-flex items-center gap-1 bg-gray-100 text-gray-700 py-1 px-2 rounded text-xs">
                                        <i class="fa-solid fa-hourglass-half text-[10px]"></i>
                                        ${horario.duracionMinutos} min
                                    </span>
                                </td>
                                
                                <%-- Max citas --%>
                                <td class="p-4 text-center">
                                    <span class="inline-flex items-center justify-center w-8 h-8 bg-verde-sena bg-opacity-10 text-verde-sena rounded-full font-bold text-sm">
                                        ${horario.maxCitas}
                                    </span>
                                </td>
                                
                                <%-- Estado (activo si hay duración > 0) --%>
                                <td class="p-4 text-center">
                                    <c:choose>
                                        <c:when test="${horario.duracionMinutos > 0}">
                                            <span class="inline-flex items-center gap-1.5 bg-green-50 text-verde-sena py-1.5 px-3 rounded-full text-xs font-bold border border-green-200">
                                                <i class="fa-solid fa-circle text-[8px]"></i>
                                                <fmt:message key="horario.activo"/>
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="inline-flex items-center gap-1.5 bg-red-50 text-red-600 py-1.5 px-3 rounded-full text-xs font-bold border border-red-200">
                                                <i class="fa-solid fa-circle text-[8px]"></i>
                                                <fmt:message key="horario.inactivo"/>
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                
                                <%-- Acciones --%>
                                <c:if test="${hasPermissionEditar || hasPermissionEliminar}">
                                    <td class="p-4">
                                        <div class="flex items-center justify-center gap-2">
                                            <c:if test="${hasPermissionEditar}">
                                                <a href="${pageContext.request.contextPath}/horarios?accion=editar&id=${horario.id}" 
                                                   class="text-celeste hover:text-azul-salud p-1.5 rounded-lg hover:bg-blue-50 transition-colors"
                                                   title="Editar">
                                                    <i class="fa-solid fa-pen-to-square"></i>
                                                </a>
                                            </c:if>
                                            
                                            <c:if test="${hasPermissionEliminar}">
                                                <button onclick="confirmarEliminar('${horario.id}')" 
                                                        class="text-red-500 hover:text-red-700 p-1.5 rounded-lg hover:bg-red-50 transition-colors"
                                                        title="Eliminar">
                                                    <i class="fa-solid fa-trash-can"></i>
                                                </button>
                                            </c:if>
                                        </div>
                                    </td>
                                </c:if>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty horarios}">
                            <tr>
                                <td colspan="${rolUsuario == 'MEDICO' ? 6 : 7}" class="p-8 text-center text-gray-400">
                                    <i class="fa-solid fa-calendar-xmark text-4xl mb-3 block"></i>
                                    <fmt:message key="horario.no.registrados"/>
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
        
        <%-- Leyenda --%>
        <div class="mt-6 bg-white rounded-xl shadow-sm p-6">
            <h3 class="text-sm font-semibold text-gray-700 mb-3">
                <i class="fa-solid fa-circle-info mr-1 text-celeste"></i>
                <fmt:message key="horario.leyenda"/>
            </h3>
            <div class="flex flex-wrap gap-4 text-sm">
                <div class="flex items-center gap-2">
                    <div class="dia-badge bg-red-100 text-red-700">1</div>
                    <span class="text-gray-600"><fmt:message key="dia.lunes"/></span>
                </div>
                <div class="flex items-center gap-2">
                    <div class="dia-badge bg-orange-100 text-orange-700">2</div>
                    <span class="text-gray-600"><fmt:message key="dia.martes"/></span>
                </div>
                <div class="flex items-center gap-2">
                    <div class="dia-badge bg-yellow-100 text-yellow-700">3</div>
                    <span class="text-gray-600"><fmt:message key="dia.miercoles"/></span>
                </div>
                <div class="flex items-center gap-2">
                    <div class="dia-badge bg-green-100 text-green-700">4</div>
                    <span class="text-gray-600"><fmt:message key="dia.jueves"/></span>
                </div>
                <div class="flex items-center gap-2">
                    <div class="dia-badge bg-blue-100 text-blue-700">5</div>
                    <span class="text-gray-600"><fmt:message key="dia.viernes"/></span>
                </div>
            </div>
        </div>
        
    </main>
    
    <%@ include file="/WEB-INF/views/templates/footer.jsp" %>
    
    <script>
        function confirmarEliminar(id) {
            if (confirm('¿Está seguro de que desea eliminar este horario?')) {
                window.location.href = '${pageContext.request.contextPath}/horarios?accion=eliminar&id=' + id;
            }
        }
    </script>

</body>
</html>