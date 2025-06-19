<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.User" %>
<%
    User user = (User) request.getAttribute("user");
%>

<!DOCTYPE html>
<html>
    <head>
        <title>View User</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background-color: #ffffff;
                color: #000000;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }

            .card {
                background-color: #f8f9fa;
                border-radius: 12px;
                border: 1px solid #dee2e6;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
            }

            .card-title {
                font-size: 1.8rem;
                font-weight: bold;
            }

            .table th, .table td {
                vertical-align: middle;
            }

            .btn-back {
                background-color: #000;
                color: #fff;
            }

            .btn-back:hover {
                background-color: #333;
            }
        </style>
    </head>
    <body>

        <div class="container py-5">
            <div class="card p-4">
                <div class="card-body">
                    <% if (user == null) { %>
                    <h4 class="text-danger"> User not found!</h4>
                    <a href="User" class="btn btn-back mt-3">Back</a>
                    <% } else {%>
                    <h2 class="card-title mb-4"> View User Detail</h2>

                    <table class="table table-bordered">
                        <tr><th>ID</th><td><%= user.getUserID()%></td></tr>
                        <tr><th>Username</th><td><%= user.getUserName()%></td></tr>
                        <tr><th>Full Name</th><td><%= user.getFullName()%></td></tr>
                        <tr><th>Email</th><td><%= user.getEmail()%></td></tr>
                        <tr><th>Phone</th><td><%= user.getPhone() != null ? user.getPhone() : "N/A"%></td></tr>
                        <tr><th>DOB</th><td><%= user.getDob() != null ? user.getDob().toString() : "N/A"%></td></tr>
                        <tr><th>Gender</th><td><%= user.getGender() != null ? user.getGender() : "N/A"%></td></tr>
                        <tr><th>Status</th><td><%= user.getStatus() == 1 ? "Active" : "Banned"%></td></tr>
                        <tr>
                            <th>Role</th>
                            <td>
                                <%
                                    int roleID = user.getRoleID();
                                    String role = (roleID == 1) ? "Admin" : (roleID == 2) ? "Staff" : "Customer";
                                %>
                                <%= role%>
                            </td>
                        </tr>
                    </table>

                    <a href="User" class="btn btn-back mt-3">← Back to User List</a>
                    <% }%>
                </div>
            </div>
        </div>

    </body>
</html>
