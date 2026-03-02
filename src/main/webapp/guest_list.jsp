<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Guests - Ocean View Resort</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">
    <div class="page-title">Guests</div>

    <c:if test="${param.success == 'created'}">
        <div class="alert alert-success">&#10003; Guest created successfully.</div>
    </c:if>
    <c:if test="${param.success == 'updated'}">
        <div class="alert alert-success">&#10003; Guest updated successfully.</div>
    </c:if>
    <c:if test="${param.success == 'deleted'}">
        <div class="alert alert-success">&#10003; Guest deleted successfully.</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-error">&#9888; ${error}</div>
    </c:if>

    <div class="card">
        <div style="display:flex;gap:12px;align-items:center;flex-wrap:wrap">
            <form method="get" action="${pageContext.request.contextPath}/guests" style="display:flex;gap:8px;flex:1">
                <input type="text" name="search" value="${search}" placeholder="Search by name, phone or NIC/Passport..." style="flex:1">
                <button type="submit" class="btn btn-primary">Search</button>
                <a href="${pageContext.request.contextPath}/guests" class="btn btn-secondary">Clear</a>
            </form>
            <a href="${pageContext.request.contextPath}/guests?action=new" class="btn btn-success">+ New Guest</a>
        </div>
    </div>

    <div class="card">
        <c:choose>
            <c:when test="${empty guests}">
                <p style="color:#666;text-align:center;padding:20px">No guest found.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <tr>
                        <th>#</th><th>Full Name</th><th>Phone</th><th>Email</th><th>NIC / Passport</th><th>Actions</th>
                    </tr>
                    <c:forEach var="g" items="${guests}" varStatus="s">
                        <tr>
                            <td>${s.count}</td>
                            <td><strong>${g.fullName}</strong></td>
                            <td>${g.phone}</td>
                            <td>${g.email}</td>
                            <td>${g.nicPassport}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/guests?action=view&id=${g.guestId}" class="btn btn-sm btn-primary">View</a>
                                <a href="${pageContext.request.contextPath}/guests?action=edit&id=${g.guestId}" class="btn btn-sm btn-secondary">Edit</a>
                                <form method="post" action="${pageContext.request.contextPath}/guests"
                                      style="display:inline"
                                      onsubmit="return confirm('Delete guest ${g.fullName}? This cannot be undone.')">
                                    <input type="hidden" name="action"  value="delete">
                                    <input type="hidden" name="guestId" value="${g.guestId}">
                                    <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
                <p style="margin-top:10px;color:#666;font-size:0.9rem">Total: ${guests.size()} guest(s)</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
