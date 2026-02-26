<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Dashboard" scope="request"/>
<jsp:include page="header.jsp"/>

<div class="page-header">
  <h1>📊 Dashboard</h1>
  <p>Welcome back, <strong>${sessionScope.userName}</strong></p>
</div>

<c:if test="${not empty error}">
  <div class="alert alert-danger"><span>⚠</span> ${error}</div>
</c:if>
<c:if test="${not empty success}">
  <div class="alert alert-success"><span>✓</span> ${success}</div>
</c:if>

<!-- Stats Cards -->
<div class="stats-grid">
  <div class="stat-card stat-blue">
    <div class="stat-icon">📋</div>
    <div class="stat-info">
      <h3>${fn:length(recentRes)}</h3>
      <p>Recent Reservations</p>
    </div>
  </div>
  <div class="stat-card stat-green">
    <div class="stat-icon">🛏</div>
    <div class="stat-info">
      <h3>Available Today</h3>
      <p>Check Room Availability</p>
    </div>
  </div>
  <div class="stat-card stat-orange">
    <div class="stat-icon">👥</div>
    <div class="stat-info">
      <h3>Active Guests</h3>
      <p>Currently Checked In</p>
    </div>
  </div>
  <div class="stat-card stat-purple">
    <div class="stat-icon">💰</div>
    <div class="stat-info">
      <h3>Revenue</h3>
      <p>View Reports</p>
    </div>
  </div>
</div>

<!-- Quick Actions -->
<div class="section">
  <h2>Quick Actions</h2>
  <div class="quick-actions">
    <a href="${pageContext.request.contextPath}/reservations?action=search-rooms" class="qa-btn qa-primary">
      <span>➕</span> New Reservation
    </a>
    <a href="${pageContext.request.contextPath}/reservations" class="qa-btn qa-info">
      <span>📋</span> All Reservations
    </a>
    <a href="${pageContext.request.contextPath}/reservations?status=CONFIRMED" class="qa-btn qa-warning">
      <span>✅</span> Confirmed
    </a>
    <a href="${pageContext.request.contextPath}/reservations?status=CHECKED_IN" class="qa-btn qa-success">
      <span>🏃</span> Checked In
    </a>
    <c:if test="${sessionScope.userRole eq 'ADMIN'}">
      <a href="${pageContext.request.contextPath}/reports" class="qa-btn qa-purple">
        <span>📈</span> Reports
      </a>
    </c:if>
  </div>
</div>

<!-- Recent Reservations -->
<div class="section">
  <h2>Recent Reservations</h2>
  <c:choose>
    <c:when test="${not empty recentRes}">
      <div class="table-wrap">
        <table class="data-table">
          <thead>
          <tr>
            <th>Reservation #</th><th>Guest</th><th>Room</th>
            <th>Check-In</th><th>Check-Out</th><th>Status</th><th>Action</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach var="r" items="${recentRes}">
            <tr>
              <td><strong>${r.reservationNumber}</strong></td>
              <td>${r.guestName}</td>
              <td>${r.roomNumber} (${r.typeName})</td>
              <td><fmt:formatDate value="${r.checkinDate}" pattern="dd MMM yyyy"/></td>
              <td><fmt:formatDate value="${r.checkoutDate}" pattern="dd MMM yyyy"/></td>
              <td><span class="badge badge-${r.status.toLowerCase()}">${r.status}</span></td>
              <td>
                <a href="${pageContext.request.contextPath}/reservations?action=view&number=${r.reservationNumber}"
                   class="btn btn-sm btn-info">View</a>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
    </c:when>
    <c:otherwise>
      <div class="empty-state">
        <p>No recent reservations found.</p>
        <a href="${pageContext.request.contextPath}/reservations?action=search-rooms" class="btn btn-primary">
          Make First Reservation
        </a>
      </div>
    </c:otherwise>
  </c:choose>
</div>

<jsp:include page="footer.jsp"/>
