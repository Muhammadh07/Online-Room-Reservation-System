package com.hotel.dao;

import com.hotel.model.User;
import com.hotel.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserDAOImpl implements UserDAO {

    private static final Logger LOG = Logger.getLogger(UserDAOImpl.class.getName());
    private final DatabaseConnection db = DatabaseConnection.getInstance();

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username=? AND is_active=TRUE";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return null;
    }

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE user_id=?";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY role, full_name";
        try (Connection c = db.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return list;
    }

    @Override
    public boolean create(User u) {
        String sql = "INSERT INTO users(username,password_hash,role,full_name,email) VALUES(?,?,?,?,?)";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getRole());
            ps.setString(4, u.getFullName());
            ps.setString(5, u.getEmail());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return false;
    }

    @Override
    public boolean update(User u) {
        String sql = "UPDATE users SET full_name=?,email=?,role=? WHERE user_id=?";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getRole());
            ps.setInt(4, u.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return false;
    }

    @Override
    public boolean deactivate(int id) {
        String sql = "UPDATE users SET is_active=FALSE WHERE user_id=?";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return false;
    }

    @Override
    public boolean updateLastLogin(int id) {
        String sql = "UPDATE users SET last_login=CURRENT_TIMESTAMP WHERE user_id=?";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return false;
    }

    @Override
    public boolean updatePassword(int id, String hash) {
        String sql = "UPDATE users SET password_hash=? WHERE user_id=?";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return false;
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username=?";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return false;
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setActive(rs.getBoolean("is_active"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        u.setLastLogin(rs.getTimestamp("last_login"));
        return u;
    }
}