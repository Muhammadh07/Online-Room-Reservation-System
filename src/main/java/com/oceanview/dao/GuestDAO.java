package com.oceanview.dao;

import com.oceanview.db.DatabaseConnection;
import com.oceanview.model.Guest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {

    public Guest findById(int guestId) throws SQLException {
        String sql = "SELECT * FROM guest WHERE guest_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public List<Guest> search(String keyword) throws SQLException {
        List<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM guest WHERE full_name LIKE ? OR phone LIKE ? OR nic_passport LIKE ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Guest> findAll() throws SQLException {
        List<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM guest ORDER BY full_name";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public boolean update(Guest guest) throws SQLException {
        String sql = "UPDATE guest SET full_name=?, email=?, phone=?, nic_passport=?, address=? WHERE guest_id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, guest.getFullName());
            ps.setString(2, guest.getEmail());
            ps.setString(3, guest.getPhone());
            ps.setString(4, guest.getNicPassport());
            ps.setString(5, guest.getAddress());
            ps.setInt(6, guest.getGuestId());
            return ps.executeUpdate() > 0;
        }
    }

    public Guest save(Guest guest) throws SQLException {
        String sql = "INSERT INTO guest (full_name, email, phone, nic_passport, address) VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, guest.getFullName());
            ps.setString(2, guest.getEmail());
            ps.setString(3, guest.getPhone());
            ps.setString(4, guest.getNicPassport());
            ps.setString(5, guest.getAddress());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) guest.setGuestId(keys.getInt(1));
        }
        return guest;
    }

    public boolean hasReservations(int guestId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservation WHERE guest_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean delete(int guestId) throws SQLException {
        String sql = "DELETE FROM guest WHERE guest_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            return ps.executeUpdate() > 0;
        }
    }

    private Guest mapRow(ResultSet rs) throws SQLException {
        Guest g = new Guest();
        g.setGuestId(rs.getInt("guest_id"));
        g.setFullName(rs.getString("full_name"));
        g.setEmail(rs.getString("email"));
        g.setPhone(rs.getString("phone"));
        g.setNicPassport(rs.getString("nic_passport"));
        g.setAddress(rs.getString("address"));
        return g;
    }
}