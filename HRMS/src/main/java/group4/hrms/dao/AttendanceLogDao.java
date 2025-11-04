package group4.hrms.dao;

import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.model.AttendanceLog;
import group4.hrms.service.AttendanceService;
import group4.hrms.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

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
                + "check_type = ?, "
                + "source = ?, "
                + "note = ?, "
                + "period_id = ?, "
                + "checked_at = ? " // cập nhật checked_at mới
                + "WHERE user_id = ? AND checked_at = ?"; // tìm bản ghi bằng checked_at cũ
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
        if (log.getCheckedAt() == null) {
            throw new IllegalArgumentException("checkedAt (old) cannot be null for update");
        }
        if (log.getCheckedAtNew() == null) {
            throw new IllegalArgumentException("checkedAtNew cannot be null for update");
        }

        stmt.setString(1, log.getCheckType());

        if (log.getSource() != null) {
            stmt.setString(2, log.getSource());
        } else {
            stmt.setNull(2, Types.VARCHAR);
        }

        if (log.getNote() != null) {
            stmt.setString(3, log.getNote());
        } else {
            stmt.setNull(3, Types.VARCHAR);
        }

        if (log.getPeriodId() != null) {
            stmt.setLong(4, log.getPeriodId());
        } else {
            stmt.setNull(4, Types.BIGINT);
        }

        // checked_at mới
        stmt.setTimestamp(5, Timestamp.valueOf(log.getCheckedAtNew()));

        // user_id
        stmt.setLong(6, log.getUserId());

        // checked_at cũ (điều kiện WHERE)
        stmt.setTimestamp(7, Timestamp.valueOf(log.getCheckedAt()));
    }

    @Override
    protected void setEntityId(AttendanceLog log, Long id) {
        log.setId(id);
    }

    @Override
    protected Long getEntityId(AttendanceLog log) {
        return log.getId();
    }

    @Override
    public AttendanceLog save(AttendanceLog entity) throws SQLException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

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

            logger.info("Created new attendance log for user {} on {}",
                    entity.getUserId(), entity.getCheckedAt());

            return entity;

        } catch (SQLException e) {
            logger.error("Error saving attendance log: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AttendanceLog update(AttendanceLog entity) throws SQLException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        String sql = createUpdateSql();

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            setUpdateParameters(stmt, entity);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating record failed, no rows affected");
            }

            logger.info("Updated record in {}", getTableName());
            return entity;

        } catch (SQLException e) {
            logger.error("Error updating record in {}: {}", getTableName(), e.getMessage(), e);
            throw e;
        }
    }

    //Tìm toàn hộ bản ghi theo định dạng frontend
    public List<AttendanceLogDto> findAllForOverview(int offset, int limit, boolean paged) throws SQLException {
        StringBuilder sql = new StringBuilder("""
    SELECT
      u.id AS employee_id,
      u.employee_code AS employee_code,
      u.full_name AS employee_name,
      d.name AS department_name,
      DATE(al.checked_at) AS work_date,
      MIN(CASE WHEN al.check_type = 'IN' THEN al.checked_at END) AS check_in,
      MAX(CASE WHEN al.check_type = 'OUT' THEN al.checked_at END) AS check_out,
      COALESCE(
        MIN(CASE WHEN al.check_type = 'IN' THEN al.note END),
        MAX(CASE WHEN al.check_type = 'OUT' THEN al.note END),
        'No Records'
      ) AS status,
      GROUP_CONCAT(DISTINCT al.source SEPARATOR ', ') AS source,
      tp.name AS period_name
    FROM attendance_logs al
    JOIN users u ON al.user_id = u.id
    LEFT JOIN departments d ON u.department_id = d.id
    LEFT JOIN timesheet_periods tp ON al.period_id = tp.id
""");

        sql.append("""
    GROUP BY u.id, u.employee_code, u.full_name, d.name, tp.name, DATE(al.checked_at)
    ORDER BY DATE(al.checked_at) DESC, u.full_name
""");

        if (paged) {
            sql.append(" LIMIT ? OFFSET ?");
        }

        List<AttendanceLogDto> results = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            if (paged) {
                stmt.setInt(1, limit);
                stmt.setInt(2, offset);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceLogDto dto = new AttendanceLogDto();

                    // Không set dto.setId nữa
                    dto.setUserId(rs.getLong("employee_id"));
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

    //Đếm số bản ghi để phân trang
    public int countAllForOverview() throws SQLException {
        String sql = """
            SELECT COUNT(*) AS total
            FROM (
                SELECT DATE(al.checked_at), u.id
                FROM attendance_logs al
                JOIN users u ON al.user_id = u.id
                LEFT JOIN departments d ON u.department_id = d.id
                LEFT JOIN timesheet_periods tp ON al.period_id = tp.id
                GROUP BY u.id, u.employee_code, u.full_name, d.name, tp.name, DATE(al.checked_at)
            ) AS subquery;
        """;

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        }

        return 0;
    }

    //Tìm attendance logs theo user ID
    public List<AttendanceLogDto> findByUserId(Long userId, int limit, int offset, boolean paged) throws SQLException {
        if (userId == null) {
            return Collections.emptyList();
        }

        StringBuilder sql = new StringBuilder("""
    SELECT
        u.id AS user_id,
        u.full_name AS employee_name,
        d.name AS department_name,
        DATE(al.checked_at) AS work_date,
        MIN(CASE WHEN al.check_type='IN' THEN al.checked_at END) AS check_in,
        MAX(CASE WHEN al.check_type='OUT' THEN al.checked_at END) AS check_out,
        COALESCE(
            MIN(CASE WHEN al.check_type='IN' THEN al.note END),
            MAX(CASE WHEN al.check_type='OUT' THEN al.note END),
            'No Records'
        ) AS status,
        GROUP_CONCAT(DISTINCT al.source SEPARATOR ', ') AS source,
        tp.name AS period_name
    FROM attendance_logs al
    JOIN users u ON al.user_id = u.id
    LEFT JOIN departments d ON u.department_id = d.id
    LEFT JOIN timesheet_periods tp ON al.period_id = tp.id
    WHERE al.user_id = ?
""");

        sql.append(" GROUP BY DATE(al.checked_at), u.id, u.full_name, d.name, tp.name");
        sql.append(" ORDER BY DATE(al.checked_at) DESC");

        if (paged) {
            sql.append(" LIMIT ? OFFSET ?");
        }

        List<AttendanceLogDto> results = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            stmt.setLong(1, userId);

            if (paged) {
                stmt.setInt(2, limit);
                stmt.setInt(3, offset);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceLogDto dto = new AttendanceLogDto();

                    // Không set dto.setId nữa
                    dto.setUserId(rs.getLong("user_id"));
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

    public List<AttendanceLogDto> findByFilter(
            Long userId,
            String employeeKeyword,
            String department,
            LocalDate startDate,
            LocalDate endDate,
            String status,
            String source,
            Long periodId,
            int limit,
            int offset,
            boolean paged
    ) throws SQLException {

        StringBuilder sql = new StringBuilder("""
        SELECT
            al.id,
            al.user_id,
            u.full_name AS employee_name,
            d.name AS department_name,
            al.check_type,
            al.checked_at,
            al.note,
            al.source,
            al.period_id,
            tp.name AS period_name,
            COALESCE(tp.is_locked, FALSE) AS period_locked
        FROM attendance_logs al
        LEFT JOIN users u ON al.user_id = u.id
        LEFT JOIN departments d ON u.department_id = d.id
        LEFT JOIN timesheet_periods tp ON al.period_id = tp.id
        WHERE 1=1
    """);

        List<Object> params = new ArrayList<>();
        if (userId != null) {
            sql.append(" AND al.user_id = ?");
            params.add(userId);
        }
        if (employeeKeyword != null && !employeeKeyword.isEmpty()) {
            sql.append(" AND (u.full_name LIKE ? OR CAST(u.id AS CHAR) LIKE ?)");
            params.add("%" + employeeKeyword + "%");
            params.add("%" + employeeKeyword + "%");
        }
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

        sql.append(" ORDER BY al.user_id, al.checked_at");

        List<AttendanceLog> logs = new ArrayList<>();
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
                    AttendanceLog log = new AttendanceLog();
                    log.setId(rs.getLong("id"));
                    log.setUserId(rs.getLong("user_id"));
                    log.setCheckType(rs.getString("check_type"));
                    Timestamp ts = rs.getTimestamp("checked_at");
                    log.setCheckedAt(ts != null ? ts.toLocalDateTime() : null);
                    log.setNote(rs.getString("note"));
                    log.setSource(rs.getString("source"));
                    log.setPeriodId(rs.getLong("period_id"));
                    log.setEmployeeName(rs.getString("employee_name"));
                    log.setDepartmentName(rs.getString("department_name"));
                    log.setPeriodName(rs.getString("period_name"));
                    log.setPeriodLocked(rs.getBoolean("period_locked"));
                    logs.add(log);
                }
            }
        }

        // -------------------------
        //  Ghép IN/OUT thành DTO
        // -------------------------
        Map<Long, List<AttendanceLog>> logsByUser = logs.stream()
                .collect(Collectors.groupingBy(AttendanceLog::getUserId, LinkedHashMap::new, Collectors.toList()));

        List<AttendanceLogDto> allDtos = new ArrayList<>();

        for (List<AttendanceLog> userLogs : logsByUser.values()) {
            userLogs.sort(Comparator.comparing(AttendanceLog::getCheckedAt));
            Map<LocalDate, List<AttendanceLog>> logsByDate = userLogs.stream()
                    .collect(Collectors.groupingBy(l -> l.getCheckedAt().toLocalDate(),
                            LinkedHashMap::new, Collectors.toList()));

            for (Map.Entry<LocalDate, List<AttendanceLog>> entry : logsByDate.entrySet()) {
                LocalDate date = entry.getKey();
                List<AttendanceLog> dayLogs = entry.getValue();
                Queue<AttendanceLog> pendingIns = new LinkedList<>();
                Queue<AttendanceLog> pendingOuts = new LinkedList<>();

                for (AttendanceLog log : dayLogs) {
                    if ("IN".equalsIgnoreCase(log.getCheckType())) {
                        pendingIns.add(log);
                    } else {
                        pendingOuts.add(log);
                    }
                }

                while (!pendingIns.isEmpty() || !pendingOuts.isEmpty()) {
                    AttendanceLog inLog = pendingIns.poll();
                    AttendanceLog outLog = null;
                    if (inLog != null) {
                        for (Iterator<AttendanceLog> it = pendingOuts.iterator(); it.hasNext();) {
                            AttendanceLog candidate = it.next();
                            if (!candidate.getCheckedAt().isBefore(inLog.getCheckedAt())) {
                                outLog = candidate;
                                it.remove();
                                break;
                            }
                        }
                        if (outLog == null && !pendingOuts.isEmpty()) {
                            outLog = pendingOuts.poll();
                        }
                    } else if (!pendingOuts.isEmpty()) {
                        outLog = pendingOuts.poll();
                    }

                    AttendanceLogDto dto = new AttendanceLogDto();
                    if (inLog != null) {
                        dto.setUserId(inLog.getUserId());
                        dto.setEmployeeName(inLog.getEmployeeName());
                        dto.setDepartment(inLog.getDepartmentName());
                        dto.setDate(date);
                        dto.setCheckIn(inLog.getCheckedAt().toLocalTime());
                        dto.setStatus(inLog.getNote());
                        dto.setSource(inLog.getSource());
                        dto.setPeriod(inLog.getPeriodName());
                        dto.setIsLocked(inLog.isPeriodLocked());
                    }
                    if (outLog != null) {
                        dto.setUserId(dto.getUserId() != null ? dto.getUserId() : outLog.getUserId());
                        dto.setEmployeeName(dto.getEmployeeName() != null ? dto.getEmployeeName() : outLog.getEmployeeName());
                        dto.setDepartment(dto.getDepartment() != null ? dto.getDepartment() : outLog.getDepartmentName());
                        dto.setDate(dto.getDate() != null ? dto.getDate() : date);
                        dto.setCheckOut(outLog.getCheckedAt().toLocalTime());
                        dto.setStatus(dto.getStatus() != null ? dto.getStatus() : outLog.getNote());
                        dto.setSource(dto.getSource() != null ? dto.getSource() : outLog.getSource());
                        dto.setPeriod(dto.getPeriod() != null ? dto.getPeriod() : outLog.getPeriodName());
                        dto.setIsLocked(dto.isIsLocked() || outLog.isPeriodLocked());
                    }
                    allDtos.add(dto);
                }
            }
        }

        // -------------------------
        //  Pagination trên DTO
        // -------------------------
        if (paged) {
            int start = Math.min(offset, allDtos.size());
            int end = Math.min(offset + limit, allDtos.size());
            return allDtos.subList(start, end);
        } else {
            return allDtos;
        }
    }

    //Đếm số bản ghi để phân trang
    public int countByFilter(
            Long userId,
            String employeeKeyword,
            String department,
            LocalDate startDate,
            LocalDate endDate,
            String status,
            String source,
            Long periodId
    ) throws SQLException {

        StringBuilder sql = new StringBuilder("""
        SELECT al.id, al.user_id, al.check_type, al.checked_at, al.note, al.source, al.period_id,
               u.full_name AS employee_name, d.name AS department_name
        FROM attendance_logs al
        LEFT JOIN users u ON al.user_id = u.id
        LEFT JOIN departments d ON u.department_id = d.id
        WHERE 1=1
    """);

        List<Object> params = new ArrayList<>();

        if (userId != null) {
            sql.append(" AND al.user_id = ?");
            params.add(userId);
        }

        if (employeeKeyword != null && !employeeKeyword.isEmpty()) {
            sql.append(" AND (u.full_name LIKE ? OR CAST(u.id AS CHAR) LIKE ?)");
            params.add("%" + employeeKeyword + "%");
            params.add("%" + employeeKeyword + "%");
        }

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

        sql.append(" ORDER BY al.user_id, al.checked_at");

        int count = 0;

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
                // Map userId -> Map date -> List<AttendanceLog>
                Map<Long, Map<LocalDate, List<AttendanceLog>>> logsByUserByDate = new LinkedHashMap<>();

                while (rs.next()) {
                    AttendanceLog log = new AttendanceLog();
                    log.setId(rs.getLong("id"));
                    log.setUserId(rs.getLong("user_id"));
                    log.setCheckType(rs.getString("check_type"));
                    Timestamp ts = rs.getTimestamp("checked_at");
                    LocalDateTime dateTime = ts != null ? ts.toLocalDateTime() : null;
                    log.setCheckedAt(dateTime);
                    log.setNote(rs.getString("note"));
                    log.setSource(rs.getString("source"));
                    log.setPeriodId(rs.getLong("period_id"));

                    LocalDate date = dateTime.toLocalDate();

                    logsByUserByDate
                            .computeIfAbsent(log.getUserId(), k -> new LinkedHashMap<>())
                            .computeIfAbsent(date, k -> new ArrayList<>())
                            .add(log);
                }

                // Ghép check-in / check-out theo user + date
                for (Map<LocalDate, List<AttendanceLog>> logsByDate : logsByUserByDate.values()) {
                    for (List<AttendanceLog> logs : logsByDate.values()) {
                        logs.sort(Comparator.comparing(AttendanceLog::getCheckedAt));
                        AttendanceLogDto currentDto = null;
                        for (AttendanceLog log : logs) {
                            if ("IN".equals(log.getCheckType())) {
                                currentDto = new AttendanceLogDto();
                                currentDto.setCheckIn(log.getCheckedAt().toLocalTime());
                            } else if ("OUT".equals(log.getCheckType()) && currentDto != null) {
                                currentDto.setCheckOut(log.getCheckedAt().toLocalTime());
                                count++; // mỗi cặp IN/OUT = 1 dòng FE
                                currentDto = null;
                            }
                        }
                        if (currentDto != null) {
                            count++; // còn IN chưa có OUT
                        }
                    }
                }
            }
        }

        return count;
    }

    public boolean saveAttendanceLogs(List<AttendanceLog> logs) {
        boolean allSuccess = true;

        for (AttendanceLog log : logs) {
            try {
                save(log);
            } catch (SQLException e) {
                allSuccess = false;
            }
        }
        return allSuccess;
    }

    public boolean updateAttendanceLogs(List<AttendanceLog> logs) {
        boolean allSuccess = true;

        for (AttendanceLog log : logs) {
            try {
                update(log);
            } catch (SQLException e) {
                allSuccess = false;
            }
        }
        return allSuccess;
    }

    public boolean deleteAttendance(Long userId, LocalDate date, LocalTime checkIn, LocalTime checkOut) throws SQLException {
        if (userId == null || date == null) {
            return false;
        }

        String sql = """
        DELETE FROM attendance_logs
        WHERE user_id = ?
          AND (
                (check_type = 'IN' AND checked_at = ?)
                OR
                (check_type = 'OUT' AND checked_at = ?)
              )
    """;

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            // Tạo timestamp đầy đủ cho IN và OUT
            Timestamp checkInTs = (checkIn != null) ? Timestamp.valueOf(date.atTime(checkIn)) : null;
            Timestamp checkOutTs = (checkOut != null) ? Timestamp.valueOf(date.atTime(checkOut)) : null;

            stmt.setTimestamp(2, checkInTs);
            stmt.setTimestamp(3, checkOutTs);

            int affected = stmt.executeUpdate();
            return affected > 0;
        }
    }

    public Map<String, List<AttendanceLogDto>> validateManualLogs(List<AttendanceLogDto> manualLogs) throws SQLException {
        Map<String, List<AttendanceLogDto>> result = new HashMap<>();
        List<AttendanceLogDto> validLogs = new ArrayList<>();
        List<AttendanceLogDto> invalidLogs = new ArrayList<>();
        result.put("valid", validLogs);
        result.put("invalid", invalidLogs);

        if (manualLogs == null || manualLogs.isEmpty()) {
            return result;
        }

        // Bước 1: Lọc spam và làm sạch dữ liệu
        List<AttendanceLogDto> cleanLogs = AttendanceService.filterSpamAndCleanLogs(manualLogs);

        // Allowed working time range
        LocalTime MIN_TIME = LocalTime.of(6, 0);
        LocalTime MAX_TIME = LocalTime.of(23, 59);

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Bước 2: Validate các logs đã được làm sạch
            for (AttendanceLogDto log : cleanLogs) {
                boolean hasConflict = false;
                String errorMessage = null;

                // === Basic validation - Required fields ===
                if (log.getUserId() == null) {
                    errorMessage = "Employee is required";
                    log.setError(errorMessage);
                    invalidLogs.add(log);
                    continue;
                }

                if (log.getDate() == null) {
                    errorMessage = "Date is required";
                    log.setError(errorMessage);
                    invalidLogs.add(log);
                    continue;
                }

                if (log.getCheckIn() == null && log.getCheckOut() == null) {
                    errorMessage = "At least one of check-in or check-out time is required";
                    log.setError(errorMessage);
                    invalidLogs.add(log);
                    continue;
                }

                // Check future date
                if (log.getDate().isAfter(LocalDate.now())) {
                    errorMessage = "Date cannot be in the future";
                    log.setError(errorMessage);
                    invalidLogs.add(log);
                    continue;
                }

                // === Time range validation ===
                if (log.getCheckIn() != null) {
                    if (log.getCheckIn().isBefore(MIN_TIME) || log.getCheckIn().isAfter(MAX_TIME)) {
                        errorMessage = "Check-in time must be between 06:00 and 23:59";
                        log.setError(errorMessage);
                        invalidLogs.add(log);
                        continue;
                    }
                }

                if (log.getCheckOut() != null) {
                    if (log.getCheckOut().isBefore(MIN_TIME) || log.getCheckOut().isAfter(MAX_TIME)) {
                        errorMessage = "Check-out time must be between 06:00 and 23:59";
                        log.setError(errorMessage);
                        invalidLogs.add(log);
                        continue;
                    }
                }

                // === Logical order: check-in < check-out ===
                if (log.getCheckIn() != null && log.getCheckOut() != null) {
                    if (!log.getCheckIn().isBefore(log.getCheckOut())) {
                        errorMessage = "Check-in time must be earlier than check-out time";
                        log.setError(errorMessage);
                        invalidLogs.add(log);
                        continue;
                    }
                }

                // === Check if user exists ===
                String checkUserSql = "SELECT COUNT(1) FROM users WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(checkUserSql)) {
                    stmt.setLong(1, log.getUserId());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) == 0) {
                            errorMessage = "Employee does not exist in the system";
                            log.setError(errorMessage);
                            invalidLogs.add(log);
                            continue;
                        }
                    }
                }

                // === Check if period is locked ===
                String checkPeriodSql = """
                SELECT COALESCE(tp.is_locked, FALSE)
                FROM timesheet_periods tp
                WHERE ? BETWEEN tp.date_start AND tp.date_end
                """;
                try (PreparedStatement stmt = conn.prepareStatement(checkPeriodSql)) {
                    stmt.setDate(1, java.sql.Date.valueOf(log.getDate()));
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next() && rs.getBoolean(1)) {
                            errorMessage = "Timesheet period is locked for this date";
                            log.setError(errorMessage);
                            invalidLogs.add(log);
                            continue;
                        }
                    }
                }

                LocalDateTime newCheckIn = log.getCheckIn() != null ? log.getDate().atTime(log.getCheckIn()) : null;
                LocalDateTime newCheckOut = null;
                if (log.getCheckOut() != null) {
                    if (log.getCheckIn() != null && log.getCheckOut().isBefore(log.getCheckIn())) {
                        newCheckOut = log.getDate().plusDays(1).atTime(log.getCheckOut());
                    } else {
                        newCheckOut = log.getDate().atTime(log.getCheckOut());
                    }
                }

                // Lấy tất cả log của user trong cùng ngày
                String sql = "SELECT checked_at, check_type FROM attendance_logs WHERE user_id = ? AND DATE(checked_at) = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setLong(1, log.getUserId());
                    stmt.setDate(2, java.sql.Date.valueOf(log.getDate()));

                    List<LocalDateTime> ins = new ArrayList<>();
                    List<LocalDateTime> outs = new ArrayList<>();

                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            String type = rs.getString("check_type");
                            Timestamp ts = rs.getTimestamp("checked_at");
                            if (ts == null || type == null) {
                                continue;
                            }

                            if ("IN".equalsIgnoreCase(type)) {
                                ins.add(ts.toLocalDateTime());
                            } else if ("OUT".equalsIgnoreCase(type)) {
                                outs.add(ts.toLocalDateTime());
                            }
                        }
                    }

                    // Sắp xếp thời gian ascending
                    ins.sort(Comparator.naturalOrder());
                    outs.sort(Comparator.naturalOrder());

                    // Ghép cặp theo logic: check-in đầu tiên → check-out gần nhất
                    List<Pair<LocalDateTime, LocalDateTime>> existingPairs = new ArrayList<>();
                    List<LocalDateTime> remainingIns = new ArrayList<>(ins);
                    List<LocalDateTime> remainingOuts = new ArrayList<>(outs);

                    while (!remainingIns.isEmpty()) {
                        LocalDateTime in = remainingIns.remove(0);
                        LocalDateTime out = null;

                        for (Iterator<LocalDateTime> it = remainingOuts.iterator(); it.hasNext();) {
                            LocalDateTime candidateOut = it.next();
                            if (!candidateOut.isBefore(in)) { // out >= in
                                out = candidateOut;
                                it.remove();
                                break;
                            }
                        }

                        // Nếu không tìm được check-out phù hợp → out = in + 8h
                        if (out == null) {
                            out = in.plusHours(8);
                        }

                        existingPairs.add(Pair.of(in, out));
                    }

                    // Kiểm tra conflict với existing pairs
                    for (Pair<LocalDateTime, LocalDateTime> pair : existingPairs) {
                        LocalDateTime existIn = pair.getLeft();
                        LocalDateTime existOut = pair.getRight();

                        boolean overlap = false;
                        
                        if (newCheckIn != null && newCheckOut != null) {
                            // Cả check-in và check-out: kiểm tra overlap khoảng thời gian
                            overlap = newCheckIn.isBefore(existOut) && newCheckOut.isAfter(existIn);
                        } else if (newCheckIn != null) {
                            // Chỉ có check-in: kiểm tra xem có bản ghi nào nằm sau check-in này không
                            // Conflict nếu: newCheckIn < existOut (có bản ghi kết thúc sau check-in mới)
                            overlap = newCheckIn.isBefore(existOut);
                        } else if (newCheckOut != null) {
                            // Chỉ có check-out: kiểm tra xem có bản ghi nào nằm trước check-out này không  
                            // Conflict nếu: newCheckOut > existIn (có bản ghi bắt đầu trước check-out mới)
                            overlap = newCheckOut.isAfter(existIn);
                        }

                        if (overlap) {
                            log.setOldCheckIn(existIn.toLocalTime());
                            log.setOldCheckOut(existOut.toLocalTime());
                            if (newCheckIn != null && newCheckOut != null) {
                                errorMessage = String.format("Attendance overlaps with existing record (%s - %s)", 
                                    existIn.toLocalTime().toString(), existOut.toLocalTime().toString());
                            } else if (newCheckIn != null) {
                                errorMessage = String.format("Check-in conflicts with existing record starting at %s", 
                                    existIn.toLocalTime().toString());
                            } else {
                                errorMessage = String.format("Check-out conflicts with existing record ending at %s", 
                                    existOut.toLocalTime().toString());
                            }
                            log.setError(errorMessage);
                            hasConflict = true;
                            break;
                        }
                    }

                    // Kiểm tra conflict với unpaired records
                    if (!hasConflict) {
                        // Kiểm tra với unpaired check-ins
                        for (LocalDateTime extraIn : remainingIns) {
                            if (newCheckIn != null) {
                                // Chỉ có check-in: conflict nếu có unpaired check-in sau nó
                                if (newCheckIn.isBefore(extraIn)) {
                                    errorMessage = String.format("Check-in conflicts with existing unpaired check-in at %s", 
                                        extraIn.toLocalTime().toString());
                                    log.setError(errorMessage);
                                    hasConflict = true;
                                    break;
                                }
                            } else if (newCheckOut != null) {
                                // Chỉ có check-out: conflict nếu có unpaired check-in trước nó
                                if (newCheckOut.isAfter(extraIn)) {
                                    errorMessage = String.format("Check-out conflicts with existing unpaired check-in at %s", 
                                        extraIn.toLocalTime().toString());
                                    log.setError(errorMessage);
                                    hasConflict = true;
                                    break;
                                }
                            }
                        }

                        // Kiểm tra với unpaired check-outs
                        if (!hasConflict) {
                            for (LocalDateTime extraOut : remainingOuts) {
                                if (newCheckIn != null) {
                                    // Chỉ có check-in: conflict nếu có unpaired check-out sau nó
                                    if (newCheckIn.isBefore(extraOut)) {
                                        errorMessage = String.format("Check-in conflicts with existing unpaired check-out at %s", 
                                            extraOut.toLocalTime().toString());
                                        log.setError(errorMessage);
                                        hasConflict = true;
                                        break;
                                    }
                                } else if (newCheckOut != null) {
                                    // Chỉ có check-out: conflict nếu có unpaired check-out trước nó
                                    if (newCheckOut.isAfter(extraOut)) {
                                        errorMessage = String.format("Check-out conflicts with existing unpaired check-out at %s", 
                                            extraOut.toLocalTime().toString());
                                        log.setError(errorMessage);
                                        hasConflict = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if (hasConflict) {
                    invalidLogs.add(log);
                } else {
                    validLogs.add(log);
                }
            }
        }

        return result;
    }

    public Map<String, List<AttendanceLogDto>> validateAppealLogs(List<AttendanceLogDto> editedLogs, 
            List<Map<String, Map<String, String>>> originalRecords) throws SQLException {
        Map<String, List<AttendanceLogDto>> result = new HashMap<>();
        List<AttendanceLogDto> validLogs = new ArrayList<>();
        List<AttendanceLogDto> invalidLogs = new ArrayList<>();
        result.put("valid", validLogs);
        result.put("invalid", invalidLogs);

        if (editedLogs == null || editedLogs.isEmpty()) {
            return result;
        }

        // Allowed working time range
        LocalTime MIN_TIME = LocalTime.of(6, 0);
        LocalTime MAX_TIME = LocalTime.of(23, 59);

        // Extract original record times to exclude from conflict checking
        List<Pair<LocalDateTime, LocalDateTime>> originalPairs = new ArrayList<>();
        if (originalRecords != null) {
            for (Map<String, Map<String, String>> record : originalRecords) {
                Map<String, String> oldRecord = record.get("oldRecord");
                if (oldRecord != null) {
                    try {
                        LocalDate date = LocalDate.parse(oldRecord.get("date"));
                        String checkInStr = oldRecord.get("checkIn");
                        String checkOutStr = oldRecord.get("checkOut");
                        
                        if (checkInStr != null && !checkInStr.trim().isEmpty() && 
                            checkOutStr != null && !checkOutStr.trim().isEmpty()) {
                            LocalTime checkIn = LocalTime.parse(checkInStr);
                            LocalTime checkOut = LocalTime.parse(checkOutStr);
                            
                            LocalDateTime checkInDateTime = date.atTime(checkIn);
                            LocalDateTime checkOutDateTime;
                            if (checkOut.isBefore(checkIn)) {
                                checkOutDateTime = date.plusDays(1).atTime(checkOut);
                            } else {
                                checkOutDateTime = date.atTime(checkOut);
                            }
                            
                            originalPairs.add(Pair.of(checkInDateTime, checkOutDateTime));
                        }
                    } catch (Exception e) {
                        // Skip invalid original records
                        System.out.println("Warning: Could not parse original record: " + e.getMessage());
                    }
                }
            }
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            for (AttendanceLogDto log : editedLogs) {
                boolean hasConflict = false;
                String errorMessage = null;

                // === Basic validation - Required fields ===
                if (log.getUserId() == null) {
                    errorMessage = "Employee is required";
                    log.setError(errorMessage);
                    invalidLogs.add(log);
                    continue;
                }

                if (log.getDate() == null) {
                    errorMessage = "Date is required";
                    log.setError(errorMessage);
                    invalidLogs.add(log);
                    continue;
                }

                if (log.getCheckIn() == null && log.getCheckOut() == null) {
                    errorMessage = "At least one of check-in or check-out time is required";
                    log.setError(errorMessage);
                    invalidLogs.add(log);
                    continue;
                }

                // Check future date
                if (log.getDate().isAfter(LocalDate.now())) {
                    errorMessage = "Date cannot be in the future";
                    log.setError(errorMessage);
                    invalidLogs.add(log);
                    continue;
                }

                // === Time range validation ===
                if (log.getCheckIn() != null) {
                    if (log.getCheckIn().isBefore(MIN_TIME) || log.getCheckIn().isAfter(MAX_TIME)) {
                        errorMessage = "Check-in time must be between 06:00 and 23:59";
                        log.setError(errorMessage);
                        invalidLogs.add(log);
                        continue;
                    }
                }

                if (log.getCheckOut() != null) {
                    if (log.getCheckOut().isBefore(MIN_TIME) || log.getCheckOut().isAfter(MAX_TIME)) {
                        errorMessage = "Check-out time must be between 06:00 and 23:59";
                        log.setError(errorMessage);
                        invalidLogs.add(log);
                        continue;
                    }
                }

                // === Logical order: check-in < check-out ===
                if (log.getCheckIn() != null && log.getCheckOut() != null) {
                    if (!log.getCheckIn().isBefore(log.getCheckOut())) {
                        errorMessage = "Check-in time must be earlier than check-out time";
                        log.setError(errorMessage);
                        invalidLogs.add(log);
                        continue;
                    }
                }

                // === Check if user exists ===
                String checkUserSql = "SELECT COUNT(1) FROM users WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(checkUserSql)) {
                    stmt.setLong(1, log.getUserId());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) == 0) {
                            errorMessage = "Employee does not exist in the system";
                            log.setError(errorMessage);
                            invalidLogs.add(log);
                            continue;
                        }
                    }
                }

                // === Check if period is locked ===
                String checkPeriodSql = """
                SELECT COALESCE(tp.is_locked, FALSE)
                FROM timesheet_periods tp
                WHERE ? BETWEEN tp.date_start AND tp.date_end
                """;
                try (PreparedStatement stmt = conn.prepareStatement(checkPeriodSql)) {
                    stmt.setDate(1, java.sql.Date.valueOf(log.getDate()));
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next() && rs.getBoolean(1)) {
                            errorMessage = "Timesheet period is locked for this date";
                            log.setError(errorMessage);
                            invalidLogs.add(log);
                            continue;
                        }
                    }
                }

                LocalDateTime newCheckIn = log.getCheckIn() != null ? log.getDate().atTime(log.getCheckIn()) : null;
                LocalDateTime newCheckOut = null;
                if (log.getCheckOut() != null) {
                    if (log.getCheckIn() != null && log.getCheckOut().isBefore(log.getCheckIn())) {
                        newCheckOut = log.getDate().plusDays(1).atTime(log.getCheckOut());
                    } else {
                        newCheckOut = log.getDate().atTime(log.getCheckOut());
                    }
                }

                // Lấy tất cả log của user trong cùng ngày
                String sql = "SELECT checked_at, check_type FROM attendance_logs WHERE user_id = ? AND DATE(checked_at) = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setLong(1, log.getUserId());
                    stmt.setDate(2, java.sql.Date.valueOf(log.getDate()));

                    List<LocalDateTime> ins = new ArrayList<>();
                    List<LocalDateTime> outs = new ArrayList<>();

                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            String type = rs.getString("check_type");
                            Timestamp ts = rs.getTimestamp("checked_at");
                            if (ts == null || type == null) {
                                continue;
                            }

                            if ("IN".equalsIgnoreCase(type)) {
                                ins.add(ts.toLocalDateTime());
                            } else if ("OUT".equalsIgnoreCase(type)) {
                                outs.add(ts.toLocalDateTime());
                            }
                        }
                    }

                    // Sắp xếp thời gian ascending
                    ins.sort(Comparator.naturalOrder());
                    outs.sort(Comparator.naturalOrder());

                    // Ghép cặp theo logic: check-in đầu tiên → check-out gần nhất
                    List<Pair<LocalDateTime, LocalDateTime>> existingPairs = new ArrayList<>();
                    List<LocalDateTime> remainingIns = new ArrayList<>(ins);
                    List<LocalDateTime> remainingOuts = new ArrayList<>(outs);

                    while (!remainingIns.isEmpty()) {
                        LocalDateTime in = remainingIns.remove(0);
                        LocalDateTime out = null;

                        for (Iterator<LocalDateTime> it = remainingOuts.iterator(); it.hasNext();) {
                            LocalDateTime candidateOut = it.next();
                            if (!candidateOut.isBefore(in)) { // out >= in
                                out = candidateOut;
                                it.remove();
                                break;
                            }
                        }

                        // Nếu không tìm được check-out phù hợp → out = in + 8h
                        if (out == null) {
                            out = in.plusHours(8);
                        }

                        existingPairs.add(Pair.of(in, out));
                    }

                    // Filter out original pairs that are being replaced
                    List<Pair<LocalDateTime, LocalDateTime>> filteredPairs = new ArrayList<>();
                    for (Pair<LocalDateTime, LocalDateTime> existingPair : existingPairs) {
                        boolean isOriginalRecord = false;
                        for (Pair<LocalDateTime, LocalDateTime> originalPair : originalPairs) {
                            // Check if this existing pair matches any original record being replaced
                            // Use a tolerance of 1 minute to account for slight differences in parsing/rounding
                            LocalDateTime existIn = existingPair.getLeft();
                            LocalDateTime existOut = existingPair.getRight();
                            LocalDateTime origIn = originalPair.getLeft();
                            LocalDateTime origOut = originalPair.getRight();
                            
                            boolean inMatches = Math.abs(existIn.toLocalTime().toSecondOfDay() - origIn.toLocalTime().toSecondOfDay()) <= 60;
                            boolean outMatches = Math.abs(existOut.toLocalTime().toSecondOfDay() - origOut.toLocalTime().toSecondOfDay()) <= 60;
                            boolean sameDate = existIn.toLocalDate().equals(origIn.toLocalDate());
                            
                            if (sameDate && inMatches && outMatches) {
                                isOriginalRecord = true;
                                System.out.println("Excluding original record from conflict check: " + 
                                    origIn.toLocalTime() + " - " + origOut.toLocalTime() + 
                                    " (matched with existing: " + existIn.toLocalTime() + " - " + existOut.toLocalTime() + ")");
                                break;
                            }
                        }
                        if (!isOriginalRecord) {
                            filteredPairs.add(existingPair);
                        }
                    }

                    // Kiểm tra trùng với các cặp còn lại (sau khi loại trừ bản ghi gốc)
                    for (Pair<LocalDateTime, LocalDateTime> pair : filteredPairs) {
                        LocalDateTime existIn = pair.getLeft();
                        LocalDateTime existOut = pair.getRight();

                        boolean overlap = false;
                        if (newCheckIn != null && newCheckOut != null) {
                            overlap = newCheckIn.isBefore(existOut) && newCheckOut.isAfter(existIn);
                        } else if (newCheckIn != null) {
                            overlap = !newCheckIn.isBefore(existIn) && !newCheckIn.isAfter(existOut);
                        } else if (newCheckOut != null) {
                            overlap = !newCheckOut.isBefore(existIn) && !newCheckOut.isAfter(existOut);
                        }

                        if (overlap) {
                            log.setOldCheckIn(existIn.toLocalTime());
                            log.setOldCheckOut(existOut.toLocalTime());
                            errorMessage = String.format("Attendance overlaps with existing record (%s - %s)", 
                                existIn.toLocalTime().toString(), existOut.toLocalTime().toString());
                            log.setError(errorMessage);
                            hasConflict = true;
                            System.out.println("Invalid because overlap: " + log.getEmployeeName()
                                    + ", newCheckIn=" + newCheckIn + ", newCheckOut=" + newCheckOut
                                    + ", existIn=" + existIn + ", existOut=" + existOut);
                            break;
                        }
                    }

                    // Kiểm tra các check-in/check-out dư thừa (không ghép được)
                    // Note: For appeal requests, we should be more lenient with unpaired records
                    // since the user is trying to fix existing data
                }

                if (hasConflict) {
                    invalidLogs.add(log);
                } else {
                    validLogs.add(log);
                }
            }
        }

        return result;
    }

    public Map<String, List<AttendanceLogDto>> validateAndImportExcelLogs(List<AttendanceLogDto> excelLogs) throws SQLException {
        Map<String, List<AttendanceLogDto>> result = new HashMap<>();
        List<AttendanceLogDto> validLogs = new ArrayList<>();
        List<AttendanceLogDto> invalidLogs = new ArrayList<>();
        result.put("valid", validLogs);
        result.put("invalid", invalidLogs);

        if (excelLogs == null || excelLogs.isEmpty()) {
            return result;
        }

        // Bước 1: Lọc spam và làm sạch dữ liệu
        List<AttendanceLogDto> cleanLogs = AttendanceService.filterSpamAndCleanLogs(excelLogs);

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Bước 2: Validate các logs đã được làm sạch với DB
            for (AttendanceLogDto log : cleanLogs) {
                boolean hasConflict = false;

                // -----------------------------
                // Kiểm tra bắt buộc
                // -----------------------------
                if (log.getUserId() == null) {
                    log.setError("Invalid: userId is missing");
                    invalidLogs.add(log);
                    continue;
                }
                if (log.getDate() == null) {
                    log.setError("Invalid: date is missing");
                    invalidLogs.add(log);
                    continue;
                }
                if (log.getCheckIn() == null && log.getCheckOut() == null) {
                    log.setError("Invalid: at least one of check-in or check-out time is required");
                    invalidLogs.add(log);
                    continue;
                }
                if (log.getDate().isAfter(LocalDate.now())) {
                    log.setError("Invalid: date is in the future");
                    invalidLogs.add(log);
                    continue;
                }

                // -----------------------------
                // Kiểm tra user tồn tại
                // -----------------------------
                String checkUserSql = "SELECT COUNT(1) FROM users WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(checkUserSql)) {
                    stmt.setLong(1, log.getUserId());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) == 0) {
                            log.setError("Invalid: userId does not exist in the system");
                            invalidLogs.add(log);
                            continue;
                        }
                    }
                }

                // -----------------------------
                // Kiểm tra kỳ công đã khóa
                // -----------------------------
                String checkPeriodSql = """
                SELECT COALESCE(tp.is_locked, FALSE)
                FROM timesheet_periods tp
                WHERE ? BETWEEN tp.date_start AND tp.date_end
                """;
                try (PreparedStatement stmt = conn.prepareStatement(checkPeriodSql)) {
                    stmt.setDate(1, java.sql.Date.valueOf(log.getDate()));
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next() && rs.getBoolean(1)) {
                            log.setError("Invalid: period is locked");
                            invalidLogs.add(log);
                            continue;
                        }
                    }
                }

                // -----------------------------
                // Chuẩn bị check trùng IN/OUT
                // -----------------------------
                LocalDateTime newCheckIn = log.getCheckIn() != null ? log.getDate().atTime(log.getCheckIn()) : null;
                LocalDateTime newCheckOut = null;
                if (log.getCheckOut() != null) {
                    if (log.getCheckIn() != null && log.getCheckOut().isBefore(log.getCheckIn())) {
                        newCheckOut = log.getDate().plusDays(1).atTime(log.getCheckOut());
                    } else {
                        newCheckOut = log.getDate().atTime(log.getCheckOut());
                    }
                }

                // Lấy tất cả log của user cùng ngày
                String sql = "SELECT checked_at, check_type FROM attendance_logs WHERE user_id = ? AND DATE(checked_at) = ?";
                List<LocalDateTime> ins = new ArrayList<>();
                List<LocalDateTime> outs = new ArrayList<>();

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setLong(1, log.getUserId());
                    stmt.setDate(2, java.sql.Date.valueOf(log.getDate()));
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            String type = rs.getString("check_type");
                            Timestamp ts = rs.getTimestamp("checked_at");
                            if (ts == null || type == null) {
                                continue;
                            }

                            if ("IN".equalsIgnoreCase(type)) {
                                ins.add(ts.toLocalDateTime());
                            } else if ("OUT".equalsIgnoreCase(type)) {
                                outs.add(ts.toLocalDateTime());
                            }
                        }
                    }
                }

                ins.sort(Comparator.naturalOrder());
                outs.sort(Comparator.naturalOrder());

                List<Pair<LocalDateTime, LocalDateTime>> existingPairs = new ArrayList<>();
                List<LocalDateTime> remainingIns = new ArrayList<>(ins);
                List<LocalDateTime> remainingOuts = new ArrayList<>(outs);

                while (!remainingIns.isEmpty()) {
                    LocalDateTime in = remainingIns.remove(0);
                    LocalDateTime out = null;
                    for (Iterator<LocalDateTime> it = remainingOuts.iterator(); it.hasNext();) {
                        LocalDateTime candidateOut = it.next();
                        if (!candidateOut.isBefore(in)) {
                            out = candidateOut;
                            it.remove();
                            break;
                        }
                    }
                    if (out == null) {
                        out = in.plusHours(8);
                    }
                    existingPairs.add(Pair.of(in, out));
                }

                // -----------------------------
                // Kiểm tra conflict với existing pairs
                // -----------------------------
                for (Pair<LocalDateTime, LocalDateTime> pair : existingPairs) {
                    LocalDateTime existIn = pair.getLeft();
                    LocalDateTime existOut = pair.getRight();

                    boolean overlap = false;
                    
                    if (newCheckIn != null && newCheckOut != null) {
                        // Cả check-in và check-out: kiểm tra overlap khoảng thời gian
                        overlap = newCheckIn.isBefore(existOut) && newCheckOut.isAfter(existIn);
                    } else if (newCheckIn != null) {
                        // Chỉ có check-in: kiểm tra xem có bản ghi nào nằm sau check-in này không
                        overlap = newCheckIn.isBefore(existOut);
                    } else if (newCheckOut != null) {
                        // Chỉ có check-out: kiểm tra xem có bản ghi nào nằm trước check-out này không
                        overlap = newCheckOut.isAfter(existIn);
                    }

                    if (overlap) {
                        log.setOldCheckIn(existIn.toLocalTime());
                        log.setOldCheckOut(existOut.toLocalTime());
                        if (newCheckIn != null && newCheckOut != null) {
                            log.setError("Invalid: duplicate or overlapping attendance");
                        } else if (newCheckIn != null) {
                            log.setError("Invalid: check-in conflicts with existing record");
                        } else {
                            log.setError("Invalid: check-out conflicts with existing record");
                        }
                        hasConflict = true;
                        break;
                    }
                }
                
                // Kiểm tra conflict với unpaired records
                if (!hasConflict) {
                    // Kiểm tra với unpaired check-ins
                    for (LocalDateTime extraIn : remainingIns) {
                        if (newCheckIn != null) {
                            // Chỉ có check-in: conflict nếu có unpaired check-in sau nó
                            if (newCheckIn.isBefore(extraIn)) {
                                log.setError("Invalid: check-in conflicts with existing unpaired check-in");
                                hasConflict = true;
                                break;
                            }
                        } else if (newCheckOut != null) {
                            // Chỉ có check-out: conflict nếu có unpaired check-in trước nó
                            if (newCheckOut.isAfter(extraIn)) {
                                log.setError("Invalid: check-out conflicts with existing unpaired check-in");
                                hasConflict = true;
                                break;
                            }
                        }
                    }
                    
                    // Kiểm tra với unpaired check-outs
                    if (!hasConflict) {
                        for (LocalDateTime extraOut : remainingOuts) {
                            if (newCheckIn != null) {
                                // Chỉ có check-in: conflict nếu có unpaired check-out sau nó
                                if (newCheckIn.isBefore(extraOut)) {
                                    log.setError("Invalid: check-in conflicts with existing unpaired check-out");
                                    hasConflict = true;
                                    break;
                                }
                            } else if (newCheckOut != null) {
                                // Chỉ có check-out: conflict nếu có unpaired check-out trước nó
                                if (newCheckOut.isAfter(extraOut)) {
                                    log.setError("Invalid: check-out conflicts with existing unpaired check-out");
                                    hasConflict = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (hasConflict) {
                    invalidLogs.add(log);
                } else {
                    validLogs.add(log);
                }
            }
        }

        return result;
    }

    public Map<String, List<AttendanceLogDto>> validateExcelInternalConsistency(List<AttendanceLogDto> excelLogs) {
        Map<String, List<AttendanceLogDto>> result = new HashMap<>();
        List<AttendanceLogDto> validLogs = new ArrayList<>();
        List<AttendanceLogDto> invalidLogs = new ArrayList<>();
        result.put("valid", validLogs);
        result.put("invalid", invalidLogs);

        if (excelLogs == null || excelLogs.isEmpty()) {
            return result;
        }

        // Bước 1: Lọc spam và làm sạch dữ liệu
        List<AttendanceLogDto> cleanLogs = AttendanceService.filterSpamAndCleanLogs(excelLogs);

        // Allowed working time range
        LocalTime MIN_TIME = LocalTime.of(6, 0);
        LocalTime MAX_TIME = LocalTime.of(23, 59);

        // Map để nhóm log theo user + date
        Map<String, List<AttendanceLogDto>> logsByUserDate = new HashMap<>();

        // Bước 2: Validate các logs đã được làm sạch
        for (AttendanceLogDto log : cleanLogs) {

            // === Basic validation ===
            if (log.getUserId() == null) {
                log.setError("Invalid: userId is missing");
                invalidLogs.add(log);
                continue;
            }

            if (log.getDate() == null) {
                log.setError("Invalid: date is missing");
                invalidLogs.add(log);
                continue;
            }

            // Check future date
            if (log.getDate().isAfter(LocalDate.now())) {
                log.setError("Invalid: date is in the future");
                invalidLogs.add(log);
                continue;
            }

            // === Time range validation ===
            if (log.getCheckIn() != null) {
                if (log.getCheckIn().isBefore(MIN_TIME) || log.getCheckIn().isAfter(MAX_TIME)) {
                    log.setError("Invalid: check-in must be between 06:00 and 23:59");
                    invalidLogs.add(log);
                    continue;
                }
            }

            if (log.getCheckOut() != null) {
                if (log.getCheckOut().isBefore(MIN_TIME) || log.getCheckOut().isAfter(MAX_TIME)) {
                    log.setError("Invalid: check-out must be between 06:00 and 23:59");
                    invalidLogs.add(log);
                    continue;
                }
            }

            // === Logical order: check-in < check-out ===
            if (log.getCheckIn() != null && log.getCheckOut() != null) {
                if (!log.getCheckIn().isBefore(log.getCheckOut())) {
                    log.setError("Invalid: check-in must be earlier than check-out");
                    invalidLogs.add(log);
                    continue;
                }
            }

            // Nếu qua hết kiểm tra, thêm vào nhóm user-date
            String key = log.getUserId() + "_" + log.getDate();
            logsByUserDate.computeIfAbsent(key, k -> new ArrayList<>()).add(log);
        }

        // === Check for overlapping logs in the same day/user ===
        for (Map.Entry<String, List<AttendanceLogDto>> entry : logsByUserDate.entrySet()) {
            List<AttendanceLogDto> group = entry.getValue();

            // Chuẩn hóa thành thời điểm checkIn/checkOut
            List<Pair<AttendanceLogDto, Pair<LocalDateTime, LocalDateTime>>> times = new ArrayList<>();
            for (AttendanceLogDto log : group) {
                LocalDateTime checkIn = log.getCheckIn() != null ? log.getDate().atTime(log.getCheckIn()) : null;
                LocalDateTime checkOut = null;
                if (log.getCheckOut() != null) {
                    // Nếu checkout < checkin, coi là qua ngày hôm sau
                    if (log.getCheckIn() != null && log.getCheckOut().isBefore(log.getCheckIn())) {
                        checkOut = log.getDate().plusDays(1).atTime(log.getCheckOut());
                    } else {
                        checkOut = log.getDate().atTime(log.getCheckOut());
                    }
                }
                times.add(Pair.of(log, Pair.of(checkIn, checkOut)));
            }

            // Kiểm tra trùng lặp giữa các khoảng thời gian
            int n = times.size();
            for (int i = 0; i < n; i++) {
                LocalDateTime in1 = times.get(i).getRight().getLeft();
                LocalDateTime out1 = times.get(i).getRight().getRight();

                for (int j = i + 1; j < n; j++) {
                    LocalDateTime in2 = times.get(j).getRight().getLeft();
                    LocalDateTime out2 = times.get(j).getRight().getRight();

                    boolean overlap = false;
                    if (in1 != null && out1 != null && in2 != null && out2 != null) {
                        overlap = in1.isBefore(out2) && out1.isAfter(in2);
                    }

                    if (overlap) {
                        times.get(i).getLeft().setError("Invalid: duplicate attendance in Excel");
                        times.get(j).getLeft().setError("Invalid: duplicate attendance in Excel");
                    }
                }
            }

            // Phân loại valid/invalid
            for (Pair<AttendanceLogDto, Pair<LocalDateTime, LocalDateTime>> pair : times) {
                AttendanceLogDto log = pair.getLeft();
                if (log.getError() != null) {
                    invalidLogs.add(log);
                } else {
                    validLogs.add(log);
                }
            }
        }

        return result;
    }

    /**
     * Tìm attendance logs theo userId và date
     *
     * @param userId ID của user
     * @param date Ngày cần tìm
     * @return Danh sách attendance logs trong ngày
     */
    public List<AttendanceLog> findByUserIdAndDate(Long userId, LocalDate date) {
        List<AttendanceLog> logs = new ArrayList<>();
        String sql = "SELECT id, user_id, check_type, checked_at, source, note, period_id, created_at "
                + "FROM attendance_logs "
                + "WHERE user_id = ? AND DATE(checked_at) = ? "
                + "ORDER BY checked_at";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setDate(2, java.sql.Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToEntity(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding attendance logs by userId and date: "
                    + userId + ", " + date, e);
        }

        return logs;
    }
}
