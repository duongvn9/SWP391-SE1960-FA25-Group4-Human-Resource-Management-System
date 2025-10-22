package group4.hrms.service;

import group4.hrms.dao.HolidayDao;
import group4.hrms.model.Holiday;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Test cho OTCalculationService.determineOTType()
 * 
 * Mục tiêu: Đạt 100% Statement Coverage và Decision Coverage
 * 
 * Test cases bao phủ tất cả các nhánh điều kiện:
 * 1. Holiday exists và là substitute day → COMPENSATORY
 * 2. Holiday exists và không phải substitute day → HOLIDAY
 * 3. Không có holiday và là Saturday → WEEKEND
 * 4. Không có holiday và là Sunday → WEEKEND
 * 5. Không có holiday và là weekday → WEEKDAY
 * 6. SQLException xảy ra → fallback logic
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OTCalculationService - determineOTType Method Tests")
class OTCalculationServiceTest {

    @Mock
    private HolidayDao holidayDao;

    private OTCalculationService otCalculationService;

    @BeforeEach
    void setUp() {
        otCalculationService = new OTCalculationService(holidayDao);
    }

    @Test
    @DisplayName("testDetermineOTType_ShouldReturnCOMPENSATORY_WhenDateIsSubstituteDay")
    void testDetermineOTType_ShouldReturnCOMPENSATORY_WhenDateIsSubstituteDay() throws SQLException {
        // Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 1); // Monday
        Holiday mockHoliday = mock(Holiday.class);
        when(mockHoliday.isSubstituteDay()).thenReturn(true);
        when(holidayDao.findByDate(testDate)).thenReturn(mockHoliday);

        // Act
        OTCalculationService.OTType result = otCalculationService.determineOTType(testDate);

        // Assert
        assertEquals(OTCalculationService.OTType.COMPENSATORY, result);
        assertEquals("COMPENSATORY", result.getCode());
        assertEquals(2.0, result.getMultiplier());

        // Verify interactions
        verify(holidayDao).findByDate(testDate);
        verify(mockHoliday).isSubstituteDay();
    }

    @Test
    @DisplayName("testDetermineOTType_ShouldReturnHOLIDAY_WhenDateIsOriginalHoliday")
    void testDetermineOTType_ShouldReturnHOLIDAY_WhenDateIsOriginalHoliday() throws SQLException {
        // Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 1); // Monday
        Holiday mockHoliday = mock(Holiday.class);
        when(mockHoliday.isSubstituteDay()).thenReturn(false);
        when(holidayDao.findByDate(testDate)).thenReturn(mockHoliday);

        // Act
        OTCalculationService.OTType result = otCalculationService.determineOTType(testDate);

        // Assert
        assertEquals(OTCalculationService.OTType.HOLIDAY, result);
        assertEquals("HOLIDAY", result.getCode());
        assertEquals(3.0, result.getMultiplier());

        // Verify interactions
        verify(holidayDao).findByDate(testDate);
        verify(mockHoliday).isSubstituteDay();
    }

    @Test
    @DisplayName("testDetermineOTType_ShouldReturnWEEKEND_WhenDateIsSaturday")
    void testDetermineOTType_ShouldReturnWEEKEND_WhenDateIsSaturday() throws SQLException {
        // Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 6); // Saturday
        when(holidayDao.findByDate(testDate)).thenReturn(null);

        // Act
        OTCalculationService.OTType result = otCalculationService.determineOTType(testDate);

        // Assert
        assertEquals(OTCalculationService.OTType.WEEKEND, result);
        assertEquals("WEEKEND", result.getCode());
        assertEquals(2.0, result.getMultiplier());

        // Verify interactions
        verify(holidayDao).findByDate(testDate);
    }

