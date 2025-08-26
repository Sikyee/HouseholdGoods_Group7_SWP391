/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.FeedbackDAO;
import Model.OrderDetail;
import Model.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/ViewFeedbackOrders")
public class ViewFeedbackOrdersController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false); // không tạo session mới nếu chưa có
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || user.getRoleID() !=3) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        FeedbackDAO dao = new FeedbackDAO();
        List<OrderDetail> withoutFeedback = dao.getOrdersWithoutFeedback(user.getUserID());
        List<OrderDetail> withFeedback = dao.getOrdersWithFeedback(user.getUserID());
        
        System.err.println("without feedback" + withoutFeedback);
        System.err.println("with feedback" + withFeedback);

        request.setAttribute("withoutFeedback", withoutFeedback);
        request.setAttribute("withFeedback", withFeedback);

        RequestDispatcher rd = request.getRequestDispatcher("viewFeedbackOrders.jsp");
        rd.forward(request, response);
    }
}
