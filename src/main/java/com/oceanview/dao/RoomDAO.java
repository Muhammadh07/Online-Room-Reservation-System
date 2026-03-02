package com.oceanview.dao;

import com.oceanview.db.DatabaseConnection;
import com.oceanview.model.Room;
import com.oceanview.model.RoomType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public List<Room> findAvailable(String checkIn, String checkOut, int typeId) throws SQLException {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT r.*, rt.type_name, rt.base_price FROM room r " +
                "JOIN room_type rt ON r.type_id = rt.type_id " +
                "WHERE COALESCE(r.is_available, 1) = 1 " +
                (typeId > 0 ? "AND r.type_id = ? " : "") +
                "AND r.room_id NOT IN (" +
                "  SELECT room_id FROM reservation " +
                "  WHERE status NOT IN ('CANCELLED','CHECKED_OUT') " +
                "  AND NOT (check_out <= ? OR check_in >= ?)" +
                ") ORDER BY r.room_number";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            if (typeId > 0) ps.setInt(idx++, typeId);
            ps.setString(idx++, checkIn);
            ps.setString(idx, checkOut);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Room> findAll() throws SQLException {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT r.*, rt.type_name, rt.base_price FROM room r JOIN room_type rt ON r.type_id = rt.type_id ORDER BY r.room_number";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Room findById(int roomId) throws SQLException {
        String sql = "SELECT r.*, rt.type_name, rt.base_price FROM room r JOIN room_type rt ON r.type_id = rt.type_id WHERE r.room_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public boolean setAvailable(int roomId, boolean available) throws SQLException {
        String sql = "UPDATE room SET is_available = ? WHERE room_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setInt(2, roomId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<RoomType> findAllTypes() throws SQLException {
        List<RoomType> list = new ArrayList<>();
        String sql = "SELECT * FROM room_type ORDER BY base_price";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                RoomType rt = new RoomType();
                rt.setTypeId(rs.getInt("type_id"));
                rt.setTypeName(rs.getString("type_name"));
                rt.setDescription(rs.getString("description"));
                rt.setBasePrice(rs.getDouble("base_price"));
                list.add(rt);
            }
        }
        return list;
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setRoomId(rs.getInt("room_id"));
        r.setRoomNumber(rs.getString("room_number"));
        r.setTypeId(rs.getInt("type_id"));
        r.setTypeName(rs.getString("type_name"));
        r.setBasePrice(rs.getDouble("base_price"));
        r.setFloorNumber(rs.getInt("floor_number"));
        r.setAvailable(rs.getBoolean("is_available"));
        r.setMaxOccupancy(rs.getInt("max_occupancy"));
        return r;
    }
}