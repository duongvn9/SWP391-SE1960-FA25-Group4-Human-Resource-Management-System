-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: database-1.cnkww8swwrks.ap-southeast-1.rds.amazonaws.com    Database: hrms
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '';

--
-- Table structure for table `account_features`
--

DROP TABLE IF EXISTS `account_features`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_features` (
  `account_id` bigint NOT NULL,
  `feature_id` bigint NOT NULL,
  `effect` enum('GRANT','DENY') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`account_id`,`feature_id`),
  KEY `FK_af_feature` (`feature_id`),
  CONSTRAINT `FK_af_account` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_af_feature` FOREIGN KEY (`feature_id`) REFERENCES `features` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_features`
--

/*!40000 ALTER TABLE `account_features` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_features` ENABLE KEYS */;

--
-- Table structure for table `account_roles`
--

DROP TABLE IF EXISTS `account_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_roles` (
  `account_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `assigned_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `assigned_by` bigint DEFAULT NULL,
  PRIMARY KEY (`account_id`,`role_id`),
  KEY `fk_account_roles_assigned_by` (`assigned_by`),
  KEY `idx_account_roles_account` (`account_id`),
  KEY `idx_account_roles_role` (`role_id`),
  KEY `idx_account_roles_assigned_at` (`assigned_at`),
  CONSTRAINT `fk_account_roles_account` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_account_roles_assigned_by` FOREIGN KEY (`assigned_by`) REFERENCES `accounts` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_account_roles_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Many-to-many relationship between accounts and roles for RBAC';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_roles`
--

/*!40000 ALTER TABLE `account_roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_roles` ENABLE KEYS */;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accounts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `username` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email_login` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
  `failed_attempts` int NOT NULL DEFAULT '0',
  `last_login_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  `updated_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email_login` (`email_login`),
  KEY `IX_accounts_user` (`user_id`),
  KEY `IX_accounts_filter` (`status`,`user_id`),
  KEY `IX_accounts_status` (`status`),
  CONSTRAINT `FK_accounts_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (40,51,'admin','vduong2709@gmail.com','active',0,'2025-10-21 03:38:13','2025-10-15 15:42:38','2025-10-21 03:38:13'),(41,52,'hieucong2468','hieucong2468@gmail.com','active',0,'2025-10-18 21:11:40','2025-10-17 03:18:32','2025-10-21 02:11:05'),(42,53,'trunghieu999909','trunghieu999909@gmail.com','active',0,'2025-10-20 22:08:40','2025-10-17 03:32:46','2025-10-21 03:20:00'),(44,55,'duongnguyen291105','duongnguyen291105@gmail.com','active',0,NULL,'2025-10-17 13:17:32','2025-10-20 22:45:36'),(45,56,'vancaotran','vancaotran@gmail.com','active',0,NULL,'2025-10-17 13:54:59','2025-10-20 00:36:40'),(46,57,'thanhminhnguyen','thanhminhnguyen@gmail.com','active',0,NULL,'2025-10-17 14:02:56','2025-10-20 22:10:03'),(52,65,'ngọc','ngoc@gmail.com','active',0,NULL,'2025-10-21 01:11:32','2025-10-21 01:11:32'),(53,60,'he0045','hr@gmail.com','active',0,NULL,'2025-10-21 01:52:15','2025-10-21 01:52:15'),(54,62,'hoang','hoang@gmail.com','active',0,NULL,'2025-10-21 02:14:27','2025-10-21 02:14:27');
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;

--
-- Table structure for table `applications`
--

DROP TABLE IF EXISTS `applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  KEY `IX_app_job_email` (`job_id`,`email`),
  KEY `IX_app_job_cccd` (`job_id`,`cccd`),
  CONSTRAINT `FK_app_job` FOREIGN KEY (`job_id`) REFERENCES `job_postings` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `applications`
--

/*!40000 ALTER TABLE `applications` DISABLE KEYS */;
/*!40000 ALTER TABLE `applications` ENABLE KEYS */;

--
-- Table structure for table `attachments`
--

DROP TABLE IF EXISTS `attachments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  `attachment_type` varchar(10) DEFAULT 'FILE',
  `external_url` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IX_attach_owner` (`owner_type`,`owner_id`),
  KEY `FK_attach_uploader` (`uploaded_by_account_id`),
  CONSTRAINT `FK_attach_uploader` FOREIGN KEY (`uploaded_by_account_id`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attachments`
--

/*!40000 ALTER TABLE `attachments` DISABLE KEYS */;
INSERT INTO `attachments` VALUES (1,'REQUEST',33,'uploads\\REQUEST\\2025\\10\\edb63f54-e67f-4242-bb35-fa21a04ecfd3.jpg','defaultAvatar.jpg','image/jpeg',7079,'d720744f5c7a645bb107b666285dafcdf6d4fe263984ea82433b8f1d5789bf99',41,'2025-10-19 21:57:46','FILE',NULL),(2,'REQUEST',34,'uploads\\REQUEST\\2025\\10\\49b6a55b-c6d8-478b-a7b0-7abd682511c9.jpg','defaultAvatar.jpg','image/jpeg',7079,'d720744f5c7a645bb107b666285dafcdf6d4fe263984ea82433b8f1d5789bf99',41,'2025-10-19 22:01:59','FILE',NULL),(3,'REQUEST',35,'assets/img/Request/2025/10/e128a209-c98e-4cda-8dee-f1ec4d1a1605.jpg','defaultAvatar.jpg','image/jpeg',7079,'d720744f5c7a645bb107b666285dafcdf6d4fe263984ea82433b8f1d5789bf99',41,'2025-10-19 22:14:58','FILE',NULL),(4,'REQUEST',37,'','Google Drive Link','external/link',0,NULL,41,'2025-10-19 22:47:36','LINK','https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'),(5,'REQUEST',38,'','Google Drive Link','external/link',0,NULL,46,'2025-10-19 22:52:01','LINK','https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'),(6,'REQUEST',39,'','Google Drive Link','external/link',0,NULL,41,'2025-10-19 23:26:18','LINK','https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'),(7,'REQUEST',40,'','Google Drive Link','external/link',0,NULL,41,'2025-10-19 23:31:30','LINK','https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'),(8,'REQUEST',42,'','Google Drive Link','external/link',0,NULL,41,'2025-10-20 00:39:58','LINK','https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'),(9,'REQUEST',43,'','Google Drive Link','external/link',0,NULL,41,'2025-10-20 00:45:52','LINK','https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'),(10,'REQUEST',44,'','Google Drive Link','external/link',0,NULL,41,'2025-10-20 00:46:58','LINK','https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing'),(11,'REQUEST',53,'','Google Drive Link','external/link',0,NULL,46,'2025-10-20 22:05:06','LINK','https://drive.google.com/file/d/16txG2rto3mlgx_cEAS_MTKFOJ48dYG6Q/view?usp=sharing');
/*!40000 ALTER TABLE `attachments` ENABLE KEYS */;

--
-- Table structure for table `attendance_logs`
--

DROP TABLE IF EXISTS `attendance_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `check_type` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `checked_at` datetime NOT NULL,
  `source` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `period_id` bigint DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  KEY `IX_att_user_time` (`user_id`,`checked_at`),
  KEY `FK_att_period` (`period_id`),
  CONSTRAINT `FK_att_period` FOREIGN KEY (`period_id`) REFERENCES `timesheet_periods` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `FK_att_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=199 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance_logs`
--

/*!40000 ALTER TABLE `attendance_logs` DISABLE KEYS */;
INSERT INTO `attendance_logs` VALUES (167,51,'IN','2025-11-14 07:40:00','Excel','On time',4,'2025-10-20 13:32:51'),(168,51,'OUT','2025-11-14 17:40:00','Excel','On time',4,'2025-10-20 13:32:52'),(169,51,'IN','2025-11-15 08:20:00','Excel','Late',4,'2025-10-20 13:32:52'),(170,51,'OUT','2025-11-15 18:20:00','Excel','Late',4,'2025-10-20 13:32:52'),(171,51,'IN','2025-11-16 07:50:00','Excel','On time',4,'2025-10-20 13:32:52'),(172,51,'OUT','2025-11-16 17:50:00','Excel','On time',4,'2025-10-20 13:32:52'),(173,51,'IN','2025-11-17 09:10:00','Excel','Late',4,'2025-10-20 13:32:52'),(174,51,'OUT','2025-11-17 18:10:00','Excel','Late',4,'2025-10-20 13:32:52'),(175,51,'IN','2025-11-18 07:35:00','Excel','On time',4,'2025-10-20 13:32:52'),(176,51,'OUT','2025-11-18 17:35:00','Excel','On time',4,'2025-10-20 13:32:52'),(177,51,'IN','2025-11-19 08:05:00','Excel','Late',4,'2025-10-20 13:32:52'),(178,51,'OUT','2025-11-19 18:00:00','Excel','Late',4,'2025-10-20 13:32:52'),(183,51,'IN','2025-12-22 08:20:00','Excel','Late',5,'2025-10-20 13:32:53'),(184,51,'OUT','2025-12-22 18:20:00','Excel','Late',5,'2025-10-20 13:32:53'),(185,51,'IN','2025-12-23 07:50:00','Excel','On time',5,'2025-10-20 13:32:53'),(186,51,'OUT','2025-12-23 17:50:00','Excel','On time',5,'2025-10-20 13:32:53'),(187,51,'IN','2025-12-24 09:10:00','Excel','Late',5,'2025-10-20 13:32:53'),(188,51,'OUT','2025-12-24 18:10:00','Excel','Late',5,'2025-10-20 13:32:53'),(189,51,'IN','2025-12-25 07:35:00','Excel','On time',5,'2025-10-20 13:32:53'),(190,51,'OUT','2025-12-25 17:35:00','Excel','On time',5,'2025-10-20 13:32:53'),(191,51,'IN','2025-12-26 08:05:00','Excel','Late',5,'2025-10-20 13:32:53'),(192,51,'OUT','2025-12-26 18:00:00','Excel','Late',5,'2025-10-20 13:32:53'),(193,51,'IN','2025-12-27 07:55:00','Excel','Late',5,'2025-10-20 13:32:53'),(194,51,'OUT','2025-12-27 17:55:00','Excel','Late',5,'2025-10-20 13:32:54');
/*!40000 ALTER TABLE `attendance_logs` ENABLE KEYS */;

--
-- Table structure for table `audit_events`
--

DROP TABLE IF EXISTS `audit_events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_id` bigint DEFAULT NULL,
  `event_type` varchar(64) NOT NULL,
  `entity_type` varchar(64) DEFAULT NULL,
  `entity_id` bigint DEFAULT NULL,
  `ip` varchar(64) DEFAULT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  KEY `IX_audit_entity` (`entity_type`,`entity_id`,`created_at`),
  KEY `FK_audit_account` (`account_id`),
  CONSTRAINT `FK_audit_account` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_events`
--

/*!40000 ALTER TABLE `audit_events` DISABLE KEYS */;
/*!40000 ALTER TABLE `audit_events` ENABLE KEYS */;

--
-- Table structure for table `auth_identities`
--

DROP TABLE IF EXISTS `auth_identities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_identities` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_id` bigint NOT NULL,
  `provider` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `provider_user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email_verified` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_auth_provider_subject` (`provider`,`provider_user_id`),
  KEY `IX_auth_account` (`account_id`),
  CONSTRAINT `FK_identity_account` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_identities`
--

/*!40000 ALTER TABLE `auth_identities` DISABLE KEYS */;
INSERT INTO `auth_identities` VALUES (38,40,'local','40','vduong2709@gmail.com',1,'2025-10-15 15:42:38'),(39,40,'google','118175320454479209810','vduong2709@gmail.com',1,'2025-10-15 22:49:07'),(40,41,'local','hieucong2468','hieucong2468@gmail.com',1,'2025-10-17 03:18:32'),(41,42,'local','trunghieu999909','trunghieu999909@gmail.com',1,'2025-10-17 03:32:46'),(43,41,'google','116351005802456025666','hieucong2468@gmail.com',1,'2025-10-17 12:51:47'),(44,44,'local','duongnguyen291105','duongnguyen291105@gmail.com',1,'2025-10-17 13:17:33'),(45,45,'local','vancaotran','vancaotran@gmail.com',1,'2025-10-17 13:55:00'),(46,46,'local','thanhminhnguyen','thanhminhnguyen@gmail.com',1,'2025-10-17 14:02:56'),(47,44,'google','102386974965261966542','duongnguyen291105@gmail.com',1,'2025-10-18 13:55:26'),(48,53,'local','he0045','hr@gmail.com',0,'2025-10-21 01:52:15'),(49,54,'local','hoang','hoang@gmail.com',0,'2025-10-21 02:14:27');
/*!40000 ALTER TABLE `auth_identities` ENABLE KEYS */;

--
-- Table structure for table `auth_local_credentials`
--

DROP TABLE IF EXISTS `auth_local_credentials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_local_credentials` (
  `identity_id` bigint NOT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`identity_id`),
  CONSTRAINT `FK_local_identity` FOREIGN KEY (`identity_id`) REFERENCES `auth_identities` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_local_credentials`
--

/*!40000 ALTER TABLE `auth_local_credentials` DISABLE KEYS */;
INSERT INTO `auth_local_credentials` VALUES (38,'$2a$10$7pXUSihF9WqxzKIXYcUtaepm1oOKtTCBXxB2TPJLRw80rs/idBT6C','2025-10-15 15:56:51'),(40,'$2a$10$TTBxKkMb/.2YhlInil0KEeOIrm/BsfAFdzi8khxWXqhmPoLu8Yv6G','2025-10-16 20:34:53'),(41,'$2a$10$TTBxKkMb/.2YhlInil0KEeOIrm/BsfAFdzi8khxWXqhmPoLu8Yv6G','2025-10-16 20:34:53'),(44,'$2a$10$1fqLO7zMeWVW3OIMFKChtO7CucngOdRo4B7vhZ1QbBvG127wN.LJG','2025-10-17 13:17:33'),(45,'$2a$10$PSYyBVcC7Td2bEUA0esrzO/gdNUM8xrb0WEL3TPTsERI7e9iBxlGO','2025-10-17 13:55:00'),(46,'$2a$10$DBaaBH640l1a4LvsDPjUuehU6GSoMrKrEEEPKsgyaAXROebsiMdzO','2025-10-17 14:02:56'),(48,'$2a$12$3hJrPqQLDHJ8uwO6wk1NxuwiUhNZ7zQxmZ39gIPdACzQWkdV3UQI6','2025-10-21 01:52:15'),(49,'$2a$12$yssNMFbYAOyD1Dpug7AQMuDy.cq.BA.SKLlZI.QGei15FLnW23Lve','2025-10-21 02:14:27');
/*!40000 ALTER TABLE `auth_local_credentials` ENABLE KEYS */;

--
-- Table structure for table `department_features`
--

DROP TABLE IF EXISTS `department_features`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department_features` (
  `department_id` bigint NOT NULL,
  `feature_id` bigint NOT NULL,
  `effect` enum('GRANT','DENY') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`department_id`,`feature_id`),
  KEY `FK_df_feature` (`feature_id`),
  CONSTRAINT `FK_df_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_df_feature` FOREIGN KEY (`feature_id`) REFERENCES `features` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department_features`
--

/*!40000 ALTER TABLE `department_features` DISABLE KEYS */;
/*!40000 ALTER TABLE `department_features` ENABLE KEYS */;

--
-- Table structure for table `departments`
--

DROP TABLE IF EXISTS `departments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `departments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `head_account_id` bigint DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  `updated_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  KEY `FK_dept_head` (`head_account_id`),
  CONSTRAINT `FK_dept_head` FOREIGN KEY (`head_account_id`) REFERENCES `accounts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `departments`
--

/*!40000 ALTER TABLE `departments` DISABLE KEYS */;
INSERT INTO `departments` VALUES (1,'Human Resource',NULL,'2025-10-16 09:33:20','2025-10-20 20:07:24'),(2,'Information Technology',NULL,'2025-10-16 09:33:20','2025-10-20 20:06:06'),(3,'Sales',NULL,'2025-10-16 09:33:20','2025-10-16 09:33:20'),(4,'Marketing',NULL,'2025-10-16 09:33:20','2025-10-16 09:33:20'),(5,'Finance',NULL,'2025-10-16 09:33:20','2025-10-20 22:48:59'),(6,'QA',NULL,'2025-10-18 13:01:17','2025-10-18 13:01:17');
/*!40000 ALTER TABLE `departments` ENABLE KEYS */;

--
-- Table structure for table `employment_contracts`
--

DROP TABLE IF EXISTS `employment_contracts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employment_contracts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `contract_no` varchar(50) DEFAULT NULL,
  `contract_type` varchar(32) DEFAULT NULL,
  `start_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  `base_salary` decimal(18,2) NOT NULL,
  `currency` varchar(8) DEFAULT NULL,
  `status` varchar(24) NOT NULL DEFAULT 'active',
  `file_path` varchar(1024) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `created_by_account_id` bigint DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  `updated_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  KEY `IX_contract_user_start` (`user_id`,`start_date`),
  KEY `FK_contract_creator` (`created_by_account_id`),
  CONSTRAINT `FK_contract_creator` FOREIGN KEY (`created_by_account_id`) REFERENCES `accounts` (`id`),
  CONSTRAINT `FK_contract_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employment_contracts`
--

/*!40000 ALTER TABLE `employment_contracts` DISABLE KEYS */;
INSERT INTO `employment_contracts` VALUES (5,51,'CONTRACT-2024-002','fixed_term','2024-01-15','2025-12-31',30000000.00,'VND','active','contract.pdf','Two-year fixed-term contract for Senior Administrator',NULL,'2025-10-16 09:08:02','2025-10-16 09:08:02');
/*!40000 ALTER TABLE `employment_contracts` ENABLE KEYS */;

--
-- Table structure for table `features`
--

DROP TABLE IF EXISTS `features`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `features` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `route` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort_order` int NOT NULL DEFAULT '0',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  `updated_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `features`
--

/*!40000 ALTER TABLE `features` DISABLE KEYS */;
/*!40000 ALTER TABLE `features` ENABLE KEYS */;

--
-- Table structure for table `holiday_calendar`
--

DROP TABLE IF EXISTS `holiday_calendar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `holiday_calendar` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `year` int NOT NULL,
  `name` varchar(64) NOT NULL,
  `tet_duration` int NOT NULL DEFAULT '7',
  `auto_compensatory` tinyint(1) NOT NULL DEFAULT '1',
  `is_generated` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  `updated_at` datetime NOT NULL DEFAULT (utc_timestamp()) ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_hcal` (`year`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `holiday_calendar`
--

/*!40000 ALTER TABLE `holiday_calendar` DISABLE KEYS */;
INSERT INTO `holiday_calendar` VALUES (2,2025,'Vietnam Public Holidays 2025',7,1,1,'2025-10-15 22:48:18','2025-10-16 12:39:25'),(3,2026,'Vietnam Public Holidays 2026',7,1,1,'2025-10-15 22:48:19','2025-10-16 12:39:53'),(4,2027,'Vietnam Public Holidays 2027',7,1,1,'2025-10-15 22:48:19','2025-10-16 12:39:27'),(5,2028,'Vietnam Public Holidays 2028',7,1,1,'2025-10-15 22:48:19','2025-10-16 12:53:56'),(6,2029,'Vietnam Public Holidays 2029',7,1,1,'2025-10-15 22:48:19','2025-10-16 12:53:04'),(7,2030,'Vietnam Public Holidays 2030',7,1,1,'2025-10-15 22:48:19','2025-10-16 12:39:48');
/*!40000 ALTER TABLE `holiday_calendar` ENABLE KEYS */;

--
-- Table structure for table `holidays`
--

DROP TABLE IF EXISTS `holidays`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `holidays` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `calendar_id` bigint NOT NULL,
  `date_holiday` date NOT NULL,
  `name` varchar(128) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  `is_substitute` tinyint(1) DEFAULT '0' COMMENT 'TRUE if this is a compensatory/substitute day',
  `original_holiday_date` date DEFAULT NULL COMMENT 'Original holiday date if this is a substitute day',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_holidays` (`calendar_id`,`date_holiday`),
  KEY `idx_holidays_is_substitute` (`is_substitute`),
  KEY `idx_holidays_original_date` (`original_holiday_date`),
  CONSTRAINT `FK_holidays_calendar` FOREIGN KEY (`calendar_id`) REFERENCES `holiday_calendar` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Public holidays and substitute days. Substitute days have 200% OT rate, original holidays have 300% OT rate.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `holidays`
--

/*!40000 ALTER TABLE `holidays` DISABLE KEYS */;
INSERT INTO `holidays` VALUES (13,2,'2025-01-01','Tết Dương lịch','2025-10-15 23:15:28',0,NULL),(14,3,'2026-01-01','Tết Dương lịch','2025-10-15 23:15:28',0,NULL),(15,4,'2027-01-01','Tết Dương lịch','2025-10-15 23:15:28',0,NULL),(16,5,'2028-01-01','Tết Dương lịch','2025-10-15 23:15:29',0,NULL),(17,6,'2029-01-01','Tết Dương lịch','2025-10-15 23:15:29',0,NULL),(18,7,'2030-01-01','Tết Dương lịch','2025-10-15 23:15:29',0,NULL),(19,2,'2025-04-30','Ngày Giải phóng miền Nam','2025-10-15 23:17:01',0,NULL),(20,3,'2026-04-30','Ngày Giải phóng miền Nam','2025-10-15 23:17:01',0,NULL),(21,4,'2027-04-30','Ngày Giải phóng miền Nam','2025-10-15 23:17:02',0,NULL),(22,5,'2028-01-03','Nghỉ bù Tết Dương lịch (rơi vào T7)','2025-10-15 23:17:02',1,'2028-01-01'),(23,6,'2029-04-30','Ngày Giải phóng miền Nam','2025-10-15 23:17:02',0,NULL),(24,7,'2030-04-30','Ngày Giải phóng miền Nam','2025-10-15 23:17:02',0,NULL),(25,2,'2025-05-01','Ngày Quốc tế Lao động','2025-10-15 23:17:45',0,NULL),(26,2,'2025-09-02','Quốc khánh (Ngày 1)','2025-10-15 23:17:45',0,NULL),(27,3,'2026-05-01','Ngày Quốc tế Lao động','2025-10-15 23:17:46',0,NULL),(28,3,'2026-09-02','Quốc khánh (Ngày 1)','2025-10-15 23:17:46',0,NULL),(29,4,'2027-05-01','Ngày Quốc tế Lao động','2025-10-15 23:17:46',0,NULL),(30,4,'2027-05-03','Nghỉ bù Ngày Quốc tế Lao động (rơi vào T7)','2025-10-15 23:17:47',1,'2027-05-01'),(31,2,'2025-09-03','Quốc khánh (Ngày 2)','2025-10-15 23:18:39',0,NULL),(32,3,'2026-09-03','Quốc khánh (Ngày 2)','2025-10-15 23:18:39',0,NULL),(33,4,'2027-09-02','Quốc khánh (Ngày 1)','2025-10-15 23:18:40',0,NULL),(34,5,'2028-04-30','Ngày Giải phóng miền Nam','2025-10-15 23:18:40',0,NULL),(35,6,'2029-05-01','Ngày Quốc tế Lao động','2025-10-15 23:18:40',0,NULL),(36,7,'2030-05-01','Ngày Quốc tế Lao động','2025-10-15 23:18:41',0,NULL),(37,2,'2025-01-28','Tết Nguyên Đán (Ngày 28 Tết)','2025-10-15 23:19:19',0,NULL),(38,3,'2026-02-16','Tết Nguyên Đán (Ngày 28 Tết)','2025-10-15 23:19:19',0,NULL),(39,4,'2027-09-03','Quốc khánh (Ngày 2)','2025-10-15 23:19:20',0,NULL),(40,5,'2028-05-01','Nghỉ bù Ngày Giải phóng miền Nam (rơi vào CN)','2025-10-15 23:19:20',0,NULL),(41,6,'2029-09-02','Quốc khánh (Ngày 1)','2025-10-15 23:19:21',0,NULL),(42,7,'2030-09-02','Quốc khánh (Ngày 1)','2025-10-15 23:19:21',0,NULL),(43,2,'2025-01-29','Tết Nguyên Đán (Ngày 29 Tết)','2025-10-15 23:23:25',0,NULL),(44,3,'2026-02-17','Tết Nguyên Đán (Ngày 29 Tết)','2025-10-15 23:23:26',0,NULL),(45,4,'2027-02-05','Tết Nguyên Đán (Ngày 28 Tết)','2025-10-15 23:23:26',0,NULL),(46,5,'2028-09-02','Quốc khánh (Ngày 1)','2025-10-15 23:23:27',0,NULL),(47,6,'2029-09-03','Nghỉ bù Quốc khánh (Ngày 1) (rơi vào CN)','2025-10-15 23:23:28',0,NULL),(48,7,'2030-09-03','Quốc khánh (Ngày 2)','2025-10-15 23:23:28',0,NULL),(49,2,'2025-01-30','Tết Nguyên Đán (Mùng 1 Tết)','2025-10-15 23:23:45',0,NULL),(50,3,'2026-02-18','Tết Nguyên Đán (Mùng 1 Tết)','2025-10-15 23:23:46',0,NULL),(51,4,'2027-02-06','Tết Nguyên Đán (Ngày 29 Tết)','2025-10-15 23:23:47',0,NULL),(52,5,'2028-09-04','Nghỉ bù Quốc khánh (Ngày 1) (rơi vào T7)','2025-10-15 23:23:47',0,NULL),(53,6,'2029-02-12','Tết Nguyên Đán (Ngày 28 Tết)','2025-10-15 23:23:48',0,NULL),(54,7,'2030-02-02','Tết Nguyên Đán (Ngày 28 Tết)','2025-10-15 23:23:48',0,NULL),(55,2,'2025-01-31','Tết Nguyên Đán (Mùng 2 Tết)','2025-10-15 23:32:09',0,NULL),(56,3,'2026-02-19','Tết Nguyên Đán (Mùng 2 Tết)','2025-10-15 23:32:10',0,NULL),(57,4,'2027-02-08','Nghỉ bù Tết Nguyên Đán (Ngày 29 Tết) (rơi vào T7)','2025-10-15 23:32:12',0,NULL),(58,5,'2028-09-03','Quốc khánh (Ngày 2)','2025-10-15 23:32:13',0,NULL),(59,6,'2029-02-13','Tết Nguyên Đán (Ngày 29 Tết)','2025-10-15 23:32:14',0,NULL),(60,7,'2030-02-04','Nghỉ bù Tết Nguyên Đán (Ngày 28 Tết) (rơi vào T7)','2025-10-15 23:32:15',0,NULL),(61,2,'2025-02-01','Tết Nguyên Đán (Mùng 3 Tết)','2025-10-15 23:33:20',0,NULL),(62,3,'2026-02-20','Tết Nguyên Đán (Mùng 3 Tết)','2025-10-15 23:33:21',0,NULL),(63,4,'2027-02-07','Tết Nguyên Đán (Mùng 1 Tết)','2025-10-15 23:33:22',0,NULL),(64,5,'2028-09-05','Nghỉ bù Quốc khánh (Ngày 2) (rơi vào CN)','2025-10-15 23:33:23',0,NULL),(65,6,'2029-02-14','Tết Nguyên Đán (Mùng 1 Tết)','2025-10-15 23:33:24',0,NULL),(66,7,'2030-02-03','Tết Nguyên Đán (Ngày 29 Tết)','2025-10-15 23:33:25',0,NULL),(67,2,'2025-02-03','Nghỉ bù Tết Nguyên Đán (Mùng 3 Tết) (rơi vào T7)','2025-10-16 00:35:23',0,NULL),(68,3,'2026-02-21','Tết Nguyên Đán (Mùng 4 Tết)','2025-10-16 00:35:24',0,NULL),(69,4,'2027-02-09','Nghỉ bù Tết Nguyên Đán (Mùng 1 Tết) (rơi vào CN)','2025-10-16 00:35:25',0,NULL),(70,5,'2028-01-25','Tết Nguyên Đán (Ngày 28 Tết)','2025-10-16 00:35:25',0,NULL),(71,6,'2029-02-15','Tết Nguyên Đán (Mùng 2 Tết)','2025-10-16 00:35:26',0,NULL),(72,7,'2030-02-05','Nghỉ bù Tết Nguyên Đán (Ngày 29 Tết) (rơi vào CN)','2025-10-16 00:35:27',0,NULL),(73,2,'2025-02-02','Tết Nguyên Đán (Mùng 4 Tết)','2025-10-16 00:35:35',0,NULL),(74,3,'2026-02-23','Nghỉ bù Tết Nguyên Đán (Mùng 4 Tết) (rơi vào T7)','2025-10-16 00:35:36',0,NULL),(75,4,'2027-02-10','Tết Nguyên Đán (Mùng 4 Tết)','2025-10-16 00:35:37',0,NULL),(76,5,'2028-01-26','Tết Nguyên Đán (Ngày 29 Tết)','2025-10-16 00:35:38',0,NULL),(77,6,'2029-02-16','Tết Nguyên Đán (Mùng 3 Tết)','2025-10-16 00:35:38',0,NULL),(78,7,'2030-02-06','Tết Nguyên Đán (Mùng 3 Tết)','2025-10-16 00:35:39',0,NULL),(79,2,'2025-02-04','Nghỉ bù Tết Nguyên Đán (Mùng 4 Tết) (rơi vào CN)','2025-10-16 00:36:05',0,NULL),(80,3,'2026-02-22','Tết Nguyên Đán (Mùng 5 Tết)','2025-10-16 00:36:06',0,NULL),(81,4,'2027-02-11','Tết Nguyên Đán (Mùng 5 Tết)','2025-10-16 00:36:06',0,NULL),(82,5,'2028-01-27','Tết Nguyên Đán (Mùng 1 Tết)','2025-10-16 00:36:07',0,NULL),(83,6,'2029-02-17','Tết Nguyên Đán (Mùng 4 Tết)','2025-10-16 00:36:08',0,NULL),(84,7,'2030-02-07','Tết Nguyên Đán (Mùng 4 Tết)','2025-10-16 00:36:09',0,NULL),(85,2,'2025-04-07','Giỗ Tổ Hùng Vương','2025-10-16 00:57:22',0,NULL),(86,3,'2026-02-24','Nghỉ bù Tết Nguyên Đán (Mùng 5 Tết) (rơi vào CN)','2025-10-16 00:57:23',0,NULL),(87,4,'2027-04-16','Giỗ Tổ Hùng Vương','2025-10-16 00:57:24',0,NULL),(88,5,'2028-01-28','Tết Nguyên Đán (Mùng 2 Tết)','2025-10-16 00:57:25',0,NULL),(89,6,'2029-02-19','Nghỉ bù Tết Nguyên Đán (Mùng 4 Tết) (rơi vào T7)','2025-10-16 00:57:26',0,NULL),(90,7,'2030-02-08','Tết Nguyên Đán (Mùng 5 Tết)','2025-10-16 00:57:27',0,NULL),(91,3,'2026-04-26','Giỗ Tổ Hùng Vương','2025-10-16 12:39:25',0,NULL),(92,5,'2028-01-29','Tết Nguyên Đán (Mùng 3 Tết)','2025-10-16 12:39:28',0,NULL),(93,6,'2029-02-18','Tết Nguyên Đán (Mùng 5 Tết)','2025-10-16 12:39:28',0,NULL),(94,7,'2030-04-12','Giỗ Tổ Hùng Vương','2025-10-16 12:39:29',0,NULL),(95,3,'2026-04-27','Nghỉ bù Giỗ Tổ Hùng Vương (rơi vào CN)','2025-10-16 12:39:44',1,'2026-04-26'),(96,5,'2028-01-31','Nghỉ bù Tết Nguyên Đán (Mùng 3 Tết) (rơi vào T7)','2025-10-16 12:39:45',1,'2028-01-29'),(97,6,'2029-02-20','Nghỉ bù Tết Nguyên Đán (Mùng 5 Tết) (rơi vào CN)','2025-10-16 12:39:46',1,'2029-02-18'),(98,5,'2028-01-30','Tết Nguyên Đán (Mùng 4 Tết)','2025-10-16 12:39:53',0,NULL),(99,6,'2029-04-23','Giỗ Tổ Hùng Vương','2025-10-16 12:39:54',0,NULL),(100,5,'2028-02-01','Nghỉ bù Tết Nguyên Đán (Mùng 4 Tết) (rơi vào CN)','2025-10-16 12:53:02',1,'2028-01-30'),(101,5,'2028-04-04','Giỗ Tổ Hùng Vương','2025-10-16 12:53:12',0,NULL);
/*!40000 ALTER TABLE `holidays` ENABLE KEYS */;

--
-- Table structure for table `job_postings`
--

DROP TABLE IF EXISTS `job_postings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `min_salary` decimal(18,2) DEFAULT NULL,
  `max_salary` decimal(18,2) DEFAULT NULL,
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
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  `updated_at` datetime NOT NULL DEFAULT (utc_timestamp()),
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `job_postings`
--

/*!40000 ALTER TABLE `job_postings` DISABLE KEYS */;
/*!40000 ALTER TABLE `job_postings` ENABLE KEYS */;

--
-- Table structure for table `leave_balances`
--

DROP TABLE IF EXISTS `leave_balances`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `leave_balances` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `leave_type_id` bigint NOT NULL,
  `year` int NOT NULL,
  `balance_days` decimal(6,2) NOT NULL DEFAULT '0.00',
  `updated_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_lb` (`user_id`,`leave_type_id`,`year`),
  KEY `FK_lb_type` (`leave_type_id`),
  CONSTRAINT `FK_lb_type` FOREIGN KEY (`leave_type_id`) REFERENCES `leave_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_lb_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `leave_balances`
--

/*!40000 ALTER TABLE `leave_balances` DISABLE KEYS */;
/*!40000 ALTER TABLE `leave_balances` ENABLE KEYS */;

--
-- Table structure for table `leave_ledger`
--

DROP TABLE IF EXISTS `leave_ledger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `leave_ledger` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `leave_type_id` bigint NOT NULL,
  `request_id` bigint DEFAULT NULL,
  `delta_days` decimal(6,2) NOT NULL,
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  KEY `IX_ll_user_time` (`user_id`,`created_at`),
  KEY `FK_ll_type` (`leave_type_id`),
  KEY `FK_ll_request` (`request_id`),
  CONSTRAINT `FK_ll_request` FOREIGN KEY (`request_id`) REFERENCES `requests` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `FK_ll_type` FOREIGN KEY (`leave_type_id`) REFERENCES `leave_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_ll_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `leave_ledger`
--

/*!40000 ALTER TABLE `leave_ledger` DISABLE KEYS */;
/*!40000 ALTER TABLE `leave_ledger` ENABLE KEYS */;

--
-- Table structure for table `leave_types`
--

DROP TABLE IF EXISTS `leave_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `leave_types` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `default_days` decimal(5,2) NOT NULL DEFAULT '0.00',
  `max_days` decimal(5,2) DEFAULT NULL,
  `is_paid` tinyint(1) NOT NULL DEFAULT '1',
  `requires_approval` tinyint(1) NOT NULL DEFAULT '1',
  `requires_certificate` tinyint(1) NOT NULL DEFAULT '0',
  `min_advance_notice` int DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `leave_types`
--

/*!40000 ALTER TABLE `leave_types` DISABLE KEYS */;
INSERT INTO `leave_types` VALUES (7,'ANNUAL','Annual Leave','Annual leave after 12 months: 12/14/16 days per condition. Pro-rate if <12 months; +1 day per 5 years service',12.00,21.00,1,1,0,3,1,'2025-10-15 15:37:43','2025-10-15 15:37:43'),(8,'PERSONAL','Personal Leave','Personal leave (paid): 3 days (marriage); 1 day (child marriage); 3 days (death of parent/spouse/child)',3.00,3.00,1,1,1,1,1,'2025-10-15 15:37:43','2025-10-15 15:37:43'),(9,'MATERNITY','Maternity Leave','Maternity 6 months (Social Insurance). Records and coordinates SI claims per BR-LV-07',180.00,180.00,1,1,1,30,1,'2025-10-15 15:37:43','2025-10-15 15:37:43'),(10,'SICK','Sick Leave','Sick leave (SI): 30/40/60 days by SI seniority. Child-care sickness: 20 days (<3y), 15 days (3-<7y)',30.00,60.00,1,0,1,0,1,'2025-10-15 15:37:43','2025-10-15 15:37:43'),(11,'EMERGENCY','Emergency Leave','Emergency leave for urgent family matters, accidents, or unforeseen circumstances. No advance notice required.',3.00,5.00,1,1,0,0,1,'2025-10-15 15:37:43','2025-10-15 15:37:43'),(12,'UNPAID','Unpaid Leave','npaid leave - Max 5 days per request, 13 days per month, 30 days per year. 3 days advance notice required. Salary will be deducted.\'',5.00,30.00,0,1,0,3,1,'2025-10-15 15:37:43','2025-10-16 14:32:11'),(13,'PATERNITY','Paternity Leave','Paternity leave for fathers. Must be taken within 60 days from birth. Paid leave under company policy (5-14 working days as per law, company provides 7 days). Birth certificate required.',7.00,7.00,1,1,1,0,1,'2025-10-17 08:16:26','2025-10-17 08:16:26');
/*!40000 ALTER TABLE `leave_types` ENABLE KEYS */;

--
-- Table structure for table `ot_policies`
--

DROP TABLE IF EXISTS `ot_policies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ot_policies` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(32) NOT NULL,
  `name` varchar(128) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `rules_json` json DEFAULT NULL,
  `assignments_json` json DEFAULT NULL,
  `updated_by_account_id` bigint DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  `updated_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `FK_ot_updated_by` (`updated_by_account_id`),
  CONSTRAINT `FK_ot_updated_by` FOREIGN KEY (`updated_by_account_id`) REFERENCES `accounts` (`id`),
  CONSTRAINT `ot_policies_chk_1` CHECK (((`rules_json` is null) or json_valid(`rules_json`))),
  CONSTRAINT `ot_policies_chk_2` CHECK (((`assignments_json` is null) or json_valid(`assignments_json`)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ot_policies`
--

/*!40000 ALTER TABLE `ot_policies` DISABLE KEYS */;
/*!40000 ALTER TABLE `ot_policies` ENABLE KEYS */;

--
-- Table structure for table `outbox_messages`
--

DROP TABLE IF EXISTS `outbox_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `outbox_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `topic` varchar(128) NOT NULL,
  `payload_json` json NOT NULL,
  `headers_json` json DEFAULT NULL,
  `status` varchar(16) NOT NULL DEFAULT 'NEW',
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  `sent_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `outbox_messages_chk_1` CHECK (json_valid(`payload_json`)),
  CONSTRAINT `outbox_messages_chk_2` CHECK (((`headers_json` is null) or json_valid(`headers_json`)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `outbox_messages`
--

/*!40000 ALTER TABLE `outbox_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `outbox_messages` ENABLE KEYS */;

--
-- Table structure for table `payslips`
--

DROP TABLE IF EXISTS `payslips`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payslips` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `period_start` date NOT NULL,
  `period_end` date NOT NULL,
  `currency` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gross_amount` decimal(18,2) NOT NULL DEFAULT '0.00',
  `net_amount` decimal(18,2) NOT NULL DEFAULT '0.00',
  `details_json` json DEFAULT NULL,
  `file_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'approved',
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_ps_user_period` (`user_id`,`period_start`,`period_end`),
  CONSTRAINT `FK_ps_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payslips`
--

/*!40000 ALTER TABLE `payslips` DISABLE KEYS */;
/*!40000 ALTER TABLE `payslips` ENABLE KEYS */;

--
-- Table structure for table `position_roles`
--

DROP TABLE IF EXISTS `position_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `position_roles` (
  `position_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`position_id`,`role_id`),
  KEY `FK_pos_roles_role` (`role_id`),
  CONSTRAINT `FK_pos_roles_position` FOREIGN KEY (`position_id`) REFERENCES `positions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_pos_roles_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `position_roles`
--

/*!40000 ALTER TABLE `position_roles` DISABLE KEYS */;
INSERT INTO `position_roles` VALUES (6,10),(7,11),(8,12),(9,13),(10,14);
/*!40000 ALTER TABLE `position_roles` ENABLE KEYS */;

--
-- Table structure for table `positions`
--

DROP TABLE IF EXISTS `positions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `positions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `job_level` int DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  `updated_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `positions`
--

/*!40000 ALTER TABLE `positions` DISABLE KEYS */;
INSERT INTO `positions` VALUES (6,'ADMIN','Administrator',1,'2025-10-16 09:33:20','2025-10-16 09:33:20'),(7,'HR_MANAGER','HR Manager',2,'2025-10-16 09:33:20','2025-10-16 09:33:20'),(8,'HR_STAFF','HR Staff',3,'2025-10-16 09:33:20','2025-10-16 09:33:20'),(9,'DEPT_MANAGER','Department Manager',4,'2025-10-16 09:33:20','2025-10-16 09:33:20'),(10,'STAFF','Staff',5,'2025-10-16 09:33:20','2025-10-16 09:33:20');
/*!40000 ALTER TABLE `positions` ENABLE KEYS */;

--
-- Table structure for table `request_transitions`
--

DROP TABLE IF EXISTS `request_transitions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `request_transitions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `request_id` bigint NOT NULL,
  `from_status` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `to_status` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `action` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `actor_account_id` bigint NOT NULL,
  `actor_user_id` bigint NOT NULL,
  `note` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  KEY `IX_rt_request` (`request_id`,`created_at`),
  KEY `FK_rt_actor_account` (`actor_account_id`),
  KEY `FK_rt_actor_user` (`actor_user_id`),
  CONSTRAINT `FK_rt_actor_account` FOREIGN KEY (`actor_account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_rt_actor_user` FOREIGN KEY (`actor_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_rt_request` FOREIGN KEY (`request_id`) REFERENCES `requests` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `request_transitions`
--

/*!40000 ALTER TABLE `request_transitions` DISABLE KEYS */;
/*!40000 ALTER TABLE `request_transitions` ENABLE KEYS */;

--
-- Table structure for table `request_types`
--

DROP TABLE IF EXISTS `request_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `request_types` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `request_types`
--

/*!40000 ALTER TABLE `request_types` DISABLE KEYS */;
INSERT INTO `request_types` VALUES (6,'LEAVE_REQUEST','Leave Request','2025-10-15 15:26:07'),(7,'OVERTIME_REQUEST','Overtime Request','2025-10-15 15:26:07'),(8,'ADJUSTMENT_REQUEST','Adjustment Request','2025-10-15 15:26:07'),(9,'RECRUITMENT_REQUEST','Recruitment Request','2025-10-15 15:26:07'),(19,'PERSONAL_INFO_UPDATE','Personal Information Update','2025-10-20 23:07:43');
/*!40000 ALTER TABLE `request_types` ENABLE KEYS */;

--
-- Table structure for table `requests`
--

DROP TABLE IF EXISTS `requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  `updated_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  KEY `IX_requests_type_status` (`request_type_id`,`status`),
  KEY `IX_requests_creator` (`created_by_account_id`),
  KEY `FK_requests_creator_user` (`created_by_user_id`),
  KEY `FK_requests_dept` (`department_id`),
  KEY `FK_requests_current_approver` (`current_approver_account_id`),
  CONSTRAINT `FK_requests_creator_account` FOREIGN KEY (`created_by_account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_requests_creator_user` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_requests_current_approver` FOREIGN KEY (`current_approver_account_id`) REFERENCES `accounts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `FK_requests_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `FK_requests_type` FOREIGN KEY (`request_type_id`) REFERENCES `request_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `requests`
--

/*!40000 ALTER TABLE `requests` DISABLE KEYS */;
INSERT INTO `requests` VALUES (16,6,'Leave Request - Annual Leave','{\"reason\": \"check\", \"endDate\": \"2025-10-23T23:59:59\", \"dayCount\": 4, \"isHalfDay\": false, \"startDate\": \"2025-10-20T00:00\", \"durationDays\": 4.0, \"leaveTypeCode\": \"ANNUAL\", \"leaveTypeName\": \"Annual Leave\", \"certificateRequired\": false}',40,51,NULL,'PENDING',NULL,NULL,'2025-10-16 22:33:01','2025-10-16 22:33:01'),(17,6,'Leave Request - Unpaid Leave','{\"reason\": \"check\", \"endDate\": \"2025-10-20T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-10-20T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"UNPAID\", \"leaveTypeName\": \"Unpaid Leave\", \"certificateRequired\": false}',40,51,NULL,'PENDING',NULL,NULL,'2025-10-16 22:38:43','2025-10-16 22:38:43'),(18,6,'Leave Request - Unpaid Leave','{\"reason\": \"check file\", \"endDate\": \"2025-11-03T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-11-03T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"PM\", \"leaveTypeCode\": \"UNPAID\", \"leaveTypeName\": \"Unpaid Leave\", \"certificateRequired\": false}',40,51,NULL,'PENDING',NULL,NULL,'2025-10-16 22:40:22','2025-10-16 22:40:22'),(19,7,'OT Request - 2025-10-18','{\"otDate\": \"2025-10-18\", \"otType\": \"WEEKEND\", \"reason\": \"I need to handle some problem with my work\", \"endTime\": \"13:46\", \"otHours\": 5.0, \"startTime\": \"08:46\", \"payMultiplier\": 2.0, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-17T00:46:52.444678500\", \"createdByManager\": false}',40,51,NULL,'PENDING',NULL,NULL,'2025-10-17 00:46:53','2025-10-17 00:46:53'),(20,6,'Leave Request - Unpaid Leave','{\"reason\": \"c\", \"endDate\": \"2025-10-20T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-10-20T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"UNPAID\", \"leaveTypeName\": \"Unpaid Leave\", \"certificateRequired\": false}',40,51,NULL,'PENDING',NULL,NULL,'2025-10-17 01:18:27','2025-10-17 01:18:27'),(21,6,'Leave Request - Unpaid Leave','{\"reason\": \"c\", \"endDate\": \"2025-10-20T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-10-20T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"UNPAID\", \"leaveTypeName\": \"Unpaid Leave\", \"certificateRequired\": false}',40,51,NULL,'PENDING',NULL,NULL,'2025-10-17 01:28:18','2025-10-17 01:28:18'),(22,6,'Leave Request - Unpaid Leave','{\"reason\": \"c\", \"endDate\": \"2025-10-20T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-10-20T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"UNPAID\", \"leaveTypeName\": \"Unpaid Leave\", \"certificateRequired\": false}',40,51,NULL,'PENDING',NULL,NULL,'2025-10-17 01:29:00','2025-10-17 01:29:00'),(23,6,'Leave Request - Unpaid Leave','{\"reason\": \"c\", \"endDate\": \"2025-10-20T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-10-20T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"UNPAID\", \"leaveTypeName\": \"Unpaid Leave\", \"certificateRequired\": false}',40,51,NULL,'PENDING',NULL,NULL,'2025-10-17 01:36:51','2025-10-17 01:36:51'),(24,6,'Leave Request - Unpaid Leave','{\"reason\": \"c\", \"endDate\": \"2025-10-20T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-10-20T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"UNPAID\", \"leaveTypeName\": \"Unpaid Leave\", \"certificateRequired\": false}',40,51,NULL,'PENDING',NULL,NULL,'2025-10-17 01:41:34','2025-10-17 01:41:34'),(25,7,'OT Request - 2025-10-30','{\"otDate\": \"2025-10-30\", \"otType\": \"WEEKDAY\", \"reason\": \"c\", \"endTime\": \"21:48\", \"otHours\": 1.9, \"startTime\": \"19:54\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-17T01:55:33.882767900\", \"createdByManager\": false}',40,51,NULL,'PENDING',NULL,NULL,'2025-10-17 01:55:34','2025-10-17 01:55:34'),(26,7,'OT Request - 2025-10-29','{\"otDate\": \"2025-10-29\", \"otType\": \"WEEKDAY\", \"reason\": \"c\", \"endTime\": \"10:55\", \"otHours\": 2.0, \"startTime\": \"08:55\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-17T01:57:13.676329600\", \"createdByManager\": false}',40,51,NULL,'PENDING',NULL,NULL,'2025-10-17 01:57:14','2025-10-17 01:57:14'),(27,7,'OT Request - 2025-10-28','{\"otDate\": \"2025-10-28\", \"otType\": \"WEEKDAY\", \"reason\": \"c\", \"endTime\": \"21:15\", \"otHours\": 1.5, \"startTime\": \"19:45\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-17T02:19:45.866037300\", \"createdByManager\": false}',40,51,NULL,'PENDING',NULL,NULL,'2025-10-17 02:19:46','2025-10-17 02:19:46'),(28,6,'Leave Request - Unpaid Leave','{\"reason\": \"c\", \"endDate\": \"2025-11-17T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-11-17T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"UNPAID\", \"leaveTypeName\": \"Unpaid Leave\", \"certificateRequired\": false}',41,52,1,'PENDING',NULL,NULL,'2025-10-18 17:56:42','2025-10-18 17:56:42'),(30,7,'OT Request - 2025-10-27','{\"otDate\": \"2025-10-27\", \"otType\": \"WEEKDAY\", \"reason\": \"c\", \"endTime\": \"21:30\", \"otHours\": 2.0, \"startTime\": \"19:30\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-19T01:03:36.612677600\", \"createdByManager\": false}',46,57,5,'APPROVED',41,NULL,'2025-10-19 01:03:37','2025-10-19 01:45:20'),(31,6,'Leave Request - Unpaid Leave','{\"reason\": \"c\", \"endDate\": \"2025-10-27T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-10-27T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"UNPAID\", \"leaveTypeName\": \"Unpaid Leave\", \"certificateRequired\": false}',46,57,5,'REJECTED',41,NULL,'2025-10-19 01:04:23','2025-10-19 01:45:07'),(32,6,'Leave Request - Personal Leave','{\"reason\": \"test\", \"endDate\": \"2025-10-30T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-10-30T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"PERSONAL\", \"leaveTypeName\": \"Personal Leave\", \"certificateRequired\": true}',41,52,1,'PENDING',NULL,NULL,'2025-10-19 21:50:43','2025-10-19 21:50:43'),(33,6,'Leave Request - Personal Leave','{\"reason\": \"test\", \"endDate\": \"2025-11-18T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-11-18T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"PERSONAL\", \"leaveTypeName\": \"Personal Leave\", \"certificateRequired\": true}',41,52,1,'PENDING',NULL,NULL,'2025-10-19 21:57:45','2025-10-19 21:57:45'),(34,6,'Leave Request - Personal Leave','{\"reason\": \"test\", \"endDate\": \"2025-11-26T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-11-26T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"PERSONAL\", \"leaveTypeName\": \"Personal Leave\", \"certificateRequired\": true}',41,52,1,'PENDING',NULL,NULL,'2025-10-19 22:01:59','2025-10-19 22:01:59'),(35,6,'Leave Request - Personal Leave','{\"reason\": \"test\", \"endDate\": \"2025-10-21T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-10-21T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"PERSONAL\", \"leaveTypeName\": \"Personal Leave\", \"certificateRequired\": true}',41,52,1,'PENDING',NULL,NULL,'2025-10-19 22:14:58','2025-10-19 22:14:58'),(36,6,'Leave Request - Personal Leave','{\"reason\": \"test link\", \"endDate\": \"2025-10-22T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-10-22T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"PM\", \"leaveTypeCode\": \"PERSONAL\", \"leaveTypeName\": \"Personal Leave\", \"certificateRequired\": true}',41,52,1,'PENDING',NULL,NULL,'2025-10-19 22:43:00','2025-10-19 22:43:00'),(37,6,'Leave Request - Sick Leave','{\"reason\": \"test drive\", \"endDate\": \"2025-11-25T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-11-25T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"PM\", \"leaveTypeCode\": \"SICK\", \"leaveTypeName\": \"Sick Leave\", \"certificateRequired\": true}',41,52,1,'PENDING',NULL,NULL,'2025-10-19 22:47:36','2025-10-19 22:47:36'),(38,6,'Leave Request - Sick Leave','{\"reason\": \"test\", \"endDate\": \"2025-11-25T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-11-25T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"SICK\", \"leaveTypeName\": \"Sick Leave\", \"certificateRequired\": true}',46,57,5,'APPROVED',41,NULL,'2025-10-19 22:52:01','2025-10-19 22:53:19'),(39,7,'OT Request - 2025-10-30','{\"otDate\": \"2025-10-30\", \"otType\": \"WEEKDAY\", \"reason\": \"check\", \"endTime\": \"21:15\", \"otHours\": 1.75, \"startTime\": \"19:30\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-19T23:26:17.993699300\", \"createdByManager\": false}',41,52,1,'PENDING',NULL,NULL,'2025-10-19 23:26:18','2025-10-19 23:26:18'),(40,7,'OT Request - 2025-10-23','{\"otDate\": \"2025-10-23\", \"otType\": \"WEEKDAY\", \"reason\": \"test\", \"endTime\": \"21:30\", \"otHours\": 2.0, \"startTime\": \"19:30\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-19T23:31:29.452477800\", \"createdByManager\": false}',41,57,5,'APPROVED',41,NULL,'2025-10-19 23:31:30','2025-10-20 00:22:33'),(41,7,'OT Request - 2025-10-25','{\"otDate\": \"2025-10-25\", \"otType\": \"WEEKEND\", \"reason\": \"test\", \"endTime\": \"17:00\", \"otHours\": 9.0, \"startTime\": \"08:00\", \"payMultiplier\": 2.0, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-19T23:57:52.578156300\", \"createdByManager\": false}',41,56,2,'REJECTED',41,'no','2025-10-19 23:57:53','2025-10-20 01:38:58'),(42,7,'OT Request - 2025-10-22','{\"otDate\": \"2025-10-22\", \"otType\": \"WEEKDAY\", \"reason\": \"check\", \"endTime\": \"21:00\", \"otHours\": 2.0, \"startTime\": \"19:00\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-20T00:39:57.357032600\", \"createdByManager\": false}',41,53,2,'APPROVED',41,'ok','2025-10-20 00:39:57','2025-10-20 01:38:51'),(43,7,'OT Request - 2025-10-21 (Created by Manager)','{\"otDate\": \"2025-10-21\", \"otType\": \"WEEKDAY\", \"reason\": \"check\", \"endTime\": \"21:00\", \"otHours\": 2.0, \"startTime\": \"19:00\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-20T00:45:52.255713900\", \"createdByManager\": true, \"managerAccountId\": 41}',41,53,2,'REJECTED',42,NULL,'2025-10-20 00:45:52','2025-10-20 00:48:02'),(44,7,'OT Request - 2025-10-23 (Created by Manager)','{\"otDate\": \"2025-10-23\", \"otType\": \"WEEKDAY\", \"reason\": \"check\", \"endTime\": \"21:00\", \"otHours\": 2.0, \"startTime\": \"19:00\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-20T00:46:57.967091\", \"createdByManager\": true, \"managerAccountId\": 41}',41,53,2,'REJECTED',41,'check','2025-10-20 00:46:58','2025-10-20 02:42:17'),(45,7,'OT Request - 2025-10-27 (Created by Manager)','{\"otDate\": \"2025-10-27\", \"otType\": \"WEEKDAY\", \"reason\": \"check\", \"endTime\": \"21:00\", \"otHours\": 1.75, \"startTime\": \"19:15\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-20T01:02:22.914019900\", \"createdByManager\": true, \"managerAccountId\": 41}',41,53,2,'APPROVED',42,NULL,'2025-10-20 01:02:23','2025-10-20 01:29:01'),(51,8,'check','{\"detail_text\": \"test\", \"attendance_dates\": \"2025-10-20,2025-10-19\"}',41,52,1,'PENDING',NULL,NULL,'2025-10-20 21:06:27','2025-10-20 21:06:27'),(52,8,'check','{\"detail_text\": \"test\", \"attendance_dates\": \"\"}',41,52,1,'PENDING',NULL,NULL,'2025-10-20 21:06:37','2025-10-20 21:06:37'),(53,6,'Leave Request - Sick Leave','{\"reason\": \"check\", \"endDate\": \"2025-11-26T23:59:59\", \"dayCount\": 1, \"isHalfDay\": true, \"startDate\": \"2025-11-26T00:00\", \"durationDays\": 0.5, \"halfDayPeriod\": \"AM\", \"leaveTypeCode\": \"SICK\", \"leaveTypeName\": \"Sick Leave\", \"certificateRequired\": true}',46,57,5,'REJECTED',42,'check','2025-10-20 22:05:06','2025-10-20 22:25:11'),(54,7,'OT Request - 2025-11-26','{\"otDate\": \"2025-11-26\", \"otType\": \"WEEKDAY\", \"reason\": \"check\", \"endTime\": \"21:00\", \"otHours\": 1.5, \"startTime\": \"19:30\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-20T22:05:47.323887300\", \"createdByManager\": false}',46,57,5,'APPROVED',42,'check','2025-10-20 22:05:47','2025-10-20 22:25:04'),(55,8,'check','{\"detail_text\": \"check\", \"attendance_dates\": \"2025-10-28\"}',46,57,5,'PENDING',NULL,NULL,'2025-10-20 22:06:28','2025-10-20 22:06:28'),(56,9,'Senior Backend Engineer - Project Alpha','{\"jobType\": \"FULL_TIME\", \"jobLevel\": \"SENIOR\", \"quantity\": 2, \"maxSalary\": 1600000.0, \"minSalary\": 900000.0, \"jobSummary\": \"test all\", \"salaryType\": \"GROSS\", \"positionCode\": \"AUTO_GEN_CODE\", \"positionName\": \"Backend Developer\", \"workingLocation\": \"Ho Chi Minh\", \"recruitmentReason\": \"for test\"}',42,53,2,'REJECTED',41,'như cc','2025-10-20 22:10:08','2025-10-21 01:54:25'),(57,9,'Junior front-end - project beta','{\"jobType\": \"INTERNSHIP\", \"jobLevel\": \"JUNIOR\", \"quantity\": 1, \"maxSalary\": 200000.0, \"minSalary\": 100000.0, \"jobSummary\": \"test all \", \"salaryType\": \"NET\", \"positionCode\": \"AUTO_GEN_CODE\", \"positionName\": \"Front-end Developer\", \"workingLocation\": \"Ho Chi Minh City, District 1\", \"recruitmentReason\": \"test \"}',42,53,5,'APPROVED',41,'check','2025-10-20 22:16:20','2025-10-21 01:47:10'),(58,9,'as','{\"jobType\": \"Part-time\", \"jobLevel\": \"Junior\", \"quantity\": 1, \"maxSalary\": 20000000.0, \"minSalary\": 16000000.0, \"jobSummary\": \"die\", \"salaryType\": \"Net\", \"positionName\": \"sleep\", \"workingLocation\": \"HCM\", \"recruitmentReason\": \"sleeep\"}',42,53,5,'PENDING',NULL,NULL,'2025-10-21 02:07:45','2025-10-21 02:07:45'),(59,7,'OT Request - 2025-10-23','{\"otDate\": \"2025-10-23\", \"otType\": \"WEEKDAY\", \"reason\": \"finshed job \", \"endTime\": \"20:45\", \"otHours\": 1.0, \"startTime\": \"19:45\", \"payMultiplier\": 1.5, \"employeeConsent\": true, \"consentTimestamp\": \"2025-10-21T02:15:01.175697500\", \"createdByManager\": false}',41,52,1,'PENDING',NULL,NULL,'2025-10-21 02:15:01','2025-10-21 02:15:01'),(60,9,'Senior Backend Engineer - Project Alpha','{\"jobType\": \"Full-time\", \"jobLevel\": \"SENIOR\", \"quantity\": 2, \"maxSalary\": 10000000000.0, \"minSalary\": 10000000.0, \"jobSummary\": \"test\", \"salaryType\": \"Gross\", \"positionName\": \"Backend Developer\", \"attachmentPath\": \"https://docs.google.com/spreadsheets/d/1t6HRMni8tnY4ULR8V9oKGaIUkSfQ9WGDgkvYMzlwoeU/edit?usp=drive_link\", \"workingLocation\": \"100 Main Street, San Francisco, CA\", \"recruitmentReason\": \"test\"}',42,53,5,'PENDING',NULL,NULL,'2025-10-21 03:17:45','2025-10-21 03:17:45'),(61,9,'Hiring for Senior Data Analyst Position','{\"jobType\": \"Full-time\", \"jobLevel\": \"MIDDLE\", \"quantity\": 2, \"maxSalary\": 2222222222222.0, \"minSalary\": 1111111111.0, \"jobSummary\": \"tets\", \"salaryType\": \"Gross\", \"positionName\": \"Data Analyst\", \"attachmentPath\": \"https://docs.google.com/spreadsheets/d/1t6HRMni8tnY4ULR8V9oKGaIUkSfQ9WGDgkvYMzlwoeU/edit?usp=drive_link\", \"workingLocation\": \"Ho Chi Minh City, District 1\", \"recruitmentReason\": \"test\"}',42,53,5,'PENDING',NULL,NULL,'2025-10-21 03:20:43','2025-10-21 03:20:43');
/*!40000 ALTER TABLE `requests` ENABLE KEYS */;

--
-- Table structure for table `role_features`
--

DROP TABLE IF EXISTS `role_features`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_features` (
  `role_id` bigint NOT NULL,
  `feature_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`,`feature_id`),
  KEY `FK_rf_feature` (`feature_id`),
  CONSTRAINT `FK_rf_feature` FOREIGN KEY (`feature_id`) REFERENCES `features` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_rf_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_features`
--

/*!40000 ALTER TABLE `role_features` DISABLE KEYS */;
/*!40000 ALTER TABLE `role_features` ENABLE KEYS */;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `priority` int NOT NULL DEFAULT '0',
  `is_system` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  `updated_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (10,'ADMIN','Admin',100,1,'2025-10-16 09:33:20','2025-10-16 09:33:20'),(11,'HR_MANAGER','HR Manager',90,1,'2025-10-16 09:33:20','2025-10-16 09:33:20'),(12,'HR_STAFF','HR Staff',80,1,'2025-10-16 09:33:20','2025-10-16 09:33:20'),(13,'DEPT_MANAGER','Department Manager',70,1,'2025-10-16 09:33:20','2025-10-16 09:33:20'),(14,'EMPLOYEE','Employee',50,1,'2025-10-16 09:33:20','2025-10-16 09:33:20'),(15,'GUEST','Guest',0,0,'2025-10-18 05:57:08','2025-10-18 05:57:08'),(19,'GUEST2','Guest2',0,0,'2025-10-20 12:26:23','2025-10-20 12:26:23');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;

--
-- Table structure for table `salary_history`
--

DROP TABLE IF EXISTS `salary_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `salary_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `amount` decimal(18,2) NOT NULL,
  `currency` varchar(8) DEFAULT NULL,
  `effective_from` date NOT NULL,
  `effective_to` date DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `created_by_account_id` bigint DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_salary_period` (`user_id`,`effective_from`),
  KEY `FK_salary_created_by` (`created_by_account_id`),
  CONSTRAINT `FK_salary_created_by` FOREIGN KEY (`created_by_account_id`) REFERENCES `accounts` (`id`),
  CONSTRAINT `FK_salary_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salary_history`
--

/*!40000 ALTER TABLE `salary_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `salary_history` ENABLE KEYS */;

--
-- Table structure for table `system_parameters`
--

DROP TABLE IF EXISTS `system_parameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_parameters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `scope_type` varchar(16) NOT NULL DEFAULT 'GLOBAL',
  `scope_id` bigint DEFAULT NULL,
  `namespace` varchar(64) NOT NULL,
  `param_key` varchar(64) NOT NULL,
  `value_json` json NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `updated_by_account_id` bigint DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_sp` (`scope_type`,`scope_id`,`namespace`,`param_key`),
  KEY `FK_sp_updated_by` (`updated_by_account_id`),
  CONSTRAINT `FK_sp_updated_by` FOREIGN KEY (`updated_by_account_id`) REFERENCES `accounts` (`id`),
  CONSTRAINT `system_parameters_chk_1` CHECK (json_valid(`value_json`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_parameters`
--

/*!40000 ALTER TABLE `system_parameters` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_parameters` ENABLE KEYS */;

--
-- Table structure for table `timesheet_periods`
--

DROP TABLE IF EXISTS `timesheet_periods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `timesheet_periods` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_start` date NOT NULL,
  `date_end` date NOT NULL,
  `is_locked` tinyint(1) NOT NULL DEFAULT '0',
  `locked_by` bigint DEFAULT NULL,
  `locked_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_tsp` (`date_start`,`date_end`),
  KEY `FK_tsp_locked_by` (`locked_by`),
  CONSTRAINT `FK_tsp_locked_by` FOREIGN KEY (`locked_by`) REFERENCES `accounts` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `timesheet_periods`
--

/*!40000 ALTER TABLE `timesheet_periods` DISABLE KEYS */;
INSERT INTO `timesheet_periods` VALUES (3,'October 2025','2025-10-01','2025-10-31',0,NULL,NULL,'2025-10-18 03:10:46'),(4,'November 2025','2025-11-01','2025-11-30',0,NULL,NULL,'2025-10-16 11:07:05'),(5,'December 2025','2025-12-01','2025-12-31',1,40,'2025-10-20 23:48:48','2025-10-16 11:07:05'),(6,'January 2026','2026-01-01','2026-01-31',0,NULL,NULL,'2025-10-16 11:07:05'),(7,'February 2026','2026-02-01','2026-02-28',0,NULL,NULL,'2025-10-16 11:07:05');
/*!40000 ALTER TABLE `timesheet_periods` ENABLE KEYS */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `created_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  `updated_at` datetime NOT NULL DEFAULT (utc_timestamp()),
  PRIMARY KEY (`id`),
  UNIQUE KEY `employee_code` (`employee_code`),
  UNIQUE KEY `cccd` (`cccd`),
  UNIQUE KEY `email_company` (`email_company`),
  UNIQUE KEY `UQ_users_application_id` (`application_id`),
  KEY `FK_users_dept` (`department_id`),
  KEY `FK_users_pos` (`position_id`),
  KEY `IX_users_status` (`status`),
  KEY `IX_users_filter` (`status`,`department_id`,`position_id`),
  CONSTRAINT `FK_users_application` FOREIGN KEY (`application_id`) REFERENCES `applications` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `FK_users_dept` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `FK_users_pos` FOREIGN KEY (`position_id`) REFERENCES `positions` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (51,'ADMIN001','Vũ Ngọc Dương','2005-02-09','male','Nam Dinh','001305607892','2021-12-05','Cục cảnh sát',NULL,NULL,'vduong2709@gmail.com','0999999999',3,6,'active','2025-10-15',NULL,'2025-10-15','Phu Thuong, Tay Ho, Ha Noi','Nam Dinh','Hà Nội','','10000','Vietnam',NULL,'2025-10-15 15:42:38','2025-10-20 16:32:56'),(52,'EMP000052','Nguyễn Công Hiếu',NULL,'male',NULL,NULL,NULL,NULL,NULL,NULL,'hieucong2468@gmail.com',NULL,1,7,'active','2025-10-08',NULL,'2025-10-09',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-10-16 20:18:32','2025-10-16 20:18:32'),(53,'IT000024','Nguyễn Trung Hiếu',NULL,'female',NULL,NULL,NULL,NULL,NULL,NULL,'trunghieu999909@gmail.com',NULL,5,9,'active','2025-10-01',NULL,'2025-10-01',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-10-16 20:32:46','2025-10-16 20:32:46'),(55,'EMP000055','Nguyễn Đức Dương','2005-11-29','male','Sơn Tây','001205000888','2021-12-05','Cuc canh sat',NULL,NULL,'duongnguyen291105@gmail.com','01111111114',1,8,'active','2025-10-10',NULL,'2025-10-10','Au Co, Tay Ho','','Hà Nội','','','Vietnam',NULL,'2025-10-17 06:17:32','2025-10-20 14:57:02'),(56,'EP001','Trần Văn Cao',NULL,'male',NULL,NULL,NULL,NULL,NULL,NULL,'vancaotran@gmail.com',NULL,2,10,'active','2025-10-09',NULL,'2025-10-09',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-10-17 06:45:29','2025-10-17 06:45:29'),(57,'EP002','Nguyễn Thanh Minh',NULL,'Male',NULL,NULL,NULL,NULL,NULL,NULL,'thanhminhnguyen@gmail.com','0111111113',5,10,'active','2025-10-01',NULL,'2025-10-01',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-10-17 06:58:19','2025-10-20 16:22:22'),(60,'HE0045','HR Test',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'hr@gmail.com','0111111115',1,8,'active','2025-10-19',NULL,'2025-10-20',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-10-20 16:25:01','2025-10-20 16:25:01'),(61,'HE0046','Mai Phú Trọng',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'trong@gmail.com','01111111115',5,10,'active','2025-10-08',NULL,'2025-10-07',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-10-20 16:53:14','2025-10-20 17:42:02'),(62,'HE0047','Hoàng',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'hoang@gmail.com','01111111116',5,10,'active','2025-10-14',NULL,'2025-10-21',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-10-20 17:53:16','2025-10-20 17:53:16'),(63,'HE0048','Tùng',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'tung@gmail.com','01111111117',6,10,'active','2025-10-14',NULL,'2025-10-16',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-10-20 17:57:12','2025-10-20 18:07:53'),(64,'HE0049','Vũ',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'vu@gmail.com','01111111118',6,10,'active','2025-10-22',NULL,'2025-10-22',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-10-20 18:10:39','2025-10-20 18:10:39'),(65,'HE0050','Ngọc Hân',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ngoc@gmail.com','01111111119',6,9,'active','2025-10-22',NULL,'2025-10-22',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-10-20 18:11:31','2025-10-20 19:44:58');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

--
-- Dumping routines for database 'hrms'
--
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-21  3:49:11
