package Controller;

import DAO.ReplyFeedbackDAO;
import DAO.FeedbackDAO;
import Model.ReplyFeedback;
import Model.User;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
/**
 *
 * @author TriTN
 */
@WebServlet("/ReplyFeedback")
public class ReplyFeedbackController extends HttpServlet {

    private ReplyFeedbackDAO replyDAO = new ReplyFeedbackDAO();
    private FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(true);
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String replyText = request.getParameter("replyText") != null ? request.getParameter("replyText").trim() : "";
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        try {
            if ("update".equals(action) || "delete".equals(action)) {
                if (user.getRoleID() != 1 && user.getRoleID() != 2) {
                    if (isAjax) {
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"Access denied\"}");
                        return;
                    }
                    response.sendRedirect("access-denied.jsp");
                    return;
                }

                if ("update".equals(action)) {
                    int replyID = Integer.parseInt(request.getParameter("replyID"));
                    if (isReplyValid(replyText)) {
                        ReplyFeedback reply = new ReplyFeedback();
                        reply.setReplyID(replyID);
                        reply.setReplyText(replyText);
                        replyDAO.updateReply(reply);
                    }

                } else if ("delete".equals(action)) {
                    int replyID = Integer.parseInt(request.getParameter("replyID"));
                    int feedbackID = Integer.parseInt(request.getParameter("feedbackID"));
                    replyDAO.deleteReply(replyID);

                    boolean hasActiveReplies = false;
                    for (ReplyFeedback r : replyDAO.getRepliesByFeedbackID(feedbackID)) {
                        if (!r.getIsDeleted()) {
                            hasActiveReplies = true;
                            break;
                        }
                    }
//                    if (!hasActiveReplies) {
//                        feedbackDAO.updateStatus(feedbackID, "Processing");
//                    }
                }

            } else { // insert reply
                int feedbackID = Integer.parseInt(request.getParameter("feedbackID"));
                if (isReplyValid(replyText)) {
                    ReplyFeedback reply = new ReplyFeedback();
                    reply.setFeedbackID(feedbackID);
                    reply.setUserID(user.getUserID());
                    reply.setReplyText(replyText);
                    replyDAO.insertReply(reply);

                    if (user.getRoleID() == 3) {
                        feedbackDAO.updateStatus(feedbackID, "Customer Replied");
                    } else {
                        feedbackDAO.updateStatus(feedbackID, "Resolved");
                    }

                    if (isAjax) {
                        response.setContentType("application/json;charset=UTF-8");
                        PrintWriter out = response.getWriter();
                        String json = String.format("{\"userName\":\"%s\",\"replyText\":\"%s\"}",
                                user.getFullName(), replyText.replace("\"","\\\""));
                        out.write(json);
                        out.flush();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("ViewFeedbackOrders");
    }

    private boolean isReplyValid(String text) {
        return text != null && text.length() >= 1 && text.length() <= 1000 && !text.toLowerCase().contains("<script");
    }
}
