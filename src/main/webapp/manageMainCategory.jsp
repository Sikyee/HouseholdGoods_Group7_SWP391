<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, Model.Category" %>
<%@ include file="left-sidebar.jsp" %>

<%
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    String context = request.getContextPath();
%>

<div class="content" style="margin-left:240px; padding:20px;">
    <h4>📦 Manage Main Categories</h4>

    <a href="<%= context%>/MainCategory?action=add" class="btn btn-success mb-2">
        <i class="fas fa-plus-circle"></i> Add Category
    </a>

    <table class="table table-bordered">
        <thead>
            <tr>
                <th>ID</th><th>Category Name</th><th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <%
                if (categories != null && !categories.isEmpty()) {
                    for (Category c : categories) {
            %>
            <tr>
                <td><%= c.getCategoryID()%></td>
                <td><%= c.getCategoryName()%></td>

                <td>
                    <a href="<%= context%>/MainCategory?action=edit&id=<%= c.getCategoryID()%>" class="btn btn-warning btn-sm">
                        <i class="fas fa-edit"></i> Edit
                    </a>


                    <a href="<%= context%>/MainCategory?action=delete&id=<%= c.getCategoryID()%>" 
                       class="btn btn-danger btn-sm"
                       onclick="return confirm('Are you sure you want to delete this category?');">
                        <i class="fas fa-trash"></i> Delete
                    </a>
                </td>
            </tr>
            <%
                }
            } else {
            %>
            <tr>
                <td colspan="7" class="text-center">No category found.</td>
            </tr>
            <%
                }
            %>
        </tbody>
    </table>
</div>
