<%-- 
    Document   : product-form
    Created on : Jun 15, 2025, 4:05:42 PM
    Author     : thong
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="Model.Product" %>
<%@ include file="left-sidebar.jsp" %>
<%
    Product product = (Product) request.getAttribute("product");
    String context = request.getContextPath();
%>
<!DOCTYPE html>
<html>
    <head>
        <title><%= (product == null) ? "Add Product" : "Edit Product"%></title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" />
        <style>
            body {
                background-color: #f5f5f5;
            }
            .form-container {
                max-width: 720px;
                margin: 40px auto;
                background: #ffffff;
                padding: 30px 40px;
                border-radius: 12px;
                box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            }
            .form-title {
                text-align: center;
                margin-bottom: 25px;
                color: #5d4037;
            }
        </style>
    </head>
    <body>
        <div class="form-container">
            <h4 class="form-title"><%= (product == null) ? "➕ Add New Household Product" : "✏️ Edit Household Product"%></h4>

            <form method="post" action="<%= context%>/Product" enctype="multipart/form-data" onsubmit="return validateForm()">
                <% if (product != null) {%>
                <input type="hidden" name="id" value="<%= product.getProductID()%>"/>
                <input type="hidden" name="image" value="<%= product.getImage()%>"/>
                <% }%>

                <!-- Product Name -->
                <div class="mb-3">
                    <label class="form-label">Product Name</label>
                    <input type="text" name="productName" class="form-control" required
                           value="<%= (product != null) ? product.getProductName() : ""%>">
                </div>

                <!-- Description -->
                <div class="mb-3">
                    <label class="form-label">Description</label>
                    <textarea name="description" class="form-control" rows="3" required><%= (product != null) ? product.getDescription() : ""%></textarea>
                </div>

                <!-- Price -->
                <div class="mb-3">
                    <label class="form-label">Price (VND)</label>
                    <input type="number" name="price" class="form-control" min="0" required
                           value="<%= (product != null) ? product.getPrice() : ""%>">
                </div>

                <!-- Stock Quantity -->
                <div class="mb-3">
                    <label class="form-label">Stock Quantity</label>
                    <input type="number" name="stonkQuantity" class="form-control" min="0" required
                           value="<%= (product != null) ? product.getStonkQuantity() : ""%>">
                </div>

                <!-- Subcategory ID -->
                <div class="mb-3">
                    <label class="form-label">Sub Category ID</label>
                    <input type="number" name="subCategory" class="form-control" min="1" required
                           value="<%= (product != null) ? product.getSubCategory() : ""%>">
                </div>

                <!-- Brand ID -->
                <div class="mb-3">
                    <label class="form-label">Brand ID</label>
                    <input type="number" name="brandID" class="form-control" min="1" required
                           value="<%= (product != null) ? product.getBrandID() : ""%>">
                </div>

                <!-- Image Upload -->
                <div class="mb-3">
                    <label class="form-label">Product Image (JPG/PNG)</label>
                    <input type="file" name="imageFile" accept=".jpg,.jpeg,.png" class="form-control">
                    <% if (product != null && product.getImage() != null) {%>
                    <small class="text-muted">Current image: <%= product.getImage()%></small>
                    <% }%>
                </div>

                <!-- Submit Button -->
                <div class="d-flex justify-content-between mt-4">
                    <button type="submit" class="btn btn-primary">
                        <%= (product == null) ? "Add Product" : "Update Product"%>
                    </button>

                    <a href="<%= context%>/Product" class="btn btn-secondary">
                        Cancel
                    </a>
                </div>

                <script>
                    function validateForm() {
                        const price = document.querySelector('input[name="price"]').value;
                        const stock = document.querySelector('input[name="stonkQuantity"]').value;

                        if (price < 0 || stock < 0) {
                            alert("Price and Stock Quantity cannot be negative.");
                            return false;
                        }

                        return true;
                    }
                </script>
                </body>
                </html>