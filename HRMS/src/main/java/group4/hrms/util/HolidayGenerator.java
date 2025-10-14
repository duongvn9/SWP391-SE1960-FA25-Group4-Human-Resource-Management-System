package group4.hrms.util;

import group4.hrms.dao.HolidayCalendarDao;
import group4.hrms.dao.HolidayDao;
import group4.hrms.model.Holiday;
import group4.hrms.model.HolidayCalendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Holiday Generator - 100% Automatic using Lunar Calendar Library
 *
 * NO MORE HARDCODING!
 * - Automatically calculates lunar dates using lunar-java library
 * - Automatically generates Tết from 28 Tết to Mùng 5 (7 days)
 * - Automatically adds compensatory days when holidays fall on weekends
 * - Each holiday that falls on Sat/Sun gets its own compensatory day
 *
 * Usage:
 *   HolidayGenerator generator = new HolidayGenerator(calendarDao, holidayDao);
 *   generator.generateHolidaysForYear(2025);
 */
public class HolidayGenerator {

    private static final Logger logger = Logger.getLogger(HolidayGenerator.class.getName());

    private final HolidayCalendarDao calendarDao;
    private final HolidayDao holidayDao;

    public HolidayGenerator(HolidayCalendarDao calendarDao, HolidayDao holidayDao) {
        this.calendarDao = calendarDao;
        this.holidayDao = holidayDao;
    }

    /**
     * Generate all Vietnam public holidays for a specific year
     * Automatically calculates lunar dates - no manual updates needed!
     *
     * @param year The year to generate holidays for
     * @return Number of holidays generated
     */
    public int generateHolidaysForYear(int year) {
        logger.info("Generating holidays for year " + year);

        try {
            // Get or create holiday calendar
            HolidayCalendar calendar = getOrCreateCalendar(year);

            // Check if already generated
            if (calendar.isGenerated()) {
                logger.info("Holidays already generated for year " + year);
                return 0;
            }

            List<Holiday> holidays = new ArrayList<>();

            // Fixed holidays (Solar calendar)
            holidays.addAll(generateFixedHolidays(calendar, year));

            // Lunar holidays (automatically calculated)
            holidays.addAll(generateLunarHolidays(calendar, year));

            // Save all holidays
            int count = 0;
            for (Holiday holiday : holidays) {
                if (!holidayDao.exists(calendar.getId(), holiday.getDateHoliday())) {
                    holidayDao.save(holiday);
                    count++;
                }
            }

            // Mark as generated
            calendar.setGenerated(true);
            calendarDao.update(calendar);

            logger.info("Generated " + count + " holidays for year " + year);
            return count;

        } catch (Exception e) {
            logger.severe("Error generating holidays for year " + year + ": " + e.getMessage());
            throw new RuntimeException("Failed to generate holidays", e);
        }
    }

    /**
     * Generate fixed holidays (solar calendar)
     */
    private List<Holiday> generateFixedHolidays(HolidayCalendar calendar, int year) {
        List<Holiday> holidays = new ArrayList<>();

        // New Year's Day (Jan 1)
        addHolidayWithCompensatory(holidays, calendar,
            LocalDate.of(year, 1, 1), "Tết Dương lịch");

        // Reunification Day (Apr 30)
        addHolidayWithCompensatory(holidays, calendar,
            LocalDate.of(year, 4, 30), "Ngày Giải phóng miền Nam");

        // International Labor Day (May 1)
        addHolidayWithCompensatory(holidays, calendar,
            LocalDate.of(year, 5, 1), "Ngày Quốc tế Lao động");

        // National Day (Sep 2-3, 2 days)
        addHolidayWithCompensatory(holidays, calendar,
            LocalDate.of(year, 9, 2), "Quốc khánh (Ngày 1)");
        addHolidayWithCompensatory(holidays, calendar,
            LocalDate.of(year, 9, 3), "Quốc khánh (Ngày 2)");

        return holidays;
    }

