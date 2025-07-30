<%-- 
    Document   : orderDetail
    Created on : Jul 12, 2025, 8:14:51 PM
    Author     : TriTM
--%>
<%@ page import="Model.OrderInfo, Model.OrderDetail" %>
<%@ page import="java.util.List" %>
<%@ include file="header.jsp" %>

<%
    OrderInfo o = (OrderInfo) request.getAttribute("order");
    List<OrderDetail> details = (List<OrderDetail>) request.getAttribute("details");
    String context = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Order Detail</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="text-primary">Order Detail</h2>
        <a href="Order?action=list" class="btn btn-secondary">? Back</a>
    </div>

    <% if (o != null) { %>
    <div class="card mb-4">
        <div class="card-body">
            <h5 class="card-title">Order Information</h5>
            <p><strong>Order ID:</strong> <%= o.getOrderID() %></p>
            <p><strong>Date:</strong> <%= o.getOrderDate() %></p>
            <p><strong>Total Price:</strong> $<%= String.format("%.2f", o.getTotalPrice()) %></p>
            <p><strong>Final Price:</strong> $<%= String.format("%.2f", o.getFinalPrice()) %></p>
            <p><strong>Shipping Address:</strong> <%= o.getDeliveryAddress() %></p>
            <p><strong>Full Name:</strong> <%= o.getFullName() %></p>
            <p><strong>Phone:</strong> <%= o.getPhone() %></p>
            <p><strong>Status:</strong>
                <span class="badge bg-info text-dark">
                   <%
    int status = o.getOrderStatusID();
    String statusText = "Unknown";
    switch (status) {
        case 1:
            statusText = "Pending";
            break;
        case 2:
            statusText = "Processing";
            break;
        case 3:
            statusText = "Shipped";
            break;
        case 4:
            statusText = "Completed";
            break;
        case 5:
            statusText = "Canceled";
            break;
    }
%>

                    <%= statusText %>
                </span>
            </p>

            <form action="Order" method="get" class="row g-2 align-items-center">
                <input type="hidden" name="action" value="updateStatus">
                <input type="hidden" name="id" value="<%= o.getOrderID() %>">
                <div class="col-auto">
                    <label for="status" class="form-label mb-0">Change Status:</label>
                </div>
                <div class="col-auto">
                    <select name="status" id="status" class="form-select">
                        <option value="1">Pending</option>
                        <option value="2">Processing</option>
                        <option value="3">Shipped</option>
                        <option value="4">Completed</option>
                        <option value="5">Canceled</option>
                    </select>
                </div>
                <div class="col-auto">
                    <button type="submit" class="btn btn-primary">Update</button>
                </div>
            </form>
        </div>
    </div>

    <h4>Order Items</h4>
    <table class="table table-bordered table-hover bg-white">
        <thead class="table-primary">
        <tr>
            <th>OrderDetail ID</th>
            <th>Product ID</th>
            <th>Order Name</th>
            <th>Quantity</th>
            <th>Total Price</th>
        </tr>
        </thead>
        <tbody>
        <% if (details != null && !details.isEmpty()) {
            for (OrderDetail d : details) { %>
                <tr>
                    <td><%= d.getOrderDetailID() %></td>
                    <td><%= d.getProductID() %></td>
                    <td><%= d.getOrderName() %></td>
                    <td><%= d.getQuantity() %></td>
                    <td>$<%= String.format("%.2f", d.getTotalPrice()) %></td>
                </tr>
        <%  } 
           } else { %>
            <tr><td colspan="5" class="text-center text-muted">No order details found.</td></tr>
        <% } %>
        </tbody>
    </table>

    <% } else { %>
        <div class="alert alert-danger">Order not found!</div>
    <% } %>
</div>
</body>
</html>
