-- ==================== UPDATE UNPAID LEAVE LIMITS ====================
-- File: update-unpaid-leave-limits.sql
-- Purpose: Update Unpaid Leave with reasonable limits to prevent abuse
--
-- New Rules:
-- - Per request: Max 5 working days
-- - Per month: Max 13 working days
-- - Per year: Max 30 calendar days
-- - Advance notice: 3 days (reasonable and flexible)

USE hrms;

-- Update Unpaid Leave limits
UPDATE leave_types
SET
    default_days = 30,           -- Annual allocation: 30 days
    max_days = 5,                -- Per request: max 5 days
    min_advance_notice = 3,      -- Advance notice: 3 days (reasonable and flexible)
    description = 'Unpaid leave - Max 5 days per request, 13 days per month, 30 days per year. 3 days advance notice required. Salary will be deducted.',
    updated_at = UTC_TIMESTAMP()
WHERE code IN ('UNPAID', 'UNPAID_LEAVE');

-- Verify the update
SELECT
    id,
    code,
    name,
    default_days AS 'Annual Limit',
    max_days AS 'Per Request Limit',
    min_advance_notice AS 'Advance Notice (days)',
    is_paid AS 'Is Paid',
    description
FROM leave_types
WHERE code IN ('UNPAID', 'UNPAID_LEAVE');

-- ==================== NOTES ====================
--
-- Implementation:
-- 1. default_days = 30: Total allowed per year (annual allocation)
-- 2. max_days = 5: Maximum per single request
-- 3. Monthly limit (13 days): Will be validated in application code
-- 4. min_advance_notice = 3: Must request 3 days in advance (reasonable and flexible)
--
-- Validation Logic (to be implemented in LeaveRequestService):
-- - Check request days <= 5 (max_days)
-- - Check total used in current month <= 13 days
-- - Check total used in current year <= 30 days
-- - Check advance notice >= 3 days
--
-- Benefits:
-- - Prevents abuse of unpaid leave
-- - Ensures business continuity (max 5 days per request)
-- - Reasonable monthly limit (13 days = ~50% of working days)
-- - Annual cap prevents excessive absence
-- - Advance notice allows proper planning
--
-- ==================== END ====================
