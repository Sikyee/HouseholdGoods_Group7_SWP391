<%-- 
    Document   : inventory
    Created on : 25 thg 8, 2025, 22:37:38
    Author     : pts03
--%>

<%@page import="Model.Inventory"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html;charset=UTF-8" %>
<%@ include file="left-sidebar.jsp" %>
<%
    List<Inventory> products = (List<Inventory>) request.getAttribute("products");
    String context = request.getContextPath();
    boolean allSoldOut = true;
%>

<!DOCTYPE html>
<html>
    <head>
        <title>Inventory</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>
        <style>
            .content {
                margin-left: 240px;
                padding: 20px;
                background-color: #fefaf5;
                min-height: 100vh;
            }
            table {
                background: #fff;
                border-radius: 10px;
                overflow: hidden;
                box-shadow: 0 2px 6px rgba(0,0,0,0.1);
            }
            th {
                background: #593a25;
                color: #fff;
                text-align: center;
            }
            td {
                vertical-align: middle;
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
                        <th>Tên sản phẩm</th>
                        <th>Tồn kho</th>
                    </tr>
                </thead>
                <tbody>
                    <% int index = 1;
                        for (Inventory p : products) {
                            // Status = 1 mới hiển thị (DAO cũng đã lọc rồi, nhưng check thêm vẫn an toàn)
                            if (p.getStatus() == 1) {
                                if (p.getQuantity() > 0) {
                                    allSoldOut = false;
                                }%>
                    <tr>
                        <td><%= index++%></td>
                        <td><%= p.getProductName()%></td>
                        <td>
                            <% if (p.getQuantity() > 0) {%>
                            <%= p.getQuantity()%>
                            <% } else { %>
                            <span class="text-danger fw-bold">Sold Out</span>
                            <% } %>
                        </td>
                    </tr>
                    <% }
                        } %>
                </tbody>
            </table>

            <% if (allSoldOut) { %>
            <div class="alert alert-danger text-center mt-3">
                🚨 Tất cả sản phẩm đã hết hàng!
            </div>
            <% } %>
            <% } else { %>
            <div class="alert alert-info mt-3">Không có sản phẩm nào trong kho.</div>
            <% }%>
        </div>
    </body>
</html>
