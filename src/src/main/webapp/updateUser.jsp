<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.User" %>
<%
    User user = (User) request.getAttribute("user");
%>

<!DOCTYPE html>
<html>
    <head>
        <title>Update User</title>
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

            .btn-dark {
                background-color: #000;
                color: #fff;
            }

            .btn-dark:hover {
                background-color: #333;
            }

            label {
                font-weight: 500;
            }
        </style>
    </head>
    <body>

        <div class="container py-5">
            <div class="card p-4">
                <div class="card-body">
                    <% if (user == null) { %>
                    <h4 class="text-danger"> User not found!</h4>
                    <a href="User" class="btn btn-dark mt-3">Back</a>
                    <% } else {%>
                    <h2 class="card-title mb-4"> Update User Information</h2>

                    <form action="User?action=updateSubmit" method="post">
                        <input type="hidden" name="userID" value="<%= user.getUserID()%>">

                        <div class="mb-3">
                            <label for="fullName" class="form-label">Full Name</label>
                            <input type="text" class="form-control" id="fullName" name="fullName" required value="<%= user.getFullName()%>">
                        </div>

                        <div class="mb-3">
                            <label for="email" class="form-label">Email</label>
                            <input type="email" class="form-control" id="email" name="email" required value="<%= user.getEmail()%>">
                        </div>

                        <div class="mb-3">
                            <label for="phone" class="form-label">Phone</label>
                            <input type="text" class="form-control" id="phone" name="phone" value="<%= user.getPhone() != null ? user.getPhone() : ""%>">
                        </div>

                        <div class="mb-3">
                            <label for="dob" class="form-label">Date of Birth</label>
                            <input type="date" class="form-control" id="dob" name="dob" value="<%= user.getDob() != null ? user.getDob().toString() : ""%>">
                        </div>

                        <div class="mb-3">
                            <label for="gender" class="form-label">Gender</label>
                            <select class="form-select" id="gender" name="gender">
                                <option value="Male" <%= "Male".equals(user.getGender()) ? "selected" : ""%>>Male</option>
                                <option value="Female" <%= "Female".equals(user.getGender()) ? "selected" : ""%>>Female</option>
                                <option value="Other" <%= "Other".equals(user.getGender()) ? "selected" : ""%>>Other</option>
                            </select>
                        </div>

                        <div class="d-flex justify-content-between mt-4">
                            <a href="User" class="btn btn-secondary">← Cancel</a>
                            <button type="submit" class="btn btn-dark">💾 Save Changes</button>
                        </div>
                    </form>
                    <% }%>
                </div>
            </div>
        </div>

    </body>
</html>
