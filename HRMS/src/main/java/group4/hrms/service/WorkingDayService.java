package group4.hrms.service;

import group4.hrms.dao.HolidayDao;
import group4.hrms.dto.ValidationResult;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for working day validation
 * Handles date validation for half-day leave requests
 *
 * Requirements: 5.7
 */
public class WorkingDayService {
    private static final Logger logger = Logger.getLogger(WorkingDayService.class.getName());

    private final HolidayDao holidayDao;

    public WorkingDayService() {
        this.holidayDao = new HolidayDao();
    }

    public WorkingDayService(HolidayDao holidayDao) {
        this.holidayDao = holidayDao;
    }

    /**
     * Check if date is a working day (not weekend or holiday)
     *
     * Requirements: 5.7
     *
     * @param date Date to check
     * @return true if date is a working day, false otherwise
     */
    public boolean isWorkingDay(LocalDate date) {
        if (date == null) {
            logger.warning("isWorkingDay called with null date");
            return false;
        }

        try {
            // Check if date is weekend (Saturday or Sunday)
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                logger.fine(String.format("Date %s is a weekend (%s)", date, dayOfWeek));
                return false;
            }

            // Check if date is a holiday
            boolean isHoliday = holidayDao.isHoliday(date);
            if (isHoliday) {
                logger.fine(String.format("Date %s is a holiday", date));
                return false;
            }

            logger.fine(String.format("Date %s is a working day", date));
            return true;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, String.format("Error checking if date %s is working day", date), e);
            // In case of database error, assume it's a working day to avoid blocking operations
            // This is a safe default as the validation can be done manually during approval
            return true;
        }
    }

    /**
     * Validate that half-day leave can be requested on the given date
     *
     * Requirements: 5.7
     *
     * @param date Date to validate
     * @return ValidationResult with success or error message
     */
    public ValidationResult validateHalfDayDate(LocalDate date) {
        if (date == null) {
            logger.warning("validateHalfDayDate called with null date");
            return ValidationResult.error("Date cannot be null");
        }

        logger.fine(String.format("Validating half-day date: %s", date));

        // Check if weekend
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            String errorMessage = String.format(
                "Half-day leave cannot be requested on weekends. %s is a %s.",
                date,
                dayOfWeek
            );
            logger.info(String.format("Half-day validation failed for date %s: weekend", date));
            return ValidationResult.error(errorMessage);
        }

        // Check if holiday
        try {
            boolean isHoliday = holidayDao.isHoliday(date);
            if (isHoliday) {
                String errorMessage = String.format(
                    "Half-day leave cannot be requested on holidays. %s is a public holiday.",
                    date
                );
                logger.info(String.format("Half-day validation failed for date %s: holiday", date));
                return ValidationResult.error(errorMessage);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, String.format("Error checking if date %s is holiday", date), e);
            // Continue validation - assume not a holiday if database error
        }

        logger.fine(String.format("Half-day date validation passed for date %s", date));
        return ValidationResult.success();
    }
}
