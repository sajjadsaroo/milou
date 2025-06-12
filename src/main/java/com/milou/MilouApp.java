package com.milou;

import com.milou.models.Email;
import com.milou.services.AuthService;
import com.milou.services.EmailService;
import com.milou.models.User;

import java.time.LocalDateTime;

public class MilouApp {
    public static void main(String[] args) {

        AuthService auth = new AuthService();
        try {

            User user  = auth.login("testuser@milou.com", "securepassword123");
            if(user != null) {
                Email email = new Email();
                email.setMessage_code("msg12345");
                email.setSender(user);
                email.setSubject("تست اتصال به دیتابیس");
                email.setBody("این یک ایمیل تستی است.");
                email.setTimestamp(LocalDateTime.now());

                try {
                    EmailService emailService = new EmailService();
                    emailService.sendEmail(email);
                    System.out.println("ایمیل با موفقیت ذخیره شد: " + email);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}