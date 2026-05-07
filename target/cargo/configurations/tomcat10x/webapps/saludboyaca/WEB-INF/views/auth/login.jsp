<%-- login.jsp v2 — Cambio de idioma 100% AJAX, sin recarga --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'es'}"/>
<fmt:setBundle basename="messages"/>

<!DOCTYPE html>
<html lang="${sessionScope.lang != null ? sessionScope.lang : 'es'}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title data-i18n="login.titulo"><fmt:message key="login.titulo"/> — SaludBoyacá</title>
    
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
                        'morado-otp': '#6C3483',
                    }
                }
            }
        }
    </script>
</head>
<body class="login-page">

    <div class="login-card">
        <!-- Logo -->
        <div class="login-logo">
            <div style="width: 56px; height: 56px; background: linear-gradient(135deg, var(--azul-salud), var(--verde-sena)); border-radius: 12px; display: flex; align-items: center; justify-content: center; margin: 0 auto 1rem; color: white; font-size: 1.5rem;">
                <i class="fa-solid fa-heart-pulse"></i>
            </div>
            <h1 style="color: var(--azul-salud); font-size: 1.75rem; font-weight: 700;">SaludBoyacá</h1>
            <p class="subtitle" data-i18n="login.subtitulo"><fmt:message key="login.subtitulo"/></p>
        </div>

        <!-- Selector de idiomas -->
        <div class="lang-selector" id="langSelector">
            <button type="button" 
                    class="lang-btn ${sessionScope.lang == 'es' || sessionScope.lang == null ? 'active' : ''}" 
                    data-lang="es" onclick="cambiarIdioma('es')">
                <span class="flag">🇨🇴</span><span>ES</span>
            </button>
            <button type="button" 
                    class="lang-btn ${sessionScope.lang == 'en' ? 'active' : ''}" 
                    data-lang="en" onclick="cambiarIdioma('en')">
                <span class="flag">🇺🇸</span><span>EN</span>
            </button>
            <button type="button" 
                    class="lang-btn ${sessionScope.lang == 'it' ? 'active' : ''}" 
                    data-lang="it" onclick="cambiarIdioma('it')">
                <span class="flag">🇮🇹</span><span>IT</span>
            </button>
        </div>

        <!-- Error del servidor (este NO se traduce por AJAX porque viene del request) -->
        <c:if test="${not empty error}">
            <div class="login-error">
                <i class="fa-solid fa-circle-exclamation"></i>
                <span>${error}</span>
            </div>
        </c:if>

        <!-- Formulario -->
        <form class="login-form" action="${pageContext.request.contextPath}/login" method="POST" id="loginForm">
            <input type="hidden" name="lang" id="langInput" value="${sessionScope.lang != null ? sessionScope.lang : 'es'}">
            
            <div class="form-group">
                <label for="username">
                    <i class="fa-solid fa-user" style="margin-right: 0.5rem; color: var(--azul-salud);"></i>
                    <span data-i18n="login.usuario"><fmt:message key="login.usuario"/></span>
                </label>
                <input type="text" 
                       id="username" 
                       name="username" 
                       data-i18n="login.usuario"
                       placeholder="<fmt:message key='login.usuario'/>" 
                       required 
                       autocomplete="username">
            </div>

            <div class="form-group">
                <label for="password">
                    <i class="fa-solid fa-lock" style="margin-right: 0.5rem; color: var(--azul-salud);"></i>
                    <span data-i18n="login.contrasena"><fmt:message key="login.contrasena"/></span>
                </label>
                <input type="password" 
                       id="password" 
                       name="password" 
                       data-i18n="login.contrasena"
                       placeholder="<fmt:message key='login.contrasena'/>" 
                       required 
                       autocomplete="current-password">
            </div>

            <button type="submit" class="btn-primary">
                <i class="fa-solid fa-arrow-right-to-bracket" style="margin-right: 0.5rem;"></i>
                <span data-i18n="login.ingresar"><fmt:message key="login.ingresar"/></span>
            </button>
        </form>

        <div class="login-footer">
            <a href="#" data-i18n="login.olvido"><fmt:message key="login.olvido"/></a>
        </div>
    </div>

    <!-- JavaScript de traducción AJAX -->
    <script>
        const LOGIN_KEYS = [
            'login.titulo',
            'login.usuario',
            'login.contrasena',
            'login.ingresar',
            'login.olvido',
            'login.subtitulo'
        ];

        function cambiarIdioma(lang) {
            // 1. Actualizar botones visuales
            document.querySelectorAll('.lang-btn').forEach(btn => {
                btn.classList.toggle('active', btn.dataset.lang === lang);
            });

            // 2. Actualizar input hidden para el POST
            const langInput = document.getElementById('langInput');
            if (langInput) langInput.value = lang;

            // 3. Llamar al LocaleServlet
            const contextPath = '${pageContext.request.contextPath}';
            const keysParam = LOGIN_KEYS.join(',');
            const url = contextPath + '/locale?lang=' + encodeURIComponent(lang) + '&keys=' + encodeURIComponent(keysParam);

            fetch(url, {
                method: 'GET',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest',
                    'Accept': 'application/json'
                }
            })
            .then(r => {
                if (!r.ok) throw new Error('HTTP ' + r.status);
                return r.json();
            })
            .then(data => {
                if (data._status !== 'ok') return;

                // 4. Aplicar traducciones a CADA elemento con data-i18n
                LOGIN_KEYS.forEach(key => {
                    if (!data[key]) return;
                    
                    document.querySelectorAll('[data-i18n="' + key + '"]').forEach(el => {
                        const tag = el.tagName;
                        
                        // TITLE de la página
                        if (tag === 'TITLE') {
                            document.title = data[key] + ' — SaludBoyacá';
                        }
                        // INPUT / TEXTAREA → placeholder
                        else if (tag === 'INPUT' || tag === 'TEXTAREA') {
                            el.placeholder = data[key];
                        }
                        // Resto → textContent
                        else {
                            el.textContent = data[key];
                        }
                    });
                });

                // 5. Actualizar lang del HTML
                document.documentElement.lang = lang;
                
                // 6. Guardar preferencia
                localStorage.setItem('saludboyaca_lang', lang);
            })
            .catch(err => {
                console.error('Error cambiando idioma:', err);
            });
        }

        // Al cargar: si hay idioma guardado y no hay sesión, aplicarlo
        document.addEventListener('DOMContentLoaded', function() {
            const saved = localStorage.getItem('saludboyaca_lang');
            const current = '${sessionScope.lang}';
            if (saved && saved !== current && !current) {
                cambiarIdioma(saved);
            }
        });
    </script>

</body>
</html>