    @Test
    @DisplayName("testDetermineOTType_ShouldReturnWEEKEND_WhenDateIsSunday")
    void testDetermineOTType_ShouldReturnWEEKEND_WhenDateIsSunday() throws SQLException {
        // Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 7); // Sunday
        when(holidayDao.findByDate(testDate)).thenReturn(null);

        // Act
        OTCalculationService.OTType result = otCalculationService.determineOTType(testDate);

        // Assert
        assertEquals(OTCalculationService.OTType.WEEKEND, result);
        assertEquals("WEEKEND", result.getCode());
        assertEquals(2.0, result.getMultiplier());

        // Verify interactions
        verify(holidayDao).findByDate(testDate);
    }

    @Test
    @DisplayName("testDetermineOTType_ShouldReturnWEEKDAY_WhenDateIsWeekday")
    void testDetermineOTType_ShouldReturnWEEKDAY_WhenDateIsWeekday() throws SQLException {
        // Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 2); // Tuesday
        when(holidayDao.findByDate(testDate)).thenReturn(null);

        // Act
        OTCalculationService.OTType result = otCalculationService.determineOTType(testDate);

        // Assert
        assertEquals(OTCalculationService.OTType.WEEKDAY, result);
        assertEquals("WEEKDAY", result.getCode());
        assertEquals(1.5, result.getMultiplier());

        // Verify interactions
        verify(holidayDao).findByDate(testDate);
    }

    @Test
    @DisplayName("testDetermineOTType_ShouldReturnWEEKDAY_WhenDateIsMonday")
    void testDetermineOTType_ShouldReturnWEEKDAY_WhenDateIsMonday() throws SQLException {
        // Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 8); // Monday
        when(holidayDao.findByDate(testDate)).thenReturn(null);

        // Act
        OTCalculationService.OTType result = otCalculationService.determineOTType(testDate);

        // Assert
        assertEquals(OTCalculationService.OTType.WEEKDAY, result);
        assertEquals("WEEKDAY", result.getCode());
        assertEquals(1.5, result.getMultiplier());

        // Verify interactions
        verify(holidayDao).findByDate(testDate);
    }

    @Test
    @DisplayName("testDetermineOTType_ShouldReturnWEEKDAY_WhenDateIsWednesday")
    void testDetermineOTType_ShouldReturnWEEKDAY_WhenDateIsWednesday() throws SQLException {
        // Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 3); // Wednesday
        when(holidayDao.findByDate(testDate)).thenReturn(null);

        // Act
        OTCalculationService.OTType result = otCalculationService.determineOTType(testDate);

        // Assert
        assertEquals(OTCalculationService.OTType.WEEKDAY, result);
        assertEquals("WEEKDAY", result.getCode());
        assertEquals(1.5, result.getMultiplier());

        // Verify interactions
        verify(holidayDao).findByDate(testDate);
    }

    @Test
    @DisplayName("testDetermineOTType_ShouldReturnWEEKDAY_WhenDateIsThursday")
    void testDetermineOTType_ShouldReturnWEEKDAY_WhenDateIsThursday() throws SQLException {
        // Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 4); // Thursday
        when(holidayDao.findByDate(testDate)).thenReturn(null);

        // Act
        OTCalculationService.OTType result = otCalculationService.determineOTType(testDate);

        // Assert
        assertEquals(OTCalculationService.OTType.WEEKDAY, result);
        assertEquals("WEEKDAY", result.getCode());
        assertEquals(1.5, result.getMultiplier());

        // Verify interactions
        verify(holidayDao).findByDate(testDate);
    }

    @Test
    @DisplayName("testDetermineOTType_ShouldReturnWEEKDAY_WhenDateIsFriday")
    void testDetermineOTType_ShouldReturnWEEKDAY_WhenDateIsFriday() throws SQLException {
        // Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 5); // Friday
        when(holidayDao.findByDate(testDate)).thenReturn(null);

        // Act
        OTCalculationService.OTType result = otCalculationService.determineOTType(testDate);

        // Assert
        assertEquals(OTCalculationService.OTType.WEEKDAY, result);
        assertEquals("WEEKDAY", result.getCode());
        assertEquals(1.5, result.getMultiplier());

        // Verify interactions
        verify(holidayDao).findByDate(testDate);
    }

