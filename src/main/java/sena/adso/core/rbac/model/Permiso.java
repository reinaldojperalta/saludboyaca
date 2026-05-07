package sena.adso.core.rbac.model;

import java.util.Objects;

import sena.adso.core.model.Identifiable;

/**
 * DTO para la entidad Permiso.
 * 
 * Representa una acción específica en el sistema usando el formato
 * recurso:accion.
 * Ejemplos: "cita:crear", "paciente:eliminar", "dashboard:ver"
 */
public class Permiso implements Identifiable<Integer> {

    private Integer id;
    private String permissionKey;
    private String description;

    public Permiso() {
    }

    public Permiso(Integer id, String permissionKey, String description) {
        this.id = id;
        this.permissionKey = permissionKey;
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

    public String getPermissionKey() {
        return permissionKey;
    }

    public void setPermissionKey(String permissionKey) {
        this.permissionKey = permissionKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // ============================================================
    // EQUALS Y HASHCODE (basado en permissionKey, que es UNIQUE)
    // ============================================================

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Permiso permiso = (Permiso) o;
        return Objects.equals(permissionKey, permiso.permissionKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionKey);
    }

    @Override
    public String toString() {
        return "Permiso{" +
                "permissionKey='" + permissionKey + '\'' +
                '}';
    }
}