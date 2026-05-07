# SaludBoyacá - Documento de Arquitectura y Contexto Permanente

> **Versión:** 1.0  
> **Fecha:** 2026-04-27  
> **Proyecto:** Sistema de Gestión de Citas Médicas - Centro de Salud Municipal de Paipa, Boyacá  
> **Tecnología Base:** Java 17 + Tomcat 10 + Jakarta EE 9+ + MySQL 8.0  
> **Estado:** Fase de diseño de arquitectura backend completada. Pendiente implementación.

---

## 1. Filosofía de Diseño

### Principios Fundamentales

1. **Máxima Abstracción en Backend:** Todo el código de negocio debe estar desacoplado, reutilizable y extensible mediante herencia, interfaces e inyección de dependencias.
2. **Zero Lógica en JSP:** Las vistas son solo presentación. Cualquier lógica de negocio, validación o decisión debe residir en Servlets, Servicios o DAOs.
3. **Framework Interno (Core):** Se construye un "mini-framework" dentro del paquete `core` que proporciona infraestructura reutilizable (GenericDAO, BaseServlet, TransactionManager, etc.).
4. **Separación de Responsabilidades:** Cada capa tiene una única razón de cambio:
   - **Modelo:** Datos y estructuras (DTOs con Builder)
   - **DAO:** Acceso a datos puro (SQL)
   - **Servicio:** Lógica de negocio y reglas
   - **Servlet:** Control HTTP y orquestación
   - **Filtro:** Cross-cutting concerns (auth, audit, i18n)
5. **Configuración por Ambiente:** Variables de entorno para todo lo que cambia entre local, Docker y producción.

### Decisiones de Arquitectura Clave

| Aspecto | Decisión | Justificación |
|---------|----------|---------------|
| **JDK** | 17 | LTS estable, features modernos (records no usados por compatibilidad con Tomcat 10) |
| **Servidor** | Tomcat 10 | Requerimiento del taller. Obliga Jakarta EE 9+ (paquetes `jakarta.*`) |
| **Servlet API** | 6.0.0 | Versión de Jakarta EE compatible con Tomcat 10 |
| **JSTL** | 3.0.0 (Glassfish) | Implementación Jakarta-compatible |
| **Mail** | Jakarta Mail 2.0.1 | SMTP para OTP. En local usa MailHog (puerto 1025, sin auth). En producción Gmail con App Password |
| **Builder Pattern** | Sí, con herencia recursiva | DTOs inmutables y type-safe. Permite `new Usuario.Builder().nombres("X").username("Y").build()` |
| **Conexión BD** | ThreadLocal<Connection> | Cada request HTTP (hilo) tiene su conexión privada. Permite transacciones sin pasar Connection por parámetro |
| **Auth** | Sesión HTTP + OTP en BD | OTP se guarda en `otp_tokens`, se valida contra BD, se marca como usado. No se elimina (auditoría) |
| **RBAC** | Cache en memoria (ServletContext) | Roles y permisos se cargan al iniciar la app. Se consultan en cada request contra memoria, no BD |
| **Logs** | ThreadLocal + Async | AuditContext guarda datos del request en ThreadLocal. Filtro al final persiste async vía ExecutorService |
| **CSS** | Tailwind CDN | Por simplicidad en desarrollo. No se compila. Variables CSS custom en `saludboyaca.css` |
| **Vistas** | Dentro de `WEB-INF/views/` | Protegidas contra acceso directo. Solo accesibles vía `RequestDispatcher.forward()` |
| **Recursos estáticos** | Fuera de WEB-INF en `resources/` | CSS, JS, imágenes accesibles directamente por URL |

---

