<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Reports - Ocean View Resort</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .report-tabs { display:flex; gap:8px; margin-bottom:20px; flex-wrap:wrap; }
        .report-tab { padding:9px 18px; background:#fff; border:2px solid #1a3a5c; color:#1a3a5c; border-radius:5px; text-decoration:none; font-weight:500; }
        .report-tab.active, .report-tab:hover { background:#1a3a5c; color:#fff; }
        .total-row td { font-weight:bold; background:#f7fafc; }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">
    <div class="page-title">Reports</div>

    <div class="report-tabs">
        <a href="${pageContext.request.contextPath}/reports" class="report-tab ${empty reportType ? 'active' : ''}">📋 Menu</a>
        <a href="${pageContext.request.contextPath}/reports?type=occupancy" class="report-tab ${reportType=='occupancy' ? 'active' : ''}">🏨 Occupancy</a>
        <a href="${pageContext.request.contextPath}/reports?type=revenue" class="report-tab ${reportType=='revenue' ? 'active' : ''}">💰 Revenue</a>
        <a href="${pageContext.request.contextPath}/reports?type=payment" class="report-tab ${reportType=='payment' ? 'active' : ''}">💳 Payment Methods</a>
        <a href="${pageContext.request.contextPath}/reports?type=checkins" class="report-tab ${reportType=='checkins' ? 'active' : ''}">📅 Today's Check-Ins</a>
    </div>

    <!-- Menu -->
    <c:if test="${empty reportType}">
        <div style="display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:16px">
            <a href="${pageContext.request.contextPath}/reports?type=occupancy" style="text-decoration:none">
                <div class="card" style="text-align:center;cursor:pointer;transition:box-shadow 0.2s" onmouseover="this.style.boxShadow='0 4px 16px rgba(0,0,0,0.15)'" onmouseout="this.style.boxShadow=''">
                    <div style="font-size:2.5rem">🏨</div>
                    <div style="font-weight:600;color:#1a3a5c;margin-top:8px">Occupancy Report</div>
                    <div style="font-size:0.85rem;color:#666;margin-top:4px">Reservations by date range</div>
                </div>
            </a>
            <a href="${pageContext.request.contextPath}/reports?type=revenue" style="text-decoration:none">
                <div class="card" style="text-align:center;cursor:pointer">
                    <div style="font-size:2.5rem">💰</div>
                    <div style="font-weight:600;color:#1a3a5c;margin-top:8px">Revenue Report</div>
                    <div style="font-size:0.85rem;color:#666;margin-top:4px">Payments collected by date range</div>
                </div>
            </a>
            <a href="${pageContext.request.contextPath}/reports?type=payment" style="text-decoration:none">
                <div class="card" style="text-align:center;cursor:pointer">
                    <div style="font-size:2.5rem">💳</div>
                    <div style="font-weight:600;color:#1a3a5c;margin-top:8px">Payment Methods</div>
                    <div style="font-size:0.85rem;color:#666;margin-top:4px">Summary by payment method</div>
                </div>
            </a>
            <a href="${pageContext.request.contextPath}/reports?type=checkins" style="text-decoration:none">
                <div class="card" style="text-align:center;cursor:pointer">
                    <div style="font-size:2.5rem">📅</div>
                    <div style="font-weight:600;color:#1a3a5c;margin-top:8px">Check-In Report</div>
                    <div style="font-size:0.85rem;color:#666;margin-top:4px">Arrivals by date</div>
                </div>
            </a>
        </div>
    </c:if>

    <!-- Occupancy Report -->
    <c:if test="${reportType == 'occupancy'}">
        <div class="card">
            <form method="get" action="${pageContext.request.contextPath}/reports" style="display:flex;gap:12px;align-items:flex-end">
                <input type="hidden" name="type" value="occupancy">
                <div class="form-group" style="margin:0">
                    <label>From</label>
                    <input type="date" name="from" value="${from}">
                </div>
                <div class="form-group" style="margin:0">
                    <label>To</label>
                    <input type="date" name="to" value="${to}">
                </div>
                <button type="submit" class="btn btn-primary">Generate</button>
            </form>
        </div>
        <div class="card">
            <div class="card-title">Occupancy Report: ${from} to ${to}</div>
            <table>
                <tr><th>Res. No</th><th>Guest</th><th>Room</th><th>Type</th><th>Check-In</th><th>Check-Out</th><th>Nights</th><th>Status</th></tr>
                <c:forEach var="r" items="${reservations}">
                    <tr>
                        <td>${r.reservationNo}</td>
                        <td>${r.guestName}</td>
                        <td>${r.roomNumber}</td>
                        <td>${r.roomTypeName}</td>
                        <td>${r.checkIn}</td>
                        <td>${r.checkOut}</td>
                        <td>${r.nights}</td>
                        <td><span class="badge badge-${r.status.toLowerCase()}">${r.status}</span></td>
                    </tr>
                </c:forEach>
            </table>
            <p style="margin-top:10px;color:#666">Total: ${reservations.size()} reservation(s)</p>
        </div>
    </c:if>

    <!-- Revenue Report -->
    <c:if test="${reportType == 'revenue'}">
        <div class="card">
            <form method="get" action="${pageContext.request.contextPath}/reports" style="display:flex;gap:12px;align-items:flex-end">
                <input type="hidden" name="type" value="revenue">
                <div class="form-group" style="margin:0">
                    <label>From</label>
                    <input type="date" name="from" value="${from}">
                </div>
                <div class="form-group" style="margin:0">
                    <label>To</label>
                    <input type="date" name="to" value="${to}">
                </div>
                <button type="submit" class="btn btn-primary">Generate</button>
            </form>
        </div>
        <div class="card">
            <div class="card-title">Revenue Report: ${from} to ${to}</div>
            <div style="font-size:1.4rem;font-weight:bold;color:#27ae60;margin-bottom:16px">
                Total Revenue: $<fmt:formatNumber value="${totalRevenue}" pattern="0.00"/>
            </div>
            <table>
                <tr><th>Date</th><th>Method</th><th>Amount</th><th>Reference</th><th>Recorded By</th></tr>
                <c:forEach var="p" items="${payments}">
                    <tr>
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

    <!-- Payment Methods Report -->
    <c:if test="${reportType == 'payment'}">
        <div class="card">
            <div class="card-title">Payment Method Summary</div>
            <table>
                <tr><th>Payment Method</th><th>No. of Transactions</th><th>Total Amount</th></tr>
                <c:set var="grandTotal" value="0"/>
                <c:forEach var="row" items="${paymentSummary}">
                    <c:set var="grandTotal" value="${grandTotal + row.total}"/>
                    <tr>
                        <td>${row.method}</td>
                        <td>${row.count}</td>
                        <td>$<fmt:formatNumber value="${row.total}" pattern="0.00"/></td>
                    </tr>
                </c:forEach>
                <tr class="total-row">
                    <td>TOTAL</td><td>-</td>
                    <td>$<fmt:formatNumber value="${grandTotal}" pattern="0.00"/></td>
                </tr>
            </table>
        </div>
    </c:if>

    <!-- Check-Ins Report -->
    <c:if test="${reportType == 'checkins'}">
        <div class="card">
            <form method="get" action="${pageContext.request.contextPath}/reports" style="display:flex;gap:12px;align-items:flex-end">
                <input type="hidden" name="type" value="checkins">
                <div class="form-group" style="margin:0">
                    <label>Date</label>
                    <input type="date" name="date" value="${date}">
                </div>
                <button type="submit" class="btn btn-primary">Generate</button>
            </form>
        </div>
        <div class="card">
            <div class="card-title">Check-Ins for: ${date}</div>
            <table>
                <tr><th>Res. No</th><th>Guest</th><th>Phone</th><th>Room</th><th>Type</th><th>Check-Out</th><th>Nights</th><th>Status</th></tr>
                <c:forEach var="r" items="${reservations}">
                    <tr>
                        <td>${r.reservationNo}</td>
                        <td>${r.guestName}</td>
                        <td>${r.guestPhone}</td>
                        <td>${r.roomNumber}</td>
                        <td>${r.roomTypeName}</td>
                        <td>${r.checkOut}</td>
                        <td>${r.nights}</td>
                        <td><span class="badge badge-${r.status.toLowerCase()}">${r.status}</span></td>
                    </tr>
                </c:forEach>
            </table>
            <p style="margin-top:10px;color:#666">Total arrivals: ${reservations.size()}</p>
        </div>
    </c:if>

</div>
</body>
</html>
