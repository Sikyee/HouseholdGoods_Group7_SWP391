package Controller;

import DAO.*;
import Model.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "OrderDetailController", urlPatterns = {"/order-detail"})
public class OrderDetailController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int orderID = Integer.parseInt(request.getParameter("orderID"));

            OrderDAO orderDAO = new OrderDAO();
            OrderDetailDAO detailDAO = new OrderDetailDAO();
            CancelReasonDAO cancelReasonDAO = new CancelReasonDAO();
            OrderStatusDAO orderStatusDAO = new OrderStatusDAO();

            OrderInfo order = orderDAO.getOrderByID(orderID);
            List<OrderDetail> detailList = detailDAO.getOrderDetailsByOrderID(orderID);
            CancelReason cancelReason = cancelReasonDAO.getCancelReasonByOrderID(orderID);
            List<OrderStatus> statusList = orderStatusDAO.getAllOrderStatuses();

            request.setAttribute("order", order);
            request.setAttribute("orderDetailList", detailList);
            request.setAttribute("cancelReason", cancelReason);
            request.setAttribute("orderStatusList", statusList);

            RequestDispatcher rd = request.getRequestDispatcher("orderDetail.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("order.jsp");
        }
    }
}