## 2. Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── sena/adso/saludboyaca/
│   │       ├── core/                          # Framework interno (reutilizable)
│   │       │   ├── model/
│   │       │   │   └── Identifiable.java      # Interfaz: getId(), setId()
│   │       │   ├── servlet/
│   │       │   │   └── BaseServlet.java       # Utilidades HTTP comunes
│   │       │   ├── dao/
│   │       │   │   ├── GenericDAO.java        # CRUD abstracto con SQL semi-manual
│   │       │   │   └── TransactionManager.java # ThreadLocal<Connection>
│   │       │   ├── security/                  # Descomposición del AuthFilter
│   │       │   │   ├── AuthFilter.java        # Orquesta auth + authz
│   │       │   │   ├── Authenticator.java     # Interfaz
│   │       │   │   ├── SessionAuthenticator.java # Impl: sesión + OTP verificado
│   │       │   │   ├── Authorizer.java        # Interfaz
│   │       │   │   ├── RBACAuthorizer.java    # Impl: consulta RBACCache
│   │       │   │   └── RouteMatcher.java      # Define rutas públicas
│   │       │   ├── rbac/
│   │       │   │   ├── model/                 # Rol, Permiso (DTOs simples)
│   │       │   │   └── RBACCache.java         # Map<rol, Set<permissionKey>> en ServletContext
│   │       │   ├── audit/
│   │       │   │   ├── AuditContext.java      # ThreadLocal<AuditEntry>
│   │       │   │   ├── AuditInterceptor.java  # @WebFilter("/*") - captura request/response
│   │       │   │   ├── AsyncAuditWriter.java  # ExecutorService + INSERT a activity_logs
│   │       │   │   └── AuditEntry.java        # DTO del log
│   │       │   ├── exception/
│   │       │   │   ├── BusinessException.java # 400 - validaciones de negocio
│   │       │   │   ├── AuthException.java     # 401/403
│   │       │   │   └── DataAccessException.java # 500 - BD
│   │       │   └── util/
│   │       │       ├── I18nHelper.java        # ResourceBundle + Locale de sesión
│   │       │       ├── Validator.java         # Validaciones genéricas
│   │       │       └── DatabaseConfig.java    # Lee variables de entorno
│   │       ├── config/
│   │       │   └── AppInitializer.java        # ServletContextListener
│   │       │                                   # - Carga RBACCache
│   │       │                                   # - Inyecta Authenticator/Authorizer en contexto
│   │       │                                   # - Inicializa AsyncAuditWriter
│   │       │                                   # - Valida conexión a BD
│   │       └── modules/                        # Dominio del negocio
│   │           ├── auth/
│   │           │   ├── model/
│   │           │   │   ├── Persona.java        # Clase base abstracta (nombres, apellidos, documento)
│   │           │   │   ├── Usuario.java        # extends Persona + Builder
│   │           │   │   ├── Rol.java            # DTO simple
│   │           │   │   └── Permiso.java        # DTO simple
│   │           │   ├── dao/
│   │           │   │   ├── UsuarioDAO.java     # extends GenericDAO<Usuario, Integer>
│   │           │   │   ├── RolDAO.java
│   │           │   │   └── PermisoDAO.java
│   │           │   ├── servlet/
│   │           │   │   ├── LoginServlet.java   # extends BaseServlet
│   │           │   │   ├── OTPServlet.java     # extends BaseServlet
│   │           │   │   └── LogoutServlet.java  # extends BaseServlet
│   │           │   └── service/
│   │           │       └── OTPService.java     # Genera código, guarda en otp_tokens, envía vía MailHog
│   │           ├── paciente/
│   │           │   ├── model/Paciente.java     # extends Persona + Builder
│   │           │   ├── dao/PacienteDAO.java    # extends GenericDAO<Paciente, Integer>
│   │           │   └── servlet/PacienteServlet.java # extends BaseServlet (switch propio)
│   │           ├── cita/
│   │           │   ├── model/
│   │           │   │   ├── Cita.java           # DTO con Builder
│   │           │   │   ├── Especialidad.java   # DTO simple
│   │           │   │   └── Horario.java        # DTO simple
│   │           │   ├── dao/
│   │           │   │   ├── CitaDAO.java        # extends GenericDAO<Cita, Integer>
│   │           │   │   ├── EspecialidadDAO.java
│   │           │   │   └── HorarioDAO.java
│   │           │   ├── servlet/CitaServlet.java # extends BaseServlet (switch propio)
│   │           │   └── service/CitaService.java # Validaciones de disponibilidad, conflictos
│   │           └── consulta/
│   │               ├── servlet/ConsultaPublicaServlet.java # extends BaseServlet, ruta pública
│   │               └── service/ConsultaService.java
│   ├── resources/
│   │   ├── messages.properties                 # Español (default)
│   │   ├── messages_en.properties              # Inglés
│   │   ├── messages_it.properties              # Italiano
│   │   └── saludboyaca.sql                     # Script de BD completo
│   └── webapp/
│       ├── WEB-INF/
│       │   ├── web.xml                         # Mínimo: filtros, welcome-file
│       │   └── views/                          # JSP protegidos (solo forward)
│       │       ├── templates/
│       │       │   ├── layout.jsp              # Decorator pattern
│       │       │   ├── header.jsp              # Navbar con menú condicional por rol
│       │       │   └── footer.jsp
│       │       ├── auth/
│       │       │   ├── login.jsp
│       │       │   └── otp.jsp
│       │       ├── dashboard.jsp
│       │       ├── pacientes/
│       │       │   ├── lista.jsp
│       │       │   └── formulario.jsp
│       │       ├── citas/
│       │       │   ├── lista.jsp
│       │       │   ├── formulario.jsp
│       │       │   └── detalle.jsp
│       │       ├── horarios/
│       │       │   └── lista.jsp
│       │       ├── consulta.jsp                # Módulo público (sin auth)
│       │       └── error.jsp
│       └── resources/                          # Estáticos públicos
│           ├── css/
│           │   └── saludboyaca.css             # Variables CSS, NO copiar del taller
│           ├── js/
│           └── images/
└── test/                                       # Tests unitarios e integración
```

---

## 3. Modelo de Datos (MySQL 8.0)

### 3.1 Esquema Completo

```sql
-- Configuración inicial
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 1. TABLAS PRINCIPALES
-- =============================================

