/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.WriteFeedbackDAO;
import Model.OrderDetail;
import Model.Feedback;
import Model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/WriteFeedback")
public class WriteFeedbackController extends HttpServlet {

    private WriteFeedbackDAO dao = new WriteFeedbackDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String odid = request.getParameter("orderDetailID");
        if (odid != null) {
            int orderDetailID = Integer.parseInt(odid);
            OrderDetail od = dao.getOrderDetailByID(orderDetailID);
            request.setAttribute("orderDetail", od);

            if (dao.hasFeedback(orderDetailID)) {
                request.setAttribute("alreadyFeedback", true);
                Feedback fb = dao.getFeedbackByOrderDetailID(orderDetailID);
                request.setAttribute("existingFeedback", fb);
            }

            request.getRequestDispatcher("writeFeedback.jsp").forward(request, response);
        } else {
            response.sendRedirect("order");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int orderDetailID = Integer.parseInt(request.getParameter("orderDetailID"));
        String ratingStr = request.getParameter("rating");
        String comment = request.getParameter("comment");

        // Nếu đã có feedback thì chặn
        if (dao.hasFeedback(orderDetailID)) {
            request.setAttribute("error", "You have already submitted feedback for this item.");
            request.setAttribute("alreadyFeedback", true);
            Feedback fb = dao.getFeedbackByOrderDetailID(orderDetailID);
            request.setAttribute("existingFeedback", fb);
            OrderDetail od = dao.getOrderDetailByID(orderDetailID);
            request.setAttribute("orderDetail", od);
            request.getRequestDispatcher("writeFeedback.jsp").forward(request, response);
            return;
        }

        // Validate rating
        int rating = -1;
        try {
            rating = Integer.parseInt(ratingStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Rating must be between 1 and 5.");
            OrderDetail od = dao.getOrderDetailByID(orderDetailID);
            request.setAttribute("orderDetail", od);
            request.getRequestDispatcher("writeFeedback.jsp").forward(request, response);
            return;
        }

        if (rating < 1 || rating > 5) {
            request.setAttribute("error", "Rating must be between 1 and 5.");
            OrderDetail od = dao.getOrderDetailByID(orderDetailID);
            request.setAttribute("orderDetail", od);
            request.getRequestDispatcher("writeFeedback.jsp").forward(request, response);
            return;
        }

        // Validate comment
        if (comment == null || comment.trim().length() < 5 
                || comment.trim().length() > 500 
                || comment.toLowerCase().contains("<script")) {
            request.setAttribute("error", "Comment must be 5–500 characters and safe.");
            OrderDetail od = dao.getOrderDetailByID(orderDetailID);
            request.setAttribute("orderDetail", od);
            request.getRequestDispatcher("writeFeedback.jsp").forward(request, response);
            return;
        }

        // Save feedback
        Feedback fb = new Feedback();
        fb.setUserID(user.getUserID());
        fb.setOrderDetailID(orderDetailID);
        fb.setRating(rating);
        fb.setComment(comment.trim());

        boolean success = dao.insertFeedback(fb);
        if (success) {
            request.setAttribute("success", "Your feedback has been submitted successfully!");
        } else {
            request.setAttribute("error", "Failed to submit feedback. Please try again.");
        }

        OrderDetail od = dao.getOrderDetailByID(orderDetailID);
        request.setAttribute("orderDetail", od);
        request.getRequestDispatcher("writeFeedback.jsp").forward(request, response);
        //ghi gì dó de commit lai
    }
}
