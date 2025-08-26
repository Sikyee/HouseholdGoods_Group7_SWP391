/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

/**
 *
 * @author thong
 */
import DAO.OrderDAO;
import DAO.ProductDAO;
import DAO.UserDAO;
import Model.User;
import Model.dto.WeeklyRevenue;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/dashboard")
public class DashboardController extends HttpServlet {

    UserDAO userDAO = new UserDAO();
    ProductDAO productDAO = new ProductDAO();
    OrderDAO orderDAO = new OrderDAO();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int userCount = userDAO.getAllUsers().size();
            int productCount = productDAO.getAllProducts().size();
            String revenueADay = String.format("%,d", orderDAO.getRevenueToday());

            // dữ liệu 7 ngày cho chart
            List<WeeklyRevenue> week = orderDAO.getRevenueLast7Days();

// CanvasJS cần [{label: "...", y: ...}, ...]
            List<Map<String, Object>> chartData = new ArrayList<>();
            java.util.Locale locale = java.util.Locale.ENGLISH; // hoặc Locale.forLanguageTag("vi")
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM");

            for (WeeklyRevenue w : week) {
                Map<String, Object> point = new HashMap<>();
                // Cách 1: label là thứ (Mon, Tue, ...)
                String label = w.getDate().getDayOfWeek()
                        .getDisplayName(java.time.format.TextStyle.SHORT, locale);

                // Cách 2 (nếu muốn): label là ngày "dd/MM"
                // String label = w.getDate().format(fmt);
                point.put("label", label);
                point.put("y", w.getTotal());
                chartData.add(point);
            }

            String weekJson = new com.google.gson.Gson().toJson(chartData);

            req.setAttribute("userCount", userCount);
            req.setAttribute("productCount", productCount);
            req.setAttribute("revenueADay", revenueADay);
            req.setAttribute("weekRevenueData", weekJson);

            req.getRequestDispatcher("dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
