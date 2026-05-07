package sena.adso.modules.cita.model;

import java.util.Objects;

import sena.adso.core.model.Identifiable;

/**
 * Especialidad médica disponible en el centro de salud.
 * 
 * Ej: Medicina General, Cardiología, Pediatría.
 * No usa Builder porque es una entidad simple de configuración.
 */
public class Especialidad implements Identifiable<Integer> {

    private Integer id;
    private String nombre;
    private String descripcion;

    public Especialidad() {
    }

    public Especialidad(Integer id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescription() {
        return descripcion;
    }

    public void setDescription(String descripcion) {
        this.descripcion = descripcion;
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
        Especialidad that = (Especialidad) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Especialidad{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}