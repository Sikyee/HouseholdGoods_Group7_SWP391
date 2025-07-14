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
import java.util.List;
/**
 *
 * @author TriTN
 */

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

        List<OrderDetail> orders = dao.getOrderDetailsByUser(user.getUserID());
        request.setAttribute("orders", orders);
        request.getRequestDispatcher("writeFeedback.jsp").forward(request, response);
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
        int rating = Integer.parseInt(request.getParameter("rating"));
        String comment = request.getParameter("comment");

        Feedback fb = new Feedback();
        fb.setUserID(user.getUserID());
        fb.setOrderDetailID(orderDetailID);
        fb.setRating(rating);
        fb.setComment(comment);

        dao.insertFeedback(fb);

        response.sendRedirect("WriteFeedback");
    }
}
