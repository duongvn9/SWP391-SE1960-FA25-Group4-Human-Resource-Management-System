package group4.hrms.service;

import group4.hrms.dao.LeaveTypeDao;
import group4.hrms.dao.RequestDao;
import group4.hrms.dto.LeaveRequestDetail;
import group4.hrms.model.LeaveType;
import group4.hrms.model.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Test cho LeaveBalanceService
 * 
 * Mục tiêu: Đạt 100% Statement Coverage và Decision Coverage
 * 
 * Test 2 hàm chính:
 * 1. calculateUsedDays() - Tính số ngày nghỉ đã sử dụng
 * 2. hasSufficientBalance() - Kiểm tra số dư nghỉ phép có đủ không
 * 
 * Các test cases bao phủ tất cả các nhánh điều kiện:
 * - Request với status APPROVED/PENDING/REJECTED
 * - Request có/không có LeaveRequestDetail
 * - Request với leaveTypeCode khớp/không khớp
 * - Request với startDate hợp lệ/không hợp lệ/null
 * - Request với isHalfDay = true/false/null
 * - LeaveType paid/unpaid
 * - LeaveType có defaultDays/unlimited
 * - SQLException xảy ra
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LeaveBalanceService - calculateUsedDays and hasSufficientBalance Tests")
class LeaveBalanceServiceTest {

    @Mock
    private RequestDao requestDao;

    @Mock
    private LeaveTypeDao leaveTypeDao;

    private LeaveBalanceService leaveBalanceService;

    @BeforeEach
    void setUp() {
        leaveBalanceService = new LeaveBalanceService(requestDao, leaveTypeDao);
    }

    // ==================== TEST calculateUsedDays() ====================

    @Test
    @DisplayName("testCalculateUsedDays_ShouldReturnZero_WhenNoRequests")
    void testCalculateUsedDays_ShouldReturnZero_WhenNoRequests() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        int year = 2024;

        when(requestDao.findByUserId(userId)).thenReturn(new ArrayList<>());

        // Act
        double result = leaveBalanceService.calculateUsedDays(userId, leaveTypeCode, year);

        // Assert
        assertEquals(0.0, result);
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testCalculateUsedDays_ShouldCountOnlyApprovedRequests_WhenMultipleStatuses")
    void testCalculateUsedDays_ShouldCountOnlyApprovedRequests_WhenMultipleStatuses() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        int year = 2024;

        // Create APPROVED request
        Request approvedRequest = createRequest(1L, "APPROVED", leaveTypeCode,
                "2024-01-15T09:00:00", "2024-01-15T18:00:00", 1, false);

        // Create PENDING request (should not be counted)
        Request pendingRequest = createRequest(2L, "PENDING", leaveTypeCode,
                "2024-02-15T09:00:00", "2024-02-15T18:00:00", 1, false);

        // Create REJECTED request (should not be counted)
        Request rejectedRequest = createRequest(3L, "REJECTED", leaveTypeCode,
                "2024-03-15T09:00:00", "2024-03-15T18:00:00", 1, false);

        when(requestDao.findByUserId(userId)).thenReturn(Arrays.asList(
                approvedRequest, pendingRequest, rejectedRequest));

        // Act
        double result = leaveBalanceService.calculateUsedDays(userId, leaveTypeCode, year);

        // Assert
        assertEquals(1.0, result); // Only approved request counted
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testCalculateUsedDays_ShouldSkipRequest_WhenLeaveDetailIsNull")
    void testCalculateUsedDays_ShouldSkipRequest_WhenLeaveDetailIsNull() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        int year = 2024;

        Request request = new Request();
        request.setId(1L);
        request.setStatus("APPROVED");
        request.setDetailJson(null); // No detail
        request.setCreatedAt(LocalDateTime.of(2024, 1, 15, 9, 0));

        when(requestDao.findByUserId(userId)).thenReturn(Arrays.asList(request));

        // Act
        double result = leaveBalanceService.calculateUsedDays(userId, leaveTypeCode, year);

        // Assert
        assertEquals(0.0, result); // Request skipped due to null detail
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testCalculateUsedDays_ShouldSkipRequest_WhenLeaveTypeCodeDoesNotMatch")
    void testCalculateUsedDays_ShouldSkipRequest_WhenLeaveTypeCodeDoesNotMatch() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        int year = 2024;

        // Request with different leave type code
        Request request = createRequest(1L, "APPROVED", "SICK",
                "2024-01-15T09:00:00", "2024-01-15T18:00:00", 1, false);

