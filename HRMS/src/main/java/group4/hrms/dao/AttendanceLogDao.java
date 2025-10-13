package group4.hrms.dao;

import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.model.AttendanceLog;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DAO class để xử lý các thao tác với bảng AttendanceLog
 *
 * @author Group4
 */
public class AttendanceLogDao extends BaseDao<AttendanceLog, Long> {

    @Override
    protected String getTableName() {
        return "attendance_logs";
    }

    @Override
    protected AttendanceLog mapResultSetToEntity(ResultSet rs) throws SQLException {
        AttendanceLog log = new AttendanceLog();

        log.setId(rs.getLong("id"));
        log.setUserId(rs.getLong("user_id"));
        log.setCheckType(rs.getString("check_type"));

        Timestamp checkedAtTs = rs.getTimestamp("checked_at");
        if (checkedAtTs != null) {
            log.setCheckedAt(checkedAtTs.toLocalDateTime());
        }

        log.setSource(rs.getString("source"));
        log.setNote(rs.getString("note"));

        long periodId = rs.getLong("period_id");
        if (!rs.wasNull()) {
            log.setPeriodId(periodId);
        }

        Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            log.setCreatedAt(createdAtTs.toLocalDateTime());
        }

        return log;
    }

    @Override
    protected String createInsertSql() {
        return "INSERT INTO attendance_logs ("
                + "user_id, check_type, checked_at, source, note, period_id"
                + ") VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String createUpdateSql() {
        return "UPDATE attendance_logs SET "
                + "user_id = ?, "
                + "check_type = ?, "
                + "checked_at = ?, "
                + "source = ?, "
                + "note = ?, "
                + "period_id = ? "
                + "WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, AttendanceLog log) throws SQLException {
        stmt.setLong(1, log.getUserId());
        stmt.setString(2, log.getCheckType());

        if (log.getCheckedAt() != null) {
            stmt.setTimestamp(3, Timestamp.valueOf(log.getCheckedAt()));
        } else {
            stmt.setNull(3, Types.TIMESTAMP);
        }

        if (log.getSource() != null) {
            stmt.setString(4, log.getSource());
        } else {
            stmt.setNull(4, Types.VARCHAR);
        }

        if (log.getNote() != null) {
            stmt.setString(5, log.getNote());
        } else {
            stmt.setNull(5, Types.VARCHAR);
        }

        if (log.getPeriodId() != null) {
            stmt.setLong(6, log.getPeriodId());
        } else {
            stmt.setNull(6, Types.BIGINT);
        }
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, AttendanceLog log) throws SQLException {
        stmt.setLong(1, log.getUserId());
        stmt.setString(2, log.getCheckType());

        if (log.getCheckedAt() != null) {
            stmt.setTimestamp(3, Timestamp.valueOf(log.getCheckedAt()));
        } else {
            stmt.setNull(3, Types.TIMESTAMP);
        }

        if (log.getSource() != null) {
            stmt.setString(4, log.getSource());
        } else {
            stmt.setNull(4, Types.VARCHAR);
        }

        if (log.getNote() != null) {
            stmt.setString(5, log.getNote());
        } else {
            stmt.setNull(5, Types.VARCHAR);
        }

        if (log.getPeriodId() != null) {
            stmt.setLong(6, log.getPeriodId());
        } else {
            stmt.setNull(6, Types.BIGINT);
        }

        stmt.setLong(7, log.getId());
    }

    @Override
    protected void setEntityId(AttendanceLog log, Long id) {
        log.setId(id);
    }

    @Override
    protected Long getEntityId(AttendanceLog log) {
        return log.getId();
    }

    //Override save method để xử lý insert/update
    @Override
    public AttendanceLog save(AttendanceLog entity) throws SQLException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        if (getEntityId(entity) != null) {
            return update(entity);
        } else {
            String sql = createInsertSql();

            try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                setInsertParameters(stmt, entity);

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating attendance log failed, no rows affected");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long generatedId = generatedKeys.getLong(1);
                        setEntityId(entity, generatedId);
                    } else {
                        throw new SQLException("Creating attendance log failed, no ID obtained");
                    }
                }

                // Thay vì log WorkDate, dùng checkedAt
                logger.info("Created new attendance log for user {} on {}",
                        entity.getUserId(), entity.getCheckedAt());
                return entity;

            } catch (SQLException e) {
                logger.error("Error saving attendance log: {}", e.getMessage(), e);
                throw e;
            }
        }
    }

    //Tìm toàn hộ bản ghi theo định dạng frontend
    public List<AttendanceLogDto> findAllForOverview() throws SQLException {
        String sql = """
            SELECT
              u.id AS employee_id,
              u.employee_code AS employee_code,
              u.full_name AS employee_name,
              d.name AS department_name,
              DATE(al.checked_at) AS work_date,
              MIN(CASE WHEN al.check_type = 'IN'  THEN al.checked_at END)  AS check_in,
              MAX(CASE WHEN al.check_type = 'OUT' THEN al.checked_at END)  AS check_out,
              COALESCE(
                MIN(CASE WHEN al.check_type = 'IN'  THEN al.note END),
                MAX(CASE WHEN al.check_type = 'OUT' THEN al.note END),
                'No Records'
              ) AS status,
              GROUP_CONCAT(DISTINCT al.source SEPARATOR ', ') AS source,
              tp.name AS period_name
            FROM attendance_logs al
            JOIN users u ON al.user_id = u.id
            LEFT JOIN departments d ON u.department_id = d.id
            LEFT JOIN timesheet_periods tp ON al.period_id = tp.id
            GROUP BY
              u.id,
              u.employee_code,
              u.full_name,
              d.name,
              tp.name,
              DATE(al.checked_at)
            ORDER BY DATE(al.checked_at) DESC, u.full_name;
        """;

        List<AttendanceLogDto> results = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                AttendanceLogDto dto = new AttendanceLogDto();
                dto.setEmployeeId(rs.getLong("employee_id"));
                dto.setEmployeeName(rs.getString("employee_name"));
                dto.setDepartment(rs.getString("department_name"));

                // Lấy LocalDate từ sql Date
                Date sqlDate = rs.getDate("work_date");
                dto.setDate(sqlDate != null ? sqlDate.toLocalDate() : null);

                // Lấy LocalTime từ Timestamp
                Timestamp inTs = rs.getTimestamp("check_in");
                Timestamp outTs = rs.getTimestamp("check_out");
                dto.setCheckIn(inTs != null ? inTs.toLocalDateTime().toLocalTime() : null);
                dto.setCheckOut(outTs != null ? outTs.toLocalDateTime().toLocalTime() : null);

                dto.setStatus(rs.getString("status"));
                dto.setSource(rs.getString("source"));
                dto.setPeriod(rs.getString("period_name"));

                results.add(dto);
            }

        } catch (SQLException e) {
        }

        return results;
    }

    //Tìm attendance logs theo user ID
    public List<AttendanceLogDto> findByUserId(Long userId) throws SQLException {
        if (userId == null) {
            return Collections.emptyList();
        }

        String sql = """
        SELECT
          DATE(al.checked_at) AS work_date,
          MIN(CASE WHEN al.check_type = 'IN'  THEN al.checked_at END) AS check_in,
          MAX(CASE WHEN al.check_type = 'OUT' THEN al.checked_at END) AS check_out,
          COALESCE(
            MIN(CASE WHEN al.check_type = 'IN'  THEN al.note END),
            MAX(CASE WHEN al.check_type = 'OUT' THEN al.note END),
            'No Records'
          ) AS status,
          GROUP_CONCAT(DISTINCT al.source SEPARATOR ', ') AS source,
          tp.name AS period_name
        FROM attendance_logs al
        LEFT JOIN timesheet_periods tp ON al.period_id = tp.id
        WHERE al.user_id = ?
        GROUP BY DATE(al.checked_at), tp.name
        ORDER BY DATE(al.checked_at) DESC;
    """;

        List<AttendanceLogDto> results = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceLogDto dto = new AttendanceLogDto();

                    Date sqlDate = rs.getDate("work_date");
                    dto.setDate(sqlDate != null ? sqlDate.toLocalDate() : null);

                    Timestamp inTs = rs.getTimestamp("check_in");
                    Timestamp outTs = rs.getTimestamp("check_out");
                    dto.setCheckIn(inTs != null ? inTs.toLocalDateTime().toLocalTime() : null);
                    dto.setCheckOut(outTs != null ? outTs.toLocalDateTime().toLocalTime() : null);

                    dto.setStatus(rs.getString("status"));
                    dto.setSource(rs.getString("source"));
                    dto.setPeriod(rs.getString("period_name"));

                    results.add(dto);
                }
            }
        } catch (SQLException e) {
            throw e;
        }

        return results;
    }

    //filter lọc record
