<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Hotel — Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="login-page">

<div class="login-container">
    <div class="login-brand">
        <div class="brand-icon">🏨</div>
        <h1>Grand Hotel</h1>
        <p>Room Reservation System</p>
    </div>

    <div class="login-card">
        <h2>Staff Login</h2>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">
                <span class="alert-icon">⚠</span> ${error}
            </div>
        </c:if>

        <c:if test="${param.logout eq 'true'}">
            <div class="alert alert-success">
                <span class="alert-icon">✓</span> You have been logged out successfully.
            </div>
        </c:if>

        <form method="POST" action="${pageContext.request.contextPath}/login" novalidate id="loginForm">
            <div class="form-group">
                <label for="username">Username</label>
                <div class="input-icon-wrap">
                    <span class="input-icon">👤</span>
                    <input type="text" id="username" name="username"
                           value="${not empty username ? username : ''}"
                           placeholder="Enter username"
                           required autofocus autocomplete="username"
                           <c:if test="${locked}">disabled</c:if>>
                </div>
                <span class="field-error" id="usernameErr"></span>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <div class="input-icon-wrap">
                    <span class="input-icon">🔒</span>
                    <input type="password" id="password" name="password"
                           placeholder="Enter password"
                           required autocomplete="current-password"
                           <c:if test="${locked}">disabled</c:if>>
                    <button type="button" class="toggle-pw" onclick="togglePw()" title="Show/Hide">👁</button>
                </div>
                <span class="field-error" id="passwordErr"></span>
            </div>

            <c:if test="${not locked}">
                <button type="submit" class="btn btn-primary btn-full">Sign In</button>
            </c:if>
            <c:if test="${locked}">
                <button type="button" class="btn btn-danger btn-full" disabled>Account Locked</button>
            </c:if>
        </form>

        <div class="login-footer">
            <small>Default — admin / Admin@123 &nbsp;|&nbsp; receptionist / Staff@123</small>
        </div>
    </div>
</div>

<script>
    function togglePw() {
        const p = document.getElementById('password');
        p.type = p.type === 'password' ? 'text' : 'password';
    }
    document.getElementById('loginForm').addEventListener('submit', function(e) {
        let ok = true;
        const u = document.getElementById('username').value.trim();
        const p = document.getElementById('password').value;
        if (!u) { document.getElementById('usernameErr').textContent = 'Username is required'; ok = false; }
        else    { document.getElementById('usernameErr').textContent = ''; }
        if (!p) { document.getElementById('passwordErr').textContent = 'Password is required'; ok = false; }
        else    { document.getElementById('passwordErr').textContent = ''; }
        if (!ok) e.preventDefault();
    });
</script>
</body>
</html>
