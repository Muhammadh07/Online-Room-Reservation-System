<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="New Reservation" scope="request"/>
<jsp:include page="header.jsp"/>

<div class="page-header">
  <h1>➕ New Reservation</h1>
  <a href="${pageContext.request.contextPath}/reservations" class="btn btn-secondary">← Back</a>
</div>

<c:if test="${not empty error}">
  <div class="alert alert-danger"><span>⚠</span> ${error}</div>
</c:if>
<c:if test="${not empty success}">
  <div class="alert alert-success">
    <span>✓</span> ${success}
    <c:if test="${not empty resNumber}">
      <br><strong>Reservation Number: ${resNumber}</strong>
      <a href="${pageContext.request.contextPath}/reservations?action=view&number=${resNumber}"
         class="btn btn-sm btn-info" style="margin-left:10px">View Details</a>
    </c:if>
  </div>
</c:if>

<!-- STEP 1: Search Available Rooms -->
<div class="card">
  <div class="card-header">
    <h2>Step 1 — Search Available Rooms</h2>
  </div>
  <div class="card-body">
    <form method="POST" action="${pageContext.request.contextPath}/reservations" id="searchForm">
      <input type="hidden" name="action" value="search-rooms">
      <div class="form-row">
        <div class="form-group">
          <label>Room Type</label>
          <select name="roomType" class="form-control">
            <option value="">All Types</option>
            <option value="SINGLE"  ${roomType eq 'SINGLE'  ? 'selected' : ''}>Single</option>
            <option value="DOUBLE"  ${roomType eq 'DOUBLE'  ? 'selected' : ''}>Double</option>
            <option value="DELUXE"  ${roomType eq 'DELUXE'  ? 'selected' : ''}>Deluxe</option>
            <option value="SUITE"   ${roomType eq 'SUITE'   ? 'selected' : ''}>Suite</option>
          </select>
        </div>
        <div class="form-group">
          <label>Check-In Date <span class="required">*</span></label>
          <input type="date" name="checkin" class="form-control"
                 value="${checkin}" required
                 min="<%= java.time.LocalDate.now() %>">
          <span class="field-error" id="checkinErr"></span>
        </div>
        <div class="form-group">
          <label>Check-Out Date <span class="required">*</span></label>
          <input type="date" name="checkout" class="form-control"
                 value="${checkout}" required
                 min="<%= java.time.LocalDate.now().plusDays(1) %>">
          <span class="field-error" id="checkoutErr"></span>
        </div>
        <div class="form-group form-group-btn">
          <label>&nbsp;</label>
          <button type="submit" class="btn btn-primary">🔍 Search Rooms</button>
        </div>
      </div>
    </form>
  </div>
</div>

<!-- Available Rooms Result -->
<c:if test="${not empty availableRooms}">
  <div class="card">
    <div class="card-header">
      <h2>Available Rooms — ${fn:length(availableRooms)} found</h2>
    </div>
    <div class="card-body">
      <div class="room-grid">
        <c:forEach var="room" items="${availableRooms}">
          <div class="room-card" onclick="selectRoom(${room.roomId},'${room.roomNumber}','${room.typeName}',${room.ratePerNight})">
            <div class="room-number">Room ${room.roomNumber}</div>
            <div class="room-type badge badge-${room.typeName.toLowerCase()}">${room.typeName}</div>
            <div class="room-detail">Floor: ${room.floorNumber}</div>
            <div class="room-detail">Max: ${room.maxOccupancy} guests</div>
            <div class="room-rate">LKR <fmt:formatNumber value="${room.ratePerNight}" pattern="#,##0.00"/>/night</div>
            <div class="room-select-btn">Select</div>
          </div>
        </c:forEach>
      </div>
    </div>
  </div>

  <!-- STEP 2: Guest Details + Confirm -->
  <div class="card" id="guestForm" style="display:none">
    <div class="card-header">
      <h2>Step 2 — Guest Details</h2>
      <div class="selected-room-info" id="selectedRoomInfo"></div>
    </div>
    <div class="card-body">
      <form method="POST" action="${pageContext.request.contextPath}/reservations" id="reserveForm" novalidate>
        <input type="hidden" name="action"   value="add">
        <input type="hidden" name="roomId"   id="roomId">
        <input type="hidden" name="checkin"  value="${checkin}">
        <input type="hidden" name="checkout" value="${checkout}">

        <div class="form-row">
          <div class="form-group">
            <label>Guest Full Name <span class="required">*</span></label>
            <input type="text" name="guestName" class="form-control"
                   value="${guestName}" placeholder="Enter full name" required
                   maxlength="100">
            <span class="field-error" id="guestNameErr"></span>
          </div>
          <div class="form-group">
            <label>Contact Number <span class="required">*</span></label>
            <input type="tel" name="contactNumber" class="form-control"
                   value="${contactNumber}" placeholder="+94 77 123 4567" required
                   pattern="[0-9+\-\s()]{7,15}">
            <span class="field-error" id="contactErr"></span>
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>Address</label>
            <input type="text" name="address" class="form-control"
                   value="${address}" placeholder="Guest address" maxlength="200">
          </div>
          <div class="form-group">
            <label>Email Address</label>
            <input type="email" name="email" class="form-control"
                   value="${email}" placeholder="guest@email.com">
            <span class="field-error" id="emailErr"></span>
          </div>
        </div>

        <div class="booking-summary" id="bookingSummary"></div>

        <div class="form-actions">
          <button type="button" class="btn btn-secondary" onclick="document.getElementById('guestForm').style.display='none'">
            ← Change Room
          </button>
          <button type="submit" class="btn btn-success btn-lg">
            ✅ Confirm Reservation
          </button>
        </div>
      </form>
    </div>
  </div>
