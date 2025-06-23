<%-- 
    Document   : managePromotion
    Created on : Jun 15, 2025, 9:19:03 AM
    Author     : TriTM
--%>

<%@ page import="Model.Promotion" %>
<%@ page import="Model.Utils" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

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
                padding-top: 80px;
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
                color: white;
            }

            .btn-action.delete {
                background-color: #f44336;
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
                top: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0,0,0,0.4);
            }

            .modal-content {
                background-color: #fff;
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
                top: 20px;
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
        </style>
    </head>

    <body>
        <%@ include file="header.jsp" %>

        <div class="main-content" style="padding-left: 20px; padding-right: 20px;">
            <div class="content-top">
                <!-- Alert -->
                <div id="alertPopup">Voucher with this code already exists and is active. Please edit or delete it first.</div>

                <h2>Promotion List</h2>

                <input type="text" id="searchInput" placeholder="Search by code..." onkeyup="filterPromotions()" />

                <button class="add-button" onclick="openModal()">Add Promotion</button>
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
                        <tr>
                            <td><%= p.getPromotionID()%></td>
                            <td><%= p.getCode()%></td>
                            <td><%= p.getDescription()%></td>
                            <td><%= p.getDiscountType()%></td>
                            <td><%= p.getDiscountValue()%></td>
                            <td><%= p.getStartDate()%></td>
                            <td><%= p.getEndDate()%></td>
                            <td><%= p.getMinOrderValue()%></td>
                            <td><%= p.getMaxUsage()%></td>
                            <td><%= p.getUsedCount()%></td>
                            <td><%= Utils.getStatus(p.getEndDate())%></td>
                            <td>
                                <a class="btn-action edit" href="PromotionController?action=edit&id=<%= p.getPromotionID()%>">Edit</a>
                                <a class="btn-action delete" href="PromotionController?action=delete&id=<%= p.getPromotionID()%>" onclick="return confirm('Are you sure?');">Delete</a>
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
                    <form action="PromotionController" method="post">
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
                        <div class="form-row">
                            <label>Is Active:</label>
                            <select name="isActive">
                                <option value="true" <%= (edit != null && edit.isIsActive()) ? "selected" : ""%>>True</option>
                                <option value="false" <%= (edit != null && !edit.isIsActive()) ? "selected" : ""%>>False</option>
                            </select>
                        </div>
                        <div class="form-row submit-row">
                            <input type="submit" value="<%= (edit != null) ? "Update" : "Add"%> Promotion" />
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
                                    <a class="btn-action reactivate" href="PromotionController?action=reactivate&id=<%= d.getPromotionID()%>">Reactivate</a>
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
                        row.classList.remove("zebra");
                        if (visibleIndex % 2 === 1) {
                            row.classList.add("zebra");
                        }
                        visibleIndex++;
                    } else {
                        row.style.display = "none";
                        row.classList.remove("zebra");
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

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

        <%@ include file="footer.jsp" %>
    </body>
</html>
