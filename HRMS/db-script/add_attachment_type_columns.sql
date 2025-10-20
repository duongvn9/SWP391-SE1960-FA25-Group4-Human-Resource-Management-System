-- ============================================================
-- Script: Add attachment_type and external_url columns
-- Purpose: Enable hybrid attachment support (file upload + Google Drive links)
-- Date: 2025-10-19
-- Author: HRMS Development Team
-- ============================================================

USE hrms;

-- Modify path column to allow NULL (for external links)
ALTER TABLE attachments
MODIFY COLUMN path NVARCHAR(1024) NULL COMMENT 'File path for FILE type attachments (NULL for LINK type)';

-- Add attachment_type column (FILE or LINK)
ALTER TABLE attachments
ADD COLUMN attachment_type VARCHAR(10) DEFAULT 'FILE' COMMENT 'Type of attachment: FILE (upload) or LINK (external URL)';

-- Add external_url column for Google Drive links
ALTER TABLE attachments
ADD COLUMN external_url VARCHAR(500) NULL COMMENT 'External URL for LINK type attachments (e.g., Google Drive)';

-- Add index for faster queries by type
CREATE INDEX idx_attachment_type ON attachments(attachment_type);

-- Update existing records to have attachment_type = 'FILE'
UPDATE attachments
SET attachment_type = 'FILE'
WHERE attachment_type IS NULL;

-- Verify changes
SELECT
    COUNT(*) as total_attachments,
    SUM(CASE WHEN attachment_type = 'FILE' THEN 1 ELSE 0 END) as file_uploads,
    SUM(CASE WHEN attachment_type = 'LINK' THEN 1 ELSE 0 END) as external_links
FROM attachments;

-- Show table structure
DESCRIBE attachments;

-- ============================================================
-- Success Message
-- ============================================================
SELECT 'Attachment table updated successfully! You can now use hybrid attachments.' as message;
