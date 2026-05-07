<%-- 
    citas/formulario.jsp — Crear/Editar cita médica
    Ubicación: WEB-INF/views/citas/formulario.jsp
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
    <title><fmt:message key="cita.form.titulo"/> — SaludBoyacá</title>
    
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
        <div class="mb-8">
            <h1 class="text-3xl font-bold text-azul-salud">
                <fmt:message key="cita.form.titulo"/>
            </h1>
            <p class="text-gray-600 mt-1">
                <fmt:message key="cita.form.subtitulo"/>
            </p>
        </div>

        <%-- Mensaje de error --%>
        <c:if test="${not empty error}">
            <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
                <i class="fa-solid fa-circle-exclamation mr-2"></i>
                ${error}
            </div>
        </c:if>

        <%-- Formulario --%>
        <form action="${pageContext.request.contextPath}/citas" method="POST" 
              class="bg-white rounded-xl shadow-sm p-6 sm:p-8">
            
            <input type="hidden" name="accion" value="${modoEdicion ? 'actualizar' : 'crear'}">
            <c:if test="${modoEdicion}">
                <input type="hidden" name="id" value="${cita.id}">
            </c:if>

            <%-- Paciente --%>
            <div class="mb-6">
                <label for="idPaciente" class="block text-sm font-semibold text-gray-700 mb-2">
                    <i class="fa-solid fa-user mr-1.5 text-azul-salud"></i>
                    <fmt:message key="cita.paciente"/>
                </label>
                <select id="idPaciente" name="idPaciente" required
                        class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-celeste focus:border-celeste outline-none transition-colors bg-gray-50">
                    <option value=""><fmt:message key="cita.seleccionar.paciente"/></option>
                    <c:forEach var="paciente" items="${pacientes}">
                        <option value="${paciente.id}" <c:if test="${cita.idPaciente == paciente.id}">selected</c:if>>
                            ${paciente.nombres} ${paciente.apellidos}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <%-- Médico --%>
            <div class="mb-6">
                <label for="idMedico" class="block text-sm font-semibold text-gray-700 mb-2">
                    <i class="fa-solid fa-user-doctor mr-1.5 text-azul-salud"></i>
                    <fmt:message key="cita.medico"/>
                </label>
                <select id="idMedico" name="idMedico" required
                        class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-celeste focus:border-celeste outline-none transition-colors bg-gray-50">
                    <option value=""><fmt:message key="cita.seleccionar.medico"/></option>
                    <c:forEach var="medico" items="${medicos}">
                        <option value="${medico.id}" <c:if test="${cita.idMedico == medico.id}">selected</c:if>>
                            ${medico.nombres} ${medico.apellidos}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <%-- Especialidad --%>
            <div class="mb-6">
                <label for="idEspecialidad" class="block text-sm font-semibold text-gray-700 mb-2">
                    <i class="fa-solid fa-stethoscope mr-1.5 text-azul-salud"></i>
                    <fmt:message key="cita.especialidad"/>
                </label>
                <select id="idEspecialidad" name="idEspecialidad" required
                        class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-celeste focus:border-celeste outline-none transition-colors bg-gray-50">
                    <option value=""><fmt:message key="cita.seleccionar.especialidad"/></option>
                    <c:forEach var="esp" items="${especialidades}">
                        <option value="${esp.id}" <c:if test="${cita.idEspecialidad == esp.id}">selected</c:if>>
                            ${esp.nombre}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <%-- Fecha y Hora --%>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-6 mb-6">
                <div>
                    <label for="fechaCita" class="block text-sm font-semibold text-gray-700 mb-2">
                        <i class="fa-solid fa-calendar mr-1.5 text-azul-salud"></i>
                        <fmt:message key="cita.fecha"/>
                    </label>
                    <input type="date" id="fechaCita" name="fechaCita" required value="${cita.fechaCita}"
                           class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-celeste focus:border-celeste outline-none transition-colors bg-gray-50">
                </div>
                <div>
                    <label for="horaCita" class="block text-sm font-semibold text-gray-700 mb-2">
                        <i class="fa-solid fa-clock mr-1.5 text-azul-salud"></i>
                        <fmt:message key="cita.hora"/>
                    </label>
                    <input type="time" id="horaCita" name="horaCita" required value="${cita.horaCita}"
                           class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-celeste focus:border-celeste outline-none transition-colors bg-gray-50">
                </div>
            </div>

            <%-- Motivo --%>
            <div class="mb-8">
                <label for="motivo" class="block text-sm font-semibold text-gray-700 mb-2">
                    <i class="fa-solid fa-file-medical mr-1.5 text-azul-salud"></i>
                    <fmt:message key="cita.motivo"/>
                </label>
                <textarea id="motivo" name="motivo" rows="3"
                          class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-celeste focus:border-celeste outline-none transition-colors bg-gray-50 resize-none"
                          placeholder="<fmt:message key='cita.motivo.placeholder'/>">${cita.motivo}</textarea>
            </div>

            <%-- Botones --%>
            <div class="flex flex-col sm:flex-row gap-3">
                <button type="submit" 
                        class="flex-1 px-6 py-3 bg-verde-sena hover:bg-opacity-90 text-white font-semibold rounded-lg transition-colors shadow-sm">
                    <i class="fa-solid fa-check mr-2"></i>
                    <fmt:message key="cita.guardar"/>
                </button>
                <a href="${pageContext.request.contextPath}/citas" 
                   class="flex-1 px-6 py-3 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold rounded-lg transition-colors text-center">
                    <i class="fa-solid fa-xmark mr-2"></i>
                    <fmt:message key="cita.cancelar"/>
                </a>
            </div>
        </form>
        
    </main>
    
    <%@ include file="/WEB-INF/views/templates/footer.jsp" %>

</body>
</html>