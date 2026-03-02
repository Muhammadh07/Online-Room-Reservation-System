package com.oceanview.services;

import com.oceanview.dao.UserDAO;
import com.oceanview.model.User;

import java.sql.SQLException;

public class AuthService {
    private static AuthService instance;
    private final UserDAO userDAO = new UserDAO();

    private AuthService() {}

    public static synchronized AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

    public User login(String username, String password) throws SQLException {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty())
            return null;
        boolean valid = userDAO.authenticate(username.trim(), password);
        if (valid) return userDAO.findByUsername(username.trim());
        return null;
    }
}