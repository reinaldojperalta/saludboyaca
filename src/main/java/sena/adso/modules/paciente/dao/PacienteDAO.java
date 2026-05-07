package sena.adso.modules.paciente.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import sena.adso.core.dao.GenericDAO;
import sena.adso.core.exception.DataAccessException;
import sena.adso.modules.paciente.model.Paciente;

/**
 * DAO para la entidad Paciente.
 */
public class PacienteDAO extends GenericDAO<Paciente, Integer> {

    @Override
    protected String getTableName() {
        return "pacientes";
    }

    @Override
    protected String getColumnNames() {
        return "nombres, apellidos, documento, fecha_nacimiento, telefono, email, eps, vereda_barrio";
    }

    @Override
    protected Paciente mapRow(ResultSet rs) throws SQLException {
        return new Paciente.Builder()
                .id(rs.getInt("id"))
                .nombres(rs.getString("nombres"))
                .apellidos(rs.getString("apellidos"))
                .documento(rs.getString("documento"))
                .fechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate())
                .telefono(rs.getString("telefono"))
                .email(rs.getString("email"))
                .eps(rs.getString("eps"))
                .veredaBarrio(rs.getString("vereda_barrio"))
                .build();
    }

    @Override
    protected void prepareInsert(PreparedStatement ps, Paciente p) throws SQLException {
        ps.setString(1, p.getNombres());
        ps.setString(2, p.getApellidos());
        ps.setString(3, p.getDocumento());
        ps.setDate(4, Date.valueOf(p.getFechaNacimiento()));
        ps.setString(5, p.getTelefono());
        ps.setString(6, p.getEmail());
        ps.setString(7, p.getEps());
        ps.setString(8, p.getVeredaBarrio());
    }

    @Override
    protected void prepareUpdate(PreparedStatement ps, Paciente p) throws SQLException {
        ps.setString(1, p.getNombres());
        ps.setString(2, p.getApellidos());
        ps.setString(3, p.getDocumento());
        ps.setDate(4, Date.valueOf(p.getFechaNacimiento()));
        ps.setString(5, p.getTelefono());
        ps.setString(6, p.getEmail());
        ps.setString(7, p.getEps());
        ps.setString(8, p.getVeredaBarrio());
    }

    // ============================================================
    // MÉTODOS ESPECÍFICOS
    // ============================================================

    /**
     * Busca paciente por número de documento.
     */
    public Optional<Paciente> buscarPorDocumento(String documento) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE documento = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, documento);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("SELECT_BY_DOCUMENTO", getTableName(),
                    "documento=" + documento, e);
        }

        return Optional.empty();
    }
}