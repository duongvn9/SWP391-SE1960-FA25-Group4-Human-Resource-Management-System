-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: database-1.cnkww8swwrks.ap-southeast-1.rds.amazonaws.com    Database: hrms
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */
;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */
;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */
;
/*!50503 SET NAMES utf8mb4 */
;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */
;
/*!40103 SET TIME_ZONE='+00:00' */
;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */
;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */
;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */
;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */
;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;

SET @@SESSION.SQL_LOG_BIN = 0;

--
-- GTID state at the beginning of the backup
--

SET
    @@GLOBAL.GTID_PURGED = /*!80000 '+'*/ '';

--
-- Table structure for table `account_features`
--

DROP TABLE IF EXISTS `account_features`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `account_features` (
    `account_id` bigint NOT NULL,
    `feature_id` bigint NOT NULL,
    `effect` enum('GRANT', 'DENY') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    PRIMARY KEY (`account_id`, `feature_id`),
    KEY `FK_af_feature` (`feature_id`),
    CONSTRAINT `FK_af_account` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_af_feature` FOREIGN KEY (`feature_id`) REFERENCES `features` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `account_features`
--

/*!40000 ALTER TABLE `account_features` DISABLE KEYS */
;
/*!40000 ALTER TABLE `account_features` ENABLE KEYS */
;

--
-- Table structure for table `account_roles`
--

DROP TABLE IF EXISTS `account_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `account_roles` (
    `account_id` bigint NOT NULL,
    `role_id` bigint NOT NULL,
    `assigned_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `assigned_by` bigint DEFAULT NULL,
    PRIMARY KEY (`account_id`, `role_id`),
    KEY `fk_account_roles_assigned_by` (`assigned_by`),
    KEY `idx_account_roles_account` (`account_id`),
    KEY `idx_account_roles_role` (`role_id`),
    KEY `idx_account_roles_assigned_at` (`assigned_at`),
    CONSTRAINT `fk_account_roles_account` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_account_roles_assigned_by` FOREIGN KEY (`assigned_by`) REFERENCES `accounts` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_account_roles_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Many-to-many relationship between accounts and roles for RBAC';
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `account_roles`
--

/*!40000 ALTER TABLE `account_roles` DISABLE KEYS */
;
INSERT INTO
    `account_roles`
VALUES (
        55,
        11,
        '2025-10-21 03:50:57',
        NULL
    ),
    (
        56,
        14,
        '2025-10-21 13:28:10',
        NULL
    ),
    (
        57,
        14,
        '2025-10-21 13:28:30',
        NULL
    ),
    (
        58,
        14,
        '2025-10-21 17:00:44',
        NULL
    );
/*!40000 ALTER TABLE `account_roles` ENABLE KEYS */
;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `accounts` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `username` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `email_login` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `status` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
    `failed_attempts` int NOT NULL DEFAULT '0',
    `last_login_at` datetime DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`),
    UNIQUE KEY `email_login` (`email_login`),
    KEY `IX_accounts_user` (`user_id`),
    KEY `IX_accounts_filter` (`status`, `user_id`),
    KEY `IX_accounts_status` (`status`),
    KEY `idx_accounts_user_id` (`user_id`),
    KEY `idx_accounts_username` (`username`),
    KEY `idx_accounts_user_status` (`user_id`, `status`),
    KEY `idx_accounts_status` (`status`),
    KEY `idx_accounts_created_at` (`created_at`),
    KEY `idx_accounts_created_status` (`created_at` DESC, `status`),
    KEY `idx_accounts_last_login` (`last_login_at`),
    CONSTRAINT `FK_accounts_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 83 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `accounts`
--

/*!40000 ALTER TABLE `accounts` DISABLE KEYS */
;
INSERT INTO
    `accounts`
VALUES (
        40,
        51,
        'admin',
        'vduong2709@gmail.com',
        'active',
        0,
        '2025-10-31 03:31:28',
        '2025-10-15 15:42:38',
        '2025-10-31 03:31:28'
    ),
    (
        41,
        52,
        'hieucong2468',
        'hieucong2468@gmail.com',
        'active',
        0,
        '2025-10-31 03:17:42',
        '2025-10-17 03:18:32',
        '2025-10-31 03:17:42'
    ),
    (
        42,
        53,
        'trunghieu999909',
        'trunghieu999909@gmail.com',
        'active',
        0,
        '2025-10-31 03:31:55',
        '2025-10-17 03:32:46',
        '2025-10-31 03:31:55'
    ),
    (
        44,
        55,
        'duongnguyen291105',
        'duongnguyen291105@gmail.com',
        'active',
        0,
        '2025-10-31 03:33:55',
        '2025-10-17 13:17:32',
        '2025-10-31 03:33:55'
    ),
    (
        45,
        56,
        'vancaotran',
        'vancaotran@gmail.com',
        'active',
        0,
        '2025-10-29 16:13:35',
        '2025-10-17 13:54:59',
        '2025-10-29 16:13:35'
    ),
    (
        46,
        57,
        'thanhminhnguyen',
        'thanhminhnguyen@gmail.com',
        'active',
        0,
        '2025-10-22 23:27:51',
        '2025-10-17 14:02:56',
        '2025-10-22 23:27:51'
    ),
    (
        53,
        60,
        'he0045',
        'hr@gmail.com',
        'active',
        0,
        '2025-10-29 20:42:22',
        '2025-10-21 01:52:15',
        '2025-10-29 20:42:22'
    ),
    (
        54,
        62,
        'hoang',
        'hoang@gmail.com',
        'active',
        0,
        '2025-10-22 16:50:05',
        '2025-10-21 02:14:27',
        '2025-10-22 16:50:05'
    ),
    (
        55,
        61,
        'trong',
        'trong@gmail.com',
        'active',
        0,
        NULL,
        '2025-10-21 03:50:56',
        '2025-10-21 03:50:56'
    ),
    (
        56,
        64,
        'tuanvu',
        'vu@gmail.com',
        'active',
        0,
        '2025-10-22 16:04:34',
        '2025-10-21 13:28:09',
        '2025-10-22 16:04:34'
    ),
    (
        57,
        63,
        'thanhtung',
        'tung@gmail.com',
        'active',
        0,
        '2025-10-28 16:31:56',
        '2025-10-21 13:28:29',
        '2025-10-28 16:31:56'
    ),
    (
        58,
        62,
        'hoang2',
        'hoang2@gmail.com',
        'inactive',
        0,
        NULL,
        '2025-10-21 17:00:43',
        '2025-10-21 23:17:18'
    ),
    (
        62,
        73,
        'lucduong',
        'lucduong@gmail.com',
        'active',
        0,
        '2025-10-29 16:12:01',
        '2025-10-21 23:35:05',
        '2025-10-29 16:12:01'
    ),
    (
        63,
        72,
        'tahieu',
        'tahieu@gmail.com',
        'active',
        0,
        NULL,
        '2025-10-21 23:37:01',
        '2025-10-21 23:37:01'
    ),
    (
        64,
        70,
        'he0055',
        'ha@gmail.com',
        'active',
        0,
        NULL,
        '2025-10-21 23:37:08',
        '2025-10-21 23:37:08'
    ),
    (
        65,
        68,
        'he0053',
        'tuan@gmail.com',
        'active',
        0,
        '2025-10-31 03:34:39',
        '2025-10-21 23:37:13',
        '2025-10-31 03:34:39'
    ),
    (
        66,
        66,
        'dung',
        'dung@gmail.com',
        'active',
        0,
        '2025-10-22 15:59:44',
        '2025-10-21 23:37:24',
        '2025-10-22 15:59:44'
    ),
    (
        67,
        74,
        'admin2',
        'admin2@gmail.com',
        'active',
        0,
        '2025-10-31 03:26:04',
        '2025-10-22 01:04:12',
        '2025-10-31 03:26:04'
    ),
    (
        68,
        75,
        'sahur',
        'hihihiihi@gmail.com',
        'active',
        0,
        '2025-10-22 16:24:48',
        '2025-10-22 16:15:47',
        '2025-10-22 16:24:48'
    ),
    (
        69,
        76,
        'sahur2',
        'konami98@gmail.com',
        'active',
        0,
        '2025-10-22 17:08:00',
        '2025-10-22 16:23:23',
        '2025-10-22 17:08:00'
    ),
    (
        70,
        77,
        'sahursahur',
        'konami97@gmail.com',
        'active',
        0,
        '2025-10-29 16:12:32',
        '2025-10-22 17:10:23',
        '2025-10-29 16:12:32'
    ),
    (
        71,
        80,
        'he0065',
        'manh@gmail.com',
        'active',
        0,
        '2025-10-31 03:31:46',
        '2025-10-23 01:32:10',
        '2025-10-31 03:31:46'
    ),
    (
        72,
        71,
        'trang',
        'trang@gmail.com',
        'active',
        0,
        '2025-10-28 14:17:03',
        '2025-10-23 01:38:52',
        '2025-10-28 14:17:03'
    ),
    (
        73,
        67,
        'tai',
        'tai@gmail.com',
        'active',
        0,
        '2025-10-31 00:44:46',
        '2025-10-23 01:39:09',
        '2025-10-31 00:44:46'
    ),
    (
        74,
        69,
        'ngan',
        'ngan@gmail.com',
        'active',
        0,
        NULL,
        '2025-10-23 01:39:26',
        '2025-10-23 01:39:26'
    ),
    (
        75,
        65,
        'ngochan',
        'ngochan@gmail.com',
        'active',
        0,
        '2025-10-28 17:24:00',
        '2025-10-23 01:42:45',
        '2025-10-28 17:24:00'
    ),
    (
        76,
        79,
        'ducanh',
        'ducanh@gmail.com',
        'inactive',
        0,
        NULL,
        '2025-10-23 01:43:03',
        '2025-10-23 23:15:32'
    ),
    (
        77,
        78,
        'lam',
        'lam@gmail.com',
        'active',
        0,
        '2025-10-28 14:16:16',
        '2025-10-23 01:43:12',
        '2025-10-28 14:16:16'
    ),
    (
        78,
        81,
        'khanh',
        'khanh@gmail.com',
        'active',
        0,
        '2025-10-31 03:18:33',
        '2025-10-23 23:27:10',
        '2025-10-31 03:18:33'
    ),
    (
        81,
        84,
        'nhi',
        'nhi@gmail.com',
        'active',
        0,
        '2025-10-31 00:48:23',
        '2025-10-30 23:13:49',
        '2025-10-31 00:48:23'
    ),
    (
        82,
        86,
        'admin3',
        'admin3@gmail.com',
        'active',
        0,
        '2025-10-31 03:10:18',
        '2025-10-31 03:10:09',
        '2025-10-31 03:10:18'
    );
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */
;

--
-- Table structure for table `applications`
--

DROP TABLE IF EXISTS `applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `applications` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `job_id` bigint NOT NULL,
    `status` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'new',
    `note` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `dob` date DEFAULT NULL,
    `gender` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `hometown` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `address_line1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `address_line2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `city` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `state` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `postal_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `country` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `resume_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `cccd` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `cccd_issued_date` date DEFAULT NULL,
    `cccd_issued_place` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `cccd_front_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `cccd_back_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    KEY `IX_app_job_email` (`job_id`, `email`),
    KEY `IX_app_job_cccd` (`job_id`, `cccd`),
    CONSTRAINT `FK_app_job` FOREIGN KEY (`job_id`) REFERENCES `job_postings` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `applications`
--

/*!40000 ALTER TABLE `applications` DISABLE KEYS */
;
/*!40000 ALTER TABLE `applications` ENABLE KEYS */
;

--
-- Table structure for table `attachments`
--

DROP TABLE IF EXISTS `attachments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `attachments` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `owner_type` varchar(64) NOT NULL,
    `owner_id` bigint NOT NULL,
    `path` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
    `original_name` varchar(255) NOT NULL,
    `content_type` varchar(128) DEFAULT NULL,
    `size_bytes` bigint DEFAULT NULL,
    `checksum_sha256` char(64) DEFAULT NULL,
    `uploaded_by_account_id` bigint DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `attachment_type` varchar(10) DEFAULT 'FILE',
    `external_url` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `IX_attach_owner` (`owner_type`, `owner_id`),
    KEY `FK_attach_uploader` (`uploaded_by_account_id`),
    CONSTRAINT `FK_attach_uploader` FOREIGN KEY (`uploaded_by_account_id`) REFERENCES `accounts` (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 69 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `attachments`
--

/*!40000 ALTER TABLE `attachments` DISABLE KEYS */
;
INSERT INTO
    `attachments`
VALUES (
        4,
        'REQUEST',
        37,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        41,
        '2025-10-19 22:47:36',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        5,
        'REQUEST',
        38,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        46,
        '2025-10-19 22:52:01',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        6,
        'REQUEST',
        39,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        41,
        '2025-10-19 23:26:18',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        7,
        'REQUEST',
        40,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        41,
        '2025-10-19 23:31:30',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        8,
        'REQUEST',
        42,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        41,
        '2025-10-20 00:39:58',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        9,
        'REQUEST',
        43,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        41,
        '2025-10-20 00:45:52',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        10,
        'REQUEST',
        44,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        41,
        '2025-10-20 00:46:58',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        11,
        'REQUEST',
        53,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        46,
        '2025-10-20 22:05:06',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        13,
        'REQUEST',
        67,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        40,
        '2025-10-21 22:36:45',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        14,
        'REQUEST',
        68,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        44,
        '2025-10-22 10:46:22',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        16,
        'REQUEST',
        72,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        42,
        '2025-10-22 22:51:26',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        17,
        'REQUEST',
        73,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        42,
        '2025-10-22 22:53:01',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        18,
        'REQUEST',
        75,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        42,
        '2025-10-22 23:08:06',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        19,
        'REQUEST',
        76,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        42,
        '2025-10-22 23:11:40',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        20,
        'REQUEST',
        77,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        46,
        '2025-10-22 23:32:59',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        21,
        'REQUEST',
        78,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-24 01:02:13',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        23,
        'REQUEST',
        85,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        78,
        '2025-10-24 03:12:00',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        26,
        'REQUEST',
        95,
        '2025/10/3686533a-10e0-4e66-bf03-d5a29a158dc8.jpg',
        'defaultAvatar.jpg',
        'image/jpeg',
        7079,
        'd720744f5c7a645bb107b666285dafcdf6d4fe263984ea82433b8f1d5789bf99',
        78,
        '2025-10-25 00:22:05',
        'FILE',
        NULL
    ),
    (
        27,
        'REQUEST',
        104,
        '2025/10/02eb5276-31ab-4b11-b45e-13ac2829a828.jpg',
        'defaultAvatar.jpg',
        'image/jpeg',
        7079,
        'd720744f5c7a645bb107b666285dafcdf6d4fe263984ea82433b8f1d5789bf99',
        41,
        '2025-10-28 14:40:11',
        'FILE',
        NULL
    ),
    (
        28,
        'REQUEST',
        105,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        41,
        '2025-10-28 14:41:34',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        29,
        'REQUEST',
        106,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        57,
        '2025-10-28 14:43:00',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        30,
        'REQUEST',
        107,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        57,
        '2025-10-28 14:43:48',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        31,
        'REQUEST',
        108,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        57,
        '2025-10-28 14:57:59',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        32,
        'REQUEST',
        109,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        57,
        '2025-10-28 14:59:32',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        33,
        'REQUEST',
        110,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        57,
        '2025-10-28 15:07:01',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        34,
        'REQUEST',
        115,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        75,
        '2025-10-28 16:37:18',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        35,
        'REQUEST',
        120,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        41,
        '2025-10-28 17:23:38',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        36,
        'REQUEST',
        122,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-28 23:02:59',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        37,
        'REQUEST',
        123,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-28 23:07:04',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        38,
        'REQUEST',
        124,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-28 23:09:14',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        39,
        'REQUEST',
        125,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-28 23:11:39',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        40,
        'REQUEST',
        126,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-28 23:17:05',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        41,
        'REQUEST',
        127,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-28 23:23:05',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        42,
        'REQUEST',
        128,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-28 23:23:42',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        43,
        'REQUEST',
        129,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-28 23:28:05',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        44,
        'REQUEST',
        130,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-28 23:31:05',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        45,
        'REQUEST',
        131,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-28 23:33:02',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        46,
        'REQUEST',
        132,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-28 23:37:22',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        47,
        'REQUEST',
        133,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-28 23:38:26',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        48,
        'REQUEST',
        134,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-28 23:43:01',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        49,
        'REQUEST',
        135,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-28 23:45:11',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        50,
        'REQUEST',
        136,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-28 23:50:05',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        51,
        'REQUEST',
        137,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-28 23:51:32',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        52,
        'REQUEST',
        138,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-28 23:52:17',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        53,
        'REQUEST',
        139,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        41,
        '2025-10-29 10:37:21',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        54,
        'REQUEST',
        141,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        70,
        '2025-10-29 15:18:15',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        55,
        'REQUEST',
        144,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        70,
        '2025-10-29 15:23:39',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        56,
        'REQUEST',
        148,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        53,
        '2025-10-29 20:46:55',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        57,
        'REQUEST',
        149,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        53,
        '2025-10-29 21:12:27',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        58,
        'REQUEST',
        152,
        '2025/10/56ab6c8b-2ab5-43dd-9324-56545d43b11f.jpg',
        'defaultAvatar.jpg',
        'image/jpeg',
        7079,
        'd720744f5c7a645bb107b666285dafcdf6d4fe263984ea82433b8f1d5789bf99',
        71,
        '2025-10-30 01:24:40',
        'FILE',
        NULL
    ),
    (
        59,
        'REQUEST',
        157,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        78,
        '2025-10-30 02:02:39',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        60,
        'REQUEST',
        158,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        78,
        '2025-10-30 02:30:35',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        61,
        'REQUEST',
        161,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        78,
        '2025-10-30 16:43:44',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        62,
        'REQUEST',
        166,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        78,
        '2025-10-30 21:22:51',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        63,
        'REQUEST',
        169,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        65,
        '2025-10-31 00:36:42',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        64,
        'REQUEST',
        172,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        65,
        '2025-10-31 00:54:15',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        65,
        'REQUEST',
        180,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        78,
        '2025-10-31 02:17:23',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        66,
        'REQUEST',
        181,
        '',
        'Google drive link',
        'External/link',
        0,
        NULL,
        78,
        '2025-10-31 02:30:14',
        'LINK',
        'https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0'
    ),
    (
        67,
        'REQUEST',
        183,
        '',
        'Google Drive Link',
        'external/link',
        0,
        NULL,
        42,
        '2025-10-31 03:20:05',
        'LINK',
        'https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'
    ),
    (
        68,
        'REQUEST',
        184,
        '2025/10/4dba115e-007f-4ed6-b694-cbf02dc65d39.jpg',
        'defaultAvatar.jpg',
        'image/jpeg',
        7079,
        'd720744f5c7a645bb107b666285dafcdf6d4fe263984ea82433b8f1d5789bf99',
        42,
        '2025-10-31 03:20:59',
        'FILE',
        NULL
    );
/*!40000 ALTER TABLE `attachments` ENABLE KEYS */
;

--
-- Table structure for table `attendance_logs`
--

DROP TABLE IF EXISTS `attendance_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `attendance_logs` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `check_type` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `checked_at` datetime NOT NULL,
    `source` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `period_id` bigint DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    KEY `IX_att_user_time` (`user_id`, `checked_at`),
    KEY `FK_att_period` (`period_id`),
    KEY `idx_attendance_user_type_date` (
        `user_id`,
        `check_type`,
        `checked_at`
    ),
    CONSTRAINT `FK_att_period` FOREIGN KEY (`period_id`) REFERENCES `timesheet_periods` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_att_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 2011 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `attendance_logs`
--

/*!40000 ALTER TABLE `attendance_logs` DISABLE KEYS */
;
INSERT INTO
    `attendance_logs`
VALUES (
        1983,
        51,
        'IN',
        '2025-10-22 08:03:00',
        'Excel',
        'On Time',
        12,
        '2025-10-30 20:02:49'
    ),
    (
        1984,
        51,
        'OUT',
        '2025-10-22 17:12:00',
        'Excel',
        'On Time',
        12,
        '2025-10-30 20:02:50'
    ),
    (
        1985,
        51,
        'IN',
        '2025-10-23 08:00:00',
        'Excel',
        'On Time',
        12,
        '2025-10-30 20:02:50'
    ),
    (
        1986,
        51,
        'OUT',
        '2025-10-23 17:16:00',
        'Excel',
        'On Time',
        12,
        '2025-10-30 20:02:50'
    ),
    (
        1987,
        51,
        'IN',
        '2025-10-12 08:04:00',
        'Excel',
        'On Time',
        12,
        '2025-10-30 20:02:50'
    ),
    (
        1988,
        51,
        'OUT',
        '2025-10-12 17:18:00',
        'Excel',
        'On Time',
        12,
        '2025-10-30 20:02:50'
    ),
    (
        1989,
        53,
        'IN',
        '2025-10-12 08:02:00',
        'Excel',
        'On Time',
        12,
        '2025-10-30 20:02:50'
    ),
    (
        1990,
        53,
        'OUT',
        '2025-10-12 17:00:00',
        'Excel',
        'On Time',
        12,
        '2025-10-30 20:02:50'
    ),
    (
        1991,
        51,
        'IN',
        '2025-10-02 09:07:00',
        'Excel',
        'Late',
        12,
        '2025-10-30 20:02:50'
    ),
    (
        1992,
        51,
        'OUT',
        '2025-10-02 17:18:00',
        'Excel',
        'Late',
        12,
        '2025-10-30 20:02:50'
    ),
    (
        1993,
        52,
        'IN',
        '2025-10-08 08:05:00',
        'Excel',
        'On Time',
        12,
        '2025-10-30 20:02:50'
    ),
    (
        1994,
        52,
        'OUT',
        '2025-10-08 17:13:00',
        'Excel',
        'On Time',
        12,
        '2025-10-30 20:02:50'
    ),
    (
        1995,
        53,
        'IN',
        '2025-10-09 08:02:00',
        'Excel',
        'On Time',
        12,
        '2025-10-30 20:02:50'
    ),
    (
        1996,
        53,
        'OUT',
        '2025-10-09 17:14:00',
        'Excel',
        'On Time',
        12,
        '2025-10-30 20:02:50'
    ),
    (
        1997,
        51,
        'IN',
        '2025-10-25 09:17:00',
        'Excel',
        'Late',
        12,
        '2025-10-30 20:02:50'
    ),
    (
        1998,
        51,
        'OUT',
        '2025-10-25 17:09:00',
        'Excel',
        'Late',
        12,
        '2025-10-30 20:02:50'
    ),
    (
        1999,
        52,
        'IN',
        '2025-10-26 08:03:00',
        'Excel',
        'Late',
        12,
        '2025-10-30 20:02:51'
    ),
    (
        2000,
        52,
        'OUT',
        '2025-10-26 17:19:00',
        'Excel',
        'Late',
        12,
        '2025-10-30 20:02:51'
    ),
    (
        2001,
        52,
        'IN',
        '2025-10-04 08:17:00',
        'Excel',
        'Late',
        12,
        '2025-10-30 20:02:51'
    ),
    (
        2002,
        52,
        'OUT',
        '2025-10-04 17:03:00',
        'Excel',
        'Late',
        12,
        '2025-10-30 20:02:51'
    ),
    (
        2003,
        52,
        'IN',
        '2025-10-06 08:05:00',
        'Excel',
        'On Time',
        12,
        '2025-10-30 20:02:51'
    ),
    (
        2004,
        52,
        'OUT',
        '2025-10-06 17:04:00',
        'Excel',
        'On Time',
        12,
        '2025-10-30 20:02:51'
    ),
    (
        2005,
        81,
        'IN',
        '2025-10-30 07:42:00',
        'manual',
        'On Time',
        12,
        '2025-10-30 20:08:03'
    ),
    (
        2006,
        81,
        'OUT',
        '2025-10-30 17:30:00',
        'manual',
        'On Time',
        12,
        '2025-10-30 20:08:03'
    ),
    (
        2007,
        81,
        'IN',
        '2025-10-30 19:18:00',
        'manual',
        'Over Time',
        12,
        '2025-10-30 20:18:20'
    ),
    (
        2008,
        81,
        'OUT',
        '2025-10-30 22:18:00',
        'manual',
        'Over Time',
        12,
        '2025-10-30 20:18:20'
    ),
    (
        2009,
        80,
        'IN',
        '2025-10-30 07:32:00',
        'manual',
        'On Time',
        12,
        '2025-10-30 20:32:20'
    ),
    (
        2010,
        80,
        'OUT',
        '2025-10-30 18:32:00',
        'manual',
        'On Time',
        12,
        '2025-10-30 20:32:20'
    );
/*!40000 ALTER TABLE `attendance_logs` ENABLE KEYS */
;

--
-- Table structure for table `audit_events`
--

DROP TABLE IF EXISTS `audit_events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `audit_events` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `account_id` bigint DEFAULT NULL,
    `event_type` varchar(64) NOT NULL,
    `entity_type` varchar(64) DEFAULT NULL,
    `entity_id` bigint DEFAULT NULL,
    `ip` varchar(64) DEFAULT NULL,
    `user_agent` varchar(255) DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    KEY `IX_audit_entity` (
        `entity_type`,
        `entity_id`,
        `created_at`
    ),
    KEY `FK_audit_account` (`account_id`),
    CONSTRAINT `FK_audit_account` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `audit_events`
--

/*!40000 ALTER TABLE `audit_events` DISABLE KEYS */
;
/*!40000 ALTER TABLE `audit_events` ENABLE KEYS */
;

--
-- Table structure for table `auth_identities`
--

DROP TABLE IF EXISTS `auth_identities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `auth_identities` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `account_id` bigint NOT NULL,
    `provider` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `provider_user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `email_verified` tinyint(1) NOT NULL DEFAULT '0',
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    UNIQUE KEY `UQ_auth_provider_subject` (
        `provider`,
        `provider_user_id`
    ),
    KEY `IX_auth_account` (`account_id`),
    CONSTRAINT `FK_identity_account` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 74 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `auth_identities`
--

/*!40000 ALTER TABLE `auth_identities` DISABLE KEYS */
;
INSERT INTO
    `auth_identities`
VALUES (
        38,
        40,
        'local',
        '40',
        'vduong2709@gmail.com',
        1,
        '2025-10-15 15:42:38'
    ),
    (
        39,
        40,
        'google',
        '118175320454479209810',
        'vduong2709@gmail.com',
        1,
        '2025-10-15 22:49:07'
    ),
    (
        40,
        41,
        'local',
        'hieucong2468',
        'hieucong2468@gmail.com',
        1,
        '2025-10-17 03:18:32'
    ),
    (
        41,
        42,
        'local',
        'trunghieu999909',
        'trunghieu999909@gmail.com',
        1,
        '2025-10-17 03:32:46'
    ),
    (
        43,
        41,
        'google',
        '116351005802456025666',
        'hieucong2468@gmail.com',
        1,
        '2025-10-17 12:51:47'
    ),
    (
        44,
        44,
        'local',
        'duongnguyen291105',
        'duongnguyen291105@gmail.com',
        1,
        '2025-10-17 13:17:33'
    ),
    (
        45,
        45,
        'local',
        'vancaotran',
        'vancaotran@gmail.com',
        1,
        '2025-10-17 13:55:00'
    ),
    (
        46,
        46,
        'local',
        'thanhminhnguyen',
        'thanhminhnguyen@gmail.com',
        1,
        '2025-10-17 14:02:56'
    ),
    (
        47,
        44,
        'google',
        '102386974965261966542',
        'duongnguyen291105@gmail.com',
        1,
        '2025-10-18 13:55:26'
    ),
    (
        48,
        53,
        'local',
        'he0045',
        'hr@gmail.com',
        0,
        '2025-10-21 01:52:15'
    ),
    (
        49,
        54,
        'local',
        'hoang',
        'hoang@gmail.com',
        0,
        '2025-10-21 02:14:27'
    ),
    (
        50,
        55,
        'local',
        'trong',
        'trong@gmail.com',
        0,
        '2025-10-21 03:50:56'
    ),
    (
        51,
        56,
        'local',
        'tuanvu',
        'vu@gmail.com',
        0,
        '2025-10-21 13:28:09'
    ),
    (
        52,
        57,
        'local',
        'thanhtung',
        'tung@gmail.com',
        0,
        '2025-10-21 13:28:29'
    ),
    (
        53,
        58,
        'local',
        'hoang2',
        'hoang2@gmail.com',
        0,
        '2025-10-21 17:00:43'
    ),
    (
        54,
        62,
        'local',
        'lucduong',
        'lucduong@gmail.com',
        0,
        '2025-10-21 23:35:05'
    ),
    (
        55,
        63,
        'local',
        'tahieu',
        'tahieu@gmail.com',
        0,
        '2025-10-21 23:37:01'
    ),
    (
        56,
        64,
        'local',
        'he0055',
        'ha@gmail.com',
        0,
        '2025-10-21 23:37:08'
    ),
    (
        57,
        65,
        'local',
        'he0053',
        'tuan@gmail.com',
        0,
        '2025-10-21 23:37:13'
    ),
    (
        58,
        66,
        'local',
        'dung',
        'dung@gmail.com',
        0,
        '2025-10-21 23:37:24'
    ),
    (
        59,
        67,
        'local',
        'he0059',
        'admin2@gmail.com',
        0,
        '2025-10-22 01:04:12'
    ),
    (
        60,
        68,
        'local',
        'sahur',
        'hihihiihi@gmail.com',
        0,
        '2025-10-22 16:15:47'
    ),
    (
        61,
        69,
        'local',
        'sahur2',
        'konami98@gmail.com',
        0,
        '2025-10-22 16:23:23'
    ),
    (
        62,
        70,
        'local',
        'sahursahur',
        'konami97@gmail.com',
        0,
        '2025-10-22 17:10:23'
    ),
    (
        63,
        71,
        'local',
        'he0065',
        'manh@gmail.com',
        0,
        '2025-10-23 01:32:10'
    ),
    (
        64,
        72,
        'local',
        'trang',
        'trang@gmail.com',
        0,
        '2025-10-23 01:38:52'
    ),
    (
        65,
        73,
        'local',
        'tai',
        'tai@gmail.com',
        0,
        '2025-10-23 01:39:09'
    ),
    (
        66,
        74,
        'local',
        'ngan',
        'ngan@gmail.com',
        0,
        '2025-10-23 01:39:26'
    ),
    (
        67,
        75,
        'local',
        'ngochan',
        'ngoc@gmail.com',
        0,
        '2025-10-23 01:42:45'
    ),
    (
        68,
        76,
        'local',
        'ducanh',
        'ducanh@gmail.com',
        0,
        '2025-10-23 01:43:03'
    ),
    (
        69,
        77,
        'local',
        'lam',
        'lam@gmail.com',
        0,
        '2025-10-23 01:43:12'
    ),
    (
        70,
        78,
        'local',
        'khanh',
        'khanh@gmail.com',
        0,
        '2025-10-23 23:27:10'
    ),
    (
        72,
        81,
        'local',
        'nhi',
        'nhi@gmail.com',
        0,
        '2025-10-30 23:13:49'
    ),
    (
        73,
        82,
        'local',
        'admin3',
        'admin3@gmail.com',
        0,
        '2025-10-31 03:10:09'
    );
/*!40000 ALTER TABLE `auth_identities` ENABLE KEYS */
;

--
-- Table structure for table `auth_local_credentials`
--

DROP TABLE IF EXISTS `auth_local_credentials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `auth_local_credentials` (
    `identity_id` bigint NOT NULL,
    `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `password_updated_at` datetime DEFAULT NULL,
    PRIMARY KEY (`identity_id`),
    CONSTRAINT `FK_local_identity` FOREIGN KEY (`identity_id`) REFERENCES `auth_identities` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `auth_local_credentials`
--

/*!40000 ALTER TABLE `auth_local_credentials` DISABLE KEYS */
;
INSERT INTO
    `auth_local_credentials`
VALUES (
        38,
        '$2a$12$l9T0.7AEjH1azFMofNJUt./kXGI8DBFRbC2AGcSNuRG4Z34M9o70G',
        '2025-10-31 00:04:35'
    ),
    (
        40,
        '$2a$12$sG9RY58k.u6p3sRVJLugO.v4hsq5x642TFq4bVHzGcw/Hxy.yWfFy',
        '2025-10-23 01:42:06'
    ),
    (
        41,
        '$2a$12$ADlTfg4tfjHobgQKmQSKG.FmPxSSX7eXiRk7qutmOlsBhRVtfceGq',
        '2025-10-23 01:41:53'
    ),
    (
        44,
        '$2a$12$ubI/yF/FQcd6G0V1b2iZ3OY.ZzTqrJCvhNbHBAZyiRCqkr7AXBYE2',
        '2025-10-31 02:43:58'
    ),
    (
        45,
        '$2a$12$H/DskbxRk1sQwc3HO0bfX.siVnCT/O5CN6r4CknFjFKLJ.NXH1fIy',
        '2025-10-23 01:41:33'
    ),
    (
        46,
        '$2a$12$8ZE4zRlT/kJ8waCMad4Ahu0lY7hHtURzQGEZoCfYDpP1MLAnBiDYa',
        '2025-10-23 01:41:13'
    ),
    (
        48,
        '$2a$12$QURsAs8CRieqkjz.dBjMM.5k2n7R5V.24pZMib/txfN8sXKv2CwE.',
        '2025-10-23 01:41:22'
    ),
    (
        49,
        '$2a$12$bAueBPjURzesr2aMg3t.ruMvII04Rte8JZzWCVc9tpC6nDbr3LY/C',
        '2025-10-23 01:40:25'
    ),
    (
        50,
        '$2a$12$3o0gN.pru0g1f5DVxyvOX.WHAj84HPIzwoH3d.hOiVEmOyPVKygY.',
        '2025-10-23 01:40:19'
    ),
    (
        51,
        '$2a$12$s6SoifutaHZrmO.ZSexLy.MoiQnaaMKPWm2oc9UviWoBQzskU8rO.',
        '2025-10-23 01:40:11'
    ),
    (
        52,
        '$2a$12$9.U/LGuk0VMSI8RmG5YSd.ZaL2zwuDZ7kVcyoDTXdLQH1UMSH/6Sm',
        '2025-10-23 01:40:03'
    ),
    (
        53,
        '$2a$12$sihQgSPWdDRIF8a0KMD.iu0QhxATgRFXQKqcNHzDlBb6subORjm9O',
        '2025-10-23 01:38:03'
    ),
    (
        54,
        '$2a$12$QyFaHOcDxCkAbGSs8VqAreY8GjJAS7MWxhoFq4J6eiENyGbnGPd9e',
        '2025-10-23 01:35:00'
    ),
    (
        55,
        '$2a$12$q2UKYqbo1ofFRbXwhILHieDHw2xA0dtLQT1UC6jo.bUAw7qEERL3.',
        '2025-10-23 01:34:52'
    ),
    (
        56,
        '$2a$12$OVef0WmGjiwj8LNXFTqz3ekRNtZXu2o9D6PkSOAPDupfcYNhb8cRq',
        '2025-10-23 01:34:44'
    ),
    (
        57,
        '$2a$12$nz4AjDNq1wI3R7vcAYM1G.81CeXVnK9DE4CNSwKQ5R.Z/WjBUB1Sm',
        '2025-10-23 01:34:24'
    ),
    (
        58,
        '$2a$12$QrU41eoTDlPpENQwuaGL4eGEnX0tXR6cxEe1dCQgggMV./ED8ow/W',
        '2025-10-23 01:34:15'
    ),
    (
        59,
        '$2a$12$U.PXjhuVs/5V1T/K4vIiWu2oiGdl1BiJKsqLF9PmrarCNR0kMO6Si',
        '2025-10-23 01:33:11'
    ),
    (
        60,
        '$2a$12$I8cyIhLxpTN8kxqDiYAOb.qCKtSTmnazKlS41uajAjh/Sz5PB8K2C',
        '2025-10-23 01:32:59'
    ),
    (
        61,
        '$2a$12$.Gdl/diwjHmDParETClcveDbS2tCJ78lRU9lOrfMNpEGAmjduzaGu',
        '2025-10-23 01:32:51'
    ),
    (
        62,
        '$2a$12$kbcprt9v3f6clxyZGHiatuwfBMA6tkqOuwoCkzjbc8dh022OyP5Aq',
        '2025-10-23 01:32:33'
    ),
    (
        63,
        '$2a$12$3EnVUJ/D8231gQ9BbvfH6.f1IP4vFxSKSWpHrxqBDJ4JP4mU60iX.',
        '2025-10-23 01:32:10'
    ),
    (
        64,
        '$2a$12$cYCzvjrHJy5qrJFV5rmIb.K85eSKithWOwoVuT1r7p2PMC1V4AwqC',
        '2025-10-23 01:38:52'
    ),
    (
        65,
        '$2a$12$MihqBSryMkReWLvKl0ObceElErbI7nTiwQK/XouD6C/Y5EclwtzrS',
        '2025-10-23 01:39:09'
    ),
    (
        66,
        '$2a$12$KiZoD0danqZgUCrUw7ANgOyWvOMELEFMCOzHcr3f4O1RN7TjGx3UO',
        '2025-10-23 01:39:26'
    ),
    (
        67,
        '$2a$12$DMuOWk3ziCRe1fr69Tg5xe.6bCXg3CkZaIt8W4UKyF0rQjVmj9duy',
        '2025-10-23 01:42:45'
    ),
    (
        68,
        '$2a$12$14MjHy54mnRLGS/EtlUkhumA3mnKT20vKLUnAf6Xw/OlqicixWZ..',
        '2025-10-23 01:43:03'
    ),
    (
        69,
        '$2a$12$bsZPjtGEBAte6tekc1cLIe2lqHDT2U7hHi2aVYFIlPtfuMvlaApSK',
        '2025-10-23 23:24:06'
    ),
    (
        70,
        '$2a$12$UqhwF6vl8bx4GOjCiVSqBuqeGmeuN9tArCLklTeQ.WE646Qo7w/NC',
        '2025-10-24 04:38:52'
    ),
    (
        72,
        '$2a$12$g56fhkFmZHTuZakXCt0nr.iMF7FmgHWLQDAl/0maAT5AcQbdYpgni',
        '2025-10-30 23:30:40'
    ),
    (
        73,
        '$2a$12$kgTur02ii4wj5pT4FxreEuXPiCRcFTizoqFfRQDL2TDaUTUEOEcmm',
        '2025-10-31 03:10:09'
    );
/*!40000 ALTER TABLE `auth_local_credentials` ENABLE KEYS */
;

--
-- Table structure for table `department_features`
--

DROP TABLE IF EXISTS `department_features`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `department_features` (
    `department_id` bigint NOT NULL,
    `feature_id` bigint NOT NULL,
    `effect` enum('GRANT', 'DENY') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    PRIMARY KEY (`department_id`, `feature_id`),
    KEY `FK_df_feature` (`feature_id`),
    CONSTRAINT `FK_df_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_df_feature` FOREIGN KEY (`feature_id`) REFERENCES `features` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `department_features`
--

/*!40000 ALTER TABLE `department_features` DISABLE KEYS */
;
/*!40000 ALTER TABLE `department_features` ENABLE KEYS */
;

--
-- Table structure for table `departments`
--

DROP TABLE IF EXISTS `departments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `departments` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `head_account_id` bigint DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_dept_head` (`head_account_id`),
    KEY `idx_departments_name` (`name`),
    CONSTRAINT `FK_dept_head` FOREIGN KEY (`head_account_id`) REFERENCES `accounts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 14 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `departments`
--

/*!40000 ALTER TABLE `departments` DISABLE KEYS */
;
INSERT INTO
    `departments`
VALUES (
        1,
        'Human Resource',
        NULL,
        '2025-10-16 09:33:20',
        '2025-10-30 23:50:45',
        NULL
    ),
    (
        2,
        'Corporate Relations',
        NULL,
        '2025-10-16 09:33:20',
        '2025-10-31 03:03:04',
        NULL
    ),
    (
        3,
        'Sales',
        NULL,
        '2025-10-16 09:33:20',
        '2025-10-16 09:33:20',
        NULL
    ),
    (
        4,
        'Marketing',
        NULL,
        '2025-10-16 09:33:20',
        '2025-10-16 09:33:20',
        NULL
    ),
    (
        5,
        'Finance',
        NULL,
        '2025-10-16 09:33:20',
        '2025-10-30 23:46:24',
        NULL
    ),
    (
        6,
        'QA',
        NULL,
        '2025-10-18 13:01:17',
        '2025-10-18 13:01:17',
        NULL
    ),
    (
        9,
        'IT Support',
        NULL,
        '2025-10-21 14:05:29',
        '2025-10-30 22:51:32',
        NULL
    );
/*!40000 ALTER TABLE `departments` ENABLE KEYS */
;

--
-- Table structure for table `employment_contracts`
--

DROP TABLE IF EXISTS `employment_contracts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `employment_contracts` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `contract_no` varchar(50) DEFAULT NULL,
    `contract_type` varchar(32) DEFAULT NULL,
    `start_date` date NOT NULL,
    `end_date` date DEFAULT NULL,
    `base_salary` decimal(18, 2) NOT NULL,
    `currency` varchar(8) DEFAULT NULL,
    `status` varchar(24) NOT NULL DEFAULT 'active',
    `file_path` varchar(1024) DEFAULT NULL,
    `note` varchar(255) DEFAULT NULL,
    `created_by_account_id` bigint DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    KEY `IX_contract_user_start` (`user_id`, `start_date`),
    KEY `FK_contract_creator` (`created_by_account_id`),
    CONSTRAINT `FK_contract_creator` FOREIGN KEY (`created_by_account_id`) REFERENCES `accounts` (`id`),
    CONSTRAINT `FK_contract_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 6 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `employment_contracts`
--

/*!40000 ALTER TABLE `employment_contracts` DISABLE KEYS */
;
INSERT INTO
    `employment_contracts`
VALUES (
        5,
        51,
        'CONTRACT-2024-002',
        'fixed_term',
        '2024-01-15',
        '2025-12-31',
        30000000.00,
        'VND',
        'active',
        'contract.pdf',
        'Two-year fixed-term contract for Senior Administrator',
        NULL,
        '2025-10-16 09:08:02',
        '2025-10-31 01:57:05'
    );
/*!40000 ALTER TABLE `employment_contracts` ENABLE KEYS */
;

--
-- Table structure for table `features`
--

DROP TABLE IF EXISTS `features`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `features` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `route` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `sort_order` int NOT NULL DEFAULT '0',
    `is_active` tinyint(1) NOT NULL DEFAULT '1',
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    UNIQUE KEY `code` (`code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `features`
--

/*!40000 ALTER TABLE `features` DISABLE KEYS */
;
/*!40000 ALTER TABLE `features` ENABLE KEYS */
;

--
-- Table structure for table `holiday_calendar`
--

DROP TABLE IF EXISTS `holiday_calendar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `holiday_calendar` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `year` int NOT NULL,
    `name` varchar(64) NOT NULL,
    `tet_duration` int NOT NULL DEFAULT '7',
    `auto_compensatory` tinyint(1) NOT NULL DEFAULT '1',
    `is_generated` tinyint(1) NOT NULL DEFAULT '0',
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()) ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UQ_hcal` (`year`, `name`)
) ENGINE = InnoDB AUTO_INCREMENT = 8 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `holiday_calendar`
--

/*!40000 ALTER TABLE `holiday_calendar` DISABLE KEYS */
;
INSERT INTO
    `holiday_calendar`
VALUES (
        2,
        2025,
        'Vietnam Public Holidays 2025',
        7,
        1,
        1,
        '2025-10-15 22:48:18',
        '2025-10-16 12:39:25'
    ),
    (
        3,
        2026,
        'Vietnam Public Holidays 2026',
        7,
        1,
        1,
        '2025-10-15 22:48:19',
        '2025-10-16 12:39:53'
    ),
    (
        4,
        2027,
        'Vietnam Public Holidays 2027',
        7,
        1,
        1,
        '2025-10-15 22:48:19',
        '2025-10-16 12:39:27'
    ),
    (
        5,
        2028,
        'Vietnam Public Holidays 2028',
        7,
        1,
        1,
        '2025-10-15 22:48:19',
        '2025-10-16 12:53:56'
    ),
    (
        6,
        2029,
        'Vietnam Public Holidays 2029',
        7,
        1,
        1,
        '2025-10-15 22:48:19',
        '2025-10-16 12:53:04'
    ),
    (
        7,
        2030,
        'Vietnam Public Holidays 2030',
        7,
        1,
        1,
        '2025-10-15 22:48:19',
        '2025-10-16 12:39:48'
    );
/*!40000 ALTER TABLE `holiday_calendar` ENABLE KEYS */
;

--
-- Table structure for table `holidays`
--

DROP TABLE IF EXISTS `holidays`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `holidays` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `calendar_id` bigint NOT NULL,
    `date_holiday` date NOT NULL,
    `name` varchar(128) NOT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `is_substitute` tinyint(1) DEFAULT '0' COMMENT 'TRUE if this is a compensatory/substitute day',
    `original_holiday_date` date DEFAULT NULL COMMENT 'Original holiday date if this is a substitute day',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UQ_holidays` (`calendar_id`, `date_holiday`),
    KEY `idx_holidays_is_substitute` (`is_substitute`),
    KEY `idx_holidays_original_date` (`original_holiday_date`),
    CONSTRAINT `FK_holidays_calendar` FOREIGN KEY (`calendar_id`) REFERENCES `holiday_calendar` (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 102 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Public holidays and substitute days. Substitute days have 200% OT rate, original holidays have 300% OT rate.';
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `holidays`
--

/*!40000 ALTER TABLE `holidays` DISABLE KEYS */
;
INSERT INTO
    `holidays`
VALUES (
        13,
        2,
        '2025-01-01',
        'Tết Dương lịch',
        '2025-10-15 23:15:28',
        0,
        NULL
    ),
    (
        14,
        3,
        '2026-01-01',
        'Tết Dương lịch',
        '2025-10-15 23:15:28',
        0,
        NULL
    ),
    (
        15,
        4,
        '2027-01-01',
        'Tết Dương lịch',
        '2025-10-15 23:15:28',
        0,
        NULL
    ),
    (
        16,
        5,
        '2028-01-01',
        'Tết Dương lịch',
        '2025-10-15 23:15:29',
        0,
        NULL
    ),
    (
        17,
        6,
        '2029-01-01',
        'Tết Dương lịch',
        '2025-10-15 23:15:29',
        0,
        NULL
    ),
    (
        18,
        7,
        '2030-01-01',
        'Tết Dương lịch',
        '2025-10-15 23:15:29',
        0,
        NULL
    ),
    (
        19,
        2,
        '2025-04-30',
        'Ngày Giải phóng miền Nam',
        '2025-10-15 23:17:01',
        0,
        NULL
    ),
    (
        20,
        3,
        '2026-04-30',
        'Ngày Giải phóng miền Nam',
        '2025-10-15 23:17:01',
        0,
        NULL
    ),
    (
        21,
        4,
        '2027-04-30',
        'Ngày Giải phóng miền Nam',
        '2025-10-15 23:17:02',
        0,
        NULL
    ),
    (
        22,
        5,
        '2028-01-03',
        'Nghỉ bù Tết Dương lịch (rơi vào T7)',
        '2025-10-15 23:17:02',
        1,
        '2028-01-01'
    ),
    (
        23,
        6,
        '2029-04-30',
        'Ngày Giải phóng miền Nam',
        '2025-10-15 23:17:02',
        0,
        NULL
    ),
    (
        24,
        7,
        '2030-04-30',
        'Ngày Giải phóng miền Nam',
        '2025-10-15 23:17:02',
        0,
        NULL
    ),
    (
        25,
        2,
        '2025-05-01',
        'Ngày Quốc tế Lao động',
        '2025-10-15 23:17:45',
        0,
        NULL
    ),
    (
        26,
        2,
        '2025-09-02',
        'Quốc khánh (Ngày 1)',
        '2025-10-15 23:17:45',
        0,
        NULL
    ),
    (
        27,
        3,
        '2026-05-01',
        'Ngày Quốc tế Lao động',
        '2025-10-15 23:17:46',
        0,
        NULL
    ),
    (
        28,
        3,
        '2026-09-02',
        'Quốc khánh (Ngày 1)',
        '2025-10-15 23:17:46',
        0,
        NULL
    ),
    (
        29,
        4,
        '2027-05-01',
        'Ngày Quốc tế Lao động',
        '2025-10-15 23:17:46',
        0,
        NULL
    ),
    (
        30,
        4,
        '2027-05-03',
        'Nghỉ bù Ngày Quốc tế Lao động (rơi vào T7)',
        '2025-10-15 23:17:47',
        1,
        '2027-05-01'
    ),
    (
        31,
        2,
        '2025-09-03',
        'Quốc khánh (Ngày 2)',
        '2025-10-15 23:18:39',
        0,
        NULL
    ),
    (
        32,
        3,
        '2026-09-03',
        'Quốc khánh (Ngày 2)',
        '2025-10-15 23:18:39',
        0,
        NULL
    ),
    (
        33,
        4,
        '2027-09-02',
        'Quốc khánh (Ngày 1)',
        '2025-10-15 23:18:40',
        0,
        NULL
    ),
    (
        34,
        5,
        '2028-04-30',
        'Ngày Giải phóng miền Nam',
        '2025-10-15 23:18:40',
        0,
        NULL
    ),
    (
        35,
        6,
        '2029-05-01',
        'Ngày Quốc tế Lao động',
        '2025-10-15 23:18:40',
        0,
        NULL
    ),
    (
        36,
        7,
        '2030-05-01',
        'Ngày Quốc tế Lao động',
        '2025-10-15 23:18:41',
        0,
        NULL
    ),
    (
        37,
        2,
        '2025-01-28',
        'Tết Nguyên Đán (Ngày 28 Tết)',
        '2025-10-15 23:19:19',
        0,
        NULL
    ),
    (
        38,
        3,
        '2026-02-16',
        'Tết Nguyên Đán (Ngày 28 Tết)',
        '2025-10-15 23:19:19',
        0,
        NULL
    ),
    (
        39,
        4,
        '2027-09-03',
        'Quốc khánh (Ngày 2)',
        '2025-10-15 23:19:20',
        0,
        NULL
    ),
    (
        40,
        5,
        '2028-05-01',
        'Nghỉ bù Ngày Giải phóng miền Nam (rơi vào CN)',
        '2025-10-15 23:19:20',
        0,
        NULL
    ),
    (
        41,
        6,
        '2029-09-02',
        'Quốc khánh (Ngày 1)',
        '2025-10-15 23:19:21',
        0,
        NULL
    ),
    (
        42,
        7,
        '2030-09-02',
        'Quốc khánh (Ngày 1)',
        '2025-10-15 23:19:21',
        0,
        NULL
    ),
    (
        43,
        2,
        '2025-01-29',
        'Tết Nguyên Đán (Ngày 29 Tết)',
        '2025-10-15 23:23:25',
        0,
        NULL
    ),
    (
        44,
        3,
        '2026-02-17',
        'Tết Nguyên Đán (Ngày 29 Tết)',
        '2025-10-15 23:23:26',
        0,
        NULL
    ),
    (
        45,
        4,
        '2027-02-05',
        'Tết Nguyên Đán (Ngày 28 Tết)',
        '2025-10-15 23:23:26',
        0,
        NULL
    ),
    (
        46,
        5,
        '2028-09-02',
        'Quốc khánh (Ngày 1)',
        '2025-10-15 23:23:27',
        0,
        NULL
    ),
    (
        47,
        6,
        '2029-09-03',
        'Nghỉ bù Quốc khánh (Ngày 1) (rơi vào CN)',
        '2025-10-15 23:23:28',
        0,
        NULL
    ),
    (
        48,
        7,
        '2030-09-03',
        'Quốc khánh (Ngày 2)',
        '2025-10-15 23:23:28',
        0,
        NULL
    ),
    (
        49,
        2,
        '2025-01-30',
        'Tết Nguyên Đán (Mùng 1 Tết)',
        '2025-10-15 23:23:45',
        0,
        NULL
    ),
    (
        50,
        3,
        '2026-02-18',
        'Tết Nguyên Đán (Mùng 1 Tết)',
        '2025-10-15 23:23:46',
        0,
        NULL
    ),
    (
        51,
        4,
        '2027-02-06',
        'Tết Nguyên Đán (Ngày 29 Tết)',
        '2025-10-15 23:23:47',
        0,
        NULL
    ),
    (
        52,
        5,
        '2028-09-04',
        'Nghỉ bù Quốc khánh (Ngày 1) (rơi vào T7)',
        '2025-10-15 23:23:47',
        0,
        NULL
    ),
    (
        53,
        6,
        '2029-02-12',
        'Tết Nguyên Đán (Ngày 28 Tết)',
        '2025-10-15 23:23:48',
        0,
        NULL
    ),
    (
        54,
        7,
        '2030-02-02',
        'Tết Nguyên Đán (Ngày 28 Tết)',
        '2025-10-15 23:23:48',
        0,
        NULL
    ),
    (
        55,
        2,
        '2025-01-31',
        'Tết Nguyên Đán (Mùng 2 Tết)',
        '2025-10-15 23:32:09',
        0,
        NULL
    ),
    (
        56,
        3,
        '2026-02-19',
        'Tết Nguyên Đán (Mùng 2 Tết)',
        '2025-10-15 23:32:10',
        0,
        NULL
    ),
    (
        57,
        4,
        '2027-02-08',
        'Nghỉ bù Tết Nguyên Đán (Ngày 29 Tết) (rơi vào T7)',
        '2025-10-15 23:32:12',
        0,
        NULL
    ),
    (
        58,
        5,
        '2028-09-03',
        'Quốc khánh (Ngày 2)',
        '2025-10-15 23:32:13',
        0,
        NULL
    ),
    (
        59,
        6,
        '2029-02-13',
        'Tết Nguyên Đán (Ngày 29 Tết)',
        '2025-10-15 23:32:14',
        0,
        NULL
    ),
    (
        60,
        7,
        '2030-02-04',
        'Nghỉ bù Tết Nguyên Đán (Ngày 28 Tết) (rơi vào T7)',
        '2025-10-15 23:32:15',
        0,
        NULL
    ),
    (
        61,
        2,
        '2025-02-01',
        'Tết Nguyên Đán (Mùng 3 Tết)',
        '2025-10-15 23:33:20',
        0,
        NULL
    ),
    (
        62,
        3,
        '2026-02-20',
        'Tết Nguyên Đán (Mùng 3 Tết)',
        '2025-10-15 23:33:21',
        0,
        NULL
    ),
    (
        63,
        4,
        '2027-02-07',
        'Tết Nguyên Đán (Mùng 1 Tết)',
        '2025-10-15 23:33:22',
        0,
        NULL
    ),
    (
        64,
        5,
        '2028-09-05',
        'Nghỉ bù Quốc khánh (Ngày 2) (rơi vào CN)',
        '2025-10-15 23:33:23',
        0,
        NULL
    ),
    (
        65,
        6,
        '2029-02-14',
        'Tết Nguyên Đán (Mùng 1 Tết)',
        '2025-10-15 23:33:24',
        0,
        NULL
    ),
    (
        66,
        7,
        '2030-02-03',
        'Tết Nguyên Đán (Ngày 29 Tết)',
        '2025-10-15 23:33:25',
        0,
        NULL
    ),
    (
        67,
        2,
        '2025-02-03',
        'Nghỉ bù Tết Nguyên Đán (Mùng 3 Tết) (rơi vào T7)',
        '2025-10-16 00:35:23',
        0,
        NULL
    ),
    (
        68,
        3,
        '2026-02-21',
        'Tết Nguyên Đán (Mùng 4 Tết)',
        '2025-10-16 00:35:24',
        0,
        NULL
    ),
    (
        69,
        4,
        '2027-02-09',
        'Nghỉ bù Tết Nguyên Đán (Mùng 1 Tết) (rơi vào CN)',
        '2025-10-16 00:35:25',
        0,
        NULL
    ),
    (
        70,
        5,
        '2028-01-25',
        'Tết Nguyên Đán (Ngày 28 Tết)',
        '2025-10-16 00:35:25',
        0,
        NULL
    ),
    (
        71,
        6,
        '2029-02-15',
        'Tết Nguyên Đán (Mùng 2 Tết)',
        '2025-10-16 00:35:26',
        0,
        NULL
    ),
    (
        72,
        7,
        '2030-02-05',
        'Nghỉ bù Tết Nguyên Đán (Ngày 29 Tết) (rơi vào CN)',
        '2025-10-16 00:35:27',
        0,
        NULL
    ),
    (
        73,
        2,
        '2025-02-02',
        'Tết Nguyên Đán (Mùng 4 Tết)',
        '2025-10-16 00:35:35',
        0,
        NULL
    ),
    (
        74,
        3,
        '2026-02-23',
        'Nghỉ bù Tết Nguyên Đán (Mùng 4 Tết) (rơi vào T7)',
        '2025-10-16 00:35:36',
        0,
        NULL
    ),
    (
        75,
        4,
        '2027-02-10',
        'Tết Nguyên Đán (Mùng 4 Tết)',
        '2025-10-16 00:35:37',
        0,
        NULL
    ),
    (
        76,
        5,
        '2028-01-26',
        'Tết Nguyên Đán (Ngày 29 Tết)',
        '2025-10-16 00:35:38',
        0,
        NULL
    ),
    (
        77,
        6,
        '2029-02-16',
        'Tết Nguyên Đán (Mùng 3 Tết)',
        '2025-10-16 00:35:38',
        0,
        NULL
    ),
    (
        78,
        7,
        '2030-02-06',
        'Tết Nguyên Đán (Mùng 3 Tết)',
        '2025-10-16 00:35:39',
        0,
        NULL
    ),
    (
        79,
        2,
        '2025-02-04',
        'Nghỉ bù Tết Nguyên Đán (Mùng 4 Tết) (rơi vào CN)',
        '2025-10-16 00:36:05',
        0,
        NULL
    ),
    (
        80,
        3,
        '2026-02-22',
        'Tết Nguyên Đán (Mùng 5 Tết)',
        '2025-10-16 00:36:06',
        0,
        NULL
    ),
    (
        81,
        4,
        '2027-02-11',
        'Tết Nguyên Đán (Mùng 5 Tết)',
        '2025-10-16 00:36:06',
        0,
        NULL
    ),
    (
        82,
        5,
        '2028-01-27',
        'Tết Nguyên Đán (Mùng 1 Tết)',
        '2025-10-16 00:36:07',
        0,
        NULL
    ),
    (
        83,
        6,
        '2029-02-17',
        'Tết Nguyên Đán (Mùng 4 Tết)',
        '2025-10-16 00:36:08',
        0,
        NULL
    ),
    (
        84,
        7,
        '2030-02-07',
        'Tết Nguyên Đán (Mùng 4 Tết)',
        '2025-10-16 00:36:09',
        0,
        NULL
    ),
    (
        85,
        2,
        '2025-04-07',
        'Giỗ Tổ Hùng Vương',
        '2025-10-16 00:57:22',
        0,
        NULL
    ),
    (
        86,
        3,
        '2026-02-24',
        'Nghỉ bù Tết Nguyên Đán (Mùng 5 Tết) (rơi vào CN)',
        '2025-10-16 00:57:23',
        0,
        NULL
    ),
    (
        87,
        4,
        '2027-04-16',
        'Giỗ Tổ Hùng Vương',
        '2025-10-16 00:57:24',
        0,
        NULL
    ),
    (
        88,
        5,
        '2028-01-28',
        'Tết Nguyên Đán (Mùng 2 Tết)',
        '2025-10-16 00:57:25',
        0,
        NULL
    ),
    (
        89,
        6,
        '2029-02-19',
        'Nghỉ bù Tết Nguyên Đán (Mùng 4 Tết) (rơi vào T7)',
        '2025-10-16 00:57:26',
        0,
        NULL
    ),
    (
        90,
        7,
        '2030-02-08',
        'Tết Nguyên Đán (Mùng 5 Tết)',
        '2025-10-16 00:57:27',
        0,
        NULL
    ),
    (
        91,
        3,
        '2026-04-26',
        'Giỗ Tổ Hùng Vương',
        '2025-10-16 12:39:25',
        0,
        NULL
    ),
    (
        92,
        5,
        '2028-01-29',
        'Tết Nguyên Đán (Mùng 3 Tết)',
        '2025-10-16 12:39:28',
        0,
        NULL
    ),
    (
        93,
        6,
        '2029-02-18',
        'Tết Nguyên Đán (Mùng 5 Tết)',
        '2025-10-16 12:39:28',
        0,
        NULL
    ),
    (
        94,
        7,
        '2030-04-12',
        'Giỗ Tổ Hùng Vương',
        '2025-10-16 12:39:29',
        0,
        NULL
    ),
    (
        95,
        3,
        '2026-04-27',
        'Nghỉ bù Giỗ Tổ Hùng Vương (rơi vào CN)',
        '2025-10-16 12:39:44',
        1,
        '2026-04-26'
    ),
    (
        96,
        5,
        '2028-01-31',
        'Nghỉ bù Tết Nguyên Đán (Mùng 3 Tết) (rơi vào T7)',
        '2025-10-16 12:39:45',
        1,
        '2028-01-29'
    ),
    (
        97,
        6,
        '2029-02-20',
        'Nghỉ bù Tết Nguyên Đán (Mùng 5 Tết) (rơi vào CN)',
        '2025-10-16 12:39:46',
        1,
        '2029-02-18'
    ),
    (
        98,
        5,
        '2028-01-30',
        'Tết Nguyên Đán (Mùng 4 Tết)',
        '2025-10-16 12:39:53',
        0,
        NULL
    ),
    (
        99,
        6,
        '2029-04-23',
        'Giỗ Tổ Hùng Vương',
        '2025-10-16 12:39:54',
        0,
        NULL
    ),
    (
        100,
        5,
        '2028-02-01',
        'Nghỉ bù Tết Nguyên Đán (Mùng 4 Tết) (rơi vào CN)',
        '2025-10-16 12:53:02',
        1,
        '2028-01-30'
    ),
    (
        101,
        5,
        '2028-04-04',
        'Giỗ Tổ Hùng Vương',
        '2025-10-16 12:53:12',
        0,
        NULL
    );
/*!40000 ALTER TABLE `holidays` ENABLE KEYS */
;

--
-- Table structure for table `job_postings`
--

DROP TABLE IF EXISTS `job_postings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `job_postings` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `request_id` bigint DEFAULT NULL,
    `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `code` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `department_id` bigint DEFAULT NULL,
    `position_id` bigint DEFAULT NULL,
    `quantity` int DEFAULT NULL,
    `job_type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `job_level` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    `working_location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `requirements` text COLLATE utf8mb4_unicode_ci,
    `benefits` text COLLATE utf8mb4_unicode_ci,
    `min_experience_years` int DEFAULT NULL,
    `min_salary` decimal(18, 2) DEFAULT NULL,
    `max_salary` decimal(18, 2) DEFAULT NULL,
    `salary_type` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `status` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
    `rejected_reason` text COLLATE utf8mb4_unicode_ci,
    `priority` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `working_hours` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `contact_email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `contact_phone` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `start_date` date DEFAULT NULL,
    `application_deadline` date DEFAULT NULL,
    `published_at` datetime DEFAULT NULL,
    `created_by_account_id` bigint DEFAULT NULL,
    `approved_by_account_id` bigint DEFAULT NULL,
    `published_by_account_id` bigint DEFAULT NULL,
    `approved_at` datetime DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `expiry_date` date DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_job_postings_code` (`code`),
    KEY `FK_job_request` (`request_id`),
    KEY `FK_job_dept` (`department_id`),
    KEY `FK_job_creator` (`created_by_account_id`),
    KEY `FK_jp_position` (`position_id`),
    KEY `FK_jp_approved_by` (`approved_by_account_id`),
    KEY `FK_jp_published_by` (`published_by_account_id`),
    CONSTRAINT `FK_job_creator` FOREIGN KEY (`created_by_account_id`) REFERENCES `accounts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_job_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_job_request` FOREIGN KEY (`request_id`) REFERENCES `requests` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_jp_approved_by` FOREIGN KEY (`approved_by_account_id`) REFERENCES `accounts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_jp_position` FOREIGN KEY (`position_id`) REFERENCES `positions` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_jp_published_by` FOREIGN KEY (`published_by_account_id`) REFERENCES `accounts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 10 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `job_postings`
--

/*!40000 ALTER TABLE `job_postings` DISABLE KEYS */
;
INSERT INTO
    `job_postings`
VALUES (
        1,
        60,
        'Backend Developer',
        'BD - 001',
        NULL,
        NULL,
        2,
        'FULL_TIME',
        'SENIOR',
        'test1',
        'Ho Chi Minh',
        'test2',
        'test3',
        5,
        20000000.00,
        30000000.00,
        NULL,
        'PUBLISHED',
        NULL,
        NULL,
        NULL,
        'trunghieu999909@gmail.com',
        '0333180251',
        NULL,
        '2025-11-08',
        '2025-10-29 15:17:13',
        44,
        NULL,
        41,
        NULL,
        '2025-10-22 09:16:55',
        '2025-10-29 15:17:14',
        '2025-11-08'
    ),
    (
        2,
        61,
        'Data Analyst',
        'DA - 001',
        NULL,
        NULL,
        2,
        'FULL_TIME',
        'MIDDLE',
        'test1',
        'Ho Chi Minh',
        'test2',
        'only moeny',
        4,
        12000000.00,
        16000000.00,
        'NEGOTIABLE',
        'PUBLISHED',
        NULL,
        NULL,
        '',
        'trunghieu999909@gmail.com',
        '0333180251',
        NULL,
        '2025-11-15',
        '2025-10-30 14:31:57',
        44,
        41,
        41,
        '2025-10-30 09:56:26',
        '2025-10-22 09:59:49',
        '2025-10-30 14:31:57',
        '2025-11-15'
    ),
    (
        3,
        57,
        'Front-end Developer',
        'FED - 001',
        NULL,
        NULL,
        1,
        'INTERN',
        'JUNIOR',
        'test1',
        'Ho Chi Minh',
        'test2',
        'just money',
        2,
        1000000.00,
        2000000.00,
        'NET',
        'REJECTED',
        NULL,
        NULL,
        '',
        'trunghieu999909@gmail.com',
        '0333180251',
        '2025-11-01',
        '2025-11-22',
        NULL,
        41,
        NULL,
        NULL,
        NULL,
        '2025-10-22 10:49:02',
        '2025-10-28 23:28:23',
        '2025-11-22'
    ),
    (
        4,
        96,
        'Backend Developer',
        'BD - 002',
        5,
        NULL,
        1,
        'FULL_TIME',
        'JUNIOR',
        'test',
        'Ho Chi Minh',
        'test2',
        'test4',
        5,
        1000000.00,
        2000000.00,
        'NEGOTIABLE',
        'REJECTED',
        'please change email to saubeocute1@gmail.com',
        NULL,
        'Monday - Friday 8:00 - 17:00',
        'trunghieu999909@gmail.com',
        '0333180251',
        '2025-11-08',
        '2025-11-08',
        NULL,
        41,
        41,
        NULL,
        '2025-10-28 23:49:11',
        '2025-10-28 23:38:30',
        '2025-10-28 23:49:12',
        '2025-11-08'
    ),
    (
        5,
        140,
        'accountant',
        'ac  - 001',
        5,
        NULL,
        1,
        'INTERN',
        'JUNIOR',
        'test1',
        'Hanoi',
        'need specify skill',
        'only money',
        1,
        15000000.00,
        20000000.00,
        'GROSS',
        'PENDING',
        NULL,
        NULL,
        'monday - friday',
        'trunghieu999909@gmail.com',
        '0333180251',
        '2026-01-01',
        '2025-11-30',
        NULL,
        44,
        NULL,
        NULL,
        NULL,
        '2025-10-29 15:15:06',
        '2025-10-29 15:15:06',
        '2025-11-30'
    ),
    (
        6,
        147,
        'Treasurer',
        'T - 001',
        5,
        NULL,
        1,
        'FULL_TIME',
        'SENIOR',
        'The Corporate Treasurer is the steward and custodian of an organization\'s monetary assets. Their primary responsibility is to manage the company\'s cash flow, banking relationships, and financial risks to ensure the organization has sufficient liquidity to meet its obligations and funding for its strategic objectives. The Treasurer is a strategic partner to the CFO and executive team, influencing major decisions on capital structure, investments, and risk mitigation.',
        'HRMS office, Ho Chi Minh City',
        'The Treasurer is a high-level leadership role that requires extensive, progressive experience.',
        'The benefits package for a senior finance professional is generally robust, focused on financial security and work-life support.\r\n\r\nHealth & Wellness: Comprehensive medical, dental, and vision insurance.\r\n\r\nRetirement: Strong retirement plans, most commonly a 401(k) with a generous company match, and sometimes traditional pension plans.\r\n\r\nEquity & Ownership: Stock Options, Restricted Stock Units (RSUs), or other forms of long-term incentive compensation to align the Treasurer\'s interests with shareholder value.\r\n\r\nTime Off: Substantial paid time off (PTO), sick leave, and paid holidays.\r\n\r\nOther Perks: Life insurance, short-term and long-term disability coverage, and potential executive-level benefits like supplemental retirement plans or club memberships.',
        4,
        1000000.00,
        2000000.00,
        'NEGOTIABLE',
        'PUBLISHED',
        NULL,
        NULL,
        'Monday - Friday 8:00 - 17:00',
        'trunghieu999909@gmail.com',
        '0333180251',
        '2025-11-08',
        '2025-11-08',
        '2025-10-30 14:31:49',
        44,
        41,
        41,
        '2025-10-30 14:31:44',
        '2025-10-29 16:01:07',
        '2025-10-30 14:31:49',
        '2025-11-08'
    ),
    (
        7,
        116,
        'Accountant',
        'AC - 003',
        5,
        NULL,
        1,
        'FULL_TIME',
        'MIDDLE',
        'accountant',
        '100 Main Street, San Francisco, CA',
        'oke in all skill',
        'just money',
        5,
        1000000.00,
        2000000.00,
        'GROSS',
        'PUBLISHED',
        NULL,
        NULL,
        'Monday - Friday 8h00 - 17h00',
        'saubeocute1@gmail.com',
        '0333180251',
        '2025-11-01',
        '2025-10-31',
        '2025-10-30 14:32:14',
        44,
        41,
        41,
        '2025-10-30 14:32:08',
        '2025-10-29 16:22:33',
        '2025-10-30 14:32:14',
        '2025-10-31'
    );
/*!40000 ALTER TABLE `job_postings` ENABLE KEYS */
;

--
-- Table structure for table `leave_balances`
--

DROP TABLE IF EXISTS `leave_balances`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `leave_balances` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `leave_type_id` bigint NOT NULL,
    `year` int NOT NULL,
    `balance_days` decimal(6, 2) NOT NULL DEFAULT '0.00',
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    UNIQUE KEY `UQ_lb` (
        `user_id`,
        `leave_type_id`,
        `year`
    ),
    KEY `FK_lb_type` (`leave_type_id`),
    CONSTRAINT `FK_lb_type` FOREIGN KEY (`leave_type_id`) REFERENCES `leave_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_lb_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `leave_balances`
--

/*!40000 ALTER TABLE `leave_balances` DISABLE KEYS */
;
/*!40000 ALTER TABLE `leave_balances` ENABLE KEYS */
;

--
-- Table structure for table `leave_ledger`
--

DROP TABLE IF EXISTS `leave_ledger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `leave_ledger` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `leave_type_id` bigint NOT NULL,
    `request_id` bigint DEFAULT NULL,
    `delta_days` decimal(6, 2) NOT NULL,
    `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    KEY `IX_ll_user_time` (`user_id`, `created_at`),
    KEY `FK_ll_type` (`leave_type_id`),
    KEY `FK_ll_request` (`request_id`),
    CONSTRAINT `FK_ll_request` FOREIGN KEY (`request_id`) REFERENCES `requests` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_ll_type` FOREIGN KEY (`leave_type_id`) REFERENCES `leave_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_ll_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `leave_ledger`
--

/*!40000 ALTER TABLE `leave_ledger` DISABLE KEYS */
;
/*!40000 ALTER TABLE `leave_ledger` ENABLE KEYS */
;

--
-- Table structure for table `leave_types`
--

DROP TABLE IF EXISTS `leave_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `leave_types` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    `default_days` decimal(5, 2) NOT NULL DEFAULT '0.00',
    `max_days` decimal(5, 2) DEFAULT NULL,
    `is_paid` tinyint(1) NOT NULL DEFAULT '1',
    `requires_approval` tinyint(1) NOT NULL DEFAULT '1',
    `requires_certificate` tinyint(1) NOT NULL DEFAULT '0',
    `min_advance_notice` int DEFAULT NULL,
    `is_active` tinyint(1) NOT NULL DEFAULT '1',
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `code` (`code`)
) ENGINE = InnoDB AUTO_INCREMENT = 14 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `leave_types`
--

/*!40000 ALTER TABLE `leave_types` DISABLE KEYS */
;
INSERT INTO
    `leave_types`
VALUES (
        7,
        'ANNUAL',
        'Annual Leave',
        'Annual leave after 12 months: 12/14/16 days per condition. Pro-rate if <12 months; +1 day per 5 years service',
        12.00,
        21.00,
        1,
        1,
        0,
        3,
        1,
        '2025-10-15 15:37:43',
        '2025-10-15 15:37:43'
    ),
    (
        8,
        'PERSONAL',
        'Personal Leave',
        'Personal leave (paid): 3 days (marriage); 1 day (child marriage); 3 days (death of parent/spouse/child)',
        3.00,
        3.00,
        1,
        1,
        1,
        1,
        1,
        '2025-10-15 15:37:43',
        '2025-10-15 15:37:43'
    ),
    (
        9,
        'MATERNITY',
        'Maternity Leave',
        'Maternity 6 months (Social Insurance). Records and coordinates SI claims per BR-LV-07',
        180.00,
        180.00,
        1,
        1,
        1,
        30,
        1,
        '2025-10-15 15:37:43',
        '2025-10-15 15:37:43'
    ),
    (
        10,
        'SICK',
        'Sick Leave',
        'Sick leave (SI): 30/40/60 days by SI seniority. Child-care sickness: 20 days (<3y), 15 days (3-<7y)',
        30.00,
        60.00,
        1,
        0,
        1,
        0,
        1,
        '2025-10-15 15:37:43',
        '2025-10-15 15:37:43'
    ),
    (
        11,
        'EMERGENCY',
        'Emergency Leave',
        'Emergency leave for urgent family matters, accidents, or unforeseen circumstances. No advance notice required.',
        3.00,
        5.00,
        1,
        1,
        0,
        0,
        1,
        '2025-10-15 15:37:43',
        '2025-10-15 15:37:43'
    ),
    (
        12,
        'UNPAID',
        'Unpaid Leave',
        'npaid leave - Max 5 days per request, 13 days per month, 30 days per year. 3 days advance notice required. Salary will be deducted.\'',
        5.00,
        30.00,
        0,
        1,
        0,
        3,
        1,
        '2025-10-15 15:37:43',
        '2025-10-16 14:32:11'
    ),
    (
        13,
        'PATERNITY',
        'Paternity Leave',
        'Paternity leave for fathers. Must be taken within 60 days from birth. Paid leave under company policy (5-14 working days as per law, company provides 7 days). Birth certificate required.',
        7.00,
        7.00,
        1,
        1,
        1,
        0,
        1,
        '2025-10-17 08:16:26',
        '2025-10-17 08:16:26'
    );
/*!40000 ALTER TABLE `leave_types` ENABLE KEYS */
;

--
-- Table structure for table `ot_policies`
--

DROP TABLE IF EXISTS `ot_policies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `ot_policies` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `code` varchar(32) NOT NULL,
    `name` varchar(128) NOT NULL,
    `description` varchar(255) DEFAULT NULL,
    `rules_json` json DEFAULT NULL,
    `assignments_json` json DEFAULT NULL,
    `updated_by_account_id` bigint DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    UNIQUE KEY `code` (`code`),
    KEY `FK_ot_updated_by` (`updated_by_account_id`),
    CONSTRAINT `FK_ot_updated_by` FOREIGN KEY (`updated_by_account_id`) REFERENCES `accounts` (`id`),
    CONSTRAINT `ot_policies_chk_1` CHECK (
        (
            (`rules_json` is null)
            or json_valid(`rules_json`)
        )
    ),
    CONSTRAINT `ot_policies_chk_2` CHECK (
        (
            (`assignments_json` is null)
            or json_valid(`assignments_json`)
        )
    )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `ot_policies`
--

/*!40000 ALTER TABLE `ot_policies` DISABLE KEYS */
;
/*!40000 ALTER TABLE `ot_policies` ENABLE KEYS */
;

--
-- Table structure for table `outbox_messages`
--

DROP TABLE IF EXISTS `outbox_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `outbox_messages` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `topic` varchar(128) NOT NULL,
    `payload_json` json NOT NULL,
    `headers_json` json DEFAULT NULL,
    `status` varchar(16) NOT NULL DEFAULT 'NEW',
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `sent_at` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `outbox_messages_chk_1` CHECK (json_valid(`payload_json`)),
    CONSTRAINT `outbox_messages_chk_2` CHECK (
        (
            (`headers_json` is null)
            or json_valid(`headers_json`)
        )
    )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `outbox_messages`
--

/*!40000 ALTER TABLE `outbox_messages` DISABLE KEYS */
;
/*!40000 ALTER TABLE `outbox_messages` ENABLE KEYS */
;

--
-- Table structure for table `payslips`
--

DROP TABLE IF EXISTS `payslips`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `payslips` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `period_start` date NOT NULL,
    `period_end` date NOT NULL,
    `currency` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `gross_amount` decimal(18, 2) NOT NULL DEFAULT '0.00',
    `net_amount` decimal(18, 2) NOT NULL DEFAULT '0.00',
    `details_json` json DEFAULT NULL,
    `file_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `status` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'approved',
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    UNIQUE KEY `UQ_ps_user_period` (
        `user_id`,
        `period_start`,
        `period_end`
    ),
    CONSTRAINT `FK_ps_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `payslips`
--

/*!40000 ALTER TABLE `payslips` DISABLE KEYS */
;
/*!40000 ALTER TABLE `payslips` ENABLE KEYS */
;

--
-- Table structure for table `position_roles`
--

DROP TABLE IF EXISTS `position_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `position_roles` (
    `position_id` bigint NOT NULL,
    `role_id` bigint NOT NULL,
    PRIMARY KEY (`position_id`, `role_id`),
    KEY `FK_pos_roles_role` (`role_id`),
    CONSTRAINT `FK_pos_roles_position` FOREIGN KEY (`position_id`) REFERENCES `positions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_pos_roles_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `position_roles`
--

/*!40000 ALTER TABLE `position_roles` DISABLE KEYS */
;
INSERT INTO
    `position_roles`
VALUES (6, 10),
    (7, 11),
    (8, 12),
    (9, 13),
    (10, 14);
/*!40000 ALTER TABLE `position_roles` ENABLE KEYS */
;

--
-- Table structure for table `positions`
--

DROP TABLE IF EXISTS `positions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `positions` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `job_level` int DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `code` (`code`),
    KEY `idx_positions_name` (`name`),
    KEY `idx_positions_code` (`code`),
    KEY `idx_positions_job_level` (`job_level`),
    KEY `idx_positions_code_level` (`code`, `job_level`)
) ENGINE = InnoDB AUTO_INCREMENT = 13 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `positions`
--

/*!40000 ALTER TABLE `positions` DISABLE KEYS */
;
INSERT INTO
    `positions`
VALUES (
        6,
        'ADMIN',
        'Administrator',
        1,
        '2025-10-16 09:33:20',
        '2025-10-16 09:33:20',
        NULL
    ),
    (
        7,
        'HR_MANAGER',
        'HR Manager',
        2,
        '2025-10-16 09:33:20',
        '2025-10-16 09:33:20',
        NULL
    ),
    (
        8,
        'HR_STAFF',
        'HR Staff',
        3,
        '2025-10-16 09:33:20',
        '2025-10-16 09:33:20',
        NULL
    ),
    (
        9,
        'DEPT_MANAGER',
        'Department Manager',
        4,
        '2025-10-16 09:33:20',
        '2025-10-16 09:33:20',
        NULL
    ),
    (
        10,
        'STAFF',
        'Staff',
        5,
        '2025-10-16 09:33:20',
        '2025-10-16 09:33:20',
        NULL
    );
/*!40000 ALTER TABLE `positions` ENABLE KEYS */
;

--
-- Table structure for table `request_transitions`
--

DROP TABLE IF EXISTS `request_transitions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `request_transitions` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `request_id` bigint NOT NULL,
    `from_status` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `to_status` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `action` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `actor_account_id` bigint NOT NULL,
    `actor_user_id` bigint NOT NULL,
    `note` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    KEY `IX_rt_request` (`request_id`, `created_at`),
    KEY `FK_rt_actor_account` (`actor_account_id`),
    KEY `FK_rt_actor_user` (`actor_user_id`),
    CONSTRAINT `FK_rt_actor_account` FOREIGN KEY (`actor_account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_rt_actor_user` FOREIGN KEY (`actor_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_rt_request` FOREIGN KEY (`request_id`) REFERENCES `requests` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `request_transitions`
--

/*!40000 ALTER TABLE `request_transitions` DISABLE KEYS */
;
/*!40000 ALTER TABLE `request_transitions` ENABLE KEYS */
;

--
-- Table structure for table `request_types`
--

DROP TABLE IF EXISTS `request_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `request_types` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    UNIQUE KEY `code` (`code`)
) ENGINE = InnoDB AUTO_INCREMENT = 27 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `request_types`
--

/*!40000 ALTER TABLE `request_types` DISABLE KEYS */
;
INSERT INTO
    `request_types`
VALUES (
        6,
        'LEAVE_REQUEST',
        'Leave Request',
        '2025-10-15 15:26:07'
    ),
    (
        7,
        'OVERTIME_REQUEST',
        'Overtime Request',
        '2025-10-15 15:26:07'
    ),
    (
        8,
        'ADJUSTMENT_REQUEST',
        'Adjustment Request',
        '2025-10-15 15:26:07'
    ),
    (
        9,
        'RECRUITMENT_REQUEST',
        'Recruitment Request',
        '2025-10-15 15:26:07'
    );
/*!40000 ALTER TABLE `request_types` ENABLE KEYS */
;

--
-- Table structure for table `requests`
--

DROP TABLE IF EXISTS `requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `requests` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `request_type_id` bigint NOT NULL,
    `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `detail` json DEFAULT NULL,
    `created_by_account_id` bigint NOT NULL,
    `created_by_user_id` bigint NOT NULL,
    `department_id` bigint DEFAULT NULL,
    `status` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DRAFT',
    `current_approver_account_id` bigint DEFAULT NULL,
    `approve_reason` text COLLATE utf8mb4_unicode_ci COMMENT 'Reason provided when approving or rejecting the request',
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    KEY `IX_requests_type_status` (`request_type_id`, `status`),
    KEY `IX_requests_creator` (`created_by_account_id`),
    KEY `FK_requests_creator_user` (`created_by_user_id`),
    KEY `FK_requests_dept` (`department_id`),
    KEY `FK_requests_current_approver` (`current_approver_account_id`),
    KEY `idx_requests_user_status` (
        `created_by_user_id`,
        `status`
    ),
    KEY `idx_requests_dept_status` (`department_id`, `status`),
    KEY `idx_requests_approver_status` (
        `current_approver_account_id`,
        `status`
    ),
    CONSTRAINT `FK_requests_creator_account` FOREIGN KEY (`created_by_account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_requests_creator_user` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_requests_current_approver` FOREIGN KEY (`current_approver_account_id`) REFERENCES `accounts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_requests_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_requests_type` FOREIGN KEY (`request_type_id`) REFERENCES `request_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 187 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `requests`
--

/*!40000 ALTER TABLE `requests` DISABLE KEYS */
;
INSERT INTO
    `requests`
VALUES (
        53,
        6,
        'Leave Request - Sick Leave',
        '{\"reason\": \"check\", \"endDate\": \"2025-11-26T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-11-26T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"SICK\", \"leaveTypeName\": \"Sick Leave\", \"certificateRequired\": true}',
        46,
        57,
        5,
        'REJECTED',
        42,
        'check',
        '2025-10-20 22:05:06',
        '2025-10-20 22:25:11'
    ),
    (
        54,
        7,
        'OT Request - 2025-11-26',
        '{\"otDate\": \"2025-11-26\", \"otType\": \"WEEKDAY\", \"reason\": \"check\", \"endTime\": \"21:00\", \"otHours\": 1.5, \"startTime\": \"19:30\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-20T22:05:47.323887300\", \"createdByManager\": false}',
        46,
        57,
        5,
        'APPROVED',
        42,
        'check',
        '2025-10-20 22:05:47',
        '2025-10-20 22:25:04'
    ),
    (
        55,
        8,
        'check',
        '{\"detail_text\": \"check\", \"appealStatus\": \"APPROVED\", \"attendance_dates\": \"2025-10-28\"}',
        46,
        57,
        5,
        'APPROVED',
        41,
        'ok',
        '2025-10-20 22:06:28',
        '2025-10-30 02:35:00'
    ),
    (
        56,
        9,
        'Senior Backend Engineer - Project Alpha',
        '{\"jobType\": \"FULL_TIME\", \"jobLevel\": \"SENIOR\", \"quantity\": 2, \"maxSalary\": 1600000.0, \"minSalary\": 900000.0, \"jobSummary\": \"test all\", \"salaryType\": \"GROSS\", \"positionCode\": \"AUTO_GEN_CODE\", \"positionName\": \"Backend Developer\", \"workingLocation\": \"Ho Chi Minh\", \"recruitmentReason\": \"for test\"}',
        42,
        53,
        2,
        'REJECTED',
        41,
        'như cc',
        '2025-10-20 22:10:08',
        '2025-10-21 01:54:25'
    ),
    (
        57,
        9,
        'Junior front-end - project beta',
        '{\"jobType\": \"INTERNSHIP\", \"jobLevel\": \"JUNIOR\", \"quantity\": 1, \"maxSalary\": 200000.0, \"minSalary\": 100000.0, \"jobSummary\": \"test all \", \"salaryType\": \"NET\", \"positionCode\": \"AUTO_GEN_CODE\", \"positionName\": \"Front-end Developer\", \"workingLocation\": \"Ho Chi Minh City, District 1\", \"recruitmentReason\": \"test \"}',
        42,
        53,
        5,
        'APPROVED',
        41,
        'check',
        '2025-10-20 22:16:20',
        '2025-10-21 01:47:10'
    ),
    (
        60,
        9,
        'Senior Backend Engineer - Project Alpha',
        '{\"jobType\": \"Full-time\", \"jobLevel\": \"SENIOR\", \"quantity\": 2, \"maxSalary\": 10000000000.0, \"minSalary\": 10000000.0, \"jobSummary\": \"test\", \"salaryType\": \"Gross\", \"positionName\": \"Backend Developer\", \"attachmentPath\": \"https://docs.google.com/spreadsheets/d/1t6HRMni8tnY4ULR8V9oKGaIUkSfQ9WGDgkvYMzlwoeU/edit?usp=drive_link\", \"workingLocation\": \"100 Main Street, San Francisco, CA\", \"recruitmentReason\": \"test\"}',
        42,
        53,
        5,
        'APPROVED',
        41,
        NULL,
        '2025-10-21 03:17:45',
        '2025-10-21 22:03:15'
    ),
    (
        61,
        9,
        'Hiring for Senior Data Analyst Position',
        '{\"jobType\": \"Full-time\", \"jobLevel\": \"MIDDLE\", \"quantity\": 2, \"maxSalary\": 2222222222222.0, \"minSalary\": 1111111111.0, \"jobSummary\": \"tets\", \"salaryType\": \"Gross\", \"positionName\": \"Data Analyst\", \"attachmentPath\": \"https://docs.google.com/spreadsheets/d/1t6HRMni8tnY4ULR8V9oKGaIUkSfQ9WGDgkvYMzlwoeU/edit?usp=drive_link\", \"workingLocation\": \"Ho Chi Minh City, District 1\", \"recruitmentReason\": \"test\"}',
        42,
        53,
        5,
        'APPROVED',
        41,
        NULL,
        '2025-10-21 03:20:43',
        '2025-10-21 22:03:03'
    ),
    (
        69,
        6,
        'Leave Request - Unpaid Leave',
        '{\"reason\": \"test\", \"endDate\": \"2025-10-31T23:59:59\", \"dayCount\": 4, \"isHalfDay\": false, \"startDate\": \"2025-10-28T00:00\", \"durationDays\": 4.0, \"leaveTypeCode\": \"UNPAID\", \"leaveTypeName\": \"Unpaid Leave\", \"certificateRequired\": false}',
        54,
        62,
        5,
        'REJECTED',
        NULL,
        'Request expired (effective date passed). Auto-rejected by system.',
        '2025-10-22 15:23:22',
        '2025-10-28 14:24:07'
    ),
    (
        70,
        7,
        'OT Request - 2026-01-03 (Created by Manager)',
        '{\"otDate\": \"2026-01-03\", \"otType\": \"WEEKEND\", \"reason\": \"c\", \"endTime\": \"10:30\", \"otHours\": 3.25, \"startTime\": \"07:15\", \"payMultiplier\": 2.0, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-22T16:48:59.415695900\", \"createdByManager\": true, \"managerAccountId\": 42}',
        42,
        62,
        5,
        'REJECTED',
        42,
        'ok',
        '2025-10-22 16:48:59',
        '2025-10-22 16:51:16'
    ),
    (
        71,
        7,
        'OT Request - 2026-01-01',
        '{\"otDate\": \"2026-01-01\", \"otType\": \"HOLIDAY\", \"reason\": \"wook\", \"endTime\": \"11:15\", \"otHours\": 3.25, \"startTime\": \"08:00\", \"payMultiplier\": 3.0, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-22T21:47:48.651557\", \"createdByManager\": false}',
        46,
        57,
        5,
        'APPROVED',
        41,
        'ok',
        '2025-10-22 21:47:49',
        '2025-10-30 01:18:31'
    ),
    (
        72,
        7,
        'OT Request - 2025-10-23 (Created by Manager)',
        '{\"otDate\": \"2025-10-23\", \"otType\": \"WEEKDAY\", \"reason\": \"test\", \"endTime\": \"21:00\", \"otHours\": 2.0, \"startTime\": \"19:00\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-22T22:51:25.945774900\", \"createdByManager\": true, \"managerAccountId\": 42}',
        42,
        57,
        5,
        'APPROVED',
        46,
        NULL,
        '2025-10-22 22:51:26',
        '2025-10-22 22:54:07'
    ),
    (
        73,
        7,
        'OT Request - 2025-10-25 (Created by Manager)',
        '{\"otDate\": \"2025-10-25\", \"otType\": \"WEEKEND\", \"reason\": \"test\", \"endTime\": \"12:45\", \"otHours\": 2.25, \"startTime\": \"10:30\", \"payMultiplier\": 2.0, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-22T22:53:01.134708100\", \"createdByManager\": true, \"managerAccountId\": 42}',
        42,
        57,
        5,
        'APPROVED',
        46,
        'ok',
        '2025-10-22 22:53:01',
        '2025-10-22 22:53:31'
    ),
    (
        76,
        7,
        'OT Request - 2025-10-26 (Created by Manager)',
        '{\"otDate\": \"2025-10-26\", \"otType\": \"WEEKEND\", \"reason\": \"check\", \"endTime\": \"11:15\", \"otHours\": 3.25, \"startTime\": \"08:00\", \"payMultiplier\": 2.0, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-22T23:11:40.228669600\", \"createdByManager\": true, \"managerAccountId\": 42}',
        42,
        57,
        5,
        'REJECTED',
        46,
        'check',
        '2025-10-22 23:11:40',
        '2025-10-22 23:23:41'
    ),
    (
        77,
        7,
        'OT Request - 2025-10-28',
        '{\"otDate\": \"2025-10-28\", \"otType\": \"WEEKDAY\", \"reason\": \"check\", \"endTime\": \"21:00\", \"otHours\": 1.75, \"startTime\": \"19:15\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-22T23:32:58.505491600\", \"createdByManager\": false}',
        46,
        57,
        5,
        'REJECTED',
        NULL,
        'Request expired (effective date passed). Auto-rejected by system.',
        '2025-10-22 23:32:59',
        '2025-10-28 14:24:07'
    ),
    (
        79,
        7,
        'OT Request - 2025-10-27',
        '{\"otDate\": \"2025-10-27\", \"otType\": \"WEEKDAY\", \"reason\": \"CC\", \"endTime\": \"21:00\", \"otHours\": 2.0, \"startTime\": \"19:00\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-24T01:20:57.308409500\", \"createdByManager\": false}',
        78,
        81,
        5,
        'APPROVED',
        42,
        'cc',
        '2025-10-24 01:20:57',
        '2025-10-24 01:29:13'
    ),
    (
        80,
        7,
        'OT Request - 2026-01-01',
        '{\"otDate\": \"2026-01-01\", \"otType\": \"HOLIDAY\", \"reason\": \"CC\", \"endTime\": \"18:00\", \"otHours\": 10.0, \"startTime\": \"08:00\", \"payMultiplier\": 3.0, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-24T01:32:09.142421\", \"createdByManager\": false}',
        78,
        81,
        5,
        'REJECTED',
        42,
        'không',
        '2025-10-24 01:32:09',
        '2025-10-24 01:36:22'
    ),
    (
        81,
        7,
        'OT Request - 2025-10-25 (Created by Manager)',
        '{\"otDate\": \"2025-10-25\", \"otType\": \"WEEKEND\", \"reason\": \"đi ot đi em\", \"endTime\": \"17:45\", \"otHours\": 7.75, \"startTime\": \"10:00\", \"payMultiplier\": 2.0, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-24T01:38:18.053431300\", \"createdByManager\": true, \"managerAccountId\": 42}',
        42,
        81,
        5,
        'REJECTED',
        42,
        'không cần nữa',
        '2025-10-24 01:38:18',
        '2025-10-24 01:49:39'
    ),
    (
        83,
        7,
        'check',
        '{\"otDate\": \"2026-02-17\", \"otType\": \"HOLIDAY\", \"reason\": \"check\", \"endTime\": \"19:00\", \"otHours\": 9.0, \"startTime\": \"10:00\", \"payMultiplier\": 3.0, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-24T02:54:33.680181\", \"createdByManager\": false}',
        78,
        81,
        5,
        'REJECTED',
        41,
        'no',
        '2025-10-24 02:54:34',
        '2025-10-24 03:13:49'
    ),
    (
        85,
        6,
        'check',
        '{\"reason\": \"check\", \"endDate\": \"2025-10-29T23:59:59\", \"dayCount\": 2, \"isHalfDay\": false, \"startDate\": \"2025-10-28T00:00\", \"durationDays\": 2.0, \"leaveTypeCode\": \"PERSONAL\", \"leaveTypeName\": \"Personal Leave\", \"certificateRequired\": true}',
        78,
        81,
        5,
        'REJECTED',
        41,
        'no',
        '2025-10-24 03:12:00',
        '2025-10-24 03:13:42'
    ),
    (
        86,
        6,
        'testt',
        '{\"reason\": \"tets\", \"endDate\": \"2025-10-30T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-10-30T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"PM\", \"leaveTypeCode\": \"EMERGENCY\", \"leaveTypeName\": \"Emergency Leave\", \"certificateRequired\": false}',
        78,
        81,
        5,
        'REJECTED',
        41,
        'no',
        '2025-10-24 03:12:41',
        '2025-10-24 03:13:35'
    ),
    (
        87,
        7,
        'check1',
        '{\"otDate\": \"2025-10-28\", \"otType\": \"WEEKDAY\", \"reason\": \"check\", \"endTime\": \"20:00\", \"otHours\": 1.0, \"startTime\": \"19:00\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-24T03:16:05.736624400\", \"createdByManager\": false}',
        78,
        81,
        5,
        'APPROVED',
        42,
        'ok',
        '2025-10-24 03:16:06',
        '2025-10-24 03:33:13'
    ),
    (
        88,
        7,
        'check2',
        '{\"otDate\": \"2025-10-28\", \"otType\": \"WEEKDAY\", \"reason\": \"test\", \"endTime\": \"22:00\", \"otHours\": 1.0, \"startTime\": \"21:00\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-24T03:16:33.759157100\", \"createdByManager\": false}',
        78,
        81,
        5,
        'APPROVED',
        42,
        'check',
        '2025-10-24 03:16:34',
        '2025-10-24 03:32:33'
    ),
    (
        90,
        7,
        'check3',
        '{\"otDate\": \"2025-11-01\", \"otType\": \"WEEKEND\", \"reason\": \"check\", \"endTime\": \"12:00\", \"otHours\": 4.0, \"startTime\": \"08:00\", \"payMultiplier\": 2.0, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-24T04:20:32.941442200\", \"createdByManager\": false}',
        78,
        81,
        5,
        'REJECTED',
        41,
        'no',
        '2025-10-24 04:20:33',
        '2025-10-30 02:58:49'
    ),
    (
        91,
        7,
        'check4',
        '{\"otDate\": \"2025-11-01\", \"otType\": \"WEEKEND\", \"reason\": \"ok\", \"endTime\": \"12:00\", \"otHours\": 4.0, \"startTime\": \"08:00\", \"payMultiplier\": 2.0, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-24T04:22:32.653440\", \"createdByManager\": false}',
        42,
        53,
        5,
        'APPROVED',
        41,
        'ok',
        '2025-10-24 04:22:33',
        '2025-10-30 01:14:53'
    ),
    (
        92,
        7,
        'check4',
        '{\"otDate\": \"2025-11-02\", \"otType\": \"WEEKEND\", \"reason\": \"check\", \"endTime\": \"10:00\", \"otHours\": 2.0, \"startTime\": \"08:00\", \"payMultiplier\": 2.0, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-24T04:25:39.612808800\", \"createdByManager\": true, \"managerAccountId\": 42}',
        42,
        81,
        5,
        'REJECTED',
        78,
        'ok',
        '2025-10-24 04:25:40',
        '2025-10-24 04:47:21'
    ),
    (
        95,
        6,
        'check',
        '{\"reason\": \"check\", \"endDate\": \"2025-11-11T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-11-11T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"PM\", \"leaveTypeCode\": \"UNPAID\", \"leaveTypeName\": \"Unpaid Leave\", \"certificateRequired\": false}',
        78,
        81,
        5,
        'REJECTED',
        41,
        'ok',
        '2025-10-25 00:22:05',
        '2025-10-30 01:48:09'
    ),
    (
        96,
        9,
        'python dev recruit',
        '{\"jobType\": \"Full-time\", \"jobLevel\": \"JUNIOR\", \"quantity\": 1, \"maxSalary\": 2000000.0, \"minSalary\": 1000000.0, \"jobSummary\": \"1.8 Training Needs; 4.1 Human Resource; 4.2 Hardwares & Softwares\", \"salaryType\": \"Negotiable\", \"attachments\": [], \"positionName\": \"Backend Developer\", \"workingLocation\": \"Ho Chi Minh City, District 1\", \"recruitmentReason\": \"changing\"}',
        42,
        53,
        5,
        'APPROVED',
        44,
        'oke được đó DUYỆT',
        '2025-10-27 20:58:42',
        '2025-10-28 16:10:24'
    ),
    (
        97,
        7,
        'check',
        '{\"otDate\": \"2025-10-29\", \"otType\": \"WEEKDAY\", \"reason\": \"ok\", \"endTime\": \"21:00\", \"otHours\": 2.0, \"startTime\": \"19:00\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-28T01:23:27.045265100\", \"createdByManager\": false}',
        77,
        78,
        3,
        'REJECTED',
        72,
        'no',
        '2025-10-28 01:23:27',
        '2025-10-28 01:25:42'
    ),
    (
        99,
        7,
        'check',
        '{\"otDate\": \"2025-10-29\", \"otType\": \"WEEKDAY\", \"reason\": \"ok\", \"endTime\": \"21:00\", \"otHours\": 2.0, \"startTime\": \"19:00\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-28T01:30:08.727530700\", \"createdByManager\": false}',
        77,
        78,
        3,
        'APPROVED',
        72,
        'ok',
        '2025-10-28 01:30:09',
        '2025-10-28 01:30:30'
    ),
    (
        100,
        7,
        'check',
        '{\"otDate\": \"2025-10-30\", \"otType\": \"WEEKDAY\", \"reason\": \"ok\", \"endTime\": \"22:00\", \"otHours\": 2.0, \"startTime\": \"20:00\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-28T01:35:55.257983400\", \"createdByManager\": false}',
        77,
        78,
        3,
        'REJECTED',
        NULL,
        'Request expired (effective date passed). Auto-rejected by system.',
        '2025-10-28 01:35:55',
        '2025-10-30 00:29:06'
    ),
    (
        101,
        7,
        'check',
        '{\"otDate\": \"2025-11-01\", \"otType\": \"WEEKEND\", \"reason\": \"okkk\", \"endTime\": \"13:00\", \"otHours\": 4.0, \"startTime\": \"09:00\", \"payMultiplier\": 2.0, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-28T01:36:37.794795900\", \"createdByManager\": false}',
        77,
        78,
        3,
        'APPROVED',
        71,
        'ok',
        '2025-10-28 01:36:38',
        '2025-10-28 14:24:50'
    ),
    (
        102,
        7,
        'check4',
        '{\"otDate\": \"2025-11-11\", \"otType\": \"WEEKDAY\", \"reason\": \"ok\", \"endTime\": \"21:00\", \"otHours\": 2.0, \"startTime\": \"19:00\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-28T14:09:40.927208300\", \"createdByManager\": false}',
        72,
        71,
        3,
        'APPROVED',
        41,
        NULL,
        '2025-10-28 14:09:41',
        '2025-10-30 01:10:05'
    ),
    (
        103,
        7,
        'check4',
        '{\"otDate\": \"2025-11-04\", \"otType\": \"WEEKDAY\", \"reason\": \"pl\", \"endTime\": \"21:00\", \"otHours\": 2.0, \"startTime\": \"19:00\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-28T14:15:56.886963500\", \"createdByManager\": true, \"managerAccountId\": 72}',
        72,
        78,
        3,
        'REJECTED',
        77,
        'ok',
        '2025-10-28 14:15:57',
        '2025-10-28 14:16:52'
    ),
    (
        106,
        6,
        'check',
        '{\"reason\": \"ok\", \"endDate\": \"2025-10-30T23:59:59\", \"dayCount\": 1, \"isHalfDay\": false, \"startDate\": \"2025-10-30T00:00\", \"durationDays\": 1.0, \"leaveTypeCode\": \"PERSONAL\", \"leaveTypeName\": \"Personal Leave\", \"certificateRequired\": true}',
        57,
        63,
        6,
        'REJECTED',
        75,
        'no',
        '2025-10-28 14:43:00',
        '2025-10-28 15:54:05'
    ),
    (
        109,
        6,
        'check',
        '{\"reason\": \"ok\", \"endDate\": \"2025-10-31T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-10-31T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"PERSONAL\", \"leaveTypeName\": \"Personal Leave\", \"certificateRequired\": true}',
        57,
        63,
        6,
        'APPROVED',
        41,
        'ok',
        '2025-10-28 14:59:32',
        '2025-10-28 15:41:20'
    ),
    (
        110,
        6,
        'check',
        '{\"reason\": \"ok\", \"endDate\": \"2025-10-31T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-10-31T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"PM\", \"leaveTypeCode\": \"PERSONAL\", \"leaveTypeName\": \"Personal Leave\", \"certificateRequired\": true}',
        57,
        63,
        6,
        'REJECTED',
        75,
        'ok',
        '2025-10-28 15:07:01',
        '2025-10-28 16:02:03'
    ),
    (
        112,
        7,
        'check4',
        '{\"otDate\": \"2025-10-31\", \"otType\": \"WEEKDAY\", \"reason\": \"ok\", \"endTime\": \"21:00\", \"otHours\": 2.0, \"startTime\": \"19:00\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-28T15:26:21.941328600\", \"createdByManager\": false}',
        57,
        63,
        6,
        'REJECTED',
        75,
        'no',
        '2025-10-28 15:26:22',
        '2025-10-28 16:02:19'
    ),
    (
        113,
        7,
        'check4',
        '{\"otDate\": \"2025-10-31\", \"otType\": \"WEEKDAY\", \"reason\": \"ok\", \"endTime\": \"21:00\", \"otHours\": 2.0, \"startTime\": \"19:00\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-28T16:07:42.869070300\", \"createdByManager\": false}',
        57,
        63,
        6,
        'APPROVED',
        41,
        NULL,
        '2025-10-28 16:07:43',
        '2025-10-30 01:09:56'
    ),
    (
        115,
        6,
        'check',
        '{\"reason\": \"ok\", \"endDate\": \"2025-10-31T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-10-31T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"PERSONAL\", \"leaveTypeName\": \"Personal Leave\", \"certificateRequired\": true}',
        75,
        65,
        6,
        'APPROVED',
        41,
        'ok',
        '2025-10-28 16:37:18',
        '2025-10-30 01:09:44'
    ),
    (
        116,
        9,
        'Accountant Header Recruit',
        '{\"jobType\": \"Full-time\", \"jobLevel\": \"MIDDLE\", \"quantity\": 1, \"jobSummary\": \"accountant\", \"salaryType\": \"Gross\", \"positionName\": \"Accountant\", \"attachmentPath\": \"https://docs.google.com/spreadsheets/d/1t6HRMni8tnY4ULR8V9oKGaIUkSfQ9WGDgkvYMzlwoeU/edit?usp=drive_link\", \"workingLocation\": \"100 Main Street, San Francisco, CA\", \"recruitmentReason\": \"test\"}',
        42,
        53,
        5,
        'APPROVED',
        44,
        'oke do it',
        '2025-10-28 17:01:21',
        '2025-10-29 15:40:20'
    ),
    (
        117,
        9,
        'Accountant Header Recruit',
        '{\"jobType\": \"Full-time\", \"jobLevel\": \"MIDDLE\", \"quantity\": 1, \"jobSummary\": \"accountant\", \"salaryType\": \"Gross\", \"positionName\": \"Accountant\", \"attachmentPath\": \"https://docs.google.com/spreadsheets/d/1t6HRMni8tnY4ULR8V9oKGaIUkSfQ9WGDgkvYMzlwoeU/edit?usp=drive_link\", \"workingLocation\": \"100 Main Street, San Francisco, CA\", \"recruitmentReason\": \"test\"}',
        42,
        53,
        5,
        'APPROVED',
        44,
        NULL,
        '2025-10-28 17:01:32',
        '2025-10-29 15:40:30'
    ),
    (
        118,
        9,
        'Test job recruit',
        '{\"jobType\": \"Internship\", \"jobLevel\": \"SENIOR\", \"quantity\": 1, \"maxSalary\": 2000000.0, \"minSalary\": 1000000.0, \"jobSummary\": \"test\", \"salaryType\": \"Gross\", \"attachments\": [], \"positionName\": \"Test job\", \"workingLocation\": \"HRMS office, Hanoi\", \"recruitmentReason\": \"test he\"}',
        42,
        53,
        5,
        'APPROVED',
        44,
        NULL,
        '2025-10-28 17:02:43',
        '2025-10-29 15:41:11'
    ),
    (
        140,
        9,
        'as',
        '{\"jobType\": \"Internship\", \"jobLevel\": \"JUNIOR\", \"quantity\": 1, \"maxSalary\": 2000000000.0, \"minSalary\": 10000000.0, \"jobSummary\": \"test1\", \"salaryType\": \"Gross\", \"attachments\": [], \"positionName\": \"accountant\", \"workingLocation\": \"HRMS office, Hanoi\", \"recruitmentReason\": \"test2\"}',
        42,
        53,
        5,
        'APPROVED',
        44,
        'approve',
        '2025-10-29 15:08:43',
        '2025-10-29 15:10:51'
    ),
    (
        141,
        6,
        'check',
        '{\"reason\": \"ok\", \"endDate\": \"2025-11-02T23:59:59\", \"dayCount\": 2, \"isHalfDay\": false, \"startDate\": \"2025-10-30T00:00\", \"durationDays\": 2.0, \"leaveTypeCode\": \"PERSONAL\", \"leaveTypeName\": \"Personal Leave\", \"certificateRequired\": true}',
        70,
        77,
        2,
        'REJECTED',
        NULL,
        'Request expired (effective date passed). Auto-rejected by system.',
        '2025-10-29 15:18:13',
        '2025-10-30 00:29:06'
    ),
    (
        142,
        6,
        'check',
        '{\"reason\": \"ok\", \"endDate\": \"2025-11-03T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-11-03T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"EMERGENCY\", \"leaveTypeName\": \"Emergency Leave\", \"certificateRequired\": false}',
        70,
        77,
        2,
        'PENDING',
        73,
        NULL,
        '2025-10-29 15:21:00',
        '2025-10-29 15:21:01'
    ),
    (
        143,
        6,
        'check',
        '{\"reason\": \"ok\", \"endDate\": \"2025-11-03T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-11-03T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"PM\", \"leaveTypeCode\": \"EMERGENCY\", \"leaveTypeName\": \"Emergency Leave\", \"certificateRequired\": false}',
        70,
        77,
        2,
        'PENDING',
        73,
        NULL,
        '2025-10-29 15:22:33',
        '2025-10-29 15:22:34'
    ),
    (
        144,
        6,
        'check',
        '{\"reason\": \"ok\", \"endDate\": \"2025-11-04T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-11-04T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"PERSONAL\", \"leaveTypeName\": \"Personal Leave\", \"certificateRequired\": true}',
        70,
        77,
        2,
        'REJECTED',
        41,
        'no',
        '2025-10-29 15:23:38',
        '2025-10-30 02:59:15'
    ),
    (
        147,
        9,
        'Treasurer Recruit for Finance',
        '{\"jobType\": \"Full-time\", \"jobLevel\": \"SENIOR\", \"quantity\": 1, \"maxSalary\": 2000000.0, \"minSalary\": 1000000.0, \"jobSummary\": \"The Corporate Treasurer is the steward and custodian of an organization\'s monetary assets. Their primary responsibility is to manage the company\'s cash flow, banking relationships, and financial risks to ensure the organization has sufficient liquidity to meet its obligations and funding for its strategic objectives. The Treasurer is a strategic partner to the CFO and executive team, influencing major decisions on capital structure, investments, and risk mitigation.\", \"salaryType\": \"Negotiable\", \"positionName\": \"Treasurer \", \"attachmentPath\": \"https://drive.google.com/drive/folders/1ySreT6ksdStP_QPehPQs59kCS0xwsMS5?usp=drive_link\", \"workingLocation\": \"HRMS office, Ho Chi Minh City\", \"recruitmentReason\": \"changing people from project\"}',
        42,
        53,
        5,
        'APPROVED',
        44,
        NULL,
        '2025-10-29 15:36:51',
        '2025-10-29 15:41:18'
    ),
    (
        156,
        8,
        'ok',
        '{\"reason\": \"ok\", \"records\": [{\"newRecord\": {\"date\": \"2025-10-28\", \"status\": \"On Time\", \"checkIn\": \"09:30\", \"checkOut\": \"10:58\"}, \"oldRecord\": {\"date\": \"2025-10-28\", \"period\": \"October 2025\", \"source\": \"manual\", \"status\": \"On Time\", \"checkIn\": \"08:30\", \"checkOut\": \"00:58\"}}], \"detail_text\": \"ok\", \"attendanceDates\": [\"2025-10-28\"]}',
        71,
        80,
        1,
        'APPROVED',
        41,
        'ok',
        '2025-10-30 01:58:04',
        '2025-10-30 01:58:55'
    ),
    (
        157,
        8,
        '123',
        '{\"records\": [{\"newRecord\": {\"date\": \"2025-10-28\", \"status\": \"On Time\", \"checkIn\": \"07:01\", \"checkOut\": \"17:45\"}, \"oldRecord\": {\"date\": \"2025-10-28\", \"period\": \"October 2025\", \"source\": \"manual\", \"status\": \"On Time\", \"checkIn\": \"07:01\", \"checkOut\": \"17:01\"}}], \"detail_text\": \"123\", \"user_attachment_link\": \"https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0\"}',
        78,
        81,
        5,
        'REJECTED',
        41,
        'ok',
        '2025-10-30 02:02:39',
        '2025-10-30 02:12:22'
    ),
    (
        158,
        8,
        'appeal request',
        '{\"records\": [{\"newRecord\": {\"date\": \"2025-10-09\", \"status\": \"On Time\", \"checkIn\": \"07:20\", \"checkOut\": \"18:21\"}, \"oldRecord\": {\"date\": \"2025-10-09\", \"period\": \"October 2025\", \"source\": \"manual\", \"status\": \"Late\", \"checkIn\": \"09:20\", \"checkOut\": \"18:21\"}}], \"detail_text\": \"123\", \"appealStatus\": \"APPROVED\", \"user_attachment_link\": \"https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0\"}',
        78,
        81,
        5,
        'APPROVED',
        71,
        'ok',
        '2025-10-30 02:30:35',
        '2025-10-30 02:33:55'
    ),
    (
        159,
        8,
        'appea; request',
        '{\"records\": [{\"newRecord\": {\"date\": \"2025-10-08\", \"status\": \"On Time\", \"checkIn\": \"07:20\", \"checkOut\": \"17:30\"}, \"oldRecord\": {\"date\": \"2025-10-08\", \"period\": \"October 2025\", \"source\": \"manual\", \"status\": \"On Time\", \"checkIn\": \"08:20\", \"checkOut\": \"17:30\"}}], \"detail_text\": \"456\", \"appealStatus\": \"REJECTED\"}',
        78,
        81,
        5,
        'REJECTED',
        41,
        'ok',
        '2025-10-30 02:31:09',
        '2025-10-30 02:35:10'
    ),
    (
        162,
        8,
        '123',
        '{\"reason\": \"123\", \"records\": [{\"newRecord\": {\"date\": \"2025-10-08\", \"status\": \"On Time\", \"checkIn\": \"07:20\", \"checkOut\": \"17:30\"}, \"oldRecord\": {\"date\": \"2025-10-08\", \"period\": \"October 2025\", \"source\": \"manual\", \"status\": \"On Time\", \"checkIn\": \"08:20\", \"checkOut\": \"17:30\"}}], \"detail_text\": \"123\", \"appealStatus\": \"APPROVED\", \"attendanceDates\": [\"2025-10-08\"]}',
        78,
        81,
        5,
        'APPROVED',
        41,
        'ok',
        '2025-10-30 16:48:02',
        '2025-10-30 16:49:07'
    ),
    (
        163,
        8,
        '123',
        '{\"reason\": \"123\", \"records\": [{\"newRecord\": {\"date\": \"2025-10-08\", \"status\": \"Over Time\", \"checkIn\": \"17:00\", \"checkOut\": \"20:23\"}, \"oldRecord\": {\"date\": \"2025-10-08\", \"period\": \"October 2025\", \"source\": \"manual\", \"status\": \"Over Time\", \"checkIn\": \"18:00\", \"checkOut\": \"20:23\"}}], \"detail_text\": \"123\", \"attendanceDates\": [\"2025-10-08\"]}',
        78,
        81,
        5,
        'REJECTED',
        NULL,
        'Request expired (effective date passed). Auto-rejected by system.',
        '2025-10-30 16:50:31',
        '2025-10-30 16:50:33'
    ),
    (
        164,
        8,
        '456',
        '{\"reason\": \"456\", \"records\": [{\"newRecord\": {\"date\": \"2025-10-08\", \"status\": \"Over Time\", \"checkIn\": \"19:00\", \"checkOut\": \"21:23\"}, \"oldRecord\": {\"date\": \"2025-10-08\", \"period\": \"October 2025\", \"source\": \"manual\", \"status\": \"Over Time\", \"checkIn\": \"18:00\", \"checkOut\": \"20:23\"}}], \"detail_text\": \"456\", \"appealStatus\": \"APPROVED\", \"attendanceDates\": [\"2025-10-08\"]}',
        78,
        81,
        5,
        'APPROVED',
        41,
        'ok',
        '2025-10-30 16:52:10',
        '2025-10-30 16:52:38'
    ),
    (
        165,
        9,
        'Senior Backend Engineer - Project Alpha',
        '{\"jobType\": \"Full-time\", \"jobLevel\": \"JUNIOR\", \"quantity\": 1, \"maxSalary\": 2000000.0, \"minSalary\": 1000000.0, \"jobSummary\": \"test111111111111111111111111111111\", \"salaryType\": \"Net\", \"positionName\": \"Backend Developer\", \"workingLocation\": \"HRMS office, Hanoi\", \"recruitmentReason\": \"i need for resource\"}',
        42,
        53,
        5,
        'PENDING',
        NULL,
        NULL,
        '2025-10-30 20:16:24',
        '2025-10-30 20:16:24'
    ),
    (
        166,
        8,
        '678',
        '{\"reason\": \"678\", \"records\": [{\"newRecord\": {\"date\": \"2025-10-09\", \"status\": \"On Time\", \"checkIn\": \"07:00\", \"checkOut\": \"18:00\"}, \"oldRecord\": {\"date\": \"2025-10-09\", \"period\": \"October 2025\", \"source\": \"manual\", \"status\": \"Late\", \"checkIn\": \"07:20\", \"checkOut\": \"18:21\"}}, {\"newRecord\": {\"date\": \"2025-10-30\", \"status\": \"On Time\", \"checkIn\": \"08:01\", \"checkOut\": \"17:35\"}, \"oldRecord\": {\"date\": \"2025-10-30\", \"period\": \"October 2025\", \"source\": \"manual\", \"status\": \"On Time\", \"checkIn\": \"07:01\", \"checkOut\": \"17:01\"}}], \"detail_text\": \"678\", \"appealStatus\": \"APPROVED\", \"attachmentPath\": \"https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0\", \"attendanceDates\": [\"2025-10-09\", \"2025-10-30\"], \"user_attachment_link\": \"https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0\"}',
        78,
        81,
        5,
        'APPROVED',
        41,
        'ok',
        '2025-10-30 21:22:51',
        '2025-10-30 21:24:25'
    ),
    (
        167,
        9,
        'Front-end Developer',
        '{\"jobType\": \"Internship\", \"jobLevel\": \"JUNIOR\", \"quantity\": 1, \"maxSalary\": 2000000.0, \"minSalary\": 1000000.0, \"jobSummary\": \"test1\", \"salaryType\": \"Negotiable\", \"positionName\": \"Senior Software\", \"workingLocation\": \"HRMS office, Ho Chi Minh City\", \"recruitmentReason\": \"test2\"}',
        42,
        53,
        5,
        'PENDING',
        NULL,
        NULL,
        '2025-10-30 22:19:56',
        '2025-10-30 22:19:56'
    ),
    (
        179,
        9,
        'Accountant Header Recruit',
        '{\"jobType\": \"Full-time\", \"jobLevel\": \"SENIOR\", \"quantity\": 1, \"maxSalary\": 2000000.0, \"minSalary\": 1000000.0, \"jobSummary\": \"test\", \"salaryType\": \"Gross\", \"positionName\": \"Accountant\", \"attachmentPath\": \"https://docs.google.com/spreadsheets/d/1t6HRMni8tnY4ULR8V9oKGaIUkSfQ9WGDgkvYMzlwoeU/edit?usp=drive_link\", \"workingLocation\": \"HRMS office, Ho Chi Minh City\", \"recruitmentReason\": \"previous header go to jail\"}',
        42,
        53,
        5,
        'APPROVED',
        44,
        'oke',
        '2025-10-31 01:26:04',
        '2025-10-31 01:33:45'
    ),
    (
        180,
        8,
        'appeal',
        '{\"reason\": \"appeal\", \"records\": [{\"newRecord\": {\"date\": \"2025-10-09\", \"status\": \"On Time\", \"checkIn\": \"07:00\", \"checkOut\": \"18:00\"}, \"oldRecord\": {\"date\": \"2025-10-09\", \"period\": \"October 2025\", \"source\": \"manual\", \"status\": \"On time\", \"checkIn\": \"08:00\", \"checkOut\": \"18:00\"}}], \"detail_text\": \"appeal\", \"appealStatus\": \"APPROVED\", \"attachmentPath\": \"https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0\", \"attendanceDates\": [\"2025-10-09\"], \"user_attachment_link\": \"https://docs.google.com/document/d/1HkKCJzzwqKKTtKXvrk9x0kRm1sV81vmhO4mpFoMEQqg/edit?tab=t.0\"}',
        78,
        81,
        5,
        'APPROVED',
        41,
        'ok',
        '2025-10-31 02:17:23',
        '2025-10-31 02:19:34'
    ),
    (
        182,
        9,
        'as',
        '{\"jobType\": \"Full-time\", \"jobLevel\": \"JUNIOR\", \"quantity\": 1, \"jobSummary\": \"sdasd\", \"salaryType\": \"Gross\", \"positionName\": \"sleep\", \"attachmentPath\": \"https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing\", \"workingLocation\": \"HRMS office, Hanoi\", \"recruitmentReason\": \"\"}',
        42,
        53,
        5,
        'PENDING',
        NULL,
        NULL,
        '2025-10-31 02:54:39',
        '2025-10-31 02:54:39'
    ),
    (
        183,
        7,
        'check4',
        '{\"otDate\": \"2025-11-01\", \"otType\": \"WEEKEND\", \"reason\": \"ok\", \"endTime\": \"21:00\", \"otHours\": 2.0, \"startTime\": \"19:00\", \"payMultiplier\": 2.0, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-31T03:20:02.317025400\", \"createdByManager\": false}',
        42,
        53,
        5,
        'PENDING',
        82,
        NULL,
        '2025-10-31 03:20:02',
        '2025-10-31 03:20:05'
    ),
    (
        184,
        7,
        'check4',
        '{\"otDate\": \"2025-11-02\", \"otType\": \"WEEKEND\", \"reason\": \"ok\", \"endTime\": \"08:00\", \"otHours\": 1.0, \"startTime\": \"07:00\", \"payMultiplier\": 2.0, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-31T03:20:56.094790100\", \"createdByManager\": false}',
        42,
        53,
        5,
        'PENDING',
        82,
        NULL,
        '2025-10-31 03:20:56',
        '2025-10-31 03:20:59'
    ),
    (
        185,
        7,
        'check4',
        '{\"otDate\": \"2025-11-01\", \"otType\": \"WEEKEND\", \"reason\": \"ok\", \"endTime\": \"09:00\", \"otHours\": 3.0, \"startTime\": \"06:00\", \"payMultiplier\": 2.0, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-31T03:23:14.351185600\", \"createdByManager\": true, \"managerAccountId\": 42}',
        42,
        81,
        5,
        'PENDING',
        NULL,
        NULL,
        '2025-10-31 03:23:14',
        '2025-10-31 03:23:14'
    ),
    (
        186,
        7,
        'check4',
        '{\"otDate\": \"2025-11-01\", \"otType\": \"WEEKEND\", \"reason\": \"ok\", \"endTime\": \"22:00\", \"otHours\": 2.0, \"startTime\": \"20:00\", \"payMultiplier\": 2.0, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-31T03:24:16.547266700\", \"createdByManager\": true, \"managerAccountId\": 42}',
        42,
        66,
        5,
        'PENDING',
        NULL,
        NULL,
        '2025-10-31 03:24:17',
        '2025-10-31 03:24:17'
    );
/*!40000 ALTER TABLE `requests` ENABLE KEYS */
;

--
-- Table structure for table `role_features`
--

DROP TABLE IF EXISTS `role_features`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `role_features` (
    `role_id` bigint NOT NULL,
    `feature_id` bigint NOT NULL,
    PRIMARY KEY (`role_id`, `feature_id`),
    KEY `FK_rf_feature` (`feature_id`),
    CONSTRAINT `FK_rf_feature` FOREIGN KEY (`feature_id`) REFERENCES `features` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_rf_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `role_features`
--

/*!40000 ALTER TABLE `role_features` DISABLE KEYS */
;
/*!40000 ALTER TABLE `role_features` ENABLE KEYS */
;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `roles` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `priority` int NOT NULL DEFAULT '0',
    `is_system` tinyint(1) NOT NULL DEFAULT '0',
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    UNIQUE KEY `code` (`code`)
) ENGINE = InnoDB AUTO_INCREMENT = 20 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `roles`
--

/*!40000 ALTER TABLE `roles` DISABLE KEYS */
;
INSERT INTO
    `roles`
VALUES (
        10,
        'ADMIN',
        'Admin',
        100,
        1,
        '2025-10-16 09:33:20',
        '2025-10-16 09:33:20'
    ),
    (
        11,
        'HR_MANAGER',
        'HR Manager',
        90,
        1,
        '2025-10-16 09:33:20',
        '2025-10-16 09:33:20'
    ),
    (
        12,
        'HR_STAFF',
        'HR Staff',
        80,
        1,
        '2025-10-16 09:33:20',
        '2025-10-16 09:33:20'
    ),
    (
        13,
        'DEPT_MANAGER',
        'Department Manager',
        70,
        1,
        '2025-10-16 09:33:20',
        '2025-10-16 09:33:20'
    ),
    (
        14,
        'EMPLOYEE',
        'Employee',
        50,
        1,
        '2025-10-16 09:33:20',
        '2025-10-16 09:33:20'
    ),
    (
        15,
        'GUEST',
        'Guest',
        0,
        0,
        '2025-10-18 05:57:08',
        '2025-10-18 05:57:08'
    );
/*!40000 ALTER TABLE `roles` ENABLE KEYS */
;

--
-- Table structure for table `salary_history`
--

DROP TABLE IF EXISTS `salary_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `salary_history` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `amount` decimal(18, 2) NOT NULL,
    `currency` varchar(8) DEFAULT NULL,
    `effective_from` date NOT NULL,
    `effective_to` date DEFAULT NULL,
    `reason` varchar(255) DEFAULT NULL,
    `created_by_account_id` bigint DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    UNIQUE KEY `UQ_salary_period` (`user_id`, `effective_from`),
    KEY `FK_salary_created_by` (`created_by_account_id`),
    CONSTRAINT `FK_salary_created_by` FOREIGN KEY (`created_by_account_id`) REFERENCES `accounts` (`id`),
    CONSTRAINT `FK_salary_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `salary_history`
--

/*!40000 ALTER TABLE `salary_history` DISABLE KEYS */
;
/*!40000 ALTER TABLE `salary_history` ENABLE KEYS */
;

--
-- Table structure for table `system_parameters`
--

DROP TABLE IF EXISTS `system_parameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `system_parameters` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `scope_type` varchar(16) NOT NULL DEFAULT 'GLOBAL',
    `scope_id` bigint DEFAULT NULL,
    `namespace` varchar(64) NOT NULL,
    `param_key` varchar(64) NOT NULL,
    `value_json` json NOT NULL,
    `description` varchar(255) DEFAULT NULL,
    `updated_by_account_id` bigint DEFAULT NULL,
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    UNIQUE KEY `UQ_sp` (
        `scope_type`,
        `scope_id`,
        `namespace`,
        `param_key`
    ),
    KEY `FK_sp_updated_by` (`updated_by_account_id`),
    CONSTRAINT `FK_sp_updated_by` FOREIGN KEY (`updated_by_account_id`) REFERENCES `accounts` (`id`),
    CONSTRAINT `system_parameters_chk_1` CHECK (json_valid(`value_json`))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `system_parameters`
--

/*!40000 ALTER TABLE `system_parameters` DISABLE KEYS */
;
/*!40000 ALTER TABLE `system_parameters` ENABLE KEYS */
;

--
-- Table structure for table `timesheet_periods`
--

DROP TABLE IF EXISTS `timesheet_periods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `timesheet_periods` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `date_start` date NOT NULL,
    `date_end` date NOT NULL,
    `is_locked` tinyint(1) NOT NULL DEFAULT '0',
    `locked_by` bigint DEFAULT NULL,
    `locked_at` datetime DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    UNIQUE KEY `UQ_tsp` (`date_start`, `date_end`),
    KEY `FK_tsp_locked_by` (`locked_by`),
    CONSTRAINT `FK_tsp_locked_by` FOREIGN KEY (`locked_by`) REFERENCES `accounts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 17 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `timesheet_periods`
--

/*!40000 ALTER TABLE `timesheet_periods` DISABLE KEYS */
;
INSERT INTO
    `timesheet_periods`
VALUES (
        3,
        'January 2025',
        '2025-01-01',
        '2025-01-31',
        0,
        NULL,
        NULL,
        '2025-10-26 16:12:12'
    ),
    (
        4,
        'February 2025',
        '2025-02-01',
        '2025-02-28',
        0,
        NULL,
        NULL,
        '2025-10-26 16:12:12'
    ),
    (
        5,
        'March 2025',
        '2025-03-01',
        '2025-03-31',
        0,
        NULL,
        NULL,
        '2025-10-26 16:12:12'
    ),
    (
        6,
        'April 2025',
        '2025-04-01',
        '2025-04-30',
        0,
        NULL,
        NULL,
        '2025-10-26 16:12:12'
    ),
    (
        7,
        'May 2025',
        '2025-05-01',
        '2025-05-31',
        0,
        NULL,
        NULL,
        '2025-10-26 16:12:12'
    ),
    (
        8,
        'June 2025',
        '2025-06-01',
        '2025-06-30',
        0,
        NULL,
        NULL,
        '2025-10-26 16:12:12'
    ),
    (
        9,
        'July 2025',
        '2025-07-01',
        '2025-07-31',
        0,
        NULL,
        NULL,
        '2025-10-26 16:12:12'
    ),
    (
        10,
        'August 2025',
        '2025-08-01',
        '2025-08-31',
        0,
        NULL,
        NULL,
        '2025-10-26 16:12:12'
    ),
    (
        11,
        'September 2025',
        '2025-09-01',
        '2025-09-30',
        0,
        NULL,
        NULL,
        '2025-10-26 16:12:12'
    ),
    (
        12,
        'October 2025',
        '2025-10-01',
        '2025-10-31',
        0,
        NULL,
        NULL,
        '2025-10-18 03:10:46'
    ),
    (
        13,
        'November 2025',
        '2025-11-01',
        '2025-11-30',
        0,
        NULL,
        NULL,
        '2025-10-16 11:07:05'
    ),
    (
        14,
        'December 2025',
        '2025-12-01',
        '2025-12-31',
        0,
        NULL,
        NULL,
        '2025-10-16 11:07:05'
    ),
    (
        15,
        'January 2026',
        '2026-01-01',
        '2026-01-31',
        0,
        NULL,
        NULL,
        '2025-10-16 11:07:05'
    ),
    (
        16,
        'February 2026',
        '2026-02-01',
        '2026-02-28',
        0,
        NULL,
        NULL,
        '2025-10-16 11:07:05'
    );
/*!40000 ALTER TABLE `timesheet_periods` ENABLE KEYS */
;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `users` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `employee_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `dob` date DEFAULT NULL,
    `gender` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `hometown` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `cccd` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `cccd_issued_date` date DEFAULT NULL,
    `cccd_issued_place` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `cccd_front_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `cccd_back_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `email_company` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `department_id` bigint DEFAULT NULL,
    `position_id` bigint DEFAULT NULL,
    `status` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
    `date_joined` date DEFAULT NULL,
    `date_left` date DEFAULT NULL,
    `start_work_date` date DEFAULT NULL,
    `address_line1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `address_line2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `city` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `state` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `postal_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `country` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `application_id` bigint DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `cccd_expired_date` date DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `employee_code` (`employee_code`),
    UNIQUE KEY `cccd` (`cccd`),
    UNIQUE KEY `email_company` (`email_company`),
    UNIQUE KEY `UQ_users_application_id` (`application_id`),
    KEY `FK_users_dept` (`department_id`),
    KEY `FK_users_pos` (`position_id`),
    KEY `IX_users_status` (`status`),
    KEY `IX_users_filter` (
        `status`,
        `department_id`,
        `position_id`
    ),
    KEY `idx_users_employee_code` (`employee_code`),
    KEY `idx_users_email_company` (`email_company`),
    KEY `idx_users_phone` (`phone`),
    KEY `idx_users_full_name` (`full_name`),
    KEY `idx_users_dept_status` (`department_id`, `status`),
    KEY `idx_users_pos_status` (`position_id`, `status`),
    KEY `idx_users_dept_pos_status` (
        `department_id`,
        `position_id`,
        `status`
    ),
    KEY `idx_users_status` (`status`),
    KEY `idx_users_created_at` (`created_at`),
    KEY `idx_users_date_joined` (`date_joined`),
    KEY `idx_users_status_gender` (`status`, `gender`),
    KEY `idx_users_gender` (`gender`),
    KEY `idx_users_search_employee_code` (`employee_code`),
    KEY `idx_users_search_full_name` (`full_name` (100)),
    KEY `idx_users_created_status` (`created_at` DESC, `status`),
    CONSTRAINT `FK_users_application` FOREIGN KEY (`application_id`) REFERENCES `applications` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_users_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_users_pos` FOREIGN KEY (`position_id`) REFERENCES `positions` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 87 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping data for table `users`
--

/*!40000 ALTER TABLE `users` DISABLE KEYS */
;
INSERT INTO
    `users`
VALUES (
        51,
        'ADMIN001',
        'Vũ Ngọc Dương',
        '1965-10-28',
        'male',
        'Nam Dinh',
        '001305607899',
        '2021-12-05',
        'Cục cảnh sát',
        NULL,
        NULL,
        'vduong2709@gmail.com',
        '0888888888',
        9,
        6,
        'active',
        '2025-10-15',
        NULL,
        '2025-10-15',
        'Phu Thuong, Tay Ho, Ha Noi',
        'Nam Dinh',
        'Hà Nội',
        NULL,
        '10000',
        'Vietnam',
        NULL,
        '2025-10-15 15:42:38',
        '2025-10-30 20:29:42',
        '2026-10-30'
    ),
    (
        52,
        'EMP000052',
        'Nguyễn Công Hiếu',
        NULL,
        'female',
        '',
        '001205003333',
        NULL,
        '',
        NULL,
        NULL,
        'hieucong2468@gmail.com',
        '0123456780',
        1,
        7,
        'active',
        '2025-10-08',
        NULL,
        '2025-10-09',
        '',
        '',
        '',
        '',
        '',
        '',
        NULL,
        '2025-10-16 20:18:32',
        '2025-10-23 21:58:53',
        NULL
    ),
    (
        53,
        'IT000024',
        'Nguyễn Trung Hiếu',
        NULL,
        'female',
        '',
        '',
        NULL,
        '',
        NULL,
        NULL,
        'trunghieu999909@gmail.com',
        '0123456789',
        5,
        9,
        'active',
        '2025-10-01',
        NULL,
        '2025-10-01',
        '',
        '',
        '',
        '',
        '',
        'Vietnam',
        NULL,
        '2025-10-16 20:32:46',
        '2025-10-21 18:26:10',
        NULL
    ),
    (
        55,
        'EMP000055',
        'Nguyễn Đức Dương',
        '2005-02-09',
        'male',
        'Sơn Tây',
        '012345678901',
        '2025-02-09',
        'Cuc canh sat',
        NULL,
        NULL,
        'duongnguyen291105@gmail.com',
        '0111111111',
        1,
        8,
        'active',
        '2025-10-10',
        NULL,
        '2025-10-10',
        'Au Co, Tay Ho',
        'Ngo Quyen, Son Tay',
        'Hà Nội',
        NULL,
        '10000',
        'Vietnam',
        NULL,
        '2025-10-17 06:17:32',
        '2025-10-29 08:35:08',
        NULL
    ),
    (
        56,
        'EP001',
        'Trần Văn Cao',
        NULL,
        'Male',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'vancaotran@gmail.com',
        '0982188455',
        2,
        10,
        'active',
        '2025-10-09',
        NULL,
        '2025-10-09',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-17 06:45:29',
        '2025-10-22 19:51:44',
        NULL
    ),
    (
        57,
        'EP002',
        'Nguyễn Thanh Minh',
        NULL,
        'Male',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'thanhminhnguyen@gmail.com',
        '0111111113',
        5,
        10,
        'active',
        '2025-10-01',
        NULL,
        '2025-10-01',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-17 06:58:19',
        '2025-10-20 16:22:22',
        NULL
    ),
    (
        60,
        'HE0045',
        'HR Test',
        NULL,
        'Female',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'hr@gmail.com',
        '0111111115',
        1,
        8,
        'active',
        '2025-10-19',
        NULL,
        '2025-10-20',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-20 16:25:01',
        '2025-10-22 19:50:23',
        NULL
    ),
    (
        61,
        'HE0046',
        'Mai Phú Trọng',
        NULL,
        'Male',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'trong@gmail.com',
        '01111111115',
        5,
        10,
        'active',
        '2025-10-08',
        NULL,
        '2025-10-07',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-20 16:53:14',
        '2025-10-22 19:50:14',
        NULL
    ),
    (
        62,
        'HE0047',
        'Hoàng',
        NULL,
        'Male',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'hoang@gmail.com',
        '01111111116',
        5,
        10,
        'active',
        '2025-10-14',
        NULL,
        '2025-10-21',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-20 17:53:16',
        '2025-10-22 19:50:06',
        NULL
    ),
    (
        63,
        'HE0048',
        'Tùng',
        NULL,
        'Male',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'tung@gmail.com',
        '01111111117',
        6,
        10,
        'active',
        '2025-10-14',
        NULL,
        '2025-10-16',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-20 17:57:12',
        '2025-10-22 19:49:58',
        NULL
    ),
    (
        64,
        'HE0049',
        'Vũ',
        NULL,
        'female',
        '',
        '000000000000',
        NULL,
        '',
        NULL,
        NULL,
        'vu@gmail.com',
        '01111111118',
        6,
        10,
        'active',
        '2025-10-22',
        NULL,
        '2025-10-22',
        '',
        '',
        '',
        '',
        '',
        '',
        NULL,
        '2025-10-20 18:10:39',
        '2025-10-22 09:05:28',
        NULL
    ),
    (
        65,
        'HE0050',
        'Ngọc Hân',
        NULL,
        'Female',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'ngoc@gmail.com',
        '01111111119',
        6,
        9,
        'active',
        '2025-10-22',
        NULL,
        '2025-10-22',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-20 18:11:31',
        '2025-10-22 19:49:50',
        NULL
    ),
    (
        66,
        'HE0051',
        'Dũng',
        NULL,
        'male',
        '',
        '001200000000',
        NULL,
        '',
        NULL,
        NULL,
        'dung@gmail.com',
        '01111111120',
        5,
        10,
        'active',
        '2025-10-22',
        NULL,
        '2025-10-22',
        '',
        '',
        '',
        '',
        '',
        '',
        NULL,
        '2025-10-21 16:24:41',
        '2025-10-22 09:00:14',
        NULL
    ),
    (
        67,
        'HE0052',
        'Tài',
        NULL,
        'Male',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'tai@gmail.com',
        '01111111121',
        2,
        9,
        'active',
        '2025-10-22',
        NULL,
        '2025-10-22',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-21 16:26:10',
        '2025-10-22 19:49:41',
        NULL
    ),
    (
        68,
        'HE0053',
        'Tuấn',
        NULL,
        'male',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'tuan@gmail.com',
        '0111111112',
        2,
        10,
        'active',
        '2019-10-21',
        NULL,
        '2019-10-21',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-21 16:27:08',
        '2025-10-30 17:51:12',
        NULL
    ),
    (
        69,
        'HE0054',
        'Ngân',
        NULL,
        'Female',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'ngan@gmail.com',
        '01111111123',
        4,
        9,
        'active',
        '2025-10-21',
        NULL,
        '2025-10-21',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-21 16:28:09',
        '2025-10-22 19:49:24',
        NULL
    ),
    (
        70,
        'HE0055',
        'Hà',
        NULL,
        'Female',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'ha@gmail.com',
        '01111111124',
        6,
        10,
        'active',
        '2025-10-21',
        NULL,
        '2025-10-21',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-21 16:29:14',
        '2025-10-22 19:49:10',
        NULL
    ),
    (
        71,
        'HE0056',
        'Trang',
        '2025-10-23',
        'female',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'trang@gmail.com',
        '01111111125',
        3,
        9,
        'active',
        '2025-10-21',
        NULL,
        '2025-10-21',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'Vietnam',
        NULL,
        '2025-10-21 16:30:21',
        '2025-10-22 18:46:20',
        NULL
    ),
    (
        72,
        'HE0057',
        'Tạ Hiếu',
        NULL,
        'male',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'tahieu@gmail.com',
        '0111111112',
        3,
        10,
        'active',
        '2025-10-21',
        NULL,
        '2025-10-21',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-21 16:31:07',
        '2025-10-30 19:59:11',
        NULL
    ),
    (
        73,
        'HE0058',
        'Lực Dương',
        NULL,
        'male',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'lucduong@gmail.com',
        '0111111112',
        4,
        10,
        'active',
        '2025-10-21',
        NULL,
        '2025-10-21',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-21 16:34:12',
        '2025-10-30 19:59:02',
        NULL
    ),
    (
        74,
        'HE0059',
        'ADMIN2',
        NULL,
        'male',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'admin2@gmail.com',
        '0982188499',
        9,
        6,
        'active',
        '2025-10-21',
        NULL,
        '2025-10-21',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-21 17:25:41',
        '2025-10-30 16:02:36',
        NULL
    ),
    (
        75,
        'HE0060',
        'sahur',
        NULL,
        'male',
        '',
        '001205009522',
        NULL,
        '',
        NULL,
        NULL,
        'hihihiihi@gmail.com',
        '0000000009',
        5,
        10,
        'active',
        '2025-10-22',
        NULL,
        '2025-10-22',
        '',
        '',
        '',
        '',
        '',
        '',
        NULL,
        '2025-10-22 09:15:01',
        '2025-10-22 09:17:49',
        NULL
    ),
    (
        76,
        'HE0061',
        'sahur',
        NULL,
        'male',
        '',
        '000011112222',
        NULL,
        '',
        NULL,
        NULL,
        'konami98@gmail.com',
        '1133113311',
        4,
        10,
        'active',
        '2025-10-22',
        NULL,
        '2025-10-22',
        '',
        '',
        '',
        '',
        '',
        '',
        NULL,
        '2025-10-22 09:23:08',
        '2025-10-22 09:27:56',
        NULL
    ),
    (
        77,
        'HE0062',
        'sahursahur',
        NULL,
        'female',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'konami97@gmail.com',
        '00000000008',
        2,
        10,
        'active',
        '2025-10-22',
        NULL,
        '2025-10-22',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-22 10:09:58',
        '2025-10-22 10:26:19',
        NULL
    ),
    (
        78,
        'HE0063',
        'Lâm',
        NULL,
        'Male',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'lam@gmail.com',
        '01111111140',
        3,
        10,
        'active',
        '2025-10-22',
        NULL,
        '2025-10-22',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-22 14:24:11',
        '2025-10-22 19:44:12',
        NULL
    ),
    (
        79,
        'HE0064',
        'Đức Anh',
        NULL,
        'Male',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'ducanh@gmail.com',
        '0982188455',
        5,
        10,
        'active',
        '2025-10-15',
        NULL,
        '2025-10-22',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-22 14:25:06',
        '2025-10-22 19:44:02',
        NULL
    ),
    (
        80,
        'HE0065',
        'Mạnh',
        NULL,
        'male',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'manh@gmail.com',
        '0333666123',
        1,
        8,
        'active',
        '2025-10-21',
        NULL,
        '2025-10-22',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-22 18:30:22',
        '2025-10-22 18:30:22',
        NULL
    ),
    (
        81,
        'HE0066',
        'Khanh',
        NULL,
        'male',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'khanh@gmail.com',
        '0982188446',
        5,
        10,
        'active',
        '2025-10-22',
        NULL,
        '2025-10-22',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-23 16:01:17',
        '2025-10-30 19:58:53',
        NULL
    ),
    (
        82,
        'HE0067',
        'Thảo',
        NULL,
        'female',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'thao@gmail.com',
        '0333156498',
        1,
        8,
        'active',
        '2025-10-21',
        NULL,
        '2025-10-22',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-24 06:26:49',
        '2025-10-24 06:26:49',
        NULL
    ),
    (
        84,
        'HE0069',
        'Nhi',
        NULL,
        'female',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'nhi@gmail.com',
        '0897256423',
        6,
        10,
        'active',
        '2025-10-22',
        NULL,
        '2025-10-24',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-26 10:43:07',
        '2025-10-26 10:43:07',
        NULL
    ),
    (
        86,
        'HE0070',
        'Admin 3',
        NULL,
        'female',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'admin3@gmail.com',
        '0982188488',
        9,
        6,
        'active',
        '2025-10-08',
        NULL,
        '2025-10-15',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        '2025-10-30 20:09:44',
        '2025-10-30 20:09:44',
        NULL
    );
/*!40000 ALTER TABLE `users` ENABLE KEYS */
;

--
-- Dumping routines for database 'hrms'
--
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */
;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */
;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */
;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */
;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */
;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */
;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */
;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */
;

-- Dump completed on 2025-10-31  3:36:10