        when(requestDao.findByUserId(userId)).thenReturn(Arrays.asList(request));

        // Act
        double result = leaveBalanceService.calculateUsedDays(userId, leaveTypeCode, year);

        // Assert
        assertEquals(0.0, result); // Request skipped due to different leave type
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testCalculateUsedDays_ShouldCountFullDay_WhenIsHalfDayIsFalse")
    void testCalculateUsedDays_ShouldCountFullDay_WhenIsHalfDayIsFalse() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        int year = 2024;

        Request request = createRequest(1L, "APPROVED", leaveTypeCode,
                "2024-01-15T09:00:00", "2024-01-16T18:00:00", 2, false);

        when(requestDao.findByUserId(userId)).thenReturn(Arrays.asList(request));

        // Act
        double result = leaveBalanceService.calculateUsedDays(userId, leaveTypeCode, year);

        // Assert
        assertEquals(2.0, result); // Full 2 days counted
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testCalculateUsedDays_ShouldCountHalfDay_WhenIsHalfDayIsTrue")
    void testCalculateUsedDays_ShouldCountHalfDay_WhenIsHalfDayIsTrue() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        int year = 2024;

        Request request = createRequest(1L, "APPROVED", leaveTypeCode,
                "2024-01-15T09:00:00", "2024-01-15T13:00:00", 1, true);

        when(requestDao.findByUserId(userId)).thenReturn(Arrays.asList(request));

        // Act
        double result = leaveBalanceService.calculateUsedDays(userId, leaveTypeCode, year);

        // Assert
        assertEquals(0.5, result); // Half day counted
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testCalculateUsedDays_ShouldCountMultipleRequests_WhenMixedFullAndHalfDays")
    void testCalculateUsedDays_ShouldCountMultipleRequests_WhenMixedFullAndHalfDays() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        int year = 2024;

        Request fullDayRequest = createRequest(1L, "APPROVED", leaveTypeCode,
                "2024-01-15T09:00:00", "2024-01-16T18:00:00", 2, false);

        Request halfDayRequest = createRequest(2L, "APPROVED", leaveTypeCode,
                "2024-02-15T09:00:00", "2024-02-15T13:00:00", 1, true);

        when(requestDao.findByUserId(userId)).thenReturn(Arrays.asList(
                fullDayRequest, halfDayRequest));

        // Act
        double result = leaveBalanceService.calculateUsedDays(userId, leaveTypeCode, year);

        // Assert
        assertEquals(2.5, result); // 2.0 + 0.5 = 2.5 days
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testCalculateUsedDays_ShouldSkipRequest_WhenStartDateYearDoesNotMatch")
    void testCalculateUsedDays_ShouldSkipRequest_WhenStartDateYearDoesNotMatch() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        int year = 2024;

        // Request from different year
        Request request = createRequest(1L, "APPROVED", leaveTypeCode,
                "2023-01-15T09:00:00", "2023-01-15T18:00:00", 1, false);

        when(requestDao.findByUserId(userId)).thenReturn(Arrays.asList(request));

        // Act
        double result = leaveBalanceService.calculateUsedDays(userId, leaveTypeCode, year);

        // Assert
        assertEquals(0.0, result); // Request from 2023, not counted for 2024
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testCalculateUsedDays_ShouldFallbackToCreatedAt_WhenStartDateIsNull")
    void testCalculateUsedDays_ShouldFallbackToCreatedAt_WhenStartDateIsNull() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        int year = 2024;

        Request request = createRequestWithNullStartDate(1L, "APPROVED", leaveTypeCode, 1, false);
        request.setCreatedAt(LocalDateTime.of(2024, 1, 15, 9, 0));

        when(requestDao.findByUserId(userId)).thenReturn(Arrays.asList(request));

        // Act
        double result = leaveBalanceService.calculateUsedDays(userId, leaveTypeCode, year);

        // Assert
        assertEquals(1.0, result); // Counted using createdAt year
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testCalculateUsedDays_ShouldFallbackToCreatedAt_WhenStartDateIsEmpty")
    void testCalculateUsedDays_ShouldFallbackToCreatedAt_WhenStartDateIsEmpty() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        int year = 2024;

        Request request = createRequestWithEmptyStartDate(1L, "APPROVED", leaveTypeCode, 1, false);
        request.setCreatedAt(LocalDateTime.of(2024, 1, 15, 9, 0));

        when(requestDao.findByUserId(userId)).thenReturn(Arrays.asList(request));

