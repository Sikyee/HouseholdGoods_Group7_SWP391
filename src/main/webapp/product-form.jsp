<%-- 
    Document   : product-form
    Created on : Jun 15, 2025, 4:05:42 PM
    Author     : thong
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="Model.Product" %>
<%@ include file="left-sidebar.jsp" %>

<%
    Product p = (Product) request.getAttribute("product");
    boolean isEdit = (p != null);
    String context = request.getContextPath();
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title><%= isEdit ? "Edit" : "Add"%> Product</title>
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
                <h4><%= isEdit ? "✏️ Edit" : "➕ Add"%> Product</h4>
            </div>

            <!-- ✅ Phải có enctype để hỗ trợ upload -->
            <form method="post" action="<%= context%>/Product" enctype="multipart/form-data" class="row g-3">
                <input type="hidden" name="id" value="<%= isEdit ? p.getProductID() : ""%>" />
                <input type="hidden" name="image" value="<%= isEdit ? p.getImage() : ""%>" />

                <div class="col-md-6">
                    <label class="form-label">Product Name</label>
                    <input type="text" name="productName" required class="form-control" value="<%= isEdit ? p.getProductName() : ""%>"/>
                </div>

                <div class="col-md-6">
                    <label class="form-label">Price (₫)</label>
                    <input type="number" name="price" required class="form-control" value="<%= isEdit ? p.getPrice() : ""%>"/>
                </div>

                <div class="col-md-12">
                    <label class="form-label">Description</label>
                    <textarea name="description" class="form-control" rows="3"><%= isEdit ? p.getDescription() : ""%></textarea>
                </div>

                <div class="col-md-6">
                    <label class="form-label">Stock Quantity</label>
                    <input type="number" name="stonkQuantity" required class="form-control" value="<%= isEdit ? p.getStonkQuantity() : ""%>"/>
                </div>

                <div class="col-md-6">
                    <label class="form-label">Product Image</label>
                    <input type="file" name="imageFile" accept="image/*" class="form-control" <%= isEdit ? "" : "required"%> />
                    <% if (isEdit && p.getImage() != null && !p.getImage().isEmpty()) {%>
                    <div class="mt-2">
                        <img src="images/<%= p.getImage()%>" width="100" alt="Current Image"/>
                    </div>
                    <% }%>
                </div>

                <div class="col-md-6">
                    <label class="form-label">Brand ID</label>
                    <input type="number" name="brandID" required class="form-control" value="<%= isEdit ? p.getBrandID() : ""%>"/>
                </div>

                <div class="col-md-6">
                    <label class="form-label">SubCategory ID</label>
                    <input type="number" name="subCategory" required class="form-control" value="<%= isEdit ? p.getSubCategory() : ""%>"/>
                </div>

                <div class="col-12">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> Save
                    </button>
                    <a href="<%= context%>/Product" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>

    </body>
</html>



