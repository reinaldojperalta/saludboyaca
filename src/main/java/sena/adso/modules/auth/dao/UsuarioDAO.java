package sena.adso.modules.auth.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import sena.adso.core.dao.GenericDAO;
import sena.adso.core.exception.DataAccessException;
import sena.adso.core.rbac.model.Rol;
import sena.adso.modules.auth.model.Usuario;

/**
 * DAO para la entidad Usuario.
 * 
 * Proporciona operaciones CRUD heredadas de GenericDAO más métodos
 * específicos como validarLogin() y buscarPorUsername().
 */
public class UsuarioDAO extends GenericDAO<Usuario, Integer> {

    @Override
    protected String getTableName() {
        return "usuarios";
    }

    @Override
    protected String getColumnNames() {
        return "nombres, apellidos, documento, email, username, password, id_especialidad, lang_preferido";
    }

    @Override
    protected Usuario mapRow(ResultSet rs) throws SQLException {
        return new Usuario.Builder()
                .id(rs.getInt("id"))
                .nombres(rs.getString("nombres"))
                .apellidos(rs.getString("apellidos"))
                .documento(rs.getString("documento"))
                .email(rs.getString("email"))
                .username(rs.getString("username"))
                .password(rs.getString("password"))
                .idEspecialidad(rs.getObject("id_especialidad", Integer.class))
                .langPreferido(rs.getString("lang_preferido"))
                .build();
    }

    @Override
    protected void prepareInsert(PreparedStatement ps, Usuario u) throws SQLException {
        ps.setString(1, u.getNombres());
        ps.setString(2, u.getApellidos());
        ps.setString(3, u.getDocumento());
        ps.setString(4, u.getEmail());
        ps.setString(5, u.getUsername());
        ps.setString(6, u.getPassword());
        ps.setObject(7, u.getIdEspecialidad());
        ps.setString(8, u.getLangPreferido());
    }

    @Override
    protected void prepareUpdate(PreparedStatement ps, Usuario u) throws SQLException {
        ps.setString(1, u.getNombres());
        ps.setString(2, u.getApellidos());
        ps.setString(3, u.getDocumento());
        ps.setString(4, u.getEmail());
        ps.setString(5, u.getUsername());
        ps.setString(6, u.getPassword());
        ps.setObject(7, u.getIdEspecialidad());
        ps.setString(8, u.getLangPreferido());
    }

    // ============================================================
    // MÉTODOS ESPECÍFICOS
    // ============================================================

    /**
     * Valida credenciales de login y carga los roles del usuario.
     */
    public Optional<Usuario> validarLogin(String username, String password) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE username = ? AND password = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario base = mapRow(rs);

                    // FIX: Cargar roles del usuario
                    Set<Rol> roles = cargarRoles(base.getId());

                    // Reconstruir usuario con roles
                    Usuario usuario = new Usuario.Builder()
                            .id(base.getId())
                            .nombres(base.getNombres())
                            .apellidos(base.getApellidos())
                            .documento(base.getDocumento())
                            .email(base.getEmail())
                            .username(base.getUsername())
                            .password(base.getPassword())
                            .idEspecialidad(base.getIdEspecialidad())
                            .langPreferido(base.getLangPreferido())
                            .roles(roles)
                            .build();

                    return Optional.of(usuario);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("LOGIN", getTableName(),
                    "username=" + username, e);
        }

        return Optional.empty();
    }

    /**
     * Busca un usuario por su username (con roles cargados).
     */
    public Optional<Usuario> buscarPorUsername(String username) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE username = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario base = mapRow(rs);
                    Set<Rol> roles = cargarRoles(base.getId());

                    Usuario usuario = new Usuario.Builder()
                            .id(base.getId())
                            .nombres(base.getNombres())
                            .apellidos(base.getApellidos())
                            .documento(base.getDocumento())
                            .email(base.getEmail())
                            .username(base.getUsername())
                            .password(base.getPassword())
                            .idEspecialidad(base.getIdEspecialidad())
                            .langPreferido(base.getLangPreferido())
                            .roles(roles)
                            .build();

                    return Optional.of(usuario);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("SELECT_BY_USERNAME", getTableName(),
                    "username=" + username, e);
        }

        return Optional.empty();
    }

    /**
     * Busca un usuario por su email.
     */
    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE email = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("SELECT_BY_EMAIL", getTableName(),
                    "email=" + email, e);
        }

        return Optional.empty();
    }

    // ============================================================
    // PRIVADOS
    // ============================================================

    /**
     * Carga los roles de un usuario desde la tabla user_roles.
     * FIX CRÍTICO: Sin esto, Usuario.getRol() retorna null.
     */
    private Set<Rol> cargarRoles(Integer userId) throws SQLException {
        String sql = "SELECT r.id, r.name, r.description " +
                "FROM user_roles ur " +
                "JOIN roles r ON ur.role_id = r.id " +
                "WHERE ur.user_id = ?";

        Set<Rol> roles = new HashSet<>();

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roles.add(new Rol(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description")));
                }
            }
        }

        return roles;
    }
}