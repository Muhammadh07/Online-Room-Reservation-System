<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Reservations" scope="request"/>
<jsp:include page="header.jsp"/>

<div class="page-header">
    <h1>📋 Reservations</h1>
    <a href="${pageContext.request.contextPath}/reservations?action=search-rooms"
       class="btn btn-primary">➕ New Reservation</a>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-danger"><span>⚠</span> ${error}</div>
</c:if>
<c:if test="${not empty success}">
    <div class="alert alert-success"><span>✓</span> ${success}</div>
</c:if>

<!-- Filter / Search Bar -->
<div class="card">
    <div class="card-body filter-bar">
        <form method="GET" action="${pageContext.request.contextPath}/reservations" class="filter-form">
            <input type="text" name="search" placeholder="Search by guest name..."
                   value="${search}" class="form-control search-input">
            <select name="status" class="form-control">
                <option value="">All Status</option>
                <option value="CONFIRMED"  ${statusFilter eq 'CONFIRMED'  ? 'selected' : ''}>Confirmed</option>
                <option value="CHECKED_IN" ${statusFilter eq 'CHECKED_IN' ? 'selected' : ''}>Checked In</option>
                <option value="CHECKED_OUT"${statusFilter eq 'CHECKED_OUT'? 'selected' : ''}>Checked Out</option>
                <option value="CANCELLED"  ${statusFilter eq 'CANCELLED'  ? 'selected' : ''}>Cancelled</option>
            </select>
            <button type="submit" class="btn btn-primary">🔍 Filter</button>
            <a href="${pageContext.request.contextPath}/reservations" class="btn btn-secondary">Reset</a>
        </form>
    </div>
</div>

<!-- Reservations Table -->
<div class="card">
    <div class="card-body">
        <c:choose>
            <c:when test="${not empty reservations}">
                <div class="table-info">
                    Showing <strong>${fn:length(reservations)}</strong> reservation(s)
                </div>
                <div class="table-wrap">
                    <table class="data-table" id="resTable">
                        <thead>
                        <tr>
                            <th>#</th><th>Reservation No.</th><th>Guest</th>
                            <th>Room</th><th>Check-In</th><th>Check-Out</th>
                            <th>Nights</th><th>Status</th><th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="r" items="${reservations}" varStatus="i">
                            <tr class="row-${r.status.toLowerCase()}">
                                <td>${i.count}</td>
                                <td><strong>${r.reservationNumber}</strong></td>
                                <td>
                                    <div class="guest-info">
                                        <strong>${r.guestName}</strong>
                                        <small>${r.contactNumber}</small>
                                    </div>
                                </td>
                                <td>${r.roomNumber} <small>(${r.typeName})</small></td>
                                <td><fmt:formatDate value="${r.checkinDate}" pattern="dd MMM yyyy"/></td>
                                <td><fmt:formatDate value="${r.checkoutDate}" pattern="dd MMM yyyy"/></td>
                                <td>${r.nights}</td>
                                <td><span class="badge badge-${r.status.toLowerCase()}">${r.status}</span></td>
                                <td class="action-btns">
                                    <a href="${pageContext.request.contextPath}/reservations?action=view&number=${r.reservationNumber}"
                                       class="btn btn-sm btn-info" title="View">👁</a>

                                    <c:if test="${r.status eq 'CONFIRMED'}">
                                        <form method="POST" action="${pageContext.request.contextPath}/reservations"
                                              style="display:inline"
                                              onsubmit="return confirm('Check in guest ${r.guestName}?')">
                                            <input type="hidden" name="action" value="checkin">
                                            <input type="hidden" name="number" value="${r.reservationNumber}">
                                            <button type="submit" class="btn btn-sm btn-success" title="Check In">✅</button>
                                        </form>
                                        <form method="POST" action="${pageContext.request.contextPath}/reservations"
                                              style="display:inline"
                                              onsubmit="return confirm('Cancel reservation ${r.reservationNumber}?')">
                                            <input type="hidden" name="action" value="cancel">
                                            <input type="hidden" name="number" value="${r.reservationNumber}">
                                            <button type="submit" class="btn btn-sm btn-danger" title="Cancel">✖</button>
                                        </form>
                                    </c:if>

                                    <c:if test="${r.status eq 'CHECKED_IN'}">
                                        <form method="POST" action="${pageContext.request.contextPath}/reservations"
                                              style="display:inline"
                                              onsubmit="return confirm('Checkout guest ${r.guestName} and generate bill?')">
                                            <input type="hidden" name="action" value="checkout">
                                            <input type="hidden" name="number" value="${r.reservationNumber}">
                                            <button type="submit" class="btn btn-sm btn-warning" title="Checkout">🏁</button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-icon">📋</div>
                    <h3>No reservations found</h3>
                    <p>Try adjusting your search or create a new reservation.</p>
                    <a href="${pageContext.request.contextPath}/reservations?action=search-rooms"
                       class="btn btn-primary">New Reservation</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="footer.jsp"/>