CREATE TABLE especialidades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    descripcion VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(80) NOT NULL,
    apellidos VARCHAR(80) NOT NULL,
    documento VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    id_especialidad INT NULL,
    lang_preferido VARCHAR(5) DEFAULT 'es',
    FOREIGN KEY (id_especialidad) REFERENCES especialidades(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE pacientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(80) NOT NULL,
    apellidos VARCHAR(80) NOT NULL,
    documento VARCHAR(20) NOT NULL UNIQUE,
    fecha_nacimiento DATE NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    eps VARCHAR(80) NOT NULL,
    vereda_barrio VARCHAR(80)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE horarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_medico INT NOT NULL,
    dia_semana TINYINT NOT NULL COMMENT '1=Lun 2=Mar 3=Mié 4=Jue 5=Vie',
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    max_citas INT DEFAULT 10,
    FOREIGN KEY (id_medico) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE citas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente INT NOT NULL,
    id_medico INT NOT NULL,
    id_especialidad INT NOT NULL,
    fecha_cita DATE NOT NULL,
    hora_cita TIME NOT NULL,
    motivo VARCHAR(300),
    estado ENUM('PROGRAMADA','CONFIRMADA','ATENDIDA','CANCELADA') DEFAULT 'PROGRAMADA',
    observaciones VARCHAR(500),
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    id_registrado_por INT,
    FOREIGN KEY (id_paciente) REFERENCES pacientes(id),
    FOREIGN KEY (id_medico) REFERENCES usuarios(id),
    FOREIGN KEY (id_especialidad) REFERENCES especialidades(id),
    FOREIGN KEY (id_registrado_por) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 2. SISTEMA RBAC (Role-Based Access Control)
-- =============================================

CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE permissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    permission_key VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE role_permissions (
    role_id INT,
    permission_id INT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_roles (
    user_id INT,
    role_id INT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES usuarios(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 3. TOKENS OTP Y LOGS
-- =============================================

CREATE TABLE otp_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    codigo VARCHAR(6) NOT NULL,
    fecha_gen DATETIME DEFAULT CURRENT_TIMESTAMP,
    expira_en DATETIME NOT NULL,
    usado TINYINT(1) DEFAULT 0,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE activity_logs (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id INT NOT NULL,
    action_name VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id VARCHAR(36),
    status VARCHAR(20) NOT NULL,
    request_data JSON,
    response_data JSON,
    error_message TEXT,
    execution_time_ms INT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 4. ÍNDICES DE PERFORMANCE
-- =============================================

CREATE INDEX idx_citas_fecha_estado ON citas(fecha_cita, estado);
CREATE INDEX idx_citas_medico_fecha ON citas(id_medico, fecha_cita);
CREATE INDEX idx_citas_paciente ON citas(id_paciente, fecha_cita DESC);
CREATE INDEX idx_horarios_medico_dia ON horarios(id_medico, dia_semana);
CREATE INDEX idx_otp_validacion ON otp_tokens(id_usuario, codigo, expira_en, usado);
CREATE INDEX idx_logs_user_time ON activity_logs(user_id, created_at DESC);
CREATE INDEX idx_logs_action ON activity_logs(action_name, created_at);

SET FOREIGN_KEY_CHECKS = 1;
```

### 3.2 Decisiones del Modelo de Datos

| Decisión | Justificación |
|----------|---------------|
| **No hay `user_permissions`** | Simplificación. Solo RBAC puro (roles → permisos). Permisos directos por usuario se agregarían en futuro si es necesario |
| **OTP no se borra, se marca `usado=1`** | Auditoría. Se puede ver historial de intentos de login |
| **`activity_logs` en vez de `log_accesos`** | Diseño más profesional y flexible. Guarda request/response como JSON, tiempo de ejecución, etc. |
| **`entity_id` como VARCHAR(36)** | Permite IDs INT o UUID según la entidad |
| **No hay `ON DELETE CASCADE`** | Integridad referencial manejada en código (Servicios) para mayor control y evitar borrados accidentales |
| **No hay vistas ni SPs** | Toda la lógica de negocio reside en Java. La BD es solo persistencia |
| **Charset utf8mb4** | Soporte completo de Unicode (emojis, caracteres especiales del español) |

---

## 4. Patrones de Diseño Implementados

### 4.1 Builder con Herencia Recursiva (DTOs)

Permite construir objetos inmutables type-safe con herencia:

```java
// Clase base
public abstract class Persona {
    protected final String nombres;
    protected final String apellidos;
    protected final String documento;

    protected Persona(Builder<?> builder) {
        this.nombres = builder.nombres;
        this.apellidos = builder.apellidos;
        this.documento = builder.documento;
    }

    public abstract static class Builder<T extends Builder<T>> {
        protected String nombres;
        protected String apellidos;
        protected String documento;

        public T nombres(String val) { this.nombres = val; return self(); }
        public T apellidos(String val) { this.apellidos = val; return self(); }
        public T documento(String val) { this.documento = val; return self(); }

        protected abstract T self();
        public abstract Persona build();
    }
}

// Clase hija
public class Usuario extends Persona {
    private final String username;
    private final String email;

    private Usuario(Builder builder) {
        super(builder);
        this.username = builder.username;
        this.email = builder.email;
    }

    public static class Builder extends Persona.Builder<Builder> {
        private String username;
        private String email;

        public Builder username(String val) { this.username = val; return self(); }
        public Builder email(String val) { this.email = val; return self(); }

        @Override protected Builder self() { return this; }
        @Override public Usuario build() { return new Usuario(this); }
    }
}

// Uso
Usuario u = new Usuario.Builder()
    .nombres("Carlos")      // heredado
    .apellidos("Gómez")     // heredado
    .username("cgomez")     // propio
    .email("c@g.com")       // propio
    .build();
```

### 4.2 GenericDAO con SQL Semi-Manual

Abstracción de CRUD que obliga a los hijos a definir detalles específicos:

```java
public abstract class GenericDAO<T extends Identifiable<ID>, ID> {

    protected Connection getConnection() {
        return TransactionManager.get(); // ThreadLocal
    }

    public T findById(ID id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        // ... PreparedStatement, executeQuery, mapRow(rs)
    }

    public List<T> findAll() { ... }
    public ID insert(T entity) { ... }
    public void update(T entity) { ... }
    public void delete(ID id) { ... }

    // Métodos abstractos que cada DAO implementa
    protected abstract String getTableName();
    protected abstract String getColumnNames();
    protected abstract T mapRow(ResultSet rs) throws SQLException;
    protected abstract void prepareInsert(PreparedStatement ps, T entity) throws SQLException;
    protected abstract void prepareUpdate(PreparedStatement ps, T entity) throws SQLException;
}
```

### 4.3 ThreadLocal para Conexiones (TransactionManager)

Cada hilo de request tiene su propia conexión, permitiendo transacciones sin pasar Connection por parámetro:

```java
public class TransactionManager {
    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    public static void begin() throws SQLException {
        Connection conn = DatabaseConfig.getDataSource().getConnection();
        conn.setAutoCommit(false);
        connectionHolder.set(conn);
    }

    public static Connection get() {
        return connectionHolder.get();
    }

    public static void commit() throws SQLException {
        connectionHolder.get().commit();
    }

    public static void rollback() {
        try { connectionHolder.get().rollback(); } catch (SQLException e) { /* log */ }
    }

    public static void close() {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) { /* log */ }
            connectionHolder.remove(); // ¡CRÍTICO! Evita fugas entre requests
        }
    }
}
```

**Flujo típico en un Servlet:**
```java
TransactionManager.begin();
try {
    pacienteDAO.insert(paciente);
    citaDAO.insert(cita);
    logDAO.insert(log);
    TransactionManager.commit();
} catch (Exception e) {
    TransactionManager.rollback();
    throw new BusinessException("Error al crear cita", e);
} finally {
    TransactionManager.close();
}
```

### 4.4 AuthFilter Descompuesto (Inyección vía ServletContext)

El filtro no tiene lógica de negocio. Orquesta componentes inyectados:

```java
@WebFilter(urlPatterns = {"/*"})
public class AuthFilter implements Filter {
    private Authenticator authenticator;
    private Authorizer authorizer;
    private RouteMatcher publicRoutes;

    public void init(FilterConfig cfg) {
        ServletContext ctx = cfg.getServletContext();
        this.authenticator = (Authenticator) ctx.getAttribute("authenticator");
        this.authorizer = (Authorizer) ctx.getAttribute("authorizer");
        this.publicRoutes = (RouteMatcher) ctx.getAttribute("publicRoutes");
    }

    public void doFilter(req, resp, chain) {
        if (publicRoutes.matches(req)) { chain.doFilter(); return; }
        if (!authenticator.authenticate(req, resp)) { redirectToLogin(resp); return; }
        if (!authorizer.authorize(req, resp)) { sendForbidden(req, resp); return; }
        chain.doFilter();
    }
}
```

**Componentes:**
- `SessionAuthenticator`: Verifica `session.getAttribute("usuario") != null && session.getAttribute("otpVerificado") == Boolean.TRUE`
- `RBACAuthorizer`: Consulta `RBACCache` para verificar si el rol del usuario tiene el permiso requerido para la ruta actual
- `RouteMatcher`: Lista de patrones excluidos (`/login`, `/otp`, `/consulta`, `/resources/*`, etc.)

### 4.5 AuditInterceptor + ThreadLocal (Logs Async)

El Servlet inicia el contexto, el filtro finaliza y persiste:

```java
// En BaseServlet
protected void audit(HttpServletRequest req, String action, String entityType, Long entityId) {
    AuditEntry entry = new AuditEntry();
    entry.setUserId(getCurrentUser(req).getId());
    entry.setAction(action);
    entry.setEntityType(entityType);
    entry.setEntityId(entityId);
    entry.setStartTime(System.currentTimeMillis());
    entry.setIp(req.getRemoteAddr());
    AuditContext.set(entry);
}

// En AuditInterceptor (Filtro)
public void doFilter(req, resp, chain) {
    try {
        chain.doFilter(req, resp);
    } finally {
        AuditEntry entry = AuditContext.get();
        if (entry != null) {
            entry.setExecutionTime(System.currentTimeMillis() - entry.getStartTime());
            entry.setStatus(resp.getStatus() >= 400 ? "FAILED" : "SUCCESS");
            AsyncAuditWriter.submit(entry); // INSERT en segundo plano
            AuditContext.clear(); // Limpia ThreadLocal
        }
    }
}
```

### 4.6 BaseServlet (Template Method para Servlets)

Proporciona utilidades comunes sin imponer un switch genérico:

```java
public abstract class BaseServlet extends HttpServlet {

    protected void forward(HttpServletRequest req, HttpServletResponse resp, String view) 
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + view + ".jsp").forward(req, resp);
    }

    protected void sendError(HttpServletRequest req, HttpServletResponse resp, 
            String i18nKey, int status) throws ServletException, IOException {
        req.setAttribute("error", I18nHelper.get(req, i18nKey));
        resp.setStatus(status);
        forward(req, resp, "error");
    }

    protected Usuario getCurrentUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s != null ? (Usuario) s.getAttribute("usuario") : null;
    }

    protected boolean hasPermission(HttpServletRequest req, String permissionKey) {
        Usuario u = getCurrentUser(req);
        return u != null && RBACCache.hasPermission(u.getRol(), permissionKey);
    }

    protected void requireAuth(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        if (getCurrentUser(req) == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
```

---

## 5. Flujo de Autenticación (OTP)

```
1. Usuario accede a /login
   → LoginServlet.doGet() → forward a login.jsp (sin CAPTCHA)

2. Usuario envía usuario + contraseña (POST /login)
   → LoginServlet.doPost()
   → UsuarioDAO.validarLogin(username, password)
   → Si inválido: sendError("login.error.credenciales")
   → Si válido:
      a. OTPService.generarOTP() → código de 6 dígitos
      b. INSERT INTO otp_tokens (id_usuario, codigo, expira_en)
      c. OTPService.enviarOTP(email, código) → MailHog (local) o Gmail (prod)
      d. session.setAttribute("usuario", usuario)
      e. session.setAttribute("otpVerificado", false)
      f. redirect a /otp

3. Usuario ve otp.jsp con email enmascarado
   → Ingresa código (POST /otp)
   → OTPServlet.doPost()
   → SELECT * FROM otp_tokens WHERE id_usuario=X AND codigo=Y AND expira_en > NOW() AND usado=0
   → Si inválido: forward a otp.jsp con error
   → Si válido:
      a. UPDATE otp_tokens SET usado=1 WHERE id=...
      b. session.setAttribute("otpVerificado", true)
      c. redirect a /dashboard

4. AuthFilter intercepta cada request
   → Si ruta pública: permite
   → Si no hay sesión o otpVerificado != true: redirect /login
   → Si hay sesión + OTP verificado: chain.doFilter()
```

---

## 6. Flujo de Auditoría (Logs)

```
1. Servlet inicia audit:
   audit(req, "CITA_CREATE", "Cita", null);

2. Servlet ejecuta lógica de negocio
   → CitaService.validarDisponibilidad()
   → CitaDAO.insert(cita)
   → AuditContext.updateEntityId(nuevaCitaId)

3. AuditInterceptor (Filtro) al finalizar request:
   → Calcula execution_time_ms
   → Determina status (SUCCESS/FAILED según HTTP status)
   → AsyncAuditWriter.submit(entry)
   → ExecutorService ejecuta INSERT en activity_logs en hilo separado
   → AuditContext.clear() // Limpia ThreadLocal
```

---

## 7. Configuración por Ambiente

### Variables de Entorno

| Variable | Local | Docker | Producción |
|----------|-------|--------|------------|
| `DB_URL` | `jdbc:mysql://localhost:3306/saludboyaca?useSSL=false&serverTimezone=UTC` | `jdbc:mysql://host.docker.internal:3306/saludboyaca?useSSL=false&serverTimezone=UTC` | URL de Railway/PlanetScale |
| `DB_USER` | `root` | `root` | Usuario del hosting |
| `DB_PASS` | `''` (vacío) | `''` | Contraseña del hosting |
| `EMAIL_HOST` | `localhost` | `host.docker.internal` | `smtp.gmail.com` |
| `EMAIL_PORT` | `1025` | `1025` | `587` |
| `EMAIL_USER` | `''` | `''` | `tucorreo@gmail.com` |
| `EMAIL_PASS` | `''` | `''` | App Password de Gmail |
| `APP_ENV` | `local` | `docker` | `production` |

### DatabaseConfig

```java
public class DatabaseConfig {
    public static String getDbUrl() {
        return System.getenv().getOrDefault("DB_URL", 
            "jdbc:mysql://localhost:3306/saludboyaca?useSSL=false&serverTimezone=UTC");
    }
    public static String getDbUser() {
        return System.getenv().getOrDefault("DB_USER", "root");
    }
    public static String getDbPass() {
        return System.getenv().getOrDefault("DB_PASS", "");
    }
    // ... similares para EMAIL_* ...
}
```

---

## 8. Internacionalización (i18n)

### Archivos de Properties

```properties
# messages.properties (Español - default)
app.nombre=SaludBoyacá - Gestión de Citas
app.institucion=Centro de Salud Municipal de Paipa, Boyacá

login.titulo=Iniciar Sesión
login.usuario=Usuario
login.contrasena=Contraseña
login.ingresar=Ingresar
login.error.credenciales=Usuario o contraseña incorrectos

otp.titulo=Verificación en Dos Pasos
otp.instruccion=Ingrese el código de 6 dígitos enviado a: {0}
otp.campo=Código OTP
otp.verificar=Verificar
otp.error=Código incorrecto o expirado

nav.dashboard=Panel de Control
nav.pacientes=Pacientes
nav.citas=Citas Médicas
nav.salir=Cerrar Sesión

dashboard.bienvenida=Bienvenido, {0}
dashboard.citas.hoy=Citas para hoy
```

### LocaleFilter

```java
@WebFilter("/*")
public class LocaleFilter implements Filter {
    private static final Set<String> IDIOMAS_VALIDOS = Set.of("es", "en", "it");

    public void doFilter(req, resp, chain) {
        HttpSession session = req.getSession();
        String lang = req.getParameter("lang");

        if (lang != null && IDIOMAS_VALIDOS.contains(lang)) {
            session.setAttribute("lang", lang);
        } else if (session.getAttribute("lang") == null) {
            session.setAttribute("lang", "es");
        }

        chain.doFilter(req, resp);
    }
}
```

### Uso en JSP

```jsp
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'es'}"/>
<fmt:setBundle basename="messages"/>

<h1><fmt:message key="login.titulo"/></h1>
<p><fmt:message key="otp.instruccion"><fmt:param value="${emailMasked}"/></fmt:message></p>
```

---

## 9. Dependencias Maven (pom.xml)

```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

    <jakarta.servlet.api.version>6.0.0</jakarta.servlet.api.version>
    <jakarta.servlet.jsp.api.version>3.1.1</jakarta.servlet.jsp.api.version>
    <jakarta.servlet.jsp.jstl.version>3.0.0</jakarta.servlet.jsp.jstl.version>
    <jakarta.mail.version>2.0.1</jakarta.mail.version>
    <mysql.connector.version>8.0.33</mysql.connector.version>
    <itext.version>5.5.13.3</itext.version>
    <apache.poi.version>5.2.3</apache.poi.version>
</properties>

<dependencies>
    <!-- Jakarta EE 9+ (Tomcat 10) -->
    <dependency>
        <groupId>jakarta.servlet</groupId>
        <artifactId>jakarta.servlet-api</artifactId>
        <version>${jakarta.servlet.api.version}</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>jakarta.servlet.jsp</groupId>
        <artifactId>jakarta.servlet.jsp-api</artifactId>
        <version>${jakarta.servlet.jsp.api.version}</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.glassfish.web</groupId>
        <artifactId>jakarta.servlet.jsp.jstl</artifactId>
        <version>${jakarta.servlet.jsp.jstl.version}</version>
    </dependency>
    <dependency>
        <groupId>com.sun.mail</groupId>
        <artifactId>jakarta.mail</artifactId>
        <version>${jakarta.mail.version}</version>
    </dependency>

    <!-- Base de datos -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>${mysql.connector.version}</version>
    </dependency>

    <!-- Utilidades -->
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>itextpdf</artifactId>
        <version>${itext.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>${apache.poi.version}</version>
    </dependency>

    <!-- Logging -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.9</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.4.11</version>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.5.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.2.220</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Nota crítica:** Todas las dependencias usan `jakarta.*`, NO `javax.*`. Tomcat 10 no reconoce paquetes `javax.servlet.*`.

---

## 10. Consideraciones para Live/Producción

### Problemas conocidos y soluciones

| Problema | Solución |
|----------|----------|
| **Race condition en citas** (dos usuarios agendan misma hora) | `SELECT ... FOR UPDATE` en `CitaService` + transacción SERIALIZABLE |
| **Pool de conexiones** | Migrar de `DriverManager` a HikariCP cuando se vaya a producción |
| **Rate limiting OTP** | Contador de intentos fallidos por IP/usuario en `OTPTokenDAO` |
| **Limpieza OTPs vencidos** | Job programado cada 24h: `DELETE FROM otp_tokens WHERE expira_en < NOW() - INTERVAL 7 DAY` |
| **Sesiones en cluster** | Si se escala a múltiples Tomcats, reemplazar sesiones HTTP por Redis o sticky sessions |
| **Logs masivos** | Particionar `activity_logs` por mes o usar TTL (MySQL 8.0 no tiene TTL nativo, requiere evento programado) |

### Docker (futuro)

```dockerfile
# Multi-stage build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM tomcat:10.1-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/saludboyaca.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
ENV DB_URL=""
ENV DB_USER=""
ENV DB_PASS=""
ENV EMAIL_PASS=""
CMD ["catalina.sh", "run"]
```

---

## 11. Checklist de Implementación Pendiente

### Fase 1: Core Infrastructure
- [ ] `core/exception/` - BusinessException, AuthException, DataAccessException
- [ ] `core/model/Identifiable.java`
- [ ] `core/util/DatabaseConfig.java`
- [ ] `core/dao/TransactionManager.java`
- [ ] `core/dao/GenericDAO.java`
- [ ] `core/util/I18nHelper.java`
- [ ] `core/audit/AuditEntry.java`
- [ ] `core/audit/AuditContext.java`
- [ ] `core/audit/AsyncAuditWriter.java`
- [ ] `core/audit/AuditInterceptor.java`
- [ ] `core/security/Authenticator.java` (interfaz)
- [ ] `core/security/SessionAuthenticator.java` (impl)
- [ ] `core/security/Authorizer.java` (interfaz)
- [ ] `core/security/RBACAuthorizer.java` (impl)
- [ ] `core/security/RouteMatcher.java`
- [ ] `core/security/AuthFilter.java`
- [ ] `core/rbac/model/Rol.java`
- [ ] `core/rbac/model/Permiso.java`
- [ ] `core/rbac/RBACCache.java`
- [ ] `config/AppInitializer.java`

### Fase 2: Módulo Auth
- [ ] `modules/auth/model/Persona.java`
- [ ] `modules/auth/model/Usuario.java` (con Builder)
- [ ] `modules/auth/dao/UsuarioDAO.java`
- [ ] `modules/auth/dao/RolDAO.java`
- [ ] `modules/auth/dao/PermisoDAO.java`
- [ ] `modules/auth/servlet/LoginServlet.java`
- [ ] `modules/auth/servlet/OTPServlet.java`
- [ ] `modules/auth/servlet/LogoutServlet.java`
- [ ] `modules/auth/service/OTPService.java`

### Fase 3: Módulos de Negocio
- [ ] `modules/paciente/model/Paciente.java` (con Builder)
- [ ] `modules/paciente/dao/PacienteDAO.java`
- [ ] `modules/paciente/servlet/PacienteServlet.java`
- [ ] `modules/cita/model/Cita.java` (con Builder)
- [ ] `modules/cita/model/Especialidad.java`
- [ ] `modules/cita/model/Horario.java`
- [ ] `modules/cita/dao/CitaDAO.java`
- [ ] `modules/cita/dao/EspecialidadDAO.java`
- [ ] `modules/cita/dao/HorarioDAO.java`
- [ ] `modules/cita/servlet/CitaServlet.java`
- [ ] `modules/cita/service/CitaService.java`
- [ ] `modules/consulta/servlet/ConsultaPublicaServlet.java`
- [ ] `modules/consulta/service/ConsultaService.java`

### Fase 4: Vistas (JSP)
- [ ] `WEB-INF/views/templates/layout.jsp`
- [ ] `WEB-INF/views/templates/header.jsp`
- [ ] `WEB-INF/views/templates/footer.jsp`
- [ ] `WEB-INF/views/auth/login.jsp`
- [ ] `WEB-INF/views/auth/otp.jsp`
- [ ] `WEB-INF/views/dashboard.jsp`
- [ ] `WEB-INF/views/pacientes/lista.jsp`
- [ ] `WEB-INF/views/pacientes/formulario.jsp`
- [ ] `WEB-INF/views/citas/lista.jsp`
- [ ] `WEB-INF/views/citas/formulario.jsp`
- [ ] `WEB-INF/views/citas/detalle.jsp`
- [ ] `WEB-INF/views/horarios/lista.jsp`
- [ ] `WEB-INF/views/consulta.jsp`
- [ ] `WEB-INF/views/error.jsp`

### Fase 5: Recursos y Configuración
- [ ] `webapp/resources/css/saludboyaca.css`
- [ ] `src/main/resources/messages.properties`
- [ ] `src/main/resources/messages_en.properties`
- [ ] `src/main/resources/messages_it.properties`
- [ ] `WEB-INF/web.xml`
- [ ] `pom.xml` (ya definido)

### Fase 6: Testing y Docker
- [ ] Tests unitarios para GenericDAO
- [ ] Tests para TransactionManager
- [ ] Tests para OTPService
- [ ] Dockerfile
- [ ] Docker Compose (app + MySQL + MailHog)

---

## 12. Notas para Otros LLMs

### Contexto que debe mantenerse

1. **Este proyecto es una evolución del taller SENA**, no una copia. Se rechaza explícitamente:
   - SQL hardcodeado en Servlets
   - PreparedStatement reemplazado por concatenación de Strings
   - Textos hardcodeados en JSP (sin `<fmt:message>`)
   - CSS copiado del sistema de vacunación
   - CAPTCHA en login (reemplazado por OTP)

2. **La abstracción es prioridad.** Cada nuevo componente debe evaluarse:
   - ¿Puede ir en `core/` y ser reutilizable?
   - ¿Usa interfaces para permitir cambios de implementación?
   - ¿No acopla capas (Servlet no habla directamente con BD)?

3. **Jakarta EE 9+ es obligatorio.** Nunca usar `javax.servlet.*`, `javax.mail.*`, etc.

4. **ThreadLocal requiere `.remove()` en finally.** Siempre. En filtros, en Servlets, en cualquier lugar que use `AuditContext` o `TransactionManager`.

5. **MailHog para local, Gmail para prod.** La configuración de email debe detectar el ambiente vía `APP_ENV` o variables de entorno.

6. **El Builder con herencia recursiva es verboso pero type-safe.** No simplificar a setters tradicionales salvo que el usuario lo solicite explícitamente.

7. **RBAC es cache en memoria.** Si se agregan roles/permisos dinámicamente, se necesita mecanismo de invalidación de cache (no implementado aún).

8. **Vistas dentro de WEB-INF.** Nunca poner JSP accesibles directamente. Siempre via `RequestDispatcher.forward()`.

9. **No vistas ni SPs en BD.** Toda lógica de negocio en Java. La BD es solo persistencia.

10. **Tailwind CDN en desarrollo.** Si se pide build local, se agregará Node.js al pipeline de build, no ahora.

---

*Documento generado como contexto permanente del proyecto SaludBoyacá. Actualizar según evolucione la implementación.*
