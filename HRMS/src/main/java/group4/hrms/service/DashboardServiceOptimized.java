package group4.hrms.service;

import group4.hrms.dao.DashboardDaoOptimized;
import group4.hrms.dto.DashboardKpiDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * OPTIMIZED Service for Dashboard KPI business logic
 * Reduces database queries from 30+ to just 5 queries
 */
public class DashboardServiceOptimized {
    private static final Logger logger = LoggerFactory.getLogger(DashboardServiceOptimized.class);
    private final DashboardDaoOptimized dashboardDao;

    public DashboardServiceOptimized() {
        this.dashboardDao = new DashboardDaoOptimized();
    }

    public DashboardServiceOptimized(DashboardDaoOptimized dashboardDao) {
        this.dashboardDao = dashboardDao;
    }

    /**
     * Get all dashboard KPIs with OPTIMIZED queries
     * OLD: 30+ separate queries
     * NEW: 5 queries total (counts, decimals, payroll, breakdowns, trends)
     */
    public DashboardKpiDto getDashboardKpis() {
        logger.info("Fetching dashboard KPIs (OPTIMIZED)");
        long startTime = System.currentTimeMillis();
        
        DashboardKpiDto kpis = new DashboardKpiDto();

        try {
            // Query 1: Get all basic counts in ONE query (16 metrics)
            Map<String, Integer> counts = dashboardDao.getAllBasicCounts();
            kpis.setTotalEmployees(counts.getOrDefault("total_employees", 0));
            kpis.setActiveEmployees(counts.getOrDefault("active_employees", 0));
            kpis.setNewEmployeesThisMonth(counts.getOrDefault("new_employees_this_month", 0));
            kpis.setTotalDepartments(counts.getOrDefault("total_departments", 0));
            kpis.setTotalAccounts(counts.getOrDefault("total_accounts", 0));
            kpis.setActiveAccounts(counts.getOrDefault("active_accounts", 0));
            kpis.setInactiveAccounts(counts.getOrDefault("inactive_accounts", 0));
            kpis.setLockedAccounts(counts.getOrDefault("locked_accounts", 0));
            kpis.setPendingLeaveRequests(counts.getOrDefault("pending_leave_requests", 0));
            kpis.setApprovedLeavesToday(counts.getOrDefault("approved_leaves_today", 0));
            kpis.setPendingOtRequests(counts.getOrDefault("pending_ot_requests", 0));
            kpis.setPresentToday(counts.getOrDefault("present_today", 0));
            kpis.setLateCheckinsThisWeek(counts.getOrDefault("late_checkins_this_week", 0));
            kpis.setPayslipsGenerated(counts.getOrDefault("payslips_generated", 0));
            kpis.setPendingRecruitmentRequests(counts.getOrDefault("pending_recruitment_requests", 0));
            kpis.setTotalRecruitmentRequestsThisMonth(counts.getOrDefault("total_recruitment_requests_this_month", 0));
            
            // Calculate absent today
            int activeEmployees = kpis.getActiveEmployees();
            int presentToday = kpis.getPresentToday();
            kpis.setAbsentToday(Math.max(0, activeEmployees - presentToday));

            // Query 2: Get all decimal metrics in ONE query (4 metrics)
            Map<String, Double> decimals = dashboardDao.getAllDecimalMetrics();
            kpis.setAverageLeaveBalance(decimals.getOrDefault("avg_leave_balance", 0.0));
            double totalOtHours = decimals.getOrDefault("total_ot_hours", 0.0);
            kpis.setTotalOtHoursThisMonth(totalOtHours);
            kpis.setAttendanceRate(decimals.getOrDefault("attendance_rate", 0.0));
            
            // Calculate average OT per employee
            if (activeEmployees > 0) {
                kpis.setAverageOtHoursPerEmployee(totalOtHours / activeEmployees);
            }

            // Query 3: Get payroll metrics by currency in ONE query
            Map<String, BigDecimal> payroll = dashboardDao.getPayrollMetrics();
            kpis.setTotalPayrollThisMonthVND(payroll.getOrDefault("total_payroll_VND", BigDecimal.ZERO));
            kpis.setTotalPayrollThisMonthUSD(payroll.getOrDefault("total_payroll_USD", BigDecimal.ZERO));
            kpis.setAverageSalaryVND(payroll.getOrDefault("avg_salary_VND", BigDecimal.ZERO));
            kpis.setAverageSalaryUSD(payroll.getOrDefault("avg_salary_USD", BigDecimal.ZERO));

            // Query 4: Get payroll by department (VND and USD separately)
            Map<String, BigDecimal> payrollByDeptVND = dashboardDao.getPayrollByDepartmentByCurrency("VND");
            Map<String, BigDecimal> payrollByDeptUSD = dashboardDao.getPayrollByDepartmentByCurrency("USD");
            kpis.setPayrollByDepartmentVND(payrollByDeptVND);
            kpis.setPayrollByDepartmentUSD(payrollByDeptUSD);

            // Query 5: Get all breakdowns in ONE query (7 breakdowns)
            Map<String, Map<String, Integer>> breakdowns = dashboardDao.getAllBreakdowns();
            kpis.setEmployeesByDepartment(breakdowns.get("employees_by_department"));
            kpis.setEmployeesByPosition(breakdowns.get("employees_by_position"));
            kpis.setRequestsByStatus(breakdowns.get("requests_by_status"));
            kpis.setRequestsByType(breakdowns.get("requests_by_type"));
            kpis.setOtRequestsByStatus(breakdowns.get("ot_requests_by_status"));
            kpis.setLeaveRequestsByStatus(breakdowns.get("leave_requests_by_status"));
            kpis.setRecruitmentRequestsByStatus(breakdowns.get("recruitment_requests_by_status"));

            // Query 6: Get all trends in ONE query (3 trends)
            Map<String, Map<String, Double>> trends = dashboardDao.getAllTrends();
            
            // Convert employee trend from Double to Integer
            Map<String, Double> employeeTrendDouble = trends.get("employee_trend");
            Map<String, Integer> employeeTrendInt = new LinkedHashMap<>();
            employeeTrendDouble.forEach((k, v) -> employeeTrendInt.put(k, v.intValue()));
            kpis.setEmployeeTrend(employeeTrendInt);
            
            kpis.setOtTrend(trends.get("ot_trend"));
            kpis.setAttendanceTrend(trends.get("attendance_trend"));

            long endTime = System.currentTimeMillis();
            logger.info("Successfully fetched dashboard KPIs in {}ms (OPTIMIZED: 7 queries)", endTime - startTime);
            
        } catch (Exception e) {
            logger.error("Error fetching dashboard KPIs", e);
            // Return partial data instead of throwing exception
        }

        return kpis;
    }
}
