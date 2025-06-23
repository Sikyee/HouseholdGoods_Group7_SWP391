/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.FeedbackDAO;
import Model.Feedback;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
/**
 *
 * @author TriTN
 */
@WebServlet("/Feedback")
public class FeedbackController extends HttpServlet {
    private FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        int id = request.getParameter("id") != null ? Integer.parseInt(request.getParameter("id")) : -1;

        // Xóa mềm
        if ("delete".equals(action) && id != -1) {
            feedbackDAO.softDeleteFeedback(id);
            response.sendRedirect("Feedback");
            return;
        }

        // Cập nhật trạng thái
        if ("updateStatus".equals(action) && id != -1) {
            String status = request.getParameter("status");
            feedbackDAO.updateStatus(id, status);
            response.sendRedirect("Feedback");
            return;
        }

        // Lọc dữ liệu
        String userName = request.getParameter("userName");
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String date = request.getParameter("date");

        int page = 1;
        int recordsPerPage = 10;
        if (request.getParameter("page") != null) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException ignored) {}
        }

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

        request.setAttribute("feedbacks", feedbacks);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("manageFeedback.jsp").forward(request, response);
    }
}
