package group4.hrms.email.dao;

import group4.hrms.dao.BaseDao;
import group4.hrms.email.model.EmailQueue;
import group4.hrms.email.model.EmailStatus;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class để xử lý các thao tác với bảng email_queue
 * 
 * @author Group4
 */
public class EmailQueueDao extends BaseDao<EmailQueue, Long> {

    @Override
    protected String getTableName() {
        return "email_queue";
    }

    @Override
    protected EmailQueue mapResultSetToEntity(ResultSet rs) throws SQLException {
        EmailQueue queue = new EmailQueue();
        queue.setId(rs.getLong("id"));
        queue.setRecipientEmail(rs.getString("recipient_email"));
        queue.setSubject(rs.getString("subject"));
        queue.setContent(rs.getString("content"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            queue.setStatus(EmailStatus.valueOf(statusStr));
        }

        queue.setRetryCount(rs.getInt("retry_count"));
        queue.setScheduledAt(getLocalDateTime(rs, "scheduled_at"));
        queue.setSentAt(getLocalDateTime(rs, "sent_at"));
        queue.setErrorMessage(rs.getString("error_message"));
        queue.setReferenceId(rs.getString("reference_id"));
        queue.setCreatedAt(getLocalDateTime(rs, "created_at"));

        return queue;
    }

    @Override
    protected void setEntityId(EmailQueue queue, Long id) {
        queue.setId(id);
    }

    @Override
    protected Long getEntityId(EmailQueue queue) {
        return queue.getId();
    }

    @Override
    protected String createInsertSql() {
        return "INSERT INTO email_queue (recipient_email, subject, content, status, retry_count, " +
                "scheduled_at, sent_at, error_message, reference_id, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String createUpdateSql() {
        return "UPDATE email_queue SET recipient_email = ?, subject = ?, content = ?, status = ?, " +
                "retry_count = ?, scheduled_at = ?, sent_at = ?, error_message = ?, reference_id = ? " +
                "WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, EmailQueue queue) throws SQLException {
        stmt.setString(1, queue.getRecipientEmail());
        stmt.setString(2, queue.getSubject());
        stmt.setString(3, queue.getContent());
        stmt.setString(4, queue.getStatus() != null ? queue.getStatus().name() : EmailStatus.PENDING.name());
        stmt.setInt(5, queue.getRetryCount());
        setTimestamp(stmt, 6, queue.getScheduledAt() != null ? queue.getScheduledAt() : LocalDateTime.now());
        setTimestamp(stmt, 7, queue.getSentAt());
        stmt.setString(8, queue.getErrorMessage());
        stmt.setString(9, queue.getReferenceId());
        setTimestamp(stmt, 10, queue.getCreatedAt() != null ? queue.getCreatedAt() : LocalDateTime.now());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, EmailQueue queue) throws SQLException {
        stmt.setString(1, queue.getRecipientEmail());
        stmt.setString(2, queue.getSubject());
        stmt.setString(3, queue.getContent());
        stmt.setString(4, queue.getStatus() != null ? queue.getStatus().name() : null);
        stmt.setInt(5, queue.getRetryCount());
        setTimestamp(stmt, 6, queue.getScheduledAt());
        setTimestamp(stmt, 7, queue.getSentAt());
        stmt.setString(8, queue.getErrorMessage());
        stmt.setString(9, queue.getReferenceId());
        stmt.setLong(10, queue.getId());
    }

    // Business methods

    /**
     * Tìm emails theo status và sắp xếp theo scheduled_at
     */
    public List<EmailQueue> findByStatusOrderByScheduledAt(EmailStatus status) throws SQLException {
        if (status == null) {
            return new ArrayList<>();
        }

        List<EmailQueue> emails = new ArrayList<>();
        String sql = "SELECT * FROM email_queue WHERE status = ? ORDER BY scheduled_at ASC";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emails.add(mapResultSetToEntity(rs));
                }
            }

