package sena.adso.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import sena.adso.core.exception.DataAccessException;
import sena.adso.core.model.Identifiable;

/**
 * DAO genérico abstracto que proporciona operaciones CRUD básicas.
 * 
 * Cada DAO concreto debe implementar los métodos abstractos que definen
 * la estructura de la tabla y el mapeo de filas a objetos.
 * 
 * La conexión se obtiene automáticamente del TransactionManager (ThreadLocal),
 * por lo que NO se pasa Connection por parámetro.
 * 
 * @param <T>  Tipo de la entidad (debe implementar Identifiable<ID>)
 * @param <ID> Tipo del identificador (Integer, Long, String, etc.)
 */
public abstract class GenericDAO<T extends Identifiable<ID>, ID> {

    /**
     * Obtiene la conexión del hilo actual vía TransactionManager.
     * Falla rápido si no hay transacción activa.
     */
    protected Connection getConnection() {
        return TransactionManager.get();
    }

    // ============================================================
    // MÉTODOS CRUD CONCRETOS (usando métodos abstractos)
    // ============================================================

    /**
     * Busca una entidad por su ID.
     */
    public Optional<T> findById(ID id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            setIdParameter(ps, 1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("SELECT_BY_ID", getTableName(),
                    "id=" + id, e);
        }
        return Optional.empty();
    }

    /**
     * Obtiene todas las entidades de la tabla.
     */
    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTableName();
        List<T> results = new ArrayList<>();
        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("SELECT_ALL", getTableName(), e);
        }
        return results;
    }

    /**
     * Inserta una nueva entidad y devuelve el ID generado.
     */
    public ID insert(T entity) {
        String sql = buildInsertSql();
        try (PreparedStatement ps = getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {

            prepareInsert(ps, entity);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ID generatedId = extractGeneratedKey(rs);
                    entity.setId(generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("INSERT", getTableName(), e);
        }
        throw new DataAccessException("INSERT", getTableName(),
                "No se pudo obtener el ID generado");
    }

    /**
     * Actualiza una entidad existente.
     */
    public void update(T entity) {
        String sql = buildUpdateSql();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            prepareUpdate(ps, entity);
            setIdParameter(ps, getUpdateIdParameterIndex(), entity.getId());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DataAccessException("UPDATE", getTableName(),
                        "No se encontró registro con id=" + entity.getId());
            }
        } catch (SQLException e) {
            throw new DataAccessException("UPDATE", getTableName(), e);
        }
    }

    /**
     * Elimina una entidad por su ID.
     */
    public void delete(ID id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            setIdParameter(ps, 1, id);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DataAccessException("DELETE", getTableName(),
                        "No se encontró registro con id=" + id);
            }
        } catch (SQLException e) {
            throw new DataAccessException("DELETE", getTableName(), e);
        }
    }

    /**
     * Cuenta el total de registros.
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM " + getTableName();
        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("COUNT", getTableName(), e);
        }
        return 0;
    }

    // ============================================================
    // MÉTODOS ABSTRACTOS (cada DAO concreto los implementa)
    // ============================================================

    /**
     * Nombre de la tabla en la base de datos.
     * Ej: "usuarios", "pacientes", "citas"
     */
    protected abstract String getTableName();

    /**
     * Lista de columnas para INSERT (sin ID).
     * Ej: "nombres, apellidos, documento, email"
     */
    protected abstract String getColumnNames();

    /**
     * Mapea una fila del ResultSet a un objeto DTO.
     */
    protected abstract T mapRow(ResultSet rs) throws SQLException;

    /**
     * Setea los parámetros del PreparedStatement para INSERT.
     * El orden debe coincidir con getColumnNames().
     */
    protected abstract void prepareInsert(PreparedStatement ps, T entity) throws SQLException;

    /**
     * Setea los parámetros del PreparedStatement para UPDATE.
     * El ID se setea después, vía setIdParameter().
     */
    protected abstract void prepareUpdate(PreparedStatement ps, T entity) throws SQLException;

    // ============================================================
    // MÉTODOS AUXILIARES (pueden sobreescribirse si es necesario)
    // ============================================================

    /**
     * Setea el parámetro ID en un PreparedStatement.
     * Por defecto asume Integer. Sobrescribir si ID es otro tipo.
     */
    protected void setIdParameter(PreparedStatement ps, int index, ID id) throws SQLException {
        if (id instanceof Integer) {
            ps.setInt(index, (Integer) id);
        } else if (id instanceof Long) {
            ps.setLong(index, (Long) id);
        } else if (id instanceof String) {
            ps.setString(index, (String) id);
        } else {
            ps.setObject(index, id);
        }
    }

    /**
     * Extrae la clave generada del ResultSet.
     * Por defecto asume Integer. Sobrescribir si es necesario.
     */
    @SuppressWarnings("unchecked")
    protected ID extractGeneratedKey(ResultSet rs) throws SQLException {
        return (ID) Integer.valueOf(rs.getInt(1));
    }

    /**
     * Construye el SQL de INSERT.
     */
    private String buildInsertSql() {
        String columns = getColumnNames();
        String placeholders = columns.replaceAll("[^,]+", "?");
        return "INSERT INTO " + getTableName() + " (" + columns + ") VALUES (" + placeholders + ")";
    }

    /**
     * Construye el SQL de UPDATE.
     */
    private String buildUpdateSql() {
        String[] columns = getColumnNames().split(",\\s*");
        StringBuilder setClause = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            if (i > 0)
                setClause.append(", ");
            setClause.append(columns[i]).append(" = ?");
        }
        return "UPDATE " + getTableName() + " SET " + setClause + " WHERE id = ?";
    }

    /**
     * Índice del parámetro ID en el UPDATE (después de todas las columnas).
     */
    private int getUpdateIdParameterIndex() {
        return getColumnNames().split(",\\s*").length + 1;
    }

    // En GenericDAO.java
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM " + getTableName();
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("COUNT_ALL", getTableName(), e);
        }
        return 0;
    }
}