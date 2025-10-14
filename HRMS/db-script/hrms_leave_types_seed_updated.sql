-- ====================================================================
-- HRMS Leave Request System - Seed Data (Updated for Current DB Schema)
-- Compatible với cấu trúc database hiện tại của project
-- Business Rules: BR-LV-03 đến BR-LV-06 + Emergency & Unpaid Leave
-- ====================================================================

USE hrms;

-- ====================================================================
-- 1. REQUEST TYPES - Insert vào cấu trúc hiện tại
-- ====================================================================

INSERT INTO
    request_types (code, name)
VALUES (
        'LEAVE_REQUEST',
        'Leave Request'
    ),
    (
        'OVERTIME_REQUEST',
        'Overtime Request'
    ),
    (
        'ADJUSTMENT_REQUEST',
        'Adjustment Request'
    ),
    (
        'DOCUMENT_REQUEST',
        'Document Request'
    )
ON DUPLICATE KEY UPDATE
    name = VALUES(name);

-- ====================================================================
-- 2. THÊM CÁC COLUMN CẦN THIẾT VÀO LEAVE_TYPES (nếu chưa có)
-- ====================================================================

-- Kiểm tra và thêm cột description
SELECT COUNT(*) INTO @col_exists
FROM INFORMATION_SCHEMA.COLUMNS
WHERE
    TABLE_SCHEMA = 'hrms'
    AND TABLE_NAME = 'leave_types'
    AND COLUMN_NAME = 'description';

SET
    @sql = IF(
        @col_exists = 0,
        'ALTER TABLE leave_types ADD COLUMN description TEXT NULL AFTER name',
        'SELECT "description column exists" as message'
    );

PREPARE stmt FROM @sql;

EXECUTE stmt;

DEALLOCATE PREPARE stmt;

-- Kiểm tra và thêm cột max_days
SELECT COUNT(*) INTO @col_exists
FROM INFORMATION_SCHEMA.COLUMNS
WHERE
    TABLE_SCHEMA = 'hrms'
    AND TABLE_NAME = 'leave_types'
    AND COLUMN_NAME = 'max_days';

SET
    @sql = IF(
        @col_exists = 0,
        'ALTER TABLE leave_types ADD COLUMN max_days DECIMAL(5,2) NULL DEFAULT NULL AFTER default_days',
        'SELECT "max_days column exists" as message'
    );

PREPARE stmt FROM @sql;

EXECUTE stmt;

DEALLOCATE PREPARE stmt;

-- Kiểm tra và thêm cột requires_approval
SELECT COUNT(*) INTO @col_exists
FROM INFORMATION_SCHEMA.COLUMNS
WHERE
    TABLE_SCHEMA = 'hrms'
    AND TABLE_NAME = 'leave_types'
    AND COLUMN_NAME = 'requires_approval';

SET
    @sql = IF(
        @col_exists = 0,
        'ALTER TABLE leave_types ADD COLUMN requires_approval TINYINT(1) NOT NULL DEFAULT 1 AFTER is_paid',
        'SELECT "requires_approval column exists" as message'
    );

PREPARE stmt FROM @sql;

EXECUTE stmt;

DEALLOCATE PREPARE stmt;

-- Kiểm tra và thêm cột requires_certificate
SELECT COUNT(*) INTO @col_exists
FROM INFORMATION_SCHEMA.COLUMNS
WHERE
    TABLE_SCHEMA = 'hrms'
    AND TABLE_NAME = 'leave_types'
    AND COLUMN_NAME = 'requires_certificate';

SET
    @sql = IF(
        @col_exists = 0,
        'ALTER TABLE leave_types ADD COLUMN requires_certificate TINYINT(1) NOT NULL DEFAULT 0 AFTER requires_approval',
        'SELECT "requires_certificate column exists" as message'
    );

PREPARE stmt FROM @sql;

EXECUTE stmt;

DEALLOCATE PREPARE stmt;

-- Kiểm tra và thêm cột min_advance_notice
SELECT COUNT(*) INTO @col_exists
FROM INFORMATION_SCHEMA.COLUMNS
WHERE
    TABLE_SCHEMA = 'hrms'
    AND TABLE_NAME = 'leave_types'
    AND COLUMN_NAME = 'min_advance_notice';

SET
    @sql = IF(
        @col_exists = 0,
        'ALTER TABLE leave_types ADD COLUMN min_advance_notice INT NULL DEFAULT NULL AFTER requires_certificate',
        'SELECT "min_advance_notice column exists" as message'
    );

PREPARE stmt FROM @sql;

EXECUTE stmt;

DEALLOCATE PREPARE stmt;

-- Kiểm tra và thêm cột is_active
SELECT COUNT(*) INTO @col_exists
FROM INFORMATION_SCHEMA.COLUMNS
WHERE
    TABLE_SCHEMA = 'hrms'
    AND TABLE_NAME = 'leave_types'
    AND COLUMN_NAME = 'is_active';

SET
    @sql = IF(
        @col_exists = 0,
        'ALTER TABLE leave_types ADD COLUMN is_active TINYINT(1) NOT NULL DEFAULT 1 AFTER min_advance_notice',
        'SELECT "is_active column exists" as message'
    );

PREPARE stmt FROM @sql;

EXECUTE stmt;

DEALLOCATE PREPARE stmt;

-- Kiểm tra và thêm cột updated_at
SELECT COUNT(*) INTO @col_exists
FROM INFORMATION_SCHEMA.COLUMNS
WHERE
    TABLE_SCHEMA = 'hrms'
    AND TABLE_NAME = 'leave_types'
    AND COLUMN_NAME = 'updated_at';

