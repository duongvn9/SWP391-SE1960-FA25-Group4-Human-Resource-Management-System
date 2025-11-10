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
    private int totalDepartments;
    
    // Account metrics
    private int totalAccounts;
    private int activeAccounts;
    private int inactiveAccounts;
    private int lockedAccounts;
    
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
    
    // Payroll metrics - separated by currency
    private BigDecimal totalPayrollThisMonthVND;
    private BigDecimal totalPayrollThisMonthUSD;
    private int payslipsGenerated;
    private BigDecimal averageSalaryVND;
    private BigDecimal averageSalaryUSD;
    
    // Department breakdown
    private Map<String, Integer> employeesByDepartment;
    private Map<String, Integer> employeesByPosition;
    
    // Payroll by department
    private Map<String, BigDecimal> payrollByDepartment;
    private Map<String, BigDecimal> payrollByDepartmentVND;
    private Map<String, BigDecimal> payrollByDepartmentUSD;
    
    // Request status breakdown
    private Map<String, Integer> requestsByStatus;
    private Map<String, Integer> requestsByType;
    private Map<String, Integer> otRequestsByStatus;
    private Map<String, Integer> leaveRequestsByStatus;
    private Map<String, Integer> recruitmentRequestsByStatus;
    
    // Recruitment metrics
    private int pendingRecruitmentRequests;
    private int totalRecruitmentRequestsThisMonth;
    
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
    
    public int getTotalDepartments() {
        return totalDepartments;
    }
    
    public void setTotalDepartments(int totalDepartments) {
        this.totalDepartments = totalDepartments;
    }
    
    public int getTotalAccounts() {
        return totalAccounts;
    }
    
    public void setTotalAccounts(int totalAccounts) {
        this.totalAccounts = totalAccounts;
    }
    
    public int getActiveAccounts() {
        return activeAccounts;
    }
    
    public void setActiveAccounts(int activeAccounts) {
        this.activeAccounts = activeAccounts;
    }
    
    public int getInactiveAccounts() {
        return inactiveAccounts;
    }
    
    public void setInactiveAccounts(int inactiveAccounts) {
        this.inactiveAccounts = inactiveAccounts;
    }
    
    public int getLockedAccounts() {
        return lockedAccounts;
    }
    
    public void setLockedAccounts(int lockedAccounts) {
        this.lockedAccounts = lockedAccounts;
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

    public BigDecimal getTotalPayrollThisMonthVND() {
        return totalPayrollThisMonthVND;
    }

    public void setTotalPayrollThisMonthVND(BigDecimal totalPayrollThisMonthVND) {
        this.totalPayrollThisMonthVND = totalPayrollThisMonthVND;
    }

    public BigDecimal getTotalPayrollThisMonthUSD() {
        return totalPayrollThisMonthUSD;
    }

    public void setTotalPayrollThisMonthUSD(BigDecimal totalPayrollThisMonthUSD) {
        this.totalPayrollThisMonthUSD = totalPayrollThisMonthUSD;
    }

    public int getPayslipsGenerated() {
        return payslipsGenerated;
    }

    public void setPayslipsGenerated(int payslipsGenerated) {
        this.payslipsGenerated = payslipsGenerated;
    }

    public BigDecimal getAverageSalaryVND() {
        return averageSalaryVND;
    }

    public void setAverageSalaryVND(BigDecimal averageSalaryVND) {
        this.averageSalaryVND = averageSalaryVND;
    }

    public BigDecimal getAverageSalaryUSD() {
        return averageSalaryUSD;
    }

    public void setAverageSalaryUSD(BigDecimal averageSalaryUSD) {
        this.averageSalaryUSD = averageSalaryUSD;
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

    public Map<String, BigDecimal> getPayrollByDepartment() {
        return payrollByDepartment;
    }

    public void setPayrollByDepartment(Map<String, BigDecimal> payrollByDepartment) {
        this.payrollByDepartment = payrollByDepartment;
    }

    public Map<String, BigDecimal> getPayrollByDepartmentVND() {
        return payrollByDepartmentVND;
    }

    public void setPayrollByDepartmentVND(Map<String, BigDecimal> payrollByDepartmentVND) {
        this.payrollByDepartmentVND = payrollByDepartmentVND;
    }

    public Map<String, BigDecimal> getPayrollByDepartmentUSD() {
        return payrollByDepartmentUSD;
    }

    public void setPayrollByDepartmentUSD(Map<String, BigDecimal> payrollByDepartmentUSD) {
        this.payrollByDepartmentUSD = payrollByDepartmentUSD;
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

    public Map<String, Integer> getOtRequestsByStatus() {
        return otRequestsByStatus;
    }

    public void setOtRequestsByStatus(Map<String, Integer> otRequestsByStatus) {
        this.otRequestsByStatus = otRequestsByStatus;
    }

    public Map<String, Integer> getLeaveRequestsByStatus() {
        return leaveRequestsByStatus;
    }

    public void setLeaveRequestsByStatus(Map<String, Integer> leaveRequestsByStatus) {
        this.leaveRequestsByStatus = leaveRequestsByStatus;
    }

    public Map<String, Integer> getRecruitmentRequestsByStatus() {
        return recruitmentRequestsByStatus;
    }

    public void setRecruitmentRequestsByStatus(Map<String, Integer> recruitmentRequestsByStatus) {
        this.recruitmentRequestsByStatus = recruitmentRequestsByStatus;
    }

    public int getPendingRecruitmentRequests() {
        return pendingRecruitmentRequests;
    }

    public void setPendingRecruitmentRequests(int pendingRecruitmentRequests) {
        this.pendingRecruitmentRequests = pendingRecruitmentRequests;
    }

    public int getTotalRecruitmentRequestsThisMonth() {
        return totalRecruitmentRequestsThisMonth;
    }

    public void setTotalRecruitmentRequestsThisMonth(int totalRecruitmentRequestsThisMonth) {
        this.totalRecruitmentRequestsThisMonth = totalRecruitmentRequestsThisMonth;
    }
}
