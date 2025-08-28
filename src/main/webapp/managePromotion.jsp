<%-- Document: managePromotion --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="left-sidebar.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
    Integer currentPage = (Integer) request.getAttribute("page");
    Integer totalPages  = (Integer) request.getAttribute("totalPages");
    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>Manage Promotions</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        :root{ --bg:#0b1220; --card:#ffffff; --muted:#64748b; --ring:#2563eb; --danger:#b00020; --brand:#0ea5e9; --brand-2:#22c55e; }
        body { font-family: Inter, system-ui, Arial, sans-serif; }
        table { width: 100%; border-collapse: collapse; margin-bottom: 12px; }
        th, td { border: 1px solid #e5e7eb; padding: 10px; text-align: center; }
        th { background: #f8fafc; font-weight: 600; color: #334155; }
        .zebra:nth-child(even) { background-color: #f8fafc; }
        .description-cell { max-width: 240px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
        .add-button, .deleted-button {
            margin: 10px 10px 10px 0; padding: 10px 16px; border: none; border-radius: 10px;
            cursor: pointer; color: white; font-weight: 700; box-shadow: 0 6px 18px rgba(2,6,23,.08);
        }
        .add-button { background: linear-gradient(135deg,var(--brand),#38bdf8); }
        .add-button:hover { filter: brightness(1.05); }
        .deleted-button { background: linear-gradient(135deg,#f59e0b,#f97316); }
        .deleted-button:hover { filter: brightness(1.05); }
        .btn-action {
            padding: 8px 12px; border-radius: 10px; font-size: 14px; font-weight: 700; margin: 2px;
            text-decoration: none; display: inline-block; box-shadow: 0 3px 10px rgba(2,6,23,.06);
        }
        .btn-action.edit { background: #22c55e; color: #fff; }
        .btn-action.delete { background: #ef4444; color: #fff; }
        .btn-action.reactivate { background: #3b82f6; color: #fff; }
        #searchInput{ width:280px; padding:10px 12px; border-radius:10px; border:1px solid #d0d7de; }
        .modal{ display:none; position:fixed; z-index:999; inset:0; background: rgba(2,6,23,.55); backdrop-filter: blur(3px); }
        .modal-content{
            background: var(--card); position:absolute; top:50%; left:50%;
            transform: translate(-50%, -50%) scale(.96);
            padding: 24px; border-radius: 16px; width: 92%; max-width: 720px;
            border: 1px solid #e5e7eb; box-shadow: 0 24px 60px rgba(2,6,23,.18);
            animation: modalIn .18s ease-out forwards;
        }
        @keyframes modalIn { to{ transform: translate(-50%, -50%) scale(1); } }
        .close{ float:right; font-size:24px; font-weight:800; color:#94a3b8; line-height:1; padding:2px 8px; border-radius:8px; }
        .close:hover{ color:#0f172a; background:#f1f5f9; cursor:pointer; }
        .form-row{ display:grid; grid-template-columns: 170px 1fr; gap:12px; align-items:center; margin:12px 0; }
        .form-row label{ font-weight:600; color:#334155; text-align:right; }
        @media (max-width: 640px){ .form-row{ grid-template-columns:1fr; } .form-row label{ text-align:left; } }
        .form-row input[type="text"], .form-row input[type="number"], .form-row input[type="date"], .form-row select, .form-row textarea{
            width:100%; padding:10px 12px; border-radius:10px; border:1px solid #d0d7de; background:#fff; color:#0f172a; outline:none;
            transition:border-color .15s, box-shadow .15s, background .15s;
        }
        .form-row input:hover, .form-row select:hover, .form-row textarea:hover{ border-color:#bac3cc; }
        .form-row input:focus, .form-row select:focus, .form-row textarea:focus{
            border-color: var(--ring); box-shadow: 0 0 0 3px rgba(37,99,235,.15);
        }
        .unit-wrap{ display:flex; align-items:center; gap:8px; }
        .unit-badge{ user-select:none; min-width:36px; text-align:center; padding:8px 10px; border-radius:10px; background:#f1f5f9; color:#0f172a; font-weight:700; border:1px solid #e5e7eb; }
        .submit-row{ display:flex; justify-content:flex-end; gap:8px; margin-top:6px; }
        #saveBtn{
            appearance:none; border:none; border-radius:12px; padding:12px 18px; font-weight:800; letter-spacing:.2px;
            background: linear-gradient(135deg,var(--brand-2),#16a34a); color:#fff; box-shadow:0 10px 22px rgba(34,197,94,.22);
            transition: transform .06s ease, filter .15s ease; cursor:pointer;
        }
        #saveBtn:hover{ filter: brightness(1.05); }
        #saveBtn:active{ transform: translateY(1px); }
        .pagination{ display:flex; flex-wrap:wrap; gap:6px; align-items:center; justify-content:flex-end; margin:10px 0 0; }
        .page-btn{ border:1px solid #e5e7eb; background:#fff; padding:6px 10px; border-radius:8px; cursor:pointer; text-decoration:none; color:#0f172a; }
        .page-btn.disabled{ opacity:.5; pointer-events:none; }
        .page-btn.active{ background:#0ea5e9; color:#fff; border-color:#0ea5e9; }
        .main-content { padding-left: 260px; padding-right: 20px; padding-top: 20px; }
        .content-top { margin-bottom: 8px; }
        .badge-scheduled { background:#fde68a; border:1px solid #f59e0b; color:#92400e; padding:2px 8px; border-radius: 999px; font-weight:700; }
        .badge-inactive  { background:#f3f4f6; border:1px solid #d1d5db; color:#374151; padding:2px 8px; border-radius: 999px; font-weight:700; }
    </style>
</head>
<body>
<div class="main-content">
    <div class="content-top">
        <h4 class="mb-2"><i class="fa-solid fa-bullhorn"></i> Promotion List</h4>

        <!-- Alerts -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">${error}</div>
        </c:if>
        <c:if test="${codeExists}">
            <div class="alert alert-warning" role="alert">This code already exists and is active.</div>
        </c:if>
        <c:if test="${reactivate}">
            <div class="alert alert-info" role="alert">This code belongs to a disabled promotion. Do you want to activate it?</div>
        </c:if>

        <input type="text" id="searchInput" placeholder="Search by code..." onkeyup="filterPromotions()" />
        <a href="Promotion?action=prepareAdd&page=<%= currentPage %>" class="add-button text-decoration-none">Add Promotion</a>
    </div>

    <div class="content-bottom">
        <!-- Active -->
        <h6 class="fw-bold mb-2">Active Promotions</h6>
        <table>
            <thead>
            <tr>
                <th>ID</th><th>Code</th><th>Title</th><th>Description</th>
                <th>Discount</th><th>Period</th><th>Min Price</th><th>Brand</th><th>Action</th>
            </tr>
            </thead>
            <tbody id="promotionTable">
            <c:forEach var="p" items="${list}">
                <tr class="zebra">
                    <td>${p.promotionID}</td>
                    <td>${p.code}</td>
                    <td>${p.title}</td>
                    <td class="description-cell" title="${fn:escapeXml(p.description)}"><c:out value="${p.description}"/></td>
                    <td>${p.discountType} - ${p.discountValue}</td>
                    <td>
                        <fmt:formatDate value="${p.startDate}" pattern="yyyy-MM-dd"/> →
                        <fmt:formatDate value="${p.endDate}" pattern="yyyy-MM-dd"/>
                    </td>
                    <td>${p.minOrderValue}</td>
                    <td>${p.brandName}</td>
                    <td>
                        <a href="Promotion?action=edit&id=${p.promotionID}&page=<%= currentPage %>" class="btn-action edit">Edit</a>
                        <a href="Promotion?action=deactivate&id=${p.promotionID}&page=<%= currentPage %>"
                           class="btn-action delete btn-deactivate"
                           data-id="${p.promotionID}">Deactivate</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <!-- Pagination (server-side) -->
        <div class="pagination">
            <a class="page-btn <%= (currentPage <= 1 ? "disabled" : "") %>" href="Promotion?action=list&page=<%= (currentPage-1) %>">Prev</a>
            <%
                int range = 2;
                int start = Math.max(1, currentPage - range);
                int end = Math.min(totalPages, currentPage + range);
                if (currentPage <= range) end = Math.min(totalPages, 1 + range * 2);
                if (currentPage + range > totalPages) start = Math.max(1, totalPages - range * 2);
                for (int i = start; i <= end; i++) {
            %>
                <a class="page-btn <%= (i == currentPage ? "active" : "") %>" href="Promotion?action=list&page=<%= i %>"><%= i %></a>
            <% } %>
            <a class="page-btn <%= (currentPage >= totalPages ? "disabled" : "") %>"
               href="Promotion?action=list&page=<%= (currentPage + 1) %>">Next</a>
        </div>

        <!-- Deactive (Inactive or Scheduled) -->
        <h6 class="fw-bold mt-4 mb-2">Deactive Promotions</h6>
        <table>
            <thead>
            <tr>
                <th>ID</th><th>Code</th><th>Title</th><th>Description</th><th>Discount</th><th>Period</th><th>Status</th><th>Min Price</th><th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="p" items="${deactiveList}">
                <tr class="zebra">
                    <td>${p.promotionID}</td>
                    <td>${p.code}</td>
                    <td>${p.title}</td>
                    <td class="description-cell" title="${fn:escapeXml(p.description)}"><c:out value="${p.description}"/></td>
                    <td>${p.discountType} - ${p.discountValue}</td>
                    <td>
                        <fmt:formatDate value="${p.startDate}" pattern="yyyy-MM-dd"/> →
                        <fmt:formatDate value="${p.endDate}" pattern="yyyy-MM-dd"/>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${p.startDate != null && today != null && p.startDate.time > today.time}">
                                <span class="badge-scheduled">Scheduled</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge-inactive">Inactive</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>${p.minOrderValue}</td>
                    <td>
                        <a
                          href="Promotion?action=activate&id=${p.promotionID}&page=<%= currentPage %>"
                          class="btn-action reactivate btn-activate"
                          data-id="${p.promotionID}"
                          data-start="${p.startDate}"
                          data-scheduled="${p.startDate != null && today != null && p.startDate.time > today.time}"
                        >Activate</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <!-- Debug -->
        <small class="text-muted">
            page=${page}, totalPages=${totalPages}, totalItems=${totalItems},
            activeCount=${fn:length(list)}, deactiveCount=${fn:length(deactiveList)}
        </small>
    </div>
</div>

<!-- Modal Add/Edit -->
<c:if test="${showModal}">
    <div id="promotionModal" class="modal" style="display:block" aria-hidden="false" aria-labelledby="promotionModalTitle">
        <div class="modal-content">
            <span class="close" onclick="closeModal()" aria-label="Close">×</span>
            <h2 id="promotionModalTitle">
                <c:choose><c:when test="${promotion != null}">Edit Promotion</c:when><c:otherwise>Create Promotion</c:otherwise></c:choose>
            </h2>

            <form id="promotionForm" action="Promotion" method="post" autocomplete="off">
                <input type="hidden" name="page" value="<%= currentPage %>"/>

                <c:if test="${promotion != null}">
                    <input type="hidden" name="promotionID" value="${promotion.promotionID}"/>
                </c:if>

                <div class="form-row">
                    <label>Code:</label>
                    <input type="text" name="code" value="${promotion.code}" maxlength="64" required />
                </div>

                <div class="form-row">
                    <label>Title:</label>
                    <input type="text" name="title" value="${promotion.title}" maxlength="255" required />
                </div>

                <div class="form-row">
                    <label>Description:</label>
                    <textarea name="description" rows="2" maxlength="500">${promotion.description}</textarea>
                </div>

                <div class="form-row">
                    <label>Discount Type:</label>
                    <select name="discountType" id="discountType" required>
                        <option value="percentage" <c:if test="${promotion != null && promotion.discountType == 'percentage'}">selected</c:if>>Percentage</option>
                        <option value="fixed" <c:if test="${promotion != null && promotion.discountType == 'fixed'}">selected</c:if>>Fixed Amount</option>
                    </select>
                </div>

                <div class="form-row">
                    <label>Discount Value:</label>
                    <div class="unit-wrap">
                        <input type="number" name="discountValue" id="discountValue" value="${promotion.discountValue}" required/>
                        <span id="discountUnit" class="unit-badge">%</span>
                    </div>
                </div>

                <div class="form-row">
                    <label>Start Date:</label>
                    <input type="date" name="startDate" id="startDate"
                           value="<fmt:formatDate value='${promotion.startDate}' pattern='yyyy-MM-dd'/>" required/>
                </div>

                <div class="form-row">
                    <label>End Date:</label>
                    <input type="date" name="endDate" id="endDate"
                           value="<fmt:formatDate value='${promotion.endDate}' pattern='yyyy-MM-dd'/>" required/>
                </div>

                <div class="form-row" id="dateErrorRow" style="display:none;">
                    <label></label>
                    <div id="dateErrorMsg" style="color:#b00020; font-weight:700;">End date must be after start date.</div>
                </div>

                <div class="form-row">
                    <label>Min Price:</label>
                    <input type="number" name="minOrderValue" step="1" min="0" value="${promotion.minOrderValue}" required/>
                </div>

                <div class="form-row">
                    <label>Brand:</label>
                    <select name="brand" required>
                        <option value="">-- Brand --</option>
                        <c:forEach var="b" items="${brands}">
                            <option value="${b.brandID}" <c:if test="${promotion != null && promotion.brandID == b.brandID}">selected</c:if>>
                                ${b.brandName}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <c:choose>
                    <c:when test="${promotion != null}">
                        <input type="hidden" name="isActive" value="${promotion.isActive}"/>
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="isActive" value="true"/>
                    </c:otherwise>
                </c:choose>

                <div class="form-row submit-row">
                    <button id="saveBtn" type="submit">💾 Save</button>
                    <a href="Promotion?action=list&page=<%= currentPage %>" class="btn btn-outline-secondary">❌ Cancel</a>
                </div>
            </form>
        </div>
    </div>
</c:if>

<!-- Custom confirm modal: Activate (Scheduled) -->
<div id="scheduledConfirm" class="modal" style="display:none" aria-hidden="true">
  <div class="modal-content" role="dialog" aria-modal="true" aria-labelledby="scheduledConfirmTitle">
    <span class="close" id="scheduledClose" aria-label="Close">×</span>
    <h3 id="scheduledConfirmTitle" class="mb-2">Scheduled promotion</h3>
    <p class="mb-3">
      This promotion is scheduled to start on <b id="scheduledStart">—</b>.<br>
      Would you like to change the start date (open the Edit modal)?
    </p>
    <div class="d-flex gap-2 justify-content-end">
      <button type="button" class="btn btn-outline-secondary" id="keepScheduleBtn">Keep schedule</button>
      <button type="button" class="btn btn-primary" id="openEditBtn">Open Edit</button>
    </div>
  </div>
</div>

<!-- Custom confirm modal: Deactivate -->
<div id="deactivateConfirm" class="modal" style="display:none" aria-hidden="true">
  <div class="modal-content" role="dialog" aria-modal="true" aria-labelledby="deactivateConfirmTitle">
    <span class="close" id="deactivateClose" aria-label="Close">×</span>
    <h3 id="deactivateConfirmTitle" class="mb-2">Deactivate promotion</h3>
    <p class="mb-3">
      Are you sure you want to deactivate this promotion?
    </p>
    <div class="d-flex gap-2 justify-content-end">
      <button type="button" class="btn btn-outline-secondary" id="cancelDeactivateBtn">Cancel</button>
      <button type="button" class="btn btn-danger" id="confirmDeactivateBtn">Deactivate</button>
    </div>
  </div>
</div>

<script>
    function closeModal(){
        const m=document.getElementById('promotionModal');
        if(m) m.style.display='none';
        window.location.href='Promotion?action=list&page=<%= currentPage %>';
    }

    function filterPromotions(){
        const q = (document.getElementById('searchInput').value || '').toLowerCase();
        const rows = document.querySelectorAll('#promotionTable tr');
        let visibleIndex = 0;
        rows.forEach(tr=>{
            const code = (tr.cells[1]?.textContent || '').toLowerCase();
            const show = code.includes(q);
            tr.style.display = show ? '' : 'none';
            if(show){
                tr.style.backgroundColor = (visibleIndex % 2 === 0) ? '#ffffff' : '#f8fafc';
                visibleIndex++;
            }
        });
    }

    (function () {
        const startInput = document.getElementById("startDate");
        const endInput   = document.getElementById("endDate");
        const errorRow   = document.getElementById("dateErrorRow");

        function toDate(val){ return val ? new Date(val + "T00:00:00") : null; }
        function showError(show){
            if(!errorRow) return;
            errorRow.style.display = show ? "flex" : "none";
            if (endInput) endInput.classList.toggle("is-invalid", !!show);
        }
        function syncEndMin(){
            if (startInput && startInput.value) endInput.min = startInput.value;
            else endInput.removeAttribute("min");
        }
        function validateDates(){
            const s = toDate(startInput?.value);
            const e = toDate(endInput?.value);
            if (s && e && e < s) showError(true); else showError(false);
        }
        if(startInput && endInput){
            startInput.addEventListener("input", ()=>{ syncEndMin(); validateDates(); });
            endInput.addEventListener("input", validateDates);
            window.addEventListener("load", ()=>{ syncEndMin(); validateDates(); });
        }
    })();

    // 👉 Client-side rules aligned with server
    (function(){
        const typeSel = document.getElementById('discountType');
        const valInp  = document.getElementById('discountValue');
        const unitEl  = document.getElementById('discountUnit');
        if(!typeSel || !valInp || !unitEl) return;

        function clamp(val, min, max){
            if (val === "" || isNaN(val)) return "";
            val = Number(val);
            if (min != null && val < min) return min;
            if (max != null && val > max) return max;
            return val;
        }

        function applyRules(){
            const t = typeSel.value;
            if (t === "percentage") {
                unitEl.textContent = "%";
                valInp.min = "1";
                valInp.max = "100";
                valInp.step = "1";
                valInp.placeholder = "1–100";
                valInp.title = "Percentage (1–100)";
                valInp.value = clamp(valInp.value, 1, 100);
            } else { // fixed
                unitEl.textContent = "₫";
                valInp.min = "1000";
                valInp.removeAttribute("max");
                valInp.step = "1000";
                valInp.placeholder = "≥ 1000 (VND)";
                valInp.title = "Fixed amount (>= 1000 VND)";
                const v = clamp(valInp.value, 1000, null);
                if (v !== "") valInp.value = v;
            }
        }

        typeSel.addEventListener('change', applyRules);
        valInp.addEventListener('input', applyRules);
        window.addEventListener('load', applyRules);
    })();

    // 👉 Custom modals
    (function() {
      function fmt(d) {
        if (!d) return '';
        try {
          const dt = new Date(d);
          const m = String(dt.getMonth() + 1).padStart(2, '0');
          const day = String(dt.getDate()).padStart(2, '0');
          return dt.getFullYear() + '-' + m + '-' + day;
        } catch (_) { return d; }
      }

      // Activate (Scheduled) modal
      var scheduledModal = document.getElementById('scheduledConfirm');
      var scheduledStartEl = document.getElementById('scheduledStart');
      var btnOpenEdit = document.getElementById('openEditBtn');
      var btnKeep = document.getElementById('keepScheduleBtn');
      var btnScheduledClose = document.getElementById('scheduledClose');
      var pendingActivateId = null;

      function openScheduledConfirm(id, start) {
        pendingActivateId = id;
        scheduledStartEl.textContent = fmt(start);
        scheduledModal.style.display = 'block';
      }
      function closeScheduledConfirm() {
        scheduledModal.style.display = 'none';
        pendingActivateId = null;
      }

      btnOpenEdit.addEventListener('click', function() {
        if (pendingActivateId) {
          window.location.href = 'Promotion?action=edit&id=' + encodeURIComponent(pendingActivateId) +
                                 '&page=' + encodeURIComponent('<%= currentPage %>');
        }
      });
      btnKeep.addEventListener('click', closeScheduledConfirm);
      btnScheduledClose.addEventListener('click', closeScheduledConfirm);
      window.addEventListener('keydown', function(e){ if (e.key === 'Escape') closeScheduledConfirm(); });
      scheduledModal.addEventListener('click', function(e){ if (e.target === scheduledModal) closeScheduledConfirm(); });

      document.querySelectorAll('.btn-activate').forEach(function(btn) {
        btn.addEventListener('click', function(e) {
          var scheduled = String(this.dataset.scheduled) === 'true';
          if (!scheduled) return; // normal activate
          e.preventDefault();
          openScheduledConfirm(this.dataset.id, this.dataset.start);
        });
      });

      // Deactivate modal
      var deactivateModal = document.getElementById('deactivateConfirm');
      var btnDeactivateClose = document.getElementById('deactivateClose');
      var btnCancelDeactivate = document.getElementById('cancelDeactivateBtn');
      var btnConfirmDeactivate = document.getElementById('confirmDeactivateBtn');
      var pendingDeactivateId = null;

      function openDeactivateConfirm(id) {
        pendingDeactivateId = id;
        deactivateModal.style.display = 'block';
      }
      function closeDeactivateConfirm() {
        deactivateModal.style.display = 'none';
        pendingDeactivateId = null;
      }

      btnDeactivateClose.addEventListener('click', closeDeactivateConfirm);
      btnCancelDeactivate.addEventListener('click', closeDeactivateConfirm);
      btnConfirmDeactivate.addEventListener('click', function() {
        if (pendingDeactivateId) {
          window.location.href = 'Promotion?action=deactivate&id=' + encodeURIComponent(pendingDeactivateId) +
                                 '&page=' + encodeURIComponent('<%= currentPage %>');
        }
      });
      window.addEventListener('keydown', function(e){ if (e.key === 'Escape') closeDeactivateConfirm(); });
      deactivateModal.addEventListener('click', function(e){ if (e.target === deactivateModal) closeDeactivateConfirm(); });

      document.querySelectorAll('.btn-deactivate').forEach(function(btn) {
        btn.addEventListener('click', function(e) {
          e.preventDefault();
          openDeactivateConfirm(this.dataset.id);
        });
      });
    })();
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
