<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Guest Profile - Ocean View Resort</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .detail-table td:first-child { font-weight:600; width:160px; color:#555; }
        .detail-table td { padding:8px 12px; border-bottom:1px solid #eee; }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">
    <div class="page-title">Guest: ${guest.fullName}</div>

    <div style="display:grid;grid-template-columns:1fr 2fr;gap:20px">
        <div class="card">
            <div class="card-title">Guest Details</div>
            <table class="detail-table">
                <tr><td>Full Name</td><td>${guest.fullName}</td></tr>
                <tr><td>Phone</td><td>${guest.phone}</td></tr>
                <tr><td>Email</td><td>${guest.email}</td></tr>
                <tr><td>NIC / Passport</td><td>${guest.nicPassport}</td></tr>
                <tr><td>Address</td><td>${guest.address}</td></tr>
            </table>
            <div style="margin-top:14px;display:flex;gap:8px">
                <a href="${pageContext.request.contextPath}/guests?action=edit&id=${guest.guestId}" class="btn btn-secondary">Edit Guest</a>
                <a href="${pageContext.request.contextPath}/guests" class="btn btn-secondary">← Back</a>
            </div>
        </div>

        <div class="card">
            <div class="card-title">Reservation History (${reservations.size()})</div>
            <c:choose>
                <c:when test="${empty reservations}">
                    <p style="color:#666;padding:10px">No reservations found for this guest.</p>
                </c:when>
                <c:otherwise>
                    <table>
                        <tr>
                            <th>Res. No</th><th>Room</th><th>Check-In</th><th>Check-Out</th><th>Nights</th><th>Status</th><th></th>
                        </tr>
                        <c:forEach var="r" items="${reservations}">
                            <tr>
                                <td><strong>${r.reservationNo}</strong></td>
                                <td>${r.roomNumber} (${r.roomTypeName})</td>
                                <td>${r.checkIn}</td>
                                <td>${r.checkOut}</td>
                                <td>${r.nights}</td>
                                <td><span class="badge badge-${r.status.toLowerCase()}">${r.status}</span></td>
                                <td><a href="${pageContext.request.contextPath}/reservations?action=view&id=${r.reservationId}" class="btn btn-sm btn-primary">View</a></td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
</body>
</html>
