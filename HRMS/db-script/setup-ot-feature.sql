-- ==================== SETUP OT REQUEST FEATURE ====================
-- File: setup-ot-feature.sql
-- Purpose: Setup database for OT Request with AUTO-CREATE calendars
--
-- What this does:
-- 1. Add 4 columns to holiday_calendar table
-- 2. That's it! Calendars will be AUTO-CREATED when needed by Java code
--
-- Prerequisites:
-- - hrms_mysql.sql (creates tables)
-- - request_types_seed.sql (creates OVERTIME_REQUEST)
--
-- After running:
-- - Just call: generator.generateHolidaysForYear(2025);
-- - Calendar will be AUTO-CREATED if not exists
-- - Holidays will be AUTO-GENERATED using lunar library

USE hrms;

-- ==================== ADD COLUMNS TO holiday_calendar ====================

SET @dbname = DATABASE();
SET @tablename = 'holiday_calendar';

-- Add tet_duration (số ngày nghỉ Tết, mặc định 7)
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = 'tet_duration');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE holiday_calendar ADD COLUMN tet_duration INT NOT NULL DEFAULT 7 AFTER name',
    'SELECT ''tet_duration exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Add auto_compensatory (tự động nghỉ bù)
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = 'auto_compensatory');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE holiday_calendar ADD COLUMN auto_compensatory BOOLEAN NOT NULL DEFAULT 1 AFTER tet_duration',
    'SELECT ''auto_compensatory exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Add is_generated (đã generate chưa)
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = 'is_generated');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE holiday_calendar ADD COLUMN is_generated BOOLEAN NOT NULL DEFAULT 0 AFTER auto_compensatory',
    'SELECT ''is_generated exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Add updated_at
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = 'updated_at');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE holiday_calendar ADD COLUMN updated_at DATETIME(0) NOT NULL DEFAULT (UTC_TIMESTAMP()) ON UPDATE CURRENT_TIMESTAMP AFTER created_at',
    'SELECT ''updated_at exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ==================== ADD COLUMNS TO holidays TABLE ====================
-- Support for BR-OT-02: Distinguish original holidays (300% OT) vs substitute days (200% OT)

SET @tablename = 'holidays';

-- Add is_substitute (TRUE if this is a compensatory/substitute day)
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = 'is_substitute');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE holidays ADD COLUMN is_substitute BOOLEAN DEFAULT FALSE COMMENT ''TRUE if this is a compensatory/substitute day (200% OT)'' AFTER name',
    'SELECT ''is_substitute exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Add original_holiday_date (link to original holiday if this is a substitute)
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = @tablename AND COLUMN_NAME = 'original_holiday_date');
SET @sql = IF(@col_exists = 0,
    'ALTER TABLE holidays ADD COLUMN original_holiday_date DATE NULL COMMENT ''Original holiday date if this is a substitute day'' AFTER is_substitute',
    'SELECT ''original_holiday_date exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Add indexes for better query performance
SET @index_exists = (SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = 'holidays' AND INDEX_NAME = 'idx_holidays_is_substitute');
SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_holidays_is_substitute ON holidays(is_substitute)',
    'SELECT ''idx_holidays_is_substitute exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = 'holidays' AND INDEX_NAME = 'idx_holidays_original_date');
SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_holidays_original_date ON holidays(original_holiday_date)',
    'SELECT ''idx_holidays_original_date exists'' AS msg');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Update existing holidays to mark as original (not substitute)
UPDATE holidays SET is_substitute = FALSE WHERE is_substitute IS NULL;

-- ==================== VERIFICATION ====================

SELECT '✓ Setup completed!' AS status;

SELECT
    CASE
        WHEN EXISTS (SELECT 1 FROM request_types WHERE code = 'OVERTIME_REQUEST')
        THEN '✓ OVERTIME_REQUEST exists'
        ELSE '✗ Run request_types_seed.sql first!'
    END as request_type_check,
    CASE
        WHEN EXISTS (
            SELECT 1 FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
            AND TABLE_NAME = 'holiday_calendar'
            AND COLUMN_NAME = 'tet_duration'
        )
        THEN '✓ Columns added'
        ELSE '✗ Columns not added'
    END as schema_check;

