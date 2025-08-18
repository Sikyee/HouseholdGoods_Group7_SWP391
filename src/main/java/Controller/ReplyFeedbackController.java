/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.ReplyFeedbackDAO;
import DAO.FeedbackDAO;
import Model.ReplyFeedback;
import Model.User;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

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
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || (user.getRoleID() != 1 && user.getRoleID() != 2)) {
            response.sendRedirect("access-denied.jsp");
            return;
        }

        String action = request.getParameter("action");
        String replyText = request.getParameter("replyText") != null ? request.getParameter("replyText").trim() : "";

        try {
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

               // Check còn reply chưa xoá không
boolean hasActiveReplies = false;
for (ReplyFeedback r : replyDAO.getRepliesByFeedbackID(feedbackID)) {
    if (!r.getIsDeleted()) {
        hasActiveReplies = true;
        break;
    }
}
if (!hasActiveReplies) {
    feedbackDAO.updateStatus(feedbackID, "Processing");
}


            } else { // insert reply
                int feedbackID = Integer.parseInt(request.getParameter("feedbackID"));
                if (isReplyValid(replyText)) {
                    ReplyFeedback reply = new ReplyFeedback();
                    reply.setFeedbackID(feedbackID);
                    reply.setUserID(user.getUserID());
                    reply.setReplyText(replyText);
                    replyDAO.insertReply(reply);

                    feedbackDAO.updateStatus(feedbackID, "Resolved");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("Feedback");
    }

    private boolean isReplyValid(String text) {
        return text != null && text.length() >= 1 && text.length() <= 1000 && !text.toLowerCase().contains("<script");
        //ghi gì dó de commit lai
    }
}
