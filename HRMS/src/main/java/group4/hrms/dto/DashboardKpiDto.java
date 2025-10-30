package group4.hrms.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO for Dashboard KPI metrics
 */
public class DashboardKpiDto {
    // Employee metrics
    private int totalEmployees;
    private int activeEmployees;
    private int newEmployeesThisMonth;
    
    // Leave metrics
    private int pendingLeaveRequests;
    private int approvedLeavesToday;
    private double averageLeaveBalance;
    
    // OT metrics
    private int pendingOtRequests;
    private double totalOtHoursThisMonth;
    private double averageOtHoursPerEmployee;
    
    // Attendance metrics
    private int presentToday;
    private int absentToday;
    private int lateCheckinsThisWeek;
    private double attendanceRate;
    
    // Payroll metrics
    private BigDecimal totalPayrollThisMonth;
    private int payslipsGenerated;
    private BigDecimal averageSalary;
    
    // Department breakdown
    private Map<String, Integer> employeesByDepartment;
    private Map<String, Integer> employeesByPosition;
    
    // Request status breakdown
    private Map<String, Integer> requestsByStatus;
    private Map<String, Integer> requestsByType;
    
    // Monthly trends (last 6 months)
    private Map<String, Integer> employeeTrend;
    private Map<String, Double> otTrend;
    private Map<String, Double> attendanceTrend;

    // Constructors
    public DashboardKpiDto() {
    }

    // Getters and Setters
    public int getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public int getActiveEmployees() {
        return activeEmployees;
    }

    public void setActiveEmployees(int activeEmployees) {
        this.activeEmployees = activeEmployees;
    }

    public int getNewEmployeesThisMonth() {
        return newEmployeesThisMonth;
    }

    public void setNewEmployeesThisMonth(int newEmployeesThisMonth) {
        this.newEmployeesThisMonth = newEmployeesThisMonth;
    }

    public int getPendingLeaveRequests() {
        return pendingLeaveRequests;
    }

    public void setPendingLeaveRequests(int pendingLeaveRequests) {
        this.pendingLeaveRequests = pendingLeaveRequests;
    }

    public int getApprovedLeavesToday() {
        return approvedLeavesToday;
    }

    public void setApprovedLeavesToday(int approvedLeavesToday) {
        this.approvedLeavesToday = approvedLeavesToday;
    }

    public double getAverageLeaveBalance() {
        return averageLeaveBalance;
    }

    public void setAverageLeaveBalance(double averageLeaveBalance) {
        this.averageLeaveBalance = averageLeaveBalance;
    }

    public int getPendingOtRequests() {
        return pendingOtRequests;
    }

    public void setPendingOtRequests(int pendingOtRequests) {
        this.pendingOtRequests = pendingOtRequests;
    }

    public double getTotalOtHoursThisMonth() {
        return totalOtHoursThisMonth;
    }

    public void setTotalOtHoursThisMonth(double totalOtHoursThisMonth) {
        this.totalOtHoursThisMonth = totalOtHoursThisMonth;
    }

    public double getAverageOtHoursPerEmployee() {
        return averageOtHoursPerEmployee;
    }

    public void setAverageOtHoursPerEmployee(double averageOtHoursPerEmployee) {
        this.averageOtHoursPerEmployee = averageOtHoursPerEmployee;
    }

    public int getPresentToday() {
        return presentToday;
    }

    public void setPresentToday(int presentToday) {
        this.presentToday = presentToday;
    }

    public int getAbsentToday() {
        return absentToday;
    }

    public void setAbsentToday(int absentToday) {
        this.absentToday = absentToday;
    }

    public int getLateCheckinsThisWeek() {
        return lateCheckinsThisWeek;
    }

    public void setLateCheckinsThisWeek(int lateCheckinsThisWeek) {
        this.lateCheckinsThisWeek = lateCheckinsThisWeek;
    }

    public double getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(double attendanceRate) {
        this.attendanceRate = attendanceRate;
    }

    public BigDecimal getTotalPayrollThisMonth() {
        return totalPayrollThisMonth;
    }

    public void setTotalPayrollThisMonth(BigDecimal totalPayrollThisMonth) {
        this.totalPayrollThisMonth = totalPayrollThisMonth;
    }

    public int getPayslipsGenerated() {
        return payslipsGenerated;
    }

    public void setPayslipsGenerated(int payslipsGenerated) {
        this.payslipsGenerated = payslipsGenerated;
    }

    public BigDecimal getAverageSalary() {
        return averageSalary;
    }

    public void setAverageSalary(BigDecimal averageSalary) {
        this.averageSalary = averageSalary;
    }

    public Map<String, Integer> getEmployeesByDepartment() {
        return employeesByDepartment;
    }

    public void setEmployeesByDepartment(Map<String, Integer> employeesByDepartment) {
        this.employeesByDepartment = employeesByDepartment;
    }

    public Map<String, Integer> getEmployeesByPosition() {
        return employeesByPosition;
    }

    public void setEmployeesByPosition(Map<String, Integer> employeesByPosition) {
        this.employeesByPosition = employeesByPosition;
    }

    public Map<String, Integer> getRequestsByStatus() {
        return requestsByStatus;
    }

    public void setRequestsByStatus(Map<String, Integer> requestsByStatus) {
        this.requestsByStatus = requestsByStatus;
    }

    public Map<String, Integer> getRequestsByType() {
        return requestsByType;
    }

    public void setRequestsByType(Map<String, Integer> requestsByType) {
        this.requestsByType = requestsByType;
    }

    public Map<String, Integer> getEmployeeTrend() {
        return employeeTrend;
    }

    public void setEmployeeTrend(Map<String, Integer> employeeTrend) {
        this.employeeTrend = employeeTrend;
    }

    public Map<String, Double> getOtTrend() {
        return otTrend;
    }

    public void setOtTrend(Map<String, Double> otTrend) {
        this.otTrend = otTrend;
    }

    public Map<String, Double> getAttendanceTrend() {
        return attendanceTrend;
    }

    public void setAttendanceTrend(Map<String, Double> attendanceTrend) {
        this.attendanceTrend = attendanceTrend;
    }
}
