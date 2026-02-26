package com.hotel.dao;

import com.hotel.model.Guest;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ReservationDAOImp implements ReservationDAO {

    private static final Logger LOG = Logger.getLogger(ReservationDAOImp.class.getName());
    private final DatabaseConnection db = DatabaseConnection.getInstance();

    private static final String BASE_SELECT =
            "SELECT r.*, g.guest_name, g.contact_number, rm.room_number, rt.type_name, " +
                    "u.full_name AS staff_name, " +
                    "fn_calculate_nights(r.checkin_date,r.checkout_date) AS nights, " +
                    "fn_get_room_rate(r.room_id,r.checkin_date) AS rate " +
                    "FROM reservations r " +
                    "JOIN guests g  ON r.guest_id  = g.guest_id " +
                    "JOIN rooms  rm ON r.room_id   = rm.room_id " +
                    "JOIN room_types rt ON rm.room_type_id = rt.room_type_id " +
                    "JOIN users  u  ON r.staff_id  = u.user_id ";

    @Override
    public String addReservation(int guestId, int roomId, int staffId,
                                 Date checkin, Date checkout) throws Exception {
        String sql = "{CALL sp_add_reservation(?,?,?,?,?,?,?)}";
        try (Connection c = db.getConnection(); CallableStatement cs = c.prepareCall(sql)) {
            cs.setInt(1, guestId); cs.setInt(2, roomId); cs.setInt(3, staffId);
            cs.setDate(4, checkin); cs.setDate(5, checkout);
            cs.registerOutParameter(6, Types.VARCHAR);
            cs.registerOutParameter(7, Types.VARCHAR);
            cs.execute();
            String result = cs.getString(7);
            if (result != null && result.startsWith("ERROR:"))
                throw new Exception(result.substring(6));
            return cs.getString(6);
        }
    }

    @Override
    public Reservation findByNumber(String number) {
        String sql = BASE_SELECT + "WHERE r.reservation_number=?";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, number);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return null;
    }

    @Override
    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY r.created_at DESC";
        try (Connection c = db.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return list;
    }

    @Override
    public List<Reservation> findByGuestName(String name) {
        List<Reservation> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE g.guest_name LIKE ? ORDER BY r.created_at DESC";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return list;
    }

    @Override
    public List<Reservation> findByStatus(String status) {
        List<Reservation> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE r.status=? ORDER BY r.created_at DESC";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return list;
    }

    @Override
    public List<Room> getAvailableRooms(String roomType, Date checkin, Date checkout) {
        List<Room> rooms = new ArrayList<>();
        String sql = "{CALL sp_get_available_rooms(?,?,?)}";
        try (Connection c = db.getConnection(); CallableStatement cs = c.prepareCall(sql)) {
            cs.setString(1, roomType == null ? "" : roomType);
            cs.setDate(2, checkin); cs.setDate(3, checkout);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Room r = new Room();
                r.setRoomId(rs.getInt("room_id"));
                r.setRoomNumber(rs.getString("room_number"));
                r.setFloorNumber(rs.getInt("floor_number"));
                r.setTypeName(rs.getString("type_name"));
                r.setRatePerNight(rs.getDouble("rate_per_night"));
                r.setMaxOccupancy(rs.getInt("max_occupancy"));
                rooms.add(r);
            }
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return rooms;
    }

    @Override
    public boolean cancelReservation(String number) {
        String sql = "UPDATE reservations SET status='CANCELLED' " +
                "WHERE reservation_number=? AND status IN ('CONFIRMED')";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, number);
            if (ps.executeUpdate() > 0) {
                String upRoom = "UPDATE rooms rm JOIN reservations r ON rm.room_id=r.room_id " +
                        "SET rm.room_status='AVAILABLE' WHERE r.reservation_number=?";
                try (PreparedStatement ps2 = c.prepareStatement(upRoom)) {
                    ps2.setString(1, number); ps2.executeUpdate();
                }
                return true;
            }
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return false;
    }

    @Override
    public boolean checkIn(String number) {
        String sql = "UPDATE reservations SET status='CHECKED_IN' " +
                "WHERE reservation_number=? AND status='CONFIRMED'";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, number);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return false;
    }

    @Override
    public String checkOut(String number) throws Exception {
        String sql = "{CALL sp_checkout(?,?)}";
        try (Connection c = db.getConnection(); CallableStatement cs = c.prepareCall(sql)) {
            cs.setString(1, number);
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.execute();
            String result = cs.getString(2);
            if (result != null && result.startsWith("ERROR:"))
                throw new Exception(result.substring(6));
            return "SUCCESS";
        }
    }

    @Override
    public Guest findOrCreateGuest(String name, String contact, String address, String email) {
        String findSql = "SELECT * FROM guests WHERE contact_number=?";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(findSql)) {
            ps.setString(1, contact);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Guest g = new Guest();
                g.setGuestId(rs.getInt("guest_id"));
                g.setGuestName(rs.getString("guest_name"));
                g.setAddress(rs.getString("address"));
                g.setContactNumber(rs.getString("contact_number"));
                g.setEmail(rs.getString("email"));
                return g;
            }
        } catch (SQLException e) { LOG.severe(e.getMessage()); }

        String ins = "INSERT INTO guests(guest_name,address,contact_number,email) VALUES(?,?,?,?)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name); ps.setString(2, address);
            ps.setString(3, contact); ps.setString(4, email);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                Guest g = new Guest(name, address, contact, email);
                g.setGuestId(keys.getInt(1));
                return g;
            }
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return null;
    }

    @Override
    public List<Reservation> findRecentReservations(int limit) {
        List<Reservation> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY r.created_at DESC LIMIT ?";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return list;
    }

    private Reservation map(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setReservationId(rs.getInt("reservation_id"));
        r.setReservationNumber(rs.getString("reservation_number"));
        r.setGuestId(rs.getInt("guest_id"));
        r.setGuestName(rs.getString("guest_name"));
        r.setContactNumber(rs.getString("contact_number"));
        r.setRoomId(rs.getInt("room_id"));
        r.setRoomNumber(rs.getString("room_number"));
        r.setTypeName(rs.getString("type_name"));
        r.setStaffId(rs.getInt("staff_id"));
        r.setStaffName(rs.getString("staff_name"));
        r.setCheckinDate(rs.getDate("checkin_date"));
        r.setCheckoutDate(rs.getDate("checkout_date"));
        r.setStatus(rs.getString("status"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        r.setNights(rs.getInt("nights"));
        r.setRatePerNight(rs.getDouble("rate"));
        return r;
    }
}