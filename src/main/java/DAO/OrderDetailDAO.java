/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Model.OrderDetail;
import DB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TriTM
 */
public class OrderDetailDAO {

    // Lấy danh sách chi tiết đơn hàng theo orderID
    public List<OrderDetail> getOrderDetailsByOrderID(int orderID) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM OrderDetail WHERE orderID = ?";
        try (
                 Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
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

    // Thêm chi tiết đơn hàng (nếu cần dùng insert)
    public void insertOrderDetails(List<OrderDetail> details) {
        String sql = "INSERT INTO OrderDetail (productID, orderID, orderName, quantity, totalPrice) VALUES (?, ?, ?, ?, ?)";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            for (OrderDetail od : details) {
                ps.setInt(1, od.getProductID());
                ps.setInt(2, od.getOrderID());
                ps.setString(3, od.getOrderName());
                ps.setInt(4, od.getQuantity());
                ps.setLong(5, od.getTotalPrice());
                ps.addBatch();
            }

            ps.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<OrderDetail> getOrderDetailsByOrderID(String orderId) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM OrderDetail WHERE orderID = ?";
        try (
                 Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
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

}
