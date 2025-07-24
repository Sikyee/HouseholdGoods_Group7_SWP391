<%-- 
    Document   : writeFeedback
    Created on : Jul 3, 2025, 10:15:29 AM
    Author     : TriTN
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, Model.OrderDetail" %>
<%@ include file="left-sidebar.jsp" %>

<%
    List<OrderDetail> orders = (List<OrderDetail>) request.getAttribute("orders");
    String context = request.getContextPath();
%>

<div class="content" style="margin-left: 220px; padding: 2rem;">
    <h4>📝 Write Feedback</h4>

    <% if (orders == null || orders.isEmpty()) { %>
        <p>No order found to write feedback.</p>
    <% } else { %>
        <div class="row">
            <% for (OrderDetail od : orders) { %>
                <div class="col-md-4 mb-4">
                    <div class="card h-100">
                        <div class="card-body">
                            <h5 class="card-title"><%= od.getOrderName() %></h5>
                            <p class="card-text">Quantity: <%= od.getQuantity() %></p>
                            <p class="card-text">Total: <%= od.getTotalPrice() %> VND</p>
                            <button class="btn btn-primary btn-sm" data-bs-toggle="modal"
                                    data-bs-target="#feedbackModal<%= od.getOrderDetailID() %>">
                                Write Feedback
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Modal -->
                <div class="modal fade" id="feedbackModal<%= od.getOrderDetailID() %>" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content text-start">
                            <form method="post" action="<%= context %>/WriteFeedback">
                                <div class="modal-header">
                                    <h5 class="modal-title">Feedback for: <%= od.getOrderName() %></h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <div class="modal-body">
                                    <input type="hidden" name="orderDetailID" value="<%= od.getOrderDetailID() %>">
                                    <div class="mb-3">
                                        <label for="rating" class="form-label">Rating</label>
                                        <select name="rating" class="form-select" required>
                                            <option value="">Choose...</option>
                                            <option value="1">★☆☆☆☆</option>
                                            <option value="2">★★☆☆☆</option>
                                            <option value="3">★★★☆☆</option>
                                            <option value="4">★★★★☆</option>
                                            <option value="5">★★★★★</option>
                                        </select>
                                    </div>
                                    <div class="mb-3">
                                        <label for="comment" class="form-label">Comment</label>
                                        <textarea name="comment" class="form-control" rows="3" required></textarea>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="submit" class="btn btn-success">Send Feedback</button>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            <% } %>
        </div>
    <% } %>
</div>

<!-- Bootstrap -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
