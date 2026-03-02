package com.oceanview.servlets;

import com.oceanview.dao.BillDAO;
import com.oceanview.dao.GuestDAO;
import com.oceanview.dao.PaymentDAO;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomDAO;
import com.oceanview.model.Bill;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardServlet extends HttpServlet {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final RoomDAO        roomDAO        = new RoomDAO();
    private final PaymentDAO     paymentDAO     = new PaymentDAO();
    private final BillDAO        billDAO        = new BillDAO();
    private final GuestDAO       guestDAO       = new GuestDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            // ── Reservations ─────────────────────────────────────
            List<Reservation> allRes = reservationDAO.findAll();
            long confirmed   = allRes.stream().filter(r -> "CONFIRMED".equals(r.getStatus())).count();
            long checkedIn   = allRes.stream().filter(r -> "CHECKED_IN".equals(r.getStatus())).count();
            long checkedOut  = allRes.stream().filter(r -> "CHECKED_OUT".equals(r.getStatus())).count();
            long cancelled   = allRes.stream().filter(r -> "CANCELLED".equals(r.getStatus())).count();

            // ── Rooms ─────────────────────────────────────────────
            List<Room> rooms   = roomDAO.findAll();
            long totalRooms    = rooms.size();
            long availableRooms = rooms.stream().filter(Room::isAvailable).count();
            long occupiedRooms = totalRooms - availableRooms;
            int  occupancyPct  = totalRooms > 0 ? (int) Math.round((occupiedRooms * 100.0) / totalRooms) : 0;

            // ── Revenue & Billing ─────────────────────────────────
            double totalRevenue     = paymentDAO.getTotalRevenue();
            List<Bill> allBills     = billDAO.findAllWithDetails();
            long pendingBillsCount  = allBills.stream().filter(b -> b.getBalanceDue() > 0).count();
            double pendingAmount    = allBills.stream()
                    .filter(b -> b.getBalanceDue() > 0)
                    .mapToDouble(Bill::getBalanceDue).sum();

            // ── Guests ────────────────────────────────────────────
            long totalGuests = guestDAO.findAll().size();

            // ── Today's Activity ─────────────────────────────────
            Date today = Date.valueOf(LocalDate.now());
            List<Reservation> todayCheckIns = allRes.stream()
                    .filter(r -> today.equals(r.getCheckIn()) && !"CANCELLED".equals(r.getStatus()))
                    .collect(Collectors.toList());
            List<Reservation> todayCheckOuts = allRes.stream()
                    .filter(r -> today.equals(r.getCheckOut()) && !"CANCELLED".equals(r.getStatus()))
                    .collect(Collectors.toList());

            // ── Upcoming (next 7 days) ────────────────────────────
            Date in7 = Date.valueOf(LocalDate.now().plusDays(7));
            List<Reservation> upcoming = allRes.stream()
                    .filter(r -> r.getCheckIn() != null
                            && r.getCheckIn().after(today)
                            && !r.getCheckIn().after(in7)
                            && "CONFIRMED".equals(r.getStatus()))
                    .collect(Collectors.toList());

            // ── Recent 8 reservations ─────────────────────────────
            List<Reservation> recent = allRes.stream().limit(8).collect(Collectors.toList());

            // ── Payment method breakdown ──────────────────────────
            List<java.util.Map<String, Object>> paymentMethodSummary =
                    paymentDAO.getPaymentMethodSummary();

            // ── Bind attributes ───────────────────────────────────
            req.setAttribute("totalReservations",   allRes.size());
            req.setAttribute("confirmedCount",       confirmed);
            req.setAttribute("checkedInCount",       checkedIn);
            req.setAttribute("checkedOutCount",      checkedOut);
            req.setAttribute("cancelledCount",       cancelled);
            req.setAttribute("totalRooms",           totalRooms);
            req.setAttribute("availableRooms",       availableRooms);
            req.setAttribute("occupiedRooms",        occupiedRooms);
            req.setAttribute("occupancyPct",         occupancyPct);
            req.setAttribute("totalRevenue",         totalRevenue);
            req.setAttribute("pendingBillsCount",    pendingBillsCount);
            req.setAttribute("pendingAmount",        pendingAmount);
            req.setAttribute("totalGuests",          totalGuests);
            req.setAttribute("todayCheckIns",        todayCheckIns);
            req.setAttribute("todayCheckOuts",       todayCheckOuts);
            req.setAttribute("upcomingReservations", upcoming);
            req.setAttribute("recentReservations",   recent);
            req.setAttribute("paymentMethodSummary", paymentMethodSummary);

            req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }
}
