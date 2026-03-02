<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard - Ocean View Resort</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        /* ── Welcome Banner ──────────────────────────────── */
        .welcome-banner {
            background: linear-gradient(135deg, #1a3a5c 0%, #2471a3 100%);
            color: #fff; border-radius: 12px; padding: 28px 32px;
            margin-bottom: 24px; display: flex; align-items: center;
            justify-content: space-between; flex-wrap: wrap; gap: 16px;
        }
        .welcome-banner .wb-left h2 { font-size: 1.6rem; font-weight: 700; margin-bottom: 4px; }
        .welcome-banner .wb-left p  { font-size: 0.95rem; opacity: 0.85; }
        .welcome-banner .wb-right   { text-align: right; }
        .welcome-banner .wb-date    { font-size: 1.05rem; font-weight: 600; opacity: 0.9; }
        .welcome-banner .wb-time    { font-size: 0.85rem; opacity: 0.7; }

        /* ── Metric cards ────────────────────────────────── */
        .metrics-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(155px, 1fr));
            gap: 16px; margin-bottom: 20px;
        }
        .metric-card {
            background: #fff; border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            padding: 20px 18px; display: flex; align-items: center; gap: 14px;
            border-left: 5px solid #1a3a5c;
        }
        .metric-card.blue   { border-left-color: #2471a3; }
        .metric-card.green  { border-left-color: #27ae60; }
        .metric-card.orange { border-left-color: #e67e22; }
        .metric-card.red    { border-left-color: #e74c3c; }
        .metric-card.teal   { border-left-color: #16a085; }
        .metric-card.purple { border-left-color: #8e44ad; }
        .metric-card.navy   { border-left-color: #1a3a5c; }
        .metric-card .mc-icon { font-size: 2rem; flex-shrink: 0; }
        .metric-card .mc-body .mc-num   { font-size: 1.75rem; font-weight: 700; color: #1a3a5c; line-height:1; }
        .metric-card .mc-body .mc-label { font-size: 0.8rem; color: #777; margin-top: 4px; }
        .metric-card.green  .mc-num { color: #27ae60; }
        .metric-card.orange .mc-num { color: #e67e22; }
        .metric-card.red    .mc-num { color: #e74c3c; }
        .metric-card.teal   .mc-num { color: #16a085; }
        .metric-card.purple .mc-num { color: #8e44ad; }

        /* ── Revenue + Occupancy row ─────────────────────── */
        .revenue-row { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 20px; margin-bottom: 20px; }
        @media(max-width:768px){ .revenue-row { grid-template-columns:1fr; } }
        .rev-card {
            background: #fff; border-radius: 10px; padding: 22px 24px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }
        .rev-card .rc-label { font-size: 0.82rem; color: #888; text-transform: uppercase;
                              letter-spacing: 0.5px; margin-bottom: 6px; }
        .rev-card .rc-value { font-size: 1.9rem; font-weight: 700; color: #1a3a5c; }
        .rev-card .rc-value.green  { color: #27ae60; }
        .rev-card .rc-value.orange { color: #e67e22; }
        .rev-card .rc-sub   { font-size: 0.82rem; color: #999; margin-top: 4px; }

        /* occupancy bar */
        .occ-bar-wrap { margin-top: 10px; background: #e8eef4; border-radius: 6px; height: 10px; overflow: hidden; }
        .occ-bar-fill { height: 100%; border-radius: 6px; background: linear-gradient(90deg,#27ae60,#1abc9c);
                        transition: width 0.6s ease; }

        /* ── Today Activity ──────────────────────────────── */
        .today-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
        @media(max-width:700px){ .today-grid { grid-template-columns: 1fr; } }
        .today-section-title {
            font-weight: 700; font-size: 0.95rem; color: #1a3a5c;
            margin-bottom: 12px; padding-bottom: 6px; border-bottom: 2px solid #e8eef4;
            display: flex; align-items: center; gap: 8px;
        }
        .today-badge {
            background: #1a3a5c; color: #fff; border-radius: 10px;
            padding: 1px 8px; font-size: 0.75rem; font-weight: 600;
        }
        .today-badge.green  { background: #27ae60; }
        .today-badge.orange { background: #e67e22; }

        /* ── Quick Actions ───────────────────────────────── */
        .action-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px,1fr)); gap: 14px; }
        .action-card {
            background: #f7fafd; border: 1.5px solid #d6e4f0; border-radius: 10px;
            padding: 18px 14px; text-align: center; transition: all 0.2s; cursor: pointer;
        }
        .action-card:hover { background: #1a3a5c; border-color: #1a3a5c; box-shadow: 0 4px 16px rgba(26,58,92,0.18); }
        .action-card:hover .ac-icon, .action-card:hover .ac-title { color: #fff; }
        .action-card:hover .ac-btn { background: rgba(255,255,255,0.2); color: #fff; border-color: rgba(255,255,255,0.4); }
        .ac-icon  { font-size: 2rem; margin-bottom: 8px; }
        .ac-title { font-weight: 700; font-size: 0.9rem; color: #1a3a5c; margin-bottom: 10px; }
        .ac-btn   { display:inline-block; padding: 5px 16px; border-radius: 5px; font-size: 0.82rem;
                    font-weight:600; text-decoration:none; border: 1.5px solid #1a3a5c; color: #1a3a5c; }

        /* ── Upcoming / Recent tables ────────────────────── */
        .section-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:14px; }
        .section-header .sh-title { font-size:1rem; font-weight:700; color:#1a3a5c; }
        .section-header a { font-size:0.85rem; color:#2471a3; text-decoration:none; }
        .section-header a:hover { text-decoration:underline; }

        /* ── Payment method pills ────────────────────────── */
        .pm-row { display:flex; gap:12px; flex-wrap:wrap; margin-top:4px; }
        .pm-pill { background:#f0f4f8; border-radius:20px; padding:6px 14px; font-size:0.82rem; color:#444; }
        .pm-pill strong { color:#1a3a5c; }

        /* ── Empty state ─────────────────────────────────── */
        .empty-state { text-align:center; padding:20px; color:#aaa; font-size:0.9rem; }

        @media(max-width:600px){
            .metrics-grid { grid-template-columns: 1fr 1fr; }
            .welcome-banner .wb-right { display:none; }
        }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">

    <%-- ── Welcome Banner ──────────────────────────────────────────── --%>
    <div class="welcome-banner">
        <div class="wb-left">
            <h2>&#127754; Welcome back, <c:out value="${sessionScope.user.fullName != null && !empty sessionScope.user.fullName ? sessionScope.user.fullName : sessionScope.user.username}"/>!</h2>
            <p>Here's what's happening at Ocean View Resort today.</p>
        </div>
        <div class="wb-right">
            <div class="wb-date" id="dashDate">Loading...</div>
            <div class="wb-time" id="dashTime"></div>
        </div>
    </div>

    <%-- ── Metrics Row ──────────────────────────────────────────────── --%>
    <div class="metrics-grid">
        <div class="metric-card navy">
            <div class="mc-icon">&#128203;</div>
            <div class="mc-body">
                <div class="mc-num">${totalReservations}</div>
                <div class="mc-label">Total Reservations</div>
            </div>
        </div>
        <div class="metric-card green">
            <div class="mc-icon">&#9989;</div>
            <div class="mc-body">
                <div class="mc-num">${confirmedCount}</div>
                <div class="mc-label">Confirmed</div>
            </div>
        </div>
        <div class="metric-card blue">
            <div class="mc-icon">&#128717;</div>
            <div class="mc-body">
                <div class="mc-num">${checkedInCount}</div>
                <div class="mc-label">Checked In</div>
            </div>
        </div>
        <div class="metric-card teal">
            <div class="mc-icon">&#128316;</div>
            <div class="mc-body">
                <div class="mc-num">${checkedOutCount}</div>
                <div class="mc-label">Checked Out</div>
            </div>
        </div>
        <div class="metric-card red">
            <div class="mc-icon">&#10060;</div>
            <div class="mc-body">
                <div class="mc-num">${cancelledCount}</div>
                <div class="mc-label">Cancelled</div>
            </div>
        </div>
        <div class="metric-card orange">
            <div class="mc-icon">&#127968;</div>
            <div class="mc-body">
                <div class="mc-num">${availableRooms}/${totalRooms}</div>
                <div class="mc-label">Rooms Available</div>
            </div>
        </div>
        <div class="metric-card purple">
            <div class="mc-icon">&#128101;</div>
            <div class="mc-body">
                <div class="mc-num">${totalGuests}</div>
                <div class="mc-label">Total Guests</div>
            </div>
        </div>
    </div>

    <%-- ── Revenue / Occupancy Row ──────────────────────────────────── --%>
    <div class="revenue-row">
        <div class="rev-card">
            <div class="rc-label">&#128176; Total Revenue</div>
            <div class="rc-value green">$<fmt:formatNumber value="${totalRevenue}" pattern="#,##0.00"/></div>
            <div class="rc-sub">All recorded payments</div>
        </div>
        <div class="rev-card">
            <div class="rc-label">&#9200; Pending Payments</div>
            <div class="rc-value orange">${pendingBillsCount} bill(s)</div>
            <div class="rc-sub">Outstanding: $<fmt:formatNumber value="${pendingAmount}" pattern="#,##0.00"/></div>
        </div>
        <div class="rev-card">
            <div class="rc-label">&#127970; Room Occupancy</div>
            <div class="rc-value">${occupancyPct}%</div>
            <div class="rc-sub">${occupiedRooms} of ${totalRooms} rooms occupied</div>
            <div class="occ-bar-wrap" style="margin-top:10px">
                <div class="occ-bar-fill" style="width:${occupancyPct}%"></div>
            </div>
        </div>
    </div>

    <%-- ── Today's Activity ─────────────────────────────────────────── --%>
    <div class="card">
        <div class="card-title">&#128197; Today's Activity</div>
        <div class="today-grid">
            <%-- Check-Ins --%>
            <div>
                <div class="today-section-title">
                    &#128710; Check-Ins Today
                    <span class="today-badge green">${todayCheckIns.size()}</span>
                </div>
                <c:choose>
                    <c:when test="${empty todayCheckIns}">
                        <div class="empty-state">No check-ins scheduled today.</div>
                    </c:when>
                    <c:otherwise>
                        <table>
                            <tr><th>Res #</th><th>Guest</th><th>Room</th><th>Status</th><th></th></tr>
                            <c:forEach var="r" items="${todayCheckIns}">
                                <tr>
                                    <td><strong>${r.reservationNo}</strong></td>
                                    <td>${r.guestName}</td>
                                    <td>${r.roomNumber}</td>
                                    <td><span class="badge badge-${r.status.toLowerCase()}">${r.status}</span></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${r.status == 'CONFIRMED'}">
                                                <a href="${pageContext.request.contextPath}/reservations?action=checkin&id=${r.reservationId}" class="btn btn-sm btn-success">Check In</a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/reservations?action=view&id=${r.reservationId}" class="btn btn-sm btn-primary">View</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
            <%-- Check-Outs --%>
            <div>
                <div class="today-section-title">
                    &#128711; Check-Outs Today
                    <span class="today-badge orange">${todayCheckOuts.size()}</span>
                </div>
                <c:choose>
                    <c:when test="${empty todayCheckOuts}">
                        <div class="empty-state">No check-outs scheduled today.</div>
                    </c:when>
                    <c:otherwise>
                        <table>
                            <tr><th>Res #</th><th>Guest</th><th>Room</th><th>Status</th><th></th></tr>
                            <c:forEach var="r" items="${todayCheckOuts}">
                                <tr>
                                    <td><strong>${r.reservationNo}</strong></td>
                                    <td>${r.guestName}</td>
                                    <td>${r.roomNumber}</td>
                                    <td><span class="badge badge-${r.status.toLowerCase()}">${r.status}</span></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${r.status == 'CHECKED_IN'}">
                                                <a href="${pageContext.request.contextPath}/reservations?action=checkout&id=${r.reservationId}" class="btn btn-sm btn-warning">Check Out</a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/reservations?action=view&id=${r.reservationId}" class="btn btn-sm btn-primary">View</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <%-- ── Quick Actions ────────────────────────────────────────────── --%>
    <div class="card">
        <div class="card-title">&#9889; Quick Actions</div>
        <div class="action-grid">
            <div class="action-card">
                <div class="ac-icon">&#128203;</div>
                <div class="ac-title">New Reservation</div>
                <a href="${pageContext.request.contextPath}/reservations?action=new" class="ac-btn">Create</a>
            </div>
            <div class="action-card">
                <div class="ac-icon">&#128176;</div>
                <div class="ac-title">Record Payment</div>
                <a href="${pageContext.request.contextPath}/payment" class="ac-btn">Pay</a>
            </div>
            <div class="action-card">
                <div class="ac-icon">&#128101;</div>
                <div class="ac-title">Manage Guests</div>
                <a href="${pageContext.request.contextPath}/guests" class="ac-btn">Guests</a>
            </div>
            <div class="action-card">
                <div class="ac-icon">&#128196;</div>
                <div class="ac-title">Billing &amp; Bills</div>
                <a href="${pageContext.request.contextPath}/billing" class="ac-btn">Billing</a>
            </div>
            <div class="action-card">
                <div class="ac-icon">&#128202;</div>
                <div class="ac-title">View Reports</div>
                <a href="${pageContext.request.contextPath}/reports" class="ac-btn">Reports</a>
            </div>
            <c:if test="${sessionScope.user.role == 'ADMIN'}">
            <div class="action-card">
                <div class="ac-icon">&#9881;</div>
                <div class="ac-title">Admin Panel</div>
                <a href="${pageContext.request.contextPath}/admin" class="ac-btn">Admin</a>
            </div>
            </c:if>
        </div>
    </div>

    <%-- ── Two-column: Upcoming + Payment Methods ───────────────────── --%>
    <div class="today-grid">
        <%-- Upcoming Reservations (next 7 days) --%>
        <div class="card" style="margin-bottom:0">
            <div class="section-header">
                <span class="sh-title">&#128337; Upcoming (Next 7 Days) — ${upcomingReservations.size()}</span>
                <a href="${pageContext.request.contextPath}/reservations">View all</a>
            </div>
            <c:choose>
                <c:when test="${empty upcomingReservations}">
                    <div class="empty-state">No upcoming arrivals in the next 7 days.</div>
                </c:when>
                <c:otherwise>
                    <table>
                        <tr><th>Res #</th><th>Guest</th><th>Room</th><th>Check-In</th></tr>
                        <c:forEach var="r" items="${upcomingReservations}">
                            <tr>
                                <td><a href="${pageContext.request.contextPath}/reservations?action=view&id=${r.reservationId}" style="color:#2471a3;text-decoration:none">${r.reservationNo}</a></td>
                                <td>${r.guestName}</td>
                                <td>${r.roomNumber}</td>
                                <td>${r.checkIn}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>

        <%-- Payment Method Summary --%>
        <div class="card" style="margin-bottom:0">
            <div class="section-header">
                <span class="sh-title">&#128179; Payment Methods</span>
                <a href="${pageContext.request.contextPath}/reports">Full report</a>
            </div>
            <c:choose>
                <c:when test="${empty paymentMethodSummary}">
                    <div class="empty-state">No payment records yet.</div>
                </c:when>
                <c:otherwise>
                    <table>
                        <tr><th>Method</th><th>Transactions</th><th>Total</th></tr>
                        <c:forEach var="pm" items="${paymentMethodSummary}">
                            <tr>
                                <td><strong>${pm.method}</strong></td>
                                <td style="text-align:center">${pm.count}</td>
                                <td style="color:#27ae60;font-weight:600">$<fmt:formatNumber value="${pm.total}" pattern="#,##0.00"/></td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <%-- ── Recent Reservations ──────────────────────────────────────── --%>
    <div class="card" style="margin-top:20px">
        <div class="section-header">
            <span class="sh-title">&#128338; Recent Reservations</span>
            <a href="${pageContext.request.contextPath}/reservations">View all →</a>
        </div>
        <c:choose>
            <c:when test="${empty recentReservations}">
                <div class="empty-state">No reservations found.</div>
            </c:when>
            <c:otherwise>
                <table>
                    <tr>
                        <th>Res #</th><th>Guest</th><th>Room</th>
                        <th>Check-In</th><th>Check-Out</th><th>Nights</th><th>Status</th><th>Action</th>
                    </tr>
                    <c:forEach var="r" items="${recentReservations}">
                        <tr>
                            <td><strong>${r.reservationNo}</strong></td>
                            <td>${r.guestName}</td>
                            <td>${r.roomNumber}<br><small style="color:#999">${r.roomTypeName}</small></td>
                            <td>${r.checkIn}</td>
                            <td>${r.checkOut}</td>
                            <td style="text-align:center">${r.nights}</td>
                            <td><span class="badge badge-${r.status.toLowerCase()}">${r.status}</span></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/reservations?action=view&id=${r.reservationId}" class="btn btn-sm btn-primary">View</a>
                                <c:if test="${r.status == 'CONFIRMED'}">
                                    <a href="${pageContext.request.contextPath}/reservations?action=checkin&id=${r.reservationId}" class="btn btn-sm btn-success">Check In</a>
                                </c:if>
                                <c:if test="${r.status == 'CHECKED_IN'}">
                                    <a href="${pageContext.request.contextPath}/reservations?action=checkout&id=${r.reservationId}" class="btn btn-sm btn-warning">Check Out</a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

</div><%-- /container --%>

<script>
    // Live clock
    function updateClock() {
        var now  = new Date();
        var days = ['Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'];
        var months = ['January','February','March','April','May','June',
                      'July','August','September','October','November','December'];
        document.getElementById('dashDate').textContent =
            days[now.getDay()] + ', ' + months[now.getMonth()] + ' ' +
            now.getDate() + ', ' + now.getFullYear();
        var h = now.getHours(), m = now.getMinutes(), s = now.getSeconds();
        var ampm = h >= 12 ? 'PM' : 'AM';
        h = h % 12 || 12;
        document.getElementById('dashTime').textContent =
            (h<10?'0':'')+h + ':' + (m<10?'0':'')+m + ':' + (s<10?'0':'')+s + ' ' + ampm;
    }
    updateClock();
    setInterval(updateClock, 1000);
</script>
</body>
</html>
