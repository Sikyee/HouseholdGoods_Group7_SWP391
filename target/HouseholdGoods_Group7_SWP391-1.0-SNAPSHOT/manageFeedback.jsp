<%-- 
    Document   : manageFeedback
    Created on : Jun 16, 2025, 10:41:04 PM
    Author     : thach
--%>

<%@page import="java.sql.Connection"%>
<%@ page import="Model.Feedback" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="dao.FeedbackDAO" %>
<%
    int customerId = (int) session.getAttribute("customerId");
    FeedbackDAO dao = new FeedbackDAO((Connection)application.getAttribute("conn"));
    ArrayList<Feedback> list = dao.getAllFeedbackByCustomerId(customerId);
%>

<html>
<head>
    <title>Manage Feedback</title>
</head>
<body>
    <h2>Your Feedback</h2>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Content</th>
            <th>Reply</th>
            <th>Action</th>
        </tr>
        <%
            for (Feedback fb : list) {
        %>
        <tr>
            <td><%= fb.getId() %></td>
            <td><%= fb.getContent() %></td>
            <td><%= fb.getReply() == null ? "No reply" : fb.getReply() %></td>
            <td>
                <a href="feedback?action=view&id=<%= fb.getId() %>">View</a> |
                <a href="feedback?action=edit&id=<%= fb.getId() %>">Edit</a> |
                <a href="feedback?action=delete&id=<%= fb.getId() %>" onclick="return confirm('Delete?')">Delete</a>
            </td>
        </tr>
        <%
            }
        %>
    </table>
</body>
</html>

