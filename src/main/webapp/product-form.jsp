<%-- 
    Document   : product-form
    Created on : Jun 15, 2025, 4:05:42 PM
    Author     : thong
--%>

<%@page import="Model.SubCategory"%>
<%@page import="Model.Brand"%>
<%@page import="java.util.List"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="Model.Product" %>
<%@ include file="left-sidebar.jsp" %>
<%
    String successMessage = (String) session.getAttribute("successMessage");
    if (successMessage != null) {
%>
<style>
    /* Nền mờ phủ toàn màn hình */
    #overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.4);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 9999;
    }

    /* Hộp thông báo */
    #messageBox {
        background: #fff;
        border-radius: 12px;
        box-shadow: 0 8px 20px rgba(0,0,0,0.25);
        overflow: hidden;
        animation: fadeIn 0.3s ease;
    }

    /* Bảng */
    #messageBox table {
        width: 100%;
        border-collapse: collapse;
    }

    #messageBox th {
        background: #28a745; /* xanh lá success */
        color: #fff;
        padding: 12px;
        font-size: 20px;
        text-transform: uppercase;
        letter-spacing: 1px;
    }

    #messageBox td {
        padding: 16px;
        font-size: 16px;
        color: #333;
    }

    /* Nút OK */
    #messageBox button {
        padding: 8px 20px;
        margin-top: 10px;
        border: none;
        border-radius: 6px;
        background: #28a745;
        color: #fff;
        font-size: 16px;
        cursor: pointer;
        transition: background 0.3s ease;
    }

    #messageBox button:hover {
        background: #218838;
    }

    /* Hiệu ứng mượt khi hiện */
    @keyframes fadeIn {
        from {
            transform: scale(0.8);
            opacity: 0;
        }
        to {
            transform: scale(1);
            opacity: 1;
        }
    }
</style>

<div id="overlay">
    <div id="messageBox">
        <table>
            <tr>
                <th>Notification</th>
            </tr>
            <tr>
                <td><%= successMessage%></td>
            </tr>
            <tr>
                <td>
                    <button onclick="document.getElementById('overlay').style.display = 'none'">
                        OK
                    </button>
                </td>
            </tr>
        </table>
    </div>
</div>
<%
        session.removeAttribute("successMessage");
    }
%>


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

                <!-- Subcategory -->
                <div class="mb-3">
                    <label class="form-label">Sub Category</label>
                    <select name="subCategory" class="form-select" required>
                        <option value="">-- Select Sub Category --</option>
                        <%
                            List<SubCategory> subCategories = (List<SubCategory>) request.getAttribute("subCategories");
                            if (subCategories != null) {
                                for (SubCategory sc : subCategories) {
                        %>
                        <option value="<%= sc.getId()%>" 
                                <%= (product != null && product.getSubCategory() == sc.getId()) ? "selected" : ""%>>
                            <%= sc.getName()%>
                        </option>
                        <%
                                }
                            }
                        %>
                    </select>
                </div>

                <!-- Brand -->
                <div class="mb-3">
                    <label class="form-label">Brand</label>
                    <select name="brandID" class="form-select" required>
                        <option value="">-- Select Brand --</option>
                        <%
                            List<Brand> brands = (List<Brand>) request.getAttribute("brands");
                            if (brands != null) {
                                for (Brand b : brands) {
                        %>
                        <option value="<%= b.getId()%>" 
                                <%= (product != null && product.getBrandID() == b.getId()) ? "selected" : ""%>>
                            <%= b.getName()%>
                        </option>
                        <%
                                }
                            }
                        %>
                    </select>
                </div>


                <!-- Image Upload -->
                <div class="mb-3">
                    <label class="form-label">Product Image (JPG/PNG)</label>
                    <input type="file" name="imageFile" accept=".jpg,.jpeg,.png" class="form-control" <%= (product == null) ? "required" : ""%>>

                    <% if (product != null && product.getImage() != null) {%>
                    <div class="mt-2">
                        <small class="text-muted">Current image:</small><br>
                        <img src="<%= context%>/images/<%= product.getImage()%>" 
                             alt="Product Image" 
                             style="max-width: 150px; border:1px solid #ddd; border-radius:5px; padding:3px;">
                    </div>
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
