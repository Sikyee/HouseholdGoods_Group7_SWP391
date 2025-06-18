<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, Model.User" %>
<%@ include file="left-sidebar.jsp" %>

<%
    List<User> users = (List<User>) request.getAttribute("users");
    String context = request.getContextPath();
%>

<!-- Main content, thêm margin-left tránh bị sidebar che -->
<div class="content" style="margin-left: 220px; padding: 2rem;">
    <h4>👥 Manage Accounts</h4>

    <div class="table-responsive mt-3">
        <table class="table table-bordered table-striped align-middle text-center">
            <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Role</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
            <tbody>
                <%
                    if (users != null && !users.isEmpty()) {
                        for (User u : users) {
                            if (u.getRoleID() == 2 || u.getRoleID() == 3) { // 🔧 Chỉ hiển thị Staff & Customer
                                String role = (u.getRoleID() == 2) ? "Staff" : "Customer";
                %>
                <tr>
                    <td><%= u.getUserID()%></td>
                    <td><%= u.getFullName()%></td>
                    <td><%= u.getEmail()%></td>
                    <td><%= u.getPhone()%></td>
                    <td><%= role%></td>
                    <td><%= u.getStatus() == 1 ? "Active" : "Banned"%></td>
                    <td>
                        <% if (u.getStatus() == 1) {%>
                        <a href="<%= context%>/User?action=ban&id=<%= u.getUserID()%>" class="btn btn-danger btn-sm">Ban</a>
                        <% } else {%>
                        <a href="<%= context%>/User?action=unban&id=<%= u.getUserID()%>" class="btn btn-success btn-sm">Unban</a>
                        <% } %>
                    </td>
                </tr>
                <%
                        } // end if roleID
                    } // end for
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
