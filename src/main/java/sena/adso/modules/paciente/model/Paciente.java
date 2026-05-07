package sena.adso.modules.paciente.model;

import java.time.LocalDate;

import sena.adso.modules.auth.model.Persona;

/**
 * Entidad Paciente — persona que solicita citas médicas.
 * 
 * Extiende Persona con atributos específicos de paciente.
 */
public class Paciente extends Persona {

    private final LocalDate fechaNacimiento;
    private final String telefono;
    private final String email;
    private final String eps;
    private final String veredaBarrio;

    private Paciente(Builder builder) {
        super(builder);
        this.fechaNacimiento = builder.fechaNacimiento;
        this.telefono = builder.telefono;
        this.email = builder.email;
        this.eps = builder.eps;
        this.veredaBarrio = builder.veredaBarrio;
    }

    // ============================================================
    // GETTERS
    // ============================================================

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getEps() {
        return eps;
    }

    public String getVeredaBarrio() {
        return veredaBarrio;
    }

    public int getEdad() {
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }

    // ============================================================
    // BUILDER CONCRETO
    // ============================================================

    public static class Builder extends Persona.Builder<Builder> {

        private LocalDate fechaNacimiento;
        private String telefono;
        private String email;
        private String eps;
        private String veredaBarrio;

        public Builder fechaNacimiento(LocalDate fechaNacimiento) {
            this.fechaNacimiento = fechaNacimiento;
            return self();
        }

        public Builder telefono(String telefono) {
            this.telefono = telefono;
            return self();
        }

        public Builder email(String email) {
            this.email = email;
            return self();
        }

        public Builder eps(String eps) {
            this.eps = eps;
            return self();
        }

        public Builder veredaBarrio(String veredaBarrio) {
            this.veredaBarrio = veredaBarrio;
            return self();
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Paciente build() {
            if (nombres == null || nombres.isBlank()) {
                throw new IllegalStateException("nombres es requerido");
            }
            if (eps == null || eps.isBlank()) {
                throw new IllegalStateException("eps es requerido");
            }
            if (fechaNacimiento == null) {
                throw new IllegalStateException("fechaNacimiento es requerido");
            }

            return new Paciente(this);
        }
    }
}