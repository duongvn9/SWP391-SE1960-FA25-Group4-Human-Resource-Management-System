-- ==================== REQUEST TYPES SEED DATA ====================
-- This script adds seed data for request_types table
-- Run this after creating the database schema

USE hrms;

-- Check if we need to add missing columns to request_types table
-- The schema only has: id, code, name, created_at
-- But the model expects: description, category, requires_approval, requires_attachment,
--                        max_days, approval_workflow, is_active, updated_at

-- Add missing columns if they don't exist
ALTER TABLE request_types
ADD COLUMN IF NOT EXISTS description VARCHAR(255) NULL AFTER name,
ADD COLUMN IF NOT EXISTS category VARCHAR(32) NULL AFTER description,
ADD COLUMN IF NOT EXISTS requires_approval TINYINT(1) NOT NULL DEFAULT 1 AFTER category,
ADD COLUMN IF NOT EXISTS requires_attachment TINYINT(1) NOT NULL DEFAULT 0 AFTER requires_approval,
ADD COLUMN IF NOT EXISTS max_days INT NULL AFTER requires_attachment,
ADD COLUMN IF NOT EXISTS approval_workflow VARCHAR(32) NULL DEFAULT 'SINGLE' AFTER max_days,
ADD COLUMN IF NOT EXISTS is_active TINYINT(1) NOT NULL DEFAULT 1 AFTER approval_workflow,
ADD COLUMN IF NOT EXISTS updated_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()) AFTER created_at;

-- Insert LEAVE_REQUEST type if it doesn't exist
INSERT INTO request_types (code, name, description, category, requires_approval, requires_attachment, max_days, approval_workflow, is_active, created_at, updated_at)
SELECT 'LEAVE_REQUEST', 'Leave Request', 'Employee leave request for various types of leave', 'LEAVE', 1, 0, NULL, 'SINGLE', 1, UTC_TIMESTAMP(), UTC_TIMESTAMP()
WHERE NOT EXISTS (
    SELECT 1 FROM request_types WHERE code = 'LEAVE_REQUEST'
);

-- Insert other common request types
INSERT INTO request_types (code, name, description, category, requires_approval, requires_attachment, max_days, approval_workflow, is_active, created_at, updated_at)
SELECT 'OVERTIME_REQUEST', 'Overtime Request', 'Request for overtime work', 'OVERTIME', 1, 0, NULL, 'SINGLE', 1, UTC_TIMESTAMP(), UTC_TIMESTAMP()
WHERE NOT EXISTS (
    SELECT 1 FROM request_types WHERE code = 'OVERTIME_REQUEST'
);

INSERT INTO request_types (code, name, description, category, requires_approval, requires_attachment, max_days, approval_workflow, is_active, created_at, updated_at)
SELECT 'PERSONAL_INFO_UPDATE', 'Personal Information Update', 'Request to update personal information', 'PERSONAL', 1, 1, NULL, 'SINGLE', 1, UTC_TIMESTAMP(), UTC_TIMESTAMP()
WHERE NOT EXISTS (
    SELECT 1 FROM request_types WHERE code = 'PERSONAL_INFO_UPDATE'
);

-- Verify the data
SELECT * FROM request_types;