SET
    @sql = IF(
        @col_exists = 0,
        'ALTER TABLE leave_types ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at',
        'SELECT "updated_at column exists" as message'
    );

PREPARE stmt FROM @sql;

EXECUTE stmt;

DEALLOCATE PREPARE stmt;

-- ====================================================================
-- 3. INSERT LEAVE TYPES - 6 loại theo Business Rules
-- ====================================================================

INSERT INTO
    leave_types (
        code,
        name,
        description,
        default_days,
        max_days,
        is_paid,
        requires_approval,
        requires_certificate,
        min_advance_notice,
        is_active
    )
VALUES
    -- ============= 1. ANNUAL LEAVE (BR-LV-03) =============
    (
        'ANNUAL',
        'Annual Leave',
        'Annual leave after 12 months: 12/14/16 days per condition. Pro-rate if <12 months; +1 day per 5 years service',
        12.00,
        21.00,
        1,
        1,
        0,
        3,
        1
    ),

-- ============= 2. PERSONAL LEAVE (BR-LV-04) =============
(
    'PERSONAL',
    'Personal Leave',
    'Personal leave (paid): 3 days (marriage); 1 day (child marriage); 3 days (death of parent/spouse/child)',
    3.00,
    3.00,
    1,
    1,
    1,
    1,
    1
),

-- ============= 3. MATERNITY LEAVE (BR-LV-05) =============
(
    'MATERNITY',
    'Maternity Leave',
    'Maternity 6 months (Social Insurance). Records and coordinates SI claims per BR-LV-07',
    180.00,
    180.00,
    1,
    1,
    1,
    30,
    1
),

-- ============= 4. SICK LEAVE (BR-LV-06) =============
(
    'SICK',
    'Sick Leave',
    'Sick leave (SI): 30/40/60 days by SI seniority. Child-care sickness: 20 days (<3y), 15 days (3-<7y)',
    30.00,
    60.00,
    1,
    0,
    1,
    0,
    1
),

-- ============= 5. EMERGENCY LEAVE =============
(
    'EMERGENCY',
    'Emergency Leave',
    'Emergency leave for urgent family matters, accidents, or unforeseen circumstances. No advance notice required.',
    3.00,
    5.00,
    1,
    1,
    0,
    0,
    1
),

-- ============= 6. UNPAID LEAVE =============
(
    'UNPAID',
    'Unpaid Leave',
    'Personal leave without pay for extended absences, sabbatical, or personal matters.',
    0.00,
    90.00,
    0,
    1,
    0,
    14,
    1
)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    default_days = VALUES(default_days),
    max_days = VALUES(max_days),
    is_paid = VALUES(is_paid),
    requires_approval = VALUES(requires_approval),
    requires_certificate = VALUES(requires_certificate),
    min_advance_notice = VALUES(min_advance_notice),
    is_active = VALUES(is_active),
    updated_at = CURRENT_TIMESTAMP;

-- ====================================================================
-- 4. VERIFICATION - Kiểm tra dữ liệu
-- ====================================================================

SELECT '=== REQUEST TYPES ===' as INFO;

SELECT id, code, name FROM request_types ORDER BY code;

SELECT '=== LEAVE TYPES (6 loại theo Business Rules) ===' as INFO;

SELECT
    id,
    code,
    name,
    default_days,
    max_days,
    is_paid,
    requires_approval,
    requires_certificate,
    min_advance_notice,
    is_active
FROM leave_types
WHERE
    is_active = 1
ORDER BY code;

SELECT '=== BUSINESS RULES MAPPING ===' as INFO;

SELECT code, name, CONCAT(
        'BR-LV-', CASE code
            WHEN 'ANNUAL' THEN '03 (Annual leave after 12 months)'
            WHEN 'PERSONAL' THEN '04 (Personal leave paid)'
            WHEN 'MATERNITY' THEN '05 (Maternity 6 months)'
            WHEN 'SICK' THEN '06 (Sick leave SI)'
            WHEN 'EMERGENCY' THEN 'Custom (Emergency leave)'
            WHEN 'UNPAID' THEN 'Custom (Unpaid leave)'
            ELSE 'Unknown'
        END
    ) as business_rule_ref
FROM leave_types
WHERE
    is_active = 1
ORDER BY code;

SELECT '=== DATABASE SCHEMA INFO ===' as INFO;

SELECT
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE
    TABLE_SCHEMA = 'hrms'
    AND TABLE_NAME = 'leave_types'
ORDER BY ORDINAL_POSITION;

COMMIT;

-- ====================================================================
-- NOTES FOR INTEGRATION
-- ====================================================================
/*
NOTES: Cấu trúc database của bạn:

1. leave_types: Đã được cập nhật với các cột cần thiết
2. request_types: Giữ nguyên cấu trúc hiện tại (chỉ có code, name)
3. requests: Sử dụng JSON trong cột 'detail' để lưu leave request data

Để tạo leave request, cần:
- request_type_id = (SELECT id FROM request_types WHERE code = 'LEAVE_REQUEST')
- detail JSON sẽ chứa: {"leave_type_code": "ANNUAL", "start_date": "2024-01-01", "end_date": "2024-01-03", "reason": "Vacation", ...}

Example JSON structure cho requests.detail:
{
"leave_type_code": "ANNUAL",
"start_date": "2024-01-01",
"end_date": "2024-01-03",
"duration_days": 3,
"reason": "Personal vacation",
"manager_notes": null,
"certificate_attached": false
}
*/

SELECT '=== HRMS LEAVE SYSTEM - READY FOR USE ===' as FINAL_MESSAGE;

SELECT 'Database schema updated and seed data inserted successfully!' as STATUS;