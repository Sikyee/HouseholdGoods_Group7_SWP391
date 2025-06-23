<%-- 
    Document   : left-sidebar
    Created on : Jun 15, 2025, 2:17:32 PM
    Author     : thong
--%>

<!-- File: left-sidebar.jsp -->
<%@ page contentType="text/html;charset=UTF-8" %>
<!-- Bootstrap 5 CDN -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<!-- FontAwesome icons (tuỳ chọn) -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

<div class="sidebar d-flex flex-column p-3 text-white bg-dark position-fixed" style="width: 220px; height: 100vh;">
    <a href="dashboard" class="d-flex align-items-center mb-3 mb-md-0 me-md-auto text-white text-decoration-none">
        <img src="images/logo.png" alt="Logo" style="height: 32px; margin-right: 10px;">
        <span class="fs-5 fw-bold">TRƯỜNG AN</span>
    </a>
    <hr>
    <ul class="nav nav-pills flex-column mb-auto">
        <li><a href="dashboard" class="nav-link text-white"><i class="fas fa-users"></i> Dashboard</a></li>
        <li><a href="#" class="nav-link text-white"><i class="fas fa-calendar-week"></i> Revenue (Week)</a></li>
        <li><a href="#" class="nav-link text-white"><i class="fas fa-calendar-alt"></i> Revenue (Month)</a></li>
        <li><a href="#" class="nav-link text-white"><i class="fas fa-file-invoice"></i> Invoice</a></li>
        <li><a href="User" class="nav-link text-white"><i class="fas fa-users"></i> Accounts</a></li> 
        <li><a href="#" class="nav-link text-white"><i class="fas fa-wallet"></i> Wallets</a></li>
        <li><a href="Product" class="nav-link text-white"><i class="fas fa-box"></i> Products</a></li>
        <li>
            <a href="#!" class="nav-link text-white" id="categoryToggle">
                <i class="fas fa-list"></i> Category
                <i class="fas fa-caret-down float-end"></i>
            </a>
            <ul class="nav flex-column ms-3 collapse" id="categorySubMenu">
                <li><a href="Category" class="nav-link text-white"><i class="fas fa-tags"></i> Category List</a></li>
                <li><a href="MainCategory" class="nav-link text-white"><i class="fas fa-list-alt"></i> Main Category</a></li>
            </ul>
        </li>

        <li><a href="#" class="nav-link text-white"><i class="fas fa-star"></i> Top 10 Products</a></li>
        <li><a href="#" class="nav-link text-white"><i class="fas fa-users"></i> Top 5 Customers</a></li>
        <li><a href="#" class="nav-link text-white"><i class="fas fa-truck"></i> Supplier</a></li>
        <li class="nav-item"><a class="nav-link text-white" href="<%= request.getContextPath()%>/logout"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
    </ul>
</div>

<style>
    body {
        margin: 0;
        padding: 0;
    }
    .sidebar .nav-link {
        padding: 10px 15px;
        border-radius: 6px;
        transition: background-color 0.2s ease-in-out;
    }
    .sidebar .nav-link:hover {
        background-color: #3a3b5a;
    }
    #categorySubMenu li a {
        padding-left: 30px;
        font-size: 0.9rem;
    }
</style>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const toggle = document.getElementById("categoryToggle");
        const submenu = document.getElementById("categorySubMenu");

        toggle.addEventListener("click", function (e) {
            e.preventDefault();
            submenu.classList.toggle("show");
        });
    });
</script>
