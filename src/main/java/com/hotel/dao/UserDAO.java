package com.hotel.dao;

import com.hotel.model.User;
import java.util.List;

/**
 * UserDAO — DAO PATTERN interface for User data access.
 */
public interface UserDAO {
    User findByUsername(String username);
    User findById(int userId);
    List<User> findAll();
    boolean create(User user);
    boolean update(User user);
    boolean deactivate(int userId);
    boolean updateLastLogin(int userId);
    boolean updatePassword(int userId, String newHash);
    boolean existsByUsername(String username);
}