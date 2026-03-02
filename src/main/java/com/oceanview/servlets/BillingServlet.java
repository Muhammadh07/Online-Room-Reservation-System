package com.oceanview.servlets;

import com.oceanview.dao.BillDAO;
import com.oceanview.dao.PaymentDAO;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.model.Bill;
import com.oceanview.model.Payment;
import com.oceanview.model.Reservation;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class BillingServlet extends HttpServlet {

    private final BillDAO billDAO = new BillDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
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
                case "view": viewBill(req, resp); break;
                default:     listBills(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    private void listBills(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        List<Bill> bills = billDAO.findAllWithDetails();
        String filter = req.getParameter("filter");
        if ("paid".equals(filter)) {
            bills = bills.stream().filter(b -> b.getBalanceDue() <= 0).collect(Collectors.toList());
        } else if ("unpaid".equals(filter)) {
            bills = bills.stream().filter(b -> b.getBalanceDue() > 0).collect(Collectors.toList());
        }
        req.setAttribute("bills", bills);
        req.setAttribute("filter", filter);
        req.getRequestDispatcher("/billing_list.jsp").forward(req, resp);
    }

    private void viewBill(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int billId = Integer.parseInt(req.getParameter("id"));
        Bill bill = billDAO.findById(billId);
        if (bill == null) {
            req.setAttribute("error", "Bill not found.");
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
            return;
        }
        Reservation reservation = reservationDAO.findById(bill.getReservationId());
        List<Payment> payments = paymentDAO.findByBillId(billId);
        req.setAttribute("bill", bill);
        req.setAttribute("reservation", reservation);
        req.setAttribute("payments", payments);
        req.getRequestDispatcher("/bill.jsp").forward(req, resp);
    }
}
