<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ include file="left-sidebar.jsp" %>

<%
    List<User> users = (List<User>) request.getAttribute("users");
    String contextPath = request.getContextPath();
    String keyword = request.getAttribute("keyword") != null ? (String) request.getAttribute("keyword") : "";
%>

<style>
    th.action-header {
        min-width: 220px;
        background-color: #000;
        color: #fff;
        text-align: center;
    }

    td.action-cell {
        white-space: nowrap;
        text-align: center;
        vertical-align: middle;
    }

    thead.table-header-black th {
        background-color: #000;
        color: #fff;
        text-align: center;
        vertical-align: middle;
    }
</style>

<div class="content" style="margin-left: 220px; padding: 2rem;">
    <h4>Manage Accounts</h4>

    <div class="d-flex justify-content-end mb-3">
        <form action="<%= contextPath%>/User" method="get" class="d-flex" role="search">
            <input type="hidden" name="action" value="search">
            <input class="form-control me-2" type="search" name="keyword"
                   placeholder="Search by name or email" aria-label="Search"
                   value="<%= keyword%>" style="max-width: 300px;">
            <button class="btn btn-dark" type="submit">Search</button>
        </form>
    </div>

    <div class="table-responsive mt-3">
        <table class="table table-bordered table-striped align-middle text-center">
            <thead class="table-header-black">
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Role</th>
                    <th>Status</th>
                    <th class="action-header">Action</th>
                </tr>
            </thead>
            <tbody>
                <%
                    if (users != null && !users.isEmpty()) {
                        for (User u : users) {
                            if (u.getRoleID() == 2 || u.getRoleID() == 3) {
                                String role = (u.getRoleID() == 2) ? "Staff" : "Customer";
                %>
                <tr>
                    <td><%= u.getUserID()%></td>
                    <td><%= u.getFullName()%></td>
                    <td><%= u.getEmail()%></td>
                    <td><%= u.getPhone()%></td>
                    <td><%= role%></td>
                    <td><%= u.getStatus() == 1 ? "Active" : "Banned"%></td>
                    <td class="action-cell">
                        <a href="<%= contextPath%>/User?action=view&id=<%= u.getUserID()%>" class="btn btn-secondary btn-sm">View</a>
                        <a href="<%= contextPath%>/User?action=update&id=<%= u.getUserID()%>" class="btn btn-secondary btn-sm">Update</a>
                        <% if (u.getStatus() == 1) {%>
                        <a href="<%= contextPath%>/User?action=ban&id=<%= u.getUserID()%>" class="btn btn-warning btn-sm" onclick="return confirm('Ban this user?');">Ban</a>
                        <% } else {%>
                        <a href="<%= contextPath%>/User?action=unban&id=<%= u.getUserID()%>" class="btn btn-success btn-sm" onclick="return confirm('Unban this user?');">Unban</a>
                        <% }%>
                        <a href="<%= contextPath%>/User?action=delete&id=<%= u.getUserID()%>" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to DELETE this user?');">Delete</a>
                    </td>
                </tr>
                <%
                        }
                    }
                } else {
                %>
                <tr>
                    <td colspan="7" class="text-center">No users found.</td>
                </tr>
                <% }%>
            </tbody>
        </table>
    </div>
</div>
