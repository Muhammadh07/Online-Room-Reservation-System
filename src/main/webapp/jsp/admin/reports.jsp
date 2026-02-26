<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Reports" scope="request"/>
<jsp:include page="../common/header.jsp"/>

<div class="page-header">
    <h1>📈 Reports & Analytics</h1>
    <div class="report-tabs no-print">
        <a href="${pageContext.request.contextPath}/reports?type=summary"  class="btn btn-sm ${empty param.type or param.type eq 'summary' ? 'btn-primary' : 'btn-secondary'}">Summary</a>
        <a href="${pageContext.request.contextPath}/reports?type=revenue"  class="btn btn-sm ${param.type eq 'revenue'  ? 'btn-primary' : 'btn-secondary'}">Revenue</a>
        <a href="${pageContext.request.contextPath}/reports?type=occupancy"class="btn btn-sm ${param.type eq 'occupancy'? 'btn-primary' : 'btn-secondary'}">Occupancy</a>
        <button onclick="window.print()" class="btn btn-sm btn-info no-print">🖨 Print</button>
    </div>
</div>

<!-- Revenue Summary Cards -->
<c:if test="${not empty revenue}">
    <div class="stats-grid">
        <div class="stat-card stat-blue">
            <div class="stat-icon">📋</div>
            <div class="stat-info">
                <h3>${revenue.totalBills}</h3><p>Total Bills</p>
            </div>
        </div>
        <div class="stat-card stat-green">
            <div class="stat-icon">💰</div>
            <div class="stat-info">
                <h3>LKR <fmt:formatNumber value="${revenue.totalRevenue}" pattern="#,##0"/></h3>
                <p>Total Revenue</p>
            </div>
        </div>
        <div class="stat-card stat-orange">
            <div class="stat-icon">✅</div>
            <div class="stat-info">
                <h3>LKR <fmt:formatNumber value="${revenue.paidRevenue}" pattern="#,##0"/></h3>
                <p>Paid Revenue</p>
            </div>
        </div>
        <div class="stat-card stat-red">
            <div class="stat-icon">⏳</div>
            <div class="stat-info">
                <h3>LKR <fmt:formatNumber value="${revenue.pendingRevenue}" pattern="#,##0"/></h3>
                <p>Pending Revenue</p>
            </div>
        </div>
    </div>
</c:if>

<!-- Reservation Status Summary -->
<c:if test="${not empty confirmed or not empty checkedIn or not empty checkedOut or not empty cancelled}">
    <div class="stats-grid">
        <div class="stat-card stat-yellow">
            <div class="stat-icon">🔖</div>
            <div class="stat-info"><h3>${confirmed}</h3><p>Confirmed</p></div>
        </div>
        <div class="stat-card stat-green">
            <div class="stat-icon">🏃</div>
            <div class="stat-info"><h3>${checkedIn}</h3><p>Checked In</p></div>
        </div>
        <div class="stat-card stat-blue">
            <div class="stat-icon">🏁</div>
            <div class="stat-info"><h3>${checkedOut}</h3><p>Checked Out</p></div>
        </div>
        <div class="stat-card stat-red">
            <div class="stat-icon">✖</div>
            <div class="stat-info"><h3>${cancelled}</h3><p>Cancelled</p></div>
        </div>
    </div>
</c:if>

<!-- Recent Reservations Table -->
<c:if test="${not empty recentRes}">
    <div class="card">
        <div class="card-header"><h2>Recent Reservations</h2></div>
        <div class="card-body">
            <div class="table-wrap">
                <table class="data-table">
                    <thead>
                    <tr>
                        <th>Reservation #</th><th>Guest</th><th>Room</th>
                        <th>Check-In</th><th>Check-Out</th><th>Nights</th><th>Status</th>
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
                            <td>${r.nights}</td>
                            <td><span class="badge badge-${r.status.toLowerCase()}">${r.status}</span></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</c:if>

<!-- Bills / Revenue Table -->
<c:if test="${not empty bills}">
    <div class="card">
        <div class="card-header"><h2>Revenue Detail</h2></div>
        <div class="card-body">
            <div class="table-wrap">
                <table class="data-table">
                    <thead>
                    <tr>
                        <th>Bill #</th><th>Reservation</th><th>Guest</th>
                        <th>Room</th><th>Nights</th>
                        <th>Subtotal</th><th>Tax</th><th>Total</th><th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="b" items="${bills}">
                        <tr>
                            <td><strong>${b.billNumber}</strong></td>
                            <td>${b.reservationNumber}</td>
                            <td>${b.guestName}</td>
                            <td>${b.roomNumber}</td>
                            <td>${b.nights}</td>
                            <td>LKR <fmt:formatNumber value="${b.subtotal}"    pattern="#,##0.00"/></td>
                            <td>LKR <fmt:formatNumber value="${b.taxAmount}"   pattern="#,##0.00"/></td>
                            <td><strong>LKR <fmt:formatNumber value="${b.totalAmount}" pattern="#,##0.00"/></strong></td>
                            <td><span class="badge badge-${b.paymentStatus.toLowerCase()}">${b.paymentStatus}</span></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</c:if>

<jsp:include page="../common/footer.jsp"/>
