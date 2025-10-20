package group4.hrms.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test cases for AppealRequestDetail JSON parsing.
 * Verifies compatibility with database JSON format (snake_case with comma-separated dates).
 */
class AppealRequestDetailTest {

    @Test
    void testParseEmptyAttendanceDates() {
        String json = "{\"detail_text\": \"test\", \"attendance_dates\": \"\"}";
        
        AppealRequestDetail detail = AppealRequestDetail.fromJson(json);
        
        assertNotNull(detail);
        assertEquals("test", detail.getReason());
        assertNotNull(detail.getAttendanceDates());
        assertTrue(detail.getAttendanceDates().isEmpty(), "Empty string should result in empty list");
    }

    @Test
    void testParseMultipleAttendanceDates() {
        String json = "{\"detail_text\": \"Missed check-in\", \"attendance_dates\": \"2025-10-20,2025-10-19\"}";
        
        AppealRequestDetail detail = AppealRequestDetail.fromJson(json);
        
        assertNotNull(detail);
        assertEquals("Missed check-in", detail.getReason());
        
        List<String> dates = detail.getAttendanceDates();
        assertNotNull(dates);
        assertEquals(2, dates.size());
        assertEquals("2025-10-20", dates.get(0));
        assertEquals("2025-10-19", dates.get(1));
    }

    @Test
    void testParseMultipleAttendanceDatesWithDot() {
        // Database format uses dot as separator instead of comma
        String json = "{\"detail_text\": \"test\", \"attendance_dates\": \"2025-10-20.2025-10-19\"}";
        
        AppealRequestDetail detail = AppealRequestDetail.fromJson(json);
        
        assertNotNull(detail);
        assertEquals("test", detail.getReason());
        
        List<String> dates = detail.getAttendanceDates();
        assertNotNull(dates);
        assertEquals(2, dates.size());
        assertEquals("2025-10-20", dates.get(0));
        assertEquals("2025-10-19", dates.get(1));
    }

    @Test
    void testParseSingleAttendanceDate() {
        String json = "{\"detail_text\": \"Wrong clock-out time\", \"attendance_dates\": \"2025-10-20\"}";
        
        AppealRequestDetail detail = AppealRequestDetail.fromJson(json);
        
        assertNotNull(detail);
        assertEquals("Wrong clock-out time", detail.getReason());
        
        List<String> dates = detail.getAttendanceDates();
        assertNotNull(dates);
        assertEquals(1, dates.size());
        assertEquals("2025-10-20", dates.get(0));
    }

    @Test
    void testParseWithWhitespace() {
        String json = "{\"detail_text\": \"Appeal\", \"attendance_dates\": \"2025-10-20, 2025-10-19 , 2025-10-18\"}";
        
        AppealRequestDetail detail = AppealRequestDetail.fromJson(json);
        
        List<String> dates = detail.getAttendanceDates();
        assertNotNull(dates);
        assertEquals(3, dates.size());
        // Verify whitespace is trimmed
        assertEquals("2025-10-20", dates.get(0));
        assertEquals("2025-10-19", dates.get(1));
        assertEquals("2025-10-18", dates.get(2));
    }

    @Test
    void testParseWithAllFields() {
        String json = "{"
            + "\"detail_text\": \"Clock-in missed\", "
            + "\"attendance_dates\": \"2025-10-20,2025-10-19\", "
            + "\"attachment_path\": \"/uploads/proof.jpg\", "
            + "\"hr_notes\": \"Verified with security\", "
            + "\"hrm_notes\": \"Approved\", "
            + "\"resolution_action\": \"Attendance corrected\", "
            + "\"submitted_date\": \"2025-10-21\""
            + "}";
        
        AppealRequestDetail detail = AppealRequestDetail.fromJson(json);
        
        assertNotNull(detail);
        assertEquals("Clock-in missed", detail.getReason());
        assertEquals(2, detail.getAttendanceDates().size());
        assertEquals("/uploads/proof.jpg", detail.getAttachmentPath());
        assertEquals("Verified with security", detail.getHrNotes());
        assertEquals("Approved", detail.getHrmNotes());
        assertEquals("Attendance corrected", detail.getResolutionAction());
        assertEquals("2025-10-21", detail.getSubmittedDate());
    }

    @Test
    void testToString() {
        AppealRequestDetail detail = new AppealRequestDetail();
        detail.setReason("Test reason");
        detail.setAttendanceDates(List.of("2025-10-20"));
        
        String str = detail.toString();
        
        assertTrue(str.contains("Test reason"));
        assertTrue(str.contains("2025-10-20"));
    }
}
