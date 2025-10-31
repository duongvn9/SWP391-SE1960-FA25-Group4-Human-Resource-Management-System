-- Script để thêm các cột date cần thiết cho job_postings
-- Chạy script này để fix vấn đề hiển thị Application deadline và Start date

-- Kiểm tra và thêm cột start_date nếu chưa có
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'job_postings' 
  AND COLUMN_NAME = 'start_date';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE job_postings ADD COLUMN start_date DATE NULL AFTER contact_phone', 
    'SELECT "Column start_date already exists" as message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Kiểm tra và thêm cột application_deadline nếu chưa có
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'job_postings' 
  AND COLUMN_NAME = 'application_deadline';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE job_postings ADD COLUMN application_deadline DATE NULL AFTER start_date', 
    'SELECT "Column application_deadline already exists" as message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Kiểm tra kết quả
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'job_postings' 
  AND COLUMN_NAME IN ('start_date', 'application_deadline')
ORDER BY ORDINAL_POSITION;