<%@page import="java.util.List"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="Model.Category" %>
<%@ include file="left-sidebar.jsp" %>

<%
    Category c = (Category) request.getAttribute("category");
    boolean isEdit = (c != null);
    String context = request.getContextPath();
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title><%= isEdit ? "Edit" : "Add"%> Category</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
        <style>
            .content {
                margin-left: 240px;
                padding: 30px;
            }
            .form-label {
                font-weight: 500;
            }
        </style>
    </head>
    <body>

        <div class="content">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h4><%= isEdit ? "✏️ Edit" : "➕ Add"%> Category</h4>
            </div>

            <!-- ✅ Phải có enctype để hỗ trợ upload -->
            <form method="post" action="<%= context%>/Category" enctype="multipart/form-data" class="row g-3">
                <% if (isEdit) {%>
                <input type="hidden" name="id" value="<%= c.getSubCategoryID()%>" />
                <% } %>

                <div class="col-md-6">
                    <label class="form-label">Category Name</label>
                    <select name="categoryID" class="form-select" required>
                        <option value="">-- Select Category --</option>
                        <%
                            List<Category> categoryList = (List<Category>) request.getAttribute("categoryList");
                            for (Category cat : categoryList) {
                                boolean selected = isEdit && cat.getCategoryID() == c.getCategoryID();
                        %>
                        <option value="<%= cat.getCategoryID()%>" <%= selected ? "selected" : ""%>>
                            <%= cat.getCategoryName()%>
                        </option>
                        <% }%>
                    </select>
                </div>

                <div class="col-md-6">
                    <label class="form-label">SubCategory Name</label>
                    <input type="text" name="subCategoryName" required class="form-control"
                           value="<%= isEdit ? c.getSubCategoryName() : ""%>" />
                </div>

                <div class="col-12">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> Save
                    </button>
                    <a href="<%= context%>/Category" class="btn btn-secondary">Cancel</a>
                </div>
            </form>

        </div>

    </body>
</html>