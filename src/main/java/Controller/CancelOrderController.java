package Controller;

import DAO.OrderDAO;
import DAO.CancelReasonDAO;
import Model.CancelReason;
import Model.User;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "CancelOrderController", urlPatterns = {"/cancelOrder", "/cancel-order"})
public class CancelOrderController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        HttpSession session = request.getSession();

        User userObj = (User) session.getAttribute("user");
        Integer roleAttr = (Integer) session.getAttribute("role");
        Integer roleId = roleAttr != null ? roleAttr : (userObj != null ? userObj.getRoleID() : null);

        // Chỉ cho Customer (role == 3) đã đăng nhập
        if (roleId == null || roleId != 3) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            int orderID = Integer.parseInt(request.getParameter("orderID"));
            String reasonText = request.getParameter("reason");

            CancelReasonDAO reasonDAO = new CancelReasonDAO();
            reasonDAO.insertCancelReason(new CancelReason(0, orderID, reasonText));

            OrderDAO orderDAO = new OrderDAO();
            boolean ok = orderDAO.cancelOrderAndRestock(orderID);

            if (ok) {
                response.sendRedirect("order-detail?orderID=" + orderID + "&msg=cancelled");
            } else {
                response.sendRedirect("order-detail?orderID=" + orderID + "&err=cancel_failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("order?err=internal");
        }
    }
}
