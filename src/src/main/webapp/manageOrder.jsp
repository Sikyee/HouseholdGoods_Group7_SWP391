<%-- 
    Document   : manageOrder
    Created on : Jul 12, 2025, 8:14:05 PM
    Author     : TriTM
--%>
<%@ page import="Model.OrderInfo" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="left-sidebar.jsp" %>

<%
    List<OrderInfo> orders = (List<OrderInfo>) request.getAttribute("orders");
    String context = request.getContextPath();
%>

<div class="content" style="margin-left: 220px; padding: 2rem;">

<!DOCTYPE html>
<html>
<head>
    <title>Manage Orders</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>
<body class="bg-light">
<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="text-primary">Order Management</h2>
        <form class="d-flex" action="Order" method="get">
            <input type="hidden" name="action" value="search">
            <input class="form-control me-2" type="text" name="keyword" placeholder="Search by ID or Address">
            <button class="btn btn-outline-primary" type="submit">Search</button>
            <a href="Order?action=list" class="btn btn-outline-success ms-2">Show Active</a>
            <a href="Order?action=trash" class="btn btn-outline-secondary ms-2">Show Deleted</a>
        </form>
    </div>

    <table class="table table-bordered table-hover bg-white">
        <thead class="table-primary">
        <tr>
            <th>ID</th>
            <th>UserID</th>
            <th>Status</th>
            <th>Date</th>
            <th>Payment</th>
            <th>Voucher</th>
            <th>Total</th>
            <th>Final</th>
            <th>Full Name</th>
            <th>Address</th>
            <th>Phone</th>
            <th>Deleted?</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <%
            if (orders != null && !orders.isEmpty()) {
                for (OrderInfo o : orders) {
                    boolean isDeleted = o.isIsDeleted();
        %>
        <tr class="<%= isDeleted ? "table-danger" : "" %>">
            <td><%= o.getOrderID() %></td>
            <td><%= o.getUserID() %></td>
            <td>
                <%
                    int status = o.getOrderStatusID();
                    String statusText = "Unknown";
                    switch (status) {
                        case 1: statusText = "Pending"; break;
                        case 2: statusText = "Processing"; break;
                        case 3: statusText = "Shipped"; break;
                        case 4: statusText = "Completed"; break;
                        case 5: statusText = "Canceled"; break;
                    }
                %>
                <span class="badge bg-info text-dark"><%= statusText %></span>
            </td>
            <td><%= o.getOrderDate() %></td>
            <td><%= o.getPaymentMethodID() %></td>
            <td><%= o.getVoucherID() == 0 ? "None" : o.getVoucherID() %></td>
            <td>$<%= String.format("%.2f", o.getTotalPrice()) %></td>
            <td>$<%= String.format("%.2f", o.getFinalPrice()) %></td>
            <td><%= o.getFullName() %></td>
            <td><%= o.getDeliveryAddress() %></td>
            <td><%= o.getPhone() %></td>
            <td><%= isDeleted ? "Yes" : "No" %></td>
            <td>
                <a href="Order?action=view&id=<%= o.getOrderID() %>" class="btn btn-sm btn-primary mb-1">View</a>

                <% if (!isDeleted) { %>
                    <a href="Order?action=cancel&id=<%= o.getOrderID() %>" class="btn btn-sm btn-warning mb-1"
                       onclick="return confirm('Cancel this order?')">Cancel</a>
                    <a href="Order?action=delete&id=<%= o.getOrderID() %>" class="btn btn-sm btn-danger mb-1"
                       onclick="return confirm('Delete this order?')">Delete</a>
                <% } else { %>
                    <a href="Order?action=restore&id=<%= o.getOrderID() %>" class="btn btn-sm btn-success mb-1"
                       onclick="return confirm('Restore this order?')">Restore</a>
                <% } %>
            </td>
        </tr>
        <%
                }
            } else {
        %>
        <tr>
            <td colspan="13" class="text-center text-muted">No orders found.</td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>
</div>
</body>
</html>
