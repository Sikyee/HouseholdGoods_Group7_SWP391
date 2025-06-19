<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, Model.User" %>
<%@ include file="left-sidebar.jsp" %>

<%
    List<User> users = (List<User>) request.getAttribute("users");
    String context = request.getContextPath();
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
</style>

<style>
    thead.table-header-black th {
        background-color: #000;
        color: #fff;
        text-align: center;
        vertical-align: middle;
    }

    td.action-cell {
        white-space: nowrap;
        text-align: center;
        vertical-align: middle;
    }
</style>
<div class="content" style="margin-left: 220px; padding: 2rem;">
    <h4>👥 Manage Accounts</h4>

    <div class="table-responsive mt-3">
        <table class="table table-bordered table-striped align-middle text-center">

            <thead class="table-header-black">
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
                        <a href="<%= context%>/User?action=view&id=<%= u.getUserID()%>" class="btn btn-dark btn-sm">View</a>
                        <a href="<%= context%>/User?action=update&id=<%= u.getUserID()%>" class="btn btn-dark btn-sm">Update</a>
                        <% if (u.getStatus() == 1) {%>
                        <a href="<%= context%>/User?action=ban&id=<%= u.getUserID()%>" class="btn btn-dark btn-sm">Ban</a>
                        <% } else {%>
                        <a href="<%= context%>/User?action=unban&id=<%= u.getUserID()%>" class="btn btn-dark btn-sm">Unban</a>
                        <% } %>
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
