<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login - Ocean View Resort</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="login-wrapper">
    <div class="login-card">
        <div class="login-logo">
            <h2>🌊 Ocean View Resort</h2>
            <p>Reservation Management System</p>
        </div>
        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error">${error}</div>
        <% } %>
        <form method="post" action="${pageContext.request.contextPath}/login">
            <div class="form-group">
                <label>Username</label>
                <input type="text" name="username" value="${param.username}" required autofocus placeholder="Enter username">
            </div>
            <div class="form-group">
                <label>Password</label>
                <input type="password" name="password" required placeholder="Enter password">
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%;margin-top:8px">Sign In</button>
        </form>
    </div>
</div>
</body>
</html>
