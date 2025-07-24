/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.ReplyFeedbackDAO;
import Model.ReplyFeedback;

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
    private ReplyFeedbackDAO dao = new ReplyFeedbackDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String replyText = request.getParameter("replyText");

        try {
            if ("update".equals(action)) {
                // ✅ Cập nhật phản hồi
                int replyID = Integer.parseInt(request.getParameter("replyID"));
                ReplyFeedback reply = new ReplyFeedback();
                reply.setReplyID(replyID);
                reply.setReplyText(replyText);
                dao.updateReply(reply);

            } else if ("delete".equals(action)) {
                // ✅ Xóa phản hồi
                int replyID = Integer.parseInt(request.getParameter("replyID"));
                dao.deleteReply(replyID);

            } else {
                // ✅ Gửi phản hồi mới
                int feedbackID = Integer.parseInt(request.getParameter("feedbackID"));
                int userID = 1; // Giả lập: admin đang đăng nhập
                ReplyFeedback reply = new ReplyFeedback();
                reply.setFeedbackID(feedbackID);
                reply.setUserID(userID);
                reply.setReplyText(replyText);
                dao.insertReply(reply);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("Feedback");
    }
}
