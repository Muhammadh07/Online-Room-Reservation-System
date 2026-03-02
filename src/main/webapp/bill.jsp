<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Invoice #${bill.billId} - Ocean View Resort</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .invoice-header { display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:20px; }
        .invoice-meta { text-align:right;color:#555;font-size:0.9rem; }
        .detail-table td:first-child { font-weight:600; width:170px; color:#555; }
        .detail-table td { padding:8px 12px; border-bottom:1px solid #eee; }
        @media print { .no-print { display:none; } }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">
    <div class="card">
        <div class="invoice-header">
            <div>
                <div style="font-size:1.6rem;font-weight:700;color:#1a3a5c">&#127754; Ocean View Resort</div>
                <div style="color:#555;margin-top:4px">Invoice #${bill.billId}</div>
            </div>
            <div class="invoice-meta">
                <div>Generated: ${bill.generatedAt}</div>
                <div>Reservation: <strong>${bill.reservationNo}</strong></div>
                <c:if test="${not empty bill.guestName}">
                    <div>Guest: <strong>${bill.guestName}</strong></div>
                </c:if>
            </div>
        </div>

        <c:if test="${not empty reservation}">
            <div style="display:grid;grid-template-columns:1fr 1fr;gap:20px">
                <div>
                    <div class="card-title">Reservation Details</div>
                    <table class="detail-table">
                        <tr><td>Guest</td><td>${reservation.guestName}</td></tr>
                        <tr><td>Phone</td><td>${reservation.guestPhone}</td></tr>
                        <tr><td>Room</td><td>${reservation.roomNumber} (${reservation.roomTypeName})</td></tr>
                        <tr><td>Check-In</td><td>${reservation.checkIn}</td></tr>
                        <tr><td>Check-Out</td><td>${reservation.checkOut}</td></tr>
                        <tr><td>Nights</td><td>${reservation.nights}</td></tr>
                        <tr><td>Adults / Children</td><td>${reservation.numAdults} / ${reservation.numChildren}</td></tr>
                        <tr><td>Status</td><td><span class="badge badge-${reservation.status.toLowerCase()}">${reservation.status}</span></td></tr>
                    </table>
                </div>

                <div>
                    <div class="card-title">Bill Summary</div>
                    <table class="detail-table">
                        <tr><td>Room Rate/Night</td><td>$<fmt:formatNumber value="${reservation.roomPrice}" pattern="0.00"/></td></tr>
                        <tr><td>Nights</td><td>${reservation.nights}</td></tr>
                        <tr><td>Subtotal</td><td>$<fmt:formatNumber value="${bill.totalAmount - bill.taxAmount}" pattern="0.00"/></td></tr>
                        <tr><td>Tax (10%)</td><td>$<fmt:formatNumber value="${bill.taxAmount}" pattern="0.00"/></td></tr>
                        <tr><td>Discount</td><td>$<fmt:formatNumber value="${bill.discount}" pattern="0.00"/></td></tr>
                        <tr><td><strong>Total</strong></td><td><strong>$<fmt:formatNumber value="${bill.totalAmount}" pattern="0.00"/></strong></td></tr>
                        <tr><td><strong>Balance Due</strong></td>
                            <td><strong style="color:${bill.balanceDue > 0 ? '#e74c3c' : '#27ae60'}">
                                $<fmt:formatNumber value="${bill.balanceDue}" pattern="0.00"/>
                            </strong></td>
                        </tr>
                    </table>
                    <c:if test="${bill.balanceDue > 0}">
                        <a href="${pageContext.request.contextPath}/payment?action=record&billId=${bill.billId}"
                           class="btn btn-success no-print" style="margin-top:14px">Record Payment</a>
                    </c:if>
                </div>
            </div>
        </c:if>

        <c:if test="${not empty payments}">
            <div class="card-title" style="margin-top:20px">Payment History</div>
            <table>
                <tr><th>#</th><th>Date</th><th>Method</th><th>Amount</th><th>Reference</th><th>Recorded By</th></tr>
                <c:forEach var="p" items="${payments}" varStatus="s">
                    <tr>
                        <td>${s.count}</td>
                        <td>${p.paymentDate}</td>
                        <td>${p.methodName}</td>
                        <td>$<fmt:formatNumber value="${p.amount}" pattern="0.00"/></td>
                        <td>${p.referenceNo}</td>
                        <td>${p.recordedByName}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>

        <div style="margin-top:20px;display:flex;gap:10px" class="no-print">
            <a href="${pageContext.request.contextPath}/billing" class="btn btn-secondary">← Back to Billing</a>
            <c:if test="${not empty reservation}">
                <a href="${pageContext.request.contextPath}/reservations?action=view&id=${reservation.reservationId}" class="btn btn-primary">View Reservation</a>
            </c:if>
            <button onclick="window.print()" class="btn btn-secondary">Print Invoice</button>
        </div>
    </div>
</div>
</body>
</html>
