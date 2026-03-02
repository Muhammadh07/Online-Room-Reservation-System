<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Cancel Reservation - Ocean View Resort</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">
    <div class="page-title">Cancel Reservation</div>

    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <div class="card" style="max-width:500px">
        <div class="card-title">Enter Reservation Number to Cancel</div>
        <div class="alert alert-info">Note: Reservations with outstanding balance cannot be cancelled.</div>
        <form method="post" action="${pageContext.request.contextPath}/reservations" onsubmit="return confirm('Are you sure you want to cancel this reservation?')">
            <input type="hidden" name="action" value="cancel">
            <div class="form-group">
                <label>Reservation Number *</label>
                <input type="text" name="reservationNo" placeholder="e.g. OVR1234567890" required autofocus
                       value="${not empty param.resNo ? param.resNo : ''}">
            </div>
            <button type="submit" class="btn btn-danger">Cancel Reservation</button>
            <a href="${pageContext.request.contextPath}/reservations" class="btn btn-secondary" style="margin-left:10px">Back</a>
        </form>
    </div>
</div>
</body>
</html>
