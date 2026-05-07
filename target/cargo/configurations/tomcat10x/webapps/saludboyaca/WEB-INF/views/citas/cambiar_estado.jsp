<%-- 
    citas/cambiar_estado.jsp — Cambiar estado de una cita médica
    Ubicación: WEB-INF/views/citas/cambiar_estado.jsp
    Acceso: /citas?accion=cambiar-estado&id=X
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
    <title><fmt:message key="cita.cambiar.estado"/> — SaludBoyacá</title>
    
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

    <main class="flex-grow max-w-3xl mx-auto py-8 px-4 w-full">
        
        <%-- Título --%>
        <div class="mb-6">
            <h1 class="text-2xl font-bold text-azul-salud">
                <i class="fa-solid fa-arrows-rotate mr-2"></i>
                <fmt:message key="cita.cambiar.estado"/>
            </h1>
            <p class="text-gray-500 mt-1 text-sm">
                <fmt:message key="cita.cambiar.estado.subtitulo"/>
            </p>
        </div>

        <%-- Card de la cita actual --%>
        <div class="bg-white rounded-xl shadow-sm p-6 mb-6 border-l-4 border-celeste">
            <div class="flex items-start justify-between mb-4">
                <div>
                    <h3 class="font-bold text-lg text-gray-800">${cita.nombrePaciente}</h3>
                    <p class="text-sm text-gray-500">
                        <i class="fa-solid fa-user-doctor mr-1"></i> ${cita.nombreMedico}
                    </p>
                </div>
                <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold border
                    <c:choose>
                        <c:when test="${cita.estado.name() == 'PROGRAMADA'}">bg-yellow-50 text-yellow-700 border-yellow-200</c:when>
                        <c:when test="${cita.estado.name() == 'CONFIRMADA'}">bg-green-50 text-verde-sena border-green-200</c:when>
                        <c:when test="${cita.estado.name() == 'ATENDIDA'}">bg-blue-50 text-celeste border-blue-200</c:when>
                        <c:when test="${cita.estado.name() == 'CANCELADA'}">bg-red-50 text-red-600 border-red-200</c:when>
                    </c:choose>">
                    <i class="fa-solid fa-circle text-[8px]"></i>
                    ${cita.estado}
                </span>
            </div>
            
            <div class="grid grid-cols-2 gap-4 text-sm text-gray-600">
                <div>
                    <span class="text-gray-400 text-xs uppercase tracking-wider"><fmt:message key="cita.especialidad"/></span>
                    <p class="font-medium">${cita.nombreEspecialidad}</p>
                </div>
                <div>
                    <span class="text-gray-400 text-xs uppercase tracking-wider"><fmt:message key="cita.fecha"/></span>
                    <p class="font-medium">${cita.fechaCita} | ${cita.horaCita}</p>
                </div>
                <div class="col-span-2">
                    <span class="text-gray-400 text-xs uppercase tracking-wider"><fmt:message key="cita.motivo"/></span>
                    <p class="font-medium">${cita.motivo != null ? cita.motivo : '-'}</p>
                </div>
            </div>
        </div>

        <%-- Formulario de cambio de estado --%>
        <div class="bg-white rounded-xl shadow-sm p-6">
            <h2 class="text-lg font-semibold text-gray-800 mb-4">
                <fmt:message key="cita.nuevo.estado"/>
            </h2>

            <form action="${pageContext.request.contextPath}/citas" method="POST" class="space-y-6">
                <input type="hidden" name="accion" value="cambiar-estado">
                <input type="hidden" name="id" value="${cita.id}">

                <%-- Select de estado --%>
                <div>
                    <label for="estado" class="block text-sm font-semibold text-gray-700 mb-2">
                        <fmt:message key="cita.estado"/>
                    </label>
                    <select name="estado" id="estado" required
                            class="w-full px-4 py-3 border-2 border-gray-200 rounded-lg focus:border-celeste focus:ring-2 focus:ring-celeste/20 outline-none transition-all bg-white">
                        
                        <%-- Opciones según estado actual --%>
                        <c:choose>
                            <c:when test="${cita.estado.name() == 'PROGRAMADA'}">
                                <option value="CONFIRMADA">
                                    <fmt:message key="estado.confirmada"/> — <fmt:message key="estado.confirmar.cita"/>
                                </option>
                                <option value="CANCELADA" class="text-red-600">
                                    <fmt:message key="estado.cancelada"/> — <fmt:message key="estado.cancelar.cita"/>
                                </option>
                            </c:when>
                            <c:when test="${cita.estado.name() == 'CONFIRMADA'}">
                                <option value="ATENDIDA">
                                    <fmt:message key="estado.atendida"/> — <fmt:message key="estado.marcar.atendida"/>
                                </option>
                                <option value="CANCELADA" class="text-red-600">
                                    <fmt:message key="estado.cancelada"/> — <fmt:message key="estado.cancelar.cita"/>
                                </option>
                            </c:when>
                            <c:otherwise>
                                <option disabled selected>
                                    <fmt:message key="cita.no.cambios.disponibles"/>
                                </option>
                            </c:otherwise>
                        </c:choose>
                    </select>
                </div>

                <%-- Observaciones --%>
                <div>
                    <label for="observaciones" class="block text-sm font-semibold text-gray-700 mb-2">
                        <fmt:message key="cita.observaciones"/> 
                        <span class="text-gray-400 font-normal">(<fmt:message key="form.opcional"/>)</span>
                    </label>
                    <textarea name="observaciones" id="observaciones" rows="3" maxlength="500"
                              class="w-full px-4 py-3 border-2 border-gray-200 rounded-lg focus:border-celeste focus:ring-2 focus:ring-celeste/20 outline-none transition-all resize-none"
                              placeholder="<fmt:message key='cita.observaciones.placeholder'/>"></textarea>
                </div>

                <%-- Botones --%>
                <div class="flex flex-col sm:flex-row gap-3 pt-2">
                    <button type="submit" 
                            class="flex-1 bg-azul-salud hover:bg-opacity-90 text-white font-bold py-3 px-6 rounded-lg transition-all flex items-center justify-center gap-2">
                        <i class="fa-solid fa-check"></i>
                        <fmt:message key="btn.guardar.cambios"/>
                    </button>
                    
                    <a href="${pageContext.request.contextPath}/citas?accion=listar" 
                       class="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 font-bold py-3 px-6 rounded-lg transition-all text-center flex items-center justify-center gap-2">
                        <i class="fa-solid fa-xmark"></i>
                        <fmt:message key="btn.cancelar"/>
                    </a>
                </div>
            </form>
        </div>

    </main>

    <%@ include file="/WEB-INF/views/templates/footer.jsp" %>

</body>
</html>