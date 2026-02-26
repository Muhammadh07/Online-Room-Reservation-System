<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="View Reservation" scope="request"/>
<jsp:include page="header.jsp"/>

<div class="page-header">
  <h1>🔍 Reservation Details</h1>
  <a href="${pageContext.request.contextPath}/reservations" class="btn btn-secondary">← Back</a>
</div>

<c:if test="${not empty error}">
  <div class="alert alert-danger"><span>⚠</span> ${error}</div>
</c:if>

<c:if test="${not empty reservation}">
  <div class="card">
    <div class="card-header">
      <h2>Reservation: ${reservation.reservationNumber}</h2>
      <span class="badge badge-${reservation.status.toLowerCase()}">${reservation.status}</span>
    </div>
    <div class="card-body">
      <div class="form-row">
        <div class="detail-group">
          <h3>Guest Information</h3>
          <table class="detail-table">
            <tr><td>Guest Name</td><td><strong>${reservation.guestName}</strong></td></tr>
            <tr><td>Contact</td><td>${reservation.contactNumber}</td></tr>
          </table>
        </div>
        <div class="detail-group">
          <h3>Room Information</h3>
          <table class="detail-table">
            <tr><td>Room</td><td><strong>${reservation.roomNumber}</strong></td></tr>
            <tr><td>Type</td><td>${reservation.typeName}</td></tr>
            <tr><td>Floor</td><td>Floor ${reservation.roomNumber.substring(0,1)}</td></tr>
            <tr><td>Rate/Night</td>
              <td>LKR <fmt:formatNumber value="${reservation.ratePerNight}" pattern="#,##0.00"/></td></tr>
          </table>
        </div>
        <div class="detail-group">
          <h3>Booking Details</h3>
          <table class="detail-table">
            <tr><td>Check-In</td>
              <td><strong><fmt:formatDate value="${reservation.checkinDate}" pattern="dd MMM yyyy"/></strong></td></tr>
            <tr><td>Check-Out</td>
              <td><strong><fmt:formatDate value="${reservation.checkoutDate}" pattern="dd MMM yyyy"/></strong></td></tr>
            <tr><td>Nights</td><td>${reservation.nights}</td></tr>
            <tr><td>Est. Total</td>
              <td><strong>LKR <fmt:formatNumber value="${reservation.nights * reservation.ratePerNight * 1.1}" pattern="#,##0.00"/></strong></td></tr>
            <tr><td>Created By</td><td>${reservation.staffName}</td></tr>
            <tr><td>Created At</td>
              <td><fmt:formatDate value="${reservation.createdAt}" pattern="dd MMM yyyy HH:mm"/></td></tr>
          </table>
        </div>
      </div>

      <!-- Actions -->
      <div class="form-actions" style="margin-top:20px">
        <c:if test="${reservation.status eq 'CONFIRMED'}">
          <form method="POST" action="${pageContext.request.contextPath}/reservations"
                style="display:inline"
                onsubmit="return confirm('Check in ${reservation.guestName}?')">
            <input type="hidden" name="action" value="checkin">
            <input type="hidden" name="number" value="${reservation.reservationNumber}">
            <button type="submit" class="btn btn-success">✅ Check In</button>
          </form>
          <form method="POST" action="${pageContext.request.contextPath}/reservations"
                style="display:inline"
                onsubmit="return confirm('Cancel this reservation?')">
            <input type="hidden" name="action" value="cancel">
            <input type="hidden" name="number" value="${reservation.reservationNumber}">
            <button type="submit" class="btn btn-danger">✖ Cancel</button>
          </form>
        </c:if>
        <c:if test="${reservation.status eq 'CHECKED_IN'}">
          <form method="POST" action="${pageContext.request.contextPath}/reservations"
                style="display:inline"
                onsubmit="return confirm('Checkout and generate bill?')">
            <input type="hidden" name="action" value="checkout">
            <input type="hidden" name="number" value="${reservation.reservationNumber}">
            <button type="submit" class="btn btn-warning">🏁 Checkout & Generate Bill</button>
          </form>
        </c:if>
      </div>
    </div>
  </div>
</c:if>

<style>
  .detail-group { flex: 1; min-width: 220px; }
  .detail-group h3 { font-size: 14px; color: var(--primary); margin-bottom: 10px;
    border-bottom: 1px solid var(--border); padding-bottom: 5px; }
  .detail-table { width: 100%; font-size: 13px; }
  .detail-table td { padding: 7px 10px; }
  .detail-table td:first-child { color: var(--text-muted); width: 110px; font-weight: 600; }
  .detail-table tr:nth-child(even) { background: #f5f5f5; }
</style>

<jsp:include page="footer.jsp"/>
