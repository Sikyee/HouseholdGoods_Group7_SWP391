<%-- 
    Document   : manageUser
    Created on : Jul 17, 2025, 1:58:16 PM
    Author     : thong
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, Model.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Manage Staff</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                margin: 0;
                font-family: 'Segoe UI', sans-serif;
            }
            .main-content {
                margin-left: 240px;
                padding: 20px;
            }
            h1 {
                margin-top: 0;
            }
        </style>
    </head>
    <body>
        <jsp:include page="left-sidebar.jsp" />

        <div class="main-content">
            <h1>Staff Management</h1>
            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th>UserID</th>
                        <th>Full Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="user" items="${users}">
                    <tr>
                        <td>${user.userID}</td>
                        <td>${user.fullName}</td>
                        <td>${user.email}</td>
                        <td>${user.phone}</td>
                        <td>
                    <c:choose>
                        <c:when test="${user.status == 1}">Active</c:when>
                        <c:otherwise>Banned</c:otherwise>
                    </c:choose>
                    </td>
                    <td>
                    <c:choose>
                        <c:when test="${user.status == 1}">
                            <a href="User?action=ban&type=staff&id=${user.userID}" class="btn btn-warning btn-sm">Ban</a>
                        </c:when>
                        <c:otherwise>
                            <a href="User?action=unban&type=staff&id=${user.userID}" class="btn btn-success btn-sm">Unban</a>
                        </c:otherwise>
                    </c:choose>
                    <a href="User?action=delete&type=staff&id=${user.userID}" class="btn btn-danger btn-sm"
                       onclick="return confirm('Are you sure you want to delete this user?');">Delete</a>
                    </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <!-- Nút Create Account -->
        <a href="createStaff.jsp" 
           class="btn btn-primary btn-lg"
           style="position: fixed; bottom: 20px; right: 20px; border-radius: 50px;">
            + Create Account
        </a>
    </body>
</html>


