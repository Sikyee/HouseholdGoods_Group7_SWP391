<%-- 
    Document   : viewFeedbackOrders
    Created on : Jul 31, 2025, 5:54:09 PM
    Author     : thach
--%>

<%@page import="DAO.ReplyFeedbackDAO"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, Model.OrderDetail, Model.ReplyFeedback, Model.User" %>
<%@ include file="header.jsp" %>

<%
    List<OrderDetail> withoutFeedback = (List<OrderDetail>) request.getAttribute("withoutFeedback");
    List<OrderDetail> withFeedback = (List<OrderDetail>) request.getAttribute("withFeedback");
    String context = request.getContextPath();

session = request.getSession(false);
    User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
%>

<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center">
        <h4>📦 My Order Feedback</h4>
        <a href="<%= context %>/HomePage" class="btn btn-outline-secondary btn-sm">
            ⬅ Back to Home
        </a>
    </div>

    <ul class="nav nav-tabs" id="feedbackTabs" role="tablist">
        <li class="nav-item">
            <button class="nav-link <%= "without-feedback".equals(request.getAttribute("activeTab")) ? "active tab-red" : "tab-red" %>" 
                    id="without-feedback-tab" data-bs-toggle="tab" data-bs-target="#without-feedback" type="button">Not Yet Reviewed</button>
        </li>
        <li class="nav-item">
            <button class="nav-link <%= "with-feedback".equals(request.getAttribute("activeTab")) ? "active tab-gray" : "tab-gray" %>" 
                    id="with-feedback-tab" data-bs-toggle="tab" data-bs-target="#with-feedback" type="button">Reviewed</button>
        </li>
    </ul>

    <div class="tab-content mt-3">

        <!-- Orders Without Feedback -->
        <div class="tab-pane fade <%= "without-feedback".equals(request.getAttribute("activeTab")) ? "show active" : "" %>" id="without-feedback">
            <table class="table table-bordered">
                <thead class="table-danger">
                    <tr>
                        <th>ID</th><th>Order ID</th><th>Product ID</th><th>Product Name</th><th>Quantity</th><th>Total Price</th><th>Action</th>
                    </tr>
                </thead>
                <tbody>
                <% if (withoutFeedback != null && !withoutFeedback.isEmpty()) {
                    for (OrderDetail od : withoutFeedback) { %>
                        <tr>
                            <td><%= od.getOrderDetailID() %></td>
                            <td><%= od.getOrderID() %></td>
                            <td><%= od.getProductID() %></td>
                            <td><%= od.getOrderName() %></td>
                            <td><%= od.getQuantity() %></td>
                            <td>$<%= od.getTotalPrice() %></td>
                            <td>
                                <a href="<%= context %>/WriteFeedback?orderDetailID=<%= od.getOrderDetailID() %>" class="btn btn-outline-danger btn-sm">Write Feedback</a>
                            </td>
                        </tr>
                <% }} else { %>
                    <tr><td colspan="7" class="text-center text-muted">No orders pending feedback</td></tr>
                <% } %>
                </tbody>
            </table>

            <!-- Pagination for No Feedback -->
            <%
                Integer currentPageWithoutFeedback = (Integer) request.getAttribute("currentPageWithoutFeedback");
                Integer totalPagesWithoutFeedback = (Integer) request.getAttribute("totalPagesWithoutFeedback");
                if (currentPageWithoutFeedback != null && totalPagesWithoutFeedback != null && totalPagesWithoutFeedback > 1) {
            %>
            <nav>
                <ul class="pagination justify-content-center">
                    <li class="page-item <%= (currentPageWithoutFeedback == 1) ? "disabled" : "" %>">
                        <a class="page-link" href="?tab=without-feedback&page=<%= currentPageWithoutFeedback - 1 %>">Previous</a>
                    </li>
                    <% for (int i = 1; i <= totalPagesWithoutFeedback; i++) { %>
                        <li class="page-item <%= (i == currentPageWithoutFeedback) ? "active" : "" %>">
                            <a class="page-link" href="?tab=without-feedback&page=<%= i %>"><%= i %></a>
                        </li>
                    <% } %>
                    <li class="page-item <%= (currentPageWithoutFeedback == totalPagesWithoutFeedback) ? "disabled" : "" %>">
                        <a class="page-link" href="?tab=without-feedback&page=<%= currentPageWithoutFeedback + 1 %>">Next</a>
                    </li>
                </ul>
            </nav>
            <% } %>
        </div>

