<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - SaludBoyacá</title>
    <!-- Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com"></script>
    <!-- FontAwesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        'azul-salud': '#1A5276',
                        'verde-sena': '#39A900',
                        'gris-hielo': '#EAF0F7'
                    }
                }
            }
        }
    </script>
</head>
<body class="bg-gris-hielo font-sans min-h-screen flex items-center justify-center p-4">

    <div class="max-w-md w-full bg-white rounded-2xl shadow-xl overflow-hidden border-t-8 border-red-500">
        <div class="p-8 text-center">
            <!-- Icono de Error -->
            <div class="inline-flex items-center justify-center w-20 h-20 bg-red-100 rounded-full mb-6">
                <i class="fa-solid fa-triangle-exclamation text-4xl text-red-600"></i>
            </div>

            <!-- Código de Estado -->
            <h1 class="text-5xl font-extrabold text-gray-900 mb-2">
                ${statusCode != null ? statusCode : "500"}
            </h1>
            
            <h2 class="text-xl font-bold text-azul-salud mb-4">
                ¡Algo no salió como esperábamos!
            </h2>

            <!-- Mensaje de Error (Viene del BaseServlet) -->
            <div class="bg-red-50 rounded-lg p-4 mb-6 text-sm text-red-700 border border-red-100 text-left">
                <p class="font-bold mb-1 italic">Detalle del sistema:</p>
                <p>${error != null ? error : "Ha ocurrido un error inesperado en el servidor de SaludBoyacá."}</p>
            </div>

            <!-- Botones de Acción -->
            <div class="flex flex-col space-y-3">
                <a href="${pageContext.request.contextPath}/dashboard" 
                   class="w-full bg-azul-salud hover:bg-opacity-90 text-white font-bold py-3 rounded-xl transition duration-200 flex items-center justify-center">
                    <i class="fa-solid fa-house mr-2"></i> Ir al Dashboard
                </a>
                
                <button onclick="window.history.back()" 
                        class="w-full bg-white border-2 border-gray-200 hover:border-gray-300 text-gray-600 font-bold py-3 rounded-xl transition duration-200">
                    Regresar
                </button>
            </div>
        </div>

        <!-- Footer Decorativo -->
        <div class="bg-gray-50 p-4 border-t border-gray-100 text-center">
            <p class="text-xs text-gray-400 uppercase tracking-widest font-semibold">
                SISTEMA DE GESTIÓN HOSPITALARIA • PAIPA
            </p>
        </div>
    </div>

</body>
</html>