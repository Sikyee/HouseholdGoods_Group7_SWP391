package DAO;

import DB.DBConnection;
import Model.Order;
import Model.OrderInfo;
import Model.dto.WeeklyRevenue;
import java.math.BigInteger;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderDAO {

    public List<OrderInfo> getOrdersByUserAndStatus(int userID, int statusID) {
        List<OrderInfo> list = new ArrayList<>();
        String sql = "SELECT * FROM OrderInfo WHERE userID = ? AND orderStatusID = ? ";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ps.setInt(2, statusID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderInfo order = extractOrderFromResultSet(rs);
                list.add(order);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public OrderInfo getOrderByID(int orderID) {
        String sql = "SELECT * FROM OrderInfo WHERE orderID = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractOrderFromResultSet(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateOrderStatus(int orderID, int newStatusID) {
        String sql = "UPDATE OrderInfo SET orderStatusID = ? WHERE orderID = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newStatusID);
            ps.setInt(2, orderID);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<OrderInfo> getAllOrdersByStatus(int statusID) {
        List<OrderInfo> list = new ArrayList<>();
        String sql = "SELECT * FROM OrderInfo WHERE orderStatusID = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, statusID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderInfo order = extractOrderFromResultSet(rs);
                list.add(order);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int createOrder(OrderInfo order) throws Exception {
        String sql = "INSERT INTO OrderInfo (userID, orderStatusID, orderDate, paymentMethodID, voucherID, totalPrice, finalPrice, fullName, deliveryAddress, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getUserID());
            ps.setInt(2, order.getOrderStatusID());
            ps.setTimestamp(3, new Timestamp(order.getOrderDate().getTime()));
            ps.setInt(4, order.getPaymentMethodID());

            if (order.getVoucherID() == null || order.getVoucherID() == 0) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, order.getVoucherID());
            }

            ps.setDouble(6, order.getTotalPrice());
            ps.setDouble(7, order.getFinalPrice());
            ps.setString(8, order.getFullName());
            ps.setString(9, order.getDeliveryAddress());
            ps.setString(10, order.getPhone());

            ps.executeUpdate();

            try ( ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Lấy orderID vừa tạo
                }
            }
        }
        return 0;
    }

    public boolean updateOrderStatus(OrderInfo order) throws Exception {
        String sql = "UPDATE [dbo].[OrderInfo]\n"
                + "   SET [orderStatusID] = ?\n"
                + " WHERE orderId = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, order.getOrderStatusID());
            st.setInt(2, order.getOrderID());
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return false;
    }

    public void cancelOrder(int orderID) throws SQLException {
        try {

            updateOrderStatus(orderID, 5); // Canceled
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OrderInfo extractOrderFromResultSet(ResultSet rs) throws SQLException {
        OrderInfo order = new OrderInfo();
        order.setOrderID(rs.getInt("orderID"));
        order.setUserID(rs.getInt("userID"));
        order.setOrderStatusID(rs.getInt("orderStatusID"));
        order.setOrderDate(rs.getTimestamp("orderDate"));
        order.setPaymentMethodID(rs.getInt("paymentMethodID"));
        order.setVoucherID(rs.getObject("voucherID") != null ? rs.getInt("voucherID") : null);
        order.setTotalPrice(rs.getDouble("totalPrice"));
        order.setFinalPrice(rs.getDouble("finalPrice"));
        order.setFullName(rs.getString("fullName"));
        order.setDeliveryAddress(rs.getString("deliveryAddress"));
        order.setPhone(rs.getString("phone"));
        return order;
    }

    public int getRevenueToday() throws SQLException {
        String sql = "SELECT SUM(finalPrice) AS total FROM OrderInfo WHERE orderStatusID = 1 AND orderDate BETWEEN ? AND ?";

        int total = 0;

        // Lấy thời gian bắt đầu và kết thúc của ngày hiện tại
        LocalDate today = LocalDate.now();
        Timestamp startOfDay = Timestamp.valueOf(today.atStartOfDay());
        Timestamp endOfDay = Timestamp.valueOf(today.atTime(LocalTime.MAX));

        try ( Connection connect = DBConnection.getConnection();  PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setTimestamp(1, startOfDay);
            ps.setTimestamp(2, endOfDay);

            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt("total");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return total;
    }

    public int getRevenueLastDay() throws SQLException {
        String sql = "SELECT SUM(finalPrice) AS total FROM OrderInfo WHERE orderStatusID = 1 AND orderDate BETWEEN ? AND ?";

        int total = 0;

        // Lấy thời gian bắt đầu và kết thúc của ngày hiện tại
        LocalDate today = LocalDate.now();
        Timestamp startOfDay = Timestamp.valueOf(today.atStartOfDay());
        Timestamp endOfDay = Timestamp.valueOf(today.atTime(LocalTime.MAX));

        try ( Connection connect = DBConnection.getConnection();  PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setTimestamp(1, startOfDay);
            ps.setTimestamp(2, endOfDay);

            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt("total");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return total;
    }

    public List<WeeklyRevenue> getRevenueLast7Days() throws SQLException {
        String sql
                = "WITH d AS ( "
                + "  SELECT CAST(CAST(GETDATE() AS date) AS datetime) AS d, 0 AS n "
                + "  UNION ALL "
                + "  SELECT DATEADD(day, -1, d), n + 1 FROM d WHERE n < 6 "
                + ") "
                + "SELECT CAST(d.d AS date) AS orderDay, ISNULL(SUM(oi.finalPrice), 0) AS totalRevenue "
                + "FROM d "
                + "LEFT JOIN OrderInfo oi "
                + "  ON CAST(oi.orderDate AS date) = CAST(d.d AS date) "
                + " AND oi.orderStatusID = 1 "
                + "GROUP BY CAST(d.d AS date) "
                + "ORDER BY orderDay ASC "
                + "OPTION (MAXRECURSION 100);";

        List<WeeklyRevenue> result = new ArrayList<>();
        try ( Connection connect = DBConnection.getConnection();  PreparedStatement ps = connect.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LocalDate day = rs.getDate("orderDay").toLocalDate();
                double total = rs.getDouble("totalRevenue");
                result.add(new WeeklyRevenue(day, total));
            }
        } catch (Exception ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
