<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, Model.Product, Model.Attribute" %>
<%@ include file="left-sidebar.jsp" %>

<%
    List<Product> products = (List<Product>) request.getAttribute("products");
    String context = request.getContextPath();
%>

<!DOCTYPE html>
<html>
<head>
    <title>Manage Products</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>
    <style>
        .content {
            margin-left: 240px; /* để không đè lên left-sidebar.jsp */
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
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4>🧺 Product Management</h4>
        <a href="<%= context%>/Product?action=add" class="btn btn-success">
            <i class="fa-solid fa-circle-plus"></i> Add Product
        </a>
    </div>

    <% if (products != null && !products.isEmpty()) { %>
    <table class="table table-striped table-hover">
        <thead>
            <tr>
                <th>#</th>
                <th>Product Name</th>
                <th>Price (₫)</th>
                <th>Attributes</th>
                <th style="width:150px;">Actions</th>
            </tr>
        </thead>
        <tbody>
            <% int index = 1;
               for (Product p : products) { %>
            <tr>
                <td><%= index++ %></td>
                <td><%= p.getProductName() %></td>
                <td><%= String.format("%,d", p.getPrice()) %></td>
                <td>
                    <% List<Attribute> attrs = p.getAttributes();
                       if (attrs != null && !attrs.isEmpty()) { %>
                       <ul class="mb-0 ps-3">
                           <% for (Attribute attr : attrs) { %>
                           <li><strong><%= attr.getAttributeName() %>:</strong> <%= attr.getAttributeValue() %></li>
                           <% } %>
                       </ul>
                    <% } else { %>
                       <span class="text-muted">No attributes</span>
                    <% } %>
                </td>
                <td class="text-center">
                    <a href="<%= context%>/Product?action=edit&id=<%= p.getProductID()%>" 
                       class="btn btn-warning btn-sm">
                        <i class="fa-solid fa-pen-to-square"></i>
                    </a>
                    <button class="btn btn-danger btn-sm"
                            data-bs-toggle="modal"
                            data-bs-target="#deleteModal"
                            data-id="<%= p.getProductID()%>">
                        <i class="fa-solid fa-trash-can"></i>
                    </button>
                </td>
            </tr>
            <% } %>
        </tbody>
    </table>
    <% } else { %>
    <div class="alert alert-info">No products available.</div>
    <% } %>
</div>

<!-- Delete confirmation modal -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title">Confirm Deletion</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">Are you sure you want to delete this product?</div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <a id="confirmDeleteBtn" href="#" class="btn btn-danger">Delete</a>
            </div>
        </div>
    </div>
</div>
<!-- Nút kho hàng nổi góc dưới bên phải -->
<form action="<%= context %>/Inventory" method="get">
    <button type="submit"
            class="btn btn-primary rounded-circle shadow"
            style="position: fixed; bottom: 30px; right: 30px; width:60px; height:60px;
                   display:flex; align-items:center; justify-content:center; z-index:999;">
        <i class="fa-solid fa-warehouse fa-lg"></i>
    </button>
</form>

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
