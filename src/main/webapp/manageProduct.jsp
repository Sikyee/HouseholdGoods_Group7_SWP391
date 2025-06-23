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
        <style>
            .product-card {
                border: 1px solid #ddd;
                border-radius: 12px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                transition: transform 0.2s;
                background-color: #fffdf7;
            }

            .product-card:hover {
                transform: scale(1.02);
            }

            .product-img {
                height: 180px;
                object-fit: cover;
                border-radius: 12px 12px 0 0;
            }

            .card-title {
                font-size: 1.2rem;
                color: #593a25;
            }

            .card-text {
                color: #b74b2d;
            }

            .attribute-list {
                font-size: 0.9rem;
                color: #444;
                padding-left: 1rem;
                margin-top: 10px;
            }

            .content {
                margin-left: 240px;
                padding: 20px;
                background-color: #fefaf5;
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
            <div class="row row-cols-1 row-cols-md-3 g-4">
                <% for (Product p : products) {%>
                <div class="col">
                    <div class="card product-card h-100">
                        <img src="images/<%= p.getImage()%>" class="card-img-top product-img" alt="<%= p.getProductName()%>">
                        <div class="card-body">
                            <h5 class="card-title"><%= p.getProductName()%></h5>
                            <p class="card-text fw-bold">₫<%= String.format("%,d", p.getPrice())%></p>

                            <% List<Attribute> attrs = p.getAttributes(); %>
                            <% if (attrs != null && !attrs.isEmpty()) { %>
                            <div class="attribute-list">
                                <ul>
                                    <% for (Attribute attr : attrs) {%>
                                    <li><strong><%= attr.getAttributeName()%>:</strong> <%= attr.getAttributeValue()%></li>
                                        <% } %>
                                </ul>
                            </div>
                            <% }%>
                        </div>
                        <div class="card-footer bg-transparent d-flex justify-content-between">
                            <a href="<%= context%>/Product?action=edit&id=<%= p.getProductID()%>" class="btn btn-warning btn-sm">
                                <i class="fa-solid fa-pen-to-square"></i> Edit
                            </a>
                            <button class="btn btn-danger btn-sm"
                                    data-bs-toggle="modal"
                                    data-bs-target="#deleteModal"
                                    data-id="<%= p.getProductID()%>">
                                <i class="fa-solid fa-trash-can"></i> Delete
                            </button>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>
            <% } else { %>
            <div class="alert alert-info">No products available.</div>
            <% }%>
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
