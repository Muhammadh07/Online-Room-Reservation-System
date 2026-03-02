package com.oceanview.servlets;

import com.oceanview.dao.ReservationDAO;
import com.oceanview.model.*;
import com.oceanview.services.PaymentService;
import com.oceanview.services.ReservationService;
import com.oceanview.util.ValidationUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ReportServlet extends HttpServlet {

    private final ReservationService reservationService = ReservationService.getInstance();
    private final PaymentService paymentService = PaymentService.getInstance();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getParameter("type");
        if (type == null) type = "menu";
        try {
            switch (type) {
                case "occupancy":    occupancyReport(req, resp); break;
                case "revenue":      revenueReport(req, resp); break;
                case "payment":      paymentReport(req, resp); break;
                case "checkins":     checkInsReport(req, resp); break;
                default:             showMenu(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    private void showMenu(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.getRequestDispatcher("/reports.jsp").forward(req, resp);
    }

    private void occupancyReport(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        if (!ValidationUtil.isValidDate(from) || !ValidationUtil.isValidDate(to)) {
            from = LocalDate.now().withDayOfMonth(1).toString();
            to = LocalDate.now().toString();
        }
        List<Reservation> reservations = reservationDAO.findByDateRange(from, to);
        req.setAttribute("reservations", reservations);
        req.setAttribute("from", from);
        req.setAttribute("to", to);
        req.setAttribute("reportType", "occupancy");
        req.getRequestDispatcher("/reports.jsp").forward(req, resp);
    }

    private void revenueReport(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        if (!ValidationUtil.isValidDate(from) || !ValidationUtil.isValidDate(to)) {
            from = LocalDate.now().withDayOfMonth(1).toString();
            to = LocalDate.now().toString();
        }
        List<Payment> payments = paymentService.getPaymentReport(from, to);
        double totalRevenue = payments.stream().mapToDouble(Payment::getAmount).sum();
        req.setAttribute("payments", payments);
        req.setAttribute("totalRevenue", totalRevenue);
        req.setAttribute("from", from);
        req.setAttribute("to", to);
        req.setAttribute("reportType", "revenue");
        req.getRequestDispatcher("/reports.jsp").forward(req, resp);
    }

    private void paymentReport(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        List<java.util.Map<String,Object>> summary = paymentService.getMethodSummary();
        req.setAttribute("paymentSummary", summary);
        req.setAttribute("reportType", "payment");
        req.getRequestDispatcher("/reports.jsp").forward(req, resp);
    }

    private void checkInsReport(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String date = req.getParameter("date");
        if (!ValidationUtil.isValidDate(date)) date = LocalDate.now().toString();
        List<Reservation> reservations = reservationDAO.findByDateRange(date, date);
        req.setAttribute("reservations", reservations);
        req.setAttribute("date", date);
        req.setAttribute("reportType", "checkins");
        req.getRequestDispatcher("/reports.jsp").forward(req, resp);
    }
}