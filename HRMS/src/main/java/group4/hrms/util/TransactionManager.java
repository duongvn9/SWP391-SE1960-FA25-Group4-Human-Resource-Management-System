package group4.hrms.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Transaction Manager Utility
 * Provides transaction management for service layer operations
 */
public class TransactionManager {
    private static final Logger logger = Logger.getLogger(TransactionManager.class.getName());

    /**
     * Execute operation within a transaction
     * Automatically commits on success, rolls back on error
     *
     * @param operation The operation to execute
     * @param <T> Return type
     * @return Result of the operation
     * @throws SQLException if database error occurs
     */
    public static <T> T executeInTransaction(TransactionalOperation<T> operation) throws SQLException {
        Connection conn = null;
        boolean originalAutoCommit = true;

        try {
            // Get connection and disable auto-commit
            conn = DatabaseUtil.getConnection();
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            logger.fine("Transaction started");

            // Execute the operation
            T result = operation.execute(conn);

            // Commit if successful
            conn.commit();
            logger.fine("Transaction committed successfully");

            return result;

        } catch (Exception e) {
            // Rollback on any error
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.warning("Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    logger.log(Level.SEVERE, "Failed to rollback transaction", rollbackEx);
                }
            }

            // Re-throw the exception
            if (e instanceof SQLException) {
                throw (SQLException) e;
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException("Transaction failed", e);
            }

        } finally {
            // Restore auto-commit and close connection
            if (conn != null) {
                try {
                    conn.setAutoCommit(originalAutoCommit);
                    conn.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Failed to close connection", e);
                }
            }
        }
    }

    /**
     * Execute operation within a transaction (void return)
     *
     * @param operation The operation to execute
     * @throws SQLException if database error occurs
     */
    public static void executeInTransaction(VoidTransactionalOperation operation) throws SQLException {
        executeInTransaction(conn -> {
            operation.execute(conn);
            return null;
        });
    }

    /**
     * Functional interface for transactional operations with return value
     */
    @FunctionalInterface
    public interface TransactionalOperation<T> {
        T execute(Connection conn) throws Exception;
    }

    /**
     * Functional interface for transactional operations without return value
     */
    @FunctionalInterface
    public interface VoidTransactionalOperation {
        void execute(Connection conn) throws Exception;
    }
}
