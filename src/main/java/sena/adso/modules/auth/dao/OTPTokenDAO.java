package sena.adso.modules.auth.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import sena.adso.core.exception.DataAccessException;

/**
 * DAO para la entidad OTP Token.
 * 
 * No hereda de GenericDAO porque otp_tokens no sigue el patrón CRUD estándar.
 * Tiene operaciones específicas: crear, validar, marcar usado, limpiar
 * expirados.
 */
public class OTPTokenDAO {

    /**
     * Inserta un nuevo token OTP.
     */
    public void insertar(int idUsuario, String codigo, LocalDateTime expiraEn) {
        String sql = "INSERT INTO otp_tokens (id_usuario, codigo, expira_en) VALUES (?, ?, ?)";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, codigo);
            ps.setTimestamp(3, Timestamp.valueOf(expiraEn));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("INSERT", "otp_tokens",
                    "user=" + idUsuario, e);
        }
    }

    /**
     * Valida un token OTP.
     * 
     * @return true si el código es válido, no ha expirado y no ha sido usado
     */
    public boolean validar(int idUsuario, String codigo) {
        String sql = "SELECT id FROM otp_tokens " +
                "WHERE id_usuario = ? AND codigo = ? " +
                "AND expira_en > NOW() AND usado = 0";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("VALIDATE", "otp_tokens",
                    "user=" + idUsuario, e);
        }
    }

    /**
     * Marca un token como usado.
     */
    public void marcarUsado(int idUsuario, String codigo) {
        String sql = "UPDATE otp_tokens SET usado = 1 " +
                "WHERE id_usuario = ? AND codigo = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, codigo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("UPDATE", "otp_tokens",
                    "user=" + idUsuario, e);
        }
    }

    /**
     * Elimina tokens expirados y usados (mantenimiento).
     */
    public int limpiarExpirados() {
        String sql = "DELETE FROM otp_tokens WHERE expira_en < NOW() - INTERVAL 7 DAY OR usado = 1";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("DELETE", "otp_tokens", e);
        }
    }

    /**
     * Cuenta intentos fallidos recientes de un usuario.
     */
    public long contarIntentosFallidos(int idUsuario, int minutos) {
        String sql = "SELECT COUNT(*) FROM otp_tokens " +
                "WHERE id_usuario = ? AND usado = 0 " +
                "AND expira_en > NOW() - INTERVAL ? MINUTE";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, minutos);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("COUNT", "otp_tokens", e);
        }

        return 0;
    }

    private java.sql.Connection getConnection() {
        return sena.adso.core.dao.TransactionManager.get();
    }

    public void eliminarPorUsuario(int idUsuario) {
        String sql = "DELETE FROM otp_tokens WHERE id_usuario = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("DELETE_BY_USUARIO", "otp_tokens",
                    "id_usuario=" + idUsuario, e);
        }
    }
}