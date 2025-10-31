-- Script để cập nhật dữ liệu mẫu cho start_date và application_deadline
-- Chạy sau khi đã thêm các cột bằng add_date_columns.sql

-- Cập nhật các job posting hiện có với dữ liệu mẫu
UPDATE job_postings 
SET 
    start_date = DATE_ADD(CURDATE(), INTERVAL 30 DAY),
    application_deadline = DATE_ADD(CURDATE(), INTERVAL 14 DAY)
WHERE start_date IS NULL OR application_deadline IS NULL;

-- Kiểm tra kết quả
SELECT 
    id,
    title,
    start_date,
    application_deadline,
    status,
    created_at
FROM job_postings 
ORDER BY id DESC 
LIMIT 10;