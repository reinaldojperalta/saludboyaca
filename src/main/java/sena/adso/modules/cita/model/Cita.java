package sena.adso.modules.cita.model;

import java.time.LocalDate;
import java.time.LocalTime;

import sena.adso.core.model.Identifiable;

/**
 * Entidad central del sistema: Cita médica.
 * 
 * Representa una programación de atención médica entre un paciente y un médico.
 */
public class Cita implements Identifiable<Integer> {

    private Integer id;
    private Integer idPaciente;
    private Integer idMedico;
    private Integer idEspecialidad;
    private LocalDate fechaCita;
    private LocalTime horaCita;
    private String motivo;
    private Estado estado;
    private String observaciones;
    private Integer idRegistradoPor;

    // Campos transient (no persistidos, solo para visualización)
    private String nombrePaciente;
    private String nombreMedico;
    private String nombreEspecialidad;

    public enum Estado {
        PROGRAMADA, CONFIRMADA, ATENDIDA, CANCELADA
    }

    public Cita() {
        this.estado = Estado.PROGRAMADA;
    }

    // ============================================================
    // BUILDER
    // ============================================================

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Cita cita = new Cita();

        public Builder id(Integer id) {
            cita.id = id;
            return this;
        }

        public Builder idPaciente(Integer idPaciente) {
            cita.idPaciente = idPaciente;
            return this;
        }

        public Builder idMedico(Integer idMedico) {
            cita.idMedico = idMedico;
            return this;
        }

        public Builder idEspecialidad(Integer idEspecialidad) {
            cita.idEspecialidad = idEspecialidad;
            return this;
        }

        public Builder fechaCita(LocalDate fechaCita) {
            cita.fechaCita = fechaCita;
            return this;
        }

        public Builder horaCita(LocalTime horaCita) {
            cita.horaCita = horaCita;
            return this;
        }

        public Builder motivo(String motivo) {
            cita.motivo = motivo;
            return this;
        }

        public Builder estado(Estado estado) {
            cita.estado = estado;
            return this;
        }

        public Builder observaciones(String observaciones) {
            cita.observaciones = observaciones;
            return this;
        }

        public Builder idRegistradoPor(Integer idRegistradoPor) {
            cita.idRegistradoPor = idRegistradoPor;
            return this;
        }

        public Cita build() {
            if (cita.idPaciente == null) {
                throw new IllegalStateException("idPaciente es requerido");
            }
            if (cita.idMedico == null) {
                throw new IllegalStateException("idMedico es requerido");
            }
            if (cita.fechaCita == null) {
                throw new IllegalStateException("fechaCita es requerida");
            }
            if (cita.horaCita == null) {
                throw new IllegalStateException("horaCita es requerida");
            }
            return cita;
        }
    }

    // ============================================================
    // GETTERS Y SETTERS
    // ============================================================

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Integer idPaciente) {
        this.idPaciente = idPaciente;
    }

    public Integer getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Integer idMedico) {
        this.idMedico = idMedico;
    }

    public Integer getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(Integer idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public LocalDate getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(LocalDate fechaCita) {
        this.fechaCita = fechaCita;
    }

    public LocalTime getHoraCita() {
        return horaCita;
    }

    public void setHoraCita(LocalTime horaCita) {
        this.horaCita = horaCita;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Integer getIdRegistradoPor() {
        return idRegistradoPor;
    }

    public void setIdRegistradoPor(Integer idRegistradoPor) {
        this.idRegistradoPor = idRegistradoPor;
    }

    // ============================================================
    // TRANSIENT (solo para visualización)
    // ============================================================

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public String getNombreEspecialidad() {
        return nombreEspecialidad;
    }

    public void setNombreEspecialidad(String nombreEspecialidad) {
        this.nombreEspecialidad = nombreEspecialidad;
    }

    // ============================================================
    // MÉTODOS DE CONVENIENCIA
    // ============================================================

    /**
     * Verifica si la cita está en el futuro.
     */
    public boolean isFutura() {
        return fechaCita.isAfter(LocalDate.now()) ||
                (fechaCita.isEqual(LocalDate.now()) && horaCita.isAfter(LocalTime.now()));
    }

    /**
     * Verifica si la cita puede ser cancelada.
     */
    public boolean puedeCancelarse() {
        return estado == Estado.PROGRAMADA || estado == Estado.CONFIRMADA;
    }

    @Override
    public String toString() {
        return "Cita{" +
                "id=" + id +
                ", paciente=" + idPaciente +
                ", medico=" + idMedico +
                ", fecha=" + fechaCita +
                ", hora=" + horaCita +
                ", estado=" + estado +
                '}';
    }
}