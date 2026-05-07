package sena.adso.core.rbac.model;

import java.util.Objects;

import sena.adso.core.model.Identifiable;

/**
 * DTO para la entidad Rol.
 * 
 * Representa un perfil de usuario en el sistema (MEDICO, ENFERMERO,
 * RECEPCIONISTA).
 * Es una entidad de configuración, no requiere Builder.
 */
public class Rol implements Identifiable<Integer> {

    private Integer id;
    private String name;
    private String description;

    public Rol() {
    }

    public Rol(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // ============================================================
    // GETTERS Y SETTERS
    // ============================================================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // ============================================================
    // EQUALS Y HASHCODE (basado en name, que es UNIQUE)
    // ============================================================

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Rol rol = (Rol) o;
        return Objects.equals(name, rol.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Rol{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}