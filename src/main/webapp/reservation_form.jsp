<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>New Reservation - Ocean View Resort</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">
    <div class="page-title">New Reservation</div>

    <c:if test="${not empty errors}">
        <div class="alert alert-error">
            <ul class="error-list">
                <c:forEach var="e" items="${errors}"><li>${e}</li></c:forEach>
            </ul>
        </div>
    </c:if>

    <!-- STEP 1: Search Rooms -->
    <div class="card">
        <div class="card-title">Step 1: Search Available Rooms</div>
        <form method="get" action="${pageContext.request.contextPath}/reservations">
            <input type="hidden" name="action" value="search-rooms">
            <div class="form-row-3">
                <div class="form-group">
                    <label>Check-In Date *</label>
                    <input type="date" name="checkIn" value="${checkIn}" required min="<%= java.time.LocalDate.now() %>">
                </div>
                <div class="form-group">
                    <label>Check-Out Date *</label>
                    <input type="date" name="checkOut" value="${checkOut}" required>
                </div>
                <div class="form-group">
                    <label>Room Type</label>
                    <select name="typeId">
                        <option value="0">Any Type</option>
                        <c:forEach var="rt" items="${roomTypes}">
                            <option value="${rt.typeId}" ${selectedTypeId==rt.typeId?'selected':''}>${rt.typeName} - $${rt.basePrice}/night</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <button type="submit" class="btn btn-primary">Search Available Rooms</button>
        </form>
    </div>

    <!-- STEP 2: Select room and fill details (shown after search) -->
    <c:if test="${not empty availableRooms}">
        <form method="post" action="${pageContext.request.contextPath}/reservations" id="resForm">
            <input type="hidden" name="action" value="create">
            <input type="hidden" name="checkIn" value="${checkIn}">
            <input type="hidden" name="checkOut" value="${checkOut}">

            <div class="card">
                <div class="card-title">Step 2: Select Room</div>
                <table>
                    <tr><th>Select</th><th>Room No</th><th>Type</th><th>Floor</th><th>Max Guests</th><th>Price/Night</th></tr>
                    <c:forEach var="room" items="${availableRooms}">
                        <tr>
                            <td><input type="radio" name="roomId" value="${room.roomId}" required></td>
                            <td><strong>${room.roomNumber}</strong></td>
                            <td>${room.typeName}</td>
                            <td>${room.floorNumber}</td>
                            <td>${room.maxOccupancy}</td>
                            <td>$<fmt:formatNumber value="${room.basePrice}" pattern="0.00"/></td>
                        </tr>
                    </c:forEach>
                </table>
            </div>

            <div class="card">
                <div class="card-title">Step 3: Guest Details</div>
                <div class="form-group">
                    <label>Guest</label>
                    <div style="display:flex;gap:12px;align-items:center">
                        <label><input type="radio" name="guestMode" value="existing" id="existingMode" onchange="toggleGuestMode()"> Existing Guest</label>
                        <label><input type="radio" name="guestMode" value="new" id="newMode" onchange="toggleGuestMode()" checked> New Guest</label>
                    </div>
                </div>

                <div id="existingGuestDiv" style="display:none">
                    <div class="form-group">
                        <label>Select Guest</label>
                        <select name="guestId">
                            <option value="">-- Select Guest --</option>
                            <c:forEach var="g" items="${guests}">
                                <option value="${g.guestId}">${g.fullName} (${g.phone})</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div id="newGuestDiv">
                    <div class="form-row">
                        <div class="form-group">
                            <label>Full Name *</label>
                            <input type="text" name="guestName" placeholder="e.g. John Smith">
                        </div>
                        <div class="form-group">
                            <label>Phone *</label>
                            <input type="tel" name="guestPhone" placeholder="e.g. +94771234567">
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Email</label>
                            <input type="email" name="guestEmail" placeholder="email@example.com">
                        </div>
                        <div class="form-group">
                            <label>NIC / Passport</label>
                            <input type="text" name="guestNic" placeholder="ID number">
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Address</label>
                        <input type="text" name="guestAddress" placeholder="Address">
                    </div>
                </div>
            </div>

            <div class="card">
                <div class="card-title">Step 4: Booking Details</div>
                <div class="form-row-3">
                    <div class="form-group">
                        <label>Adults *</label>
                        <input type="number" name="numAdults" value="1" min="1" max="10" required>
                    </div>
                    <div class="form-group">
                        <label>Children</label>
                        <input type="number" name="numChildren" value="0" min="0" max="10">
                    </div>
                    <div class="form-group">
                        <label>Special Requests</label>
                        <textarea name="specialRequests" placeholder="Any special requests..."></textarea>
                    </div>
                </div>
                <button type="submit" class="btn btn-success" onclick="return validateForm()">✓ Create Reservation</button>
                <a href="${pageContext.request.contextPath}/reservations" class="btn btn-secondary" style="margin-left:10px">Cancel</a>
            </div>
        </form>
    </c:if>

    <c:if test="${empty availableRooms and not empty checkIn}">
        <div class="alert alert-info">No rooms available for the selected dates and type. Please try different dates.</div>
    </c:if>
</div>

<script>
    function toggleGuestMode() {
        var mode = document.querySelector('input[name="guestMode"]:checked').value;
        document.getElementById('existingGuestDiv').style.display = (mode === 'existing') ? 'block' : 'none';
        document.getElementById('newGuestDiv').style.display = (mode === 'new') ? 'block' : 'none';
    }
    function validateForm() {
        var room = document.querySelector('input[name="roomId"]:checked');
        if (!room) { alert('Please select a room.'); return false; }
        var mode = document.querySelector('input[name="guestMode"]:checked').value;
        if (mode === 'new') {
            var name = document.querySelector('[name="guestName"]').value.trim();
            var phone = document.querySelector('[name="guestPhone"]').value.trim();
            if (!name) { alert('Guest name is required.'); return false; }
            if (!phone) { alert('Guest phone is required.'); return false; }
        }
        return true;
    }
</script>
</body>
</html>
