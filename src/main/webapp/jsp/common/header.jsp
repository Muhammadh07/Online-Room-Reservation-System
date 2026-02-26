<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Grand Hotel — ${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<nav class="navbar">
    <div class="nav-brand">
        <span class="nav-icon">🏨</span>
        <span class="nav-title">Grand Hotel</span>
    </div>
    <ul class="nav-links">
        <li><a href="${pageContext.request.contextPath}/dashboard"
               class="${pageTitle eq 'Dashboard' ? 'active' : ''}">📊 Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/reservations"
               class="${pageTitle eq 'Reservations' ? 'active' : ''}">📋 Reservations</a></li>
        <li><a href="${pageContext.request.contextPath}/reservations?action=search-rooms"
               class="${pageTitle eq 'New Reservation' ? 'active' : ''}">➕ New Reservation</a></li>
        <c:if test="${sessionScope.userRole eq 'ADMIN'}">
            <li><a href="${pageContext.request.contextPath}/staff"
                   class="${pageTitle eq 'Staff' ? 'active' : ''}">👥 Staff</a></li>
            <li><a href="${pageContext.request.contextPath}/reports"
                   class="${pageTitle eq 'Reports' ? 'active' : ''}">📈 Reports</a></li>
        </c:if>
    </ul>
    <div class="nav-user">
    <span class="user-badge ${sessionScope.userRole eq 'ADMIN' ? 'badge-admin' : 'badge-staff'}">
        ${sessionScope.userRole}
    </span>
        <span class="user-name">👤 ${sessionScope.userName}</span>
        <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
    </div>
</nav>

<main class="main-content">
