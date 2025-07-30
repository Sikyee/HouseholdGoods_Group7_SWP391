<%-- 
    Document   : cancelOrder
    Created on : Jul 23, 2025, 7:45:48 PM
    Author     : TriTM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String orderID = request.getParameter("orderID");
    if (orderID == null) {
%>
        <div class="alert alert-danger">Invalid access. Order ID is missing.</div>
<%
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Cancel Order</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body>
<div class="container mt-5">
    <h2>Cancel Order #<%= orderID %></h2>

    <form action="cancelOrder" method="post">
        <input type="hidden" name="orderID" value="<%= orderID %>"/>

        <div class="mb-3">
            <label for="reason" class="form-label">Reason for Cancellation</label>
            <textarea class="form-control" name="reason" id="reason" rows="4" required></textarea>
        </div>

        <button type="submit" class="btn btn-danger">Submit Cancellation</button>
        <a href="order" class="btn btn-secondary ms-2">Back</a>
    </form>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