        // Act
        double result = leaveBalanceService.calculateUsedDays(userId, leaveTypeCode, year);

        // Assert
        assertEquals(1.0, result); // Counted using createdAt year
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testCalculateUsedDays_ShouldFallbackToCreatedAt_WhenStartDateParsingFails")
    void testCalculateUsedDays_ShouldFallbackToCreatedAt_WhenStartDateParsingFails() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        int year = 2024;

        Request request = createRequestWithInvalidStartDate(1L, "APPROVED", leaveTypeCode, 1, false);
        request.setCreatedAt(LocalDateTime.of(2024, 1, 15, 9, 0));

        when(requestDao.findByUserId(userId)).thenReturn(Arrays.asList(request));

        // Act
        double result = leaveBalanceService.calculateUsedDays(userId, leaveTypeCode, year);

        // Assert
        assertEquals(1.0, result); // Counted using createdAt year after parsing failure
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testCalculateUsedDays_ShouldSkipRequest_WhenStartDateParsingFailsAndCreatedAtYearDoesNotMatch")
    void testCalculateUsedDays_ShouldSkipRequest_WhenStartDateParsingFailsAndCreatedAtYearDoesNotMatch()
            throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        int year = 2024;

        Request request = createRequestWithInvalidStartDate(1L, "APPROVED", leaveTypeCode, 1, false);
        request.setCreatedAt(LocalDateTime.of(2023, 1, 15, 9, 0)); // Different year

        when(requestDao.findByUserId(userId)).thenReturn(Arrays.asList(request));

        // Act
        double result = leaveBalanceService.calculateUsedDays(userId, leaveTypeCode, year);

        // Assert
        assertEquals(0.0, result); // Not counted due to year mismatch
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testCalculateUsedDays_ShouldSkipRequest_WhenCreatedAtIsNull")
    void testCalculateUsedDays_ShouldSkipRequest_WhenCreatedAtIsNull() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        int year = 2024;

        Request request = createRequestWithNullStartDate(1L, "APPROVED", leaveTypeCode, 1, false);
        request.setCreatedAt(null); // No createdAt

        when(requestDao.findByUserId(userId)).thenReturn(Arrays.asList(request));

        // Act
        double result = leaveBalanceService.calculateUsedDays(userId, leaveTypeCode, year);

        // Assert
        assertEquals(0.0, result); // Not counted due to null createdAt
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testCalculateUsedDays_ShouldReturnZero_WhenExceptionOccurs")
    void testCalculateUsedDays_ShouldReturnZero_WhenExceptionOccurs() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        int year = 2024;

        when(requestDao.findByUserId(userId)).thenThrow(new RuntimeException("Database error"));

        // Act
        double result = leaveBalanceService.calculateUsedDays(userId, leaveTypeCode, year);