    @Test
    @DisplayName("testDetermineOTType_ShouldReturnWEEKEND_WhenSQLExceptionAndDateIsSaturday")
    void testDetermineOTType_ShouldReturnWEEKEND_WhenSQLExceptionAndDateIsSaturday() throws SQLException {
        // Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 6); // Saturday
        when(holidayDao.findByDate(testDate)).thenThrow(new SQLException("Database error"));

        // Act
        OTCalculationService.OTType result = otCalculationService.determineOTType(testDate);

        // Assert
        assertEquals(OTCalculationService.OTType.WEEKEND, result);
        assertEquals("WEEKEND", result.getCode());
        assertEquals(2.0, result.getMultiplier());

        // Verify interactions
        verify(holidayDao).findByDate(testDate);
    }

    @Test
    @DisplayName("testDetermineOTType_ShouldReturnWEEKEND_WhenSQLExceptionAndDateIsSunday")
    void testDetermineOTType_ShouldReturnWEEKEND_WhenSQLExceptionAndDateIsSunday() throws SQLException {
        // Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 7); // Sunday
        when(holidayDao.findByDate(testDate)).thenThrow(new SQLException("Database error"));

        // Act
        OTCalculationService.OTType result = otCalculationService.determineOTType(testDate);

        // Assert
        assertEquals(OTCalculationService.OTType.WEEKEND, result);
        assertEquals("WEEKEND", result.getCode());
        assertEquals(2.0, result.getMultiplier());

        // Verify interactions
        verify(holidayDao).findByDate(testDate);
    }

    @Test
    @DisplayName("testDetermineOTType_ShouldReturnWEEKDAY_WhenSQLExceptionAndDateIsWeekday")
    void testDetermineOTType_ShouldReturnWEEKDAY_WhenSQLExceptionAndDateIsWeekday() throws SQLException {
        // Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 2); // Tuesday
        when(holidayDao.findByDate(testDate)).thenThrow(new SQLException("Database error"));

        // Act
        OTCalculationService.OTType result = otCalculationService.determineOTType(testDate);

        // Assert
        assertEquals(OTCalculationService.OTType.WEEKDAY, result);
        assertEquals("WEEKDAY", result.getCode());
        assertEquals(1.5, result.getMultiplier());

        // Verify interactions
        verify(holidayDao).findByDate(testDate);
    }

    // Test cho các method khác để đảm bảo coverage hoàn chỉnh
    @Test
    @DisplayName("testGetOTMultiplier_ShouldReturnCorrectMultiplier")
    void testGetOTMultiplier_ShouldReturnCorrectMultiplier() throws SQLException {
        // Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 1); // Monday
        Holiday mockHoliday = mock(Holiday.class);
        when(mockHoliday.isSubstituteDay()).thenReturn(false);
        when(holidayDao.findByDate(testDate)).thenReturn(mockHoliday);

        // Act
        double multiplier = otCalculationService.getOTMultiplier(testDate);

        // Assert
        assertEquals(3.0, multiplier);
    }

    @Test
    @DisplayName("testCalculateOTPay_ShouldCalculateCorrectPay")
    void testCalculateOTPay_ShouldCalculateCorrectPay() throws SQLException {
        // Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 1); // Monday
        Holiday mockHoliday = mock(Holiday.class);
        when(mockHoliday.isSubstituteDay()).thenReturn(false);
        when(holidayDao.findByDate(testDate)).thenReturn(mockHoliday);

        double hours = 2.0;
        double hourlyRate = 100.0;

        // Act
        double otPay = otCalculationService.calculateOTPay(testDate, hours, hourlyRate);

        // Assert
        assertEquals(600.0, otPay); // 100 * 2 * 3.0 = 600
    }
}
