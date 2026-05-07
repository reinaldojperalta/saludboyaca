
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="bg-azul-salud text-white p-4 shadow-md flex justify-between items-center sticky top-0 z-50">
    <!-- Logo y Marca -->
    <div class="flex items-center space-x-3">
        <div class="w-9 h-9 bg-verde-sena rounded-lg flex items-center justify-center">
            <i class="fa-solid fa-heart-pulse text-lg"></i>
        </div>
        <span class="text-xl font-bold tracking-wider">SaludBoyacá</span>
    </div>

    <!-- Menú Principal (RBAC) -->
    <div class="hidden md:flex space-x-1 items-center">
        <c:if test="${hasPermissionCita || pCita}">
            <a href="${pageContext.request.contextPath}/citas" 
               class="px-3 py-2 rounded-lg hover:bg-white hover:bg-opacity-15 transition-colors text-sm font-medium ${pageContext.request.requestURI.contains('/citas') ? 'bg-white bg-opacity-15' : ''}">
                <i class="fa-solid fa-calendar-check mr-1.5"></i>
                <fmt:message key="dashboard.menu.citas"/>
            </a>
        </c:if>
        
        <c:if test="${hasPermissionPaciente || pPaciente}">
            <a href="${pageContext.request.contextPath}/pacientes" 
               class="px-3 py-2 rounded-lg hover:bg-white hover:bg-opacity-15 transition-colors text-sm font-medium ${pageContext.request.requestURI.contains('/pacientes') ? 'bg-white bg-opacity-15' : ''}">
                <i class="fa-solid fa-users mr-1.5"></i>
                <fmt:message key="dashboard.menu.pacientes"/>
            </a>
        </c:if>
        
        <c:if test="${hasPermissionHorario || pHorario}">
            <a href="${pageContext.request.contextPath}/horarios" 
               class="px-3 py-2 rounded-lg hover:bg-white hover:bg-opacity-15 transition-colors text-sm font-medium ${pageContext.request.requestURI.contains('/horarios') ? 'bg-white bg-opacity-15' : ''}">
                <i class="fa-solid fa-clock mr-1.5"></i>
                <fmt:message key="dashboard.menu.horarios"/>
            </a>
        </c:if>
        
        <c:if test="${hasPermissionUsuario || pUsuario}">
            <a href="${pageContext.request.contextPath}/usuarios" 
               class="px-3 py-2 rounded-lg hover:bg-white hover:bg-opacity-15 transition-colors text-sm font-medium ${pageContext.request.requestURI.contains('/usuarios') ? 'bg-white bg-opacity-15' : ''}">
                <i class="fa-solid fa-user-shield mr-1.5"></i>
                <fmt:message key="dashboard.menu.usuarios"/>
            </a>
        </c:if>
        

    </div>

    <!-- Usuario, Idioma y Logout -->
    <div class="flex items-center space-x-4">
        <!-- Selector de Idioma -->
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

        <!-- Info Usuario -->
        <div class="text-right leading-tight hidden sm:block">
            <div class="font-bold text-sm">${sessionScope.usuarioNombre}</div>
            <div class="text-xs text-celeste opacity-80">${sessionScope.usuarioRol}</div>
        </div>

        <!-- Logout -->
        <a href="${pageContext.request.contextPath}/logout" 
           class="text-red-300 hover:text-red-100 transition-colors p-2 rounded-lg hover:bg-white hover:bg-opacity-10"
           title="<fmt:message key='nav.salir'/>">
            <i class="fa-solid fa-right-from-bracket text-lg"></i>
        </a>
    </div>
</nav>

<!-- Script cambio de idioma (compartido) -->
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
</script>