package com.hotel.servlets;

import com.hotel.handlers.*;
import com.hotel.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * MainServlet — FRONT CONTROLLER PATTERN
 *
 * Single entry point for all requests. Routes to appropriate handlers.
 * Design Pattern Rationale:
 *   - Front Controller: centralises request processing, auth checks, routing.
 *   - Command: delegates to handler objects (RequestHandler implementations).
 */
@WebServlet(urlPatterns = {
        "/login", "/logout", "/dashboard",
        "/reservations", "/staff", "/reports", "/password"
})
public class MainServlet extends HttpServlet {

    // Handler registry — Command Pattern
    private final Map<String, RequestHandler> handlers = new HashMap<>();

    @Override
    public void init() {
        handlers.put("/reservations", new ReservationHandler());
        handlers.put("/staff",        new StaffHandler());
        handlers.put("/reports",      new ReportHandler());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        process(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        process(req, res);
    }

    private void process(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String path = req.getServletPath();

        // ---- Login / Logout (no auth needed) ----
        if ("/login".equals(path)) {
            if ("POST".equals(req.getMethod())) {
                try {
                    String view = new LoginHandler().handle(req, res);
                    navigate(view, req, res);
                } catch (Exception e) {
                    req.setAttribute("error", "System error. Please try again.");
                    forward("/jsp/common/login.jsp", req, res);
                }
            } else {
                forward("/jsp/common/login.jsp", req, res);
            }
            return;
        }

        if ("/logout".equals(path)) {
            HttpSession s = req.getSession(false);
            if (s != null) s.invalidate();
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // ---- Auth guard ----
        if (!isLoggedIn(req)) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = loggedUser(req);

        // ---- Dashboard ----
        if ("/dashboard".equals(path)) {
            req.setAttribute("recentRes",
                    com.hotel.services.ReservationService.getInstance().recentReservations(5));
            forward("/jsp/common/dashboard.jsp", req, res);
            return;
        }

        // ---- Change password ----
        if ("/password".equals(path) && "POST".equals(req.getMethod())) {
            handlePasswordChange(req, res, user);
            return;
        }

        // ---- Admin-only guard ----
        if ("/staff".equals(path) || "/reports".equals(path)) {
            if (!user.isAdmin()) {
                req.setAttribute("error", "Access denied. Admin only.");
                forward("/jsp/common/dashboard.jsp", req, res);
                return;
            }
        }

        // ---- Dispatch to handler ----
        RequestHandler handler = handlers.get(path);
        if (handler != null) {
            try {
                String view = handler.handle(req, res);
                navigate(view, req, res);
            } catch (Exception e) {
                req.setAttribute("error", "Error: " + e.getMessage());
                forward("/jsp/common/dashboard.jsp", req, res);
            }
        } else {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handlePasswordChange(HttpServletRequest req, HttpServletResponse res, User user)
            throws ServletException, IOException {
        try {
            com.hotel.services.AuthService.getInstance().changePassword(
                    user.getUserId(),
                    req.getParameter("currentPassword"),
                    req.getParameter("newPassword")
            );
            req.setAttribute("success", "Password changed successfully.");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }
        forward("/jsp/common/profile.jsp", req, res);
    }

    private void navigate(String view, HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (view.startsWith("redirect:")) {
            res.sendRedirect(req.getContextPath() + "/" + view.substring(9));
        } else {
            forward(view, req, res);
        }
    }

    private void forward(String path, HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.getRequestDispatcher(path).forward(req, res);
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s != null && s.getAttribute("loggedUser") != null;
    }

    private User loggedUser(HttpServletRequest req) {
        return (User) req.getSession(false).getAttribute("loggedUser");
    }
}