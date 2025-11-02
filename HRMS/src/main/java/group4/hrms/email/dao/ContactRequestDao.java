package group4.hrms.email.dao;

import group4.hrms.dao.BaseDao;
import group4.hrms.email.model.ContactRequest;
import group4.hrms.email.model.ContactStatus;
import group4.hrms.email.model.ContactType;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO class để xử lý các thao tác với bảng contact_requests
 * 
 * @author Group4
 */
public class ContactRequestDao extends BaseDao<ContactRequest, String> {

    @Override
    protected String getTableName() {
        return "contact_requests";
    }

    @Override
    protected ContactRequest mapResultSetToEntity(ResultSet rs) throws SQLException {
        ContactRequest contact = new ContactRequest();
        contact.setId(rs.getString("id"));
        contact.setFullName(rs.getString("full_name"));
        contact.setEmail(rs.getString("email"));
        contact.setPhone(rs.getString("phone"));

        String contactTypeStr = rs.getString("contact_type");
        if (contactTypeStr != null) {
            contact.setContactType(ContactType.valueOf(contactTypeStr));
        }

        contact.setSubject(rs.getString("subject"));
        contact.setMessage(rs.getString("message"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            contact.setStatus(ContactStatus.valueOf(statusStr));
        }

        contact.setCreatedAt(getLocalDateTime(rs, "created_at"));
        contact.setUpdatedAt(getLocalDateTime(rs, "updated_at"));

        return contact;
    }

    @Override
    protected void setEntityId(ContactRequest contact, String id) {
        contact.setId(id);
    }

    @Override
    protected String getEntityId(ContactRequest contact) {
        return contact.getId();
    }

    @Override
    protected String createInsertSql() {
        return "INSERT INTO contact_requests (id, full_name, email, phone, contact_type, subject, " +
                "message, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String createUpdateSql() {
        return "UPDATE contact_requests SET full_name = ?, email = ?, phone = ?, contact_type = ?, " +
                "subject = ?, message = ?, status = ?, updated_at = ? WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, ContactRequest contact) throws SQLException {
        // Generate UUID if not set
        if (contact.getId() == null || contact.getId().isEmpty()) {
            contact.setId("CR-" + UUID.randomUUID().toString());
        }

        stmt.setString(1, contact.getId());
        stmt.setString(2, contact.getFullName());
        stmt.setString(3, contact.getEmail());
        stmt.setString(4, contact.getPhone());
        stmt.setString(5, contact.getContactType() != null ? contact.getContactType().name() : null);
        stmt.setString(6, contact.getSubject());
        stmt.setString(7, contact.getMessage());
        stmt.setString(8, contact.getStatus() != null ? contact.getStatus().name() : ContactStatus.NEW.name());
        setTimestamp(stmt, 9, contact.getCreatedAt() != null ? contact.getCreatedAt() : LocalDateTime.now());
        setTimestamp(stmt, 10, LocalDateTime.now());
    }

    /**
     * Override save method vì contact_requests sử dụng String ID thay vì
     * AUTO_INCREMENT
     */
    @Override
    public ContactRequest save(ContactRequest contact) throws SQLException {
        if (contact == null) {
            throw new IllegalArgumentException("Contact cannot be null");
        }

        // Generate ID if not exists
        if (contact.getId() == null || contact.getId().isEmpty()) {
            contact.setId("CR-" + UUID.randomUUID().toString());
        }

        String sql = createInsertSql();

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            setInsertParameters(stmt, contact);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                logger.error("Creating contact failed, no rows affected");
                throw new SQLException("Creating contact failed, no rows affected");
            }

            logger.debug("Contact saved successfully with ID: {}", contact.getId());
            return contact;

        } catch (SQLException e) {
            logger.error("Error saving record to {}: {}", getTableName(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, ContactRequest contact) throws SQLException {
        stmt.setString(1, contact.getFullName());
        stmt.setString(2, contact.getEmail());
        stmt.setString(3, contact.getPhone());
        stmt.setString(4, contact.getContactType() != null ? contact.getContactType().name() : null);
        stmt.setString(5, contact.getSubject());
        stmt.setString(6, contact.getMessage());
        stmt.setString(7, contact.getStatus() != null ? contact.getStatus().name() : null);
        setTimestamp(stmt, 8, LocalDateTime.now());
        stmt.setString(9, contact.getId());
    }

    // Business methods

    /**
     * Tìm contact requests theo email
     */
    public List<ContactRequest> findByEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<ContactRequest> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contact_requests WHERE email = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contacts.add(mapResultSetToEntity(rs));
                }
            }

            return contacts;

        } catch (SQLException e) {
            logger.error("Error finding contacts by email {}: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm contact requests theo status
     */
    public List<ContactRequest> findByStatus(ContactStatus status) throws SQLException {
        if (status == null) {
            return new ArrayList<>();
        }

        List<ContactRequest> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contact_requests WHERE status = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contacts.add(mapResultSetToEntity(rs));
                }
            }

            return contacts;

        } catch (SQLException e) {
            logger.error("Error finding contacts by status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm contact requests theo contact type
     */
    public List<ContactRequest> findByContactType(ContactType contactType) throws SQLException {
        if (contactType == null) {
            return new ArrayList<>();
        }

        List<ContactRequest> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contact_requests WHERE contact_type = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contactType.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contacts.add(mapResultSetToEntity(rs));
                }
            }

            return contacts;

        } catch (SQLException e) {
            logger.error("Error finding contacts by type {}: {}", contactType, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Cập nhật status của contact request
     */
    public boolean updateStatus(String contactId, ContactStatus newStatus) throws SQLException {
        if (contactId == null || newStatus == null) {
            return false;
        }

        String sql = "UPDATE contact_requests SET status = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus.name());
            setTimestamp(stmt, 2, LocalDateTime.now());
            stmt.setString(3, contactId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating contact status {}: {}", contactId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm các contact requests mới (NEW status)
     */
    public List<ContactRequest> findNewContacts() throws SQLException {
        return findByStatus(ContactStatus.NEW);
    }

    /**
     * Tìm các contact requests urgent (COMPLAINT type)
     */
    public List<ContactRequest> findUrgentContacts() throws SQLException {
        return findByContactType(ContactType.COMPLAINT);
    }

    /**
     * Đếm contact requests theo status
     */
    public long countByStatus(ContactStatus status) throws SQLException {
        if (status == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM contact_requests WHERE status = ?";

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
            logger.error("Error counting contacts by status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tìm contact requests trong khoảng thời gian
     */
    public List<ContactRequest> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        if (startDate == null || endDate == null) {
            return new ArrayList<>();
        }

        List<ContactRequest> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contact_requests WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            setTimestamp(stmt, 1, startDate);
            setTimestamp(stmt, 2, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contacts.add(mapResultSetToEntity(rs));
                }
            }

            return contacts;

        } catch (SQLException e) {
            logger.error("Error finding contacts by date range: {}", e.getMessage(), e);
            throw e;
        }
    }
}
