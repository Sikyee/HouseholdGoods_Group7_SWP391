<%-- 
    Document   : viewFeedbackOrders
    Created on : Jul 31, 2025, 5:54:09 PM
    Author     : thach
--%>

<%@page import="DAO.ReplyFeedbackDAO"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, Model.OrderDetail" %>
<%@ include file="header.jsp" %>

<%
    
    List<OrderDetail> withoutFeedback = (List<OrderDetail>) request.getAttribute("withoutFeedback");
    List<OrderDetail> withFeedback = (List<OrderDetail>) request.getAttribute("withFeedback");
    String context = request.getContextPath();
%>

<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center">
        <h4>📦 My Order Feedback</h4>
        <a href="<%= context %>/HomePage" class="btn btn-outline-secondary btn-sm">
            ⬅ Back to Home
        </a>
    </div>
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
                <th> ID</th>
                <th>Order ID</th>
                <th>Product ID</th>
                <th>Product Name</th>
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
                <td><%= od.getOrderID()%></td>
                <td><%= od.getProductID()%></td>
                <td><%= od.getOrderName() %></td>
                <td><%= od.getQuantity() %></td>
                <td>$<%= od.getTotalPrice() %></td>
                <td>
                    <a href="<%= context %>/WriteFeedback?orderDetailID=<%= od.getOrderDetailID() %>" 
                       class="btn btn-outline-danger btn-sm">Write Feedback</a>
                </td>
            </tr>
        <% }} else { %>
            <tr><td colspan="7" class="text-center text-muted">No orders pending feedback</td></tr>
        <% } %>
        </tbody>
    </table>

    <!-- Pagination for No Feedback -->
    <%
        Integer currentPageNoFeedback = (Integer) request.getAttribute("currentPageNoFeedback");
        Integer totalPagesNoFeedback = (Integer) request.getAttribute("totalPagesNoFeedback");
        if (currentPageNoFeedback != null && totalPagesNoFeedback != null && totalPagesNoFeedback > 1) {
    %>
    <nav>
        <ul class="pagination justify-content-center">
            <li class="page-item <%= (currentPageNoFeedback == 1) ? "disabled" : "" %>">
                <a class="page-link" href="?tab=no-feedback&page=<%= currentPageNoFeedback - 1 %>">Previous</a>
            </li>
            <% for (int i = 1; i <= totalPagesNoFeedback; i++) { %>
                <li class="page-item <%= (i == currentPageNoFeedback) ? "active" : "" %>">
                    <a class="page-link" href="?tab=no-feedback&page=<%= i %>"><%= i %></a>
                </li>
            <% } %>
            <li class="page-item <%= (currentPageNoFeedback == totalPagesNoFeedback) ? "disabled" : "" %>">
                <a class="page-link" href="?tab=no-feedback&page=<%= currentPageNoFeedback + 1 %>">Next</a>
            </li>
        </ul>
    </nav>
    <% } %>
</div>
        <!-- Orders With Feedback -->
<div class="tab-pane fade" id="with-feedback" role="tabpanel">
    <table class="table table-bordered">
        <thead class="table-secondary">
            <tr>
                <th> ID</th>
                <th>Order ID</th>
                <th>Product ID</th>
                <th>Product Name</th>
                <th>Quantity</th>
                <th>Total Price</th>
            </tr>
        </thead>
        <tbody>
        <% if (withFeedback != null && !withFeedback.isEmpty()) {
            for (OrderDetail od : withFeedback) { %>
            <tr>
                <td><%= od.getOrderDetailID() %></td>
                <td><%= od.getOrderID()%></td>
                <td><%= od.getProductID()%></td>
                <td><%= od.getOrderName() %></td>
                <td><%= od.getQuantity() %></td>
                <td>$<%= od.getTotalPrice() %></td>
                <td>
                    <%-- Icon chat --%>
                    <%
                        ReplyFeedbackDAO replyDAO = new ReplyFeedbackDAO();
                        List<Model.ReplyFeedback> replies = replyDAO.getRepliesByFeedbackID(od.getOrderDetailID());
                        boolean hasUnread = false;
                        for(Model.ReplyFeedback r : replies){
                            if(r.getUserID()!=3 && !r.getIsDeleted()){ // admin/staff reply chưa xóa
                                hasUnread = true;
                                break;
                            }
                        }
                    %>
                    <button type="button" class="btn btn-outline-primary btn-sm chat-btn" 
                            data-target="#chatModal<%= od.getOrderDetailID() %>">
                        💬
                        <% if(hasUnread){ %>
                            <span class="badge bg-danger">•</span>
                        <% } %>
                    </button>

                    <%-- Chat Modal --%>
                    <div class="modal fade" id="chatModal<%= od.getOrderDetailID() %>" tabindex="-1" role="dialog">
                      <div class="modal-dialog modal-dialog-scrollable" role="document">
                        <div class="modal-content">
                          <div class="modal-header">
                            <h5 class="modal-title">Chat for Order <%= od.getOrderID() %></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                          </div>
                          <div class="modal-body">
                            <% for(Model.ReplyFeedback r : replies){ %>
<p class="<%= r.getRoleID()==3 ? "user-msg" : "admin-msg" %>">
    <%= r.getReplyText() %>
</p>
                                   
                            <% } %>
                          </div>
                          <div class="modal-footer">
                            <form method="post" action="<%= context %>/ReplyFeedback">
                                <input type="hidden" name="feedbackID" value="<%= od.getOrderDetailID() %>" />
                                <input type="text" name="replyText" class="form-control" placeholder="Type reply..." required />
                                <button type="submit" class="btn btn-primary btn-sm">Send</button>
                            </form>
                          </div>
                        </div>
                      </div>
                    </div>

                </td>
            </tr>
        <% }} else { %>
            <tr><td colspan="7" class="text-center text-muted">No reviewed orders yet</td></tr>
        <% } %>
        </tbody>
    </table>
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
    
    .user-msg {
    text-align: right;
    background-color: #d1ffd6;
    padding: 5px 10px;
    margin: 3px 0;
    border-radius: 10px;
}

.admin-msg {
    text-align: left;
    background-color: #f1f0f0;
    padding: 5px 10px;
    margin: 3px 0;
    border-radius: 10px;
}

.badge {
    position: absolute;
    top: 0;
    right: 0;
}

</style>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    var chatButtons = document.getElementsByClassName("chat-btn");
    for(var i=0;i<chatButtons.length;i++){
        chatButtons[i].addEventListener("click", function(){
            var target = this.getAttribute("data-target");
            var modal = document.querySelector(target);
            var bsModal = new bootstrap.Modal(modal, {});
            bsModal.show();
        });
    }
</script>
