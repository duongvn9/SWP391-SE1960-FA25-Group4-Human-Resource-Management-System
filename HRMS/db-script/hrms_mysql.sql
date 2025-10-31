-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 36.50.135.207    Database: hrms
-- ------------------------------------------------------
-- Server version	8.0.36

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
    `effect` enum('GRANT', 'DENY') COLLATE utf8mb4_unicode_ci NOT NULL,
    PRIMARY KEY (`account_id`, `feature_id`),
    KEY `FK_af_feature` (`feature_id`),
    CONSTRAINT `FK_af_account` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_af_feature` FOREIGN KEY (`feature_id`) REFERENCES `features` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
    `username` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `email_login` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `status` varchar(24) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
    `failed_attempts` int NOT NULL DEFAULT '0',
    `last_login_at` datetime DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`),
    UNIQUE KEY `email_login` (`email_login`),
    KEY `IX_accounts_user` (`user_id`),
    CONSTRAINT `FK_accounts_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 40 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
    `status` varchar(24) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'new',
    `note` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `full_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `phone` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `dob` date DEFAULT NULL,
    `gender` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `hometown` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `address_line1` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `address_line2` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `city` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `state` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `postal_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `country` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `resume_path` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `cccd` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `cccd_issued_date` date DEFAULT NULL,
    `cccd_issued_place` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `cccd_front_path` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `cccd_back_path` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    KEY `IX_app_job_email` (`job_id`, `email`),
    KEY `IX_app_job_cccd` (`job_id`, `cccd`),
    CONSTRAINT `FK_app_job` FOREIGN KEY (`job_id`) REFERENCES `job_postings` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
    `path` varchar(1024) NOT NULL,
    `original_name` varchar(255) NOT NULL,
    `content_type` varchar(128) DEFAULT NULL,
    `size_bytes` bigint DEFAULT NULL,
    `checksum_sha256` char(64) DEFAULT NULL,
    `uploaded_by_account_id` bigint DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    KEY `IX_attach_owner` (`owner_type`, `owner_id`),
    KEY `FK_attach_uploader` (`uploaded_by_account_id`),
    CONSTRAINT `FK_attach_uploader` FOREIGN KEY (`uploaded_by_account_id`) REFERENCES `accounts` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
    `check_type` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL,
    `checked_at` datetime NOT NULL,
    `source` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `period_id` bigint DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    KEY `IX_att_user_time` (`user_id`, `checked_at`),
    KEY `FK_att_period` (`period_id`),
    CONSTRAINT `FK_att_period` FOREIGN KEY (`period_id`) REFERENCES `timesheet_periods` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_att_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 153 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
    `provider` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
    `provider_user_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `email_verified` tinyint(1) NOT NULL DEFAULT '0',
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    UNIQUE KEY `UQ_auth_provider_subject` (
        `provider`,
        `provider_user_id`
    ),
    KEY `IX_auth_account` (`account_id`),
    CONSTRAINT `FK_identity_account` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 38 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
    `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `password_updated_at` datetime DEFAULT NULL,
    PRIMARY KEY (`identity_id`),
    CONSTRAINT `FK_local_identity` FOREIGN KEY (`identity_id`) REFERENCES `auth_identities` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
    `effect` enum('GRANT', 'DENY') COLLATE utf8mb4_unicode_ci NOT NULL,
    PRIMARY KEY (`department_id`, `feature_id`),
    KEY `FK_df_feature` (`feature_id`),
    CONSTRAINT `FK_df_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_df_feature` FOREIGN KEY (`feature_id`) REFERENCES `features` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
    `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `head_account_id` bigint DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    KEY `FK_dept_head` (`head_account_id`),
    CONSTRAINT `FK_dept_head` FOREIGN KEY (`head_account_id`) REFERENCES `accounts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
    `code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
    `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
    `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `route` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
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
) ENGINE = InnoDB AUTO_INCREMENT = 2 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
    PRIMARY KEY (`id`),
    UNIQUE KEY `UQ_holidays` (`calendar_id`, `date_holiday`),
    CONSTRAINT `FK_holidays_calendar` FOREIGN KEY (`calendar_id`) REFERENCES `holiday_calendar` (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 13 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
    `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `department_id` bigint DEFAULT NULL,
    `description` text COLLATE utf8mb4_unicode_ci,
    `status` varchar(24) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft',
    `published_at` datetime DEFAULT NULL,
    `created_by_account_id` bigint DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    KEY `FK_job_request` (`request_id`),
    KEY `FK_job_dept` (`department_id`),
    KEY `FK_job_creator` (`created_by_account_id`),
    CONSTRAINT `FK_job_creator` FOREIGN KEY (`created_by_account_id`) REFERENCES `accounts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_job_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_job_request` FOREIGN KEY (`request_id`) REFERENCES `requests` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
    `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
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
-- Table structure for table `leave_types`
--

DROP TABLE IF EXISTS `leave_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `leave_types` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
    `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
    `description` text COLLATE utf8mb4_unicode_ci,
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
) ENGINE = InnoDB AUTO_INCREMENT = 7 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
    `currency` varchar(8) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `gross_amount` decimal(18, 2) NOT NULL DEFAULT '0.00',
    `net_amount` decimal(18, 2) NOT NULL DEFAULT '0.00',
    `details_json` json DEFAULT NULL,
    `file_path` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `status` varchar(24) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'approved',
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
-- Table structure for table `positions`
--

