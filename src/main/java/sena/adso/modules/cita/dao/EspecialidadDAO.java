package sena.adso.modules.cita.dao;

import sena.adso.core.dao.GenericDAO;
import sena.adso.modules.cita.model.Especialidad;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO para Especialidad médica.
 */
public class EspecialidadDAO extends GenericDAO<Especialidad, Integer> {
    
    @Override
    protected String getTableName() {
        return "especialidades";
    }
    
    @Override
    protected String getColumnNames() {
        return "nombre, descripcion";
    }
    
    @Override
    protected Especialidad mapRow(ResultSet rs) throws SQLException {
        return new Especialidad(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("descripcion")
        );
    }
    
    @Override
    protected void prepareInsert(PreparedStatement ps, Especialidad e) throws SQLException {
        ps.setString(1, e.getNombre());
        ps.setString(2, e.getDescription());
    }
    
    @Override
    protected void prepareUpdate(PreparedStatement ps, Especialidad e) throws SQLException {
        ps.setString(1, e.getNombre());
        ps.setString(2, e.getDescription());
    }
}