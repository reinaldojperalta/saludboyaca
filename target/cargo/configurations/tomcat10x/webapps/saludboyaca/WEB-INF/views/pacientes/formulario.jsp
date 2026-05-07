<%-- 
    pacientes/formulario.jsp — Formulario de paciente (crear/editar)
    Ubicación: WEB-INF/views/pacientes/formulario.jsp
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
    <title>
        <c:choose>
            <c:when test="${modoEdicion}"><fmt:message key="paciente.editar"/></c:when>
            <c:otherwise><fmt:message key="paciente.nuevo"/></c:otherwise>
        </c:choose>
        — SaludBoyacá
    </title>
    
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
    
    <main class="flex-grow max-w-3xl mx-auto py-8 px-4 sm:px-6 lg:px-8 w-full">
        
        <%-- Header --%>
        <div class="mb-8">
            <a href="${pageContext.request.contextPath}/pacientes?accion=listar" 
               class="text-celeste hover:text-azul-salud text-sm font-medium mb-2 inline-flex items-center transition-colors">
                <i class="fa-solid fa-arrow-left mr-1"></i>
                <fmt:message key="paciente.volver"/>
            </a>
            <h1 class="text-3xl font-bold text-azul-salud mt-2">
                <c:choose>
                    <c:when test="${modoEdicion}">
                        <i class="fa-solid fa-user-pen mr-2"></i>
                        <fmt:message key="paciente.editar"/>
                    </c:when>
                    <c:otherwise>
                        <i class="fa-solid fa-user-plus mr-2"></i>
                        <fmt:message key="paciente.nuevo"/>
                    </c:otherwise>
                </c:choose>
            </h1>
        </div>

        <%-- Mensaje de error --%>
        <c:if test="${not empty error}">
            <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6 flex items-center">
                <i class="fa-solid fa-circle-exclamation mr-2"></i>
                ${error}
            </div>
        </c:if>

        <%-- Formulario --%>
        <div class="bg-white rounded-xl shadow-sm p-6 sm:p-8">
            <form action="${pageContext.request.contextPath}/pacientes" method="post" class="space-y-6">
                
                <input type="hidden" name="accion" value="${modoEdicion ? 'actualizar' : 'crear'}">
                <c:if test="${modoEdicion}">
                    <input type="hidden" name="id" value="${paciente.id}">
                </c:if>

                <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
                    
                    <%-- Nombres --%>
                    <div>
                        <label for="nombres" class="block text-sm font-semibold text-gray-700 mb-2">
                            <fmt:message key="paciente.nombres"/>
                            <span class="text-red-500">*</span>
                        </label>
                        <input type="text" id="nombres" name="nombres" required
                               value="${paciente.nombres}"
                               class="w-full px-4 py-2.5 border-2 border-gray-200 rounded-lg focus:border-celeste focus:outline-none focus:ring-2 focus:ring-celeste/20 transition-colors bg-gray-50 focus:bg-white"
                               placeholder="Ej: Carlos Andrés">
                    </div>

                    <%-- Apellidos --%>
                    <div>
                        <label for="apellidos" class="block text-sm font-semibold text-gray-700 mb-2">
                            <fmt:message key="paciente.apellidos"/>
                            <span class="text-red-500">*</span>
                        </label>
                        <input type="text" id="apellidos" name="apellidos" required
                               value="${paciente.apellidos}"
                               class="w-full px-4 py-2.5 border-2 border-gray-200 rounded-lg focus:border-celeste focus:outline-none focus:ring-2 focus:ring-celeste/20 transition-colors bg-gray-50 focus:bg-white"
                               placeholder="Ej: Gómez Rodríguez">
                    </div>

                    <%-- Documento --%>
                    <div>
                        <label for="documento" class="block text-sm font-semibold text-gray-700 mb-2">
                            <fmt:message key="paciente.documento"/>
                            <span class="text-red-500">*</span>
                        </label>
                        <input type="text" id="documento" name="documento" required
                               value="${paciente.documento}"
                               ${modoEdicion ? 'readonly' : ''}
                               class="w-full px-4 py-2.5 border-2 border-gray-200 rounded-lg focus:border-celeste focus:outline-none focus:ring-2 focus:ring-celeste/20 transition-colors bg-gray-50 focus:bg-white ${modoEdicion ? 'opacity-60 cursor-not-allowed' : ''}"
                               placeholder="Ej: 1234567890">
                        <c:if test="${modoEdicion}">
                            <p class="text-xs text-gray-400 mt-1"><fmt:message key="paciente.documento.noeditable"/></p>
                        </c:if>
                    </div>

                    <%-- Fecha de Nacimiento --%>
                    <div>
                        <label for="fechaNacimiento" class="block text-sm font-semibold text-gray-700 mb-2">
                            <fmt:message key="paciente.nacimiento"/>
                            <span class="text-red-500">*</span>
                        </label>
                        <input type="date" id="fechaNacimiento" name="fechaNacimiento" required
                               value="${paciente.fechaNacimiento}"
                               class="w-full px-4 py-2.5 border-2 border-gray-200 rounded-lg focus:border-celeste focus:outline-none focus:ring-2 focus:ring-celeste/20 transition-colors bg-gray-50 focus:bg-white">
                    </div>

                    <%-- Teléfono --%>
                    <div>
                        <label for="telefono" class="block text-sm font-semibold text-gray-700 mb-2">
                            <fmt:message key="paciente.telefono"/>
                        </label>
                        <div class="relative">
                            <span class="absolute left-3 top-2.5 text-gray-400">
                                <i class="fa-solid fa-phone"></i>
                            </span>
                            <input type="tel" id="telefono" name="telefono"
                                   value="${paciente.telefono}"
                                   class="w-full pl-10 pr-4 py-2.5 border-2 border-gray-200 rounded-lg focus:border-celeste focus:outline-none focus:ring-2 focus:ring-celeste/20 transition-colors bg-gray-50 focus:bg-white"
                                   placeholder="Ej: 3101234567">
                        </div>
                    </div>

                    <%-- Email --%>
                    <div>
                        <label for="email" class="block text-sm font-semibold text-gray-700 mb-2">
                            <fmt:message key="paciente.email"/>
                        </label>
                        <div class="relative">
                            <span class="absolute left-3 top-2.5 text-gray-400">
                                <i class="fa-solid fa-envelope"></i>
                            </span>
                            <input type="email" id="email" name="email"
                                   value="${paciente.email}"
                                   class="w-full pl-10 pr-4 py-2.5 border-2 border-gray-200 rounded-lg focus:border-celeste focus:outline-none focus:ring-2 focus:ring-celeste/20 transition-colors bg-gray-50 focus:bg-white"
                                   placeholder="Ej: paciente@email.com">
                        </div>
                    </div>

                    <%-- EPS --%>
                    <div class="sm:col-span-2">
                        <label for="eps" class="block text-sm font-semibold text-gray-700 mb-2">
                            <fmt:message key="paciente.eps"/>
                            <span class="text-red-500">*</span>
                        </label>
                        <div class="relative">
                            <span class="absolute left-3 top-2.5 text-gray-400">
                                <i class="fa-solid fa-hospital"></i>
                            </span>
                            <input type="text" id="eps" name="eps" required
                                   value="${paciente.eps}"
                                   class="w-full pl-10 pr-4 py-2.5 border-2 border-gray-200 rounded-lg focus:border-celeste focus:outline-none focus:ring-2 focus:ring-celeste/20 transition-colors bg-gray-50 focus:bg-white"
                                   placeholder="Ej: EPS Sura, Nueva EPS, Sanitas, etc.">
                        </div>
                    </div>

                    <%-- Vereda/Barrio --%>
                    <div class="sm:col-span-2">
                        <label for="veredaBarrio" class="block text-sm font-semibold text-gray-700 mb-2">
                            <fmt:message key="paciente.vereda"/>
                        </label>
                        <div class="relative">
                            <span class="absolute left-3 top-2.5 text-gray-400">
                                <i class="fa-solid fa-location-dot"></i>
                            </span>
                            <input type="text" id="veredaBarrio" name="veredaBarrio"
                                   value="${paciente.veredaBarrio}"
                                   class="w-full pl-10 pr-4 py-2.5 border-2 border-gray-200 rounded-lg focus:border-celeste focus:outline-none focus:ring-2 focus:ring-celeste/20 transition-colors bg-gray-50 focus:bg-white"
                                   placeholder="Ej: Vereda El Carmen, Barrio Centro">
                        </div>
                    </div>

                </div>

                <%-- Botones --%>
                <div class="flex items-center justify-end gap-4 pt-4 border-t border-gray-100">
                    <a href="${pageContext.request.contextPath}/pacientes?accion=listar" 
                       class="px-6 py-2.5 border-2 border-gray-200 text-gray-600 rounded-lg hover:bg-gray-50 font-medium transition-colors">
                        <fmt:message key="paciente.cancelar"/>
                    </a>
                    <button type="submit" 
                            class="px-6 py-2.5 bg-verde-sena hover:bg-opacity-90 text-white font-semibold rounded-lg transition-colors shadow-sm flex items-center">
                        <i class="fa-solid fa-save mr-2"></i>
                        <fmt:message key="paciente.guardar"/>
                    </button>
                </div>

            </form>
        </div>
        
    </main>
    
    <%@ include file="/WEB-INF/views/templates/footer.jsp" %>

</body>
</html>