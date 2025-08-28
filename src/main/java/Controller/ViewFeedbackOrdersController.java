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
/**
 *
 * @author TriTN
 */
@WebServlet("/ViewFeedbackOrders")
public class ViewFeedbackOrdersController extends HttpServlet {

    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    HttpSession session = request.getSession(false);
    User user = (session != null) ? (User) session.getAttribute("user") : null;

    if (user == null || user.getRoleID() != 3) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    FeedbackDAO dao = new FeedbackDAO();

    // Lấy tab và page từ param
    String tab = request.getParameter("tab");
    if(tab == null) tab = "without-feedback";

    int pageWithoutFeedback = 1;
    int pageWithFeedback = 1;
    int limit = 5; // số order trên 1 trang

    try {
        pageWithoutFeedback = (tab.equals("without-feedback") && request.getParameter("page") != null)
                ? Integer.parseInt(request.getParameter("page")) : 1;
        pageWithFeedback = (tab.equals("with-feedback") && request.getParameter("page") != null)
                ? Integer.parseInt(request.getParameter("page")) : 1;
    } catch (NumberFormatException e) {
        e.printStackTrace();
    }

    // Lấy dữ liệu theo trang
    List<OrderDetail> withoutFeedback = dao.getOrdersWithoutFeedback(user.getUserID(), pageWithoutFeedback, limit);
    List<OrderDetail> withFeedback = dao.getOrdersWithFeedback(user.getUserID(), pageWithFeedback, limit);

    // Tính tổng trang
    int totalWithoutFeedback = dao.countOrdersWithoutFeedback(user.getUserID());
    int totalWithFeedback = dao.countOrdersWithFeedback(user.getUserID());

    int totalPagesWithoutFeedback = (int) Math.ceil(totalWithoutFeedback * 1.0 / limit);
    int totalPagesWithFeedback = (int) Math.ceil(totalWithFeedback * 1.0 / limit);

    request.setAttribute("withoutFeedback", withoutFeedback);
    request.setAttribute("withFeedback", withFeedback);

    request.setAttribute("currentPageWithoutFeedback", pageWithoutFeedback);
    request.setAttribute("totalPagesWithoutFeedback", totalPagesWithoutFeedback);

    request.setAttribute("currentPageWithFeedback", pageWithFeedback);
    request.setAttribute("totalPagesWithFeedback", totalPagesWithFeedback);

    request.setAttribute("activeTab", tab); // để JSP mở đúng tab
    RequestDispatcher rd = request.getRequestDispatcher("viewFeedbackOrders.jsp");
    rd.forward(request, response);
}

}
