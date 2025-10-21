-- Step 1: Thêm các cột mới (không bao gồm UNIQUE/FK constraints)
-- Chạy step này trước, kiểm tra data, rồi mới chạy step 2


-- 2. THÊM CÁC CỘT MỚI (Không tạo unique/FK)
ALTER TABLE `job_postings`
    ADD COLUMN `code` VARCHAR(64) NULL AFTER `title`,
    ADD COLUMN `position_id` BIGINT NULL AFTER `department_id`,
    ADD COLUMN `quantity` INT NULL AFTER `position_id`,
    ADD COLUMN `job_type` VARCHAR(32) NULL AFTER `quantity`,
    ADD COLUMN `job_level` VARCHAR(32) NULL AFTER `job_type`,


    ADD COLUMN `working_location` VARCHAR(255) NULL AFTER `description`,
    ADD COLUMN `requirements` TEXT NULL AFTER `working_location`,
    ADD COLUMN `benefits` TEXT NULL AFTER `requirements`,
    ADD COLUMN `min_experience_years` INT NULL AFTER `benefits`,


    ADD COLUMN `min_salary` DECIMAL(18, 2) NULL AFTER `min_experience_years`,
    ADD COLUMN `max_salary` DECIMAL(18, 2) NULL AFTER `min_salary`,
    ADD COLUMN `salary_type` VARCHAR(16) NULL AFTER `max_salary`,
    

    ADD COLUMN `rejected_reason` TEXT NULL AFTER `status`,
    ADD COLUMN `approved_by_account_id` BIGINT NULL AFTER `created_by_account_id`,
    ADD COLUMN `published_by_account_id` BIGINT NULL AFTER `approved_by_account_id`,
    ADD COLUMN `approved_at` DATETIME NULL AFTER `published_by_account_id`,


    ADD COLUMN `priority` VARCHAR(16) NULL AFTER `rejected_reason`,
    ADD COLUMN `working_hours` VARCHAR(128) NULL AFTER `priority`,
    ADD COLUMN `contact_email` VARCHAR(255) NULL AFTER `working_hours`,
    ADD COLUMN `contact_phone` VARCHAR(32) NULL AFTER `contact_email`,
    ADD COLUMN `start_date` DATE NULL AFTER `contact_phone`,
    ADD COLUMN `application_deadline` DATE NULL AFTER `start_date`;

-- 3. SỬA DEFAULT CHO STATUS
ALTER TABLE `job_postings`
    MODIFY COLUMN `status` VARCHAR(16) NOT NULL DEFAULT 'PENDING';

-- 4. KIỂM TRA DUPLICATES TRƯỚC KHI TẠO UNIQUE (Chạy query này sau khi add columns)
/*
SELECT code, COUNT(*) AS cnt
FROM job_postings 
WHERE code IS NOT NULL AND code <> ''
GROUP BY code
HAVING cnt > 1;
*/

-- Step 2: Thêm unique index và foreign keys
-- QUAN TRỌNG: Chỉ chạy sau khi đã chạy step 1 và kiểm tra duplicates

-- 1. TẠO UNIQUE INDEX CHO CODE (Sau khi đã xử lý duplicates nếu có)
ALTER TABLE `job_postings`
    ADD UNIQUE INDEX `ux_job_postings_code` (`code`);

-- 2. THÊM FOREIGN KEYS (Chạy từng ALTER riêng để dễ rollback)
ALTER TABLE `job_postings`
    ADD CONSTRAINT `FK_jp_position` FOREIGN KEY (`position_id`) 
        REFERENCES `positions` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `job_postings`
    ADD CONSTRAINT `FK_jp_approved_by` FOREIGN KEY (`approved_by_account_id`) 
        REFERENCES `accounts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `job_postings`
    ADD CONSTRAINT `FK_jp_published_by` FOREIGN KEY (`published_by_account_id`)
        REFERENCES `accounts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;