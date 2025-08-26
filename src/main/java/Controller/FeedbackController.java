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

        // Report feedback → đổi status thành Ignored
if ("report".equals(action) && id != -1) {
    feedbackDAO.updateStatus(id, "Ignored");
    response.sendRedirect("Feedback");
    return;
}

// Restore feedback → khôi phục lại status cũ (Pending chẳng hạn)
if ("restore".equals(action) && id != -1) {
    // Ở đây bạn có thể lưu trạng thái cũ trong DB (ví dụ thêm cột prevStatus).
    // Nếu không thì cho restore về "Pending"
    feedbackDAO.updateStatus(id, "Pending");
    response.sendRedirect("Feedback");
    return;
}


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
        if (page < 1) {
            page = 1;
        }

        List<Feedback> feedbacks;
        int totalRecords;

        boolean isFiltered = (userName != null && !userName.isEmpty())
                || (keyword != null && !keyword.isEmpty())
                || (status != null && !status.isEmpty())
                || (date != null && !date.isEmpty());

        if (isFiltered) {
            feedbacks = feedbackDAO.searchFeedback(userName, keyword, status, date, page, recordsPerPage);
            totalRecords = feedbackDAO.countFilteredFeedback(userName, keyword, status, date);
        } else {
            feedbacks = feedbackDAO.getFeedbackByPage(page, recordsPerPage);
            totalRecords = feedbackDAO.countAllFeedback();
        }

        int totalPages = (int) Math.ceil(totalRecords * 1.0 / recordsPerPage);
        if (page > totalPages && totalPages > 0) {
            page = totalPages;
        }

        request.setAttribute("feedbacks", feedbacks);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("manageFeedback.jsp").forward(request, response);
        
       


        
    }

    private void exportFeedback(HttpServletResponse response) throws IOException {
    List<Feedback> list = feedbackDAO.getAllFeedbackWithoutPagination(); // lấy tất cả feedback
    response.setContentType("application/vnd.ms-excel; charset=UTF-8");
    response.setHeader("Content-Disposition", "attachment; filename=Feedback_List.xls");

    PrintWriter out = response.getWriter();
    // Viết BOM để Excel nhận UTF-8
    out.write("\uFEFF");
    out.println("ID\tOrderDetailID\tCustomerID\tUserName\tProductName\tImage\tRating\tComment\tCreatedAt\tStatus");

    for (Feedback fb : list) {
        out.println(
            fb.getFeedbackID() + "\t"
            + fb.getOrderDetailID() + "\t"
            + fb.getUserID() + "\t"
            + fb.getUserName() + "\t"
            + fb.getProductName() + "\t"
            + fb.getImage() + "\t"
            + fb.getRating() + "\t"
            + fb.getComment().replaceAll("\\t", " ").replaceAll("\\n", " ") + "\t"
            + fb.getCreatedAt() + "\t"
            + fb.getStatus()
        );
    }

    out.flush();
    out.close();
}

}
