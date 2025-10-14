package group4.hrms.util;

import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;

import java.time.LocalDate;
import java.util.logging.Logger;

/**
 * Utility class for Vietnamese Lunar Calendar calculations
 * Uses lunar-java library to automatically convert lunar dates to solar dates
 *
 * NO MORE HARDCODING!
 * - Automatically calculates Lunar New Year for any year
 * - Automatically calculates Hung Kings' Day for any year
 * - Works for past, present, and future years
 *
 * Usage:
 *   LocalDate tetDate = LunarCalendarUtil.getLunarNewYear(2025);
 *   LocalDate hungKingsDate = LunarCalendarUtil.getHungKingsDay(2025);
 */
public class LunarCalendarUtil {

    private static final Logger logger = Logger.getLogger(LunarCalendarUtil.class.getName());

    /**
     * Get Lunar New Year date (Mùng 1 Tết) for a specific year
     *
     * @param year The solar year (e.g., 2025)
     * @return LocalDate of Mùng 1 Tết in solar calendar
     */
    public static LocalDate getLunarNewYear(int year) {
        try {
            // Mùng 1 tháng 1 âm lịch
            Lunar lunar = new Lunar(year, 1, 1);
            Solar solar = lunar.getSolar();

            LocalDate result = LocalDate.of(
                solar.getYear(),
                solar.getMonth(),
                solar.getDay()
            );

            logger.info("Lunar New Year " + year + ": " + result);
            return result;

        } catch (Exception e) {
            logger.severe("Error calculating Lunar New Year for " + year + ": " + e.getMessage());
            throw new RuntimeException("Failed to calculate Lunar New Year", e);
        }
    }

    /**
     * Get Hung Kings' Commemoration Day (10th day of 3rd lunar month)
     *
     * @param year The solar year (e.g., 2025)
     * @return LocalDate of Hung Kings' Day in solar calendar
     */
    public static LocalDate getHungKingsDay(int year) {
        try {
            // Ngày 10 tháng 3 âm lịch
            Lunar lunar = new Lunar(year, 3, 10);
            Solar solar = lunar.getSolar();

            LocalDate result = LocalDate.of(
                solar.getYear(),
                solar.getMonth(),
                solar.getDay()
            );

            logger.info("Hung Kings' Day " + year + ": " + result);
            return result;

        } catch (Exception e) {
            logger.severe("Error calculating Hung Kings' Day for " + year + ": " + e.getMessage());
            throw new RuntimeException("Failed to calculate Hung Kings' Day", e);
        }
    }

    /**
     * Convert lunar date to solar date
     *
     * @param year Lunar year
     * @param month Lunar month (1-12)
     * @param day Lunar day (1-30)
     * @return LocalDate in solar calendar
     */
    public static LocalDate lunarToSolar(int year, int month, int day) {
        try {
            Lunar lunar = new Lunar(year, month, day);
            Solar solar = lunar.getSolar();

            return LocalDate.of(
                solar.getYear(),
                solar.getMonth(),
                solar.getDay()
            );

        } catch (Exception e) {
            logger.severe("Error converting lunar date " + year + "-" + month + "-" + day + ": " + e.getMessage());
            throw new RuntimeException("Failed to convert lunar date", e);
        }
    }

    /**
     * Convert solar date to lunar date
     *
     * @param date Solar date
     * @return Lunar object with lunar year, month, day
     */
    public static Lunar solarToLunar(LocalDate date) {
        try {
            Solar solar = new Solar(
                date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth()
            );

            return solar.getLunar();

        } catch (Exception e) {
            logger.severe("Error converting solar date " + date + ": " + e.getMessage());
            throw new RuntimeException("Failed to convert solar date", e);
        }
    }

    /**
     * Check if a solar date is a lunar holiday
     *
     * @param date Solar date to check
     * @return true if it's Lunar New Year or Hung Kings' Day
     */
    public static boolean isLunarHoliday(LocalDate date) {
        try {
            Lunar lunar = solarToLunar(date);

            // Check if Mùng 1 Tết (1/1 lunar)
            if (lunar.getMonth() == 1 && lunar.getDay() == 1) {
                return true;
            }

            // Check if Hung Kings' Day (10/3 lunar)
            if (lunar.getMonth() == 3 && lunar.getDay() == 10) {
                return true;
            }

            return false;

        } catch (Exception e) {
            logger.warning("Error checking lunar holiday for " + date + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all Tết days for a specific year (from 28 Tết to Mùng 5)
     *
     * @param year Solar year
     * @param duration Number of days (default 7: from 28 Tết to Mùng 5)
     * @return Array of LocalDate for Tết days
     */
    public static LocalDate[] getTetDays(int year, int duration) {
        LocalDate mung1 = getLunarNewYear(year);
        LocalDate[] tetDays = new LocalDate[duration];

        // Start from 28 Tết (1 day before Mùng 1)
        LocalDate startDate = mung1.minusDays(1);

        for (int i = 0; i < duration; i++) {
            tetDays[i] = startDate.plusDays(i);
        }

        return tetDays;
    }
}
