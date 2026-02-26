package com.hotel.services;

import com.hotel.dao.UserDAO;
import com.hotel.dao.UserDAOImpl;
import com.hotel.model.User;
import com.hotel.util.PasswordUtil;
import com.hotel.util.ValidationUtil;

/**
 * AuthService — SINGLETON + FACADE PATTERN
 * Single point of truth for all authentication operations.
 * Design Pattern Rationale:
 *   - Singleton: only one AuthService needed; avoids multiple DB connections.
 *   - Facade: hides DAO + password hashing complexity behind simple methods.
 */
public class AuthService {

    private static volatile AuthService instance;
    private final UserDAO userDAO;
    public static final int MAX_ATTEMPTS = 3;

    private AuthService() { this.userDAO = new UserDAOImpl(); }

    public static AuthService getInstance() {
        if (instance == null) {
            synchronized (AuthService.class) {
                if (instance == null) instance = new AuthService();
            }
        }
        return instance;
    }

    /**
     * Authenticate a user. Returns User on success, null on failure.
     */
    public User authenticate(String username, String password) {
        if (!ValidationUtil.isNotEmpty(username) || !ValidationUtil.isNotEmpty(password))
            return null;
        User user = userDAO.findByUsername(username.trim().toLowerCase());
        if (user == null || !user.isActive()) return null;
        if (!PasswordUtil.verify(password, user.getPasswordHash())) return null;
        userDAO.updateLastLogin(user.getUserId());
        return user;
    }

    public boolean createStaff(String username, String password, String role,
                               String fullName, String email) throws Exception {
        if (!ValidationUtil.isNotEmpty(username))     throw new Exception("Username is required");
        if (!ValidationUtil.isValidPassword(password)) throw new Exception("Password must be at least 6 characters");
        if (!ValidationUtil.isNotEmpty(fullName))     throw new Exception("Full name is required");
        if (userDAO.existsByUsername(username.toLowerCase())) throw new Exception("Username already exists");

        User u = new User();
        u.setUsername(username.trim().toLowerCase());
        u.setPasswordHash(PasswordUtil.sha256(password));
        u.setRole(role);
        u.setFullName(fullName.trim());
        u.setEmail(email);
        u.setActive(true);
        return userDAO.create(u);
    }

    public boolean changePassword(int userId, String oldPwd, String newPwd) throws Exception {
        User u = userDAO.findById(userId);
        if (u == null) throw new Exception("User not found");
        if (!PasswordUtil.verify(oldPwd, u.getPasswordHash())) throw new Exception("Current password is incorrect");
        if (!ValidationUtil.isValidPassword(newPwd)) throw new Exception("New password must be at least 6 characters");
        return userDAO.updatePassword(userId, PasswordUtil.sha256(newPwd));
    }

    public boolean deactivateUser(int userId) { return userDAO.deactivate(userId); }
    public User    findById(int id)           { return userDAO.findById(id); }
    public java.util.List<User> findAll()     { return userDAO.findAll(); }
    public boolean updateUser(User u)         { return userDAO.update(u); }
}