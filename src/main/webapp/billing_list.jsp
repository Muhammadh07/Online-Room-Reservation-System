<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Billing - Ocean View Resort</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">
    <div class="page-title">Billing</div>

    <div class="card">
        <div style="display:flex;gap:10px;align-items:center;flex-wrap:wrap">
            <span style="font-weight:600;color:#555">Filter:</span>
            <a href="${pageContext.request.contextPath}/billing"
               class="btn ${empty filter ? 'btn-primary' : 'btn-secondary'} btn-sm">All</a>
            <a href="${pageContext.request.contextPath}/billing?filter=unpaid"
               class="btn ${'unpaid' == filter ? 'btn-primary' : 'btn-secondary'} btn-sm">Unpaid</a>
            <a href="${pageContext.request.contextPath}/billing?filter=paid"
               class="btn ${'paid' == filter ? 'btn-primary' : 'btn-secondary'} btn-sm">Paid</a>
        </div>
    </div>

    <div class="card">
        <c:choose>
            <c:when test="${empty bills}">
                <p style="color:#666;text-align:center;padding:20px">No bill found.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <tr>
                        <th>Bill #</th><th>Reservation No</th><th>Guest</th><th>Total</th><th>Balance Due</th><th>Generated</th><th>Status</th><th>Action</th>
                    </tr>
                    <c:forEach var="b" items="${bills}">
                        <tr>
                            <td>${b.billId}</td>
                            <td>${b.reservationNo}</td>
                            <td>${b.guestName}</td>
                            <td>$<fmt:formatNumber value="${b.totalAmount}" pattern="0.00"/></td>
                            <td style="color:${b.balanceDue > 0 ? '#e74c3c' : '#27ae60'}">
                                $<fmt:formatNumber value="${b.balanceDue}" pattern="0.00"/>
                            </td>
                            <td>${b.generatedAt}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${b.balanceDue <= 0}">
                                        <span class="badge badge-checked_out">PAID</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-confirmed">UNPAID</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/billing?action=view&id=${b.billId}" class="btn btn-sm btn-primary">Invoice</a>
                                <c:if test="${b.balanceDue > 0}">
                                    <a href="${pageContext.request.contextPath}/payment?action=record&billId=${b.billId}" class="btn btn-sm btn-success">Pay</a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
                <p style="margin-top:10px;color:#666;font-size:0.9rem">Total: ${bills.size()} bill(s)</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
