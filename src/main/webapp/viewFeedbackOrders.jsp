<%-- 
    Document   : viewFeedbackOrders
    Created on : Jul 31, 2025, 5:54:09 PM
    Author     : thach
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, Model.OrderDetail" %>
<%@ include file="header.jsp" %>

<%
    List<OrderDetail> withoutFeedback = (List<OrderDetail>) request.getAttribute("withoutFeedback");
    List<OrderDetail> withFeedback = (List<OrderDetail>) request.getAttribute("withFeedback");
    String context = request.getContextPath();
%>

<div class="container mt-4">
    <h4>📦 My Order Feedback</h4>

    <ul class="nav nav-tabs" id="feedbackTabs" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active tab-red" id="no-feedback-tab" data-bs-toggle="tab"
                    data-bs-target="#no-feedback" type="button" role="tab">
                Not Yet Reviewed
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link tab-gray" id="with-feedback-tab" data-bs-toggle="tab"
                    data-bs-target="#with-feedback" type="button" role="tab">
                Reviewed
            </button>
        </li>
    </ul>

    <div class="tab-content mt-3">
        <!-- Orders Without Feedback -->
        <div class="tab-pane fade show active" id="no-feedback" role="tabpanel">
            <table class="table table-bordered">
                <thead class="table-danger">
                    <tr>
                        <th>Order Detail ID</th>
                        <th>Order Name</th>
                        <th>Quantity</th>
                        <th>Total Price</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                <% if (withoutFeedback != null && !withoutFeedback.isEmpty()) {
                    for (OrderDetail od : withoutFeedback) { %>
                    <tr>
                        <td><%= od.getOrderDetailID() %></td>
                        <td><%= od.getOrderName() %></td>
                        <td><%= od.getQuantity() %></td>
                        <td>$<%= od.getTotalPrice() %></td>
                        <td>
                            <a href="<%= context %>/WriteFeedback?orderDetailID=<%= od.getOrderDetailID() %>" 
                               class="btn btn-outline-danger btn-sm">Write Feedback</a>
                        </td>
                    </tr>
                <% }} else { %>
                    <tr><td colspan="5" class="text-center text-muted">No orders pending feedback</td></tr>
                <% } %>
                </tbody>
            </table>
        </div>

        <!-- Orders With Feedback -->
        <div class="tab-pane fade" id="with-feedback" role="tabpanel">
            <table class="table table-bordered">
                <thead class="table-secondary">
                    <tr>
                        <th>Order Detail ID</th>
                        <th>Order Name</th>
                        <th>Quantity</th>
                        <th>Total Price</th>
                    </tr>
                </thead>
                <tbody>
                <% if (withFeedback != null && !withFeedback.isEmpty()) {
                    for (OrderDetail od : withFeedback) { %>
                    <tr>
                        <td><%= od.getOrderDetailID() %></td>
                        <td><%= od.getOrderName() %></td>
                        <td><%= od.getQuantity() %></td>
                        <td>$<%= od.getTotalPrice() %></td>
                    </tr>
                <% }} else { %>
                    <tr><td colspan="4" class="text-center text-muted">No reviewed orders yet</td></tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<style>
    .tab-red {
        background-color: #dc3545 !important; /* đỏ */
        color: #6c757d !important;           /* xám */
        font-weight: bold;
    }
    .tab-gray {
        background-color: #6c757d !important; /* xám */
        color: #dc3545 !important;            /* đỏ */
        font-weight: bold;
    }
    .nav-link.active.tab-red {
        background-color: #6c757d !important;
        color: #dc3545 !important;
    }
    .nav-link.active.tab-gray {
        background-color: #dc3545 !important;
        color: #6c757d !important;
       
    }
</style>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
