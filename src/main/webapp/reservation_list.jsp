<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Reservations - Ocean View Resort</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">
    <div class="page-title">Reservations</div>

    <c:if test="${not empty param.cancelled}">
        <div class="alert alert-success">&#10003; Reservation ${param.cancelled} has been cancelled.</div>
    </c:if>

    <div class="card">
        <div style="display:flex;gap:12px;align-items:center;flex-wrap:wrap">
            <form method="get" action="${pageContext.request.contextPath}/reservations" style="display:flex;gap:8px;flex:1">
                <input type="text" name="search" value="${search}" placeholder="Search by reservation no, guest name or phone..." style="flex:1">
                <select name="status">
                    <option value="">All Statuses</option>
                    <option value="CONFIRMED" ${statusFilter=='CONFIRMED'?'selected':''}>Confirmed</option>
                    <option value="CHECKED_IN" ${statusFilter=='CHECKED_IN'?'selected':''}>Checked In</option>
                    <option value="CHECKED_OUT" ${statusFilter=='CHECKED_OUT'?'selected':''}>Checked Out</option>
                    <option value="CANCELLED" ${statusFilter=='CANCELLED'?'selected':''}>Cancelled</option>
                </select>
                <button type="submit" class="btn btn-primary">Search</button>
                <a href="${pageContext.request.contextPath}/reservations" class="btn btn-secondary">Clear</a>
            </form>
            <a href="${pageContext.request.contextPath}/reservations?action=new" class="btn btn-success">+ New Reservation</a>
        </div>
    </div>

    <div class="card">
        <c:choose>
            <c:when test="${empty reservations}">
                <p style="color:#666;text-align:center;padding:20px">No reservations found.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <tr>
                        <th>Res. No</th><th>Guest</th><th>Phone</th><th>Room</th><th>Check-In</th><th>Check-Out</th><th>Nights</th><th>Status</th><th>Actions</th>
                    </tr>
                    <c:forEach var="r" items="${reservations}">
                        <tr>
                            <td><strong>${r.reservationNo}</strong></td>
                            <td>${r.guestName}</td>
                            <td>${r.guestPhone}</td>
                            <td>${r.roomNumber} (${r.roomTypeName})</td>
                            <td>${r.checkIn}</td>
                            <td>${r.checkOut}</td>
                            <td>${r.nights}</td>
                            <td><span class="badge badge-${r.status.toLowerCase()}">${r.status}</span></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/reservations?action=view&id=${r.reservationId}" class="btn btn-sm btn-primary">View</a>
                                <c:if test="${r.status == 'CONFIRMED'}">
                                    <a href="${pageContext.request.contextPath}/reservations?action=checkin&id=${r.reservationId}" class="btn btn-sm btn-success">Check In</a>
                                    <form method="post" action="${pageContext.request.contextPath}/reservations"
                                          style="display:inline"
                                          onsubmit="return confirm('Cancel reservation ${r.reservationNo}?')">
                                        <input type="hidden" name="action"        value="cancel">
                                        <input type="hidden" name="reservationNo" value="${r.reservationNo}">
                                        <input type="hidden" name="redirect"      value="list">
                                        <button type="submit" class="btn btn-sm btn-danger">Cancel</button>
                                    </form>
                                </c:if>
                                <c:if test="${r.status == 'CHECKED_IN'}">
                                    <a href="${pageContext.request.contextPath}/reservations?action=checkout&id=${r.reservationId}" class="btn btn-sm btn-secondary">Check Out</a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
                <p style="margin-top:10px;color:#666;font-size:0.9rem">Total: ${reservations.size()} reservation(s)</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
