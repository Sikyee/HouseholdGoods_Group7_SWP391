<%-- 
    Document   : manageVoucher
    Created on : Jun 15, 2025, 9:19:03 AM
    Author     : TriTM
--%>

<%@ page import="Model.Voucher" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<Voucher> list = (List<Voucher>) request.getAttribute("list");
    Voucher edit = (Voucher) request.getAttribute("voucher");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Manage Vouchers</title>
    <style>
        table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: center; }
        form { display: flex; flex-wrap: wrap; gap: 10px; }
        label { width: 150px; }
        input, select { flex: 1; padding: 5px; }
        .form-row { width: 100%; display: flex; align-items: center; }
        .submit-row { margin-top: 10px; }
    </style>
</head>
<body>
<h2>Voucher List</h2>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Code</th>
        <th>Description</th>
        <th>Type</th>
        <th>Value</th>
        <th>Start</th>
        <th>End</th>
        <th>Min Order</th>
        <th>Max Use</th>
        <th>Used</th>
        <th>Active</th>
        <th>Action</th>
    </tr>
    </thead>
    <tbody>
    <%
        if (list != null) {
            for (Voucher v : list) {
    %>
        <tr>
            <td><%= v.getVoucherID() %></td>
            <td><%= v.getCode() %></td>
            <td><%= v.getDescription() %></td>
            <td><%= v.getDiscountType() %></td>
            <td><%= v.getDiscountValue() %></td>
            <td><%= v.getStartDate() %></td>
            <td><%= v.getEndDate() %></td>
            <td><%= v.getMinOrderValue() %></td>
            <td><%= v.getMaxUsage() %></td>
            <td><%= v.getUsedCount() %></td>
            <td><%= v.isIsActive() %></td>
            <td>
                <a href="VoucherController?action=edit&id=<%=v.getVoucherID()%>">Edit</a> |
                <a href="VoucherController?action=delete&id=<%=v.getVoucherID()%>"
                   onclick="return confirm('Are you sure?');">Delete</a>
            </td>
        </tr>
    <%
            }
        } else {
    %>
        <tr>
            <td colspan="12">No voucher data available.</td>
        </tr>
    <%
        }
    %>
    </tbody>
</table>

<h2><%= (edit != null) ? "Edit Voucher" : "Add Voucher" %></h2>
<form action="VoucherController" method="post">
    <% if (edit != null) { %>
        <input type="hidden" name="voucherID" value="<%=edit.getVoucherID()%>" />
    <% } %>
    <div class="form-row">
        <label>Code:</label>
        <input type="text" name="code" value="<%= (edit != null) ? edit.getCode() : "" %>" required />
    </div>
    <div class="form-row">
        <label>Description:</label>
        <input type="text" name="description" value="<%= (edit != null) ? edit.getDescription() : "" %>" required />
    </div>
    <div class="form-row">
        <label>Discount Type:</label>
        <select name="discountType">
            <option value="percentage" <%= (edit != null && edit.getDiscountType().equals("percentage")) ? "selected" : "" %>>Percentage</option>
            <option value="fixed" <%= (edit != null && edit.getDiscountType().equals("fixed")) ? "selected" : "" %>>Fixed</option>
        </select>
    </div>
    <div class="form-row">
        <label>Discount Value:</label>
        <input type="number" name="discountValue" value="<%= (edit != null) ? edit.getDiscountValue() : "" %>" required />
    </div>
    <div class="form-row">
        <label>Start Date:</label>
        <input type="date" name="startDate" value="<%= (edit != null) ? edit.getStartDate() : "" %>" required />
    </div>
    <div class="form-row">
        <label>End Date:</label>
        <input type="date" name="endDate" value="<%= (edit != null) ? edit.getEndDate() : "" %>" required />
    </div>
    <div class="form-row">
        <label>Min Order Value:</label>
        <input type="number" name="minOrderValue" value="<%= (edit != null) ? edit.getMinOrderValue() : "0" %>" required />
    </div>
    <div class="form-row">
        <label>Max Usage:</label>
        <input type="number" name="maxUsage" value="<%= (edit != null) ? edit.getMaxUsage() : "1" %>" required />
    </div>
    <div class="form-row">
        <label>Used Count:</label>
        <input type="number" name="usedCount" value="<%= (edit != null) ? edit.getUsedCount() : "0" %>" required />
    </div>
    <div class="form-row">
        <label>Is Active:</label>
        <select name="isActive">
            <option value="true" <%= (edit != null && edit.isIsActive()) ? "selected" : "" %>>True</option>
            <option value="false" <%= (edit != null && !edit.isIsActive()) ? "selected" : "" %>>False</option>
        </select>
    </div>
    <div class="form-row submit-row">
        <input type="submit" value="<%= (edit != null) ? "Update" : "Add" %> Voucher" />
    </div>
</form>
</body>
</html>
