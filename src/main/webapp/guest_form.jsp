<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>${empty guest ? 'New Guest' : 'Edit Guest'} - Ocean View Resort</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">
    <div class="page-title">${empty guest ? 'New Guest' : 'Edit Guest'}</div>

    <c:if test="${not empty errors}">
        <div class="alert alert-error">
            <ul style="margin:0;padding-left:20px">
                <c:forEach var="err" items="${errors}"><li>${err}</li></c:forEach>
            </ul>
        </div>
    </c:if>

    <div class="card">
        <form method="post" action="${pageContext.request.contextPath}/guests">
            <input type="hidden" name="action" value="${empty guest ? 'create' : 'update'}">
            <c:if test="${not empty guest}">
                <input type="hidden" name="guestId" value="${guest.guestId}">
            </c:if>

            <div class="form-row">
                <div class="form-group">
                    <label>Full Name <span style="color:#e74c3c">*</span></label>
                    <input type="text" name="fullName" value="${guest.fullName}" placeholder="Enter full name" required>
                </div>
                <div class="form-group">
                    <label>Phone <span style="color:#e74c3c">*</span></label>
                    <input type="text" name="phone" value="${guest.phone}" placeholder="e.g. +94 77 123 4567">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" value="${guest.email}" placeholder="guest@example.com">
                </div>
                <div class="form-group">
                    <label>NIC / Passport No.</label>
                    <input type="text" name="nicPassport" value="${guest.nicPassport}" placeholder="National ID or Passport">
                </div>
            </div>

            <div class="form-group">
                <label>Address</label>
                <textarea name="address" rows="3" placeholder="Street, City, Country">${guest.address}</textarea>
            </div>

            <div style="margin-top:16px;display:flex;gap:10px">
                <button type="submit" class="btn btn-success">${empty guest ? 'Create Guest' : 'Save Changes'}</button>
                <a href="${pageContext.request.contextPath}/guests" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>
