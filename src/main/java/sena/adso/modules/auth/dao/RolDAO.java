package sena.adso.modules.auth.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sena.adso.core.dao.GenericDAO;
import sena.adso.core.rbac.RBACCache;
import sena.adso.core.rbac.model.Rol;

public class RolDAO extends GenericDAO<Rol, Integer> {

    @Override
    protected String getTableName() {
        return "roles";
    }

    @Override
    protected String getColumnNames() {
        return "name, description";
    }

    @Override
    protected Rol mapRow(ResultSet rs) throws SQLException {
        return new Rol(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"));
    }

    @Override
    protected void prepareInsert(PreparedStatement ps, Rol rol) throws SQLException {
        ps.setString(1, rol.getName());
        ps.setString(2, rol.getDescription());
    }

    @Override
    protected void prepareUpdate(PreparedStatement ps, Rol rol) throws SQLException {
        ps.setString(1, rol.getName());
        ps.setString(2, rol.getDescription());
    }

    /**
     * Carga los mapeos rol-permiso para construir RBACCache.
     * No es un método estándar de GenericDAO, es específico de RolDAO.
     */
    public List<RBACCache.RolePermissionMapping> loadRolePermissionMappings() throws SQLException {
        String sql = "SELECT r.name as rol_name, p.permission_key " +
                "FROM role_permissions rp " +
                "JOIN roles r ON rp.role_id = r.id " +
                "JOIN permissions p ON rp.permission_id = p.id";

        List<RBACCache.RolePermissionMapping> mappings = new ArrayList<>();

        try (PreparedStatement ps = getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                mappings.add(new RBACCache.RolePermissionMapping(
                        rs.getString("rol_name"),
                        rs.getString("permission_key")));
            }
        }

        return mappings;
    }
}