            return emails;

        } catch (SQLException e) {
            logger.error("Error finding emails by status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm emails ready to send (PENDING or RETRY status và scheduled_at <= now)
     */
    public List<EmailQueue> findReadyToSend() throws SQLException {
        List<EmailQueue> emails = new ArrayList<>();
        String sql = "SELECT * FROM email_queue " +
                "WHERE (status = ? OR status = ?) AND scheduled_at <= ? " +
                "ORDER BY scheduled_at ASC LIMIT 100";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, EmailStatus.PENDING.name());
            stmt.setString(2, EmailStatus.RETRY.name());
            setTimestamp(stmt, 3, LocalDateTime.now());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emails.add(mapResultSetToEntity(rs));
                }
            }

            return emails;

        } catch (SQLException e) {
            logger.error("Error finding ready to send emails: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm emails theo reference ID
     */
    public List<EmailQueue> findByReferenceId(String referenceId) throws SQLException {
        if (referenceId == null || referenceId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<EmailQueue> emails = new ArrayList<>();
        String sql = "SELECT * FROM email_queue WHERE reference_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, referenceId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emails.add(mapResultSetToEntity(rs));
                }
            }

            return emails;

        } catch (SQLException e) {
            logger.error("Error finding emails by reference ID {}: {}", referenceId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Đếm emails theo status
     */
    public long countByStatus(EmailStatus status) throws SQLException {
        if (status == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM email_queue WHERE status = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }

            return 0;

        } catch (SQLException e) {
            logger.error("Error counting emails by status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Xóa emails đã gửi thành công cũ hơn X ngày
     */
    public int deleteSentEmailsOlderThan(int days) throws SQLException {
        String sql = "DELETE FROM email_queue WHERE status = ? AND sent_at < ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, EmailStatus.SENT.name());
            setTimestamp(stmt, 2, LocalDateTime.now().minusDays(days));

            int deleted = stmt.executeUpdate();
            logger.info("Deleted {} sent emails older than {} days", deleted, days);
            return deleted;

        } catch (SQLException e) {
            logger.error("Error deleting old sent emails: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm failed emails
     */
    public List<EmailQueue> findFailedEmails() throws SQLException {
        return findByStatusOrderByScheduledAt(EmailStatus.FAILED);
    }

    /**
     * Tìm pending emails
     */
    public List<EmailQueue> findPendingEmails() throws SQLException {
        return findByStatusOrderByScheduledAt(EmailStatus.PENDING);
    }

    /**
     * Tìm email theo status với limit
     * Method này được sử dụng bởi EmailScheduler
     */
    public List<EmailQueue> findByStatus(EmailStatus status, int limit) throws SQLException {
        if (status == null) {
            return new ArrayList<>();
        }

        List<EmailQueue> emails = new ArrayList<>();
        String sql = "SELECT * FROM email_queue WHERE status = ? ORDER BY created_at ASC LIMIT ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emails.add(mapResultSetToEntity(rs));
                }
            }

            return emails;

        } catch (SQLException e) {
            logger.error("Error finding emails by status {} with limit {}: {}",
                    status, limit, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Override update để đảm bảo commit và verify
     */
    @Override
    public EmailQueue update(EmailQueue entity) throws SQLException {
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("Entity and entity ID cannot be null");
        }

        logger.info("EmailQueueDao: Updating email queue ID {} to status {}",
                entity.getId(), entity.getStatus());

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = createUpdateSql();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setUpdateParameters(stmt, entity);

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Updating email_queue failed, no rows affected for ID: " + entity.getId());
                }

                logger.info("Updated record in {} with {} rows affected", getTableName(), affectedRows);
                logger.debug("Update SQL: {}", sql);

                // Verify update
                String verifySql = "SELECT status FROM email_queue WHERE id = ?";
                try (PreparedStatement verifyStmt = conn.prepareStatement(verifySql)) {
                    verifyStmt.setLong(1, entity.getId());
                    try (ResultSet rs = verifyStmt.executeQuery()) {
                        if (rs.next()) {
                            String dbStatus = rs.getString("status");
                            logger.info("Verified status in DB for ID {}: {}", entity.getId(), dbStatus);
                        }
                    }
                }

                return entity;
            }

        } catch (SQLException e) {
            logger.error("Error updating email_queue ID {}: {}", entity.getId(), e.getMessage(), e);
            throw e;
        }
    }

}
