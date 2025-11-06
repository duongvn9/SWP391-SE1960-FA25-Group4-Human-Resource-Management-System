package group4.hrms.dao;

import group4.hrms.dto.AttendanceLogDto;
import group4.hrms.service.AttendanceService;
import group4.hrms.util.DatabaseUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceLogDaoTest {

    private AttendanceLogDao attendanceLogDao;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        attendanceLogDao = new AttendanceLogDao();
    }

    // ✅ Các trường hợp hợp lệ hoặc trung tính
    @Test
    void testValidateManualLogs_Case1_1_NullInput() throws SQLException {
        // Case 1.1: manualLogs = null
        Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(null);

        assertNotNull(result);
        assertTrue(result.containsKey("valid"));
        assertTrue(result.containsKey("invalid"));
        assertTrue(result.get("valid").isEmpty());
        assertTrue(result.get("invalid").isEmpty());
    }

    @Test
    void testValidateManualLogs_Case1_2_EmptyList() throws SQLException {
        // Case 1.2: manualLogs = [] (danh sách rỗng)
        List<AttendanceLogDto> emptyList = new ArrayList<>();
        Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(emptyList);

        assertNotNull(result);
        assertTrue(result.containsKey("valid"));
        assertTrue(result.containsKey("invalid"));
        assertTrue(result.get("valid").isEmpty());
        assertTrue(result.get("invalid").isEmpty());
    }

    @Test
    void testValidateManualLogs_Case4_3_CheckInAt2359() throws SQLException {
        // Case 4.3: Check-in = 23:59 (trong giới hạn cho phép)
        try (MockedStatic<DatabaseUtil> mockedDatabaseUtil = mockStatic(DatabaseUtil.class); MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {

            setupValidUserAndUnlockedPeriod(mockedDatabaseUtil);

            AttendanceLogDto log = createValidLog();
            log.setCheckIn(LocalTime.of(23, 59));
            log.setCheckOut(null);

            List<AttendanceLogDto> logs = Arrays.asList(log);

            // Mock AttendanceService.filterSpamAndCleanLogs to return the input as-is
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(1, result.get("valid").size());
            assertEquals(0, result.get("invalid").size());
        }
    }

    @Test
    void testValidateManualLogs_Case4_5_CheckOutAt2359() throws SQLException {
        // Case 4.5: Check-out = 23:59 (trong giới hạn cho phép)
        try (MockedStatic<DatabaseUtil> mockedDatabaseUtil = mockStatic(DatabaseUtil.class); MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {

            setupValidUserAndUnlockedPeriod(mockedDatabaseUtil);

            AttendanceLogDto log = createValidLog();
            log.setCheckIn(null);
            log.setCheckOut(LocalTime.of(23, 59));

            List<AttendanceLogDto> logs = Arrays.asList(log);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(1, result.get("valid").size());
            assertEquals(0, result.get("invalid").size());
        }
    }

    @Test
    void testValidateManualLogs_Case5_2_ValidCheckInCheckOut() throws SQLException {
        // Case 5.2: Check-in = 08:00, check-out = 17:00 (check-in < check-out)
        try (MockedStatic<DatabaseUtil> mockedDatabaseUtil = mockStatic(DatabaseUtil.class); MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {

            setupValidUserAndUnlockedPeriod(mockedDatabaseUtil);

            AttendanceLogDto log = createValidLog();
            log.setCheckIn(LocalTime.of(8, 0));
            log.setCheckOut(LocalTime.of(17, 0));

            List<AttendanceLogDto> logs = Arrays.asList(log);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(1, result.get("valid").size());
            assertEquals(0, result.get("invalid").size());
        }
    }

    @Test
    void testValidateManualLogs_Case6_2_UserExistsInDB() throws SQLException {
        // Case 6.2: userId tồn tại trong DB
        try (MockedStatic<DatabaseUtil> mockedDatabaseUtil = mockStatic(DatabaseUtil.class); MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {

            setupValidUserAndUnlockedPeriod(mockedDatabaseUtil);

            AttendanceLogDto log = createValidLog();

            List<AttendanceLogDto> logs = Arrays.asList(log);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(1, result.get("valid").size());
            assertEquals(0, result.get("invalid").size());
        }
    }

    @Test
    void testValidateManualLogs_Case7_2_PeriodNotLocked() throws SQLException {
        // Case 7.2: Period không bị khóa (is_locked = false)
        try (MockedStatic<DatabaseUtil> mockedDatabaseUtil = mockStatic(DatabaseUtil.class); MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {

            setupValidUserAndUnlockedPeriod(mockedDatabaseUtil);

            AttendanceLogDto log = createValidLog();

            List<AttendanceLogDto> logs = Arrays.asList(log);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(1, result.get("valid").size());
            assertEquals(0, result.get("invalid").size());
        }
    }

    // ❌ Các trường hợp không hợp lệ
    @Test
    void testValidateManualLogs_Case2_1_MissingUserId() throws SQLException {
        // Case 2.1: Thiếu userId (userId = null)
        try (MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {
            AttendanceLogDto log = createValidLog();
            log.setUserId(null);

            List<AttendanceLogDto> logs = Arrays.asList(log);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(0, result.get("valid").size());
            assertEquals(1, result.get("invalid").size());
            assertEquals("Employee is required", result.get("invalid").get(0).getError());
        }
    }

    @Test
    void testValidateManualLogs_Case2_2_MissingDate() throws SQLException {
        // Case 2.2: Thiếu date (date = null)
        try (MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {
            AttendanceLogDto log = createValidLog();
            log.setDate(null);

            List<AttendanceLogDto> logs = Arrays.asList(log);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(0, result.get("valid").size());
            assertEquals(1, result.get("invalid").size());
            assertEquals("Date is required", result.get("invalid").get(0).getError());
        }
    }

    @Test
    void testValidateManualLogs_Case2_3_MissingBothCheckInAndCheckOut() throws SQLException {
        // Case 2.3: Thiếu cả checkIn và checkOut (null - null)
        try (MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {
            AttendanceLogDto log = createValidLog();
            log.setCheckIn(null);
            log.setCheckOut(null);

            List<AttendanceLogDto> logs = Arrays.asList(log);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(0, result.get("valid").size());
            assertEquals(1, result.get("invalid").size());
            assertEquals("At least one of check-in or check-out time is required", result.get("invalid").get(0).getError());
        }
    }

    @Test
    void testValidateManualLogs_Case3_1_FutureDate() throws SQLException {
        // Case 3.1: Ngày trong tương lai
        try (MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {
            AttendanceLogDto log = createValidLog();
            log.setDate(LocalDate.now().plusDays(1));

            List<AttendanceLogDto> logs = Arrays.asList(log);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(0, result.get("valid").size());
            assertEquals(1, result.get("invalid").size());
            assertEquals("Date cannot be in the future", result.get("invalid").get(0).getError());
        }
    }

    @Test
    void testValidateManualLogs_Case4_1_CheckInTooEarly() throws SQLException {
        // Case 4.1: Check-in = 05:59 (< 06:00)
        try (MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {
            AttendanceLogDto log = createValidLog();
            log.setCheckIn(LocalTime.of(5, 59));

            List<AttendanceLogDto> logs = Arrays.asList(log);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(0, result.get("valid").size());
            assertEquals(1, result.get("invalid").size());
            assertEquals("Check-in time must be between 06:00 and 23:59", result.get("invalid").get(0).getError());
        }
    }

    @Test
    void testValidateManualLogs_Case4_2_CheckInAt0000() throws SQLException {
        // Case 4.2: Check-in = 00:00 (quá sớm)
        try (MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {
            AttendanceLogDto log = createValidLog();
            log.setCheckIn(LocalTime.of(0, 0));

            List<AttendanceLogDto> logs = Arrays.asList(log);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(0, result.get("valid").size());
            assertEquals(1, result.get("invalid").size());
            assertEquals("Check-in time must be between 06:00 and 23:59", result.get("invalid").get(0).getError());
        }
    }

    @Test
    void testValidateManualLogs_Case4_4_CheckOutAt0000() throws SQLException {
        // Case 4.4: Check-out = 00:00 (quá sớm)
        try (MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {
            AttendanceLogDto log = createValidLog();
            log.setCheckOut(LocalTime.of(0, 0));

            List<AttendanceLogDto> logs = Arrays.asList(log);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(0, result.get("valid").size());
            assertEquals(1, result.get("invalid").size());
            assertEquals("Check-out time must be between 06:00 and 23:59", result.get("invalid").get(0).getError());
        }
    }

    @Test
    void testValidateManualLogs_Case5_1_CheckInAfterCheckOut() throws SQLException {
        // Case 5.1: Check-in ≥ check-out (09:00 ≥ 08:59)
        try (MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {
            AttendanceLogDto log = createValidLog();
            log.setCheckIn(LocalTime.of(9, 0));
            log.setCheckOut(LocalTime.of(8, 59));

            List<AttendanceLogDto> logs = Arrays.asList(log);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(0, result.get("valid").size());
            assertEquals(1, result.get("invalid").size());
            assertEquals("Check-in time must be earlier than check-out time", result.get("invalid").get(0).getError());
        }
    }

    @Test
    void testValidateManualLogs_Case6_1_UserNotExists() throws SQLException {
        // Case 6.1: userId không tồn tại (không có trong DB)
        try (MockedStatic<DatabaseUtil> mockedDatabaseUtil = mockStatic(DatabaseUtil.class); MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {

            mockedDatabaseUtil.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

            // Mock user không tồn tại
            when(mockConnection.prepareStatement(contains("SELECT COUNT(1) FROM users WHERE id = ?"))).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt(1)).thenReturn(0); // User không tồn tại

            AttendanceLogDto log = createValidLog();

            List<AttendanceLogDto> logs = Arrays.asList(log);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(0, result.get("valid").size());
            assertEquals(1, result.get("invalid").size());
            assertEquals("Employee does not exist in the system", result.get("invalid").get(0).getError());
        }
    }

    @Test
    void testValidateManualLogs_Case7_1_PeriodLocked() throws SQLException {
        // Case 7.1: Kỳ công bị khóa (is_locked = true)
        try (MockedStatic<DatabaseUtil> mockedDatabaseUtil = mockStatic(DatabaseUtil.class); MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {

            mockedDatabaseUtil.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

            // Mock user tồn tại
            PreparedStatement userStmt = mock(PreparedStatement.class);
            ResultSet userRs = mock(ResultSet.class);
            when(mockConnection.prepareStatement(contains("SELECT COUNT(1) FROM users WHERE id = ?"))).thenReturn(userStmt);
            when(userStmt.executeQuery()).thenReturn(userRs);
            when(userRs.next()).thenReturn(true);
            when(userRs.getInt(1)).thenReturn(1); // User tồn tại

            // Mock period bị khóa
            PreparedStatement periodStmt = mock(PreparedStatement.class);
            ResultSet periodRs = mock(ResultSet.class);
            when(mockConnection.prepareStatement(contains("SELECT COALESCE(tp.is_locked, FALSE)"))).thenReturn(periodStmt);
            when(periodStmt.executeQuery()).thenReturn(periodRs);
            when(periodRs.next()).thenReturn(true);
            when(periodRs.getBoolean(1)).thenReturn(true); // Period bị khóa

            AttendanceLogDto log = createValidLog();

            List<AttendanceLogDto> logs = Arrays.asList(log);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(0, result.get("valid").size());
            assertEquals(1, result.get("invalid").size());
            assertEquals("Timesheet period is locked for this date", result.get("invalid").get(0).getError());
        }
    }

    // Helper methods
    /**
     * Tạo một AttendanceLogDto hợp lệ để sử dụng trong test
     */
    private AttendanceLogDto createValidLog() {
        AttendanceLogDto log = new AttendanceLogDto();
        log.setUserId(1L);
        log.setDate(LocalDate.now().minusDays(1)); // Ngày hôm qua
        log.setCheckIn(LocalTime.of(8, 0));
        log.setCheckOut(LocalTime.of(17, 0));
        return log;
    }

    /**
     * Setup mock cho user tồn tại và period không bị khóa
     */
    private void setupValidUserAndUnlockedPeriod(MockedStatic<DatabaseUtil> mockedDatabaseUtil) throws SQLException {
        mockedDatabaseUtil.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

        // Mock user tồn tại
        PreparedStatement userStmt = mock(PreparedStatement.class);
        ResultSet userRs = mock(ResultSet.class);
        when(mockConnection.prepareStatement(contains("SELECT COUNT(1) FROM users WHERE id = ?"))).thenReturn(userStmt);
        when(userStmt.executeQuery()).thenReturn(userRs);
        when(userRs.next()).thenReturn(true);
        when(userRs.getInt(1)).thenReturn(1); // User tồn tại

        // Mock period không bị khóa
        PreparedStatement periodStmt = mock(PreparedStatement.class);
        ResultSet periodRs = mock(ResultSet.class);
        when(mockConnection.prepareStatement(contains("SELECT COALESCE(tp.is_locked, FALSE)"))).thenReturn(periodStmt);
        when(periodStmt.executeQuery()).thenReturn(periodRs);
        when(periodRs.next()).thenReturn(true);
        when(periodRs.getBoolean(1)).thenReturn(false); // Period không bị khóa

        // Mock không có attendance logs hiện tại
        PreparedStatement attendanceStmt = mock(PreparedStatement.class);
        ResultSet attendanceRs = mock(ResultSet.class);
        when(mockConnection.prepareStatement(contains("SELECT checked_at, check_type FROM attendance_logs"))).thenReturn(attendanceStmt);
        when(attendanceStmt.executeQuery()).thenReturn(attendanceRs);
        when(attendanceRs.next()).thenReturn(false); // Không có logs hiện tại
    }

    // Additional edge case tests
    @Test
    void testValidateManualLogs_MultipleValidLogs() throws SQLException {
        // Test multiple valid logs at once
        try (MockedStatic<DatabaseUtil> mockedDatabaseUtil = mockStatic(DatabaseUtil.class); MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {

            setupValidUserAndUnlockedPeriod(mockedDatabaseUtil);

            AttendanceLogDto log1 = createValidLog();
            log1.setUserId(1L);
            log1.setCheckIn(LocalTime.of(8, 0));
            log1.setCheckOut(LocalTime.of(17, 0));

            AttendanceLogDto log2 = createValidLog();
            log2.setUserId(2L);
            log2.setCheckIn(LocalTime.of(9, 0));
            log2.setCheckOut(LocalTime.of(18, 0));

            List<AttendanceLogDto> logs = Arrays.asList(log1, log2);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(2, result.get("valid").size());
            assertEquals(0, result.get("invalid").size());
        }
    }

    @Test
    void testValidateManualLogs_MixedValidAndInvalidLogs() throws SQLException {
        // Test mix of valid and invalid logs
        try (MockedStatic<DatabaseUtil> mockedDatabaseUtil = mockStatic(DatabaseUtil.class); MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {

            setupValidUserAndUnlockedPeriod(mockedDatabaseUtil);

            // Valid log
            AttendanceLogDto validLog = createValidLog();
            validLog.setCheckIn(LocalTime.of(8, 0));
            validLog.setCheckOut(LocalTime.of(17, 0));

            // Invalid log - missing userId
            AttendanceLogDto invalidLog = createValidLog();
            invalidLog.setUserId(null);

            List<AttendanceLogDto> logs = Arrays.asList(validLog, invalidLog);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(1, result.get("valid").size());
            assertEquals(1, result.get("invalid").size());
            assertEquals("Employee is required", result.get("invalid").get(0).getError());
        }
    }

    @Test
    void testValidateManualLogs_OnlyCheckIn() throws SQLException {
        // Test with only check-in time (no check-out)
        try (MockedStatic<DatabaseUtil> mockedDatabaseUtil = mockStatic(DatabaseUtil.class); MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {

            setupValidUserAndUnlockedPeriod(mockedDatabaseUtil);

            AttendanceLogDto log = createValidLog();
            log.setCheckIn(LocalTime.of(8, 0));
            log.setCheckOut(null);

            List<AttendanceLogDto> logs = Arrays.asList(log);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(1, result.get("valid").size());
            assertEquals(0, result.get("invalid").size());
        }
    }

    @Test
    void testValidateManualLogs_OnlyCheckOut() throws SQLException {
        // Test with only check-out time (no check-in)
        try (MockedStatic<DatabaseUtil> mockedDatabaseUtil = mockStatic(DatabaseUtil.class); MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {

            setupValidUserAndUnlockedPeriod(mockedDatabaseUtil);

            AttendanceLogDto log = createValidLog();
            log.setCheckIn(null);
            log.setCheckOut(LocalTime.of(17, 0));

            List<AttendanceLogDto> logs = Arrays.asList(log);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(1, result.get("valid").size());
            assertEquals(0, result.get("invalid").size());
        }
    }

    @Test
    void testValidateManualLogs_BoundaryTimeValues() throws SQLException {
        // Test boundary values for time validation
        try (MockedStatic<DatabaseUtil> mockedDatabaseUtil = mockStatic(DatabaseUtil.class); MockedStatic<AttendanceService> mockedAttendanceService = mockStatic(AttendanceService.class)) {

            setupValidUserAndUnlockedPeriod(mockedDatabaseUtil);

            // Test minimum valid time (06:00)
            AttendanceLogDto log1 = createValidLog();
            log1.setCheckIn(LocalTime.of(6, 0));
            log1.setCheckOut(null);

            // Test maximum valid time (23:59)
            AttendanceLogDto log2 = createValidLog();
            log2.setUserId(2L);
            log2.setCheckIn(null);
            log2.setCheckOut(LocalTime.of(23, 59));

            List<AttendanceLogDto> logs = Arrays.asList(log1, log2);
            mockedAttendanceService.when(() -> AttendanceService.filterSpamAndCleanLogs(logs)).thenReturn(logs);

            Map<String, List<AttendanceLogDto>> result = attendanceLogDao.validateManualLogs(logs);

            assertEquals(2, result.get("valid").size());
            assertEquals(0, result.get("invalid").size());
        }
    }
}
