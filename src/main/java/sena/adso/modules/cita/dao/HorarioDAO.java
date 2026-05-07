package sena.adso.modules.cita.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import sena.adso.core.dao.GenericDAO;
import sena.adso.core.exception.DataAccessException;
import sena.adso.modules.cita.model.Horario;

/**
 * DAO para Horario de atención médica.
 */
public class HorarioDAO extends GenericDAO<Horario, Integer> {
    
    @Override
    protected String getTableName() {
        return "horarios";
    }
    
    @Override
    protected String getColumnNames() {
        return "id_medico, dia_semana, hora_inicio, hora_fin, max_citas";
    }
    
    @Override
    protected Horario mapRow(ResultSet rs) throws SQLException {
        return new Horario(
            rs.getInt("id"),
            rs.getInt("id_medico"),
            rs.getInt("dia_semana"),
            rs.getTime("hora_inicio").toLocalTime(),
            rs.getTime("hora_fin").toLocalTime(),
            rs.getInt("max_citas")
        );
    }
    
    @Override
    protected void prepareInsert(PreparedStatement ps, Horario h) throws SQLException {
        ps.setInt(1, h.getIdMedico());
        ps.setInt(2, h.getDiaSemana());
        ps.setTime(3, Time.valueOf(h.getHoraInicio()));
        ps.setTime(4, Time.valueOf(h.getHoraFin()));
        ps.setInt(5, h.getMaxCitas());
    }
    
    @Override
    protected void prepareUpdate(PreparedStatement ps, Horario h) throws SQLException {
        ps.setInt(1, h.getIdMedico());
        ps.setInt(2, h.getDiaSemana());
        ps.setTime(3, Time.valueOf(h.getHoraInicio()));
        ps.setTime(4, Time.valueOf(h.getHoraFin()));
        ps.setInt(5, h.getMaxCitas());
    }
    
    // ============================================================
    // MÉTODOS ESPECÍFICOS
    // ============================================================
    
    /**
     * Lista horarios de un médico específico.
     */
    public List<Horario> listarPorMedico(Integer idMedico) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id_medico = ? ORDER BY dia_semana, hora_inicio";
        List<Horario> horarios = new ArrayList<>();
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    horarios.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("SELECT_BY_MEDICO", getTableName(),
                "idMedico=" + idMedico, e);
        }
        
        return horarios;
    }
    
    /**
     * Lista horarios disponibles para una especialidad en un día específico.
     */
    public List<Horario> listarPorEspecialidadYDia(Integer idEspecialidad, Integer diaSemana) {
        String sql = "SELECT h.* FROM " + getTableName() + " h " +
                    "JOIN usuarios u ON h.id_medico = u.id " +
                    "WHERE u.id_especialidad = ? AND h.dia_semana = ? " +
                    "ORDER BY h.hora_inicio";
        List<Horario> horarios = new ArrayList<>();
        
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idEspecialidad);
            ps.setInt(2, diaSemana);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    horarios.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("SELECT_BY_ESP_DIA", getTableName(), e);
        }
        
        return horarios;
    }
}