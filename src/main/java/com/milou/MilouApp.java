package com.milou;

import com.milou.dao.UserDao;
import com.milou.models.Email;
import com.milou.services.AuthService;
import com.milou.utils.HibernateUtil;
import com.milou.models.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Optional;

public class MilouApp {
    public static void main(String[] args) {

        AuthService auth = new AuthService();
        try {

            User user  = auth.login("testuser@milou.com", "securepassword123");
            if(user != null) {
                for(Email email : user.getSentEmails()){
                    System.out.println(email.getSubject());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}