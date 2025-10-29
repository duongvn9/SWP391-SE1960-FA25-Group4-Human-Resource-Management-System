// package group4.hrms.service;

// import group4.hrms.dao.UserProfileDao;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.time.LocalDate;

// import static org.junit.jupiter.api.Assertions.*;

// /**
//  * Unit Test cho UserProfileService - isAgeBetween18And60()
//  * 
//  * Mục tiêu: Test hàm kiểm tra tuổi từ 18-60
//  */
// @ExtendWith(MockitoExtension.class)
// @DisplayName("UserProfileService - isAgeBetween18And60 Tests")
// class UserProfileServiceTest {

//     @Mock
//     private UserProfileDao userProfileDao;

//     private UserProfileService userProfileService;

//     @BeforeEach
//     void setUp() {
//         userProfileService = new UserProfileService(userProfileDao);
//     }

//     @Test
//     @DisplayName("Should return true when dob is null (optional field)")
//     void testIsAgeBetween18And60_NullDob() {
//         // Act
//         boolean result = userProfileService.isAgeBetween18And60(null);

//         // Assert
//         assertTrue(result, "Null DOB should return true as it's optional");
//     }

//     @Test
//     @DisplayName("Should return true when age is exactly 18")
//     void testIsAgeBetween18And60_Exactly18() {
//         // Arrange
//         LocalDate dob = LocalDate.now().minusYears(18);

//         // Act
//         boolean result = userProfileService.isAgeBetween18And60(dob);

//         // Assert
//         assertTrue(result, "Age exactly 18 should be valid");
//     }

//     @Test
//     @DisplayName("Should return true when age is exactly 60")
//     void testIsAgeBetween18And60_Exactly60() {
//         // Arrange
//         LocalDate dob = LocalDate.now().minusYears(60);

//         // Act
//         boolean result = userProfileService.isAgeBetween18And60(dob);

//         // Assert
//         assertTrue(result, "Age exactly 60 should be valid");
//     }

//     @Test
//     @DisplayName("Should return true when age is between 18 and 60")
//     void testIsAgeBetween18And60_ValidAge() {
//         // Arrange
//         LocalDate dob = LocalDate.now().minusYears(30);

//         // Act
//         boolean result = userProfileService.isAgeBetween18And60(dob);

//         // Assert
//         assertTrue(result, "Age 30 should be valid");
//     }

//     @Test
//     @DisplayName("Should return false when age is less than 18")
//     void testIsAgeBetween18And60_LessThan18() {
//         // Arrange
//         LocalDate dob = LocalDate.now().minusYears(17);

//         // Act
//         boolean result = userProfileService.isAgeBetween18And60(dob);

//         // Assert
//         assertFalse(result, "Age less than 18 should be invalid");
//     }

//     @Test
//     @DisplayName("Should return false when age is greater than 60")
//     void testIsAgeBetween18And60_GreaterThan60() {
//         // Arrange
//         LocalDate dob = LocalDate.now().minusYears(61);

//         // Act
//         boolean result = userProfileService.isAgeBetween18And60(dob);

//         // Assert
//         assertFalse(result, "Age greater than 60 should be invalid");
//     }

//     @Test
//     @DisplayName("Should return true when age is 18 but birthday hasn't occurred this year")
//     void testIsAgeBetween18And60_18YearsButBirthdayNotYet() {
//         // Arrange - Birthday is tomorrow (hasn't occurred yet)
//         LocalDate dob = LocalDate.now().minusYears(18).plusDays(1);

//         // Act
//         boolean result = userProfileService.isAgeBetween18And60(dob);

//         // Assert
//         assertFalse(result, "Person is still 17 if birthday hasn't occurred yet");
//     }

//     @Test
//     @DisplayName("Should return false when age is 60 but will turn 61 soon")
//     void testIsAgeBetween18And60_60YearsButBirthdayNotYet() {
//         // Arrange - Birthday is tomorrow (hasn't occurred yet)
//         LocalDate dob = LocalDate.now().minusYears(60).plusDays(1);

//         // Act
//         boolean result = userProfileService.isAgeBetween18And60(dob);

//         // Assert
//         assertTrue(result, "Person is still 59 if birthday hasn't occurred yet");
//     }

//     @Test
//     @DisplayName("Should handle edge case - birthday today at age 18")
//     void testIsAgeBetween18And60_BirthdayTodayAge18() {
//         // Arrange
//         LocalDate dob = LocalDate.now().minusYears(18);

//         // Act
//         boolean result = userProfileService.isAgeBetween18And60(dob);

//         // Assert
//         assertTrue(result, "Age 18 on birthday should be valid");
//     }

//     @Test
//     @DisplayName("Should handle edge case - birthday today at age 60")
//     void testIsAgeBetween18And60_BirthdayTodayAge60() {
//         // Arrange
//         LocalDate dob = LocalDate.now().minusYears(60);

//         // Act
//         boolean result = userProfileService.isAgeBetween18And60(dob);

//         // Assert
//         assertTrue(result, "Age 60 on birthday should be valid");
//     }

//     @Test
//     @DisplayName("Should handle leap year birthday")
//     void testIsAgeBetween18And60_LeapYearBirthday() {
//         // Arrange - Born on Feb 29, 2000 (leap year)
//         LocalDate dob = LocalDate.of(2000, 2, 29);

//         // Act
//         boolean result = userProfileService.isAgeBetween18And60(dob);

//         // Assert
//         assertTrue(result, "Person born on leap year should be validated correctly");
//     }
// }