-- Show existing calendars (if any)
SELECT
    CASE
        WHEN COUNT(*) = 0 THEN 'No calendars yet - will AUTO-CREATE when needed'
        ELSE CONCAT(COUNT(*), ' calendars exist')
    END as calendar_status
FROM holiday_calendar;

-- ==================== BUSINESS RULES REFERENCE ====================

SELECT '=== OT MULTIPLIERS (BR-OT-01, BR-OT-02) ===' as info;

SELECT
    'WEEKDAY' as ot_type,
    '150%' as multiplier,
    'Monday - Friday (regular working days)' as applies_to,
    'OTRequestDetail.otType = "WEEKDAY"' as code_value
UNION ALL
SELECT
    'WEEKEND' as ot_type,
    '200%' as multiplier,
    'Saturday, Sunday (weekly rest days)' as applies_to,
    'OTRequestDetail.otType = "WEEKEND"' as code_value
UNION ALL
SELECT
    'HOLIDAY' as ot_type,
    '300%' as multiplier,
    'Original public holidays (Tet, National Day, etc.)' as applies_to,
    'OTRequestDetail.otType = "HOLIDAY"' as code_value
UNION ALL
SELECT
    'COMPENSATORY' as ot_type,
    '200%' as multiplier,
    'Substitute days (when holiday falls on weekend)' as applies_to,
    'OTRequestDetail.otType = "COMPENSATORY"' as code_value;

SELECT '=== IMPORTANT: BR-OT-02 ===' as info;

SELECT
    'Substitute Day ≠ Public Holiday' as rule,
    'When holiday falls on weekend, substitute day is granted on next working day' as explanation,
    'Substitute day OT = 200% (weekly rest day rate), NOT 300% (holiday rate)' as key_point,
    'holidays.is_substitute = TRUE for substitute days' as database_field,
    'Holiday.getOTMultiplier() returns 2.0 for substitute, 3.0 for original' as code_method;

-- ==================== NEXT STEPS ====================

SELECT
    'NEXT STEPS:' as info,
    '1. Call Java: generator.generateHolidaysForYear(2025);' as step_1,
    '2. Calendar will AUTO-CREATE if not exists' as step_2,
    '3. Holidays will AUTO-GENERATE using lunar library' as step_3,
    '4. Substitute days AUTO-MARKED with is_substitute=TRUE' as step_4,
    '5. Use OTCalculationService.determineOTType(date) to get OT type' as step_5,
    '6. NO manual updates needed!' as step_6;

-- ==================== HOW IT WORKS ====================
--
-- AUTO-CREATE LOGIC:
-- When you call: generator.generateHolidaysForYear(2031)
-- 1. Check: Calendar for 2031 exists?
-- 2. NO → Auto-create with defaults (7 days Tết, auto compensatory)
-- 3. YES → Use existing
-- 4. Generate holidays using lunar library
-- 5. Mark as generated
--
-- EXAMPLE:
-- generator.generateHolidaysForYear(2025);
-- → Auto-creates calendar for 2025
-- → Calculates Mùng 1 Tết = 2025-01-29 (using lunar library)
-- → Generates 7 days Tết (28 Tết to Mùng 5)
-- → Generates fixed holidays (1/1, 30/4, 1/5, 2-3/9)
-- → Generates Hung Kings' Day (using lunar library)
-- → Auto-adds compensatory days for holidays on Sat/Sun
-- → Done!
--
-- TO CUSTOMIZE:
-- If you want 9 days Tết instead of 7:
-- INSERT INTO holiday_calendar (year, name, tet_duration, auto_compensatory)
-- VALUES (2025, 'Vietnam Public Holidays 2025', 9, 1);
-- Then call: generator.generateHolidaysForYear(2025);
--
-- ADVANTAGES:
-- ✓ No manual calendar creation needed
-- ✓ No manual date updates needed
-- ✓ Works for any year (past, present, future)
-- ✓ Lunar library calculates everything automatically
-- ✓ Each holiday on Sat/Sun gets its own compensatory day
