package sena.adso.modules.cita.model;

import java.time.LocalTime;
import java.util.Objects;

import sena.adso.core.model.Identifiable;

/**
 * Horario de atención de un médico.
 * 
 * Define el día de la semana, hora de inicio, hora de fin y máximo de citas.
 */
public class Horario implements Identifiable<Integer> {

    private Integer id;
    private Integer idMedico;
    private Integer diaSemana; // 1=Lunes, 2=Martes, ..., 5=Viernes
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer maxCitas;

    public Horario() {
    }

    public Horario(Integer id, Integer idMedico, Integer diaSemana,
            LocalTime horaInicio, LocalTime horaFin, Integer maxCitas) {
        this.id = id;
        this.idMedico = idMedico;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.maxCitas = maxCitas;
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

    public Integer getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Integer idMedico) {
        this.idMedico = idMedico;
    }

    public Integer getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(Integer diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public Integer getMaxCitas() {
        return maxCitas;
    }

    public void setMaxCitas(Integer maxCitas) {
        this.maxCitas = maxCitas;
    }

    // ============================================================
    // MÉTODOS DE CONVENIENCIA
    // ============================================================

    /**
     * Nombre del día de la semana en español.
     */
    public String getNombreDia() {
        return switch (diaSemana) {
            case 1 -> "Lunes";
            case 2 -> "Martes";
            case 3 -> "Miércoles";
            case 4 -> "Jueves";
            case 5 -> "Viernes";
            default -> "Desconocido";
        };
    }

    /**
     * Duración de la franja horaria en minutos.
     */
    public long getDuracionMinutos() {
        return java.time.Duration.between(horaInicio, horaFin).toMinutes();
    }

    // ============================================================
    // EQUALS Y HASHCODE
    // ============================================================

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Horario horario = (Horario) o;
        return Objects.equals(id, horario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Horario{" +
                "id=" + id +
                ", dia=" + getNombreDia() +
                ", " + horaInicio + "-" + horaFin +
                '}';
    }
}