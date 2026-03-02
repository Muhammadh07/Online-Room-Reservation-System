package com.oceanview.servlets;

import com.oceanview.dao.GuestDAO;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.model.Guest;
import com.oceanview.model.Reservation;
import com.oceanview.util.ValidationUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuestServlet extends HttpServlet {

    private final GuestDAO guestDAO = new GuestDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String action = req.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "new":  showNewForm(req, resp); break;
                case "edit": showEditForm(req, resp); break;
                case "view": viewGuest(req, resp);   break;
                default:     listGuests(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String action = req.getParameter("action");
        if (action == null) action = "";

        try {
            switch (action) {
                case "create": createGuest(req, resp); break;
                case "update": updateGuest(req, resp); break;
                case "delete": deleteGuest(req, resp); break;
                default:       listGuests(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    private void listGuests(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String keyword = req.getParameter("search");
        List<Guest> guests;
        if (keyword != null && !keyword.trim().isEmpty()) {
            guests = guestDAO.search(keyword.trim());
        } else {
            guests = guestDAO.findAll();
        }
        req.setAttribute("guests", guests);
        req.setAttribute("search", keyword);
        req.getRequestDispatcher("/guest_list.jsp").forward(req, resp);
    }

    private void showNewForm(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.getRequestDispatcher("/guest_form.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = Integer.parseInt(req.getParameter("id"));
        Guest guest = guestDAO.findById(id);
        if (guest == null) {
            req.setAttribute("error", "Guest not found.");
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
            return;
        }
        req.setAttribute("guest", guest);
        req.getRequestDispatcher("/guest_form.jsp").forward(req, resp);
    }

    private void viewGuest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = Integer.parseInt(req.getParameter("id"));
        Guest guest = guestDAO.findById(id);
        if (guest == null) {
            req.setAttribute("error", "Guest not found.");
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
            return;
        }
        List<Reservation> allRes = reservationDAO.findAll();
        List<Reservation> guestRes = allRes.stream()
                .filter(r -> r.getGuestId() == id)
                .collect(Collectors.toList());
        req.setAttribute("guest", guest);
        req.setAttribute("reservations", guestRes);
        req.getRequestDispatcher("/guest_details.jsp").forward(req, resp);
    }

    private void createGuest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        List<String> errors = validateGuest(req);
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("formData", req.getParameterMap());
            req.getRequestDispatcher("/guest_form.jsp").forward(req, resp);
            return;
        }
        Guest guest = buildGuest(req);
        guestDAO.save(guest);
        resp.sendRedirect(req.getContextPath() + "/guests?success=created");
    }

    private void updateGuest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = Integer.parseInt(req.getParameter("guestId"));
        List<String> errors = validateGuest(req);
        if (!errors.isEmpty()) {
            Guest existing = guestDAO.findById(id);
            req.setAttribute("guest", existing);
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/guest_form.jsp").forward(req, resp);
            return;
        }
        Guest guest = buildGuest(req);
        guest.setGuestId(id);
        guestDAO.update(guest);
        resp.sendRedirect(req.getContextPath() + "/guests?success=updated");
    }

    private void deleteGuest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = Integer.parseInt(req.getParameter("guestId"));
        if (guestDAO.hasReservations(id)) {
            req.setAttribute("error", "Cannot delete guest — they have existing reservations.");
            listGuests(req, resp);
            return;
        }
        guestDAO.delete(id);
        resp.sendRedirect(req.getContextPath() + "/guests?success=deleted");
    }

    private List<String> validateGuest(HttpServletRequest req) {
        List<String> errors = new ArrayList<>();
        String name = ValidationUtil.sanitize(req.getParameter("fullName"));
        String phone = ValidationUtil.sanitize(req.getParameter("phone"));
        String email = ValidationUtil.sanitize(req.getParameter("email"));
        if (ValidationUtil.isNullOrEmpty(name)) errors.add("Full name is required.");
        if (!ValidationUtil.isValidPhone(phone)) errors.add("Valid phone number is required (7-15 digits).");
        if (!ValidationUtil.isValidEmail(email)) errors.add("Invalid email format.");
        return errors;
    }

    private Guest buildGuest(HttpServletRequest req) {
        Guest g = new Guest();
        g.setFullName(ValidationUtil.sanitize(req.getParameter("fullName")));
        g.setEmail(ValidationUtil.sanitize(req.getParameter("email")));
        g.setPhone(ValidationUtil.sanitize(req.getParameter("phone")));
        g.setNicPassport(ValidationUtil.sanitize(req.getParameter("nicPassport")));
        g.setAddress(ValidationUtil.sanitize(req.getParameter("address")));
        return g;
    }
}
