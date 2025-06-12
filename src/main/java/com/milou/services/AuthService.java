package com.milou.services;
import com.milou.dao.*;
import com.milou.models.*;

public class AuthService {
    private final UserDao userDao = new UserDao();

    public void signUp(String name, String email, String password) throws Exception {
        String normalizedEmail = normalizeEmail(email); // متد کمکی برای افزودن @milou.com
        if(password.length() < 8) {
            throw new Exception("password too short (must be at least 8 characters)");
        }
        if (userDao.findByEmail(normalizedEmail) != null) {
            throw new Exception("this email already exists");
        }

        User newUser = new User(name, normalizedEmail, password);
        userDao.save(newUser);
    }

    public User login(String email, String password){
        String normalizedEmail = normalizeEmail(email);
        if(userDao.findByEmail(normalizedEmail) == null){
            return null;
        }
        User user = userDao.findByEmail(normalizedEmail);
        if(!user.getPassword().equals(password)){
            return null;
        }
        return user;
    }

    public String normalizeEmail(String email) {
        if (!email.contains("@")) {
            return email+"@milou.com";
        }
        return email;
    }

}