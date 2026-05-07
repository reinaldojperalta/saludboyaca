-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: vacunasdb
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

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

--
-- Table structure for table `activity_logs`
--

DROP TABLE IF EXISTS `activity_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_logs` (
  `id` char(36) NOT NULL DEFAULT (UUID()),
  `user_id` int(11) NOT NULL,
  `username` varchar(100) DEFAULT NULL,
  `action_name` varchar(100) NOT NULL,
  `entity_type` varchar(50) DEFAULT NULL,
  `entity_id` varchar(36) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `request_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`request_data`)),
  `response_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`response_data`)),
  `error_message` text DEFAULT NULL,
  `execution_time_ms` int(11) DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `user_agent` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_logs_usuario` (`user_id`,`created_at`),
  KEY `idx_logs_accion` (`action_name`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_logs`
--

LOCK TABLES `activity_logs` WRITE;
/*!40000 ALTER TABLE `activity_logs` DISABLE KEYS */;
INSERT INTO `activity_logs` VALUES ('017f973d-49c9-11f1-8021-b48c9de66fec',2,'dra.martinez','USER_LOGIN','Usuario','2','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"dra.martinez\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,44,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-07 03:58:28'),('02b66388-49c6-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,12,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-07 03:37:02'),('030d990b-49a4-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,12,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:33:39'),('06f0fb13-49c9-11f1-8021-b48c9de66fec',2,'dra.martinez','OTP_VERIFY','Usuario','2','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,11,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-07 03:58:37'),('10063a45-498a-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS',NULL,NULL,NULL,47,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 20:27:54'),('13c51cd4-49ca-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,15,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-07 04:06:08'),('15bb9dd2-4994-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,40,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 21:39:39'),('1ccea98a-49a3-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"admin\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,221,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:27:13'),('1fda6ae0-49a3-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,13,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:27:18'),('2b1d11e2-4992-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"query\":\"\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"admin\"},\"uri\":\"/saludboyaca/login\"}','{\"contentType\":\"\",\"status\":302}',NULL,776,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 21:25:56'),('302e6e14-4992-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"query\":\"\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"contentType\":\"\",\"status\":302}',NULL,39,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 21:26:04'),('384bb4dd-49a5-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"admin\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,176,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:42:18'),('3b01ca43-49a6-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"admin\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,265,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:49:32'),('3bbd781d-49a5-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,14,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:42:24'),('3f0ed3dd-49a6-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,11,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:49:39'),('45079b2c-498b-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS',NULL,NULL,NULL,826,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 20:36:33'),('45c0fc48-498c-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS',NULL,NULL,NULL,853,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 20:43:43'),('49cb76a6-498b-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS',NULL,NULL,NULL,41,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 20:36:41'),('5277b4a9-498c-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS',NULL,NULL,NULL,19,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 20:44:05'),('542f133b-49a7-11f1-8021-b48c9de66fec',1,'dr.gomez','USER_LOGIN','Usuario','1','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"dr.gomez\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,186,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:57:24'),('5719d26c-49a5-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"admin\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,59,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:43:10'),('597c35d1-4990-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS',NULL,NULL,NULL,728,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 21:12:55'),('59d4c6c9-49a7-11f1-8021-b48c9de66fec',1,'dr.gomez','OTP_VERIFY','Usuario','1','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,13,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:57:34'),('5d57c4dc-49a5-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,10,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:43:20'),('5f0d5bc4-4990-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS',NULL,NULL,NULL,38,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 21:13:04'),('64542235-49a7-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"admin\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,45,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:57:51'),('67058c04-49a7-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,13,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:57:56'),('69eb9693-49a5-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"admin\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,63,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:43:42'),('6b7c79fb-4987-11f1-aa01-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS',NULL,NULL,NULL,622,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 20:08:59'),('6f1386cd-49a5-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,12,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:43:50'),('7434164d-49c7-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"admin\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,188,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-07 03:47:22'),('7586976d-4987-11f1-aa01-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS',NULL,NULL,NULL,212,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 20:09:16'),('77eca88b-49c7-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,12,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-07 03:47:28'),('7f4540d5-4987-11f1-aa01-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS',NULL,NULL,NULL,33,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 20:09:32'),('ace59c61-49a3-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"admin\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,190,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:31:15'),('af7329ed-49c8-11f1-8021-b48c9de66fec',4,'admin','PACIENTE_UPDATE','Paciente','1','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"accion\":\"actualizar\",\"apellidos\":\"PÃ©rez RodrÃ­guez\",\"fechaNacimiento\":\"1990-05-15\",\"documento\":\"111222333\",\"eps\":\"Sura\",\"id\":\"1\",\"veredaBarrio\":\"asd\",\"telefono\":\"3101234567\",\"email\":\"juan.perez@email.com\",\"nombres\":\"Juan\"},\"uri\":\"/saludboyaca/pacientes\"}','{\"status\":302}',NULL,16,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-07 03:56:11'),('b10f9d74-49a3-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,12,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:31:22'),('b40523b2-49ca-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"admin\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,174,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-07 04:10:37'),('b4562523-4968-11f1-a0e3-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS',NULL,NULL,NULL,289,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 16:29:07'),('b5c410b0-4232-11f1-a385-b48c9de66fec',3,NULL,'user_login','User','3','SUCCESS','{\"username\":\"superadmin\"}',NULL,NULL,45,'127.0.0.1','Mozilla/5.0 (Test)','2026-04-27 12:14:59'),('b5c430fb-4232-11f1-a385-b48c9de66fec',3,NULL,'cita_create','Cita','1','SUCCESS','{\"paciente\":\"Juan Pérez\",\"medico\":\"Dr. Gómez\"}',NULL,NULL,120,'127.0.0.1','Mozilla/5.0 (Test)','2026-04-27 12:14:59'),('b721e21f-49ca-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,14,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-07 04:10:42'),('bdb6240f-4968-11f1-a0e3-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS',NULL,NULL,NULL,14,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 16:29:23'),('c21b3589-4999-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"admin\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,861,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 22:20:16'),('ca8159fe-49ca-11f1-8021-b48c9de66fec',1,'dr.gomez','USER_LOGIN','Usuario','1','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"dr.gomez\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,51,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-07 04:11:15'),('ccdf3161-4968-11f1-a0e3-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS',NULL,NULL,NULL,194,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 16:29:48'),('cef7d316-49ca-11f1-8021-b48c9de66fec',1,'dr.gomez','OTP_VERIFY','Usuario','1','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,11,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-07 04:11:22'),('d0bb8fb2-49c9-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"admin\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,79,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-07 04:04:16'),('d2689a28-4968-11f1-a0e3-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS',NULL,NULL,NULL,15,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 16:29:58'),('e875f4aa-498c-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS',NULL,NULL,NULL,827,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 20:48:16'),('e909ee5d-4999-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"admin\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,775,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 22:21:21'),('ed8c8f83-498c-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS',NULL,NULL,NULL,39,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 20:48:25'),('ee05ad90-4999-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,38,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 22:21:29'),('f47632ce-49cc-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"admin\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,45,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-07 04:26:44'),('f7a971da-49cc-11f1-8021-b48c9de66fec',4,'admin','OTP_VERIFY','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"otpCodigo\":\"***\"},\"uri\":\"/saludboyaca/otp\"}','{\"status\":302}',NULL,12,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-07 04:26:50'),('f9d322fa-4989-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS',NULL,NULL,NULL,741,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 20:27:17'),('fb873d72-4993-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"admin\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,847,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 21:38:55'),('fefe2f92-49c5-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"admin\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,204,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-07 03:36:56'),('ff4f3c37-49a3-11f1-8021-b48c9de66fec',4,'admin','USER_LOGIN','Usuario','4','SUCCESS','{\"headers\":{\"Accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8\",\"Content-Type\":\"application/x-www-form-urlencoded\"},\"method\":\"POST\",\"params\":{\"password\":\"***\",\"lang\":\"es\",\"username\":\"admin\"},\"uri\":\"/saludboyaca/login\"}','{\"status\":302}',NULL,166,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36','2026-05-06 23:33:33');
/*!40000 ALTER TABLE `activity_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `citas`
--

DROP TABLE IF EXISTS `citas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `citas` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_paciente` int(11) NOT NULL,
  `id_medico` int(11) NOT NULL,
  `id_especialidad` int(11) NOT NULL,
  `fecha_cita` date NOT NULL,
  `hora_cita` time NOT NULL,
  `motivo` varchar(300) DEFAULT NULL,
  `estado` enum('PROGRAMADA','CONFIRMADA','ATENDIDA','CANCELADA') DEFAULT 'PROGRAMADA',
  `observaciones` varchar(500) DEFAULT NULL,
  `fecha_registro` datetime DEFAULT NULL,
  `id_registrado_por` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_especialidad` (`id_especialidad`),
  KEY `id_registrado_por` (`id_registrado_por`),
  KEY `idx_citas_fecha_estado` (`fecha_cita`,`estado`),
  KEY `idx_citas_medico_fecha` (`id_medico`,`fecha_cita`),
  KEY `idx_citas_paciente` (`id_paciente`,`fecha_cita`),
  CONSTRAINT `citas_ibfk_1` FOREIGN KEY (`id_paciente`) REFERENCES `pacientes` (`id`),
  CONSTRAINT `citas_ibfk_2` FOREIGN KEY (`id_medico`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `citas_ibfk_3` FOREIGN KEY (`id_especialidad`) REFERENCES `especialidades` (`id`),
  CONSTRAINT `citas_ibfk_4` FOREIGN KEY (`id_registrado_por`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `citas`
--

LOCK TABLES `citas` WRITE;
/*!40000 ALTER TABLE `citas` DISABLE KEYS */;
INSERT INTO `citas` VALUES (1,1,1,1,'2026-04-28','09:00:00','Dolor de cabeza recurrente','PROGRAMADA',NULL,NULL,3),(2,2,2,2,'2026-04-29','15:30:00','Chequeo cardíaco anual','CONFIRMADA',NULL,NULL,3),(3,3,1,1,'2026-04-30','11:00:00','Revisión general','PROGRAMADA',NULL,NULL,3);
/*!40000 ALTER TABLE `citas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `especialidades`
--

DROP TABLE IF EXISTS `especialidades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `especialidades` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(80) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `especialidades`
--

LOCK TABLES `especialidades` WRITE;
/*!40000 ALTER TABLE `especialidades` DISABLE KEYS */;
INSERT INTO `especialidades` VALUES (1,'Medicina General','Atención primaria y consulta general'),(2,'Cardiología','Especialidad en corazón y sistema circulatorio'),(3,'Pediatría','Atención médica para niños y adolescentes');
/*!40000 ALTER TABLE `especialidades` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `horarios`
--

DROP TABLE IF EXISTS `horarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `horarios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_medico` int(11) NOT NULL,
  `dia_semana` tinyint(4) NOT NULL COMMENT '1=Lun 2=Mar 3=Mié 4=Jue 5=Vie',
  `hora_inicio` time NOT NULL,
  `hora_fin` time NOT NULL,
  `max_citas` int(11) DEFAULT 10,
  PRIMARY KEY (`id`),
  KEY `idx_horarios_medico_dia` (`id_medico`,`dia_semana`),
  CONSTRAINT `horarios_ibfk_1` FOREIGN KEY (`id_medico`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `horarios`
--

LOCK TABLES `horarios` WRITE;
/*!40000 ALTER TABLE `horarios` DISABLE KEYS */;
INSERT INTO `horarios` VALUES (1,1,1,'08:00:00','12:00:00',8),(2,1,1,'14:00:00','18:00:00',8),(3,1,2,'08:00:00','12:00:00',8),(4,1,3,'08:00:00','12:00:00',8),(5,1,4,'08:00:00','12:00:00',8),(6,2,2,'14:00:00','18:00:00',6),(7,2,3,'14:00:00','18:00:00',6),(8,2,4,'14:00:00','18:00:00',6);
/*!40000 ALTER TABLE `horarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `otp_tokens`
--

DROP TABLE IF EXISTS `otp_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `otp_tokens` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) NOT NULL,
  `codigo` varchar(6) NOT NULL,
  `fecha_gen` datetime DEFAULT NULL,
  `expira_en` datetime NOT NULL,
  `usado` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_otp_validacion` (`id_usuario`,`codigo`,`expira_en`,`usado`),
  CONSTRAINT `otp_tokens_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `otp_tokens`
--

LOCK TABLES `otp_tokens` WRITE;
/*!40000 ALTER TABLE `otp_tokens` DISABLE KEYS */;
INSERT INTO `otp_tokens` VALUES (1,3,'000000',NULL,'2999-12-31 23:59:59',0),(65,2,'618611',NULL,'2026-05-07 04:03:28',1),(68,1,'228137',NULL,'2026-05-07 04:16:15',1),(69,4,'355305',NULL,'2026-05-07 04:31:44',1);
/*!40000 ALTER TABLE `otp_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pacientes`
--

DROP TABLE IF EXISTS `pacientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pacientes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombres` varchar(80) NOT NULL,
  `apellidos` varchar(80) NOT NULL,
  `documento` varchar(20) NOT NULL,
  `fecha_nacimiento` date NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `eps` varchar(80) NOT NULL,
  `vereda_barrio` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `documento` (`documento`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pacientes`
--

LOCK TABLES `pacientes` WRITE;
/*!40000 ALTER TABLE `pacientes` DISABLE KEYS */;
INSERT INTO `pacientes` VALUES (1,'Juan','PÃ©rez RodrÃ­guez','111222333','1990-05-15','3101234567','juan.perez@email.com','Sura','asd'),(2,'María','López García','444555666','1985-08-22','3117654321','maria.lopez@email.com','Nueva EPS','Santa Mónica'),(3,'Pedro','Ramírez Torres','777888999','1975-03-10','3129876543','pedro.ramirez@email.com','Sanitas','Villa Olímpica');
/*!40000 ALTER TABLE `pacientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `permission_key` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `permission_key` (`permission_key`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
INSERT INTO `permissions` VALUES (1,'dashboard:ver','Ver panel de control'),(2,'paciente:listar','Listar pacientes'),(3,'paciente:crear','Crear nuevo paciente'),(4,'paciente:editar','Editar paciente existente'),(5,'paciente:eliminar','Eliminar paciente'),(6,'cita:listar','Listar citas médicas'),(7,'cita:crear','Crear nueva cita'),(8,'cita:editar','Editar cita existente'),(9,'cita:eliminar','Eliminar/cancelar cita'),(10,'cita:cambiar_estado','Cambiar estado de cita'),(11,'horario:ver','Ver horarios de médicos'),(12,'usuario:administrar','Administrar usuarios'),(13,'reporte:ver','Ver reportes y estadísticas'),(14,'configuracion:editar','Editar configuración del sistema');
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permissions`
--

DROP TABLE IF EXISTS `role_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permissions` (
  `role_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`),
  KEY `role_permissions_ibfk_2` (`permission_id`),
  CONSTRAINT `role_permissions_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `role_permissions_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permissions`
--

LOCK TABLES `role_permissions` WRITE;
/*!40000 ALTER TABLE `role_permissions` DISABLE KEYS */;
INSERT INTO `role_permissions` VALUES (1,1),(1,2),(1,6),(1,10),(1,11),(2,1),(2,2),(2,3),(2,4),(2,5),(2,6),(2,7),(2,8),(2,9),(2,10),(2,11),(3,1),(3,2),(3,6),(3,11),(4,1),(4,2),(4,3),(4,4),(4,5),(4,6),(4,7),(4,8),(4,9),(4,10),(4,11),(4,12),(4,13),(4,14);
/*!40000 ALTER TABLE `role_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'MEDICO','Médico especialista que atiende citas'),(2,'RECEPCIONISTA','Personal de recepción que agenda y gestiona citas'),(3,'ENFERMERO','Enfermero de apoyo, acceso de solo lectura'),(4,'ADMIN','Super administrador con acceso total al sistema');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (1,2),(2,2),(3,1),(4,4);
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombres` varchar(80) NOT NULL,
  `apellidos` varchar(80) NOT NULL,
  `documento` varchar(20) NOT NULL,
  `email` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `id_especialidad` int(11) DEFAULT NULL,
  `lang_preferido` varchar(5) DEFAULT 'es',
  PRIMARY KEY (`id`),
  UNIQUE KEY `documento` (`documento`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `username` (`username`),
  KEY `id_especialidad` (`id_especialidad`),
  CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`id_especialidad`) REFERENCES `especialidades` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Carlos','Gómez Pérez','12345678','carlos.gomez@hospital.com','dr.gomez','medico123',1,'es'),(2,'Ana','Martínez López','87654321','ana.martinez@hospital.com','dra.martinez','cardi0logA',2,'es'),(3,'Admin','Sistema','99999999','admin@sistema.com','superadmin','admin123',NULL,'es'),(4,'Super','Admin','999888777','admin@saludboyaca.local','admin','admin123',NULL,'es');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'vacunasdb'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-06 23:41:00
