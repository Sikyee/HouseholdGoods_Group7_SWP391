/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DB.DBConnection;
import Model.OrderInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Model.Order;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

/**
 *
 * @author thach
 */
public class OrderDAO {

    private Connection conn;

    public OrderDAO() {
        try {
            conn = DBConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int createOrder(Order order) throws Exception {
        String sql = "INSERT INTO OrderInfo (userID, orderStatusID, orderDate, paymentMethodID, voucherID, totalPrice, finalPrice, fullName, deliveryAddress, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try ( PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getUserID());
            ps.setInt(2, order.getOrderStatusID());
            ps.setDate(3, (Date) order.getOrderDate());
            ps.setInt(4, order.getPaymentMethodID());
            ps.setObject(5, order.getVoucherID() == 0 ? null : order.getVoucherID());
            ps.setFloat(6, order.getTotalPrice());
            ps.setFloat(7, order.getFinalPrice());
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

    public List<OrderInfo> getAllOrders() throws SQLException {
        List<OrderInfo> list = new ArrayList<>();
        String sql = "SELECT * FROM OrderInfo WHERE isDeleted = 0";
        try ( Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapOrder(rs));
            }
        }
        return list;
    }

    public List<OrderInfo> getDeletedOrders() throws SQLException {
        List<OrderInfo> list = new ArrayList<>();
        String sql = "SELECT * FROM OrderInfo WHERE isDeleted = 1";
        try ( Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapOrder(rs));
            }
        }
        return list;
    }

    public List<OrderInfo> searchOrders(String keyword) throws SQLException {
        List<OrderInfo> list = new ArrayList<>();
        String sql = "SELECT * FROM OrderInfo WHERE isDeleted = 0 AND (CAST(orderID AS VARCHAR) LIKE ? OR deliveryAddress LIKE ?)";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapOrder(rs));
            }
        }
        return list;
    }

    public OrderInfo getOrderById(int id) throws SQLException {
        String sql = "SELECT * FROM OrderInfo WHERE orderID = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapOrder(rs);
            }
        }
        return null;
    }

    public void updateStatus(int orderID, int statusID) throws SQLException {
        String sql = "UPDATE OrderInfo SET statusID = ? WHERE orderID = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statusID);
            ps.setInt(2, orderID);
            ps.executeUpdate();
        }
    }

    public void cancelOrder(int orderID) throws SQLException {
        updateStatus(orderID, 5); // Canceled
    }

    public void deleteOrder(int orderID) throws SQLException {
        String sql = "UPDATE OrderInfo SET isDeleted = 1 WHERE orderID = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            ps.executeUpdate();
        }
    }

    private OrderInfo mapOrder(ResultSet rs) throws SQLException {
        OrderInfo o = new OrderInfo();
        o.setOrderID(rs.getInt("orderID"));
        o.setUserID(rs.getInt("UserID"));
        o.setOrderStatusID(rs.getInt("OrderStatusID"));
        o.setOrderDate(rs.getDate("orderDate"));
        o.setPaymentMethodID(rs.getInt("paymentMethodID"));
        o.setVoucherID(rs.getInt("voucherID"));
        o.setTotalPrice(rs.getDouble("totalPrice"));
        o.setFinalPrice(rs.getDouble("finalPrice"));
        o.setFullName(rs.getString("fullName"));
        o.setDeliveryAddress(rs.getString("DeliveryAddress"));
        o.setPhone(rs.getString("phone"));
        o.setIsDeleted(rs.getBoolean("isDeleted"));
        return o;
    }

    public void restoreOrder(int orderID) throws SQLException {
        String sql = "UPDATE OrderInfo SET isDeleted = 0 WHERE orderID = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            ps.executeUpdate();
        }
    }

}
