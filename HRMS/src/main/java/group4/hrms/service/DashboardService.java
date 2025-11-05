package group4.hrms.service;

import group4.hrms.dao.DashboardDao;
import group4.hrms.dto.DashboardKpiDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for Dashboard KPI business logic
 */
public class DashboardService {
    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);
    private final DashboardDao dashboardDao;

    public DashboardService() {
        this.dashboardDao = new DashboardDao();
    }

    public DashboardService(DashboardDao dashboardDao) {
        this.dashboardDao = dashboardDao;
    }

    /**
     * Get all dashboard KPIs
     */
    public DashboardKpiDto getDashboardKpis() {
        logger.info("Fetching dashboard KPIs");
        
        DashboardKpiDto kpis = new DashboardKpiDto();

        try {
            // Employee metrics
            kpis.setTotalEmployees(dashboardDao.getTotalEmployees());
            kpis.setActiveEmployees(dashboardDao.getActiveEmployees());
            kpis.setNewEmployeesThisMonth(dashboardDao.getNewEmployeesThisMonth());
            kpis.setTotalDepartments(dashboardDao.getTotalDepartments());
            
            // Account metrics
            kpis.setTotalAccounts(dashboardDao.getTotalAccounts());
            kpis.setActiveAccounts(dashboardDao.getActiveAccounts());
            kpis.setInactiveAccounts(dashboardDao.getInactiveAccounts());
            kpis.setLockedAccounts(dashboardDao.getLockedAccounts());

            // Leave metrics
            kpis.setPendingLeaveRequests(dashboardDao.getPendingLeaveRequests());
            kpis.setApprovedLeavesToday(dashboardDao.getApprovedLeavesToday());
            kpis.setAverageLeaveBalance(dashboardDao.getAverageLeaveBalance());

            // OT metrics
            kpis.setPendingOtRequests(dashboardDao.getPendingOtRequests());
            double totalOtHours = dashboardDao.getTotalOtHours(); // Changed to get all time OT hours
            kpis.setTotalOtHoursThisMonth(totalOtHours);
            
            int activeEmployees = kpis.getActiveEmployees();
            if (activeEmployees > 0) {
                kpis.setAverageOtHoursPerEmployee(totalOtHours / activeEmployees);
            }

            // Attendance metrics
            kpis.setPresentToday(dashboardDao.getPresentToday());
            kpis.setAbsentToday(dashboardDao.getAbsentToday());
            kpis.setLateCheckinsThisWeek(dashboardDao.getLateCheckinsThisWeek());
            kpis.setAttendanceRate(dashboardDao.getAttendanceRate());

            // Payroll metrics - separated by currency
            kpis.setTotalPayrollThisMonthVND(dashboardDao.getTotalPayrollThisMonth("VND"));
            kpis.setTotalPayrollThisMonthUSD(dashboardDao.getTotalPayrollThisMonth("USD"));
            kpis.setPayslipsGenerated(dashboardDao.getPayslipsGeneratedThisMonth());
            kpis.setAverageSalaryVND(dashboardDao.getAverageSalary("VND"));
            kpis.setAverageSalaryUSD(dashboardDao.getAverageSalary("USD"));

            // Recruitment metrics
            kpis.setPendingRecruitmentRequests(dashboardDao.getPendingRecruitmentRequests());
            kpis.setTotalRecruitmentRequestsThisMonth(dashboardDao.getTotalRecruitmentRequestsThisMonth());
            kpis.setRecruitmentRequestsByStatus(dashboardDao.getRecruitmentRequestsByStatus());

            // Breakdowns
            kpis.setEmployeesByDepartment(dashboardDao.getEmployeesByDepartment());
            kpis.setEmployeesByPosition(dashboardDao.getEmployeesByPosition());
            kpis.setRequestsByStatus(dashboardDao.getRequestsByStatus());
            kpis.setRequestsByType(dashboardDao.getRequestsByType());
            kpis.setOtRequestsByStatus(dashboardDao.getOtRequestsByStatus());
            kpis.setLeaveRequestsByStatus(dashboardDao.getLeaveRequestsByStatus());

            // Trends
            kpis.setEmployeeTrend(dashboardDao.getEmployeeTrend());
            kpis.setOtTrend(dashboardDao.getOtTrend());
            kpis.setAttendanceTrend(dashboardDao.getAttendanceTrend());

            logger.info("Successfully fetched dashboard KPIs");
        } catch (Exception e) {
            logger.error("Error fetching dashboard KPIs", e);
            // Return partial data instead of throwing exception
        }

        return kpis;
    }
}