    /**
     * Generate lunar holidays using LunarCalendarUtil
     * 100% AUTOMATIC - No hardcoding, no manual updates needed!
     */
    private List<Holiday> generateLunarHolidays(HolidayCalendar calendar, int year) {
        List<Holiday> holidays = new ArrayList<>();

        try {
            // Lunar New Year (Tết) - Automatically calculated from lunar calendar
            LocalDate tetMung1 = LunarCalendarUtil.getLunarNewYear(year);
            int tetDuration = calendar.getTetDuration(); // Default 7 days

            // Calculate from 28 Tết (1 day before Mùng 1)
            LocalDate tetStart = tetMung1.minusDays(1); // 28 Tết

            String[] tetNames = {
                "Tết Nguyên Đán (Ngày 28 Tết)",
                "Tết Nguyên Đán (Ngày 29 Tết)",
                "Tết Nguyên Đán (Mùng 1 Tết)",
                "Tết Nguyên Đán (Mùng 2 Tết)",
                "Tết Nguyên Đán (Mùng 3 Tết)",
                "Tết Nguyên Đán (Mùng 4 Tết)",
                "Tết Nguyên Đán (Mùng 5 Tết)"
            };

            for (int i = 0; i < Math.min(tetDuration, tetNames.length); i++) {
                LocalDate tetDay = tetStart.plusDays(i);
                addHolidayWithCompensatory(holidays, calendar, tetDay, tetNames[i]);
            }

            logger.info("Generated " + tetDuration + " days for Tết starting from " + tetStart +
                       " (Mùng 1: " + tetMung1 + ")");

            // Hung Kings' Commemoration Day - Automatically calculated
            LocalDate hungKingsDay = LunarCalendarUtil.getHungKingsDay(year);
            addHolidayWithCompensatory(holidays, calendar,
                hungKingsDay, "Giỗ Tổ Hùng Vương");

            logger.info("Generated Hung Kings' Day: " + hungKingsDay);

        } catch (Exception e) {
            logger.severe("Error generating lunar holidays for year " + year + ": " + e.getMessage());
            throw new RuntimeException("Failed to generate lunar holidays", e);
        }

        return holidays;
    }

    /**
     * Add holiday and automatically calculate compensatory day if needed
     * IMPORTANT: Each holiday that falls on weekend gets its own compensatory day
     *
     * Example:
     * - Mùng 4 (Saturday) → Compensatory on next Monday
     * - Mùng 5 (Sunday) → Compensatory on next Tuesday (not Monday, because Monday is already taken)
     */
    private void addHolidayWithCompensatory(List<Holiday> holidays, HolidayCalendar calendar,
                                            LocalDate date, String name) {
        // Add the holiday
        Holiday holiday = new Holiday();
        holiday.setCalendarId(calendar.getId());
        holiday.setDateHoliday(date);
        holiday.setName(name);
        holidays.add(holiday);

        // Check if auto_compensatory is enabled
        if (!calendar.isAutoCompensatory()) {
            return; // Skip compensatory day calculation
        }

        // Check if holiday falls on weekend
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            // Calculate compensatory day
            // Saturday → +2 days (Monday)
            // Sunday → +1 day (Monday)
            int daysToAdd = (dayOfWeek == DayOfWeek.SATURDAY) ? 2 : 1;
            LocalDate initialCompensatoryDate = date.plusDays(daysToAdd);

            // Check if compensatory date already exists (to avoid duplicates)
            final LocalDate checkDate = initialCompensatoryDate;
            boolean alreadyExists = holidays.stream()
                .anyMatch(h -> h.getDateHoliday().equals(checkDate));

            LocalDate finalCompensatoryDate;
            if (!alreadyExists) {
                finalCompensatoryDate = initialCompensatoryDate;
            } else {
                // If Monday is already taken, add on Tuesday
                finalCompensatoryDate = initialCompensatoryDate.plusDays(1);
            }

            // Add compensatory day
            Holiday compensatory = new Holiday();
            compensatory.setCalendarId(calendar.getId());
            compensatory.setDateHoliday(finalCompensatoryDate);
            compensatory.setName("Nghỉ bù " + name + " (rơi vào " +
                (dayOfWeek == DayOfWeek.SATURDAY ? "T7" : "CN") + ")");
            holidays.add(compensatory);

            String availabilityNote = alreadyExists ? " (next available day)" : "";
            logger.info("Added compensatory day for " + name + " on " + finalCompensatoryDate + availabilityNote);
        }
    }

    /**
     * Get or create holiday calendar for a year
     */
    private HolidayCalendar getOrCreateCalendar(int year) throws Exception {
        String calendarName = "Vietnam Public Holidays " + year;

        // Try to find existing calendar
        HolidayCalendar calendar = calendarDao.findByYearAndName(year, calendarName);

        if (calendar == null) {
            // Create new calendar with default settings
            calendar = new HolidayCalendar();
            calendar.setYear(year);
            calendar.setName(calendarName);
            calendar.setTetDuration(7); // Default 7 days
            calendar.setAutoCompensatory(true); // Enable auto compensatory
            calendar.setGenerated(false);
            calendar = calendarDao.save(calendar);
            logger.info("Created new holiday calendar for year " + year);
        }

        return calendar;
    }

    /**
     * Generate holidays for multiple years
     */
    public void generateHolidaysForYears(int startYear, int endYear) {
        logger.info("Generating holidays for years " + startYear + " to " + endYear);

        int totalGenerated = 0;
        for (int year = startYear; year <= endYear; year++) {
            try {
                totalGenerated += generateHolidaysForYear(year);
            } catch (Exception e) {
                logger.warning("Failed to generate holidays for year " + year + ": " + e.getMessage());
            }
        }

        logger.info("Total holidays generated: " + totalGenerated);
    }
}
