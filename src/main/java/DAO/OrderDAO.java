/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DB.DBConnection;
import Model.Order;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

/**
 *
 * @author Admin
 */
public class OrderDAO {
    
    public void createOrder(Order order) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO OrderInfo (userID, orderStatusID, orderDate, paymentMethodID, voucherID, totalPrice, finalPrice, fullName, deliveryAddress, phone) VALUES (?, ?, ?, 1, NULL, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, order.getUserID());
        ps.setInt(2, order.getOrderStatusID());
        ps.setDate(3, (Date) order.getOrderDate());
        ps.setInt(4, order.getPaymentMethodID());
        ps.setInt(5, order.getOrderStatusID());
        ps.setInt(6, order.getVoucherID());
        ps.setFloat(7, order.getTotalPrice());
        ps.setFloat(8, order.getFinalPrice());
        ps.setString(9, order.getFullName());
        ps.setString(10, order.getDeliveryAddress());
        ps.setString(11, order.getPhone());
        ps.executeUpdate();
    }
}
