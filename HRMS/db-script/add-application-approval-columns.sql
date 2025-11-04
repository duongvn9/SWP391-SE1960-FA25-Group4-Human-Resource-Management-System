-- Add HR and HRM approval columns to applications table
-- This script adds approval tracking fields for the two-stage approval process

USE hrms;


ALTER TABLE applications 
ADD COLUMN hr_approver_id BIGINT NULL COMMENT 'ID of HR who reviewed the application',
ADD COLUMN hr_approver_name VARCHAR(255) NULL COMMENT 'Name of HR approver',
ADD COLUMN hr_approval_status VARCHAR(24) NULL COMMENT 'HR approval status: approved, rejected',
ADD COLUMN hr_approval_note TEXT NULL COMMENT 'HR approval note/reason',
ADD COLUMN hr_approval_date DATETIME NULL COMMENT 'When HR made the approval decision',


ADD COLUMN hrm_approver_id BIGINT NULL COMMENT 'ID of HRM who made final decision',
ADD COLUMN hrm_approver_name VARCHAR(255) NULL COMMENT 'Name of HRM approver',
ADD COLUMN hrm_approval_status VARCHAR(24) NULL COMMENT 'HRM approval status: approved, rejected',
ADD COLUMN hrm_approval_note TEXT NULL COMMENT 'HRM approval note/reason',
ADD COLUMN hrm_approval_date DATETIME NULL COMMENT 'When HRM made the final decision';


ALTER TABLE applications
ADD CONSTRAINT FK_app_hr_approver FOREIGN KEY (hr_approver_id) REFERENCES users(id) ON DELETE SET NULL,
ADD CONSTRAINT FK_app_hrm_approver FOREIGN KEY (hrm_approver_id) REFERENCES users(id) ON DELETE SET NULL;


CREATE INDEX IX_app_hr_approval ON applications(hr_approval_status, hr_approval_date);
CREATE INDEX IX_app_hrm_approval ON applications(hrm_approval_status, hrm_approval_date);
CREATE INDEX IX_app_status_created ON applications(status, created_at);


UPDATE applications 
SET hrm_approval_status = 'approved',
    hrm_approval_date = created_at
WHERE status = 'approved';


UPDATE applications 
SET hr_approval_status = 'rejected',
    hr_approval_date = created_at  
WHERE status = 'rejected';

SELECT 'Application approval columns added successfully!' as Status;


DESCRIBE applications;