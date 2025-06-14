package com.milou.services;

import com.milou.dao.*;
import com.milou.models.*;

import java.util.List;
import java.util.Optional;

public class EmailService {

    private final EmailDao emailDao = new EmailDao();


    public void sendEmail(Email email, Optional<Email> oldMail) throws Exception {
        if (emailDao.findByCode(email.getMessage_code()) != null) {
            throw new Exception("this email already sent");
        }

        try {
            emailDao.save(email);
            if (oldMail.isPresent()) {
                System.out.println("Successfully sent your reply to email " + oldMail.get().getMessage_code() + ".");
            } else {
                System.out.println("Successfully sent your email.");
            }
            System.out.println("Code: " + email.getMessage_code() + "\n");
        } catch (Exception e) {

        }

    }


    public static List<Email> allMails(Long userId) {
        EmailDao emailDao = new EmailDao();
        return emailDao.findReceivedEmailsByUserId(userId);
    }

    public static Optional<Email> readByCode(Long userId, String code) {
        EmailDao emailDao = new EmailDao();
        return emailDao.findEmailByCodeForUser(userId, code);
    }

    public static List<Email> unreadMails(Long userId) {
        EmailDao emailDao = new EmailDao();
        return emailDao.findUnreadEmailsByUserIdNative(userId);
    }

    public static List<Email> sentMails(Long userId) {
        EmailDao emailDao = new EmailDao();
        return emailDao.findSentEmails(userId);
    }
}