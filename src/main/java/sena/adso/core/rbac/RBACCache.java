package sena.adso.core.rbac;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import sena.adso.core.rbac.model.Permiso;
import sena.adso.core.rbac.model.Rol;

/**
 * Cache en memoria del sistema RBAC.
 * 
 * Se carga UNA VEZ al iniciar la aplicación (vía AppInitializer) y se
 * almacena en ServletContext para acceso global.
 * 
 * Estructura:
 * - Map<String, Set<String>>: rolName -> Set de permissionKeys
 * 
 * Thread-safe: usa ConcurrentHashMap y Collections.unmodifiableSet.
 */
public final class RBACCache {

    private final Map<String, Set<String>> rolePermissions;
    private final Map<String, Rol> rolesByName;

    public RBACCache(List<Rol> roles, List<Permiso> permisos, List<RolePermissionMapping> mappings) {
        this.rolePermissions = new ConcurrentHashMap<>();
        this.rolesByName = new ConcurrentHashMap<>();

        // Indexar roles por nombre
        for (Rol rol : roles) {
            rolesByName.put(rol.getName(), rol);
        }

        // Construir mapa de rol -> permisos
        for (RolePermissionMapping mapping : mappings) {
            String rolName = mapping.getRolName();
            String permKey = mapping.getPermissionKey();

            rolePermissions
                    .computeIfAbsent(rolName, k -> ConcurrentHashMap.newKeySet())
                    .add(permKey);
        }
    }

    /**
     * Verifica si un rol tiene un permiso específico.
     * 
     * @param rolName       Nombre del rol (ej: "MEDICO")
     * @param permissionKey Permiso a verificar (ej: "cita:crear")
     * @return true si el rol tiene el permiso
     */
    public boolean hasPermission(String rolName, String permissionKey) {
        if (rolName == null || permissionKey == null)
            return false;

        Set<String> permissions = rolePermissions.get(rolName);
        return permissions != null && permissions.contains(permissionKey);
    }

    /**
     * Obtiene todos los permisos de un rol.
     * 
     * @return Set inmutable (vacío si el rol no existe)
     */
    public Set<String> getPermissions(String rolName) {
        Set<String> perms = rolePermissions.get(rolName);
        return perms != null ? Collections.unmodifiableSet(perms) : Collections.emptySet();
    }

    /**
     * Verifica si un rol existe en el sistema.
     */
    public boolean hasRole(String rolName) {
        return rolesByName.containsKey(rolName);
    }

    /**
     * Obtiene un rol por su nombre.
     */
    public Optional<Rol> getRol(String rolName) {
        return Optional.ofNullable(rolesByName.get(rolName));
    }

    /**
     * Obtiene todos los nombres de roles cargados.
     */
    public Set<String> getAllRoleNames() {
        return Collections.unmodifiableSet(rolesByName.keySet());
    }

    // ============================================================
    // CLASE INTERNA: Mapeo rol-permiso
    // ============================================================

    /**
     * DTO simple para el mapeo entre rol y permiso.
     * Se construye desde el DAO al cargar la cache.
     */
    public static class RolePermissionMapping {
        private final String rolName;
        private final String permissionKey;

        public RolePermissionMapping(String rolName, String permissionKey) {
            this.rolName = rolName;
            this.permissionKey = permissionKey;
        }

        public String getRolName() {
            return rolName;
        }

        public String getPermissionKey() {
            return permissionKey;
        }
    }
}