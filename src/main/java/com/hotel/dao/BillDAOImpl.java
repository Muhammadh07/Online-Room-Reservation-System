package com.hotel.dao;

import com.hotel.model.Bill;
import com.hotel.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BillDAOImpl implements BillDAO {

    private static final Logger LOG = Logger.getLogger(BillDAOImpl.class.getName());
    private final DatabaseConnection db = DatabaseConnection.getInstance();

    private static final String BASE_SELECT =
            "SELECT b.*, r.reservation_number, g.guest_name, rm.room_number, rt.type_name, " +
                    "fn_calculate_nights(res.checkin_date,res.checkout_date) AS nights, " +
                    "fn_get_room_rate(res.room_id,res.checkin_date) AS rate " +
                    "FROM bills b " +
                    "JOIN reservations res ON b.reservation_id = res.reservation_id " +
                    "JOIN reservations r   ON b.reservation_id = r.reservation_id " +
                    "JOIN guests g         ON r.guest_id = g.guest_id " +
                    "JOIN rooms rm         ON r.room_id  = rm.room_id " +
                    "JOIN room_types rt    ON rm.room_type_id = rt.room_type_id ";

    @Override
    public Bill generateBill(int reservationId) throws Exception {
        String sql = "{CALL sp_generate_bill(?,?,?)}";
        try (Connection c = db.getConnection(); CallableStatement cs = c.prepareCall(sql)) {
            cs.setInt(1, reservationId);
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.registerOutParameter(3, Types.DECIMAL);
            cs.execute();
            if (cs.getString(2) == null) throw new Exception("Failed to generate bill");
            return findByReservationId(reservationId);
        }
    }

    @Override
    public Bill findByReservationId(int reservationId) {
        String sql = BASE_SELECT + "WHERE b.reservation_id=?";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return null;
    }

    @Override
    public Bill findByBillNumber(String billNumber) {
        String sql = BASE_SELECT + "WHERE b.bill_number=?";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, billNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return null;
    }

    @Override
    public boolean recordPayment(int billId, double amount, String method) {
        String sql = "INSERT INTO payments(bill_id,amount_paid,payment_method) VALUES(?,?,?)";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, billId); ps.setDouble(2, amount); ps.setString(3, method);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return false;
    }

    @Override
    public List<Bill> findAll() {
        List<Bill> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY b.generated_at DESC";
        try (Connection c = db.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return list;
    }

    @Override
    public Map<String,Object> getRevenueSummary() {
        Map<String,Object> summary = new HashMap<>();
        String sql = "SELECT COUNT(*) AS total_bills, " +
                "SUM(total_amount) AS total_revenue, " +
                "SUM(CASE WHEN payment_status='PAID' THEN total_amount ELSE 0 END) AS paid_revenue, " +
                "SUM(CASE WHEN payment_status='PENDING' THEN total_amount ELSE 0 END) AS pending_revenue " +
                "FROM bills";
        try (Connection c = db.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                summary.put("totalBills",      rs.getInt("total_bills"));
                summary.put("totalRevenue",    rs.getDouble("total_revenue"));
                summary.put("paidRevenue",     rs.getDouble("paid_revenue"));
                summary.put("pendingRevenue",  rs.getDouble("pending_revenue"));
            }
        } catch (SQLException e) { LOG.severe(e.getMessage()); }
        return summary;
    }

    private Bill map(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getInt("bill_id"));
        b.setBillNumber(rs.getString("bill_number"));
        b.setReservationId(rs.getInt("reservation_id"));
        b.setReservationNumber(rs.getString("reservation_number"));
        b.setGuestName(rs.getString("guest_name"));
        b.setRoomNumber(rs.getString("room_number"));
        b.setTypeName(rs.getString("type_name"));
        b.setNights(rs.getInt("nights"));
        b.setRatePerNight(rs.getDouble("rate"));
        b.setSubtotal(rs.getDouble("subtotal"));
        b.setTaxRate(rs.getDouble("tax_rate"));
        b.setTaxAmount(rs.getDouble("tax_amount"));
        b.setDiscountAmount(rs.getDouble("discount_amount"));
        b.setTotalAmount(rs.getDouble("total_amount"));
        b.setPaymentStatus(rs.getString("payment_status"));
        b.setGeneratedAt(rs.getTimestamp("generated_at"));
        return b;
    }
}