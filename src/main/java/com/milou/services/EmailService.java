package com.milou.services;

import com.milou.dao.*;
import com.milou.models.*;
import com.milou.utils.CodeGenerator;

import java.time.LocalDateTime;

import java.util.*;

public class EmailService {

    private final EmailDao emailDao = new EmailDao();
    private final UserDao userDao = new UserDao();


    public void replyToEmail(User replier, String originalEmailCode, String replyBody) throws Exception {
        Optional<Email> originalEmailOpt = emailDao.findEmailByCodeForUser(replier.getId(), originalEmailCode);
        if (originalEmailOpt.isEmpty()) {
            throw new Exception("Original email with code " + originalEmailCode + " not found or you don't have access.");
        }
        Email originalEmail = originalEmailOpt.get();

        String newSubject = originalEmail.getSubject().startsWith("[Re]") ?
                originalEmail.getSubject() :
                "[Re] " + originalEmail.getSubject();


        Set<String> newRecipientEmailSet = new HashSet<>();

        newRecipientEmailSet.add(originalEmail.getSender().getEmail());

        for (User recipient : originalEmail.getRecipients()) {
            if (!recipient.getId().equals(replier.getId())) {
                newRecipientEmailSet.add(recipient.getEmail());
            }
        }

        List<String> finalRecipients = new ArrayList<>(newRecipientEmailSet);

        sendEmail(replier, finalRecipients, newSubject, replyBody, "Successfully sent your reply to email " + originalEmailCode + ".");
    }

    public void forwardEmail(User forwarder, String originalEmailCode, String[] recipients) throws Exception {

        Optional<Email> originalEmailOpt = emailDao.findEmailByCodeForUser(forwarder.getId(), originalEmailCode);

        if (originalEmailOpt.isEmpty()) {
            throw new Exception("Original email with code " + originalEmailCode + " not found or you don't have access.");
        }

        Email originalEmail = originalEmailOpt.get();

        String newSubject = originalEmail.getSubject().startsWith("[Re]") ?
                originalEmail.getSubject() :
                "[Fw] " + originalEmail.getSubject();


        Set<String> newRecipientEmailSet = new HashSet<>();

        for (String recipient : recipients) {
            newRecipientEmailSet.add(recipient.trim());
        }

        List<String> finalRecipients = new ArrayList<>(newRecipientEmailSet);

        sendEmail(forwarder, finalRecipients, newSubject, originalEmail.getBody(), "Successfully forwarded your email.");
    }

    public void sendEmail(User sender, List<String> recipientEmails, String subject, String body, String message) throws Exception {

        List<User> recipientUsers = new ArrayList<>();
        for (String emailStr : recipientEmails) {
            String normalizedEmail = new AuthService().normalizeEmail(emailStr);
            User recipient = userDao.findByEmail(normalizedEmail);
            if (recipient != null) {
                recipientUsers.add(recipient);
            } else {
                System.out.println("Warning: Recipient " + emailStr + " does not exist and will be ignored.");
            }
        }

        if (recipientUsers.isEmpty()) {
            throw new Exception("No valid recipients found for this email.");
        }

        Email email = new Email(sender, subject, body);
        String uniqueCode;
        do {
            uniqueCode = CodeGenerator.generate();
        } while (emailDao.findByCode(uniqueCode) != null);
        email.setMessage_code(uniqueCode);

        email.setRecipients(recipientUsers);
        email.setTimestamp(LocalDateTime.now()); // یا ZonedDateTime

        emailDao.save(email);

        System.out.println(message);
        System.out.println("Code: " + email.getMessage_code() + "\n");

    }


    public List<Email> allMails(Long userId) {
        EmailDao emailDao = new EmailDao();
        return emailDao.findReceivedEmailsByUserId(userId);
    }

    public Optional<Email> readByCode(Long userId, String code) {
        EmailDao emailDao = new EmailDao();
        return emailDao.findEmailByCodeForUser(userId, code);
    }

    public List<Email> unreadMails(Long userId) {
        EmailDao emailDao = new EmailDao();
        return emailDao.findUnreadEmailsByUserIdNative(userId);
    }

    public List<Email> sentMails(Long userId) {
        EmailDao emailDao = new EmailDao();
        return emailDao.findSentEmails(userId);
    }
}