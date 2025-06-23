<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="navbar navbar-expand-lg navbar-dark fixed-top" style="background-color: #343a40;">
    <div class="container-fluid">
        <a class="navbar-brand" href="#"><i class="fas fa-home me-2"></i>Household Goods</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <div class="d-flex ms-auto me-3">
                <input class="form-control me-2" type="search" placeholder="Search products..." aria-label="Search">
                <button class="btn btn-outline-light" type="submit">
                    <i class="fas fa-search"></i>
                </button>
            </div>
            <ul class="navbar-nav d-flex flex-row align-items-center">
                <li class="nav-item me-3">
                    <a class="nav-link" href="<%= request.getContextPath() %>/PromotionController?action=list">
                        <i class="fas fa-tags"></i>
                    </a>
                </li>
                <li class="nav-item me-3">
                    <a href="<%= request.getContextPath()%>/profile" class="btn btn-outline-light btn-sm">
                        <i class="fas fa-user me-1"></i>Profile
                    </a>
                </li>
                <li class="nav-item">
                    <a href="<%= request.getContextPath()%>/login" class="btn btn-primary btn-sm">
                        <i class="fas fa-sign-in-alt me-1"></i>Login
                    </a>
                </li>
            </ul>
        </div>
    </div>
</nav>
