<%-- 
    Document   : manageActor
    Created on : Jul 12, 2025, 7:51:37 PM
    Author     : Le Quang Giang - CE182527 
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, Model.Actor" %>
<%@ include file="left-sidebar.jsp" %>

<%
    List<Actor> actors = (List<Actor>) request.getAttribute("actors");
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
    <h4> Manage Actors</h4>

    <!-- 🔍 Search Form aligned to the right -->
    <div class="d-flex justify-content-end mb-3">
        <form action="<%= contextPath %>/Actor" method="get" class="d-flex" role="search">
            <input type="hidden" name="action" value="search">
            <input class="form-control me-2" type="search" name="keyword"
                   placeholder="Search by name or email" aria-label="Search"
                   value="<%= keyword %>" style="max-width: 300px;">
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
                    <th>Status</th>
                    <th class="action-header">Action</th>
                </tr>
            </thead>
            <tbody>
                <%
                    if (actors != null && !actors.isEmpty()) {
                        for (Actor a : actors) {
                %>
                <tr>
                    <td><%= a.getActorID()%></td>
                    <td><%= a.getFullName()%></td>
                    <td><%= a.getEmail()%></td>
                    <td><%= a.getPhone()%></td>
                    <td><%= a.getStatus() == 1 ? "Active" : "Banned" %></td>
                    <td class="action-cell">
                        <a href="<%= contextPath %>/Actor?action=view&id=<%= a.getActorID() %>" class="btn btn-secondary btn-sm">View</a>
                        <a href="<%= contextPath %>/Actor?action=update&id=<%= a.getActorID() %>" class="btn btn-secondary btn-sm">Update</a>
                        <% if (a.getStatus() == 1) { %>
                            <a href="<%= contextPath %>/Actor?action=ban&id=<%= a.getActorID() %>" class="btn btn-warning btn-sm" onclick="return confirm('Ban this actor?');">Ban</a>
                        <% } else { %>
                            <a href="<%= contextPath %>/Actor?action=unban&id=<%= a.getActorID() %>" class="btn btn-success btn-sm" onclick="return confirm('Unban this actor?');">Unban</a>
                        <% } %>
                        <a href="<%= contextPath %>/Actor?action=delete&id=<%= a.getActorID() %>" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to DELETE this actor?');">Delete</a>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="6" class="text-center">No actors found.</td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</div>

