-- ==================== VERIFY REQUEST TYPES ====================
-- This script checks if LEAVE_REQUEST and other request types exist
-- Run this to verify your setup

USE hrms;

-- Check if request_types table exists
SELECT 'Checking request_types table...' AS status;

SELECT
    CASE
        WHEN COUNT(*) > 0 THEN 'OK: request_types table exists'
        ELSE 'ERROR: request_types table does not exist'
    END AS table_check
FROM information_schema.tables
WHERE table_schema = 'hrms'
AND table_name = 'request_types';

-- Check table structure
SELECT 'Checking table columns...' AS status;

SELECT
    column_name,
    column_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_schema = 'hrms'
AND table_name = 'request_types'
ORDER BY ordinal_position;

-- Check if LEAVE_REQUEST exists
SELECT 'Checking LEAVE_REQUEST entry...' AS status;

SELECT
    CASE
        WHEN COUNT(*) > 0 THEN 'OK: LEAVE_REQUEST exists'
        ELSE 'WARNING: LEAVE_REQUEST does not exist - run request_types_seed.sql'
    END AS leave_request_check
FROM request_types
WHERE code = 'LEAVE_REQUEST';

-- Show all request types
SELECT 'Current request types:' AS status;

SELECT
    id,
    code,
    name,
    category,
    is_active,
    created_at
FROM request_types
ORDER BY code;

-- Summary
SELECT 'Summary:' AS status;

SELECT
    COUNT(*) AS total_request_types,
    SUM(CASE WHEN is_active = 1 THEN 1 ELSE 0 END) AS active_types,
    SUM(CASE WHEN code = 'LEAVE_REQUEST' THEN 1 ELSE 0 END) AS has_leave_request
FROM request_types;
