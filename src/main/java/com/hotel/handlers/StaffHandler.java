package com.hotel.handlers;

import com.hotel.model.User;
import com.hotel.services.AuthService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * StaffHandler — COMMAND PATTERN concrete command.
 * Admin-only staff management: add, update, deactivate.
 */
public class StaffHandler implements RequestHandler {

    private final AuthService authService = AuthService.getInstance();

    @Override
    public String handle(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "add":        return handleAdd(req);
            case "edit":       return handleEdit(req);
            case "update":     return handleUpdate(req);
            case "deactivate": return handleDeactivate(req);
            default:           return handleList(req);
        }
    }

    private String handleList(HttpServletRequest req) {
        List<User> staff = authService.findAll();
        req.setAttribute("staffList", staff);
        return "/jsp/admin/staff.jsp";
    }

    private String handleAdd(HttpServletRequest req) {
        try {
            authService.createStaff(
                    req.getParameter("username"),
                    req.getParameter("password"),
                    req.getParameter("role"),
                    req.getParameter("fullName"),
                    req.getParameter("email")
            );
            req.setAttribute("success", "Staff account created successfully.");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("formData", req.getParameterMap());
        }
        return handleList(req);
    }

    private String handleEdit(HttpServletRequest req) {
        int id = Integer.parseInt(req.getParameter("id"));
        req.setAttribute("editUser", authService.findById(id));
        return handleList(req);
    }

    private String handleUpdate(HttpServletRequest req) {
        try {
            User u = authService.findById(Integer.parseInt(req.getParameter("userId")));
            u.setFullName(req.getParameter("fullName"));
            u.setEmail(req.getParameter("email"));
            u.setRole(req.getParameter("role"));
            authService.updateUser(u);
            req.setAttribute("success", "Staff updated successfully.");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }
        return handleList(req);
    }

    private String handleDeactivate(HttpServletRequest req) {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            authService.deactivateUser(id);
            req.setAttribute("success", "Staff account deactivated.");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }
        return handleList(req);
    }
}