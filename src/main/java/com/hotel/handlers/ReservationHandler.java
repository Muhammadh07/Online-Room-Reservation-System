package com.hotel.handlers;

import com.hotel.model.Bill;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.model.User;
import com.hotel.services.ReservationService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * ReservationHandler — COMMAND PATTERN concrete command.
 * Routes sub-actions: search-rooms, add, view, cancel, checkin, checkout.
 */
public class ReservationHandler implements RequestHandler {

    private final ReservationService svc = ReservationService.getInstance();

    @Override
    public String handle(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "search-rooms":  return handleSearchRooms(req);
            case "add":           return handleAdd(req);
            case "view":          return handleView(req);
            case "cancel":        return handleCancel(req);
            case "checkin":       return handleCheckIn(req);
            case "checkout":      return handleCheckOut(req);
            case "pay":           return handlePay(req);
            default:              return handleList(req);
        }
    }

    private String handleList(HttpServletRequest req) {
        String status = req.getParameter("status");
        String search = req.getParameter("search");
        List<Reservation> list;

        if (search != null && !search.isBlank())
            list = svc.searchByGuest(search.trim());
        else if (status != null && !status.isBlank())
            list = svc.findByStatus(status);
        else
            list = svc.getAllReservations();

        req.setAttribute("reservations", list);
        req.setAttribute("statusFilter", status);
        req.setAttribute("search", search);
        return "/jsp/common/reservations.jsp";
    }

    private String handleSearchRooms(HttpServletRequest req) {
        try {
            String roomType = req.getParameter("roomType");
            String checkin  = req.getParameter("checkin");
            String checkout = req.getParameter("checkout");
            List<Room> rooms = svc.getAvailableRooms(roomType, checkin, checkout);
            req.setAttribute("availableRooms", rooms);
            req.setAttribute("checkin",  checkin);
            req.setAttribute("checkout", checkout);
            req.setAttribute("roomType", roomType);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }
        return "/jsp/common/add-reservation.jsp";
    }

    private String handleAdd(HttpServletRequest req) {
        try {
            HttpSession session = req.getSession(false);
            User user = (User) session.getAttribute("loggedUser");
            String resNum = svc.addReservation(
                    req.getParameter("guestName"),
                    req.getParameter("contactNumber"),
                    req.getParameter("address"),
                    req.getParameter("email"),
                    Integer.parseInt(req.getParameter("roomId")),
                    user.getUserId(),
                    req.getParameter("checkin"),
                    req.getParameter("checkout")
            );
            req.setAttribute("success", "Reservation confirmed! Number: " + resNum);
            req.setAttribute("resNumber", resNum);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            // Restore form values
            req.setAttribute("guestName",     req.getParameter("guestName"));
            req.setAttribute("contactNumber", req.getParameter("contactNumber"));
            req.setAttribute("address",       req.getParameter("address"));
            req.setAttribute("email",         req.getParameter("email"));
        }
        return "/jsp/common/add-reservation.jsp";
    }

    private String handleView(HttpServletRequest req) {
        try {
            String number = req.getParameter("number");
            Reservation r = svc.getReservation(number);
            req.setAttribute("reservation", r);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }
        return "/jsp/common/view-reservation.jsp";
    }

    private String handleCancel(HttpServletRequest req) {
        try {
            String number = req.getParameter("number");
            svc.cancelReservation(number);
            req.setAttribute("success", "Reservation " + number + " has been cancelled.");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }
        return handleList(req);
    }

    private String handleCheckIn(HttpServletRequest req) {
        try {
            String number = req.getParameter("number");
            svc.checkIn(number);
            req.setAttribute("success", "Guest checked in successfully for " + number);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }
        return handleList(req);
    }

    private String handleCheckOut(HttpServletRequest req) {
        try {
            String number = req.getParameter("number");
            Bill bill = svc.generateBill(number);
            req.setAttribute("bill", bill);
            req.setAttribute("success", "Checkout complete. Bill: " + bill.getBillNumber());
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }
        return "/jsp/common/bill.jsp";
    }

    private String handlePay(HttpServletRequest req) {
        try {
            int    billId  = Integer.parseInt(req.getParameter("billId"));
            double amount  = Double.parseDouble(req.getParameter("amount"));
            String method  = req.getParameter("paymentMethod");
            svc.recordPayment(billId, amount, method);
            req.setAttribute("success", "Payment recorded successfully.");
            // Reload bill
            String billNum = req.getParameter("billNumber");
            req.setAttribute("bill", svc.getBillByNumber(billNum));
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }
        return "/jsp/common/bill.jsp";
    }
}