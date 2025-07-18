/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DB.DBConnection;
import Model.OrderDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TriTN
 */
public class OrderDetailDAO {

    private Connection conn;

    public OrderDetailDAO() throws Exception {
        conn = DBConnection.getConnection();
    }

    public List<OrderDetail> getDetailsByOrderID(int orderID) throws SQLException {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM OrderDetail WHERE orderID = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderDetail detail = new OrderDetail();
                detail.setOrderDetailID(rs.getInt("orderDetailID"));
                detail.setProductID(rs.getInt("productID"));
                detail.setOrderID(rs.getInt("orderID"));
                detail.setOrderName(rs.getString("orderName"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setTotalPrice(rs.getDouble("totalPrice"));
                list.add(detail);
            }
        }
        return list;
    }

    public void insertOrderDetails(List<OrderDetail> details) throws SQLException {
        String sql = "INSERT INTO OrderDetail (orderID, productID, orderName, quantity, totalPrice) VALUES (?, ?, ?, ?, ?)";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            for (OrderDetail detail : details) {
                ps.setInt(1, detail.getOrderID());
                ps.setInt(2, detail.getProductID());
                ps.setString(3, detail.getOrderName());
                ps.setInt(4, detail.getQuantity());
                ps.setDouble(5, detail.getTotalPrice());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
