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

<!DOCTYPE html>
<html>
    <head>
        <title>Manage Products</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" />
    </head>
    <body>
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
                    <% if (products != null && !products.isEmpty()) {
                for (Product p : products) {%>
                    <tr>
                        <td><%= p.getProductID()%></td>
                        <td><%= p.getProductName()%></td>
                        <td><%= p.getPrice()%></td>
                        <td><img src="images/<%= p.getImage()%>" width="60"/></td>
                        <td>
                            <a href="<%= context%>/Product?action=edit&id=<%= p.getProductID()%>" class="btn btn-warning btn-sm">Edit</a>
                            <a href="#" class="btn btn-danger btn-sm"
                               data-bs-toggle="modal"
                               data-bs-target="#deleteModal"
                               data-id="<%= p.getProductID()%>">Delete</a>
                        </td>
                    </tr>
                    <% }
        } else { %>
                    <tr><td colspan="5" class="text-center">No product found.</td></tr>
                    <% }%>
                </tbody>
            </table>
        </div>

        <!-- ✅ Delete Confirmation Modal -->
        <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Confirm Deletion</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        Are you sure you want to delete this product?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <a id="confirmDeleteBtn" href="#" class="btn btn-danger">Delete</a>
                    </div>
                </div>
            </div>
        </div>

        <!-- ✅ Bootstrap JS + Script for modal -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                var deleteModal = document.getElementById('deleteModal');
                deleteModal.addEventListener('show.bs.modal', function (event) {
                    var button = event.relatedTarget;
                    var productId = button.getAttribute('data-id');
                    var confirmBtn = document.getElementById('confirmDeleteBtn');
                    confirmBtn.href = '<%= context%>/Product?action=delete&id=' + productId;
                });
            });
        </script>
    </body>
</html>





