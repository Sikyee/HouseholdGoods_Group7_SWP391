<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Admin Dashboard - Account Management</title>
        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css"
              integrity="sha512-Kc323vGBEqzTmouAECnVceyQqyqdsSiqLQISBL29aUW4U/M7pSPA/gEUZQqv1cwx4OnYxTxve5UMg5GT6L4JJg=="
              crossorigin="anony
              mous" referrerpolicy="no-referrer" />
        <!-- Bootstrap -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css"
              rel="stylesheet">
        <!-- Custom CSS -->
        <link rel="stylesheet" href="./CSS/Style.css" />
        <link rel="stylesheet" href="./CSS/HeaderAndFooter_CSS.css" />
        <style>
            /* Sidebar Styles */
            .sidebar {
                width: 250px;
                background-color: #343a40;
                padding: 20px;
                height: 100%;
                position: fixed;
                top: 0;
                left: 0;
                color: white;
                display: flex;
                flex-direction: column;
                align-items: flex-start;
            }
            .sidebar h3 {
                margin-bottom: 30px;
                text-transform: uppercase;
                font-size: 18px;
            }

            .sidebar a {
                color: white;
                text-decoration: none;
                display: block;
                padding: 10px 15px;
                margin: 5px 0;
                width: 100%;
                text-align: left;
                border-radius: 5px;
            }

            .sidebar a:hover {
                background-color: #495057;
                transition: background-color 0.3s;
            }

            /* Main content */
            .content {
                margin-left: 250px; /* Space for the sidebar */
                padding: 20px;
                background-color: #f8f9fa;
                width: calc(100% - 250px);
                float: left;
            }

            /* Prevent floating issues */
            .clearfix::after {
                content: "";
                display: table;
                clear: both;
            }
        </style>
    </head>
    <body>
        <jsp:include page="admin_dashboard_header.jsp"></jsp:include>

            <div class="wrapper clearfix">
                <!-- Sidebar -->
               <div class="sidebar">
            <c:if test="${sessionScope.managerRole == 1}">
                <h3>Admin Dashboard</h3>            
            </c:if>    
            <c:if test="${sessionScope.managerRole == 2}">
                <h3>Staff Dashboard</h3>            
            </c:if> 
            <c:if test="${sessionScope.managerRole == 1}">
                <a href="CategoryController"><i class="fas fa-list"></i> Category Management</a>
            </c:if>        
            <a href="ProductController"><i class="fas fa-box"></i> Product Management</a>
            <a href="CustomerController_temp"><i class="fas fa-users"></i> Customer Management</a>
            <a href="listAdminOrders"><i class="fas fa-shopping-cart"></i> Order Management</a>
            <a href="FeedbackController"><i class="fas fa-comments"></i> Feedback Management</a>
                <c:if test="${sessionScope.managerRole == 1}">
                    <a href="VoucherController"><i class="fas fa-ticket-alt"></i> Voucher Management</a>
                </c:if>   
            <c:if test="${sessionScope.managerRole == 1}">
                <a href="RevenueController"><i class="fa-solid fa-chart-simple"></i> Revenue Management</a>
            </c:if> 
        </div>

            <!-- Main content -->
            <div class="content">
                <h1><h1>Customer detail: ${customer.userName}</h1></h1>

                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Phone Number</th>
                            <th>Username</th>
                            <th>Password</th>
                            <th>Create At</th>
                            <th>Status</th>
                                <c:if test="${sessionScope.managerRole == 1}">
                                <th>Actions</th>
                                </c:if>            

                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>${customer.customerID}</td>
                            <td>${customer.fullName}</td>                             
                            <td>${customer.email}</td>
                            <td>${customer.phone}</td>                               
                            <td>${customer.userName}</td>
                            <td>${customer.password}</td>
                            <td>${customer.registrationDate}</td>
                            <td>${customer.status}</td>
                            <c:if test="${sessionScope.managerRole == 1}">
                                <td>        
                                    <a href="/admin-edit-account?id=${customer.customerID}" class="btn btn-primary btn-sm"><i class="fas fa-edit"></i> Edit</a>
                                    <a href="/admin-delete-account?id=${customer.customerID}" class="btn btn-danger btn-sm"
                                       onclick="return confirm('Are you sure you want to delete accountID = ${customer.customerID}');"><i class="fas fa-trash"></i> Delete</a>                                                                   
                                </td>
                            </c:if>  
                        </tr>
                    </tbody>
                </table>
                <div class="mb-3">
                    <a href="/CustomerController_temp" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Back to Customer List
                    </a>
                </div>
            </div>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>