</c:if>

<c:if test="${not empty checkin and empty availableRooms and empty error}">
  <div class="alert alert-warning">
    <span>ℹ</span> No rooms available for the selected dates and room type. Please try different dates.
  </div>
</c:if>

<script>
  const checkin  = '${checkin}';
  const checkout = '${checkout}';

  function selectRoom(roomId, roomNumber, typeName, rate) {
    document.getElementById('roomId').value = roomId;
    document.querySelectorAll('.room-card').forEach(c => c.classList.remove('room-selected'));
    event.currentTarget.classList.add('room-selected');

    // Calculate nights
    const nights = Math.round((new Date(checkout) - new Date(checkin)) / 86400000);
    const subtotal = rate * nights;
    const tax = subtotal * 0.10;
    const total = subtotal + tax;

    document.getElementById('selectedRoomInfo').innerHTML =
            `<strong>Room ${roomNumber}</strong> — ${typeName} — LKR ${rate.toFixed(2)}/night`;

    document.getElementById('bookingSummary').innerHTML = `
    <h4>Booking Summary</h4>
    <div class="summary-row"><span>Room</span><span>Room ${roomNumber} (${typeName})</span></div>
    <div class="summary-row"><span>Check-In</span><span>${checkin}</span></div>
    <div class="summary-row"><span>Check-Out</span><span>${checkout}</span></div>
    <div class="summary-row"><span>Nights</span><span>${nights}</span></div>
    <div class="summary-row"><span>Rate/Night</span><span>LKR ${rate.toFixed(2)}</span></div>
    <div class="summary-row"><span>Subtotal</span><span>LKR ${subtotal.toFixed(2)}</span></div>
    <div class="summary-row"><span>Tax (10%)</span><span>LKR ${tax.toFixed(2)}</span></div>
    <div class="summary-row summary-total"><span>TOTAL</span><span>LKR ${total.toFixed(2)}</span></div>
  `;
    document.getElementById('guestForm').style.display = 'block';
    document.getElementById('guestForm').scrollIntoView({behavior:'smooth'});
  }

  // Client-side validation
  document.getElementById('reserveForm')?.addEventListener('submit', function(e) {
    let ok = true;
    const name    = document.querySelector('[name=guestName]').value.trim();
    const contact = document.querySelector('[name=contactNumber]').value.trim();
    const email   = document.querySelector('[name=email]').value.trim();

    if (!name) { setErr('guestNameErr','Guest name is required'); ok=false; }
    else clearErr('guestNameErr');

    if (!contact || !/^[0-9+\-\s()]{7,15}$/.test(contact.replace(/\s/g,''))) {
      setErr('contactErr','Valid contact number required'); ok=false;
    } else clearErr('contactErr');

    if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      setErr('emailErr','Invalid email address'); ok=false;
    } else clearErr('emailErr');

    if (!ok) e.preventDefault();
  });

  function setErr(id, msg)  { document.getElementById(id).textContent = msg; }
  function clearErr(id)     { document.getElementById(id).textContent = ''; }

  // Date validation
  document.querySelector('[name=checkin]')?.addEventListener('change', function() {
    const checkout = document.querySelector('[name=checkout]');
    checkout.min = this.value;
    if (checkout.value && checkout.value <= this.value) {
      const d = new Date(this.value); d.setDate(d.getDate()+1);
      checkout.value = d.toISOString().split('T')[0];
    }
  });
</script>
<jsp:include page="footer.jsp"/>
