<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav class="navbar">
    <span class="brand">&#127754; Ocean View Resort</span>
    <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
    <a href="${pageContext.request.contextPath}/reservations">Reservations</a>
    <a href="${pageContext.request.contextPath}/guests">Guests</a>
    <a href="${pageContext.request.contextPath}/payment">Payments</a>
    <a href="${pageContext.request.contextPath}/billing">Billing</a>
    <a href="${pageContext.request.contextPath}/reports">Reports</a>
    <c:if test="${sessionScope.user.role == 'ADMIN'}">
        <a href="${pageContext.request.contextPath}/admin">Admin</a>
    </c:if>
    <span class="user-info">
        &nbsp;|&nbsp; <c:out value="${sessionScope.user.fullName}"/> (${sessionScope.user.role})
    </span>
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
</nav>
