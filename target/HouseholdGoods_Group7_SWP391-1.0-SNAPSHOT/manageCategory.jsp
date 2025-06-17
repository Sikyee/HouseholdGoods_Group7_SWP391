<%-- 
    Document   : manageCategory
    Created on : Jun 16, 2025, 6:33:27 PM
    Author     : Admin
--%>

<%@page import="Model.Category"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, Model.Product" %>
<%@ include file="left-sidebar.jsp" %>

<%
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    String context = request.getContextPath();
%>

<div class="content" style="margin-left:240px; padding:20px;">
    <h4>📦 Manage Categories</h4>

    <a href="<%= context%>/Category?action=add" class="btn btn-success mb-2">
        <i class="fas fa-plus-circle"></i> Add Category
    </a>

    <table class="table table-bordered">
        <thead>
            <tr>
                <th>ID</th><th>Category Name</th><th>Sub Category Name</th><th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <%
                if (categories != null) {
                    for (Category c : categories) {
            %>
            <tr>
                <td><%= c.getCategoryID()%></td>
                <td><%= c.getCategoryName()%></td>
                <td><%= c.getSubCategoryName()%></td>

                <td>
                    <a href="<%= context%>/Product?action=edit&id=#" class="btn btn-warning btn-sm">
                        <i class="fas fa-edit"></i> Edit
                    </a>

                    
                    <a href="<%= context%>/Product?action=delete&id=#" 
                       class="btn btn-danger btn-sm"
                       onclick="return confirm('Are you sure you want to delete \"#\"?');">
                        <i class="fas fa-trash"></i> Delete
                    </a>
                </td>
            </tr>
            <%
                    }
                }
            %>
        </tbody>
    </table>
</div>
