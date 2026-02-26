package com.hotel.dao;

import com.hotel.model.Bill;
import java.util.List;
import java.util.Map;

public interface BillDAO {
    Bill         generateBill(int reservationId) throws Exception;
    Bill         findByReservationId(int reservationId);
    Bill         findByBillNumber(String billNumber);
    boolean      recordPayment(int billId, double amount, String method);
    List<Bill>   findAll();
    Map<String,Object> getRevenueSummary();
}