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

-- ==================== NEXT STEPS ====================

SELECT
    'NEXT STEPS:' as info,
    '1. Call Java: generator.generateHolidaysForYear(2025);' as step_1,
    '2. Calendar will AUTO-CREATE if not exists' as step_2,
    '3. Holidays will AUTO-GENERATE using lunar library' as step_3,
    '4. NO manual updates needed!' as step_4;

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