//    public List<AttendanceLogDto> findByFilter(
//            Long userId,
//            LocalDate startDate,
//            LocalDate endDate,
//            String status,
//            String source,
//            Long periodId) throws SQLException {
//
//        StringBuilder sql = new StringBuilder("""
//        SELECT
//            DATE(al.checked_at) AS work_date,
//            MIN(CASE WHEN al.check_type = 'IN'  THEN al.checked_at END) AS check_in,
//            MAX(CASE WHEN al.check_type = 'OUT' THEN al.checked_at END) AS check_out,
//            COALESCE(
//                MIN(CASE WHEN al.check_type = 'IN'  THEN al.note END),
//                MAX(CASE WHEN al.check_type = 'OUT' THEN al.note END),
//                'No Records'
//            ) AS status,
//            GROUP_CONCAT(DISTINCT al.source SEPARATOR ', ') AS source,
//            tp.name AS period_name
//        FROM attendance_logs al
//        LEFT JOIN timesheet_periods tp ON al.period_id = tp.id
//        WHERE 1=1
//    """);
//
//        List<Object> params = new ArrayList<>();
//
//        // 🔹 Lọc theo user
//        if (userId != null) {
//            sql.append(" AND al.user_id = ?");
//            params.add(userId);
//        }
//
//        if (startDate != null) {
//            sql.append(" AND DATE(al.checked_at) >= ?");
//            params.add(Date.valueOf(startDate));
//        }
//        if (endDate != null) {
//            sql.append(" AND DATE(al.checked_at) <= ?");
//            params.add(Date.valueOf(endDate));
//        }
//        if (status != null && !status.isEmpty()) {
//            sql.append(" AND al.note = ?");
//            params.add(status);
//        }
//        if (source != null && !source.isEmpty()) {
//            sql.append(" AND al.source = ?");
//            params.add(source);
//        }
//        if (periodId != null) {
//            sql.append(" AND al.period_id = ?");
//            params.add(periodId);
//        }
//
//        sql.append(" GROUP BY DATE(al.checked_at), tp.name");
//        sql.append(" ORDER BY DATE(al.checked_at) DESC");
//
//        List<AttendanceLogDto> results = new ArrayList<>();
//
//        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
//
//            // Set parameters
//            for (int i = 0; i < params.size(); i++) {
//                Object param = params.get(i);
//                if (param instanceof Long long1) {
//                    stmt.setLong(i + 1, long1);
//                } else if (param instanceof Date date) {
//                    stmt.setDate(i + 1, date);
//                } else if (param instanceof String string) {
//                    stmt.setString(i + 1, string);
//                }
//            }
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    AttendanceLogDto dto = new AttendanceLogDto();
//
//                    Date sqlDate = rs.getDate("work_date");
//                    dto.setDate(sqlDate != null ? sqlDate.toLocalDate() : null);
//
//                    Timestamp inTs = rs.getTimestamp("check_in");
//                    Timestamp outTs = rs.getTimestamp("check_out");
//                    dto.setCheckIn(inTs != null ? inTs.toLocalDateTime().toLocalTime() : null);
//                    dto.setCheckOut(outTs != null ? outTs.toLocalDateTime().toLocalTime() : null);
//
//                    dto.setStatus(rs.getString("status"));
//                    dto.setSource(rs.getString("source"));
//                    dto.setPeriod(rs.getString("period_name"));
//
//                    results.add(dto);
//                }
//            }
//        }
//
//        return results;
//    }
    public List<AttendanceLogDto> findByFilter(
            Long userId,
            String employeeKeyword,
            String department,
            LocalDate startDate,
            LocalDate endDate,
            String status,
            String source,
            Long periodId) throws SQLException {

        StringBuilder sql = new StringBuilder("""
        SELECT
            u.id AS employee_id,
            u.full_name AS employee_name,
            d.name AS department_name,
            DATE(al.checked_at) AS work_date,
            MIN(CASE WHEN al.check_type = 'IN'  THEN al.checked_at END) AS check_in,
            MAX(CASE WHEN al.check_type = 'OUT' THEN al.checked_at END) AS check_out,
            COALESCE(
                MIN(CASE WHEN al.check_type = 'IN'  THEN al.note END),
                MAX(CASE WHEN al.check_type = 'OUT' THEN al.note END),
                'No Records'
            ) AS status,
            GROUP_CONCAT(DISTINCT al.source SEPARATOR ', ') AS source,
            tp.name AS period_name
        FROM attendance_logs al
        LEFT JOIN timesheet_periods tp ON al.period_id = tp.id
        LEFT JOIN users u ON al.user_id = u.id
        LEFT JOIN departments d ON u.department_id = d.id
        WHERE 1=1
    """);

        List<Object> params = new ArrayList<>();

        // 🔹 Lọc theo userId (dành cho employee view)
        if (userId != null) {
            sql.append(" AND al.user_id = ?");
            params.add(userId);
        }

        // 🔹 Lọc theo employee name / id (dành cho HR view)
        if (employeeKeyword != null && !employeeKeyword.isEmpty()) {
            sql.append(" AND (u.full_name LIKE ? OR CAST(u.id AS CHAR) LIKE ?)");
            params.add("%" + employeeKeyword + "%");
            params.add("%" + employeeKeyword + "%");
        }

        // 🔹 Lọc theo department (dành cho HR view)
        if (department != null && !department.isEmpty()) {
            sql.append(" AND d.name = ?");
            params.add(department);
        }

        if (startDate != null) {
            sql.append(" AND DATE(al.checked_at) >= ?");
            params.add(Date.valueOf(startDate));
        }
        if (endDate != null) {
            sql.append(" AND DATE(al.checked_at) <= ?");
            params.add(Date.valueOf(endDate));
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND al.note = ?");
            params.add(status);
        }
        if (source != null && !source.isEmpty()) {
            sql.append(" AND al.source = ?");
            params.add(source);
        }
        if (periodId != null) {
            sql.append(" AND al.period_id = ?");
            params.add(periodId);
        }

        // 🔹 Nhóm theo user + ngày + kỳ chấm công
        sql.append("""
        GROUP BY u.id, u.full_name, d.name, DATE(al.checked_at), tp.name
        ORDER BY DATE(al.checked_at) DESC
    """);

        List<AttendanceLogDto> results = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Long l) {
                    stmt.setLong(i + 1, l);
                } else if (param instanceof Date d) {
                    stmt.setDate(i + 1, d);
                } else if (param instanceof String s) {
                    stmt.setString(i + 1, s);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceLogDto dto = new AttendanceLogDto();

                    dto.setEmployeeId(rs.getLong("employee_id"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setDepartment(rs.getString("department_name"));

                    Date sqlDate = rs.getDate("work_date");
                    dto.setDate(sqlDate != null ? sqlDate.toLocalDate() : null);

                    Timestamp inTs = rs.getTimestamp("check_in");
                    Timestamp outTs = rs.getTimestamp("check_out");
                    dto.setCheckIn(inTs != null ? inTs.toLocalDateTime().toLocalTime() : null);
                    dto.setCheckOut(outTs != null ? outTs.toLocalDateTime().toLocalTime() : null);

                    dto.setStatus(rs.getString("status"));
                    dto.setSource(rs.getString("source"));
                    dto.setPeriod(rs.getString("period_name"));

                    results.add(dto);
                }
            }
        }

        return results;
    }
}
