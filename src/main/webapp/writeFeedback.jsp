<%-- 
    Document   : writeFeedback
    Created on : Jul 3, 2025, 10:15:29 AM
    Author     : TriTN
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.OrderDetail, Model.Feedback" %>
<%@ include file="left-sidebar.jsp" %>

<%
    OrderDetail od = (OrderDetail) request.getAttribute("orderDetail");
    String context = request.getContextPath();
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    Boolean alreadyFeedback = (Boolean) request.getAttribute("alreadyFeedback");
    Feedback existingFeedback = (Feedback) request.getAttribute("existingFeedback");

    String selectedRating = request.getParameter("rating");
    String commentText = request.getParameter("comment");
%>

<div class="content" style="margin-left: 220px; padding: 2rem;">
    <h4>📝 Write Feedback for: <%= od != null ? od.getOrderName() : "" %></h4>

    <% if (error != null) { %>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <%= error %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <% if (success != null) { %>
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <%= success %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <% if (alreadyFeedback != null && alreadyFeedback && existingFeedback != null) { %>
        <div class="alert alert-info" role="alert">
            ✅ You have already submitted feedback for this item.
        </div>
        <div class="card mb-3">
            <div class="card-body">
                <h5>Your Feedback</h5>
                <p><strong>Rating:</strong> <%= existingFeedback.getRating() %> ★</p>
                <p><strong>Comment:</strong> <%= existingFeedback.getComment() %></p>
            </div>
        </div>
        <button type="button" class="btn btn-secondary" onclick="history.back()">Back</button>

    <% } else { %>
        <form method="post" action="<%= context %>/WriteFeedback" novalidate>
            <input type="hidden" name="orderDetailID" value="<%= od != null ? od.getOrderDetailID() : "" %>">

            <div class="mb-3">
                <label for="rating" class="form-label">Rating</label>
                <select name="rating" class="form-select">
                    <option value="" <%= (selectedRating == null || selectedRating.isEmpty()) ? "selected" : "" %>>Choose...</option>
                    <option value="1" <%= "1".equals(selectedRating) ? "selected" : "" %>>★☆☆☆☆</option>
                    <option value="2" <%= "2".equals(selectedRating) ? "selected" : "" %>>★★☆☆☆</option>
                    <option value="3" <%= "3".equals(selectedRating) ? "selected" : "" %>>★★★☆☆</option>
                    <option value="4" <%= "4".equals(selectedRating) ? "selected" : "" %>>★★★★☆</option>
                    <option value="5" <%= "5".equals(selectedRating) ? "selected" : "" %>>★★★★★</option>
                </select>
            </div>

            <div class="mb-3">
                <label for="comment" class="form-label">Comment</label>
                <textarea name="comment" class="form-control" rows="3"><%= commentText != null ? commentText : "" %></textarea>
            </div>

            <button type="submit" class="btn btn-success">Send Feedback</button>
           <button type="button" class="btn btn-secondary" onclick="history.back()">Cancel</button>
        </form>
    <% } %>
</div>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
