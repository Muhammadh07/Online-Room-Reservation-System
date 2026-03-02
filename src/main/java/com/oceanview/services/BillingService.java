package com.oceanview.services;

import com.oceanview.dao.BillDAO;
import com.oceanview.dao.PaymentDAO;
import com.oceanview.model.Bill;
import com.oceanview.model.Payment;

import java.sql.SQLException;
import java.util.List;

public class BillingService {
    private static BillingService instance;
    private final BillDAO billDAO = new BillDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    private BillingService() {}

    public static synchronized BillingService getInstance() {
        if (instance == null) instance = new BillingService();
        return instance;
    }

    public Bill getBillByReservationId(int reservationId) throws SQLException {
        return billDAO.findByReservationId(reservationId);
    }

    public Bill getBillById(int billId) throws SQLException {
        return billDAO.findById(billId);
    }

    public List<Payment> getPaymentsForBill(int billId) throws SQLException {
        return paymentDAO.findByBillId(billId);
    }

    public double getBalanceDue(Bill bill) throws SQLException {
        return bill.getBalanceDue();
    }

    public long getPendingBillsCount() throws SQLException {
        return billDAO.getPendingBillsCount();
    }
}