package DAO;

import Model.OrderStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import DB.DBConnection;

public class OrderStatusDAO {

    private Connection conn;

    public OrderStatusDAO() {
        try {
            conn = DBConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<OrderStatus> getAllOrderStatuses() {
        List<OrderStatus> list = new ArrayList<>();
        String sql = "SELECT orderStatusID, statusName FROM OrderStatus";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                OrderStatus status = new OrderStatus();
                status.setOrderStatusID(rs.getInt("orderStatusID"));
                status.setStatusName(rs.getString("statusName"));
                list.add(status);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
