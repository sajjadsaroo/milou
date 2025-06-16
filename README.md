# Milou - A Simple Console-Based Email Client

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Hibernate](https://img.shields.io/badge/Hibernate-6-blue.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)
![Maven](https://img.shields.io/badge/Build-Maven-brightgreen.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

A robust, console-based email simulation application built with Java and Hibernate. Milou allows users to sign up, log in, and exchange emails within a closed system. The project follows a layered architecture (DAO, Service, Entity) and demonstrates core concepts of Object-Relational Mapping (ORM) with Hibernate and MySQL.

## ✨ Features

* **User Authentication:** Secure sign-up and login functionality.
* **Email Composition:** Send new emails to one or more recipients.
* **Inbox Management:** View all, unread, or sent emails, sorted by date.
* **Email Viewing:** View full email details by a unique 6-character code. Emails are automatically marked as read for the recipient upon viewing.
* **Reply & Forward:**

  * **Reply-All:** Easily reply to the sender and all other recipients.
  * **Forward:** Forward an email to new recipients.
* **Permission Control:** Users can only view emails where they are the sender or a recipient.

## 🛠️ Tech Stack

* **Language:** [Java 17](https://www.oracle.com/java/)
* **Framework / ORM:** [Hibernate 6](https://hibernate.org/orm/)
* **Database:** [MySQL 8](https://www.mysql.com/)
* **Build Tool:** [Apache Maven](https://maven.apache.org/)

## 🏗️ Architecture

The project is built upon a classic layered architecture to ensure separation of concerns and maintainability.

* **UI Layer (`MilouApp`):** Handles all console input and output. It is a "dumb" layer that delegates all logic to the Service layer.
* **Service Layer (`AuthService`, `EmailService`):** Contains the core business logic of the application (e.g., how to register a user, the rules for replying to an email).
* **Data Access Layer (DAO - `UserDao`, `EmailDao`):** Responsible for all database communications. It encapsulates all HQL and Native SQL queries.
* **Model/Entity Layer (`User`, `Email`):** Plain Old Java Objects (POJOs) mapped to database tables using JPA/Hibernate annotations.

## 🚀 Getting Started

Follow these instructions to get a local copy up and running.

### Prerequisites

Make sure you have the following installed on your system:

* Java Development Kit (JDK) 17 or later.
* Apache Maven.
* MySQL Server.

### Installation & Setup

1. **Clone the repository:**

   ```sh
   git clone https://github.com/sajjadsaroo/milou.git
   cd milou
   ```

2. **Create the MySQL Database:**
   Connect to your MySQL server and run the following command to create the database for the project:

   ```sql
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
   ```

3. **Configure Hibernate:**
   Navigate to `src/main/resources/` and open the `hibernate.cfg.xml` file. Update the database connection properties with your MySQL username and password.

   ```xml
   <property name="connection.username">YOUR_MYSQL_USERNAME</property>
   <property name="connection.password">YOUR_MYSQL_PASSWORD</property>
   ```

4. **Build the Project:**
   Use Maven to compile the project and download all dependencies.

   ```sh
   mvn clean install
   ```

5. **Run the Application:**
   Execute the compiled JAR file from the `target` directory.

   ```sh
   java -jar target/milou-app-1.0-SNAPSHOT.jar
   ```

## ⌨️ How to Use

Once the application is running, you can interact with it using the following commands in the console:

* **Initial Menu:**

  * `L` or `login`: To log into an existing account.
  * `S` or `signup`: To create a new account.
  * `E` or `exit`: To close the application.
* **Main Menu (After Login):**

  * `S`: Send a new email.
  * `V`: View your emails (opens the View Emails Menu).
  * `R`: Reply to an email.
  * `F`: Forward an email.
  * `L`: Logout and return to the initial menu.
* **View Emails Menu:**

  * `A`: View all received emails.
  * `U`: View only unread emails.
  * `S`: View all sent emails.
  * `C`: View a specific email by its 6-character code.
  * `B`: Go back to the main menu.

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

---

*This project was developed as a comprehensive learning exercise in Java, Hibernate, and software architecture.*
