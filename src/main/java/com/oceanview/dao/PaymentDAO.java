package com.oceanview.dao;

import com.oceanview.db.DatabaseConnection;
import com.oceanview.model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    // Full query with all LEFT JOINs — safe for old data where recorded_by may be NULL
    private static final String BASE_SQL =
            "SELECT p.*, pm.method_name, " +
            "       COALESCE(u.username, '') AS recorded_by_name, " +
            "       COALESCE(r.reservation_no, '') AS reservation_no, " +
            "       COALESCE(g.full_name, '') AS guest_name " +
            "FROM payment p " +
            "JOIN payment_method pm ON p.method_id = pm.method_id " +
            "LEFT JOIN user u ON p.recorded_by = u.user_id " +
            "LEFT JOIN bill b ON p.bill_id = b.bill_id " +
            "LEFT JOIN reservation r ON b.reservation_id = r.reservation_id " +
            "LEFT JOIN guest g ON r.guest_id = g.guest_id ";

    public Payment save(Payment payment) throws SQLException {
        String sql = "INSERT INTO payment (bill_id, method_id, amount, reference_no, recorded_by) VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, payment.getBillId());
            ps.setInt(2, payment.getMethodId());
            ps.setDouble(3, payment.getAmount());
            ps.setString(4, payment.getReferenceNo());
            ps.setInt(5, payment.getRecordedBy());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) payment.setPaymentId(keys.getInt(1));
        }
        return payment;
    }

    public List<Payment> findByBillId(int billId) throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = BASE_SQL + "WHERE p.bill_id = ? ORDER BY p.payment_date";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Payment> findAll() throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = BASE_SQL + "ORDER BY p.payment_date DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Payment> findRecent(int limit) throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = BASE_SQL + "ORDER BY p.payment_date DESC LIMIT " + limit;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Payment> findByDateRange(String from, String to) throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = BASE_SQL +
                "WHERE DATE(p.payment_date) >= ? AND DATE(p.payment_date) <= ? ORDER BY p.payment_date";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, from);
            ps.setString(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<java.util.Map<String, Object>> getPaymentMethodSummary() throws SQLException {
        List<java.util.Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT pm.method_name, COUNT(p.payment_id) as count, " +
                "COALESCE(SUM(p.amount), 0) as total " +
                "FROM payment p JOIN payment_method pm ON p.method_id = pm.method_id " +
                "GROUP BY pm.method_name ORDER BY total DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                java.util.Map<String, Object> row = new java.util.LinkedHashMap<>();
                row.put("method", rs.getString("method_name"));
                row.put("count", rs.getInt("count"));
                row.put("total", rs.getDouble("total"));
                list.add(row);
            }
        }
        return list;
    }

    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payment";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0.0;
    }

    public long getTotalPaymentCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM payment";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
        }
        return 0;
    }

    public List<java.util.Map<String, String>> findAllPaymentMethods() throws SQLException {
        List<java.util.Map<String, String>> list = new ArrayList<>();
        // Try with is_active filter; fall back to all if column absent
        String sql = "SELECT method_id, method_name FROM payment_method " +
                     "WHERE COALESCE(is_active, 1) = 1 ORDER BY method_id";
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    java.util.Map<String, String> m = new java.util.LinkedHashMap<>();
                    m.put("id", rs.getString("method_id"));
                    m.put("name", rs.getString("method_name"));
                    list.add(m);
                }
            } catch (SQLException e) {
                // Fallback if is_active column doesn't exist
                list.clear();
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT method_id, method_name FROM payment_method ORDER BY method_id")) {
                    while (rs.next()) {
                        java.util.Map<String, String> m = new java.util.LinkedHashMap<>();
                        m.put("id", rs.getString("method_id"));
                        m.put("name", rs.getString("method_name"));
                        list.add(m);
                    }
                }
            }
        }
        return list;
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setPaymentId(rs.getInt("payment_id"));
        p.setBillId(rs.getInt("bill_id"));
        p.setMethodId(rs.getInt("method_id"));
        p.setMethodName(rs.getString("method_name"));
        p.setAmount(rs.getDouble("amount"));
        p.setReferenceNo(rs.getString("reference_no"));
        p.setPaymentDate(rs.getTimestamp("payment_date"));
        p.setRecordedBy(rs.getInt("recorded_by"));
        p.setRecordedByName(rs.getString("recorded_by_name"));
        p.setReservationNo(rs.getString("reservation_no"));
        p.setGuestName(rs.getString("guest_name"));
        return p;
    }
}
