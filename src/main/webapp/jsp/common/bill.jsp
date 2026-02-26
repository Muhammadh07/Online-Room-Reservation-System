<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Bill" scope="request"/>
<jsp:include page="header.jsp"/>

<div class="page-header no-print">
    <h1>💰 Bill & Payment</h1>
    <div>
        <button onclick="window.print()" class="btn btn-secondary no-print">🖨 Print Bill</button>
        <a href="${pageContext.request.contextPath}/reservations" class="btn btn-info no-print">← Reservations</a>
    </div>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-danger no-print"><span>⚠</span> ${error}</div>
</c:if>
<c:if test="${not empty success}">
    <div class="alert alert-success no-print"><span>✓</span> ${success}</div>
</c:if>

<c:if test="${not empty bill}">
    <div class="bill-container">
        <!-- Bill Header -->
        <div class="bill-header">
            <div class="bill-hotel-info">
                <h1>🏨 Grand Hotel</h1>
                <p>123 Hotel Road, Colombo 03, Sri Lanka</p>
                <p>Tel: +94 11 234 5678 | Email: info@grandhotel.lk</p>
            </div>
            <div class="bill-meta">
                <h2>INVOICE</h2>
                <table class="bill-meta-table">
                    <tr><td>Bill No:</td><td><strong>${bill.billNumber}</strong></td></tr>
                    <tr><td>Reservation:</td><td>${bill.reservationNumber}</td></tr>
                    <tr><td>Date:</td>
                        <td><fmt:formatDate value="${bill.generatedAt}" pattern="dd MMM yyyy HH:mm"/></td></tr>
                    <tr><td>Status:</td>
                        <td><span class="badge badge-${bill.paymentStatus.toLowerCase()}">${bill.paymentStatus}</span></td></tr>
                </table>
            </div>
        </div>

        <!-- Guest Info -->
        <div class="bill-guest">
            <div>
                <h3>Bill To:</h3>
                <strong>${bill.guestName}</strong>
            </div>
            <div>
                <h3>Room:</h3>
                <strong>Room ${bill.roomNumber} — ${bill.typeName}</strong>
            </div>
        </div>

        <!-- Bill Details -->
        <table class="bill-table">
            <thead>
            <tr>
                <th>Description</th><th>Nights</th><th>Rate/Night</th><th>Amount</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>Room ${bill.roomNumber} — ${bill.typeName} accommodation</td>
                <td class="text-center">${bill.nights}</td>
                <td class="text-right">LKR <fmt:formatNumber value="${bill.ratePerNight}" pattern="#,##0.00"/></td>
                <td class="text-right">LKR <fmt:formatNumber value="${bill.subtotal}" pattern="#,##0.00"/></td>
            </tr>
            </tbody>
            <tfoot>
            <tr class="bill-subtotal">
                <td colspan="3" class="text-right">Subtotal</td>
                <td class="text-right">LKR <fmt:formatNumber value="${bill.subtotal}" pattern="#,##0.00"/></td>
            </tr>
            <c:if test="${bill.discountAmount > 0}">
                <tr>
                    <td colspan="3" class="text-right">Discount</td>
                    <td class="text-right">- LKR <fmt:formatNumber value="${bill.discountAmount}" pattern="#,##0.00"/></td>
                </tr>
            </c:if>
            <tr>
                <td colspan="3" class="text-right">Tax (${bill.taxRate}%)</td>
                <td class="text-right">LKR <fmt:formatNumber value="${bill.taxAmount}" pattern="#,##0.00"/></td>
            </tr>
            <tr class="bill-total">
                <td colspan="3" class="text-right"><strong>TOTAL</strong></td>
                <td class="text-right">
                    <strong>LKR <fmt:formatNumber value="${bill.totalAmount}" pattern="#,##0.00"/></strong>
                </td>
            </tr>
            </tfoot>
        </table>

        <!-- Payment Section -->
        <c:if test="${bill.paymentStatus ne 'PAID'}">
            <div class="payment-section no-print">
                <h3>Record Payment</h3>
                <form method="POST" action="${pageContext.request.contextPath}/reservations" id="payForm" novalidate>
                    <input type="hidden" name="action"      value="pay">
                    <input type="hidden" name="billId"      value="${bill.billId}">
                    <input type="hidden" name="billNumber"  value="${bill.billNumber}">
                    <div class="form-row">
                        <div class="form-group">
                            <label>Amount (LKR) <span class="required">*</span></label>
                            <input type="number" name="amount" class="form-control" required
                                   min="1" max="${bill.totalAmount}" step="0.01"
                                   value="${bill.totalAmount}" id="payAmount">
                            <span class="field-error" id="amountErr"></span>
                        </div>
                        <div class="form-group">
                            <label>Payment Method <span class="required">*</span></label>
                            <select name="paymentMethod" class="form-control" required>
                                <option value="">-- Select --</option>
                                <option value="CASH">💵 Cash</option>
                                <option value="CARD">💳 Card</option>
                                <option value="BANK_TRANSFER">🏦 Bank Transfer</option>
                            </select>
                            <span class="field-error" id="methodErr"></span>
                        </div>
                        <div class="form-group form-group-btn">
                            <label>&nbsp;</label>
                            <button type="submit" class="btn btn-success">✅ Record Payment</button>
                        </div>
                    </div>
                </form>
            </div>
        </c:if>

        <div class="bill-footer">
            <p>Thank you for staying at Grand Hotel. We hope to see you again!</p>
            <p><small>This is a computer-generated invoice.</small></p>
        </div>
    </div>
</c:if>

<script>
    document.getElementById('payForm')?.addEventListener('submit', function(e) {
        let ok = true;
        const amt = parseFloat(document.getElementById('payAmount').value);
        const method = document.querySelector('[name=paymentMethod]').value;
        if (!amt || amt <= 0) { document.getElementById('amountErr').textContent = 'Valid amount required'; ok=false; }
        else document.getElementById('amountErr').textContent = '';
        if (!method) { document.getElementById('methodErr').textContent = 'Select payment method'; ok=false; }
        else document.getElementById('methodErr').textContent = '';
        if (!ok) e.preventDefault();
    });
</script>

<jsp:include page="footer.jsp"/>
