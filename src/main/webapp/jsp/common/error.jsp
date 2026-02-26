<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error — Grand Hotel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body style="display:flex;align-items:center;justify-content:center;min-height:100vh;background:var(--bg)">
<div style="text-align:center;padding:40px">
    <div style="font-size:72px">🏨</div>
    <h1 style="color:var(--danger);font-size:48px">${pageContext.errorData.statusCode}</h1>
    <h2>Oops! Something went wrong</h2>
    <p style="color:var(--text-muted);margin:12px 0">
        ${pageContext.errorData.statusCode eq 404 ? 'Page not found.' : 'An internal error occurred.'}
    </p>
    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">Go to Dashboard</a>
</div>
</body>
</html>
