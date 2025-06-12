package com.milou.services;
import com.milou.dao.*;
import com.milou.models.*;

public class EmailService {

    private final EmailDao emailDao = new EmailDao();


    public void sendEmail(Email email) throws Exception {
        if (emailDao.findByCode(email.getMessage_code()) != null) {
            throw new Exception("خطا: این ایمیل قبلا ارسال شده است!");
        }
        emailDao.save(email);
    }

}