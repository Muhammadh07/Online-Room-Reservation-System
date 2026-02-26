<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Staff" scope="request"/>
<jsp:include page="../common/header.jsp"/>

<div class="page-header">
    <h1>👥 Staff Management</h1>
    <button class="btn btn-primary" onclick="toggleAddForm()">➕ Add Staff</button>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-danger"><span>⚠</span> ${error}</div>
</c:if>
<c:if test="${not empty success}">
    <div class="alert alert-success"><span>✓</span> ${success}</div>
</c:if>

<!-- Add Staff Form (hidden by default) -->
<div class="card" id="addStaffCard" style="display:none">
    <div class="card-header"><h2>Add New Staff Account</h2></div>
    <div class="card-body">
        <form method="POST" action="${pageContext.request.contextPath}/staff" id="addStaffForm" novalidate>
            <input type="hidden" name="action" value="add">
            <div class="form-row">
                <div class="form-group">
                    <label>Username <span class="required">*</span></label>
                    <input type="text" name="username" class="form-control" required
                           placeholder="e.g. john.doe" maxlength="50" autocomplete="off">
                    <span class="field-error" id="unErr"></span>
                </div>
                <div class="form-group">
                    <label>Password <span class="required">*</span></label>
                    <input type="password" name="password" class="form-control" required
                           placeholder="Min 6 characters" minlength="6" autocomplete="new-password">
                    <span class="field-error" id="pwErr"></span>
                </div>
                <div class="form-group">
                    <label>Role <span class="required">*</span></label>
                    <select name="role" class="form-control" required>
                        <option value="RECEPTIONIST">Receptionist</option>
                        <option value="ADMIN">Admin</option>
                    </select>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>Full Name <span class="required">*</span></label>
                    <input type="text" name="fullName" class="form-control" required
                           placeholder="Full name" maxlength="100">
                    <span class="field-error" id="fnErr"></span>
                </div>
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" class="form-control"
                           placeholder="email@hotel.com">
                </div>
            </div>
            <div class="form-actions">
                <button type="button" class="btn btn-secondary" onclick="toggleAddForm()">Cancel</button>
                <button type="submit" class="btn btn-success">➕ Create Account</button>
            </div>
        </form>
    </div>
</div>

<!-- Edit Form (shown when editing) -->
<c:if test="${not empty editUser}">
    <div class="card">
        <div class="card-header"><h2>Edit Staff — ${editUser.fullName}</h2></div>
        <div class="card-body">
            <form method="POST" action="${pageContext.request.contextPath}/staff" novalidate>
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="userId" value="${editUser.userId}">
                <div class="form-row">
                    <div class="form-group">
                        <label>Full Name</label>
                        <input type="text" name="fullName" class="form-control"
                               value="${editUser.fullName}" required>
                    </div>
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" name="email" class="form-control"
                               value="${editUser.email}">
                    </div>
                    <div class="form-group">
                        <label>Role</label>
                        <select name="role" class="form-control">
                            <option value="RECEPTIONIST" ${editUser.role eq 'RECEPTIONIST' ? 'selected' : ''}>Receptionist</option>
                            <option value="ADMIN"        ${editUser.role eq 'ADMIN'        ? 'selected' : ''}>Admin</option>
                        </select>
                    </div>
                </div>
                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/staff" class="btn btn-secondary">Cancel</a>
                    <button type="submit" class="btn btn-primary">💾 Save Changes</button>
                </div>
            </form>
        </div>
    </div>
</c:if>

<!-- Staff Table -->
<div class="card">
    <div class="card-body">
        <div class="table-wrap">
            <table class="data-table">
                <thead>
                <tr>
                    <th>#</th><th>Username</th><th>Full Name</th>
                    <th>Role</th><th>Email</th><th>Status</th>
                    <th>Last Login</th><th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="u" items="${staffList}" varStatus="i">
                    <tr>
                        <td>${i.count}</td>
                        <td><strong>${u.username}</strong></td>
                        <td>${u.fullName}</td>
                        <td><span class="badge ${u.role eq 'ADMIN' ? 'badge-admin' : 'badge-staff'}">${u.role}</span></td>
                        <td>${u.email}</td>
                        <td>
                            <span class="status-dot ${u.active ? 'dot-green' : 'dot-red'}"></span>
                                ${u.active ? 'Active' : 'Inactive'}
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty u.lastLogin}">
                                    <fmt:formatDate value="${u.lastLogin}" pattern="dd MMM yyyy HH:mm"/>
                                </c:when>
                                <c:otherwise><em>Never</em></c:otherwise>
                            </c:choose>
                        </td>
                        <td class="action-btns">
                            <a href="${pageContext.request.contextPath}/staff?action=edit&id=${u.userId}"
                               class="btn btn-sm btn-info">✏</a>
                            <c:if test="${u.userId ne sessionScope.userId and u.active}">
                                <form method="POST" action="${pageContext.request.contextPath}/staff"
                                      style="display:inline"
                                      onsubmit="return confirm('Deactivate ${u.fullName}?')">
                                    <input type="hidden" name="action" value="deactivate">
                                    <input type="hidden" name="id" value="${u.userId}">
                                    <button type="submit" class="btn btn-sm btn-danger">🚫</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    function toggleAddForm() {
        const f = document.getElementById('addStaffCard');
        f.style.display = f.style.display === 'none' ? 'block' : 'none';
    }

    document.getElementById('addStaffForm')?.addEventListener('submit', function(e) {
        let ok = true;
        const un  = document.querySelector('[name=username]').value.trim();
        const pw  = document.querySelector('[name=password]').value;
        const fn  = document.querySelector('[name=fullName]').value.trim();
        if (!un) { setErr('unErr','Username required'); ok=false; } else clearErr('unErr');
        if (!pw || pw.length < 6) { setErr('pwErr','Min 6 characters'); ok=false; } else clearErr('pwErr');
        if (!fn) { setErr('fnErr','Full name required'); ok=false; } else clearErr('fnErr');
        if (!ok) e.preventDefault();
    });

    function setErr(id,msg)  { document.getElementById(id).textContent = msg; }
    function clearErr(id)    { document.getElementById(id).textContent = ''; }
</script>
<jsp:include page="../common/footer.jsp"/>