        // Assert
        assertEquals(0.0, result); // Returns 0.0 on exception
        verify(requestDao).findByUserId(userId);
    }

    // ==================== TEST hasSufficientBalance() ====================

    @Test
    @DisplayName("testHasSufficientBalance_ShouldReturnTrue_WhenLeaveTypeIsUnpaid")
    void testHasSufficientBalance_ShouldReturnTrue_WhenLeaveTypeIsUnpaid() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "UNPAID";
        double requestedDays = 5.0;
        int year = 2024;

        LeaveType unpaidLeaveType = new LeaveType();
        unpaidLeaveType.setCode(leaveTypeCode);
        unpaidLeaveType.setPaid(false);

        when(leaveTypeDao.findByCode(leaveTypeCode)).thenReturn(Optional.of(unpaidLeaveType));

        // Act
        boolean result = leaveBalanceService.hasSufficientBalance(userId, leaveTypeCode, requestedDays, year);

        // Assert
        assertTrue(result); // Always true for unpaid leave
        verify(leaveTypeDao).findByCode(leaveTypeCode);
        verify(requestDao, never()).findByUserId(any()); // Should not check requests
    }

    @Test
    @DisplayName("testHasSufficientBalance_ShouldReturnTrue_WhenLeaveTypeIsUnlimited")
    void testHasSufficientBalance_ShouldReturnTrue_WhenLeaveTypeIsUnlimited() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "UNLIMITED";
        double requestedDays = 10.0;
        int year = 2024;

        LeaveType unlimitedLeaveType = new LeaveType();
        unlimitedLeaveType.setCode(leaveTypeCode);
        unlimitedLeaveType.setPaid(true);
        unlimitedLeaveType.setDefaultDays(null); // Unlimited

        when(leaveTypeDao.findByCode(leaveTypeCode)).thenReturn(Optional.of(unlimitedLeaveType));

        // Act
        boolean result = leaveBalanceService.hasSufficientBalance(userId, leaveTypeCode, requestedDays, year);

        // Assert
        assertTrue(result); // Always true for unlimited leave
        verify(leaveTypeDao).findByCode(leaveTypeCode);
        verify(requestDao, never()).findByUserId(any()); // Should not check requests
    }

    @Test
    @DisplayName("testHasSufficientBalance_ShouldReturnTrue_WhenLeaveTypeDefaultDaysIsZero")
    void testHasSufficientBalance_ShouldReturnTrue_WhenLeaveTypeDefaultDaysIsZero() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "SPECIAL";
        double requestedDays = 5.0;
        int year = 2024;

        LeaveType specialLeaveType = new LeaveType();
        specialLeaveType.setCode(leaveTypeCode);
        specialLeaveType.setPaid(true);
        specialLeaveType.setDefaultDays(0); // Unlimited (0 or negative)

        when(leaveTypeDao.findByCode(leaveTypeCode)).thenReturn(Optional.of(specialLeaveType));

        // Act
        boolean result = leaveBalanceService.hasSufficientBalance(userId, leaveTypeCode, requestedDays, year);

        // Assert
        assertTrue(result); // Always true for unlimited leave
        verify(leaveTypeDao).findByCode(leaveTypeCode);
        verify(requestDao, never()).findByUserId(any());
    }

    @Test
    @DisplayName("testHasSufficientBalance_ShouldReturnTrue_WhenRemainingBalanceIsExactlyEqualToRequested")
    void testHasSufficientBalance_ShouldReturnTrue_WhenRemainingBalanceIsExactlyEqualToRequested() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        double requestedDays = 2.0;
        int year = 2024;

        LeaveType leaveType = new LeaveType();
        leaveType.setCode(leaveTypeCode);
        leaveType.setPaid(true);
        leaveType.setDefaultDays(10);

        // User has used 8 days, so remaining = 10 - 8 = 2.0
        Request usedRequest = createRequest(1L, "APPROVED", leaveTypeCode,
                "2024-01-15T09:00:00", "2024-01-22T18:00:00", 8, false);

        when(leaveTypeDao.findByCode(leaveTypeCode)).thenReturn(Optional.of(leaveType));
        when(requestDao.findByUserId(userId)).thenReturn(Arrays.asList(usedRequest));

        // Act
        boolean result = leaveBalanceService.hasSufficientBalance(userId, leaveTypeCode, requestedDays, year);

        // Assert
        assertTrue(result); // 2.0 remaining >= 2.0 requested
        verify(leaveTypeDao).findByCode(leaveTypeCode);
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testHasSufficientBalance_ShouldReturnTrue_WhenRemainingBalanceIsGreaterThanRequested")
    void testHasSufficientBalance_ShouldReturnTrue_WhenRemainingBalanceIsGreaterThanRequested() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        double requestedDays = 1.0;
        int year = 2024;

        LeaveType leaveType = new LeaveType();
        leaveType.setCode(leaveTypeCode);
        leaveType.setPaid(true);
        leaveType.setDefaultDays(10);

        // User has used 5 days, so remaining = 10 - 5 = 5.0
        Request usedRequest = createRequest(1L, "APPROVED", leaveTypeCode,
                "2024-01-15T09:00:00", "2024-01-19T18:00:00", 5, false);

        when(leaveTypeDao.findByCode(leaveTypeCode)).thenReturn(Optional.of(leaveType));
        when(requestDao.findByUserId(userId)).thenReturn(Arrays.asList(usedRequest));

        // Act
        boolean result = leaveBalanceService.hasSufficientBalance(userId, leaveTypeCode, requestedDays, year);

        // Assert
        assertTrue(result); // 5.0 remaining >= 1.0 requested
        verify(leaveTypeDao).findByCode(leaveTypeCode);
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testHasSufficientBalance_ShouldReturnFalse_WhenRemainingBalanceIsLessThanRequested")
    void testHasSufficientBalance_ShouldReturnFalse_WhenRemainingBalanceIsLessThanRequested() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        double requestedDays = 3.0;
        int year = 2024;

        LeaveType leaveType = new LeaveType();
        leaveType.setCode(leaveTypeCode);
        leaveType.setPaid(true);
        leaveType.setDefaultDays(10);

        // User has used 8 days, so remaining = 10 - 8 = 2.0
        Request usedRequest = createRequest(1L, "APPROVED", leaveTypeCode,
                "2024-01-15T09:00:00", "2024-01-22T18:00:00", 8, false);

        when(leaveTypeDao.findByCode(leaveTypeCode)).thenReturn(Optional.of(leaveType));
        when(requestDao.findByUserId(userId)).thenReturn(Arrays.asList(usedRequest));

        // Act
        boolean result = leaveBalanceService.hasSufficientBalance(userId, leaveTypeCode, requestedDays, year);

        // Assert
        assertFalse(result); // 2.0 remaining < 3.0 requested
        verify(leaveTypeDao).findByCode(leaveTypeCode);
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testHasSufficientBalance_ShouldReturnTrue_WhenRequestingHalfDayAndHalfDayRemaining")
    void testHasSufficientBalance_ShouldReturnTrue_WhenRequestingHalfDayAndHalfDayRemaining() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        double requestedDays = 0.5;
        int year = 2024;

        LeaveType leaveType = new LeaveType();
        leaveType.setCode(leaveTypeCode);
        leaveType.setPaid(true);
        leaveType.setDefaultDays(10);

        // User has used 9.5 days, so remaining = 10 - 9.5 = 0.5
        Request fullDayRequest = createRequest(1L, "APPROVED", leaveTypeCode,
                "2024-01-15T09:00:00", "2024-01-23T18:00:00", 9, false);
        Request halfDayRequest = createRequest(2L, "APPROVED", leaveTypeCode,
                "2024-02-15T09:00:00", "2024-02-15T13:00:00", 1, true);

        when(leaveTypeDao.findByCode(leaveTypeCode)).thenReturn(Optional.of(leaveType));
        when(requestDao.findByUserId(userId)).thenReturn(Arrays.asList(fullDayRequest, halfDayRequest));

        // Act
        boolean result = leaveBalanceService.hasSufficientBalance(userId, leaveTypeCode, requestedDays, year);

        // Assert
        assertTrue(result); // 0.5 remaining >= 0.5 requested
        verify(leaveTypeDao).findByCode(leaveTypeCode);
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testHasSufficientBalance_ShouldReturnFalse_WhenRequestingFullDayButOnlyHalfDayRemaining")
    void testHasSufficientBalance_ShouldReturnFalse_WhenRequestingFullDayButOnlyHalfDayRemaining() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        double requestedDays = 1.0;
        int year = 2024;

        LeaveType leaveType = new LeaveType();
        leaveType.setCode(leaveTypeCode);
        leaveType.setPaid(true);
        leaveType.setDefaultDays(10);

        // User has used 9.5 days, so remaining = 10 - 9.5 = 0.5
        Request fullDayRequest = createRequest(1L, "APPROVED", leaveTypeCode,
                "2024-01-15T09:00:00", "2024-01-23T18:00:00", 9, false);
        Request halfDayRequest = createRequest(2L, "APPROVED", leaveTypeCode,
                "2024-02-15T09:00:00", "2024-02-15T13:00:00", 1, true);

        when(leaveTypeDao.findByCode(leaveTypeCode)).thenReturn(Optional.of(leaveType));
        when(requestDao.findByUserId(userId)).thenReturn(Arrays.asList(fullDayRequest, halfDayRequest));

        // Act
        boolean result = leaveBalanceService.hasSufficientBalance(userId, leaveTypeCode, requestedDays, year);

        // Assert
        assertFalse(result); // 0.5 remaining < 1.0 requested
        verify(leaveTypeDao).findByCode(leaveTypeCode);
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testHasSufficientBalance_ShouldReturnTrue_WhenNoUsedDays")
    void testHasSufficientBalance_ShouldReturnTrue_WhenNoUsedDays() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        double requestedDays = 5.0;
        int year = 2024;

        LeaveType leaveType = new LeaveType();
        leaveType.setCode(leaveTypeCode);
        leaveType.setPaid(true);
        leaveType.setDefaultDays(10);

        when(leaveTypeDao.findByCode(leaveTypeCode)).thenReturn(Optional.of(leaveType));
        when(requestDao.findByUserId(userId)).thenReturn(new ArrayList<>()); // No used days

        // Act
        boolean result = leaveBalanceService.hasSufficientBalance(userId, leaveTypeCode, requestedDays, year);

        // Assert
        assertTrue(result); // 10.0 remaining >= 5.0 requested
        verify(leaveTypeDao).findByCode(leaveTypeCode);
        verify(requestDao).findByUserId(userId);
    }

    @Test
    @DisplayName("testHasSufficientBalance_ShouldThrowException_WhenLeaveTypeNotFound")
    void testHasSufficientBalance_ShouldThrowException_WhenLeaveTypeNotFound() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "INVALID";
        double requestedDays = 1.0;
        int year = 2024;

        when(leaveTypeDao.findByCode(leaveTypeCode)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            leaveBalanceService.hasSufficientBalance(userId, leaveTypeCode, requestedDays, year);
        });

        verify(leaveTypeDao).findByCode(leaveTypeCode);
    }

    @Test
    @DisplayName("testHasSufficientBalance_ShouldReturnFalse_WhenSQLExceptionOccurs")
    void testHasSufficientBalance_ShouldReturnFalse_WhenSQLExceptionOccurs() throws Exception {
        // Arrange
        Long userId = 1L;
        String leaveTypeCode = "ANNUAL";
        double requestedDays = 1.0;
        int year = 2024;

        when(leaveTypeDao.findByCode(leaveTypeCode)).thenThrow(new SQLException("Database error"));

        // Act
        boolean result = leaveBalanceService.hasSufficientBalance(userId, leaveTypeCode, requestedDays, year);

        // Assert
        assertFalse(result); // Returns false on exception
        verify(leaveTypeDao).findByCode(leaveTypeCode);
    }

    // ==================== HELPER METHODS ====================

    /**
     * Helper method to create a Request with LeaveRequestDetail
     */
    private Request createRequest(Long id, String status, String leaveTypeCode,
            String startDate, String endDate, int dayCount, boolean isHalfDay) {
        Request request = new Request();
        request.setId(id);
        request.setStatus(status);
        request.setCreatedAt(LocalDateTime.of(2024, 1, 1, 9, 0));

        LeaveRequestDetail detail = new LeaveRequestDetail();
        detail.setLeaveTypeCode(leaveTypeCode);
        detail.setLeaveTypeName("Leave Type");
        detail.setStartDate(startDate);
        detail.setEndDate(endDate);
        detail.setDayCount(dayCount);
        detail.setReason("Test reason");
        detail.setIsHalfDay(isHalfDay);

        request.setLeaveDetail(detail);
        return request;
    }

    /**
     * Helper method to create a Request with null startDate
     */
    private Request createRequestWithNullStartDate(Long id, String status, String leaveTypeCode,
            int dayCount, boolean isHalfDay) {
        Request request = new Request();
        request.setId(id);
        request.setStatus(status);

        LeaveRequestDetail detail = new LeaveRequestDetail();
        detail.setLeaveTypeCode(leaveTypeCode);
        detail.setLeaveTypeName("Leave Type");
        detail.setStartDate(null); // Null startDate
        detail.setEndDate("2024-01-15T18:00:00");
        detail.setDayCount(dayCount);
        detail.setReason("Test reason");
        detail.setIsHalfDay(isHalfDay);

        request.setLeaveDetail(detail);
        return request;
    }

    /**
     * Helper method to create a Request with empty startDate
     */
    private Request createRequestWithEmptyStartDate(Long id, String status, String leaveTypeCode,
            int dayCount, boolean isHalfDay) {
        Request request = new Request();
        request.setId(id);
        request.setStatus(status);

        LeaveRequestDetail detail = new LeaveRequestDetail();
        detail.setLeaveTypeCode(leaveTypeCode);
        detail.setLeaveTypeName("Leave Type");
        detail.setStartDate(""); // Empty startDate
        detail.setEndDate("2024-01-15T18:00:00");
        detail.setDayCount(dayCount);
        detail.setReason("Test reason");
        detail.setIsHalfDay(isHalfDay);

        request.setLeaveDetail(detail);
        return request;
    }

    /**
     * Helper method to create a Request with invalid startDate format
     */
    private Request createRequestWithInvalidStartDate(Long id, String status, String leaveTypeCode,
            int dayCount, boolean isHalfDay) {
        Request request = new Request();
        request.setId(id);
        request.setStatus(status);

        LeaveRequestDetail detail = new LeaveRequestDetail();
        detail.setLeaveTypeCode(leaveTypeCode);
        detail.setLeaveTypeName("Leave Type");
        detail.setStartDate("invalid-date-format"); // Invalid format
        detail.setEndDate("2024-01-15T18:00:00");
        detail.setDayCount(dayCount);
        detail.setReason("Test reason");
        detail.setIsHalfDay(isHalfDay);

        request.setLeaveDetail(detail);
        return request;
    }
}
