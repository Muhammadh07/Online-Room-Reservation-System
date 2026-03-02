package com.oceanview.servlets;

import com.oceanview.model.*;
import com.oceanview.services.BillingService;
import com.oceanview.services.PaymentService;
import com.oceanview.services.ReservationService;
import com.oceanview.util.ValidationUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PaymentServlet extends HttpServlet {

    private final PaymentService     paymentService     = PaymentService.getInstance();
    private final BillingService     billingService     = BillingService.getInstance();
    private final ReservationService reservationService = ReservationService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            loadStats(req);
            if ("record".equals(action)) {
                showPaymentForm(req, resp);
            } else {
                showPaymentSearch(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            loadStats(req);
            recordPayment(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    /** Loads stats + recent payments + payment methods — always shown in the page header. */
    private void loadStats(HttpServletRequest req) throws Exception {
        req.setAttribute("totalRevenue",       paymentService.getTotalRevenue());
        req.setAttribute("totalPaymentCount",  paymentService.getTotalPaymentCount());
        req.setAttribute("pendingBillsCount",  billingService.getPendingBillsCount());
        req.setAttribute("allRecentPayments",  paymentService.getRecentPayments(20));
        req.setAttribute("paymentMethods",     paymentService.getAllPaymentMethods());
    }

    private void showPaymentSearch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String resNo = req.getParameter("resNo");
        if (resNo != null && !resNo.trim().isEmpty()) {
            Reservation res = reservationService.getByReservationNo(resNo.trim());
            if (res == null) {
                req.setAttribute("error", "Reservation not found: " + ValidationUtil.sanitize(resNo));
            } else {
                Bill bill = billingService.getBillByReservationId(res.getReservationId());
                List<Payment> payments = bill != null
                        ? billingService.getPaymentsForBill(bill.getBillId())
                        : new ArrayList<>();
                req.setAttribute("reservation", res);
                req.setAttribute("bill", bill);
                req.setAttribute("payments", payments);
            }
        }
        req.getRequestDispatcher("/payment_form.jsp").forward(req, resp);
    }

    private void showPaymentForm(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String idStr = req.getParameter("billId");
        if (idStr == null || idStr.isEmpty()) throw new Exception("Bill ID is required.");
        int billId = Integer.parseInt(idStr);
        Bill bill = billingService.getBillById(billId);
        if (bill == null) throw new Exception("Bill not found.");
        List<Payment> payments = billingService.getPaymentsForBill(billId);
        Reservation res = reservationService.getById(bill.getReservationId());
        req.setAttribute("bill", bill);
        req.setAttribute("reservation", res);
        req.setAttribute("payments", payments);
        req.getRequestDispatcher("/payment_form.jsp").forward(req, resp);
    }

    private void recordPayment(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        List<String> errors = new ArrayList<>();
        String billIdStr   = req.getParameter("billId");
        String methodIdStr = req.getParameter("methodId");
        String amountStr   = req.getParameter("amount");
        String referenceNo = ValidationUtil.sanitize(req.getParameter("referenceNo"));

        int billId = 0;
        try { billId = Integer.parseInt(billIdStr); } catch (Exception e) { errors.add("Invalid bill."); }

        int methodId = 0;
        try { methodId = Integer.parseInt(methodIdStr); } catch (Exception e) { errors.add("Payment method is required."); }

        double amount = 0;
        if (!ValidationUtil.isPositiveDouble(amountStr)) {
            errors.add("Payment amount must be a positive number.");
        } else {
            amount = Double.parseDouble(amountStr);
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            if (billId > 0) {
                Bill bill = billingService.getBillById(billId);
                req.setAttribute("bill", bill);
                if (bill != null) {
                    req.setAttribute("reservation", reservationService.getById(bill.getReservationId()));
                    req.setAttribute("payments", billingService.getPaymentsForBill(billId));
                }
            }
            req.getRequestDispatcher("/payment_form.jsp").forward(req, resp);
            return;
        }

        User user = (User) req.getSession().getAttribute("user");
        paymentService.recordPayment(billId, methodId, amount, referenceNo, user.getUserId());

        Bill updatedBill = billingService.getBillById(billId);
        Reservation res  = reservationService.getById(updatedBill.getReservationId());
        List<Payment> payments = billingService.getPaymentsForBill(billId);

        // Reload stats after payment recorded
        loadStats(req);

        req.setAttribute("success",
                "Payment of $" + String.format("%.2f", amount) + " recorded successfully. " +
                "Balance due: $" + String.format("%.2f", updatedBill.getBalanceDue()));
        req.setAttribute("bill", updatedBill);
        req.setAttribute("reservation", res);
        req.setAttribute("payments", payments);
        req.getRequestDispatcher("/payment_form.jsp").forward(req, resp);
    }
}
