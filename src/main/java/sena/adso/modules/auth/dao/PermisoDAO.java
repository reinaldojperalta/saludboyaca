package sena.adso.modules.auth.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import sena.adso.core.dao.GenericDAO;
import sena.adso.core.rbac.model.Permiso;

public class PermisoDAO extends GenericDAO<Permiso, Integer> {

    @Override
    protected String getTableName() {
        return "permissions";
    }

    @Override
    protected String getColumnNames() {
        return "permission_key, description";
    }

    @Override
    protected Permiso mapRow(ResultSet rs) throws SQLException {
        return new Permiso(
                rs.getInt("id"),
                rs.getString("permission_key"),
                rs.getString("description"));
    }

    @Override
    protected void prepareInsert(PreparedStatement ps, Permiso permiso) throws SQLException {
        ps.setString(1, permiso.getPermissionKey());
        ps.setString(2, permiso.getDescription());
    }

    @Override
    protected void prepareUpdate(PreparedStatement ps, Permiso permiso) throws SQLException {
        ps.setString(1, permiso.getPermissionKey());
        ps.setString(2, permiso.getDescription());
    }
}