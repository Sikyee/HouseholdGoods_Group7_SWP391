<%-- 
    Document   : managePromotion
    Created on : Jun 15, 2025, 9:19:03 AM
    Author     : TriTM
--%>

<%@ page import="Model.Promotion" %>
<%@ page import="Model.Utils" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="left-sidebar.jsp" %>

<%
    List<Promotion> list = (List<Promotion>) request.getAttribute("list");
    List<Promotion> deletedList = (List<Promotion>) request.getAttribute("deletedList");
    Promotion edit = (Promotion) request.getAttribute("promotion");
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Manage Promotions</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <style>
            body {
                font-family: Arial, sans-serif;
            }

            /*            .content-top, .content-bottom {
                            width: 100%;
                            
                        }
            
                        .content-top {
                            background-color: #666666;  Trắng 
                            min-height: 10vh;
                        }
            
                        .content-bottom {
                            background-color: #f2f2f2;  Xám nhạt 
                            min-height: 50vh;
                        }*/


            table {
                width: 100%;
                border-collapse: collapse;
                margin-bottom: 20px;
            }

            th, td {
                border: 1px solid #ccc;
                padding: 8px;
                text-align: center;
            }

            th {
                background-color: #f2f2f2;
            }

            .add-button, .deleted-button {
                margin: 10px 10px 10px 0;
                padding: 8px 16px;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                color: white;
            }

            .add-button {
                background-color: #4CAF50;
            }

            .add-button:hover {
                background-color: #45a049;
            }

            .deleted-button {
                background-color: #f57c00;
            }

            .deleted-button:hover {
                background-color: #ef6c00;
            }

            .btn-action {
                padding: 6px 12px;
                border-radius: 8px;
                font-size: 14px;
                font-weight: bold;
                margin: 2px;
                text-decoration: none;
                display: inline-block;
            }

            .btn-action.edit {
                background-color: #4CAF50;
                padding: 8px 18px;
                color: white;
            }

            .btn-action.delete {
                background-color: #f44336;
                padding: 8px 10px;
                color: white;
            }

            .btn-action.reactivate {
                background-color: #2196F3;
                color: white;
            }

            .modal {
                display: none;
                position: fixed;
                z-index: 999;
                left: 0;
                top: 50px;
                width: 100%;
                height: 100%;
                background-color: rgba(0,0,0,0.4);
            }

            .modal-content {
                background-color: #cccccc;
                position: absolute;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                padding: 20px;
                border-radius: 8px;
                width: 90%;
                max-width: 600px;
            }

            .close {
                float: right;
                font-size: 24px;
                font-weight: bold;
                color: #aaa;
            }

            .close:hover {
                color: black;
                cursor: pointer;
            }

            .form-row {
                margin: 10px 0;
            }

            .form-row label {
                display: inline-block;
                width: 130px;
            }

            .submit-row {
                text-align: center;
            }

            #alertPopup {
                display: none;
                background: #ffdddd;
                color: #990000;
                padding: 10px;
                border: 1px solid #ff0000;
                position: fixed;
                top: 60px;
                left: 50%;
                transform: translateX(-50%);
                z-index: 1000;
                border-radius: 6px;
            }

            #searchInput {
                width: 250px;
                padding: 8px;
                margin-bottom: 10px;
            }

            .zebra:nth-child(even) {
                background-color: #e6f7ff;
            }

            .disabled-button {
                background-color: #a0c4ff !important;  /* Xanh dương nhạt */
                color: white !important;
                cursor: not-allowed !important;
                pointer-events: none;
                border: 1px solid #90b8f0;
                opacity: 1 !important; /* Giữ rõ */
            }

            .description-cell {
                max-width: 200px;         /* hoặc điều chỉnh theo mong muốn */
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }

            footer {
                padding-left: 260px !important;
            }


        </style>
    </head>

    <body>

        <div class="main-content" style="padding-left: 260px; padding-right: 20px; padding-top: 20px ">
            <div class="content-top">
                <!-- Alert -->
                <div id="alertPopup">Voucher with this code already exists and is active. Please edit or delete it first.</div>

                <h4><i class="fa-solid fa-tags"></i> Promotion List</h4>

                <input type="text" id="searchInput" placeholder="Search by code..." onkeyup="filterPromotions()" />
                <form action="Promotion" method="get" style="display:inline;">
                    <input type="hidden" name="action" value="prepareAdd" />
                    <button type="submit" class="add-button">Add Promotion</button>
                </form>


                <button class="deleted-button" onclick="openDeletedModal()">View Deleted Promotions</button>
            </div>

            <div class="content-bottom">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th><th>Code</th><th>Description</th><th>Type</th><th>Value</th><th>Start</th><th>End</th>
                            <th>Min Order</th><th>Max Use</th><th>Used</th><th>Status</th><th>Action</th>
                        </tr>
                    </thead>
                    <tbody id="promotionTable">
                        <% for (Promotion p : list) {%>
                        <tr class="zebra">
                            <td><%= p.getPromotionID()%></td>
                            <td><%= p.getCode()%></td>
                            <td class="description-cell" title="<%= p.getDescription()%>">
                                <%= p.getDescription()%>
                            </td>
                            <td><%= p.getDiscountType()%></td>
                            <td><%= p.getDiscountValue()%></td>
                            <td><%= p.getStartDate()%></td>
                            <td><%= p.getEndDate()%></td>
                            <td><%= p.getMinOrderValue()%></td>
                            <td><%= p.getMaxUsage()%></td>
                            <td><%= p.getUsedCount()%></td>
                            <td><%= Utils.getStatus(p.getEndDate())%></td>
                            <td>
                                <a class="btn-action edit" href="Promotion?action=edit&id=<%= p.getPromotionID()%>">Edit</a>
                                <a class="btn-action delete" href="Promotion?action=delete&id=<%= p.getPromotionID()%>" onclick="return confirm('Are you sure?');">Delete</a>

                                <a c
                                   </td>
                        </tr>
                        <% }%>
                    </tbody>
                </table>
            </div>

            <!-- Include modals (outside container to center properly) -->
            <!-- Add/Edit Modal -->
            <div id="addPromotionModal" class="modal">
                <div class="modal-content">
                    <span class="close" onclick="closeModal()">×</span>
                    <h2><%= (edit != null) ? "Edit Promotion" : "Create Promotion"%></h2>

                    <form id="promotionForm" action="Promotion" method="post">

                        <% if (edit != null) {%>
                        <input type="hidden" name="promotionID" value="<%= edit.getPromotionID()%>" />
                        <% }%>
                        <div class="form-row">
                            <label>Code:</label>
                            <input type="text" name="code" value="<%= (edit != null) ? edit.getCode() : ""%>" required />
                        </div>
                        <div class="form-row">
                            <label>Description:</label>
                            <input type="text" name="description" value="<%= (edit != null) ? edit.getDescription() : ""%>" required />
                        </div>
                        <div class="form-row">
                            <label>Discount Type:</label>
                            <select name="discountType">
                                <option value="percentage" <%= (edit != null && "percentage".equals(edit.getDiscountType())) ? "selected" : ""%>>Percentage</option>
                                <option value="fixed" <%= (edit != null && "fixed".equals(edit.getDiscountType())) ? "selected" : ""%>>Fixed</option>
                            </select>
                        </div>
                        <div class="form-row">
                            <label>Discount Value:</label>
                            <input type="number" name="discountValue" value="<%= (edit != null) ? edit.getDiscountValue() : ""%>" required />
                        </div>
                        <div class="form-row">
                            <label>Start Date:</label>
                            <input type="date" name="startDate" value="<%= (edit != null) ? edit.getStartDate() : ""%>" required />
                        </div>
                        <div class="form-row">
                            <label>End Date:</label>
                            <input type="date" name="endDate" value="<%= (edit != null) ? edit.getEndDate() : ""%>" required />
                        </div>
                        <div class="form-row">
                            <label>Min Order Value:</label>
                            <input type="number" name="minOrderValue" value="<%= (edit != null) ? edit.getMinOrderValue() : "0"%>" required />
                        </div>
                        <div class="form-row">
                            <label>Max Usage:</label>
                            <input type="number" name="maxUsage" value="<%= (edit != null) ? edit.getMaxUsage() : "1"%>" required />
                        </div>
                        <div class="form-row">
                            <label>Used Count:</label>
                            <input type="number" name="usedCount" value="<%= (edit != null) ? edit.getUsedCount() : "0"%>" required />
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
                        <!-- Trường hợp sửa, luôn giữ active -->
                        <input type="hidden" name="isActive" value="true" />
                        <% }%>

                        <div class="form-row submit-row">
                            <input id="submitBtn" type="submit" value="<%= (edit != null) ? "Update" : "Add"%> Promotion" />
                        </div>
                    </form>
                </div>
            </div>

            <!-- Deleted Modal -->
            <div id="deletedModal" class="modal">
                <div class="modal-content">
                    <span class="close" onclick="closeDeletedModal()">×</span>
                    <h3>Deleted Promotions</h3>
                    <table>
                        <thead>
                            <tr><th>ID</th><th>Code</th><th>Desc</th><th>Action</th></tr>
                        </thead>
                        <tbody>
                            <% for (Promotion d : deletedList) {%>
                            <tr>
                                <td><%= d.getPromotionID()%></td>
                                <td><%= d.getCode()%></td>
                                <td><%= d.getDescription()%></td>
                                <td>

                                    <a class="btn-action reactivate" href="Promotion?action=reactivate&id=<%= d.getPromotionID()%>">Reactivate</a>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Scripts -->
        <script>
            function openModal() {
                document.getElementById("addPromotionModal").style.display = "block";
            }

            function closeModal() {
                document.getElementById("addPromotionModal").style.display = "none";
            }

            function openDeletedModal() {
                document.getElementById("deletedModal").style.display = "block";
            }

            function closeDeletedModal() {
                document.getElementById("deletedModal").style.display = "none";
            }

            window.onclick = function (event) {
                if (event.target === document.getElementById("addPromotionModal"))
                    closeModal();
                if (event.target === document.getElementById("deletedModal"))
                    closeDeletedModal();
            };

            function filterPromotions() {
                let input = document.getElementById("searchInput").value.toLowerCase();
                let rows = document.querySelectorAll("#promotionTable tr");

                let visibleIndex = 0;
                rows.forEach(row => {
                    let code = row.cells[1].textContent.toLowerCase();
                    if (code.includes(input)) {
                        row.style.display = "";
                        row.style.backgroundColor = (visibleIndex % 2 === 0) ? "#ffffff" : "#e6f7ff";
                        visibleIndex++;
                    } else {
                        row.style.display = "none";
                    }
                });
            }
            <%-- Handle alerts and auto modal open --%>
            <% if (request.getAttribute("codeExists") != null) { %>
            window.onload = function () {
                openModal();
                document.getElementById("alertPopup").style.display = "block";
                setTimeout(() => {
                    document.getElementById("alertPopup").style.display = "none";
                }, 4000);
                filterPromotions();
            };
            <% } else if (request.getAttribute("showModal") != null) { %>
            window.onload = function () {
                openModal();
                filterPromotions();
            };
            <% } %>

            <% if (request.getAttribute("reactivate") != null) { %>
            if (confirm("This voucher code already exists but is inactive. Do you want to reactivate it?")) {
                openModal();
            }
            <% }%>
        </script>
        <script>
            const form = document.querySelector("#addPromotionModal form");
            const submitBtn = form.querySelector("input[type=submit]");
            let originalData = new FormData(form);

            function checkFormChanges() {
                const currentData = new FormData(form);
                let changed = false;
                for (let [key, value] of currentData.entries()) {
                    if (value !== originalData.get(key)) {
                        changed = true;
                        break;
                    }
                }

                if (changed) {
                    submitBtn.disabled = false;
                    submitBtn.classList.remove("disabled-button");
                } else {
                    submitBtn.disabled = true;
                    submitBtn.classList.add("disabled-button");
                }
            }

            form.querySelectorAll("input, select, textarea").forEach(field => {
                field.addEventListener("input", checkFormChanges);
            });

            window.addEventListener("load", () => {
                checkFormChanges();
            });

        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

        <%@ include file="footer.jsp" %>
    </body>
</html>
