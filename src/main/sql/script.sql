SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


DROP DATABASE IF EXISTS `milou`;

CREATE DATABASE `milou` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `milou`;

-- ===============================================
-- ساختن جدول کاربران (users)
-- ===============================================
CREATE TABLE `users` (
                         `id` BIGINT NOT NULL AUTO_INCREMENT,
                         `name` VARCHAR(255) DEFAULT NULL,
                         `email` VARCHAR(255) NOT NULL,
                         `password` VARCHAR(255) NOT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `UK_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- ساختن جدول ایمیل‌ها (emails)
-- ===============================================
CREATE TABLE `emails` (
                          `id` BIGINT NOT NULL AUTO_INCREMENT,
                          `message_code` VARCHAR(255) NOT NULL,
                          `subject` VARCHAR(255) DEFAULT NULL,
                          `body` TEXT,
                          `timestamp` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
                          `sender_id` BIGINT NOT NULL,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `UK_emails_message_code` (`message_code`),
                          KEY `FK_emails_sender_idx` (`sender_id`),
                          CONSTRAINT `FK_emails_sender` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- ساختن جدول واسط گیرندگان ایمیل (email_recipient)
-- ===============================================
CREATE TABLE `email_recipient` (
                                   `email_id` BIGINT NOT NULL,
                                   `user_id` BIGINT NOT NULL,
                                   `is_read` BOOLEAN NOT NULL DEFAULT 0,
                                   PRIMARY KEY (`email_id`, `user_id`),
                                   KEY `FK_recipient_user_idx` (`user_id`),
                                   CONSTRAINT `FK_recipient_email` FOREIGN KEY (`email_id`) REFERENCES `emails` (`id`) ON DELETE CASCADE,
                                   CONSTRAINT `FK_recipient_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


SET FOREIGN_KEY_CHECKS = 1;