<%-- 
    Document   : manageFeedback
    Created on : Jun 16, 2025, 10:41:04 PM
    Author     : TriTN
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, Model.Feedback, Model.ReplyFeedback, DAO.ReplyFeedbackDAO" %>
<%@ include file="left-sidebar.jsp" %>

<%
    List<Feedback> feedbacks = (List<Feedback>) request.getAttribute("feedbacks");
    int currentPage = (Integer) request.getAttribute("currentPage");
    int totalPages = (Integer) request.getAttribute("totalPages");

    String context = request.getContextPath();
    String keyword = request.getParameter("keyword") == null ? "" : request.getParameter("keyword");
    String statusFilter = request.getParameter("status") == null ? "" : request.getParameter("status");
    String dateFilter = request.getParameter("date") == null ? "" : request.getParameter("date");
    String userNameFilter = request.getParameter("userName") == null ? "" : request.getParameter("userName");

    ReplyFeedbackDAO replyDAO = new ReplyFeedbackDAO();
    Model.User currentUser = (Model.User) session.getAttribute("user");
%>

<div class="content" style="margin-left: 220px; padding: 2rem;">
    <h4>📝 Manage Feedback</h4>

    <!-- Filter Form -->
    <form method="get" action="<%= context%>/Feedback" class="row g-3 mb-4">
        <div class="col-md-3">
            <input type="text" name="userName" class="form-control" placeholder="User Name" value="<%= userNameFilter%>">
        </div>
        <div class="col-md-2">
            <input type="date" name="date" class="form-control" value="<%= dateFilter%>">
        </div>
        <div class="col-md-2">
            <select name="status" class="form-select">
                <option value="">All Status</option>
                <option value="Pending" <%= "Pending".equals(statusFilter) ? "selected" : ""%>>Pending</option>
                <option value="Resolved" <%= "Resolved".equals(statusFilter) ? "selected" : ""%>>Resolved</option>
                <option value="Ignored" <%= "Ignored".equals(statusFilter) ? "selected" : ""%>>Ignored</option>
            </select>
        </div>
        <div class="col-md-3">
            <input type="text" name="keyword" class="form-control" placeholder="Search comment..." value="<%= keyword%>">
        </div>
        <div class="col-md-2 d-flex gap-1">
            <button type="submit" class="btn btn-primary w-100">Search</button>
            <a href="<%= context%>/Feedback?action=export" class="btn btn-success w-100">Export</a>
        </div>
    </form>

    <!-- Feedback Table -->
    <div class="table-responsive">
        <table class="table table-bordered text-center align-middle">
            <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Order ID</th>
                    <th>Customer ID</th>
                    <th>Customer Name</th>
                    <th>Product</th>
                    <th>Image</th>
                    <th>Rating</th>
                    <th style="width: 300px;">Comment</th>
                    <th>Created At</th>
                    <th>Status</th>
                    <th style="width: 250px;">Action</th>
                </tr>
            </thead>
            <tbody>
                <% if (feedbacks != null && !feedbacks.isEmpty()) {
                    for (Feedback fb : feedbacks) {
                        List<ReplyFeedback> replies = replyDAO.getRepliesByFeedbackID(fb.getFeedbackID());
                        String comment = fb.getComment();
                        String[] wordsArr = comment.split("\\s+");
                        String shortText = String.join(" ", Arrays.copyOfRange(wordsArr, 0, Math.min(15, wordsArr.length)));
                        boolean isLong = wordsArr.length > 15;
                %>
                <tr id="feedbackRow<%= fb.getFeedbackID() %>">
                    <td><%= fb.getFeedbackID()%></td>
                    <td><%= fb.getOrderDetailID()%></td>
                    <td><%= fb.getUserID()%></td>
                    <td><%= fb.getUserName()%></td>
                    <td><%= fb.getProductName()%></td>
                    <td>
                        <img src="images/<%= fb.getImage() %>" alt="Product" width="25">
                    </td>
                    <td>
                        <%
                            int rating = fb.getRating();
                            for (int i = 1; i <= 5; i++) {
                                if (i <= rating) { %>
                                    <span class="star filled">★</span>
                                <% } else { %>
                                    ☆
                                <% }
                            }
                        %>
                    </td>
                    <td class="text-start">
                        <div class="comment-box">
                            <span class="short-text">
                                <%= shortText%>
                                <% if (isLong) { %>
                                <a href="javascript:void(0)" class="toggle-link text-primary ms-1">Show more</a>
                                <% } %>
                            </span>
                            <% if (isLong) {%>
                            <span class="full-text d-none">
                                <%= fb.getComment()%>
                                <a href="javascript:void(0)" class="toggle-link text-primary ms-1">Show less</a>
                            </span>
                            <% }%>
                        </div>
                    </td>
                    <td><%= fb.getCreatedAt()%></td>
                    <td>
                        <span class="badge
                              <%= "Pending".equals(fb.getStatus()) ? "bg-secondary"
                                      : "Processing".equals(fb.getStatus()) ? "bg-info"
                                      : "Resolved".equals(fb.getStatus()) ? "bg-success" : "bg-dark"%>">
                            <%= fb.getStatus()%>
                        </span>
                    </td>
                    <td>
                        <a href="<%= context%>/Feedback?action=delete&id=<%= fb.getFeedbackID()%>" 
                           class="btn btn-danger btn-sm" 
                           onclick="return confirm('Are you sure to delete this feedback?')">Delete</a>

                        <% if ("Ignored".equals(fb.getStatus())) { %>
                            <button class="btn btn-secondary btn-sm mt-1" disabled>Reply</button>
                            <a href="<%= context%>/Feedback?action=restore&id=<%= fb.getFeedbackID()%>" 
                               class="btn btn-warning btn-sm mt-1">Restore</a>
                        <% } else { %>
                            <button class="btn btn-success btn-sm mt-1 reply-btn" 
                                    data-bs-toggle="modal" 
                                    data-bs-target="#replyModal<%= fb.getFeedbackID()%>">
                                Reply
                            </button>
                            <a href="<%= context%>/Feedback?action=report&id=<%= fb.getFeedbackID()%>" 
                               class="btn btn-dark btn-sm mt-1">Report</a>
                        <% } %>
                    </td>
                </tr>

                <!-- Reply Modal -->
<div class="modal fade" id="replyModal<%= fb.getFeedbackID()%>" tabindex="-1">
    <div class="modal-dialog modal-dialog-scrollable modal-lg">
        <div class="modal-content text-start">
            <div class="modal-header">
                <h5 class="modal-title">Chat for Feedback #<%= fb.getFeedbackID() %></h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body" id="chatBody<%= fb.getFeedbackID() %>">
                <p class="admin-msg"><strong><%= fb.getUserName() %>:</strong> <%= fb.getComment() %></p>
                <hr>
                <% for (ReplyFeedback r : replies) { %>
                    <p class="<%= r.getUserID() == currentUser.getUserID() ? "user-msg" : "admin-msg" %>">
                        <strong><%= r.getUserName() %>:</strong> <%= r.getReplyText() %>
                    </p>
                <% } %>
            </div>
            <div class="modal-footer d-flex">
                <input type="text" class="form-control me-2" id="replyInput<%= fb.getFeedbackID() %>" placeholder="Type reply...">
                <button type="button" class="btn btn-primary btn-sm" onclick="sendReply(<%= fb.getFeedbackID() %>)">Send</button>
            </div>
        </div>
    </div>
</div>

                <% } } else { %>
                <tr><td colspan="11">No feedback found.</td></tr>
                <% } %>
            </tbody>
        </table>

        <!-- Pagination -->
        <%
            String baseLink = context + "/Feedback?";
            StringBuilder query = new StringBuilder();
            if (!userNameFilter.isEmpty()) query.append("userName=").append(userNameFilter).append("&");
            if (!keyword.isEmpty()) query.append("keyword=").append(keyword).append("&");
            if (!statusFilter.isEmpty()) query.append("status=").append(statusFilter).append("&");
            if (!dateFilter.isEmpty()) query.append("date=").append(dateFilter).append("&");
            baseLink += query.toString();
        %>

        <nav class="mt-4">
            <ul class="pagination justify-content-center">
                <li class="page-item <%= currentPage == 1 ? "disabled" : ""%>">
                    <a class="page-link" href="<%= baseLink%>page=<%= currentPage - 1%>">Previous</a>
                </li>
                <% for (int i = 1; i <= totalPages; i++) { %>
                    <li class="page-item <%= i == currentPage ? "active" : ""%>">
                        <a class="page-link" href="<%= baseLink%>page=<%= i%>"><%= i%></a>
                    </li>
                <% } %>
                <li class="page-item <%= currentPage == totalPages ? "disabled" : ""%>">
                    <a class="page-link" href="<%= baseLink%>page=<%= currentPage + 1%>">Next</a>
                </li>
            </ul>
        </nav>
    </div>
