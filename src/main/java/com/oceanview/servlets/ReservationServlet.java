package com.oceanview.servlets;

import com.oceanview.dao.GuestDAO;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomDAO;
import com.oceanview.model.*;
import com.oceanview.services.BillingService;
import com.oceanview.services.ReservationService;
import com.oceanview.util.ValidationUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReservationServlet extends HttpServlet {

    private final ReservationService reservationService = ReservationService.getInstance();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final GuestDAO guestDAO = new GuestDAO();
    private final BillingService billingService = BillingService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "new":          showNewForm(req, resp); break;
                case "search-rooms": searchRooms(req, resp); break;
                case "view":         viewReservation(req, resp); break;
                case "cancel":       showCancelForm(req, resp); break;
                case "checkin":      checkIn(req, resp); break;
                case "checkout":     checkOut(req, resp); break;
                default:             listReservations(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "";

        try {
            switch (action) {
                case "create":  createReservation(req, resp); break;
                case "cancel":  cancelReservation(req, resp); break;
                default:        listReservations(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    private void listReservations(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String keyword = req.getParameter("search");
        String statusFilter = req.getParameter("status");
        List<Reservation> list;
        if (keyword != null && !keyword.trim().isEmpty()) {
            list = reservationService.searchReservations(keyword.trim());
        } else {
            list = reservationService.getAllReservations();
        }
        if (statusFilter != null && !statusFilter.isEmpty()) {
            list.removeIf(r -> !r.getStatus().equals(statusFilter));
        }
        req.setAttribute("reservations", list);
        req.setAttribute("search", keyword);
        req.setAttribute("statusFilter", statusFilter);
        req.getRequestDispatcher("/reservation_list.jsp").forward(req, resp);
    }

    private void showNewForm(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.setAttribute("roomTypes", roomDAO.findAllTypes());
        req.getRequestDispatcher("/reservation_form.jsp").forward(req, resp);
    }

    private void searchRooms(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String checkIn = req.getParameter("checkIn");
        String checkOut = req.getParameter("checkOut");
        String typeIdStr = req.getParameter("typeId");

        List<String> errors = new ArrayList<>();
        if (!ValidationUtil.isValidDate(checkIn)) errors.add("Invalid check-in date.");
        if (!ValidationUtil.isValidDate(checkOut)) errors.add("Invalid check-out date.");
        if (errors.isEmpty() && !ValidationUtil.isCheckOutAfterCheckIn(checkIn, checkOut))
            errors.add("Check-out must be after check-in.");

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("roomTypes", roomDAO.findAllTypes());
            req.getRequestDispatcher("/reservation_form.jsp").forward(req, resp);
            return;
        }

        int typeId = 0;
        try { typeId = Integer.parseInt(typeIdStr); } catch (Exception ignored) {}

        List<Room> availableRooms = roomDAO.findAvailable(checkIn, checkOut, typeId);
        req.setAttribute("availableRooms", availableRooms);
        req.setAttribute("checkIn", checkIn);
        req.setAttribute("checkOut", checkOut);
        req.setAttribute("roomTypes", roomDAO.findAllTypes());
        req.setAttribute("selectedTypeId", typeId);
        req.setAttribute("guests", guestDAO.findAll());
        req.getRequestDispatcher("/reservation_form.jsp").forward(req, resp);
    }

    private void createReservation(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        List<String> errors = new ArrayList<>();

        String checkIn = ValidationUtil.sanitize(req.getParameter("checkIn"));
        String checkOut = ValidationUtil.sanitize(req.getParameter("checkOut"));
        String guestMode = req.getParameter("guestMode"); // "existing" or "new"
        String roomIdStr = req.getParameter("roomId");
        String numAdultsStr = req.getParameter("numAdults");
        String numChildrenStr = req.getParameter("numChildren");
        String specialRequests = ValidationUtil.sanitize(req.getParameter("specialRequests"));

        // Date validation
        if (!ValidationUtil.isValidDate(checkIn)) errors.add("Invalid check-in date.");
        if (!ValidationUtil.isValidDate(checkOut)) errors.add("Invalid check-out date.");
        if (errors.isEmpty() && !ValidationUtil.isCheckOutAfterCheckIn(checkIn, checkOut))
            errors.add("Check-out must be after check-in.");
        if (errors.isEmpty() && !ValidationUtil.isCheckInNotPast(checkIn))
            errors.add("Check-in date cannot be in the past.");

        int roomId = 0;
        try { roomId = Integer.parseInt(roomIdStr); } catch (Exception e) { errors.add("Please select a room."); }

        int numAdults = 1;
        try { numAdults = Integer.parseInt(numAdultsStr); if (numAdults < 1) errors.add("At least 1 adult required."); }
        catch (Exception e) { errors.add("Invalid number of adults."); }

        int numChildren = 0;
        try { numChildren = Integer.parseInt(numChildrenStr); if (numChildren < 0) errors.add("Children count invalid."); }
        catch (Exception ignored) {}

        // Guest handling
        int guestId = 0;
        if ("existing".equals(guestMode)) {
            try { guestId = Integer.parseInt(req.getParameter("guestId")); }
            catch (Exception e) { errors.add("Please select a guest."); }
        } else {
            // New guest
            String guestName = ValidationUtil.sanitize(req.getParameter("guestName"));
            String guestPhone = ValidationUtil.sanitize(req.getParameter("guestPhone"));
            String guestEmail = ValidationUtil.sanitize(req.getParameter("guestEmail"));
            String guestNic = ValidationUtil.sanitize(req.getParameter("guestNic"));
            String guestAddress = ValidationUtil.sanitize(req.getParameter("guestAddress"));

            if (ValidationUtil.isNullOrEmpty(guestName)) errors.add("Guest name is required.");
            if (!ValidationUtil.isValidPhone(guestPhone)) errors.add("Valid phone number is required.");
            if (!ValidationUtil.isValidEmail(guestEmail)) errors.add("Invalid email format.");

            if (errors.isEmpty()) {
                Guest g = new Guest();
                g.setFullName(guestName);
                g.setPhone(guestPhone);
                g.setEmail(guestEmail);
                g.setNicPassport(guestNic);
                g.setAddress(guestAddress);
                g = guestDAO.save(g);
                guestId = g.getGuestId();
            }
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("roomTypes", roomDAO.findAllTypes());
            req.setAttribute("guests", guestDAO.findAll());
            req.setAttribute("checkIn", checkIn);
            req.setAttribute("checkOut", checkOut);
            req.getRequestDispatcher("/reservation_form.jsp").forward(req, resp);
            return;
        }

        User user = (User) req.getSession().getAttribute("user");
        Reservation res = reservationService.createReservation(guestId, roomId, checkIn, checkOut,
                numAdults, numChildren, specialRequests, user.getUserId());

        req.setAttribute("success", "Reservation " + res.getReservationNo() + " created successfully!");
        req.setAttribute("reservation", res);
        Bill bill = billingService.getBillByReservationId(res.getReservationId());
        req.setAttribute("bill", bill);
        req.getRequestDispatcher("/reservation_details.jsp").forward(req, resp);
    }

    private void viewReservation(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String resNo = req.getParameter("no");
        String idStr = req.getParameter("id");
        Reservation res = null;
        if (resNo != null) res = reservationService.getByReservationNo(resNo);
        else if (idStr != null) res = reservationService.getById(Integer.parseInt(idStr));

        if (res == null) {
            req.setAttribute("error", "Reservation not found.");
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
            return;
        }
        Bill bill = billingService.getBillByReservationId(res.getReservationId());
        List<Payment> payments = bill != null ? billingService.getPaymentsForBill(bill.getBillId()) : new ArrayList<>();
        req.setAttribute("reservation", res);
        req.setAttribute("bill", bill);
        req.setAttribute("payments", payments);
        req.getRequestDispatcher("/reservation_details.jsp").forward(req, resp);
    }

    private void checkIn(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = Integer.parseInt(req.getParameter("id"));
        Reservation res = reservationDAO.findById(id);
        if (res == null) {
            req.setAttribute("error", "Reservation not found.");
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
            return;
        }
        reservationDAO.updateStatus(id, "CHECKED_IN");
        resp.sendRedirect(req.getContextPath() + "/reservations?action=view&id=" + id);
    }

    private void checkOut(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = Integer.parseInt(req.getParameter("id"));
        Reservation res = reservationDAO.findById(id);
        if (res == null) {
            req.setAttribute("error", "Reservation not found.");
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
            return;
        }
        reservationDAO.updateStatus(id, "CHECKED_OUT");
        roomDAO.setAvailable(res.getRoomId(), true);
        resp.sendRedirect(req.getContextPath() + "/reservations?action=view&id=" + id);
    }

    private void showCancelForm(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.getRequestDispatcher("/cancel_reservation.jsp").forward(req, resp);
    }

    private void cancelReservation(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String resNo    = ValidationUtil.sanitize(req.getParameter("reservationNo"));
        String redirect = req.getParameter("redirect"); // "list" | "detail" | null
        if (ValidationUtil.isNullOrEmpty(resNo)) {
            req.setAttribute("error", "Reservation number is required.");
            req.getRequestDispatcher("/cancel_reservation.jsp").forward(req, resp);
            return;
        }
        try {
            reservationService.cancelReservation(resNo);
            if ("list".equals(redirect)) {
                resp.sendRedirect(req.getContextPath() + "/reservations?cancelled=" + resNo);
                return;
            }
            if ("detail".equals(redirect)) {
                Reservation res = reservationService.getByReservationNo(resNo);
                if (res != null) {
                    resp.sendRedirect(req.getContextPath() + "/reservations?action=view&id=" + res.getReservationId());
                    return;
                }
            }
            req.setAttribute("success", "Reservation " + resNo + " has been cancelled.");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }
        req.getRequestDispatcher("/cancel_reservation.jsp").forward(req, resp);
    }
}