package com.milou.services;
import com.milou.dao.*;
import com.milou.models.*;

public class EmailService {

    private final EmailDao emailDao = new EmailDao();


    public void sendEmail(Email email) throws Exception {
        if (emailDao.findByCode(email.getMessage_code()) != null) {
            throw new Exception("this email already sent");
        }

        try {
            emailDao.save(email);
            System.out.println("Successfully sent your email.");
            System.out.println("Code: " + email.getMessage_code() + "\n");
        } catch (Exception e) {

        }

    }

}