</div>

<style>
.comment-box { max-width: 100%; word-wrap: break-word; }
.star.filled { color: gold; }
.user-msg { text-align: right; background-color: #d1ffd6; padding: 5px 10px; margin: 3px 0; border-radius: 10px; }
.admin-msg { text-align: left; background-color: #f1f0f0; padding: 5px 10px; margin: 3px 0; border-radius: 10px; }
</style>

<script>
document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".toggle-link").forEach(function (link) {
        link.addEventListener("click", function () {
            const td = this.closest("td");
            const shortText = td.querySelector(".short-text");
            const fullText = td.querySelector(".full-text");
            shortText.classList.toggle("d-none");
            fullText.classList.toggle("d-none");
        });
    });
});

function sendReply(feedbackID) {
    const input = document.getElementById("replyInput" + feedbackID);
    const text = input.value.trim();
    if (!text) return;

    fetch('<%= request.getContextPath() %>/ReplyFeedback', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: 'action=insert&feedbackID=' + feedbackID + '&replyText=' + encodeURIComponent(text)
    })
    .then(res => res.json())
    .then(data => {
        const chatBody = document.getElementById("chatBody" + feedbackID);
        const p = document.createElement("p");
        p.className = "user-msg";
        p.innerHTML = "<strong>" + data.userName + ":</strong> " + data.replyText;
        chatBody.appendChild(p);
        input.value = "";
        chatBody.scrollTop = chatBody.scrollHeight;

        // Update badge ngay khi reply
        const badge = document.querySelector("#feedbackRow" + feedbackID + " .badge");
        if (badge) {
            badge.textContent = data.userRole === "customer" ? "Customer Replied" : "Resolved";
            badge.className = "badge bg-success";
        }
    })
    .catch(err => { console.error(err); alert("Error sending reply."); });
}
</script>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
