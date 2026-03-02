package com.oceanview.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Front Controller Pattern - routes all requests
 */
public class FrontControllerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        // Auth check - allow login page
        if (!"/login".equals(path) && !"/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
        }

        switch (path) {
            case "/login":
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                break;
            case "/dashboard":
                req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
                break;
            case "/reservations":
                new ReservationServlet().service(req, resp);
                break;
            case "/payment":
                new PaymentServlet().service(req, resp);
                break;
            case "/reports":
                new ReportServlet().service(req, resp);
                break;
            case "/admin":
                new AdminServlet().service(req, resp);
                break;
            case "/logout":
                HttpSession sess = req.getSession(false);
                if (sess != null) sess.invalidate();
                resp.sendRedirect(req.getContextPath() + "/login");
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/dashboard");
        }
    }
}