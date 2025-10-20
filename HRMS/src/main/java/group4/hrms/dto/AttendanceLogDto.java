package group4.hrms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO cho AttendanceLog với thông tin bổ sung
 */
public class AttendanceLogDto {

    private Long userId;
    private String employeeName;
    private String department;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate date;

    private LocalTime checkIn;
    private LocalTime checkOut;
    private String status;
    private String source;
    private String period;

    public AttendanceLogDto() {
    }

    // -------- Getter & Setter --------
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalTime checkOut) {
        this.checkOut = checkOut;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getDateStr() {
        return date != null ? date.toString() : "";
    }

    public String getCheckInStr() {
        return checkIn != null ? checkIn.toString().substring(0, 5) : "";
    }

    public String getCheckOutStr() {
        return checkOut != null ? checkOut.toString().substring(0, 5) : "";
    }

    // -------- toString() để debug --------
    @Override
    public String toString() {
        return "AttendanceLogDto{"
                + "employeeId=" + userId
                + ", employeeName='" + employeeName + '\''
                + ", department='" + department + '\''
                + ", date=" + date
                + ", checkIn=" + checkIn
                + ", checkOut=" + checkOut
                + ", status='" + status + '\''
                + ", source='" + source + '\''
                + ", period='" + period + '\''
                + '}';
    }
}
