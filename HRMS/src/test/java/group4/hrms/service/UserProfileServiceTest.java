package group4.hrms.service;

import group4.hrms.dao.UserProfileDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test cho UserProfileService - Age Validation
 * 
 * Mục tiêu: Đạt 100% Statement Coverage và Decision Coverage
 * 
 * Test hàm isAgeBetween18And60() - Kiểm tra tuổi từ 18-60
 * 
 * Các test cases bao phủ tất cả các nhánh điều kiện:
 * - DOB null (required field)
 * - Tuổi chính xác 18 và 60 (boundary values)
 * - Tuổi hợp lệ giữa 18-60
 * - Tuổi dưới 18 (invalid)
 * - Tuổi trên 60 (invalid)
 * 
 * Note: Test cases về CCCD đã được tách sang UserProfileServiceCCCDTest.java
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserProfileService - Age Validation Tests")
class UserProfileServiceTest {

    @Mock
    private UserProfileDao userProfileDao;

    private UserProfileService userProfileService;

    @BeforeEach
    void setUp() {
        userProfileService = new UserProfileService(userProfileDao);
    }

    @Test
    @DisplayName("testIsAgeBetween18And60_ShouldReturnFalse_WhenDobIsNull")
    void testIsAgeBetween18And60_ShouldReturnFalse_WhenDobIsNull() {
        // Act
        boolean result = userProfileService.isAgeBetween18And60(null);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("testIsAgeBetween18And60_ShouldReturnTrue_WhenAgeIsExactly18")
    void testIsAgeBetween18And60_ShouldReturnTrue_WhenAgeIsExactly18() {
        // Arrange
        LocalDate dob = LocalDate.now().minusYears(18);

        // Act
        boolean result = userProfileService.isAgeBetween18And60(dob);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("testIsAgeBetween18And60_ShouldReturnTrue_WhenAgeIsExactly60")
    void testIsAgeBetween18And60_ShouldReturnTrue_WhenAgeIsExactly60() {
        // Arrange
        LocalDate dob = LocalDate.now().minusYears(60);

        // Act
        boolean result = userProfileService.isAgeBetween18And60(dob);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("testIsAgeBetween18And60_ShouldReturnTrue_WhenAgeIsBetween18And60")
    void testIsAgeBetween18And60_ShouldReturnTrue_WhenAgeIsBetween18And60() {
        // Arrange
        LocalDate dob = LocalDate.now().minusYears(30);

        // Act
        boolean result = userProfileService.isAgeBetween18And60(dob);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("testIsAgeBetween18And60_ShouldReturnFalse_WhenAgeIsLessThan18")
    void testIsAgeBetween18And60_ShouldReturnFalse_WhenAgeIsLessThan18() {
        // Arrange
        LocalDate dob = LocalDate.now().minusYears(17);

        // Act
        boolean result = userProfileService.isAgeBetween18And60(dob);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("testIsAgeBetween18And60_ShouldReturnFalse_WhenAgeIsGreaterThan60")
    void testIsAgeBetween18And60_ShouldReturnFalse_WhenAgeIsGreaterThan60() {
        // Arrange
        LocalDate dob = LocalDate.now().minusYears(61);

        // Act
        boolean result = userProfileService.isAgeBetween18And60(dob);

        // Assert
        assertFalse(result);
    }
}
