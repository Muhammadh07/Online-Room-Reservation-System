<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Error - Ocean View Resort</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">
    <div class="page-title">Error</div>
    <div class="card">
        <c:choose>
            <c:when test="${not empty error}">
                <div class="alert alert-danger" style="background:#fdecea;border-left:4px solid #e74c3c;padding:16px;border-radius:5px;margin-bottom:12px;">
                    <strong>Error:</strong> <c:out value="${error}"/>
                </div>
            </c:when>
            <c:when test="${not empty requestScope['javax.servlet.error.message']}">
                <div class="alert alert-danger" style="background:#fdecea;border-left:4px solid #e74c3c;padding:16px;border-radius:5px;margin-bottom:12px;">
                    <strong>Error ${requestScope['javax.servlet.error.status_code']}:</strong>
                    <c:out value="${requestScope['javax.servlet.error.message']}"/>
                </div>
            </c:when>
            <c:otherwise>
                <div class="alert alert-danger" style="background:#fdecea;border-left:4px solid #e74c3c;padding:16px;border-radius:5px;margin-bottom:12px;">
                    An unexpected error occurred. Please try again or contact support.
                </div>
            </c:otherwise>
        </c:choose>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">Go to Dashboard</a>
        <a href="javascript:history.back()" class="btn btn-secondary" style="margin-left:10px">Go Back</a>
    </div>
</div>
</body>
</html>
