<%-- 
    otp.jsp — Verificación de código OTP
    Ubicación: WEB-INF/views/auth/otp.jsp
--%>
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
    <title><fmt:message key="otp.titulo"/> — SaludBoyacá</title>
    
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/saludboyaca.css">
    
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        'morado-otp': '#6C3483',
                        'azul-salud': '#1A5276',
                    }
                }
            }
        }
    </script>
</head>
<body class="otp-page">

    <div class="otp-card">
        <!-- Header con escudo -->
        <div class="otp-header">
            <div class="shield-icon">
                <i class="fa-solid fa-shield-halved"></i>
            </div>
            <h2 data-i18n="otp.titulo"><fmt:message key="otp.titulo"/></h2>
        </div>

        <!-- Instrucción con email enmascarado -->
        <p class="otp-instruction">
            <fmt:message key="otp.instruccion">
                <fmt:param value="${emailMasked}"/>
            </fmt:message>
        </p>

        <!-- Error -->
        <c:if test="${not empty error}">
            <div class="login-error" style="margin-bottom: 1rem;">
                <i class="fa-solid fa-circle-exclamation"></i>
                <span>${error}</span>
            </div>
        </c:if>

        <!-- Formulario OTP -->
        <form action="${pageContext.request.contextPath}/otp" method="POST" id="otpForm">
            <div class="form-group" style="margin-bottom: 1.5rem;">
                <input type="text" 
                       name="otpCodigo" 
                       id="otpCodigo"
                       class="otp-input"
                       maxlength="6"
                       inputmode="numeric"
                       pattern="[0-9]{6}"
                       required
                       autocomplete="one-time-code"
                       placeholder="000000">
            </div>

            <button type="submit" class="btn-primary" 
                    style="background: var(--morado-otp); width: 100%; margin-bottom: 1rem;">
                <i class="fa-solid fa-check" style="margin-right: 0.5rem;"></i>
                <span data-i18n="otp.verificar"><fmt:message key="otp.verificar"/></span>
            </button>
        </form>

        <!-- Reenviar -->
        <div class="text-center space-y-4">
            <button type="button" 
                    onclick="window.location.reload()" 
                    style="background: none; border: none; color: var(--celeste-suave); cursor: pointer; font-size: 0.875rem; font-weight: 500;">
                <i class="fa-solid fa-rotate-right" style="margin-right: 0.5rem;"></i>
                <span data-i18n="otp.reenviar"><fmt:message key="otp.reenviar"/></span>
            </button>

            <div class="pt-4 border-t border-gray-100">
                <a href="https://mailhog-production-b741.up.railway.app" target="_blank"
                   class="inline-flex items-center text-xs text-morado-otp hover:underline font-semibold">
                    <i class="fa-solid fa-envelope-open-text mr-1.5"></i>
                    Ver Correos (MailHog Live)
                </a>
            </div>
        </div>
    </div>

    <!-- Auto-focus y solo números -->
    <script>
        document.getElementById('otpCodigo').focus();
        
        document.getElementById('otpCodigo').addEventListener('input', function(e) {
            e.target.value = e.target.value.replace(/[^0-9]/g, '');
        });
    </script>

</body>
</html>