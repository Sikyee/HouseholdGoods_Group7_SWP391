/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DB.DBConnection;
import Model.Feedback;
import Model.OrderDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TriTN
 */

public class WriteFeedbackDAO {

    public List<OrderDetail> getOrderDetailsByUser(int userID) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT od.* FROM OrderDetail od " +
                     "JOIN OrderInfo o ON od.orderID = o.orderID " +
                     "WHERE o.userID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderDetail od = new OrderDetail();
                od.setOrderDetailID(rs.getInt("orderDetailID"));
                od.setProductID(rs.getInt("productID"));
                od.setOrderID(rs.getInt("orderID"));
                od.setOrderName(rs.getString("orderName"));
                od.setQuantity(rs.getInt("quantity"));
                od.setTotalPrice((long) rs.getDouble("totalPrice"));
                list.add(od);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertFeedback(Feedback fb) {
        String sql = "INSERT INTO Feedback (userID, orderDetailID, rating, comment, createdAt, status, isDeleted) " +
                     "VALUES (?, ?, ?, ?, GETDATE(), 'Pending', 0)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, fb.getUserID());
            ps.setInt(2, fb.getOrderDetailID());
            ps.setInt(3, fb.getRating());
            ps.setString(4, fb.getComment());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
