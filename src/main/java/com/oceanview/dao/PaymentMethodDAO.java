package com.oceanview.dao;

import com.oceanview.db.DatabaseConnection;
import com.oceanview.model.PaymentMethod;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentMethodDAO {

    public List<PaymentMethod> findAll() throws SQLException {
        List<PaymentMethod> list = new ArrayList<>();
        String sql = "SELECT method_id, method_name, COALESCE(is_active, 1) AS is_active " +
                     "FROM payment_method ORDER BY method_id";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st  = conn.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public PaymentMethod findById(int id) throws SQLException {
        String sql = "SELECT method_id, method_name, COALESCE(is_active, 1) AS is_active " +
                     "FROM payment_method WHERE method_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public PaymentMethod save(PaymentMethod pm) throws SQLException {
        String sql = "INSERT INTO payment_method (method_name, is_active) VALUES (?, 1)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pm.getMethodName());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) pm.setMethodId(keys.getInt(1));
        }
        return pm;
    }

    public boolean update(PaymentMethod pm) throws SQLException {
        String sql = "UPDATE payment_method SET method_name = ? WHERE method_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pm.getMethodName());
            ps.setInt(2, pm.getMethodId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean toggleActive(int id) throws SQLException {
        String sql = "UPDATE payment_method SET is_active = 1 - COALESCE(is_active, 1) WHERE method_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private PaymentMethod mapRow(ResultSet rs) throws SQLException {
        PaymentMethod pm = new PaymentMethod();
        pm.setMethodId(rs.getInt("method_id"));
        pm.setMethodName(rs.getString("method_name"));
        pm.setActive(rs.getInt("is_active") == 1);
        return pm;
    }
}
