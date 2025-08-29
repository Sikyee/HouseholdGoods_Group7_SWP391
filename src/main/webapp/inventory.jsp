<%@page import="Model.Inventory"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html;charset=UTF-8" %>
<%@ include file="left-sidebar.jsp" %>
<%
    List<Inventory> products = (List<Inventory>) request.getAttribute("products");
    boolean allSoldOut = true;
%>

<!DOCTYPE html>
<html>
<head>
    <title>Inventory</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, sans-serif;
            background: #f8f9fa;
        }
        .content {
            margin-left: 240px; /* ✅ cùng width với sidebar */
            padding: 20px;
            min-height: 100vh;
            transition: margin-left 0.3s;
        }
        table {
            background: #ffffff;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 4px 10px rgba(0,0,0,0.08);
        }
        th {
            background: #007bff;
            color: white;
            text-align: center;
        }
        td {
            vertical-align: middle;
            text-align: center;
        }
        tr:nth-child(even) {
            background: #f2f9ff;
        }
        tr:hover {
            background: #e6f2ff;
        }
        .alert {
            border-radius: 10px;
            font-weight: bold;
        }
    </style>
</head>
<body>
<div class="content">
    <h4>🏬 Inventory Management</h4>

    <% if (products != null && !products.isEmpty()) { %>
    <table class="table table-striped table-hover mt-3">
        <thead>
            <tr>
                <th>#</th>
                <th>Product</th>
                <th>Brand</th>
                <th>Quantity</th>
            </tr>
        </thead>
        <tbody>
            <% int index = 1;
               for (Inventory p : products) {
                   if (p.getStatus() == 1) {
                       if (p.getQuantity() > 0) {
                           allSoldOut = false;
                       } %>
            <tr>
                <td><%= index++ %></td>
                <td><%= p.getProductName() %></td>
                <td><%= p.getBrandName() %></td>
                <td>
                    <% if (p.getQuantity() > 0) { %>
                        <%= p.getQuantity() %>
                    <% } else { %>
                        <span class="text-danger fw-bold">Sold Out</span>
                    <% } %>
                </td>
            </tr>
            <% } } %>
        </tbody>
    </table>

    <% if (allSoldOut) { %>
    <div class="alert alert-danger text-center mt-3">
        🚨 All products are sold out!
    </div>
    <% } %>
    <% } else { %>
    <div class="alert alert-info mt-3 text-center">
        ℹ️ No products available.
    </div>
    <% } %>
</div>
</body>
</html>
