package DAO;

import DB.DBConnection;
import Model.OrderInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public int createOrder(OrderInfo order) {
        String sql = "INSERT INTO OrderInfo (userID, orderStatusID, orderDate, paymentMethodID, voucherID, totalPrice, finalPrice, fullName, deliveryAddress, phone) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";

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

            ps.setBigDecimal(6, order.getTotalPrice());
            ps.setBigDecimal(7, order.getFinalPrice());
            ps.setString(8, order.getFullName());
            ps.setString(9, order.getDeliveryAddress());
            ps.setString(10, order.getPhone());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Trả về orderID vừa được tạo
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1; // Lỗi khi tạo
    }

    private OrderInfo extractOrderFromResultSet(ResultSet rs) throws SQLException {
        OrderInfo order = new OrderInfo();
        order.setOrderID(rs.getInt("orderID"));
        order.setUserID(rs.getInt("userID"));
        order.setOrderStatusID(rs.getInt("orderStatusID"));
        order.setOrderDate(rs.getTimestamp("orderDate"));
        order.setPaymentMethodID(rs.getInt("paymentMethodID"));
        order.setVoucherID(rs.getObject("voucherID") != null ? rs.getInt("voucherID") : null);
        order.setTotalPrice(rs.getBigDecimal("totalPrice"));
        order.setFinalPrice(rs.getBigDecimal("finalPrice"));
        order.setFullName(rs.getString("fullName"));
        order.setDeliveryAddress(rs.getString("deliveryAddress"));
        order.setPhone(rs.getString("phone"));
        return order;
    }
}
