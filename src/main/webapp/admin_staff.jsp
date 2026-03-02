<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin - Ocean View Resort</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        /* ── Tab navigation ─────────────────────────────────────── */
        .admin-tabs {
            display: flex; gap: 0; margin-bottom: 24px;
            border-bottom: 2px solid #dce4ed;
        }
        .admin-tab {
            padding: 10px 24px; cursor: pointer; font-weight: 600;
            font-size: 0.92rem; color: #666; border-bottom: 3px solid transparent;
            margin-bottom: -2px; text-decoration: none; transition: all 0.2s;
        }
        .admin-tab:hover   { color: #1a3a5c; }
        .admin-tab.active  { color: #1a3a5c; border-bottom-color: #1a3a5c; background: #f7fafd; }

        /* ── Tab sections ───────────────────────────────────────── */
        .tab-section { display: none; }
        .tab-section.active { display: block; }

        /* ── Method card icons ──────────────────────────────────── */
        .method-icon {
            display: inline-flex; align-items: center; justify-content: center;
            width: 36px; height: 36px; border-radius: 8px; font-size: 1.2rem;
            background: #eef5fc; margin-right: 8px; flex-shrink: 0;
        }

        /* ── Inline status badge ────────────────────────────────── */
        .active-dot   { color: #27ae60; font-weight: 700; }
        .inactive-dot { color: #e74c3c; font-weight: 700; }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">
    <div class="page-title">&#9881; Admin Panel</div>

    <%-- ── Global alerts ─────────────────────────────────────────── --%>
    <c:if test="${not empty success}">
        <div class="alert alert-success">&#10003; ${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-error">&#9888; ${error}</div>
    </c:if>

    <%-- ── Tab navigation ────────────────────────────────────────── --%>
    <div class="admin-tabs">
        <a class="admin-tab ${empty param.tab || param.tab == 'staff' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin?tab=staff">
            &#128101; Staff Management
        </a>
        <a class="admin-tab ${param.tab == 'methods' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin?tab=methods#payment-methods">
            &#128176; Payment Methods
        </a>
    </div>

    <%-- ══════════════════════════════════════════════════════════ --%>
    <%-- TAB 1 — Staff Management                                  --%>
    <%-- ══════════════════════════════════════════════════════════ --%>
    <div class="tab-section ${empty param.tab || param.tab == 'staff' ? 'active' : ''}">

        <c:if test="${not empty errors}">
            <div class="alert alert-error">
                <ul style="margin:0;padding-left:20px">
                    <c:forEach var="e" items="${errors}"><li>${e}</li></c:forEach>
                </ul>
            </div>
        </c:if>

        <div style="display:grid;grid-template-columns:1fr 1fr;gap:20px">

            <%-- Add / Edit Staff Form --%>
            <div class="card">
                <div class="card-title">
                    ${editUser != null ? '&#9999; Edit Staff Member' : '&#43; Add New Staff Member'}
                </div>
                <c:choose>
                    <c:when test="${editUser != null}">
                        <form method="post" action="${pageContext.request.contextPath}/admin?tab=staff">
                            <input type="hidden" name="action"  value="update">
                            <input type="hidden" name="userId"  value="${editUser.userId}">
                            <div class="form-group">
                                <label>Username</label>
                                <input type="text" value="${editUser.username}" disabled style="background:#f5f5f5">
                            </div>
                            <div class="form-group">
                                <label>Full Name *</label>
                                <input type="text" name="fullName" value="${editUser.fullName}" required>
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <label>Email</label>
                                    <input type="email" name="email" value="${editUser.email}">
                                </div>
                                <div class="form-group">
                                    <label>Phone</label>
                                    <input type="tel" name="phone" value="${editUser.phone}">
                                </div>
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <label>Role</label>
                                    <select name="role">
                                        <option value="STAFF" ${editUser.role=='STAFF'?'selected':''}>STAFF</option>
                                        <option value="ADMIN" ${editUser.role=='ADMIN'?'selected':''}>ADMIN</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label>Status</label>
                                    <select name="isActive">
                                        <option value="true"  ${editUser.active?'selected':''}>Active</option>
                                        <option value="false" ${!editUser.active?'selected':''}>Inactive</option>
                                    </select>
                                </div>
                            </div>
                            <div style="display:flex;gap:10px;margin-top:14px">
                                <button type="submit" class="btn btn-success">Save Changes</button>
                                <a href="${pageContext.request.contextPath}/admin?tab=staff" class="btn btn-secondary">Cancel</a>
                            </div>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <form method="post" action="${pageContext.request.contextPath}/admin?tab=staff">
                            <input type="hidden" name="action" value="add">
                            <div class="form-row">
                                <div class="form-group">
                                    <label>Username *</label>
                                    <input type="text" name="username" required>
                                </div>
                                <div class="form-group">
                                    <label>Password * <small style="color:#888">(min 6 chars)</small></label>
                                    <input type="password" name="password" required minlength="6">
                                </div>
                            </div>
                            <div class="form-group">
                                <label>Full Name *</label>
                                <input type="text" name="fullName" required>
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <label>Email</label>
                                    <input type="email" name="email">
                                </div>
                                <div class="form-group">
                                    <label>Phone *</label>
                                    <input type="tel" name="phone" required>
                                </div>
                            </div>
                            <div class="form-group">
                                <label>Role</label>
                                <select name="role">
                                    <option value="STAFF">STAFF</option>
                                    <option value="ADMIN">ADMIN</option>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-primary" style="margin-top:6px">Add Staff Member</button>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>

            <%-- Staff List --%>
            <div class="card">
                <div class="card-title">&#128101; Current Staff (${users.size()})</div>
                <table>
                    <tr><th>Name</th><th>Username</th><th>Role</th><th>Status</th><th>Actions</th></tr>
                    <c:forEach var="u" items="${users}">
                        <tr>
                            <td><strong>${u.fullName}</strong></td>
                            <td>${u.username}</td>
                            <td><span class="badge ${u.role == 'ADMIN' ? 'badge-confirmed' : 'badge-checked_in'}">${u.role}</span></td>
                            <td><span class="${u.active ? 'active-dot' : 'inactive-dot'}">${u.active ? '● Active' : '● Inactive'}</span></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin?tab=staff&action=edit&id=${u.userId}"
                                   class="btn btn-sm btn-secondary">Edit</a>
                                <c:if test="${u.userId != sessionScope.user.userId}">
                                    <form method="post" action="${pageContext.request.contextPath}/admin?tab=staff"
                                          style="display:inline"
                                          onsubmit="return confirm('Deactivate ${u.fullName}?')">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="userId" value="${u.userId}">
                                        <button type="submit" class="btn btn-sm btn-danger">
                                            ${u.active ? 'Deactivate' : 'Remove'}
                                        </button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </div>
    </div>

    <%-- ══════════════════════════════════════════════════════════ --%>
    <%-- TAB 2 — Payment Methods                                   --%>
    <%-- ══════════════════════════════════════════════════════════ --%>
    <div id="payment-methods" class="tab-section ${param.tab == 'methods' ? 'active' : ''}">

        <c:if test="${not empty successMethod}">
            <div class="alert alert-success">&#10003; ${successMethod}</div>
        </c:if>
        <c:if test="${not empty methodError}">
            <div class="alert alert-error">&#9888; ${methodError}</div>
        </c:if>

        <div style="display:grid;grid-template-columns:1fr 1.6fr;gap:20px">

            <%-- Add / Edit Payment Method Form --%>
            <div class="card">
                <div class="card-title">
                    ${editMethod != null ? '&#9999; Edit Payment Method' : '&#43; Add Payment Method'}
                </div>
                <c:choose>
                    <c:when test="${editMethod != null}">
                        <form method="post" action="${pageContext.request.contextPath}/admin?tab=methods">
                            <input type="hidden" name="action"   value="update-method">
                            <input type="hidden" name="methodId" value="${editMethod.methodId}">
                            <div class="form-group">
                                <label>Method Name *</label>
                                <input type="text" name="methodName" value="${editMethod.methodName}"
                                       required placeholder="e.g. Cash, Credit Card, Bank Transfer">
                            </div>
                            <div style="display:flex;gap:10px;margin-top:12px">
                                <button type="submit" class="btn btn-success">Save Changes</button>
                                <a href="${pageContext.request.contextPath}/admin?tab=methods" class="btn btn-secondary">Cancel</a>
                            </div>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <form method="post" action="${pageContext.request.contextPath}/admin?tab=methods">
                            <input type="hidden" name="action" value="add-method">
                            <div class="form-group">
                                <label>Method Name *</label>
                                <input type="text" name="methodName" required
                                       placeholder="e.g. Cash, Credit Card, PayPal...">
                            </div>
                            <button type="submit" class="btn btn-primary" style="margin-top:4px">
                                &#43; Add Method
                            </button>
                        </form>

                        <div style="margin-top:18px;padding-top:14px;border-top:1px solid #eef2f7">
                            <div style="font-size:0.82rem;color:#888;margin-bottom:8px;font-weight:600">
                                Common Method Icons
                            </div>
                            <div style="display:flex;flex-wrap:wrap;gap:8px;font-size:0.82rem;color:#555">
                                <span>&#128181; Cash</span>
                                <span>&#128179; Credit / Debit Card</span>
                                <span>&#127970; Bank Transfer</span>
                                <span>&#128196; Cheque</span>
                                <span>&#128241; Online / Mobile</span>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <%-- Payment Methods List --%>
            <div class="card">
                <div class="card-title">&#128176; Payment Methods (${paymentMethods.size()})</div>
                <c:choose>
                    <c:when test="${empty paymentMethods}">
                        <p style="color:#888;padding:16px;text-align:center">No payment methods configured.</p>
                    </c:when>
                    <c:otherwise>
                        <table>
                            <tr>
                                <th style="width:50px">#</th>
                                <th>Method Name</th>
                                <th style="text-align:center">Status</th>
                                <th>Actions</th>
                            </tr>
                            <c:forEach var="pm" items="${paymentMethods}">
                                <tr style="${!pm.active ? 'opacity:0.6;' : ''}">
                                    <td style="color:#999">${pm.methodId}</td>
                                    <td>
                                        <span class="method-icon">
                                            <c:choose>
                                                <c:when test="${pm.methodName == 'Cash'}">&#128181;</c:when>
                                                <c:when test="${pm.methodName == 'Credit Card' || pm.methodName == 'Debit Card'}">&#128179;</c:when>
                                                <c:when test="${pm.methodName == 'Bank Transfer'}">&#127970;</c:when>
                                                <c:when test="${pm.methodName == 'Cheque'}">&#128196;</c:when>
                                                <c:otherwise>&#128176;</c:otherwise>
                                            </c:choose>
                                        </span>
                                        <strong>${pm.methodName}</strong>
                                    </td>
                                    <td style="text-align:center">
                                        <span class="${pm.active ? 'active-dot' : 'inactive-dot'}">
                                            ${pm.active ? '● Active' : '● Inactive'}
                                        </span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin?tab=methods&action=edit-method&id=${pm.methodId}"
                                           class="btn btn-sm btn-secondary">Edit</a>
                                        <a href="${pageContext.request.contextPath}/admin?tab=methods&action=toggle-method&id=${pm.methodId}"
                                           class="btn btn-sm ${pm.active ? 'btn-danger' : 'btn-success'}"
                                           onclick="return confirm('${pm.active ? 'Deactivate' : 'Activate'} ${pm.methodName}?')">
                                            ${pm.active ? 'Deactivate' : 'Activate'}
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                        <p style="margin-top:10px;font-size:0.82rem;color:#999">
                            &#9432; Inactive methods will not appear in the payment form.
                        </p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

</div>
</body>
</html>
