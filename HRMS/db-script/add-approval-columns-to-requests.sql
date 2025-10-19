-- Add approve_reason column to requests table
-- Note:
-- - current_approver_account_id is reused to store who approved/rejected
-- - updated_at is used as approval/rejection timestamp
-- - approve_reason stores reason for both approval and rejection

USE hrms;

-- Check if column exists and add if it doesn't
SET @col_exists = 0;

SELECT COUNT(*) INTO @col_exists
FROM information_schema.COLUMNS
WHERE
    TABLE_SCHEMA = 'hrms'
    AND TABLE_NAME = 'requests'
    AND COLUMN_NAME = 'approve_reason';

SET
    @query = IF(
        @col_exists = 0,
        'ALTER TABLE requests ADD COLUMN approve_reason TEXT NULL COMMENT ''Reason provided when approving or rejecting the request'' AFTER current_approver_account_id',
        'SELECT ''Column approve_reason already exists'' as Status'
    );

PREPARE stmt FROM @query;

EXECUTE stmt;

DEALLOCATE PREPARE stmt;

-- Verify the changes
DESCRIBE requests;

SELECT 'Migration completed successfully!' as Status;