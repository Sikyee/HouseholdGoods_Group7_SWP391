/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.FeedbackDAO;
import Model.Feedback;
import Model.User;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/Feedback")
public class FeedbackController extends HttpServlet {
    private FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || (user.getRoleID() != 1 && user.getRoleID() != 2)) {
            response.sendRedirect("access-denied.jsp");
            return;
        }

        String action = request.getParameter("action");
        int id = request.getParameter("id") != null ? Integer.parseInt(request.getParameter("id")) : -1;

        // Xóa feedback (chỉ Admin)
        if ("delete".equals(action) && id != -1) {
            if (user.getRoleID() == 1) {
                feedbackDAO.softDeleteFeedback(id);
            }
            response.sendRedirect("Feedback");
            return;
        }

        // Cập nhật trạng thái
       /* if ("updateStatus".equals(action) && id != -1) {
            String status = request.getParameter("status");
            if (status != null && (status.equals("Pending") || status.equals("Processing")
                    || status.equals("Resolved") || status.equals("Ignored"))) {

                // Luôn cho phép Admin cập nhật, kể cả chọn Ignored
                if (user.getRoleID() == 1 || !status.equals("Ignored")) {
                    feedbackDAO.updateStatus(id, status);
                }
            }
            response.sendRedirect("Feedback");
            return;
        }*/

        // Export Feedback
        if ("export".equals(action)) {
            exportFeedback(response);
            return;
        }

        String userName = request.getParameter("userName");
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String date = request.getParameter("date");

        int page = 1;
        int recordsPerPage = 10;
        try {
            if (request.getParameter("page") != null) {
                page = Integer.parseInt(request.getParameter("page"));
            }
        } catch (NumberFormatException e) {
            page = 1;
        }
        if (page < 1) page = 1;

        List<Feedback> feedbacks;
        int totalRecords;

        boolean isFiltered = (userName != null && !userName.isEmpty()) ||
                (keyword != null && !keyword.isEmpty()) ||
                (status != null && !status.isEmpty()) ||
                (date != null && !date.isEmpty());

        if (isFiltered) {
            feedbacks = feedbackDAO.searchFeedback(userName, keyword, status, date, page, recordsPerPage);
            totalRecords = feedbackDAO.countFilteredFeedback(userName, keyword, status, date);
        } else {
            feedbacks = feedbackDAO.getFeedbackByPage(page, recordsPerPage);
            totalRecords = feedbackDAO.countAllFeedback();
        }

        int totalPages = (int) Math.ceil(totalRecords * 1.0 / recordsPerPage);
        if (page > totalPages && totalPages > 0) page = totalPages;

        request.setAttribute("feedbacks", feedbacks);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("manageFeedback.jsp").forward(request, response);
    }

    private void exportFeedback(HttpServletResponse response) throws IOException {
        List<Feedback> list = feedbackDAO.getAllFeedback();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=Feedback_List.xls");

        PrintWriter out = response.getWriter();
        out.println("ID\tUser\tOrderDetailID\tRating\tComment\tCreatedAt\tStatus");
        for (Feedback fb : list) {
            out.println(fb.getFeedbackID() + "\t" +
                        fb.getUserName() + "\t" +
                        fb.getOrderDetailID() + "\t" +
                        fb.getRating() + "\t" +
                        fb.getComment().replaceAll("\\t", " ").replaceAll("\\n", " ") + "\t" +
                        fb.getCreatedAt() + "\t" +
                        fb.getStatus());
        }
        out.flush();
        out.close();
    }
}
