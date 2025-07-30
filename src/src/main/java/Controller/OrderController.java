/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAO.OrderDAO;
import DAO.OrderDetailDAO;
import Model.OrderDetail;
import Model.OrderInfo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
/**
 *
 * @author thach
 */


@WebServlet("/Order")
public class OrderController extends HttpServlet {
    private OrderDAO dao;

    @Override
    public void init() throws ServletException {
        try {
            dao = new OrderDAO();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if (action == null || action.equals("list")) {
                List<OrderInfo> list = dao.getAllOrders();
                req.setAttribute("orders", list);
                req.getRequestDispatcher("manageOrder.jsp").forward(req, resp);
            } else if (action.equals("trash")) {
                List<OrderInfo> list = dao.getDeletedOrders();
                req.setAttribute("orders", list);
                req.getRequestDispatcher("manageOrder.jsp").forward(req, resp);
            } else if (action.equals("search")) {
                String keyword = req.getParameter("keyword");
                List<OrderInfo> list = dao.searchOrders(keyword);
                req.setAttribute("orders", list);
                req.getRequestDispatcher("manageOrder.jsp").forward(req, resp);
            } else if (action.equals("cancel")) {
                int id = Integer.parseInt(req.getParameter("id"));
                dao.cancelOrder(id);
                resp.sendRedirect("Order?action=list");
            } else if (action.equals("delete")) {
                int id = Integer.parseInt(req.getParameter("id"));
                dao.deleteOrder(id);
                resp.sendRedirect("Order?action=list");
            } else if (action.equals("updateStatus")) {
                int id = Integer.parseInt(req.getParameter("id"));
                int status = Integer.parseInt(req.getParameter("status"));
                dao.updateStatus(id, status);
                resp.sendRedirect("Order?action=list");
            } else if (action.equals("restore")) {
                int id = Integer.parseInt(req.getParameter("id"));
                dao.restoreOrder(id);
                resp.sendRedirect("Order?action=trash");
            } else if (action.equals("view")) {
                int id = Integer.parseInt(req.getParameter("id"));
                OrderInfo order = dao.getOrderById(id);
                OrderDetailDAO detailDAO = new OrderDetailDAO();
                List<OrderDetail> details = detailDAO.getDetailsByOrderID(id);
                req.setAttribute("order", order);
                req.setAttribute("details", details);
                req.getRequestDispatcher("orderDetail.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("manageOrder.jsp").forward(req, resp);
        }
    }
}
