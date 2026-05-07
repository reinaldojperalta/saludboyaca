package sena.adso.modules.auth.model;

import java.util.HashSet;
import java.util.Set;

import sena.adso.core.rbac.model.Rol;

/**
 * Entidad Usuario — personal del sistema (médicos, enfermeros, recepcionistas).
 * 
 * Extiende Persona con atributos específicos de cuenta de sistema.
 * Usa el patrón Builder con herencia recursiva para mantener type-safety.
 */
public class Usuario extends Persona {

    private final String email;
    private final String username;
    private final String password;
    private final Integer idEspecialidad;
    private final String langPreferido;
    private final Set<Rol> roles;

    private Usuario(Builder builder) {
        super(builder);
        this.email = builder.email;
        this.username = builder.username;
        this.password = builder.password;
        this.idEspecialidad = builder.idEspecialidad;
        this.langPreferido = builder.langPreferido;
        this.roles = builder.roles != null ? new HashSet<>(builder.roles) : new HashSet<>();
    }

    // ============================================================
    // GETTERS
    // ============================================================

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Integer getIdEspecialidad() {
        return idEspecialidad;
    }

    public String getLangPreferido() {
        return langPreferido;
    }

    public Set<Rol> getRoles() {
        return new HashSet<>(roles); // Copia defensiva
    }

    /**
     * Obtiene el nombre del primer rol (para compatibilidad con sesión).
     * En el futuro, un usuario puede tener múltiples roles.
     */
    public String getRol() {
        return roles.isEmpty() ? null : roles.iterator().next().getName();
    }

    /**
     * Verifica si el usuario tiene un rol específico.
     */
    public boolean hasRole(String rolName) {
        return roles.stream().anyMatch(r -> r.getName().equals(rolName));
    }

    // ============================================================
    // BUILDER CONCRETO
    // ============================================================

    public static class Builder extends Persona.Builder<Builder> {

        private String email;
        private String username;
        private String password;
        private Integer idEspecialidad;
        private String langPreferido = "es";
        private Set<Rol> roles = new HashSet<>();

        public Builder email(String email) {
            this.email = email;
            return self();
        }

        public Builder username(String username) {
            this.username = username;
            return self();
        }

        public Builder password(String password) {
            this.password = password;
            return self();
        }

        public Builder idEspecialidad(Integer idEspecialidad) {
            this.idEspecialidad = idEspecialidad;
            return self();
        }

        public Builder langPreferido(String langPreferido) {
            this.langPreferido = langPreferido;
            return self();
        }

        public Builder roles(Set<Rol> roles) {
            this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
            return self();
        }

        public Builder addRol(Rol rol) {
            this.roles.add(rol);
            return self();
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Usuario build() {
            // Validaciones mínimas
            if (nombres == null || nombres.isBlank()) {
                throw new IllegalStateException("nombres es requerido");
            }
            if (username == null || username.isBlank()) {
                throw new IllegalStateException("username es requerido");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalStateException("password es requerido");
            }

            return new Usuario(this);
        }
    }
}