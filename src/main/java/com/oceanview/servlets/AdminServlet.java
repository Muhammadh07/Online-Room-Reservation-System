package com.oceanview.servlets;

import com.oceanview.dao.PaymentMethodDAO;
import com.oceanview.dao.UserDAO;
import com.oceanview.model.PaymentMethod;
import com.oceanview.model.User;
import com.oceanview.util.ValidationUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminServlet extends HttpServlet {

    private final UserDAO          userDAO          = new UserDAO();
    private final PaymentMethodDAO paymentMethodDAO = new PaymentMethodDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User sessionUser = (User) req.getSession().getAttribute("user");
        if (sessionUser == null || !"ADMIN".equals(sessionUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required.");
            return;
        }
        String action = req.getParameter("action");
        try {
            if ("edit".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                req.setAttribute("editUser", userDAO.findById(id));
            } else if ("edit-method".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                req.setAttribute("editMethod", paymentMethodDAO.findById(id));
            } else if ("toggle-method".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                paymentMethodDAO.toggleActive(id);
                resp.sendRedirect(req.getContextPath() + "/admin?tab=methods");
                return;
            }
            loadPage(req);
            req.getRequestDispatcher("/admin_staff.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User sessionUser = (User) req.getSession().getAttribute("user");
        if (sessionUser == null || !"ADMIN".equals(sessionUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String action = req.getParameter("action");
        try {
            switch (action == null ? "" : action) {
                case "add":           addStaff(req, resp);         break;
                case "update":        updateStaff(req, resp);      break;
                case "delete":        deleteStaff(req, resp);      break;
                case "add-method":    addMethod(req, resp);        break;
                case "update-method": updateMethod(req, resp);     break;
                default:              resp.sendRedirect(req.getContextPath() + "/admin");
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            try { loadPage(req); } catch (Exception ignored) {}
            req.getRequestDispatcher("/admin_staff.jsp").forward(req, resp);
        }
    }

    // ── Page loader ───────────────────────────────────────────────

    private void loadPage(HttpServletRequest req) throws Exception {
        req.setAttribute("users",          userDAO.findAll());
        req.setAttribute("paymentMethods", paymentMethodDAO.findAll());
    }

    // ── Staff actions ─────────────────────────────────────────────

    private void addStaff(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        List<String> errors = new ArrayList<>();
        String username = ValidationUtil.sanitize(req.getParameter("username"));
        String password = req.getParameter("password");
        String fullName = ValidationUtil.sanitize(req.getParameter("fullName"));
        String email    = ValidationUtil.sanitize(req.getParameter("email"));
        String phone    = ValidationUtil.sanitize(req.getParameter("phone"));
        String role     = req.getParameter("role");

        if (ValidationUtil.isNullOrEmpty(username))                         errors.add("Username required.");
        if (ValidationUtil.isNullOrEmpty(password) || password.length() < 6) errors.add("Password must be at least 6 characters.");
        if (ValidationUtil.isNullOrEmpty(fullName))                         errors.add("Full name required.");
        if (!ValidationUtil.isValidEmail(email))                            errors.add("Invalid email.");
        if (!ValidationUtil.isValidPhone(phone))                            errors.add("Valid phone required.");
        if (!"ADMIN".equals(role) && !"STAFF".equals(role))                 errors.add("Invalid role.");

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            loadPage(req);
            req.getRequestDispatcher("/admin_staff.jsp").forward(req, resp);
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        userDAO.save(user);

        req.setAttribute("success", "Staff member " + fullName + " added successfully.");
        loadPage(req);
        req.getRequestDispatcher("/admin_staff.jsp").forward(req, resp);
    }

    private void updateStaff(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int    userId   = Integer.parseInt(req.getParameter("userId"));
        String fullName = ValidationUtil.sanitize(req.getParameter("fullName"));
        String email    = ValidationUtil.sanitize(req.getParameter("email"));
        String phone    = ValidationUtil.sanitize(req.getParameter("phone"));
        String role     = req.getParameter("role");
        boolean isActive = "true".equals(req.getParameter("isActive"));

        User user = userDAO.findById(userId);
        if (user == null) throw new Exception("User not found.");
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        user.setActive(isActive);
        userDAO.update(user);

        req.setAttribute("success", "Staff updated successfully.");
        loadPage(req);
        req.getRequestDispatcher("/admin_staff.jsp").forward(req, resp);
    }

    private void deleteStaff(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int  userId      = Integer.parseInt(req.getParameter("userId"));
        User sessionUser = (User) req.getSession().getAttribute("user");
        if (sessionUser.getUserId() == userId) throw new Exception("Cannot delete your own account.");
        userDAO.delete(userId);
        req.setAttribute("success", "Staff member deactivated.");
        loadPage(req);
        req.getRequestDispatcher("/admin_staff.jsp").forward(req, resp);
    }

    // ── Payment Method actions ────────────────────────────────────

    private void addMethod(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String name = ValidationUtil.sanitize(req.getParameter("methodName"));
        if (ValidationUtil.isNullOrEmpty(name)) {
            req.setAttribute("methodError", "Payment method name is required.");
            loadPage(req);
            req.getRequestDispatcher("/admin_staff.jsp").forward(req, resp);
            return;
        }
        PaymentMethod pm = new PaymentMethod();
        pm.setMethodName(name);
        paymentMethodDAO.save(pm);

        req.setAttribute("successMethod", "Payment method \"" + name + "\" added.");
        loadPage(req);
        req.getRequestDispatcher("/admin_staff.jsp").forward(req, resp);
    }

    private void updateMethod(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int    id   = Integer.parseInt(req.getParameter("methodId"));
        String name = ValidationUtil.sanitize(req.getParameter("methodName"));
        if (ValidationUtil.isNullOrEmpty(name)) {
            req.setAttribute("methodError", "Payment method name is required.");
            req.setAttribute("editMethod", paymentMethodDAO.findById(id));
            loadPage(req);
            req.getRequestDispatcher("/admin_staff.jsp").forward(req, resp);
            return;
        }
        PaymentMethod pm = new PaymentMethod();
        pm.setMethodId(id);
        pm.setMethodName(name);
        paymentMethodDAO.update(pm);

        req.setAttribute("successMethod", "Payment method updated.");
        loadPage(req);
        req.getRequestDispatcher("/admin_staff.jsp").forward(req, resp);
    }
}
