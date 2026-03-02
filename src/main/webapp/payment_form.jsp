<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <title>Payments - Ocean View Resort</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        /* ═══════════════════════════════════════════════════════
           PAGE HEADER
        ═══════════════════════════════════════════════════════ */
        .pmf-breadcrumb { font-size: 0.8rem; color: #aaa; margin-bottom: 6px; }
        .pmf-breadcrumb a { color: #2471a3; text-decoration: none; }
        .pmf-breadcrumb a:hover { text-decoration: underline; }

        .pmf-page-head {
            display: flex; align-items: center; justify-content: space-between;
            margin-bottom: 24px; flex-wrap: wrap; gap: 12px;
        }
        .pmf-page-head h1 {
            font-size: 1.55rem; font-weight: 800; color: #1a3a5c; margin: 0;
            display: flex; align-items: center; gap: 12px;
        }
        .pmf-title-icon {
            width: 42px; height: 42px; border-radius: 10px;
            background: linear-gradient(135deg, #1a3a5c, #2471a3);
            display: flex; align-items: center; justify-content: center;
            color: #fff; font-size: 1.2rem; flex-shrink: 0;
        }

        /* ═══════════════════════════════════════════════════════
           KPI CARDS
        ═══════════════════════════════════════════════════════ */
        .pmf-kpis {
            display: grid; grid-template-columns: repeat(3, 1fr);
            gap: 16px; margin-bottom: 24px;
        }
        @media(max-width:680px) { .pmf-kpis { grid-template-columns: 1fr; } }

        .kpi-card {
            background: #fff; border-radius: 14px;
            box-shadow: 0 2px 14px rgba(0,0,0,0.07);
            padding: 20px 22px; display: flex; align-items: center;
            gap: 16px; overflow: hidden; position: relative;
        }
        .kpi-card::after {
            content: ''; position: absolute; right: -18px; top: -18px;
            width: 80px; height: 80px; border-radius: 50%; opacity: 0.06;
        }
        .kpi-green::after  { background: #27ae60; }
        .kpi-blue::after   { background: #2471a3; }
        .kpi-orange::after { background: #e67e22; }

        .kpi-icon-box {
            width: 54px; height: 54px; border-radius: 14px;
            display: flex; align-items: center; justify-content: center;
            font-size: 1.6rem; flex-shrink: 0;
        }
        .kpi-green  .kpi-icon-box { background: #eafaf1; }
        .kpi-blue   .kpi-icon-box { background: #eaf4fc; }
        .kpi-orange .kpi-icon-box { background: #fef5ec; }

        .kpi-body { flex: 1; min-width: 0; }
        .kpi-label {
            font-size: 0.75rem; color: #aaa; text-transform: uppercase;
            letter-spacing: 0.7px; font-weight: 700; margin-bottom: 4px;
        }
        .kpi-value { font-size: 1.65rem; font-weight: 800; line-height: 1; }
        .kpi-green  .kpi-value { color: #27ae60; }
        .kpi-blue   .kpi-value { color: #2471a3; }
        .kpi-orange .kpi-value { color: #e67e22; }
        .kpi-sub { font-size: 0.76rem; color: #bbb; margin-top: 5px; }

        /* ═══════════════════════════════════════════════════════
           SEARCH BAR
        ═══════════════════════════════════════════════════════ */
        .pmf-search-card {
            background: #fff; border-radius: 14px;
            box-shadow: 0 2px 14px rgba(0,0,0,0.07);
            padding: 20px 24px; margin-bottom: 24px;
        }
        .pmf-search-row { display: flex; gap: 10px; align-items: stretch; }
        .pmf-search-field {
            flex: 1; display: flex; align-items: center;
            border: 2px solid #e0e8f0; border-radius: 10px;
            background: #f8fafc; padding: 0 16px; gap: 10px;
            transition: all 0.2s;
        }
        .pmf-search-field:focus-within {
            border-color: #1a3a5c; background: #fff;
            box-shadow: 0 0 0 4px rgba(26,58,92,0.07);
        }
        .pmf-search-field .si { font-size: 1.1rem; color: #ccc; }
        .pmf-search-field input {
            border: none !important; background: transparent !important;
            padding: 12px 0 !important; font-size: 0.95rem !important;
            flex: 1; outline: none; box-shadow: none !important;
            color: #333;
        }
        .pmf-quick-links {
            display: flex; gap: 16px; flex-wrap: wrap; margin-top: 12px;
        }
        .pmf-quick-links a {
            font-size: 0.82rem; color: #2471a3; text-decoration: none;
            display: inline-flex; align-items: center; gap: 5px;
            padding: 3px 0; border-bottom: 1px dashed transparent;
            transition: border-color 0.15s;
        }
        .pmf-quick-links a:hover { border-bottom-color: #2471a3; }

        /* ═══════════════════════════════════════════════════════
           BILL + PAYMENT LAYOUT
        ═══════════════════════════════════════════════════════ */
        .pmf-bill-layout {
            display: grid; grid-template-columns: 380px 1fr;
            gap: 20px; margin-bottom: 20px; align-items: start;
        }
        @media(max-width:880px) { .pmf-bill-layout { grid-template-columns: 1fr; } }

        /* ═══════════════════════════════════════════════════════
           INVOICE RECEIPT (left panel)
        ═══════════════════════════════════════════════════════ */
        .invoice-receipt {
            background: #fff; border-radius: 14px;
            box-shadow: 0 2px 14px rgba(0,0,0,0.07); overflow: hidden;
        }
        .inv-head {
            background: linear-gradient(135deg, #1a3a5c 0%, #2980b9 100%);
            padding: 22px 24px; color: #fff; position: relative;
        }
        .inv-head::after {
            content: ''; position: absolute; right: -30px; bottom: -30px;
            width: 100px; height: 100px; border-radius: 50%;
            background: rgba(255,255,255,0.07);
        }
        .inv-head .hotel { font-size: 1rem; font-weight: 700; letter-spacing: 0.3px; }
        .inv-head .inv-no { font-size: 0.82rem; opacity: 0.7; margin-top: 3px; }
        .inv-head .inv-status {
            display: inline-block; margin-top: 12px;
            background: rgba(255,255,255,0.18); border-radius: 20px;
            padding: 4px 14px; font-size: 0.78rem; font-weight: 700;
            letter-spacing: 0.4px;
        }

        .inv-rows { padding: 18px 24px 4px; }
        .inv-row {
            display: flex; justify-content: space-between; align-items: center;
            padding: 8px 0; border-bottom: 1px solid #f4f6f8; font-size: 0.88rem;
        }
        .inv-row:last-child { border-bottom: none; }
        .inv-row .rl { color: #888; display: flex; align-items: center; gap: 6px; }
        .inv-row .rv { font-weight: 600; color: #333; text-align: right; max-width: 55%; }

        .inv-sep {
            border: none; border-top: 2px dashed #e8edf2;
            margin: 4px 24px 0;
        }

        .inv-totals { padding: 14px 24px 16px; }
        .inv-total-row {
            display: flex; justify-content: space-between;
            padding: 5px 0; font-size: 0.88rem; color: #777;
        }
        .inv-total-row .itv { color: #444; font-weight: 600; }
        .inv-total-row.divider {
            border-top: 2px solid #eef2f7; padding-top: 10px; margin-top: 6px;
            font-size: 1rem; font-weight: 700; color: #1a3a5c;
        }
        .inv-total-row.divider .itv { color: #1a3a5c; }
        .inv-total-row.paid-row .itv { color: #27ae60; font-weight: 700; }
        .inv-total-row.bal-row .itv {
            font-size: 1.15rem; font-weight: 800; color: #e74c3c;
        }
        .inv-total-row.bal-settled .itv { color: #27ae60; }

        .inv-actions {
            padding: 14px 24px; background: #f7fafd;
            border-top: 1px solid #eef2f7; display: flex; gap: 8px; flex-wrap: wrap;
        }

        /* ═══════════════════════════════════════════════════════
           PAYMENT PANEL (right panel)
        ═══════════════════════════════════════════════════════ */
        .pay-panel {
            background: #fff; border-radius: 14px;
            box-shadow: 0 2px 14px rgba(0,0,0,0.07); overflow: hidden;
        }
        .pay-panel-top {
            padding: 18px 24px 16px; border-bottom: 1px solid #f0f4f8;
            display: flex; align-items: center; justify-content: space-between;
        }
        .pay-panel-top .ppt-title {
            font-size: 1rem; font-weight: 700; color: #1a3a5c;
            display: flex; align-items: center; gap: 8px;
        }
        .pay-panel-top .ppt-sub { font-size: 0.8rem; color: #bbb; }

        .pay-panel-body { padding: 22px 24px; }

        /* Balance hero */
        .bal-hero {
            background: linear-gradient(135deg, #fff5f5, #fff0f0);
            border: 1.5px solid #f5c6cb; border-radius: 12px;
            padding: 18px 20px; text-align: center; margin-bottom: 18px;
        }
        .bal-hero .bh-lbl {
            font-size: 0.73rem; text-transform: uppercase; letter-spacing: 0.8px;
            color: #c0392b; font-weight: 700; margin-bottom: 6px;
        }
        .bal-hero .bh-amt {
            font-size: 2.5rem; font-weight: 900; color: #c0392b;
            line-height: 1; letter-spacing: -1px;
        }
        .bal-hero .bh-note {
            font-size: 0.78rem; color: #e57070; margin-top: 8px;
        }

        /* Progress bar */
        .pay-prog { margin-bottom: 22px; }
        .pay-prog .prog-labels {
            display: flex; justify-content: space-between;
            font-size: 0.76rem; color: #aaa; margin-bottom: 7px; font-weight: 600;
        }
        .pay-prog .prog-labels .prog-pct { color: #27ae60; }
        .prog-track {
            height: 9px; background: #f0f4f8; border-radius: 99px; overflow: hidden;
        }
        .prog-fill {
            height: 100%;
            background: linear-gradient(90deg, #27ae60 0%, #52d68a 100%);
            border-radius: 99px; transition: width 0.8s cubic-bezier(.4,0,.2,1);
        }

        /* Step label */
        .step-lbl {
            font-size: 0.85rem; font-weight: 700; color: #444;
            margin-bottom: 10px; display: flex; align-items: center; gap: 9px;
        }
        .step-num {
            width: 24px; height: 24px; border-radius: 50%;
            background: #1a3a5c; color: #fff; font-size: 0.7rem; font-weight: 800;
            display: inline-flex; align-items: center; justify-content: center;
            flex-shrink: 0;
        }
        .step-lbl small { font-size: 0.77rem; font-weight: 400; color: #bbb; }

        /* Payment method grid */
        .mth-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(96px, 1fr));
            gap: 10px; margin-bottom: 6px;
        }
        .mth-card {
            border: 2px solid #e4ecf5; border-radius: 12px;
            padding: 14px 8px 10px; text-align: center; cursor: pointer;
            background: #f8fbff; transition: all 0.18s; position: relative;
            user-select: none;
        }
        .mth-card:hover {
            border-color: #2471a3; background: #eef6ff;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(36,113,163,0.12);
        }
        .mth-card.active {
            border-color: #1a3a5c; background: #e8f0f9;
            box-shadow: 0 4px 14px rgba(26,58,92,0.15);
        }
        .mth-card.active::after {
            content: '✓'; position: absolute; top: 6px; right: 9px;
            font-size: 0.68rem; font-weight: 800; color: #1a3a5c;
        }
        .mth-card input[type=radio] { display: none; }
        .mth-icon { font-size: 1.9rem; display: block; margin-bottom: 7px; line-height: 1; }
        .mth-name {
            font-size: 0.72rem; font-weight: 700; color: #666; line-height: 1.25;
        }
        .mth-card.active .mth-name { color: #1a3a5c; }
        .mth-err {
            color: #e74c3c; font-size: 0.8rem; margin-top: 5px; display: none;
        }

        /* Amount input */
        .amt-field {
            display: flex; align-items: stretch; border: 2px solid #e4ecf5;
            border-radius: 10px; overflow: hidden; background: #fff;
            transition: all 0.2s;
        }
        .amt-field:focus-within {
            border-color: #1a3a5c;
            box-shadow: 0 0 0 4px rgba(26,58,92,0.07);
        }
        .amt-currency {
            padding: 0 14px; font-size: 1.1rem; font-weight: 700; color: #1a3a5c;
            background: #f4f8fc; border-right: 2px solid #e4ecf5;
            display: flex; align-items: center; flex-shrink: 0;
        }
        .amt-field input {
            border: none !important; background: transparent !important;
            padding: 13px 14px !important; font-size: 1.35rem !important;
            font-weight: 800; color: #1a3a5c; flex: 1; outline: none;
            box-shadow: none !important; min-width: 0;
        }
        .amt-presets { display: flex; gap: 8px; margin-top: 9px; flex-wrap: wrap; }
        .amt-preset {
            padding: 5px 16px; border: 1.5px solid #d8e4f0; border-radius: 20px;
            font-size: 0.79rem; font-weight: 700; background: #f4f8fc;
            cursor: pointer; color: #555; transition: all 0.15s;
        }
        .amt-preset:hover {
            background: #e0edf8; border-color: #1a3a5c; color: #1a3a5c;
        }

        /* Reference input */
        .ref-input {
            border: 2px solid #e4ecf5 !important; border-radius: 10px !important;
            padding: 11px 14px !important; font-size: 0.92rem !important;
            transition: all 0.2s !important;
        }
        .ref-input:focus {
            border-color: #1a3a5c !important;
            box-shadow: 0 0 0 4px rgba(26,58,92,0.07) !important;
        }

        /* Submit */
        .pay-btn {
            width: 100%; padding: 15px; border: none; border-radius: 12px;
            background: linear-gradient(135deg, #1e9c56, #27ae60);
            color: #fff; font-size: 1.05rem; font-weight: 800; cursor: pointer;
            display: flex; align-items: center; justify-content: center; gap: 10px;
            box-shadow: 0 6px 18px rgba(39,174,96,0.32); transition: all 0.2s;
            letter-spacing: 0.3px; margin-top: 22px;
        }
        .pay-btn:hover {
            background: linear-gradient(135deg, #187a44, #1e9c56);
            box-shadow: 0 8px 22px rgba(39,174,96,0.42);
            transform: translateY(-2px);
        }
        .pay-btn:active { transform: translateY(0); }

        /* ═══════════════════════════════════════════════════════
           PAID / SETTLED STATE
        ═══════════════════════════════════════════════════════ */
        .paid-receipt {
            padding: 36px 28px; text-align: center;
        }
        .paid-circle {
            width: 76px; height: 76px; border-radius: 50%;
            background: linear-gradient(135deg, #27ae60, #52d68a);
            display: flex; align-items: center; justify-content: center;
            margin: 0 auto 18px; font-size: 2.2rem; color: #fff;
            box-shadow: 0 10px 28px rgba(39,174,96,0.32);
        }
        .paid-title {
            font-size: 1.35rem; font-weight: 800; color: #1a3a5c; margin-bottom: 6px;
        }
        .paid-sub { font-size: 0.88rem; color: #aaa; margin-bottom: 20px; }
        .paid-amount {
            font-size: 2.2rem; font-weight: 900; color: #27ae60;
            letter-spacing: -1px; margin-bottom: 4px;
        }
        .paid-amount-lbl { font-size: 0.78rem; color: #bbb; margin-bottom: 22px; }

        /* ═══════════════════════════════════════════════════════
           PAYMENT HISTORY
        ═══════════════════════════════════════════════════════ */
        .history-card {
            background: #fff; border-radius: 14px;
            box-shadow: 0 2px 14px rgba(0,0,0,0.07);
            overflow: hidden; margin-bottom: 20px;
        }
        .history-head {
            padding: 16px 24px; border-bottom: 1px solid #f0f4f8;
            display: flex; align-items: center; justify-content: space-between;
        }
        .history-head .ht { font-size: 1rem; font-weight: 700; color: #1a3a5c; }
        .history-head .hc { font-size: 0.8rem; color: #bbb; }

        .hist-tbl { width: 100%; border-collapse: collapse; }
        .hist-tbl th {
            background: #f7fafd; color: #7a8fa6; padding: 10px 18px;
            font-size: 0.76rem; text-transform: uppercase; letter-spacing: 0.6px;
            font-weight: 700; text-align: left; border-bottom: 1px solid #eef2f7;
        }
        .hist-tbl td {
            padding: 13px 18px; border-bottom: 1px solid #f4f6f8; font-size: 0.88rem;
        }
        .hist-tbl tr:last-child td { border-bottom: none; }
        .hist-tbl tr:hover td { background: #fafcff; }
        .hist-tbl .amt-cell { color: #27ae60; font-weight: 700; font-size: 0.95rem; }
        .hist-tbl .total-row td {
            background: #f0fbf5 !important; font-weight: 700;
            border-top: 2px solid #b2e8c9; color: #1a3a5c; font-size: 0.92rem;
        }
        .hist-tbl .total-row .amt-cell { color: #27ae60; font-size: 1rem; }

        /* Method pill badges */
        .mpill {
            display: inline-flex; align-items: center; gap: 5px;
            padding: 4px 11px; border-radius: 20px; font-size: 0.78rem; font-weight: 700;
            white-space: nowrap;
        }
        .mpill-cash    { background: #eafaf1; color: #1e8449; }
        .mpill-card    { background: #ebf5fb; color: #1a6a9a; }
        .mpill-bank    { background: #fef5ec; color: #ca6f1e; }
        .mpill-cheque  { background: #f5eef8; color: #7d3c98; }
        .mpill-default { background: #f0f4f8; color: #555; }

        /* ═══════════════════════════════════════════════════════
           RECENT TRANSACTIONS
        ═══════════════════════════════════════════════════════ */
        .txn-card {
            background: #fff; border-radius: 14px;
            box-shadow: 0 2px 14px rgba(0,0,0,0.07); overflow: hidden;
        }
        .txn-head {
            padding: 16px 24px; border-bottom: 1px solid #f0f4f8;
            display: flex; align-items: center; justify-content: space-between;
        }
        .txn-head .tt { font-size: 1rem; font-weight: 700; color: #1a3a5c; }
        .txn-head a {
            font-size: 0.82rem; color: #2471a3; text-decoration: none; font-weight: 600;
        }
        .txn-head a:hover { text-decoration: underline; }

        .txn-tbl { width: 100%; border-collapse: collapse; }
        .txn-tbl th {
            background: #f7fafd; color: #7a8fa6; padding: 10px 18px;
            font-size: 0.76rem; text-transform: uppercase; letter-spacing: 0.6px;
            font-weight: 700; text-align: left; border-bottom: 1px solid #eef2f7;
        }
        .txn-tbl td {
            padding: 12px 18px; border-bottom: 1px solid #f4f6f8; font-size: 0.87rem;
        }
        .txn-tbl tr:hover td { background: #fafcff; }
        .txn-tbl .res-link {
            color: #2471a3; text-decoration: none; font-weight: 700;
            font-family: 'Courier New', monospace; font-size: 0.83rem;
        }
        .txn-tbl .res-link:hover { text-decoration: underline; }
        .txn-tbl .amt-cell { color: #27ae60; font-weight: 700; }

        .txn-empty {
            padding: 44px; text-align: center; color: #ccc; font-size: 0.95rem;
        }
        .txn-empty .te-icon { font-size: 2.8rem; display: block; margin-bottom: 10px; opacity: 0.35; }

        @media(max-width:600px) {
            .pmf-kpis { grid-template-columns: 1fr 1fr; }
            .hist-tbl .hide-sm, .txn-tbl .hide-sm { display: none; }
        }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>
<div class="container">

    <%-- ── Page Header ──────────────────────────────────────────────── --%>
    <div class="pmf-breadcrumb">
        <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
        &rsaquo; Payments
    </div>
    <div class="pmf-page-head">
        <h1>
            <span class="pmf-title-icon">&#128176;</span>
            Payment Management
        </h1>
        <div style="display:flex;gap:8px;flex-wrap:wrap">
            <a href="${pageContext.request.contextPath}/billing" class="btn btn-secondary btn-sm">&#128196; All Bills</a>
            <a href="${pageContext.request.contextPath}/reports" class="btn btn-secondary btn-sm">&#128202; Reports</a>
        </div>
    </div>

    <%-- ── KPI Cards ────────────────────────────────────────────────── --%>
    <div class="pmf-kpis">
        <div class="kpi-card kpi-green">
            <div class="kpi-icon-box">&#128176;</div>
            <div class="kpi-body">
                <div class="kpi-label">Total Revenue</div>
                <div class="kpi-value">$<fmt:formatNumber value="${totalRevenue}" pattern="#,##0.00"/></div>
                <div class="kpi-sub">All recorded payments</div>
            </div>
        </div>
        <div class="kpi-card kpi-blue">
            <div class="kpi-icon-box">&#128203;</div>
            <div class="kpi-body">
                <div class="kpi-label">Transactions</div>
                <div class="kpi-value">${totalPaymentCount}</div>
                <div class="kpi-sub">Payments recorded</div>
            </div>
        </div>
        <div class="kpi-card kpi-orange">
            <div class="kpi-icon-box">&#9200;</div>
            <div class="kpi-body">
                <div class="kpi-label">Pending Bills</div>
                <div class="kpi-value">${pendingBillsCount}</div>
                <div class="kpi-sub">
                    <a href="${pageContext.request.contextPath}/billing?filter=unpaid"
                       style="color:#e67e22;text-decoration:none;font-weight:600">View unpaid &rarr;</a>
                </div>
            </div>
        </div>
    </div>

    <%-- ── Alerts ──────────────────────────────────────────────────── --%>
    <c:if test="${not empty success}">
        <div class="alert alert-success">&#10003; ${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-error">&#9888; ${error}</div>
    </c:if>
    <c:if test="${not empty errors}">
        <div class="alert alert-error">
            <ul style="margin:0;padding-left:20px">
                <c:forEach var="e" items="${errors}"><li>${e}</li></c:forEach>
            </ul>
        </div>
    </c:if>

    <%-- ── Search Card ─────────────────────────────────────────────── --%>
    <div class="pmf-search-card">
        <form method="get" action="${pageContext.request.contextPath}/payment">
            <div class="pmf-search-row">
                <div class="pmf-search-field">
                    <span class="si">&#128269;</span>
                    <input type="text" name="resNo"
                           placeholder="Enter Reservation Number  (e.g. OVR1234567890)"
                           value="${param.resNo}" autocomplete="off">
                </div>
                <button type="submit" class="btn btn-primary" style="padding:12px 22px;border-radius:10px;font-weight:700">
                    Find Bill
                </button>
            </div>
            <div class="pmf-quick-links">
                <a href="${pageContext.request.contextPath}/billing">&#128196; All Bills</a>
                <a href="${pageContext.request.contextPath}/billing?filter=unpaid">&#9888; Unpaid</a>
                <a href="${pageContext.request.contextPath}/reservations">&#128197; Reservations</a>
                <a href="${pageContext.request.contextPath}/guests">&#128101; Guests</a>
            </div>
        </form>
    </div>

    <%-- ════════════════════════════════════════════════════════════════
         BILL + PAYMENT SECTION  (only when bill is loaded)
    ════════════════════════════════════════════════════════════════ --%>
    <c:if test="${not empty bill}">

        <div class="pmf-bill-layout">

            <%-- ── Left: Invoice Receipt ───────────────────────────── --%>
            <div class="invoice-receipt">
                <div class="inv-head">
                    <div class="hotel">&#127965; Ocean View Resort</div>
                    <div class="inv-no">
                        Invoice #${bill.billId}
                        <c:if test="${not empty reservation}">&bull; ${reservation.reservationNo}</c:if>
                    </div>
                    <c:if test="${not empty reservation}">
                        <div class="inv-status">${reservation.status}</div>
                    </c:if>
                </div>

                <c:if test="${not empty reservation}">
                    <div class="inv-rows">
                        <div class="inv-row">
                            <span class="rl">&#128100; Guest</span>
                            <span class="rv"><strong>${reservation.guestName}</strong></span>
                        </div>
                        <c:if test="${not empty reservation.guestPhone}">
                        <div class="inv-row">
                            <span class="rl">&#128222; Phone</span>
                            <span class="rv">${reservation.guestPhone}</span>
                        </div>
                        </c:if>
                        <div class="inv-row">
                            <span class="rl">&#127968; Room</span>
                            <span class="rv">
                                ${reservation.roomNumber}
                                <c:if test="${not empty reservation.roomTypeName}">
                                    &mdash; ${reservation.roomTypeName}
                                </c:if>
                            </span>
                        </div>
                        <div class="inv-row">
                            <span class="rl">&#128197; Check-In</span>
                            <span class="rv">${reservation.checkIn}</span>
                        </div>
                        <div class="inv-row">
                            <span class="rl">&#128197; Check-Out</span>
                            <span class="rv">${reservation.checkOut}</span>
                        </div>
                        <div class="inv-row">
                            <span class="rl">&#128336; Duration</span>
                            <span class="rv">${reservation.nights} night(s)</span>
                        </div>
                    </div>
                </c:if>

                <hr class="inv-sep">

                <div class="inv-totals">
                    <c:set var="subtotal" value="${bill.totalAmount - bill.taxAmount}"/>
                    <div class="inv-total-row">
                        <span>Room Charge</span>
                        <span class="itv">$<fmt:formatNumber value="${subtotal}" pattern="#,##0.00"/></span>
                    </div>
                    <div class="inv-total-row">
                        <span>Tax (10%)</span>
                        <span class="itv">$<fmt:formatNumber value="${bill.taxAmount}" pattern="#,##0.00"/></span>
                    </div>
                    <c:if test="${bill.discount > 0}">
                    <div class="inv-total-row">
                        <span>Discount</span>
                        <span class="itv" style="color:#e74c3c">
                            &minus;$<fmt:formatNumber value="${bill.discount}" pattern="#,##0.00"/>
                        </span>
                    </div>
                    </c:if>
                    <div class="inv-total-row divider">
                        <span>Total Amount</span>
                        <span class="itv">$<fmt:formatNumber value="${bill.totalAmount}" pattern="#,##0.00"/></span>
                    </div>
                    <div class="inv-total-row paid-row">
                        <span>Amount Paid</span>
                        <span class="itv">
                            $<fmt:formatNumber value="${bill.totalAmount - bill.balanceDue}" pattern="#,##0.00"/>
                        </span>
                    </div>
                    <div class="inv-total-row ${bill.balanceDue <= 0 ? 'bal-settled' : 'bal-row'}">
                        <span>Balance Due</span>
                        <span class="itv">
                            $<fmt:formatNumber value="${bill.balanceDue}" pattern="#,##0.00"/>
                        </span>
                    </div>
                </div>

                <div class="inv-actions">
                    <a href="${pageContext.request.contextPath}/billing?action=view&id=${bill.billId}"
                       class="btn btn-sm btn-secondary">&#128196; Full Invoice</a>
                    <c:if test="${not empty reservation}">
                        <a href="${pageContext.request.contextPath}/reservations?action=view&id=${reservation.reservationId}"
                           class="btn btn-sm btn-secondary">&#128203; Reservation</a>
                    </c:if>
                </div>
            </div>

            <%-- ── Right: Payment Panel ─────────────────────────────── --%>
            <c:choose>

                <%-- ── Unpaid: show payment form ─── --%>
                <c:when test="${bill.balanceDue > 0}">
                    <div class="pay-panel">
                        <div class="pay-panel-top">
                            <span class="ppt-title">&#128179; Record Payment</span>
                            <span class="ppt-sub">Bill #${bill.billId}</span>
                        </div>
                        <div class="pay-panel-body">

                            <%-- Balance hero --%>
                            <div class="bal-hero">
                                <div class="bh-lbl">Balance Due</div>
                                <div class="bh-amt">
                                    $<fmt:formatNumber value="${bill.balanceDue}" pattern="#,##0.00"/>
                                </div>
                                <div class="bh-note">
                                    Total $<fmt:formatNumber value="${bill.totalAmount}" pattern="#,##0.00"/>
                                    &nbsp;&bull;&nbsp; Paid $<fmt:formatNumber value="${bill.totalAmount - bill.balanceDue}" pattern="#,##0.00"/>
                                </div>
                            </div>

                            <%-- Payment progress --%>
                            <c:if test="${bill.totalAmount > 0}">
                                <c:set var="paidPct" value="${(bill.totalAmount - bill.balanceDue) / bill.totalAmount * 100}"/>
                                <div class="pay-prog">
                                    <div class="prog-labels">
                                        <span>Payment Progress</span>
                                        <span class="prog-pct">
                                            <fmt:formatNumber value="${paidPct}" maxFractionDigits="0"/>% Paid
                                        </span>
                                    </div>
                                    <div class="prog-track">
                                        <div class="prog-fill" style="width:${paidPct}%"></div>
                                    </div>
                                </div>
                            </c:if>

                            <form method="post" action="${pageContext.request.contextPath}/payment"
                                  id="payForm" onsubmit="return validatePayForm()">
                                <input type="hidden" name="billId" value="${bill.billId}">

                                <%-- Step 1: Payment Method --%>
                                <div style="margin-bottom:22px">
                                    <div class="step-lbl">
                                        <span class="step-num">1</span>
                                        Select Payment Method
                                    </div>
                                    <div class="mth-grid" id="methodGrid">
                                        <c:forEach var="m" items="${paymentMethods}">
                                            <label class="mth-card ${param.methodId == m.id ? 'active' : ''}"
                                                   id="mb-${m.id}" onclick="selectMethod('${m.id}')">
                                                <input type="radio" name="methodId" value="${m.id}"
                                                       ${param.methodId == m.id ? 'checked' : ''} required>
                                                <span class="mth-icon">
                                                    <c:choose>
                                                        <c:when test="${m.name == 'Cash'}">&#128181;</c:when>
                                                        <c:when test="${fn:containsIgnoreCase(m.name,'credit')}">&#128179;</c:when>
                                                        <c:when test="${fn:containsIgnoreCase(m.name,'debit')}">&#128179;</c:when>
                                                        <c:when test="${fn:containsIgnoreCase(m.name,'bank') || fn:containsIgnoreCase(m.name,'transfer')}">&#127970;</c:when>
                                                        <c:when test="${fn:containsIgnoreCase(m.name,'cheque') || fn:containsIgnoreCase(m.name,'check')}">&#128196;</c:when>
                                                        <c:when test="${fn:containsIgnoreCase(m.name,'online') || fn:containsIgnoreCase(m.name,'mobile')}">&#128241;</c:when>
                                                        <c:otherwise>&#128176;</c:otherwise>
                                                    </c:choose>
                                                </span>
                                                <span class="mth-name">${m.name}</span>
                                            </label>
                                        </c:forEach>
                                    </div>
                                    <div class="mth-err" id="methodErr">
                                        &#9888; Please select a payment method.
                                    </div>
                                </div>

                                <%-- Step 2: Amount --%>
                                <div style="margin-bottom:22px">
                                    <div class="step-lbl">
                                        <span class="step-num">2</span>
                                        Payment Amount
                                    </div>
                                    <div class="amt-field">
                                        <span class="amt-currency">$</span>
                                        <input type="number" name="amount" id="amountInput"
                                               step="0.01" min="0.01" max="${bill.balanceDue}"
                                               value="${bill.balanceDue}" required>
                                    </div>
                                    <div class="amt-presets">
                                        <button type="button" class="amt-preset"
                                                onclick="setAmt(${bill.balanceDue})">
                                            Full Amount
                                        </button>
                                        <button type="button" class="amt-preset"
                                                onclick="setAmt((${bill.balanceDue} * 0.5).toFixed(2)">
                                            50%
                                        </button>
                                        <button type="button" class="amt-preset"
                                                onclick="setAmt((${bill.balanceDue} * 0.25).toFixed(2)">
                                            25%
                                        </button>
                                    </div>
                                </div>

                                <%-- Step 3: Reference --%>
                                <div style="margin-bottom:8px">
                                    <div class="step-lbl">
                                        <span class="step-num">3</span>
                                        Reference / Transaction No
                                        <small>(optional)</small>
                                    </div>
                                    <input type="text" name="referenceNo" class="ref-input"
                                           placeholder="Bank ref, card last 4 digits, cheque no..."
                                           value="${param.referenceNo}">
                                </div>

                                <button type="submit" class="pay-btn">
                                    &#128176;&nbsp; Confirm &amp; Record Payment
                                </button>
                            </form>
                        </div>
                    </div>
                </c:when>

                <%-- ── Fully paid: show receipt confirmation ─── --%>
                <c:otherwise>
                    <div class="pay-panel">
                        <div class="paid-receipt">
                            <div class="paid-circle">&#10003;</div>
                            <div class="paid-title">Payment Complete!</div>
                            <div class="paid-sub">
                                All charges for this reservation have been settled.
                            </div>
                            <div class="paid-amount">
                                $<fmt:formatNumber value="${bill.totalAmount}" pattern="#,##0.00"/>
                            </div>
                            <div class="paid-amount-lbl">Total Amount Paid</div>
                            <div style="display:flex;gap:10px;flex-wrap:wrap;justify-content:center">
                                <a href="${pageContext.request.contextPath}/billing?action=view&id=${bill.billId}"
                                   class="btn btn-secondary">&#128196; View Invoice</a>
                                <c:if test="${not empty reservation}">
                                    <a href="${pageContext.request.contextPath}/reservations?action=view&id=${reservation.reservationId}"
                                       class="btn btn-primary">View Reservation</a>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <%-- ── Payment History for this bill ─────────────────────────── --%>
        <c:if test="${not empty payments}">
            <div class="history-card">
                <div class="history-head">
                    <span class="ht">
                        &#128200; Payment History
                        <c:if test="${not empty reservation}">
                            <span style="font-size:0.82rem;font-weight:400;color:#bbb">
                                &mdash; ${reservation.reservationNo}
                            </span>
                        </c:if>
                    </span>
                    <span class="hc">${payments.size()} transaction(s)</span>
                </div>
                <table class="hist-tbl">
                    <tr>
                        <th>#</th>
                        <th>Date &amp; Time</th>
                        <th>Method</th>
                        <th>Amount</th>
                        <th class="hide-sm">Reference</th>
                        <th class="hide-sm">Recorded By</th>
                    </tr>
                    <c:set var="totalPaid" value="0"/>
                    <c:forEach var="p" items="${payments}" varStatus="s">
                        <c:set var="totalPaid" value="${totalPaid + p.amount}"/>
                        <tr>
                            <td style="color:#ccc;font-size:0.82rem">${s.count}</td>
                            <td style="color:#555;white-space:nowrap">
                                <fmt:formatDate value="${p.paymentDate}" pattern="dd MMM yyyy, HH:mm"/>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${p.methodName == 'Cash'}">
                                        <span class="mpill mpill-cash">&#128181; ${p.methodName}</span>
                                    </c:when>
                                    <c:when test="${fn:containsIgnoreCase(p.methodName,'credit') || fn:containsIgnoreCase(p.methodName,'debit')}">
                                        <span class="mpill mpill-card">&#128179; ${p.methodName}</span>
                                    </c:when>
                                    <c:when test="${fn:containsIgnoreCase(p.methodName,'bank') || fn:containsIgnoreCase(p.methodName,'transfer')}">
                                        <span class="mpill mpill-bank">&#127970; ${p.methodName}</span>
                                    </c:when>
                                    <c:when test="${fn:containsIgnoreCase(p.methodName,'cheque') || fn:containsIgnoreCase(p.methodName,'check')}">
                                        <span class="mpill mpill-cheque">&#128196; ${p.methodName}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="mpill mpill-default">&#128176; ${p.methodName}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="amt-cell">
                                $<fmt:formatNumber value="${p.amount}" pattern="#,##0.00"/>
                            </td>
                            <td class="hide-sm" style="color:#777">
                                ${empty p.referenceNo ? '—' : p.referenceNo}
                            </td>
                            <td class="hide-sm" style="color:#999">
                                ${empty p.recordedByName ? '—' : p.recordedByName}
                            </td>
                        </tr>
                    </c:forEach>
                    <tr class="total-row">
                        <td colspan="3" style="text-align:right;color:#666;padding:12px 18px">
                            Total Paid:
                        </td>
                        <td class="amt-cell">
                            $<fmt:formatNumber value="${totalPaid}" pattern="#,##0.00"/>
                        </td>
                        <td colspan="2" class="hide-sm"></td>
                    </tr>
                </table>
            </div>
        </c:if>

    </c:if><%-- /bill section --%>

    <%-- ════════════════════════════════════════════════════════════════
         RECENT TRANSACTIONS  (always visible)
    ════════════════════════════════════════════════════════════════ --%>
    <div class="txn-card">
        <div class="txn-head">
            <span class="tt">
                &#128338; Recent Transactions
                <span style="font-size:0.78rem;font-weight:400;color:#ccc">(last 20)</span>
            </span>
            <a href="${pageContext.request.contextPath}/reports">Full Report &rarr;</a>
        </div>
        <c:choose>
            <c:when test="${empty allRecentPayments}">
                <div class="txn-empty">
                    <span class="te-icon">&#128176;</span>
                    No payments have been recorded yet.
                </div>
            </c:when>
            <c:otherwise>
                <table class="txn-tbl">
                    <tr>
                        <th>Date &amp; Time</th>
                        <th>Reservation</th>
                        <th class="hide-sm">Guest</th>
                        <th>Method</th>
                        <th>Amount</th>
                        <th class="hide-sm">Reference</th>
                        <th class="hide-sm">By</th>
                    </tr>
                    <c:forEach var="p" items="${allRecentPayments}">
                        <tr>
                            <td style="color:#666;white-space:nowrap">
                                <fmt:formatDate value="${p.paymentDate}" pattern="dd MMM yy, HH:mm"/>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty p.reservationNo}">
                                        <a href="${pageContext.request.contextPath}/payment?resNo=${p.reservationNo}"
                                           class="res-link">${p.reservationNo}</a>
                                    </c:when>
                                    <c:otherwise><span style="color:#ddd">—</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="hide-sm" style="color:#555">
                                ${empty p.guestName ? '—' : p.guestName}
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${p.methodName == 'Cash'}">
                                        <span class="mpill mpill-cash">&#128181; ${p.methodName}</span>
                                    </c:when>
                                    <c:when test="${fn:containsIgnoreCase(p.methodName,'credit') || fn:containsIgnoreCase(p.methodName,'debit')}">
                                        <span class="mpill mpill-card">&#128179; ${p.methodName}</span>
                                    </c:when>
                                    <c:when test="${fn:containsIgnoreCase(p.methodName,'bank') || fn:containsIgnoreCase(p.methodName,'transfer')}">
                                        <span class="mpill mpill-bank">&#127970; ${p.methodName}</span>
                                    </c:when>
                                    <c:when test="${fn:containsIgnoreCase(p.methodName,'cheque') || fn:containsIgnoreCase(p.methodName,'check')}">
                                        <span class="mpill mpill-cheque">&#128196; ${p.methodName}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="mpill mpill-default">&#128176; ${p.methodName}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="amt-cell">
                                $<fmt:formatNumber value="${p.amount}" pattern="#,##0.00"/>
                            </td>
                            <td class="hide-sm" style="color:#888;font-size:0.82rem">
                                ${empty p.referenceNo ? '—' : p.referenceNo}
                            </td>
                            <td class="hide-sm" style="color:#aaa;font-size:0.82rem">
                                ${empty p.recordedByName ? '—' : p.recordedByName}
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

</div><%-- /container --%>

<script>
    function selectMethod(id) {
        document.querySelectorAll('.mth-card').forEach(function(b) {
            b.classList.remove('active');
        });
        var lbl = document.getElementById('mb-' + id);
        if (lbl) lbl.classList.add('active');
        document.getElementById('methodErr').style.display = 'none';
    }

    function setAmt(val) {
        var inp = document.getElementById('amountInput');
        if (inp) { inp.value = parseFloat(val).toFixed(2); inp.focus(); }
    }

    function validatePayForm() {
        var checked = document.querySelector('input[name="methodId"]:checked');
        if (!checked) {
            var err = document.getElementById('methodErr');
            err.style.display = 'block';
            document.getElementById('methodGrid').scrollIntoView({behavior:'smooth', block:'center'});
            return false;
        }
        var amt = parseFloat(document.getElementById('amountInput').value);
        if (!amt || amt <= 0) {
            alert('Please enter a valid payment amount.');
            return false;
        }
        return true;
    }

    // Highlight pre-selected method on page load (e.g. after validation error)
    document.addEventListener('DOMContentLoaded', function() {
        var checked = document.querySelector('input[name="methodId"]:checked');
        if (checked) {
            var lbl = checked.closest('.mth-card');
            if (lbl) lbl.classList.add('active');
        }
    });
</script>
</body>
</html>
