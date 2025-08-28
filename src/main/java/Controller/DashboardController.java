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
import Model.MonthlyRevenue;
import Model.StatusCount;
import Model.User;
import Model.dto.WeeklyRevenue;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
            String startParam = req.getParameter("start");
            String endParam = req.getParameter("end");

// mặc định: 6 tháng gần nhất
            LocalDate today = LocalDate.now();
            LocalDate defaultStart = today.minusMonths(5).withDayOfMonth(1);
            LocalDate defaultEnd = today;

            LocalDate start = (startParam == null || startParam.isEmpty())
                    ? defaultStart : LocalDate.parse(startParam);
            LocalDate end = (endParam == null || endParam.isEmpty())
                    ? defaultEnd : LocalDate.parse(endParam);

// (Tuỳ chọn) Cho phép chọn status qua query ?status=5, mặc định = 5 (đơn đã giao)
            int statusForRevenue;
            try {
                statusForRevenue = Integer.parseInt(req.getParameter("status"));
                System.out.println("Status la: " + statusForRevenue);
            } catch (Exception ignore) {
                statusForRevenue = 5;
            }

// ---- TUẦN (lọc theo khoảng ngày) ----
            List<WeeklyRevenue> week = orderDAO.getWeeklyRevenue(start, end, statusForRevenue);

            List<Map<String, Object>> weekPoints = new ArrayList<>();
            DateTimeFormatter labelFmt = DateTimeFormatter.ofPattern("dd/MM");
            for (WeeklyRevenue w : week) {
                Map<String, Object> p = new HashMap<>();
                // nhãn hiển thị ngày bắt đầu tuần (thứ Hai) - gọn, dễ nhìn
                p.put("label", w.getDate().format(labelFmt));
                p.put("y", w.getTotal());
                weekPoints.add(p);
            }
            String weekJson = new Gson().toJson(weekPoints);

// ---- THÁNG (lọc theo khoảng ngày, status = statusForRevenue) ----
            List<MonthlyRevenue> monthly = orderDAO.getMonthlyRevenue(start, end, statusForRevenue);
            List<Map<String, Object>> monthlyPoints = new ArrayList<>();
            for (MonthlyRevenue mr : monthly) {
                Map<String, Object> p = new HashMap<>();
                p.put("label", mr.getMonth().toString()); // "YYYY-MM"
                p.put("y", mr.getTotal());
                monthlyPoints.add(p);
            }
            String monthlyRevenueJson = new Gson().toJson(monthlyPoints);

// ---- PIE (đếm số đơn theo trạng thái trong khoảng ngày) ----
            List<StatusCount> statusCounts = orderDAO.getOrderCountByStatus(start, end);
            List<Map<String, Object>> piePoints = new ArrayList<>();
            for (StatusCount sc : statusCounts) {
                Map<String, Object> p = new HashMap<>();
                p.put("name", sc.getStatusName());
                p.put("y", sc.getCount());
                piePoints.add(p);
            }
            String orderStatusPieJson = new Gson().toJson(piePoints);

// ---- Set attribute cho JSP ----
            req.setAttribute("userCount", userCount);
            req.setAttribute("productCount", productCount);
            req.setAttribute("revenueADay", revenueADay);

            req.setAttribute("start", start.toString());
            req.setAttribute("end", end.toString());
            req.setAttribute("status", statusForRevenue);

            req.setAttribute("weekRevenueData", weekJson);
            req.setAttribute("monthlyRevenueData", monthlyRevenueJson);
            req.setAttribute("orderStatusPieData", orderStatusPieJson);

            req.getRequestDispatcher("dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
