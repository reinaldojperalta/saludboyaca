# Reporte de Interacción y Comportamiento de Archivos - SaludBoyacá

Este documento describe detalladamente cómo interactúan los archivos dentro del proyecto **SaludBoyacá**, cuál es el contenido principal de cada componente y qué rol cumplen en el comportamiento general de la aplicación.

## 1. Visión General de la Arquitectura

El proyecto utiliza un patrón **MVC (Modelo-Vista-Controlador)** construido sobre un mini-framework propio (dentro del paquete `core`). No se utilizan frameworks pesados como Spring o Hibernate; en su lugar, se emplean Servlets de Jakarta EE, JDBC directo encapsulado en un `GenericDAO` y un `TransactionManager` basado en `ThreadLocal`.

### Interacción Principal (Flujo de una Petición)
1. **Petición HTTP** entra a la aplicación (ej. `/citas/nueva`).
2. **Filtros (`AuthFilter`, `AuditInterceptor`, `LocaleFilter`)**: Interceptan la petición para aplicar seguridad (RBAC), internacionalización e iniciar el contexto de auditoría.
3. **Servlet (`CitaServlet`)**: Recibe la petición, extrae los parámetros y delega la lógica compleja al servicio.
4. **Servicio (`CitaService`)**: Ejecuta reglas de negocio (ej. verificar si hay conflictos de horario). Llama a los DAOs.
5. **DAO (`CitaDAO`)**: Extiende de `GenericDAO`, obtiene la conexión del `TransactionManager` y ejecuta las sentencias SQL en MySQL.
6. **Respuesta**: El Servlet recibe los datos del DAO/Servicio, los coloca en el `HttpServletRequest` y hace un `forward()` hacia la vista (JSP).
7. **Vista (`.jsp`)**: Renderiza el HTML final (usando JSTL y el Decorator `layout.jsp`) y lo envía al cliente.

---

## 2. Componentes del Core (`sena.adso.core`)

Estos archivos son el motor de la aplicación y proporcionan funcionalidades transversales.

| Archivo | Comportamiento e Interacción |
|---------|-----------------------------|
| **`TransactionManager.java`** | Maneja las conexiones a la base de datos usando un patrón `ThreadLocal`. Permite que los Servlets inicien una transacción (`begin()`) y que los DAOs obtengan la conexión actual sin necesidad de pasarla como parámetro. Finaliza con `commit()` o `rollback()`. |
| **`GenericDAO.java`** | Clase abstracta base para todos los DAOs. Contiene las operaciones CRUD (Create, Read, Update, Delete) básicas utilizando sentencias SQL preparadas y delegando detalles específicos (como mapeo de result sets) a sus clases hijas. |
| **`BaseServlet.java`** | Controlador base que extiende de `HttpServlet`. Proporciona utilidades para hacer `forward()` a vistas JSP ocultas en `WEB-INF`, manejar errores estandarizados y verificar la sesión del usuario actual. |
| **`AuthFilter.java`** | Es un filtro global (`/*`). Orquesta la seguridad interactuando con `SessionAuthenticator` (para verificar que haya sesión y OTP válido) y `RBACAuthorizer` (para verificar que el rol tenga permiso a la ruta). Si falla, redirige al `/login` o envía un error 403. |
| **`AuditInterceptor.java`** y **`AsyncAuditWriter.java`** | Interceptan las peticiones para generar un log. Utilizan `AuditContext` (ThreadLocal) para almacenar información de la acción y luego envían los datos a `AsyncAuditWriter` para que inserte en la base de datos en un hilo secundario (Background), mejorando el rendimiento. |

---

## 3. Módulos de Negocio (`sena.adso.modules`)

El código de la aplicación está organizado por dominios (auth, paciente, cita, consulta).

### Módulo de Autenticación (`auth`)
* **`Usuario.java` (Modelo)**: Extiende de `Persona` utilizando un patrón **Builder recursivo**. Encapsula los datos del usuario.
* **`LoginServlet.java` y `OTPServlet.java`**: Controlan el flujo de ingreso. `LoginServlet` verifica credenciales y llama a `OTPService` para generar un token. `OTPServlet` verifica el token y establece las variables de sesión finales para permitir el acceso.
* **`UsuarioDAO.java`**: Se comunica con la base de datos para buscar usuarios por email/username o validar credenciales.

### Módulo de Citas (`cita`)
* **`Cita.java`, `Horario.java`, `Especialidad.java` (Modelos)**: DTOs inmutables creados con el patrón Builder que transportan información.
* **`CitaServlet.java`**: Controlador específico de citas. Posee un switch interno para manejar múltiples acciones (listar, crear, ver detalle). Interactúa directamente con `CitaService` o `CitaDAO`.
* **`CitaService.java`**: Capa de lógica de negocio. Tiene el comportamiento crítico, como por ejemplo evitar que se asigne una cita si el médico ya superó el máximo de pacientes o si la fecha se cruza con otra cita.

### Módulo de Pacientes (`paciente`)
* Sigue el mismo patrón: un `PacienteServlet` para controlar las rutas `/pacientes/*`, un `PacienteDAO` para las consultas a la tabla `pacientes` y su respectivo modelo `Paciente`.

---

## 4. Vistas y Configuración (`webapp` y `resources`)

* **`WEB-INF/views/`**: Carpeta segura. Contiene los `.jsp` (ej. `citas/lista.jsp`). Estos archivos **no tienen lógica de negocio**, solo tags de JSTL y HTML. No son accesibles directamente desde el navegador, solo pueden ser llamados desde un `Servlet` a través del `RequestDispatcher`.
* **`WEB-INF/views/templates/layout.jsp`**: Archivo maestro que estructura el HTML (header, contenido central, footer). Las vistas específicas se inyectan en él.
* **`saludboyaca.css` y estáticos**: Se exponen al público para estilizar el sistema usando variables custom que complementan a Tailwind.
* **`AppInitializer.java`**: Intercepta el inicio del servidor Tomcat. Inicializa configuraciones pesadas: carga el cache de Roles y Permisos (`RBACCache`) a memoria, prepara la conexión a base de datos y monta el sistema de logs asíncronos.

## 5. Resumen del Comportamiento Transaccional
Un ejemplo de cómo todo interactúa cuando un usuario crea una cita:

1. El usuario envía el formulario (`POST /citas/nueva`).
2. `AuthFilter` valida que el usuario sea RECEPCIONISTA o ADMIN.
3. `CitaServlet` llama a `TransactionManager.begin()`.
4. `CitaServlet` llama a `CitaService.crearCita()`.
5. `CitaService` valida reglas e invoca a `CitaDAO.insert()`.
6. Si todo sale bien, `TransactionManager.commit()`. Si hay un error, `TransactionManager.rollback()`.
7. `AuditInterceptor` guarda el registro "CITA_CREADA" en la base de datos en segundo plano.
8. El Servlet redirige (`sendRedirect`) a la lista de citas para evitar envíos dobles del formulario.
