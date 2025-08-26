<%-- 
    Document   : manageVoucher
    Created on : Jun 15, 2025, 9:19:03 AM
    Author     : TriTM
--%>

<%@ page import="Model.Voucher" %>
<%@ page import="Model.Utils" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="left-sidebar.jsp" %>

<%
    List<Voucher> list = (List<Voucher>) request.getAttribute("list");
    List<Voucher> deletedList = (List<Voucher>) request.getAttribute("deletedList");
    Voucher edit = (Voucher) request.getAttribute("voucher");

    // ĐỔI TÊN: tránh trùng với implicit object 'page'
    Integer currentPage = (Integer) request.getAttribute("page");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    Integer totalItems = (Integer) request.getAttribute("totalItems");
    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Manage Vouchers</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <style>
            :root{
                --bg:#0b1220; --card:#ffffff; --muted:#64748b; --ring:#2563eb;
                --danger:#b00020; --brand:#0ea5e9; --brand-2:#22c55e;
            }

            body { font-family: Inter, system-ui, Arial, sans-serif; }

            /* Table */
            table { width: 100%; border-collapse: collapse; margin-bottom: 12px; }
            th, td { border: 1px solid #e5e7eb; padding: 10px; text-align: center; }
            th { background: #f8fafc; font-weight: 600; color: #334155; }

            /* Top actions */
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

            /* ===== Modal polish ===== */
            .modal{
                display:none; position:fixed; z-index:999; inset:0;
                background: rgba(2,6,23,.55); backdrop-filter: blur(3px);
            }
            .modal-content{
                background: var(--card); position:absolute; top:50%; left:50%;
                transform: translate(-50%, -50%) scale(.96);
                padding: 24px; border-radius: 16px; width: 92%; max-width: 720px;
                border: 1px solid #e5e7eb; box-shadow: 0 24px 60px rgba(2,6,23,.18);
                animation: modalIn .18s ease-out forwards;
            }
            @keyframes modalIn { to{ transform: translate(-50%, -50%) scale(1); } }

            .close{
                float:right; font-size:24px; font-weight:800; color:#94a3b8; line-height:1;
                padding:2px 8px; border-radius:8px;
            }
            .close:hover{ color:#0f172a; background:#f1f5f9; cursor:pointer; }

            /* Form layout inside modal: tidy grid */
            .form-row{ display:grid; grid-template-columns: 170px 1fr; gap:12px; align-items:center; margin:12px 0; }
            .form-row label{ font-weight:600; color:#334155; text-align:right; }
            @media (max-width: 640px){ .form-row{ grid-template-columns:1fr; } .form-row label{ text-align:left; } }

            .form-row input[type="text"],
            .form-row input[type="number"],
            .form-row input[type="date"],
            .form-row select, .form-row textarea{
                width:100%; padding:10px 12px; border-radius:10px; border:1px solid #d0d7de;
                background:#fff; color:#0f172a; outline:none;
                transition:border-color .15s, box-shadow .15s, background .15s;
            }
            .form-row input:hover, .form-row select:hover{ border-color:#bac3cc; }
            .form-row input:focus, .form-row select:focus{
                border-color: var(--ring);
                box-shadow: 0 0 0 3px rgba(37,99,235,.15);
            }
            input.is-invalid{
                border-color:#b00020 !important;
                box-shadow:0 0 0 3px rgba(176,0,32,.18)!important;
                outline: none;
            }

            /* Discount unit chip */
            .unit-wrap{ display:flex; align-items:center; gap:8px; }
            .unit-badge{
                user-select:none; min-width:36px; text-align:center; padding:8px 10px; border-radius:10px;
                background:#f1f5f9; color:#0f172a; font-weight:700; border:1px solid #e5e7eb;
            }

            #dateErrorRow{ display:none; }
            #dateErrorMsg{ color:#b00020; font-weight:700; }

            /* Submit aligned right */
            .submit-row{
                display:flex; justify-content:flex-end; gap:8px; margin-top:6px;
            }
            #submitBtn{
                appearance:none; border:none; border-radius:12px; padding:12px 18px; font-weight:800; letter-spacing:.2px;
                background: linear-gradient(135deg,var(--brand-2),#16a34a); color:#fff; box-shadow:0 10px 22px rgba(34,197,94,.22);
                transition: transform .06s ease, filter .15s ease; cursor:pointer;
            }
            #submitBtn:hover{ filter: brightness(1.05); }
            #submitBtn:active{ transform: translateY(1px); }
            .disabled-button{
                background:#cbd5e1!important; border:1px solid #cbd5e1!important; color:#fff!important; box-shadow:none!important;
                cursor:not-allowed!important;
            }

            /* Misc */
            #alertPopup {
                display: none; background: #ffdddd; color: #990000; padding: 10px; border: 1px solid #ff0000;
                position: fixed; top: 60px; left: 50%; transform: translateX(-50%); z-index: 1000; border-radius: 6px;
            }
            #searchInput{ width:280px; padding:10px 12px; border-radius:10px; border:1px solid #d0d7de; }
            .zebra:nth-child(even) { background-color: #f8fafc; }
            .description-cell { max-width: 240px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
            footer { padding-left: 260px !important; }
            .main-content { padding-left: 260px; padding-right: 20px; padding-top: 20px; }

            /* Pagination */
            .pagination{ display:flex; flex-wrap:wrap; gap:6px; align-items:center; justify-content:flex-end; margin:10px 0 0; }
            .page-btn{ border:1px solid #e5e7eb; background:#fff; padding:6px 10px; border-radius:8px; cursor:pointer; text-decoration:none; color:#0f172a; }
            .page-btn.disabled{ opacity:.5; pointer-events:none; }
            .page-btn.active{ background:#0ea5e9; color:#fff; border-color:#0ea5e9; }
        </style>
    </head>

    <body>
        <div class="main-content">
            <div class="content-top">
                <!-- Alert -->
                <div id="alertPopup">Voucher with this code already exists and is active. Please edit or delete it first.</div>

                <h4><i class="fa-solid fa-tags"></i> Voucher List</h4>

                <input type="text" id="searchInput" placeholder="Search by code..." onkeyup="filterVouchers()" />
                <form action="Voucher" method="get" style="display:inline;">
                    <input type="hidden" name="action" value="prepareAdd" />
                    <button type="submit" class="add-button">Add Voucher</button>
                </form>

                <button class="deleted-button" onclick="openDeletedModal()">View Deleted Vouchers</button>
            </div>

            <div class="content-bottom">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th><th>Code</th><th>Description</th><th>Type</th><th>Value</th><th>Start</th><th>End</th>
                            <th>Min Order</th><th>Max Use</th><th>Used</th><th>Status</th><th>Action</th>
                        </tr>
                    </thead>
                    <tbody id="voucherTable">
                        <% if (list != null) {
                           for (Voucher p : list) {
                               String endIso = Utils.toHtmlDate(p.getEndDate()); // ISO yyyy-MM-dd
                        %>
                        <tr class="zebra">
                            <td><%= p.getVoucherID()%></td>
                            <td><%= p.getCode()%></td>
                            <td class="description-cell" title="<%= p.getDescription() == null ? "" : p.getDescription()%>">
                                <%= p.getDescription() == null ? "" : p.getDescription()%>
                            </td>
                            <td><%= p.getDiscountType()%></td>
                            <td><%= p.getDiscountValue()%></td>
                            <td><%= Utils.toDisplayDate(p.getStartDate())%></td>
                            <td><%= Utils.toDisplayDate(p.getEndDate())%></td>
                            <td><%= p.getMinOrderValue()%></td>
                            <td><%= p.getMaxUsage()%></td>
                            <td><%= p.getUsedCount()%></td>
                            <td><%= Utils.getStatus(p.getEndDate())%></td>
                            <td>
                                <a class="btn-action edit" href="Voucher?action=edit&id=<%= p.getVoucherID()%>&page=<%= currentPage %>">Edit</a>
                                <%-- Delete button rules (ẩn nếu usedCount != 0): --%>
                                <% if (p.getUsedCount() == 0) { %>
                                    <a class="btn-action delete"
                                       href="Voucher?action=delete&id=<%= p.getVoucherID()%>"
                                       onclick="return handleDelete('<%= endIso %>');">Delete</a>
                                <% } %>
                            </td>
                        </tr>
                        <% } } %>
                    </tbody>
                </table>

                <!-- Pagination (server-side) -->
                <div class="pagination">
                    <a class="page-btn <%= (currentPage <= 1 ? "disabled" : "") %>" href="Voucher?action=list&page=<%= (currentPage-1) %>">Prev</a>

                    <%
                        int range = 2;
                        int start = Math.max(1, currentPage - range);
                        int end = Math.min(totalPages, currentPage + range);
                        if (currentPage <= range) end = Math.min(totalPages, 1 + range * 2);
                        if (currentPage + range > totalPages) start = Math.max(1, totalPages - range * 2);

                        for (int i = start; i <= end; i++) {
                    %>
                        <a class="page-btn <%= (i == currentPage ? "active" : "") %>" href="Voucher?action=list&page=<%= i %>"><%= i %></a>
                    <% } %>

                    <a class="page-btn <%= (currentPage >= totalPages ? "disabled" : "") %>" href="Voucher?action=list&page=<%= (currentPage+1) %>">Next</a>
                </div>
            </div>

            <!-- Add/Edit Modal -->
            <div id="addVoucherModal" class="modal" aria-hidden="true" aria-labelledby="voucherModalTitle">
                <div class="modal-content">
                    <span class="close" onclick="closeModal()" aria-label="Close">×</span>
                    <h2 id="voucherModalTitle"><%= (edit != null) ? "Edit Voucher" : "Create Voucher"%></h2>

                    <form id="voucherForm" action="Voucher" method="post" autocomplete="off">
                        <% if (edit != null) { %>
                        <input type="hidden" name="voucherID" value="<%= edit.getVoucherID()%>" />
                        <% } %>

                        <div class="form-row">
                            <label>Code:</label>
                            <input type="text" name="code" value="<%= (edit != null) ? edit.getCode() : ""%>" maxlength="64" required />
                        </div>

                        <div class="form-row">
                            <label>Description:</label>
                            <input type="text" name="description" 
                                   value="<%= (edit != null) ? (edit.getDescription() == null ? "" : edit.getDescription()) : ""%>" 
                                   maxlength="255" required />
                        </div>

                        <div class="form-row">
                            <label>Discount Type:</label>
                            <select name="discountType" id="discountType">
                                <option value="percentage" <%= (edit != null && "percentage".equals(edit.getDiscountType())) ? "selected" : ""%>>Percentage</option>
                                <option value="fixed" <%= (edit != null && "fixed".equals(edit.getDiscountType())) ? "selected" : ""%>>Fixed</option>
                            </select>
                        </div>

                        <div class="form-row">
                            <label>Discount Value:</label>
                            <div class="unit-wrap">
                                <input type="number" name="discountValue" id="discountValue"
                                       value="<%= (edit != null) ? edit.getDiscountValue() : ""%>" required />
                                <span id="discountUnit" class="unit-badge">%</span>
                            </div>
                        </div>

                        <div class="form-row">
                            <label>Start Date:</label>
                            <input id="startDate" type="date" name="startDate" value="<%= (edit != null) ? Utils.toHtmlDate(edit.getStartDate()) : ""%>" required />
                        </div>
                        <div class="form-row">
                            <label>End Date:</label>
                            <input id="endDate" type="date" name="endDate" value="<%= (edit != null) ? Utils.toHtmlDate(edit.getEndDate()) : ""%>" required />
                        </div>

                        <!-- Thông báo lỗi ngày -->
                        <div class="form-row" id="dateErrorRow" style="display:none;">
                            <label></label>
                            <div id="dateErrorMsg">End date must be after start date.</div>
                        </div>

                        <div class="form-row">
                            <label>Min Order Value:</label>
                            <input type="number" name="minOrderValue" step="1" min="0" value="<%= (edit != null) ? String.valueOf(edit.getMinOrderValue()) : "0"%>" required />
                        </div>
                        <div class="form-row">
                            <label>Max Usage:</label>
                            <input type="number" name="maxUsage" min="1" step="1" value="<%= (edit != null) ? String.valueOf(edit.getMaxUsage()) : "1"%>" required />
                        </div>

                        <div class="form-row">
                            <label>Used Count:</label>
                            <input type="number" name="usedCount" min="0" step="1"
                                   value="<%= (edit != null) ? String.valueOf(edit.getUsedCount()) : "0"%>"
                                   readonly />
                        </div>

                        <% if (edit == null) { %>
                        <!-- Trường hợp tạo mới -->
                        <div class="form-row">
                            <label>Is Active:</label>
                            <select name="isActive">
                                <option value="true">True</option>
                                <option value="false">False</option>
                            </select>
                        </div>
                        <% } else { %>
                        <!-- Trường hợp sửa, giữ active ở backend -->
                        <input type="hidden" name="isActive" value="true" />
                        <% } %>

                        <div class="form-row submit-row">
                            <input id="submitBtn" type="submit" value="<%= (edit != null) ? "Update" : "Add"%> Voucher" />
                        </div>
                    </form>
                </div>
            </div>

            <!-- Deleted Modal -->
            <div id="deletedModal" class="modal" aria-hidden="true" aria-labelledby="deletedModalTitle">
                <div class="modal-content">
                    <span class="close" onclick="closeDeletedModal()" aria-label="Close">×</span>
                    <h3 id="deletedModalTitle">Deleted Vouchers</h3>
                    <table>
                        <thead>
                            <tr><th>ID</th><th>Code</th><th>Desc</th><th>Action</th></tr>
                        </thead>
                        <tbody>
                            <% if (deletedList != null) {
                               for (Voucher d : deletedList) { %>
                            <tr>
                                <td><%= d.getVoucherID()%></td>
                                <td><%= d.getCode()%></td>
                                <td><%= d.getDescription() == null ? "" : d.getDescription()%></td>
                                <td>
                                    <a class="btn-action reactivate" href="Voucher?action=reactivate&id=<%= d.getVoucherID()%>">Reactivate</a>
                                </td>
                            </tr>
                            <% } } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Scripts -->
        <script>
            function openModal() { document.getElementById("addVoucherModal").style.display = "block"; }
            function closeModal() { document.getElementById("addVoucherModal").style.display = "none"; }
            function openDeletedModal() { document.getElementById("deletedModal").style.display = "block"; }
            function closeDeletedModal() { document.getElementById("deletedModal").style.display = "none"; }

            window.onclick = function (event) {
                if (event.target === document.getElementById("addVoucherModal")) closeModal();
                if (event.target === document.getElementById("deletedModal")) closeDeletedModal();
            };

            function filterVouchers() {
                // Lọc trong trang hiện tại (server đã phân trang)
                let input = document.getElementById("searchInput").value.toLowerCase();
                let rows = document.querySelectorAll("#voucherTable tr");
                let visibleIndex = 0;
                rows.forEach(row => {
                    let code = row.cells[1].textContent.toLowerCase();
                    if (code.includes(input)) {
                        row.style.display = "";
                        row.style.backgroundColor = (visibleIndex % 2 === 0) ? "#ffffff" : "#f8fafc";
                        visibleIndex++;
                    } else {
                        row.style.display = "none";
                    }
                });
            }

            // Delete guard: block delete when voucher not expired
            function handleDelete(endIso) {
                if (!endIso) return false;
                const end = new Date(endIso + "T00:00:00");
                const today = new Date();
                // if not expired
                if (today < new Date(end.getFullYear(), end.getMonth(), end.getDate() + 1)) {
                    alert("Voucher chưa hết hạn nên không thể xóa.");
                    return false;
                }
                return confirm("Bạn có chắc muốn xóa voucher này?");
            }

            <%-- Handle alerts and auto modal open --%>
            <% if (request.getAttribute("codeExists") != null) { %>
            window.onload = function () {
                openModal();
                document.getElementById("alertPopup").style.display = "block";
                setTimeout(() => {
                    document.getElementById("alertPopup").style.display = "none";
                }, 4000);
            };
            <% } else if (request.getAttribute("showModal") != null) { %>
            window.onload = function () { openModal(); };
            <% } %>

            <% if (request.getAttribute("reactivate") != null) { %>
            if (confirm("This voucher code already exists but is inactive. Do you want to reactivate it?")) {
                openModal();
            }
            <% } %>
        </script>

        <!-- Client-side date validation: End must be >= Start -->
        <script>
            (function () {
                const form = document.getElementById("voucherForm");
                if (!form) return;

                const startInput = document.getElementById("startDate");
                const endInput   = document.getElementById("endDate");
                const errorRow   = document.getElementById("dateErrorRow");
                const submitBtn  = document.getElementById("submitBtn");

                function toDate(val) { return val ? new Date(val + "T00:00:00") : null; }

                function showError(show) {
                    if (!errorRow) return;
                    errorRow.style.display = show ? "flex" : "none";
                    if (show) endInput.classList.add("is-invalid"); else endInput.classList.remove("is-invalid");
                    if (submitBtn && show) submitBtn.disabled = true;
                }

                function syncEndMin() {
                    if (startInput && startInput.value) endInput.min = startInput.value;
                    else endInput.removeAttribute("min");
                }

                function validateDates() {
                    const s = toDate(startInput.value);
                    const e = toDate(endInput.value);
                    if (s && e && e < s) showError(true); else showError(false);
                }

                startInput.addEventListener("change", () => { syncEndMin(); validateDates(); });
                endInput.addEventListener("change", validateDates);
                startInput.addEventListener("input", () => { syncEndMin(); validateDates(); });
                endInput.addEventListener("input", validateDates);

                window.addEventListener("load", () => { syncEndMin(); validateDates(); });

                form.addEventListener("submit", (ev) => {
                    const s = toDate(startInput.value);
                    const e = toDate(endInput.value);
                    if (s && e && e < s) { ev.preventDefault(); showError(true); }
                });
            })();
        </script>

        <!-- Discount Value constraints + unit badge -->
        <script>
            (function () {
                const typeSel = document.getElementById("discountType");
                const valInp  = document.getElementById("discountValue");
                const unitEl  = document.getElementById("discountUnit");
                if (!typeSel || !valInp || !unitEl) return;

                function clamp(val, min, max) {
                    if (val === "" || isNaN(val)) return "";
                    val = Number(val);
                    if (min != null && val < min) return min;
                    if (max != null && val > max) return max;
                    return val;
                }

                function applyRules() {
                    const t = typeSel.value;
                    if (t === "percentage") {
                        unitEl.textContent = "%";
                        valInp.min = "1";
                        valInp.max = "99";
                        valInp.step = "1";
                        valInp.placeholder = "1–99";
                        valInp.title = "Percentage (1–99)";
                        valInp.value = clamp(valInp.value, 1, 99);
                    } else {
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

                typeSel.addEventListener("change", applyRules);
                valInp.addEventListener("input", applyRules);
                window.addEventListener("load", applyRules);
            })();
        </script>

        <!-- Existing "disable submit unless changed" logic -->
        <script>
            const form2 = document.querySelector("#addVoucherModal form");
            if (form2) {
                const submitBtn2 = form2.querySelector("input[type=submit]");
                let originalData = new FormData(form2);

                function checkFormChanges() {
                    const currentData = new FormData(form2);
                    let changed = false;
                    for (let [key, value] of currentData.entries()) {
                        if (value !== originalData.get(key)) { changed = true; break; }
                    }
                    if (changed) {
                        submitBtn2.disabled = false;
                        submitBtn2.classList.remove("disabled-button");
                    } else {
                        submitBtn2.disabled = true;
                        submitBtn2.classList.add("disabled-button");
                    }
                }

                form2.querySelectorAll("input, select, textarea").forEach(field => {
                    field.addEventListener("input", checkFormChanges);
                });

                window.addEventListener("load", () => { checkFormChanges(); });
            }
        </script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
