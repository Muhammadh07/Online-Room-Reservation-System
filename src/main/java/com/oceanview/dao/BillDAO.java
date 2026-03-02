package com.oceanview.dao;

import com.oceanview.db.DatabaseConnection;
import com.oceanview.model.Bill;
import com.oceanview.model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    public Bill findByReservationId(int reservationId) throws SQLException {
        String sql = "SELECT b.*, r.reservation_no FROM bill b JOIN reservation r ON b.reservation_id = r.reservation_id WHERE b.reservation_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public Bill findById(int billId) throws SQLException {
        String sql = "SELECT b.*, r.reservation_no FROM bill b JOIN reservation r ON b.reservation_id = r.reservation_id WHERE b.bill_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Bill> findAllWithDetails() throws SQLException {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT b.*, r.reservation_no, g.full_name AS guest_name " +
                "FROM bill b " +
                "JOIN reservation r ON b.reservation_id = r.reservation_id " +
                "JOIN guest g ON r.guest_id = g.guest_id " +
                "ORDER BY b.generated_at DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Bill b = mapRow(rs);
                b.setGuestName(rs.getString("guest_name"));
                list.add(b);
            }
        }
        return list;
    }

    public Bill save(Bill bill) throws SQLException {
        String sql = "INSERT INTO bill (reservation_id, total_amount, tax_amount, discount, balance_due) VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bill.getReservationId());
            ps.setDouble(2, bill.getTotalAmount());
            ps.setDouble(3, bill.getTaxAmount());
            ps.setDouble(4, bill.getDiscount());
            ps.setDouble(5, bill.getBalanceDue());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) bill.setBillId(keys.getInt(1));
        }
        return bill;
    }

    public long getPendingBillsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM bill WHERE balance_due > 0";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
        }
        return 0;
    }

    public boolean updateBalance(int billId, double newBalance) throws SQLException {
        String sql = "UPDATE bill SET balance_due = ? WHERE bill_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setInt(2, billId);
            return ps.executeUpdate() > 0;
        }
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getInt("bill_id"));
        b.setReservationId(rs.getInt("reservation_id"));
        b.setReservationNo(rs.getString("reservation_no"));
        b.setTotalAmount(rs.getDouble("total_amount"));
        b.setTaxAmount(rs.getDouble("tax_amount"));
        b.setDiscount(rs.getDouble("discount"));
        b.setBalanceDue(rs.getDouble("balance_due"));
        b.setGeneratedAt(rs.getTimestamp("generated_at"));
        return b;
    }
}