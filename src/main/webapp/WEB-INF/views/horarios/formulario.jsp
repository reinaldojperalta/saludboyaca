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
    <title>Horario — SaludBoyacá</title>
    
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
        
        <div class="mb-8">
            <h1 class="text-3xl font-bold text-azul-salud">
                ${modoEdicion ? 'Editar Horario' : 'Nuevo Horario'}
            </h1>
        </div>

        <c:if test="${not empty error}">
            <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
                <i class="fa-solid fa-circle-exclamation mr-2"></i>
                ${error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/horarios" method="POST" 
              class="bg-white rounded-xl shadow-sm p-6 sm:p-8">
            
            <input type="hidden" name="accion" value="${modoEdicion ? 'actualizar' : 'crear'}">
            <c:if test="${modoEdicion}">
                <input type="hidden" name="id" value="${horario.id}">
            </c:if>

            <div class="mb-6">
                <label for="idMedico" class="block text-sm font-semibold text-gray-700 mb-2">
                    <i class="fa-solid fa-user-doctor mr-1.5 text-azul-salud"></i>
                    Médico
                </label>
                <select id="idMedico" name="idMedico" required
                        class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-celeste focus:border-celeste outline-none transition-colors bg-gray-50">
                    <option value="">Seleccione Médico</option>
                    <c:forEach var="medico" items="${medicos}">
                        <option value="${medico.id}" <c:if test="${horario.idMedico == medico.id}">selected</c:if>>
                            ${medico.nombres} ${medico.apellidos}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="mb-6">
                <label for="diaSemana" class="block text-sm font-semibold text-gray-700 mb-2">
                    <i class="fa-solid fa-calendar-day mr-1.5 text-azul-salud"></i>
                    Día de la Semana
                </label>
                <select id="diaSemana" name="diaSemana" required
                        class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-celeste focus:border-celeste outline-none transition-colors bg-gray-50">
                    <option value="">Seleccione Día</option>
                    <option value="1" <c:if test="${horario.diaSemana == 1}">selected</c:if>>Lunes</option>
                    <option value="2" <c:if test="${horario.diaSemana == 2}">selected</c:if>>Martes</option>
                    <option value="3" <c:if test="${horario.diaSemana == 3}">selected</c:if>>Miércoles</option>
                    <option value="4" <c:if test="${horario.diaSemana == 4}">selected</c:if>>Jueves</option>
                    <option value="5" <c:if test="${horario.diaSemana == 5}">selected</c:if>>Viernes</option>
                    <option value="6" <c:if test="${horario.diaSemana == 6}">selected</c:if>>Sábado</option>
                    <option value="7" <c:if test="${horario.diaSemana == 7}">selected</c:if>>Domingo</option>
                </select>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-6 mb-6">
                <div>
                    <label for="horaInicio" class="block text-sm font-semibold text-gray-700 mb-2">
                        <i class="fa-solid fa-clock mr-1.5 text-azul-salud"></i>
                        Hora Inicio
                    </label>
                    <input type="time" id="horaInicio" name="horaInicio" required value="${horario.horaInicio}"
                           class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-celeste focus:border-celeste outline-none transition-colors bg-gray-50">
                </div>
                <div>
                    <label for="horaFin" class="block text-sm font-semibold text-gray-700 mb-2">
                        <i class="fa-solid fa-clock mr-1.5 text-azul-salud"></i>
                        Hora Fin
                    </label>
                    <input type="time" id="horaFin" name="horaFin" required value="${horario.horaFin}"
                           class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-celeste focus:border-celeste outline-none transition-colors bg-gray-50">
                </div>
            </div>

            <div class="mb-8">
                <label for="maxCitas" class="block text-sm font-semibold text-gray-700 mb-2">
                    <i class="fa-solid fa-users mr-1.5 text-azul-salud"></i>
                    Máximo de Citas
                </label>
                <input type="number" id="maxCitas" name="maxCitas" required min="1" max="100" value="${horario.maxCitas}"
                       class="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-celeste focus:border-celeste outline-none transition-colors bg-gray-50">
            </div>

            <div class="flex flex-col sm:flex-row gap-3">
                <button type="submit" 
                        class="flex-1 px-6 py-3 bg-verde-sena hover:bg-opacity-90 text-white font-semibold rounded-lg transition-colors shadow-sm">
                    <i class="fa-solid fa-check mr-2"></i>
                    Guardar
                </button>
                <a href="${pageContext.request.contextPath}/horarios" 
                   class="flex-1 px-6 py-3 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold rounded-lg transition-colors text-center">
                    <i class="fa-solid fa-xmark mr-2"></i>
                    Cancelar
                </a>
            </div>
        </form>
        
    </main>
    
    <%@ include file="/WEB-INF/views/templates/footer.jsp" %>

</body>
</html>
