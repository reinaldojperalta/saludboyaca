package sena.adso.modules.cita.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import sena.adso.core.dao.GenericDAO;
import sena.adso.core.exception.DataAccessException;
import sena.adso.modules.cita.model.Cita;

/**
 * DAO para Cita médica.
 */
public class CitaDAO extends GenericDAO<Cita, Integer> {

    @Override
    protected String getTableName() {
        return "citas";
    }

    @Override
    protected String getColumnNames() {
        return "id_paciente, id_medico, id_especialidad, fecha_cita, hora_cita, motivo, estado, observaciones, id_registrado_por";
    }

    @Override
    protected Cita mapRow(ResultSet rs) throws SQLException {
        return Cita.builder()
                .id(rs.getInt("id"))
                .idPaciente(rs.getInt("id_paciente"))
                .idMedico(rs.getInt("id_medico"))
                .idEspecialidad(rs.getInt("id_especialidad"))
                .fechaCita(rs.getDate("fecha_cita").toLocalDate())
                .horaCita(rs.getTime("hora_cita").toLocalTime())
                .motivo(rs.getString("motivo"))
                .estado(Cita.Estado.valueOf(rs.getString("estado")))
                .observaciones(rs.getString("observaciones"))
                .idRegistradoPor(rs.getObject("id_registrado_por", Integer.class))
                .build();
    }

    @Override
    protected void prepareInsert(PreparedStatement ps, Cita c) throws SQLException {
        ps.setInt(1, c.getIdPaciente());
        ps.setInt(2, c.getIdMedico());
        ps.setInt(3, c.getIdEspecialidad());
        ps.setDate(4, Date.valueOf(c.getFechaCita()));
        ps.setTime(5, Time.valueOf(c.getHoraCita()));
        ps.setString(6, c.getMotivo());
        ps.setString(7, c.getEstado().name());
        ps.setString(8, c.getObservaciones());
        ps.setObject(9, c.getIdRegistradoPor());
    }

    @Override
    protected void prepareUpdate(PreparedStatement ps, Cita c) throws SQLException {
        ps.setInt(1, c.getIdPaciente());
        ps.setInt(2, c.getIdMedico());
        ps.setInt(3, c.getIdEspecialidad());
        ps.setDate(4, Date.valueOf(c.getFechaCita()));
        ps.setTime(5, Time.valueOf(c.getHoraCita()));
        ps.setString(6, c.getMotivo());
        ps.setString(7, c.getEstado().name());
        ps.setString(8, c.getObservaciones());
        ps.setObject(9, c.getIdRegistradoPor());
    }

    // ============================================================
    // MÉTODOS ESPECÍFICOS
    // ============================================================

    /**
     * Lista citas de un médico en una fecha específica.
     */
    public List<Cita> listarPorMedicoYFecha(Integer idMedico, java.time.LocalDate fecha) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id_medico = ? AND fecha_cita = ? ORDER BY hora_cita";
        List<Cita> citas = new ArrayList<>();

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setDate(2, Date.valueOf(fecha));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    citas.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("SELECT_BY_MEDICO_FECHA", getTableName(), e);
        }

        return citas;
    }

    /**
     * Lista citas de un paciente.
     */
    public List<Cita> listarPorPaciente(Integer idPaciente) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id_paciente = ? ORDER BY fecha_cita DESC, hora_cita";
        List<Cita> citas = new ArrayList<>();

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idPaciente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    citas.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("SELECT_BY_PACIENTE", getTableName(), e);
        }

        return citas;
    }

    /**
     * Cuenta citas de un médico en una fecha y hora específica.
     * Útil para validar disponibilidad.
     */
    public long contarCitasEnFranja(Integer idMedico, java.time.LocalDate fecha,
            java.time.LocalTime horaInicio, java.time.LocalTime horaFin) {
        String sql = "SELECT COUNT(*) FROM " + getTableName() +
                " WHERE id_medico = ? AND fecha_cita = ? " +
                " AND hora_cita >= ? AND hora_cita < ? " +
                " AND estado != 'CANCELADA'";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setTime(3, Time.valueOf(horaInicio));
            ps.setTime(4, Time.valueOf(horaFin));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("COUNT_FRANJA", getTableName(), e);
        }

        return 0;
    }

    /**
     * Actualiza solo el estado de una cita.
     */
    public void cambiarEstado(Integer id, Cita.Estado nuevoEstado) {
        String sql = "UPDATE " + getTableName() + " SET estado = ? WHERE id = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.name());
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("UPDATE_ESTADO", getTableName(),
                    "id=" + id + ", estado=" + nuevoEstado, e);
        }
    }

    /**
     * Lista citas con información completa (JOIN con pacientes, médicos,
     * especialidades).
     */
    public List<Cita> listarCompletas() {
        String sql = "SELECT c.*, " +
                "  p.nombres as pac_nombre, p.apellidos as pac_apellido, " +
                "  m.nombres as med_nombre, m.apellidos as med_apellido, " +
                "  e.nombre as esp_nombre " +
                "FROM citas c " +
                "JOIN pacientes p ON c.id_paciente = p.id " +
                "JOIN usuarios m ON c.id_medico = m.id " +
                "JOIN especialidades e ON c.id_especialidad = e.id " +
                "ORDER BY c.fecha_cita DESC, c.hora_cita";

        List<Cita> citas = new ArrayList<>();

        try (PreparedStatement ps = getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cita cita = mapRow(rs);
                cita.setNombrePaciente(rs.getString("pac_nombre") + " " + rs.getString("pac_apellido"));
                cita.setNombreMedico(rs.getString("med_nombre") + " " + rs.getString("med_apellido"));
                cita.setNombreEspecialidad(rs.getString("esp_nombre"));
                citas.add(cita);
            }
        } catch (SQLException e) {
            throw new DataAccessException("SELECT_COMPLETAS", getTableName(), e);
        }

        return citas;
    }
}