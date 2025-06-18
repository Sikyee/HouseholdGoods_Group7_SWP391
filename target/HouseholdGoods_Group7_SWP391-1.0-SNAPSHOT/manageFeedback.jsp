<%-- 
    Document   : manageFeedback
    Created on : Jun 16, 2025, 10:41:04 PM
    Author     : thach
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, Model.Feedback, Model.ReplyFeedback, DAO.ReplyFeedbackDAO" %>
<%@ include file="left-sidebar.jsp" %>

<%
    List<Feedback> feedbacks = (List<Feedback>) request.getAttribute("feedbacks");
    String context = request.getContextPath();
    String keyword = request.getParameter("keyword") == null ? "" : request.getParameter("keyword");
    String statusFilter = request.getParameter("status") == null ? "" : request.getParameter("status");
    String dateFilter = request.getParameter("date") == null ? "" : request.getParameter("date");
    String userNameFilter = request.getParameter("userName") == null ? "" : request.getParameter("userName");
    ReplyFeedbackDAO replyDAO = new ReplyFeedbackDAO();
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
                <option value="Processing" <%= "Processing".equals(statusFilter) ? "selected" : ""%>>Processing</option>
                <option value="Resolved" <%= "Resolved".equals(statusFilter) ? "selected" : ""%>>Resolved</option>
                <option value="Ignored" <%= "Ignored".equals(statusFilter) ? "selected" : ""%>>Ignored</option>
            </select>
        </div>
        <div class="col-md-3">
            <input type="text" name="keyword" class="form-control" placeholder="Search comment..." value="<%= keyword%>">
        </div>
        <div class="col-md-2">
            <button type="submit" class="btn btn-primary w-100">Search</button>
        </div>
    </form>

    <!-- Feedback Table -->
    <div class="table-responsive">
        <table class="table table-bordered text-center align-middle">
            <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>UserID</th>
                    <th>User</th>
                    <th>OrderDetail ID</th>
                    <th>Rating</th>
                    <th style="width: 220px;">Comment</th>
                    <th>Created At</th>
                    <th>Status</th>
                    <th style="width: 240px;">Action</th>
                </tr>
            </thead>
            <tbody>
                <%
                    if (feedbacks != null && !feedbacks.isEmpty()) {
                        for (Feedback fb : feedbacks) {
                            List<ReplyFeedback> replies = replyDAO.getRepliesByFeedbackID(fb.getFeedbackID());
                %>
                <tr>
                    <td><%= fb.getFeedbackID()%></td>
                    <td><%= fb.getUserID()%></td>
                    <td><%= fb.getUserName()%></td>
                    <td><%= fb.getOrderDetailID()%></td>
                    <td><%= fb.getRating()%></td>

                    <!-- Comment rút gọn -->
                    <td class="text-start" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                        <%= fb.getComment().split("\\s+").length > 10
                                ? String.join(" ", Arrays.copyOfRange(fb.getComment().split("\\s+"), 0, 10)) + "..."
                                : fb.getComment()%>
                    </td>

                    <td><%= fb.getCreatedAt()%></td>

                    <!-- Status -->
                    <td>
                        <form method="get" action="<%= context%>/Feedback" class="d-inline">
                            <input type="hidden" name="action" value="updateStatus" />
                            <input type="hidden" name="id" value="<%= fb.getFeedbackID()%>" />
                            <select name="status" onchange="this.form.submit()" class="form-select form-select-sm">
                                <option value="Pending" <%= "Pending".equals(fb.getStatus()) ? "selected" : ""%>>Pending</option>
                                <option value="Processing" <%= "Processing".equals(fb.getStatus()) ? "selected" : ""%>>Processing</option>
                                <option value="Resolved" <%= "Resolved".equals(fb.getStatus()) ? "selected" : ""%>>Resolved</option>
                                <option value="Ignored" <%= "Ignored".equals(fb.getStatus()) ? "selected" : ""%>>Ignored</option>
                            </select>
                        </form>
                    </td>

                    <!-- Action -->
                    <td>
                        <a href="<%= context%>/Feedback?action=delete&id=<%= fb.getFeedbackID()%>" class="btn btn-danger btn-sm">Delete</a>

                        <% if (fb.getComment().split("\\s+").length > 10) {%>
                        <button class="btn btn-info btn-sm mt-1" data-bs-toggle="modal"
                                data-bs-target="#commentModal<%= fb.getFeedbackID()%>">View more</button>
                        <% }%>

                        <button class="btn btn-success btn-sm mt-1" data-bs-toggle="modal"
                                data-bs-target="#replyModal<%= fb.getFeedbackID()%>">Reply</button>
                    </td>
                </tr>

                <% if (replies != null && !replies.isEmpty()) { %>
                <tr class="table-warning">
                    <td colspan="9" class="text-start ps-4">✅ Admin replied.</td>
                </tr>
                <% }%>

                <!-- Modal View Full Comment -->
            <div class="modal fade" id="commentModal<%= fb.getFeedbackID()%>" tabindex="-1">
                <div class="modal-dialog modal-dialog-centered modal-lg">
                    <div class="modal-content text-start">
                        <div class="modal-header">
                            <h5 class="modal-title">Full Comment</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body text-start">
                            <%= fb.getComment().trim().replaceAll("^\\s+", "")%>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Modal Reply -->
            <div class="modal fade" id="replyModal<%= fb.getFeedbackID()%>" tabindex="-1">
                <div class="modal-dialog modal-dialog-centered modal-lg">
                    <div class="modal-content text-start">
                        <div class="modal-header">
                            <h5 class="modal-title">Reply to Feedback #<%= fb.getFeedbackID()%></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <!-- History -->
                            <div class="border rounded p-3 mb-3" style="max-height: 300px; overflow-y: auto; background-color: #f8f9fa;">
                                <p><strong><%= fb.getUserName()%>:</strong> <%= fb.getComment()%></p>
                                <hr class="my-2" />
                                <% for (ReplyFeedback r : replies) {%>
                                <form method="post" action="<%= context%>/ReplyFeedback" class="mb-2">
                                    <input type="hidden" name="replyID" value="<%= r.getReplyID()%>">
                                    <div class="d-flex justify-content-between align-items-start">
                                        <textarea name="replyText" class="form-control me-2" rows="2" required><%= r.getReplyText()%></textarea>
                                        <div>
                                            <button type="submit" name="action" value="update" class="btn btn-sm btn-warning me-1">Edit</button>
                                            <button type="submit" name="action" value="delete" class="btn btn-sm btn-danger" onclick="return confirm('Delete this reply?')">Delete</button>
                                        </div>
                                    </div>
                                    <p class="text-muted small"><%= r.getCreatedAt()%></p>
                                </form>
                                <hr class="my-2" />
                                <% }%>
                            </div>

                            <!-- Form gửi mới -->
                            <form method="post" action="<%= context%>/ReplyFeedback">
                                <input type="hidden" name="feedbackID" value="<%= fb.getFeedbackID()%>">
                                <textarea name="replyText" class="form-control" rows="3" required placeholder="Enter reply..."></textarea>
                                <div class="modal-footer p-0 mt-3">
                                    <button type="submit" class="btn btn-primary">Send Reply</button>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <% } // end for %>
            <% } else { %>
            <tr><td colspan="9">No feedback found.</td></tr>
            <% } %>
            </tbody>
        </table>
    </div>
</div>

<!-- Bootstrap CDN -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
