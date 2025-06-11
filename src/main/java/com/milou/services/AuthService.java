package com.milou.services;

import com.milou.dao.UserDao;
import com.milou.models.User;
import java.util.Optional;

public class AuthService {
    private final UserDao userDao = new UserDao();
    // ... ( متدهای normalizeEmail و signUp و login اینجا میان )

    // مثلاً متد signUp این شکلی می‌شه:
    public void signUp(String name, String email, String password) throws Exception {
        // ... (validation)
        String normalizedEmail = normalizeEmail(email);
        if (userDao.findByEmail(normalizedEmail).isPresent()) {
            throw new Exception("This email is already taken.");
        }
        User newUser = new User(name, normalizedEmail, password);
        userDao.save(newUser);
    }
}