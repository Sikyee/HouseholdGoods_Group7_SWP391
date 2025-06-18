<%-- 
    Document   : managerProduct
    Created on : Jun 15, 2025, 1:48:18 PM
    Author     : thong
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, Model.Product" %>
<%@ include file="left-sidebar.jsp" %>

<%
    List<Product> products = (List<Product>) request.getAttribute("products");
    String context = request.getContextPath();
%>

<div class="content" style="margin-left:240px; padding:20px;">
    <h4>📦 Manage Products</h4>

    <a href="<%= context%>/Product?action=add" class="btn btn-success mb-2">
        <i class="fas fa-plus-circle"></i> Add Product
    </a>

    <table class="table table-bordered">
        <thead>
            <tr>
                <th>ID</th><th>Name</th><th>Price</th><th>Image</th><th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <% if (products != null) {
                    for (Product p : products) {%>
            <tr>
                <td><%= p.getProductID()%></td>
                <td><%= p.getProductName()%></td>
                <td><%= p.getPrice()%> ₫</td>
                <td><img src="images/<%= p.getImage()%>" width="60"/></td>
                <td>
                    <a href="<%= context%>/Product?action=edit&id=<%= p.getProductID()%>" class="btn btn-warning btn-sm">
                        <i class="fas fa-edit"></i> Edit
                    </a>

                    <!-- ✅ Sử dụng confirm() với tên sản phẩm -->
                    <a href="<%= context%>/Product?action=delete&id=<%= p.getProductID()%>" 
                       class="btn btn-danger btn-sm"
                       onclick="return confirm('Are you sure you want to delete \"<%= p.getProductName()%>\"?');">
                        <i class="fas fa-trash"></i> Delete
                    </a>
                </td>
            </tr>
            <% }
                }%>
        </tbody>
    </table>
</div>




