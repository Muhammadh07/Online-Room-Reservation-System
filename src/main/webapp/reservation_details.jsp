<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Reservation Details - Ocean View Resort</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .detail-table td:first-child { font-weight:600; width:180px; color:#555; }
        .detail-table td { padding:8px 12px; border-bottom:1px solid #eee; }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">

    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <div class="page-title">
        Reservation: ${reservation.reservationNo}
        <span class="badge badge-${reservation.status.toLowerCase()}" style="font-size:1rem;margin-left:12px">${reservation.status}</span>
    </div>

    <div style="display:grid;grid-template-columns:1fr 1fr;gap:20px">
        <div class="card">
            <div class="card-title">Reservation Details</div>
            <table class="detail-table">
                <tr><td>Reservation No</td><td>${reservation.reservationNo}</td></tr>
                <tr><td>Guest Name</td><td>${reservation.guestName}</td></tr>
                <tr><td>Guest Phone</td><td>${reservation.guestPhone}</td></tr>
                <tr><td>Room</td><td>${reservation.roomNumber} - ${reservation.roomTypeName}</td></tr>
                <tr><td>Check-In</td><td>${reservation.checkIn}</td></tr>
                <tr><td>Check-Out</td><td>${reservation.checkOut}</td></tr>
                <tr><td>Nights</td><td>${reservation.nights}</td></tr>
                <tr><td>Adults / Children</td><td>${reservation.numAdults} / ${reservation.numChildren}</td></tr>
                <tr><td>Created By</td><td>${reservation.createdByName}</td></tr>
                <tr><td>Created At</td><td>${reservation.createdAt}</td></tr>
                <tr><td>Special Requests</td><td>${reservation.specialRequests}</td></tr>
            </table>
        </div>

        <div class="card">
            <div class="card-title">Bill Summary</div>
            <c:if test="${not empty bill}">
                <table class="detail-table">
                    <tr><td>Room Rate/Night</td><td>$<fmt:formatNumber value="${reservation.roomPrice}" pattern="0.00"/></td></tr>
                    <tr><td>Nights</td><td>${reservation.nights}</td></tr>
                    <tr><td>Subtotal</td><td>$<fmt:formatNumber value="${bill.totalAmount - bill.taxAmount}" pattern="0.00"/></td></tr>
                    <tr><td>Tax (10%)</td><td>$<fmt:formatNumber value="${bill.taxAmount}" pattern="0.00"/></td></tr>
                    <tr><td>Discount</td><td>$<fmt:formatNumber value="${bill.discount}" pattern="0.00"/></td></tr>
                    <tr><td><strong>Total</strong></td><td><strong>$<fmt:formatNumber value="${bill.totalAmount}" pattern="0.00"/></strong></td></tr>
                    <tr><td><strong>Balance Due</strong></td><td><strong style="color:${bill.balanceDue > 0 ? '#e74c3c' : '#27ae60'}">$<fmt:formatNumber value="${bill.balanceDue}" pattern="0.00"/></strong></td></tr>
                </table>
                <c:if test="${bill.balanceDue > 0 && reservation.status != 'CANCELLED'}">
                    <a href="${pageContext.request.contextPath}/payment?action=record&billId=${bill.billId}" class="btn btn-success" style="margin-top:14px">Record Payment</a>
                </c:if>
            </c:if>
        </div>
    </div>

    <c:if test="${not empty payments}">
        <div class="card">
            <div class="card-title">Payment History</div>
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
        </div>
    </c:if>

    <div style="margin-top:10px;display:flex;gap:10px;flex-wrap:wrap">
        <a href="${pageContext.request.contextPath}/reservations" class="btn btn-secondary">← Back to List</a>
        <c:if test="${reservation.status == 'CONFIRMED'}">
            <a href="${pageContext.request.contextPath}/reservations?action=checkin&id=${reservation.reservationId}" class="btn btn-success">Check In</a>
        </c:if>
        <c:if test="${reservation.status == 'CHECKED_IN'}">
            <a href="${pageContext.request.contextPath}/reservations?action=checkout&id=${reservation.reservationId}" class="btn btn-secondary">Check Out</a>
        </c:if>
        <c:if test="${reservation.status != 'CANCELLED' && reservation.status != 'CHECKED_OUT'}">
            <form method="post" action="${pageContext.request.contextPath}/reservations"
                  style="display:inline"
                  onsubmit="return confirm('Cancel reservation ${reservation.reservationNo}? This cannot be undone.')">
                <input type="hidden" name="action"        value="cancel">
                <input type="hidden" name="reservationNo" value="${reservation.reservationNo}">
                <input type="hidden" name="redirect"      value="detail">
                <button type="submit" class="btn btn-danger">&#10007; Cancel Reservation</button>
            </form>
        </c:if>
    </div>
</div>
</body>
</html>
