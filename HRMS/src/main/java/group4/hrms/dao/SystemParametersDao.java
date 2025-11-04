package group4.hrms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group4.hrms.util.DatabaseUtil;

/**
 * DAO for SystemParameters table
 * Handles configuration data stored in namespace/key/value JSON structure
 */
public class SystemParametersDao {
    private static final Logger logger = LoggerFactory.getLogger(SystemParametersDao.class);

    private static final String SELECT_BY_NAMESPACE_KEY = """
            SELECT value_json
            FROM system_parameters
            WHERE scope_type = 'GLOBAL' AND scope_id IS NULL AND namespace = ? AND param_key = ?
            """;

    private static final String SELECT_BY_NAMESPACE = """
            SELECT param_key, value_json
            FROM system_parameters
            WHERE scope_type = 'GLOBAL' AND scope_id IS NULL AND namespace = ?
            """;

    private static final String INSERT_OR_UPDATE = """
            INSERT INTO system_parameters (scope_type, scope_id, namespace, param_key, value_json, description, updated_at)
            VALUES ('GLOBAL', NULL, ?, ?, ?, ?, NOW())
            ON DUPLICATE KEY UPDATE
                value_json = VALUES(value_json),
                description = VALUES(description),
                updated_at = NOW()
            """;

    /**
     * Get parameter value by namespace and key
     *
     * @param namespace Parameter namespace
     * @param key Parameter key
     * @return Parameter value as JSON string, or empty if not found
     */
    public Optional<String> getParameter(String namespace, String key) {
        if (namespace == null || key == null) {
            return Optional.empty();
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_NAMESPACE_KEY)) {

            ps.setString(1, namespace);
            ps.setString(2, key);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String value = rs.getString("value_json");
                    logger.debug("Found parameter {}.{}: {}", namespace, key, value);
                    return Optional.of(value);
                }
            }

            logger.debug("Parameter not found: {}.{}", namespace, key);
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error getting parameter {}.{}: {}", namespace, key, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Get all parameters for a namespace
     *
     * @param namespace Parameter namespace
     * @return Map of key-value pairs for the namespace
     */
    public Map<String, String> getParametersByNamespace(String namespace) {
        Map<String, String> parameters = new HashMap<>();

        if (namespace == null) {
            return parameters;
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_NAMESPACE)) {

            ps.setString(1, namespace);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String key = rs.getString("param_key");
                    String value = rs.getString("value_json");
                    parameters.put(key, value);
                }
            }

            logger.debug("Found {} parameters for namespace: {}", parameters.size(), namespace);
            return parameters;

        } catch (SQLException e) {
            logger.error("Error getting parameters for namespace {}: {}", namespace, e.getMessage(), e);
            return parameters;
        }
    }

    /**
     * Set parameter value
     *
     * @param namespace Parameter namespace
     * @param key Parameter key
     * @param value Parameter value (JSON string)
     * @param description Optional description
     * @return true if successful, false otherwise
     */
    public boolean setParameter(String namespace, String key, String value, String description) {
        if (namespace == null || key == null || value == null) {
            logger.warn("Invalid parameters for setParameter: namespace={}, key={}, value={}",
                       namespace, key, value);
            return false;
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_OR_UPDATE)) {

            ps.setString(1, namespace);
            ps.setString(2, key);
            ps.setString(3, value);
            ps.setString(4, description);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Successfully set parameter {}.{}", namespace, key);
                return true;
            }

            return false;

        } catch (SQLException e){
            logger.error("Error setting parameter {}.{}: {}", namespace, key, e.getMessage(), e);
            return false;
        }
    }
}