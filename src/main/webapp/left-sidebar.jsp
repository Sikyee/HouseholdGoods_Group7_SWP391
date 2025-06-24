<%@ page contentType="text/html;charset=UTF-8" %>
<!-- Bootstrap 5 -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<!-- FontAwesome -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

<!-- Sidebar -->
<div class="sidebar d-flex flex-column p-3 bg-light border-end position-fixed" style="width: 240px; height: 100vh;">
    <!-- Logo -->
    <a href="dashboard" class="d-flex align-items-center mb-4 text-decoration-none text-dark">
        <span class="fs-5 fw-bold text-uppercase">Household Shop</span>
    </a>

    <!-- Navigation -->
    <ul class="nav nav-pills flex-column">
        <li class="nav-item mb-1">
            <a href="dashboard" class="nav-link text-dark">
                <i class="fas fa-chart-line me-2"></i> Dashboard
            </a>
        </li>
        <li class="nav-item mb-1">
            <a href="User" class="nav-link text-dark">
                <i class="fas fa-user-friends me-2"></i> User Accounts
            </a>
        </li>
        <li class="nav-item mb-1">
            <a href="#!" class="nav-link text-black" id="categoryToggle">
                <i class="fas fa-list"></i> Category
                <i class="fas fa-caret-down float-end"></i>
            </a>
            <ul class="nav flex-column ms-3 collapse" id="categorySubMenu">
                <li><a href="Category" class="nav-link text-black"><i class="fas fa-tags"></i> Category List</a></li>
                <li><a href="MainCategory" class="nav-link text-black"><i class="fas fa-list-alt"></i> Main Category</a></li>
            </ul>
        </li>
        <li class="nav-item mb-1">
            <a href="Product" class="nav-link text-dark">
                <i class="fas fa-blender me-2"></i> Products
            </a>
        </li>
        <li class="nav-item mb-1">
            <a href="Promotion" class="nav-link text-dark">
                <i class="fas fa-tags me-2"></i> Promotions
            </a>
        </li>
        <li class="nav-item mb-1">
            <a href="Feedback" class="nav-link text-dark">
                <i class="fas fa-comments me-2"></i> Feedback
            </a>
        </li>
        <li class="nav-item mt-3">
            <a href="<%= request.getContextPath()%>/logout" class="nav-link text-danger">
                <i class="fas fa-sign-out-alt me-2"></i> Logout
            </a>
        </li>
    </ul>
</div>

<!-- Style -->
<style>
    body {
        margin: 0;
        padding-left: 0;
        background-color: #f9f9f9;
        font-family: 'Segoe UI', sans-serif;
    }

    .sidebar .nav-link {
        border-radius: 8px;
        transition: all 0.2s ease;
        font-size: 15px;
    }

    .sidebar .nav-link:hover {
        background-color: #e7e7e7;
    }

    .sidebar .nav-link i {
        width: 20px;
        text-align: center;
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
