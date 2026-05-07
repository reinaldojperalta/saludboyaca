<%-- 
    consulta.jsp — Consulta pública de citas médicas
    Ubicación: WEB-INF/views/consulta.jsp
    Acceso: PÚBLICO (sin autenticación)
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
    <title><fmt:message key="consulta.titulo"/> — SaludBoyacá</title>
    
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
        .consulta-card {
            background: linear-gradient(135deg, var(--azul-salud) 0%, var(--celeste) 100%);
        }
        .captcha-img {
            border-radius: 8px;
            border: 2px solid #E5E7EB;
            cursor: pointer;
        }
        .captcha-img:hover {
            opacity: 0.9;
        }
    </style>
</head>
<body class="bg-gris-hielo font-sans min-h-screen flex flex-col">

    <%-- Header simplificado (sin menú RBAC, solo logo) --%>
    <nav class="bg-azul-salud text-white p-4 shadow-md">
        <div class="max-w-7xl mx-auto px-4 flex items-center justify-between">
            <div class="flex items-center space-x-3">
                <div class="w-9 h-9 bg-verde-sena rounded-lg flex items-center justify-center">
                    <i class="fa-solid fa-heart-pulse text-lg"></i>
                </div>
                <span class="text-xl font-bold tracking-wider">SaludBoyacá</span>
            </div>
            
            <%-- Selector de idioma (público) --%>
            <div class="flex space-x-1 bg-black bg-opacity-20 rounded-lg p-1">
                <button onclick="cambiarIdioma('es')" 
                        class="px-2 py-1 rounded text-sm hover:bg-white hover:bg-opacity-20 transition-colors ${sessionScope.lang == 'es' || sessionScope.lang == null ? 'bg-white bg-opacity-30' : ''}" 
                        title="Español">🇨🇴</button>
                <button onclick="cambiarIdioma('en')" 
                        class="px-2 py-1 rounded text-sm hover:bg-white hover:bg-opacity-20 transition-colors ${sessionScope.lang == 'en' ? 'bg-white bg-opacity-30' : ''}" 
                        title="English">🇺🇸</button>
                <button onclick="cambiarIdioma('it')" 
                        class="px-2 py-1 rounded text-sm hover:bg-white hover:bg-opacity-20 transition-colors ${sessionScope.lang == 'it' ? 'bg-white bg-opacity-30' : ''}" 
                        title="Italiano">🇮🇹</button>
            </div>
        </div>
    </nav>
    
    <main class="flex-grow max-w-4xl mx-auto py-12 px-4 sm:px-6 lg:px-8 w-full">
        
        <%-- Header de consulta --%>
        <div class="text-center mb-10">
            <div class="w-16 h-16 bg-white rounded-full flex items-center justify-center mx-auto mb-4 shadow-lg">
                <i class="fa-solid fa-magnifying-glass text-2xl text-azul-salud"></i>
            </div>
            <h1 class="text-3xl font-bold text-azul-salud mb-2">
                <fmt:message key="consulta.titulo"/>
            </h1>
            <p class="text-gray-600 max-w-lg mx-auto">
                <fmt:message key="consulta.instruccion"/>
            </p>
        </div>

        <%-- Mensaje de error --%>
        <c:if test="${not empty error}">
            <div class="bg-red-50 border-l-4 border-red-500 text-red-700 px-4 py-4 rounded-lg mb-6 flex items-start">
                <i class="fa-solid fa-circle-exclamation mt-0.5 mr-3"></i>
                <span>${error}</span>
            </div>
        </c:if>

        <%-- Formulario de consulta --%>
        <div class="bg-white rounded-xl shadow-lg p-6 sm:p-8 mb-8">
            <form action="${pageContext.request.contextPath}/consulta" method="post" class="space-y-6">
                
                <%-- Documento --%>
                <div>
                    <label for="documento" class="block text-sm font-semibold text-gray-700 mb-2">
                        <i class="fa-solid fa-id-card mr-1 text-celeste"></i>
                        <fmt:message key="consulta.documento"/>
                        <span class="text-red-500">*</span>
                    </label>
                    <input type="text" 
                           id="documento" 
                           name="documento" 
                           required
                           maxlength="12"
                           pattern="\d{6,12}"
                           inputmode="numeric"
                           value="${documento}"
                           class="w-full px-4 py-3 border-2 border-gray-200 rounded-lg focus:border-celeste focus:outline-none focus:ring-2 focus:ring-celeste/20 transition-colors bg-gray-50 focus:bg-white text-lg"
                           placeholder="Ej: 1234567890">
                    <p class="text-xs text-gray-400 mt-1">
                        <fmt:message key="consulta.documento.ayuda"/>
                    </p>
                </div>

                <%-- CAPTCHA --%>
                <div>
                    <label class="block text-sm font-semibold text-gray-700 mb-2">
                        <i class="fa-solid fa-shield-halved mr-1 text-morado-seguridad"></i>
                        <fmt:message key="consulta.captcha"/>
                        <span class="text-red-500">*</span>
                    </label>
                    
                    <div class="flex flex-col sm:flex-row items-start sm:items-center gap-4">
                        <%-- Imagen CAPTCHA --%>
                        <div class="flex-shrink-0">
                            <img src="${captchaImage}" 
                                 alt="CAPTCHA" 
                                 class="captcha-img h-16 w-auto"
                                 onclick="recargarCaptcha()"
                                 title="<fmt:message key='consulta.captcha.recargar'/>">
                        </div>
                        
                        <%-- Input + botón recargar --%>
                        <div class="flex-1 w-full">
                            <div class="flex gap-2">
                                <input type="text" 
                                       id="captcha" 
                                       name="captcha" 
                                       required
                                       maxlength="6"
                                       autocomplete="off"
                                       class="flex-1 px-4 py-3 border-2 border-gray-200 rounded-lg focus:border-morado-seguridad focus:outline-none focus:ring-2 focus:ring-morado-seguridad/20 transition-colors bg-gray-50 focus:bg-white text-center font-mono text-lg tracking-widest uppercase"
                                       placeholder="ABC123">
                                <button type="button" 
                                        onclick="recargarCaptcha()"
                                        class="px-4 py-3 bg-gray-100 hover:bg-gray-200 text-gray-600 rounded-lg transition-colors"
                                        title="<fmt:message key='consulta.captcha.recargar'/>">
                                    <i class="fa-solid fa-rotate"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <%-- Botón consultar --%>
                <button type="submit" 
                        class="w-full py-3 bg-azul-salud hover:bg-opacity-90 text-white font-bold rounded-lg transition-colors shadow-md flex items-center justify-center text-lg">
                    <i class="fa-solid fa-magnifying-glass mr-2"></i>
                    <fmt:message key="consulta.buscar"/>
                </button>
            </form>
        </div>

        <%-- Resultados --%>
        <c:if test="${resultado}">
            
            <%-- Info del paciente --%>
            <div class="bg-white rounded-xl shadow-lg p-6 mb-6 border-l-4 border-verde-sena">
                <h2 class="text-lg font-bold text-gray-800 mb-3 flex items-center">
                    <i class="fa-solid fa-user-circle mr-2 text-verde-sena"></i>
                    <fmt:message key="consulta.paciente.info"/>
                </h2>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm">
                    <div>
                        <span class="text-gray-500"><fmt:message key="paciente.nombres"/>:</span>
                        <span class="font-semibold text-gray-800 ml-1">${paciente.nombres} ${paciente.apellidos}</span>
                    </div>
                    <div>
                        <span class="text-gray-500"><fmt:message key="paciente.documento"/>:</span>
                        <span class="font-semibold text-gray-800 ml-1">${paciente.documento}</span>
                    </div>
                    <div>
                        <span class="text-gray-500"><fmt:message key="paciente.eps"/>:</span>
                        <span class="font-semibold text-gray-800 ml-1">${paciente.eps}</span>
                    </div>
                </div>
            </div>

            <%-- Tabla de citas --%>
            <div class="bg-white rounded-xl shadow-lg overflow-hidden">
                <div class="p-6 border-b border-gray-100">
                    <h2 class="text-lg font-bold text-azul-salud flex items-center">
                        <i class="fa-solid fa-calendar-check mr-2"></i>
                        <fmt:message key="consulta.citas.encontradas"/>
                        <span class="ml-2 bg-azul-salud text-white text-xs px-2 py-1 rounded-full">${citas.size()}</span>
                    </h2>
                </div>
                
                <div class="overflow-x-auto">
                    <table class="w-full text-left border-collapse">
                        <thead class="bg-gray-50 text-gray-600 text-sm">
                            <tr>
                                <th class="p-4 font-semibold"><fmt:message key="cita.fecha"/></th>
                                <th class="p-4 font-semibold"><fmt:message key="cita.hora"/></th>
                                <th class="p-4 font-semibold"><fmt:message key="cita.especialidad"/></th>
                                <th class="p-4 font-semibold"><fmt:message key="cita.motivo"/></th>
                                <th class="p-4 font-semibold text-center"><fmt:message key="cita.estado"/></th>
                            </tr>
                        </thead>
                        <tbody class="text-sm">
                            <c:forEach var="cita" items="${citas}">
                                <tr class="border-b border-gray-50 hover:bg-gray-50 transition-colors">
                                    <td class="p-4 font-medium text-gray-800">${cita.fechaCita}</td>
                                    <td class="p-4 text-gray-600">${cita.horaCita}</td>
                                    <td class="p-4 text-gray-600">${cita.nombreEspecialidad}</td>
                                    <td class="p-4 text-gray-600 max-w-xs truncate">${cita.motivo}</td>
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
                        </tbody>
                    </table>
                </div>
                
                <%-- Sin citas --%>
                <c:if test="${empty citas}">
                    <div class="p-8 text-center text-gray-400">
                        <i class="fa-solid fa-calendar-xmark text-4xl mb-3 block"></i>
                        <fmt:message key="consulta.sin.citas"/>
                    </div>
                </c:if>
            </div>

            <%-- Botón nueva consulta --%>
            <div class="text-center mt-8">
                <a href="${pageContext.request.contextPath}/consulta" 
                   class="inline-flex items-center px-6 py-3 bg-white text-azul-salud font-semibold rounded-lg shadow-md hover:shadow-lg transition-shadow border-2 border-azul-salud">
                    <i class="fa-solid fa-arrow-rotate-left mr-2"></i>
                    <fmt:message key="consulta.nueva"/>
                </a>
            </div>
        </c:if>

    </main>
    
    <%-- Footer simplificado --%>
    <footer class="bg-azul-salud text-white py-4 mt-auto">
        <div class="max-w-7xl mx-auto px-4 text-center text-sm">
            <span class="font-semibold">SaludBoyacá</span>
            <span class="text-white text-opacity-60 mx-2">|</span>
            <span class="text-white text-opacity-80">Centro de Salud Municipal de Paipa</span>
        </div>
    </footer>

    <%-- Scripts --%>
    <script>
        function cambiarIdioma(lang) {
            fetch('${pageContext.request.contextPath}/locale?lang=' + encodeURIComponent(lang), { 
                method: 'GET',
                headers: { 'X-Requested-With': 'XMLHttpRequest' }
            })
            .then(response => {
                if(response.ok) {
                    window.location.reload();
                }
            })
            .catch(err => console.error('Error cambiando idioma:', err));
        }

        function recargarCaptcha() {
            fetch('${pageContext.request.contextPath}/captcha', {
                headers: { 'X-Requested-With': 'XMLHttpRequest' }
            })
            .then(response => response.json())
            .then(data => {
                if(data.success) {
                    document.querySelector('.captcha-img').src = data.image;
                    document.getElementById('captcha').value = '';
                    document.getElementById('captcha').focus();
                }
            })
            .catch(err => console.error('Error recargando CAPTCHA:', err));
        }
    </script>

</body>
</html>