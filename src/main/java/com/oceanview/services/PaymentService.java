package com.oceanview.services;

import com.oceanview.dao.BillDAO;
import com.oceanview.dao.PaymentDAO;
import com.oceanview.model.Bill;
import com.oceanview.model.Payment;

import java.sql.SQLException;
import java.util.List;

public class PaymentService {
    private static PaymentService instance;
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final BillDAO billDAO = new BillDAO();

    private PaymentService() {}

    public static synchronized PaymentService getInstance() {
        if (instance == null) instance = new PaymentService();
        return instance;
    }

    public Payment recordPayment(int billId, int methodId, double amount,
                                 String referenceNo, int recordedBy) throws Exception {
        Bill bill = billDAO.findById(billId);
        if (bill == null) throw new Exception("Bill not found.");
        if (amount <= 0) throw new Exception("Payment amount must be positive.");
        if (amount > bill.getBalanceDue() + 0.001)
            throw new Exception("Payment amount exceeds balance due of $" + String.format("%.2f", bill.getBalanceDue()));

        Payment payment = new Payment();
        payment.setBillId(billId);
        payment.setMethodId(methodId);
        payment.setAmount(amount);
        payment.setReferenceNo(referenceNo);
        payment.setRecordedBy(recordedBy);
        paymentDAO.save(payment);

        // Update balance
        double newBalance = Math.max(0, bill.getBalanceDue() - amount);
        billDAO.updateBalance(billId, newBalance);

        // Reload with method name
        List<Payment> payments = paymentDAO.findByBillId(billId);
        return payments.stream().filter(p -> p.getPaymentId() == payment.getPaymentId())
                .findFirst().orElse(payment);
    }

    public List<java.util.Map<String,String>> getAllPaymentMethods() throws SQLException {
        return paymentDAO.findAllPaymentMethods();
    }

    public double getTotalRevenue() throws SQLException {
        return paymentDAO.getTotalRevenue();
    }

    public long getTotalPaymentCount() throws SQLException {
        return paymentDAO.getTotalPaymentCount();
    }

    public List<Payment> getRecentPayments(int limit) throws SQLException {
        return paymentDAO.findRecent(limit);
    }

    public List<Payment> getPaymentReport(String from, String to) throws SQLException {
        return paymentDAO.findByDateRange(from, to);
    }

    public List<java.util.Map<String,Object>> getMethodSummary() throws SQLException {
        return paymentDAO.getPaymentMethodSummary();
    }
}