package com.oceanview.dao;

import com.oceanview.db.DatabaseConnection;
import com.oceanview.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    private static final String BASE_QUERY =
            "SELECT r.*, g.full_name AS guest_name, g.phone AS guest_phone, " +
                    "rm.room_number, rt.type_name AS room_type_name, rt.base_price AS room_price, " +
                    "u.username AS created_by_name " +
                    "FROM reservation r " +
                    "JOIN guest g ON r.guest_id = g.guest_id " +
                    "JOIN room rm ON r.room_id = rm.room_id " +
                    "JOIN room_type rt ON rm.type_id = rt.type_id " +
                    "JOIN user u ON r.user_id = u.user_id ";

    public Reservation findByReservationNo(String reservationNo) throws SQLException {
        String sql = BASE_QUERY + "WHERE r.reservation_no = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservationNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public Reservation findById(int id) throws SQLException {
        String sql = BASE_QUERY + "WHERE r.reservation_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Reservation> findAll() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = BASE_QUERY + "ORDER BY r.created_at DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Reservation> findByStatus(String status) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = BASE_QUERY + "WHERE r.status = ? ORDER BY r.check_in";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Reservation> search(String keyword) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = BASE_QUERY + "WHERE r.reservation_no LIKE ? OR g.full_name LIKE ? OR g.phone LIKE ? ORDER BY r.created_at DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Reservation save(Reservation res) throws SQLException {
        String sql = "INSERT INTO reservation (reservation_no, guest_id, room_id, user_id, check_in, check_out, num_adults, num_children, special_requests) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, res.getReservationNo());
            ps.setInt(2, res.getGuestId());
            ps.setInt(3, res.getRoomId());
            ps.setInt(4, res.getUserId());
            ps.setDate(5, res.getCheckIn());
            ps.setDate(6, res.getCheckOut());
            ps.setInt(7, res.getNumAdults());
            ps.setInt(8, res.getNumChildren());
            ps.setString(9, res.getSpecialRequests());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) res.setReservationId(keys.getInt(1));
        }
        return res;
    }

    public boolean updateStatus(int reservationId, String status) throws SQLException {
        String sql = "UPDATE reservation SET status = ? WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reservationId);
            return ps.executeUpdate() > 0;
        }
    }

    // For reports
    public List<Reservation> findByDateRange(String fromDate, String toDate) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = BASE_QUERY + "WHERE r.check_in >= ? AND r.check_in <= ? ORDER BY r.check_in";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fromDate);
            ps.setString(2, toDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setReservationId(rs.getInt("reservation_id"));
        r.setReservationNo(rs.getString("reservation_no"));
        r.setGuestId(rs.getInt("guest_id"));
        r.setGuestName(rs.getString("guest_name"));
        r.setGuestPhone(rs.getString("guest_phone"));
        r.setRoomId(rs.getInt("room_id"));
        r.setRoomNumber(rs.getString("room_number"));
        r.setRoomTypeName(rs.getString("room_type_name"));
        r.setRoomPrice(rs.getDouble("room_price"));
        r.setUserId(rs.getInt("user_id"));
        r.setCreatedByName(rs.getString("created_by_name"));
        r.setCheckIn(rs.getDate("check_in"));
        r.setCheckOut(rs.getDate("check_out"));
        r.setNumAdults(rs.getInt("num_adults"));
        r.setNumChildren(rs.getInt("num_children"));
        r.setStatus(rs.getString("status"));
        r.setSpecialRequests(rs.getString("special_requests"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }
}