DROP TABLE IF EXISTS `positions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `positions` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
    `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `job_level` int DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    UNIQUE KEY `code` (`code`)
) ENGINE = InnoDB AUTO_INCREMENT = 6 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
    `from_status` varchar(24) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `to_status` varchar(24) COLLATE utf8mb4_unicode_ci NOT NULL,
    `action` varchar(24) COLLATE utf8mb4_unicode_ci NOT NULL,
    `actor_account_id` bigint NOT NULL,
    `actor_user_id` bigint NOT NULL,
    `note` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
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
-- Table structure for table `request_types`
--

DROP TABLE IF EXISTS `request_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `request_types` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
    `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    UNIQUE KEY `code` (`code`)
) ENGINE = InnoDB AUTO_INCREMENT = 6 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
    `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `detail` json DEFAULT NULL,
    `created_by_account_id` bigint NOT NULL,
    `created_by_user_id` bigint NOT NULL,
    `department_id` bigint DEFAULT NULL,
    `status` varchar(24) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DRAFT',
    `current_approver_account_id` bigint DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    KEY `IX_requests_type_status` (`request_type_id`, `status`),
    KEY `IX_requests_creator` (`created_by_account_id`),
    KEY `FK_requests_creator_user` (`created_by_user_id`),
    KEY `FK_requests_dept` (`department_id`),
    KEY `FK_requests_current_approver` (`current_approver_account_id`),
    CONSTRAINT `FK_requests_creator_account` FOREIGN KEY (`created_by_account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_requests_creator_user` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_requests_current_approver` FOREIGN KEY (`current_approver_account_id`) REFERENCES `accounts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_requests_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_requests_type` FOREIGN KEY (`request_type_id`) REFERENCES `request_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 16 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `roles` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
    `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
    `priority` int NOT NULL DEFAULT '0',
    `is_system` tinyint(1) NOT NULL DEFAULT '0',
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    UNIQUE KEY `code` (`code`)
) ENGINE = InnoDB AUTO_INCREMENT = 10 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
-- Table structure for table `timesheet_periods`
--

DROP TABLE IF EXISTS `timesheet_periods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */
;
/*!50503 SET character_set_client = utf8mb4 */
;
CREATE TABLE `timesheet_periods` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
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
) ENGINE = InnoDB AUTO_INCREMENT = 4 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
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
    `employee_code` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `full_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `dob` date DEFAULT NULL,
    `gender` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `hometown` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `cccd` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `cccd_issued_date` date DEFAULT NULL,
    `cccd_issued_place` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `cccd_front_path` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `cccd_back_path` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `email_company` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `phone` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `department_id` bigint DEFAULT NULL,
    `position_id` bigint DEFAULT NULL,
    `status` varchar(24) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
    `date_joined` date DEFAULT NULL,
    `date_left` date DEFAULT NULL,
    `start_work_date` date DEFAULT NULL,
    `address_line1` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `address_line2` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `city` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `state` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `postal_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `country` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `application_id` bigint DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    `updated_at` datetime NOT NULL DEFAULT(utc_timestamp()),
    PRIMARY KEY (`id`),
    UNIQUE KEY `employee_code` (`employee_code`),
    UNIQUE KEY `cccd` (`cccd`),
    UNIQUE KEY `email_company` (`email_company`),
    UNIQUE KEY `UQ_users_application_id` (`application_id`),
    KEY `FK_users_dept` (`department_id`),
    KEY `FK_users_pos` (`position_id`),
    CONSTRAINT `FK_users_application` FOREIGN KEY (`application_id`) REFERENCES `applications` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_users_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_users_pos` FOREIGN KEY (`position_id`) REFERENCES `positions` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 51 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */
;

--
-- Dumping routines for database 'hrms'
--
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

-- Dump completed on 2025-10-13 21:55:43