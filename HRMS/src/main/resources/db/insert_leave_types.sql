-- ==================== INSERT REQUEST TYPES & LEAVE TYPES ====================
-- Script to populate both request_types and leave_types tables
-- This script should be run after creating the database schema

-- Insert main request types (4 loại đơn chính)
INSERT INTO
    request_types (code, name, created_at)
VALUES (
        'ATTENDANCE_APPEAL',
        'Attendance Appeal Request',
        UTC_TIMESTAMP()
    ),
    (
        'RECRUITMENT_REQUEST',
        'Recruitment Request',
        UTC_TIMESTAMP()
    ),
    (
        'OT_REQUEST',
        'Overtime Request',
        UTC_TIMESTAMP()
    ),
    (
        'LEAVE_REQUEST',
        'Leave Request',
        UTC_TIMESTAMP()
    )
ON DUPLICATE KEY UPDATE
    name = VALUES(name);

-- Insert leave types (các loại nghỉ phép cụ thể)
INSERT INTO
    leave_types (
        code,
        name,
        default_days,
        is_paid,
        created_at
    )
VALUES (
        'ANNUAL',
        'Annual Leave',
        21.00,
        TRUE,
        UTC_TIMESTAMP()
    ),
    (
        'SICK',
        'Sick Leave',
        30.00,
        TRUE,
        UTC_TIMESTAMP()
    ),
    (
        'MATERNITY',
        'Maternity Leave',
        180.00,
        TRUE,
        UTC_TIMESTAMP()
    ),
    (
        'EMERGENCY',
        'Emergency Leave',
        5.00,
        FALSE,
        UTC_TIMESTAMP()
    ),
    (
        'PERSONAL',
        'Personal Leave',
        10.00,
        FALSE,
        UTC_TIMESTAMP()
    )
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    default_days = VALUES(default_days),
    is_paid = VALUES(is_paid);

-- Verify the inserted data
SELECT id, code, name, created_at
FROM request_types
WHERE
    code IN (
        'ANNUAL',
        'SICK',
        'MATERNITY',
        'EMERGENCY',
        'PERSONAL'
    )
ORDER BY code;

-- Optional: Create a view to show leave types with their configuration details
-- This view combines database data with JSON config for easy reference
/*
CREATE VIEW v_leave_types_config AS
SELECT
rt.id,
rt.code,
rt.name,
rt.created_at,
CASE rt.code
WHEN 'ANNUAL' THEN JSON_OBJECT(
'maxDaysPerRequest', 21,
'maxDaysPerYear', 21,
'advanceNoticeDays', 7,
'canCarryOver', true,
'carryOverLimit', 5,
'requiresApproval', true
)
WHEN 'SICK' THEN JSON_OBJECT(
'maxDaysPerRequest', 30,
'maxDaysPerYear', 30,
'advanceNoticeDays', 0,
'requiresApproval', true,
'requiresMedicalCertificate', JSON_OBJECT('afterDays', 3),
'canCarryOver', false
)
WHEN 'MATERNITY' THEN JSON_OBJECT(
'maxDaysPerRequest', 180,
'maxDaysPerYear', 180,
'advanceNoticeDays', 30,
'requiresApproval', true,
'requiresDocumentation', true,
'canCarryOver', false
)
WHEN 'EMERGENCY' THEN JSON_OBJECT(
'maxDaysPerRequest', 5,
'maxDaysPerYear', 10,
'advanceNoticeDays', 0,
'requiresApproval', true,
'requiresExplanation', true,
'canCarryOver', false
)
WHEN 'PERSONAL' THEN JSON_OBJECT(
'maxDaysPerRequest', 10,
'maxDaysPerYear', 15,
'advanceNoticeDays', 3,
'requiresApproval', true,
'canCarryOver', false
)
END as config_json
FROM request_types rt
WHERE rt.code IN ('ANNUAL', 'SICK', 'MATERNITY', 'EMERGENCY', 'PERSONAL');
*/

-- Create indexes for better performance
CREATE INDEX IX_request_types_code ON request_types (code);

-- Add comments to document the leave types
ALTER TABLE request_types COMMENT = 'Contains all types of requests including leave requests. Leave types are: ANNUAL, SICK, MATERNITY, EMERGENCY, PERSONAL';

-- Show final result
SELECT 'Leave request types have been successfully inserted into request_types table' AS message;

SELECT COUNT(*) as leave_types_count
FROM request_types
WHERE
    code IN (
        'ANNUAL',
        'SICK',
        'MATERNITY',
        'EMERGENCY',
        'PERSONAL'
    );