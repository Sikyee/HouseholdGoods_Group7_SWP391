<%-- 
    Document   : manageOrder
    Created on : Jul 23, 2025, 6:58:22 PM
    Author     : TriTM
--%>
<%@page import="Model.User"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, Model.OrderInfo, Model.OrderStatus, Model.User" %>
<%@ include file="left-sidebar.jsp"%>

<%
    List<OrderInfo> orderList = (List<OrderInfo>) request.getAttribute("orderList");
    List<OrderStatus> orderStatusList = (List<OrderStatus>) request.getAttribute("orderStatusList");
    User user = (User) session.getAttribute("user");
    int userRole = user != null ? user.getRoleID() : 4; // 4 = Guest
%>
    
<div class="content" style="margin-left: 220px; padding: 2rem;">
    <h4>Manage Order</h4>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
    </head>
    <body>
        <div class="container mt-5">

            <!-- Tab navigation -->
            <!-- Tab navigation -->
            <ul class="nav nav-tabs" id="orderTab" role="tablist">
                <%
                    if (orderStatusList != null && !orderStatusList.isEmpty()) {
                        for (OrderStatus status : orderStatusList) {
                %>
                <li class="nav-item" role="presentation">
                    <button class="nav-link <%= (status.getOrderStatusID() == 1) ? "active" : ""%>" 
                            id="tab-<%= status.getOrderStatusID()%>" 
                            data-bs-toggle="tab" 
                            data-bs-target="#status-<%= status.getOrderStatusID()%>" 
                            type="button" 
                            role="tab">
                        <%= status.getStatusName()%>
                    </button>
                </li>
                <%
                    }
                } else {
                %>
                <li class="nav-item">
                    <span class="nav-link disabled">No status available</span>
                </li>
                <% } %>
            </ul>


            <!-- Tab content -->
            <div class="tab-content" id="orderTabContent">
                <%
                    for (OrderStatus status : orderStatusList) {
                        int statusID = status.getOrderStatusID();
                %>
                <div class="tab-pane fade <%= (statusID == 1) ? "show active" : ""%>" 
                     id="status-<%= statusID%>" role="tabpanel">
                    <div class="table-responsive mt-3">
                        <table class="table table-bordered table-hover">
                            <thead class="table-light">
                                <tr>
                                    <th>Order ID</th>
                                    <th>Order Date</th>
                                    <th>Full Name</th>
                                    <th>Phone</th>
                                    <th>Total Price</th>
                                    <th>Final Price</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    boolean hasOrder = false;
                                    for (OrderInfo order : orderList) {
                                        if (order.getOrderStatusID() == statusID) {
                                            hasOrder = true;
                                %>
                                <tr>
                                    <td>#<%= order.getOrderID()%></td>
                                    <td><%= order.getOrderDate()%></td>
                                    <td><%= order.getFullName()%></td>
                                    <td><%= order.getPhone()%></td>
                                    <td><%= order.getTotalPrice()%></td>
                                    <td><%= order.getFinalPrice()%></td>
                                    <td>
                                        <a href="order-detail?orderID=<%= order.getOrderID()%>" class="btn btn-sm btn-primary">View</a>


                                    </td>
                                </tr>
                                <%
                                        }
                                    }
                                    if (!hasOrder) {
                                %>
                                <tr><td colspan="7" class="text-center">No orders in this status.</td></tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
                <% }%>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
