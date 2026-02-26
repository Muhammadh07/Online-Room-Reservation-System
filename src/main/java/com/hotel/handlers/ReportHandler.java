package com.hotel.handlers;

import com.hotel.services.ReservationService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * ReportHandler — COMMAND PATTERN concrete command.
 * Generates decision-making reports for Admin.
 */
public class ReportHandler implements RequestHandler {

    private final ReservationService svc = ReservationService.getInstance();

    @Override
    public String handle(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String type = req.getParameter("type");
        if (type == null) type = "summary";

        switch (type) {
            case "revenue":     return handleRevenue(req);
            case "occupancy":   return handleOccupancy(req);
            default:            return handleSummary(req);
        }
    }

    private String handleSummary(HttpServletRequest req) {
        req.setAttribute("confirmed",   svc.findByStatus("CONFIRMED").size());
        req.setAttribute("checkedIn",   svc.findByStatus("CHECKED_IN").size());
        req.setAttribute("checkedOut",  svc.findByStatus("CHECKED_OUT").size());
        req.setAttribute("cancelled",   svc.findByStatus("CANCELLED").size());
        req.setAttribute("recentRes",   svc.recentReservations(10));
        Map<String,Object> revenue = svc.getRevenueSummary();
        req.setAttribute("revenue", revenue);
        return "/jsp/admin/reports.jsp";
    }

    private String handleRevenue(HttpServletRequest req) {
        req.setAttribute("bills",   svc.getAllBills());
        req.setAttribute("revenue", svc.getRevenueSummary());
        return "/jsp/admin/reports.jsp";
    }

    private String handleOccupancy(HttpServletRequest req) {
        req.setAttribute("checkedIn",  svc.findByStatus("CHECKED_IN"));
        req.setAttribute("confirmed",  svc.findByStatus("CONFIRMED"));
        return "/jsp/admin/reports.jsp";
    }
}