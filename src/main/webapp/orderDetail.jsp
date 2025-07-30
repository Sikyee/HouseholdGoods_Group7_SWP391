<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, java.text.SimpleDateFormat, Model.*" %>
<%@include file="left-sidebar.jsp"  %>
<%
    OrderInfo order = (OrderInfo) request.getAttribute("order");
    List<OrderDetail> orderDetailList = (List<OrderDetail>) request.getAttribute("orderDetailList");
    CancelReason cancelReason = (CancelReason) request.getAttribute("cancelReason");
    List<OrderStatus> orderStatusList = (List<OrderStatus>) request.getAttribute("orderStatusList");
    User user = (User) session.getAttribute("user");

    int userRole = user != null ? user.getRoleID() : 4;
    int statusID = order.getOrderStatusID();
    boolean isPending = (statusID == 1);
    boolean isProcessing = (statusID == 2);
    boolean isDelivering = (statusID == 3);
    boolean isCanceled = (statusID == 6);
%>

<div class="content" style="margin-left: 220px; padding: 2rem;">
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Order Details</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body>
<div class="container mt-5">
    <h2 class="mb-4">Order Detail - #<%= order.getOrderID() %></h2>

    <div class="card mb-4">
        <div class="card-body">
            <p><strong>Order Date:</strong> <%= order.getOrderDate() %></p>
            <p><strong>Full Name:</strong> <%= order.getFullName() %></p>
            <p><strong>Phone:</strong> <%= order.getPhone() %></p>
            <p><strong>Address:</strong> <%= order.getDeliveryAddress() %></p>
            <p><strong>Order Status:</strong>
                <%
                    for (OrderStatus status : orderStatusList) {
                        if (status.getOrderStatusID() == order.getOrderStatusID()) {
                            out.print(status.getStatusName());
                            break;
                        }
                    }
                %>
            </p>

            <% if (isCanceled && cancelReason != null) { %>
                <div class="alert alert-danger mt-3">
                    <strong>Cancel Reason:</strong> <%= cancelReason.getReason() %>
                </div>
            <% } %>

            <% if (isPending && userRole == 3) { %>
                <form action="cancelOrder.jsp" method="get">
                    <input type="hidden" name="orderID" value="<%= order.getOrderID() %>"/>
                    <button type="submit" class="btn btn-danger">Cancel Order</button>
                </form>
            <% } %>

            <% if (userRole == 1 || userRole == 2) {
                boolean allowUpdate = isPending || isProcessing || isDelivering;
                if (allowUpdate) {
            %>
                <form action="update-order-status" method="post" class="mt-3">
                    <input type="hidden" name="orderID" value="<%= order.getOrderID() %>"/>
                    <div class="mb-3">
                        <label for="orderStatus" class="form-label">Update Status:</label>
                        <select name="orderStatusID" id="orderStatus" class="form-select" onchange="handleStatusChange()">
                            <%
                                for (OrderStatus status : orderStatusList) {
                                    int id = status.getOrderStatusID();
                                    boolean show = false;
                                    String label = status.getStatusName();

                                    if (isPending && (id == 2 || id == 6)) {
                                        show = true;
                                        if (id == 2) label = "Order confirmed (Moved to processing)";
                                        if (id == 6) label = "Order cancelled (Out of stock)";
                                    } else if (isProcessing && (id == 3 || id == 6)) {
                                        show = true;
                                        if (id == 3) label = "Moved to delivery";
                                        if (id == 6) label = "Order cancelled (Processing error)";
                                    } else if (isDelivering && (id == 4 || id == 5)) {
                                        show = true;
                                        if (id == 4) label = "Delivered successfully.";
                                        if (id == 5) label = "Customer returned the item.";
                                    }

                                    if (show) {
                            %>
                                <option value="<%= id %>" <%= (id == statusID) ? "selected" : "" %>><%= label %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>

                    <div class="mt-3" id="cancel-reason-box" style="display: none;">
                        <input type="hidden" name="cancelReason" value="Đã hết hàng"/>
                        <div class="alert alert-warning">Cancellation reason: Out of stock.</div>
                    </div>

                    <button type="submit" class="btn btn-success">Update</button>
                </form>
            <% } else { %>
                <div class="mt-3 text-muted">Unable to update the status of this order.</div>
            <% }} %>
        </div>
    </div>

    <h4>Products</h4>
    <div class="table-responsive">
        <table class="table table-bordered table-striped mt-3">
            <thead class="table-light">
                <tr>
                    <th>Product Name</th>
                    <th>Quantity</th>
                    <th>Total Price</th>
                </tr>
            </thead>
            <tbody>
                <% for (OrderDetail od : orderDetailList) { %>
                    <tr>
                        <td><%= od.getOrderName() %></td>
                        <td><%= od.getQuantity() %></td>
                        <td><%= od.getTotalPrice() %></td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>

    <div class="mt-4">
        <h5>Total Price: <%= order.getTotalPrice() %></h5>
        <h5>Final Price (after discount): <%= order.getFinalPrice() %></h5>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function handleStatusChange() {
        const statusSelect = document.getElementById('orderStatus');
        const cancelBox = document.getElementById('cancel-reason-box');
        if (statusSelect && cancelBox) {
            cancelBox.style.display = (statusSelect.value === '6') ? 'block' : 'none';
        }
    }
    window.onload = handleStatusChange;
</script>
</body>
</html>
