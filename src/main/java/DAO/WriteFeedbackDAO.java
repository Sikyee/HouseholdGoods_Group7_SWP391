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

    public List<OrderDetail> getCompletedOrderDetailsByUser(int userID) {
    List<OrderDetail> list = new ArrayList<>();
    String sql = "SELECT od.* FROM OrderDetail od " +
             "JOIN OrderInfo o ON od.orderID = o.orderID " +
             "WHERE o.userID = ? AND o.orderStatusID = 5";

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
            od.setTotalPrice(rs.getLong("totalPrice"));
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
    
    public OrderDetail getOrderDetailByID(int orderDetailID) {
    String sql = "SELECT * FROM OrderDetail WHERE orderDetailID = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, orderDetailID);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            OrderDetail od = new OrderDetail();
            od.setOrderDetailID(rs.getInt("orderDetailID"));
            od.setProductID(rs.getInt("productID"));
            od.setOrderID(rs.getInt("orderID"));
            od.setOrderName(rs.getString("orderName"));
            od.setQuantity(rs.getInt("quantity"));
            od.setTotalPrice(rs.getLong("totalPrice"));
            return od;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}
    
    public boolean isOrderDetailValidForFeedback(int userID, int orderDetailID) {
    String sql = "SELECT COUNT(*) " +
                 "FROM OrderDetail od " +
                 "JOIN OrderInfo o ON od.orderID = o.orderID " +
                 "WHERE od.orderDetailID = ? " +
                 "AND o.userID = ? " +
                 "AND o.orderStatusID = 5"; // 5 = Completed

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, orderDetailID);
        ps.setInt(2, userID);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}

public boolean hasFeedback(int orderDetailID) {
    String sql = "SELECT COUNT(*) FROM Feedback WHERE orderDetailID = ? AND isDeleted = 0";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, orderDetailID);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}

public Feedback getFeedbackByOrderDetailID(int orderDetailID) {
    String sql = "SELECT TOP 1 * FROM Feedback WHERE orderDetailID = ? AND isDeleted = 0";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, orderDetailID);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Feedback fb = new Feedback();
            fb.setFeedbackID(rs.getInt("feedbackID"));
            fb.setUserID(rs.getInt("userID"));
            fb.setOrderDetailID(rs.getInt("orderDetailID"));
            fb.setRating(rs.getInt("rating"));
            fb.setComment(rs.getString("comment"));
            fb.setCreatedAt(rs.getTimestamp("createdAt"));
            fb.setStatus(rs.getString("status"));
            
            return fb;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}


}
