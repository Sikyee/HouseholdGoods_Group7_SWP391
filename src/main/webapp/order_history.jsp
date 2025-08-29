<%-- 
    Document   : order-history.jsp
    Author     : pts03
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, Model.Order, Model.OrderDetail" %>

<%
    List<Order> orders = (List<Order>) request.getAttribute("orders");
%>

<html>
<head>
    <title>Order History</title>
    <style>
    body {
        font-family: Arial, sans-serif;
        background: linear-gradient(to right, #fafafa, #f2f7fc);
        padding: 20px;
    }
    h2 {
        color: #34495e;
        margin-bottom: 20px;
    }
    .order-card {
        background: #fff;
        border-left: 6px solid #5dade2; /* xanh pastel */
        border-radius: 10px;
        padding: 18px;
        margin-bottom: 25px;
        box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        transition: transform 0.2s ease-in-out;
    }
    .order-card:hover {
        transform: translateY(-2px);
    }
    .order-card h3 {
        margin: 0 0 12px 0;
        color: #5dade2;
    }
    .order-info p {
        margin: 6px 0;
        color: #444;
    }
    .status {
        display: inline-block;
        padding: 5px 10px;
        border-radius: 20px;
        font-weight: bold;
        font-size: 13px;
        color: #fff;
    }
    /* màu pastel */
    .pending { background: #f7b267; }     /* cam nhạt */
    .processing { background: #7ac7e3; }  /* xanh dương nhạt */
    .cancelled { background: #e28a8a; }   /* đỏ hồng nhạt */
    .completed { background: #8fd19e; }   /* xanh lá nhạt */

    table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 14px;
        border-radius: 8px;
        overflow: hidden;
    }
    th, td {
        border: 1px solid #ddd;
        padding: 12px;
        text-align: center;
    }
    th {
        background: #a9d6f5; /* xanh pastel */
        color: #2c3e50;
    }
    tr:hover {
        background: #f9fbfd;
    }
    td img {
        border-radius: 6px;
        box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    }
    .empty-msg {
        text-align: center;
        color: #b94a48;
        font-style: italic;
    }
</style>
</head>
<body>
    <%@ include file="header.jsp" %>

<h2>Order History</h2>

<% if (orders != null && !orders.isEmpty()) { %>
    <% for (Order order : orders) { 
           String statusClass = "";
           switch(order.getOrderStatusID()) {
               case 1: statusClass = "pending"; break;
               case 2: statusClass = "processing"; break;
               case 3: statusClass = "cancelled"; break;
               default: statusClass = "completed"; break;
           }
    %>
        <div class="order-card">
            <h3>Order #<%=order.getOrderID()%></h3>
            <div class="order-info">
                <p><strong>Order Date:</strong> <%=order.getOrderDate()%></p>
                <p><strong>Status:</strong> <span class="status <%=statusClass%>"><%=order.getOrderStatusName()%></span></p>
                <p><strong>Payment Method:</strong> <%=order.getPaymentMethodName()%></p>
                <p><strong>Receiver:</strong> <%=order.getFullName()%></p>
                <p><strong>Address:</strong> <%=order.getDeliveryAddress()%></p>
                <p><strong>Phone:</strong> <%=order.getPhone()%></p>
                <p><strong>Total Price:</strong> $<%=order.getTotalPrice()%></p>
                <p><strong>Final Price (after voucher):</strong> $<%=order.getFinalPrice()%></p>
            </div>

            <table>
                <tr>
                    <th>Image</th>
                    <th>Product Name</th>
                    <th>Quantity</th>
                    <th>Price</th>
                </tr>

                <% if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) { %>
                    <tr>
                        <td colspan="4" class="empty-msg">
                            No products in this order.
                        </td>
                    </tr>
                <% } else { 
                       for (OrderDetail od : order.getOrderDetails()) { %>
                    <tr>
                        <td><img src="<%= od.getProductImage()%>" width="60"></td>
                        <td><%= od.getOrderName()%></td>
                        <td><%= od.getQuantity()%></td>
                        <td>$<%= od.getTotalPrice()%></td>
                    </tr>
                <%   } 
                   } %>
            </table>
        </div>
    <% } %>
<% } else { %>
    <p style="color:#666; font-style:italic;">You don’t have any orders yet.</p>
<% } %>
<%@ include file="footer.jsp" %>
</body>
</html>