<!-- Orders With Feedback -->
<div class="tab-pane fade <%= "with-feedback".equals(request.getAttribute("activeTab")) ? "show active" : "" %>" id="with-feedback">
    <table class="table table-bordered">
        <thead class="table-secondary">
            <tr>
                <th>ID</th><th>Order ID</th><th>Product ID</th><th>Product Name</th><th>Quantity</th><th>Total Price</th><th>Chat</th>
            </tr>
        </thead>
        <tbody>
        <% 
        if (withFeedback != null && !withFeedback.isEmpty()) {
            ReplyFeedbackDAO replyDAO = new ReplyFeedbackDAO();
            for (OrderDetail od : withFeedback) { 
                // Lấy feedbackID tương ứng với orderDetailID
                int feedbackID = replyDAO.getFeedbackIDByOrderDetailID(od.getOrderDetailID());
                if(feedbackID != -1){
                    List<ReplyFeedback> replies = replyDAO.getRepliesByFeedbackID(feedbackID);
                    boolean hasUnread = false;
                    for(ReplyFeedback r : replies){
                        if(r.getUserID() != currentUser.getUserID() && !r.getIsDeleted()){
                            hasUnread = true; break;
                        }
                    }
        %>
            <tr>
                <td><%= od.getOrderDetailID() %></td>
                <td><%= od.getOrderID() %></td>
                <td><%= od.getProductID() %></td>
                <td><%= od.getOrderName() %></td>
                <td><%= od.getQuantity() %></td>
                <td>$<%= od.getTotalPrice() %></td>
                <td>
                    <button type="button" class="btn btn-outline-primary btn-sm chat-btn" data-target="#chatModal<%= feedbackID %>">
                        💬
                        <% if(hasUnread){ %><span class="badge bg-danger">•</span><% } %>
                    </button>

                    <!-- Chat Modal -->
                    <div class="modal fade" id="chatModal<%= feedbackID %>" tabindex="-1">
                      <div class="modal-dialog modal-dialog-scrollable">
                        <div class="modal-content">
                          <div class="modal-header">
                            <h5 class="modal-title">Chat for Order <%= od.getOrderID() %></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                          </div>
                          <div class="modal-body">
                            <% for(ReplyFeedback r : replies){ %>
                                <p class="<%= (r.getUserID() == currentUser.getUserID()) ? "user-msg" : "admin-msg" %>">
                                    <strong><%= r.getUserName() %>:</strong> <%= r.getReplyText() %>
                                </p>
                            <% } %>
                          </div>
                          <div class="modal-footer">
                            <!-- FORM CHUNG CHO CẢ 2 BÊN -->
                            <form method="post" action="<%= context %>/ReplyFeedback" class="d-flex w-100">
                                <input type="hidden" name="feedbackID" value="<%= feedbackID %>">
                                <input type="text" name="replyText" class="form-control me-2" placeholder="Type reply..." required>
                                <button type="submit" class="btn btn-primary btn-sm">Send</button>
                            </form>
                          </div>
                        </div>
                      </div>
                    </div>

                </td>
            </tr>
        <% 
                } // feedbackID != -1
            } // for OrderDetail
        } else { %>
            <tr><td colspan="7" class="text-center text-muted">No reviewed orders yet</td></tr>
        <% } %>
        </tbody>
    </table>


            <!-- Pagination for With Feedback -->
            <%
                Integer currentPageWithFeedback = (Integer) request.getAttribute("currentPageWithFeedback");
                Integer totalPagesWithFeedback = (Integer) request.getAttribute("totalPagesWithFeedback");
                if (currentPageWithFeedback != null && totalPagesWithFeedback != null && totalPagesWithFeedback > 1) {
            %>
            <nav>
                <ul class="pagination justify-content-center">
                    <li class="page-item <%= (currentPageWithFeedback == 1) ? "disabled" : "" %>">
                        <a class="page-link" href="?tab=with-feedback&page=<%= currentPageWithFeedback - 1 %>">Previous</a>
                    </li>
                    <% for (int i=1; i<=totalPagesWithFeedback; i++){ %>
                        <li class="page-item <%= (i==currentPageWithFeedback)?"active":"" %>">
                            <a class="page-link" href="?tab=with-feedback&page=<%= i %>"><%= i %></a>
                        </li>
                    <% } %>
                    <li class="page-item <%= (currentPageWithFeedback==totalPagesWithFeedback)?"disabled":"" %>">
                        <a class="page-link" href="?tab=with-feedback&page=<%= currentPageWithFeedback + 1 %>">Next</a>
                    </li>
                </ul>
            </nav>
            <% } %>
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
    
.badge-new {
    background-color: red;
    color: white;
    font-size: 10px;
    border-radius: 50%;
    padding: 2px 6px;
    position: absolute;
    top: -5px;
    right: -5px;
}

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
