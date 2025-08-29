package DAO;

import DB.DBConnection;
import Model.Inventory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    public List<Inventory> getAllProducts() {
        List<Inventory> list = new ArrayList<>();
    String sql = "SELECT p.productName, b.brandName, " +
                 "SUM(p.stonk_Quantity) AS totalQuantity, " +
                 "MAX(p.status) AS status " +
                 "FROM Product p " +
                 "JOIN Brand b ON p.brandID = b.brandID " +
                 "GROUP BY p.productName, b.brandName";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Inventory inv = new Inventory();
            inv.setProductName(rs.getString("productName"));
            inv.setBrandName(rs.getString("brandName"));
            inv.setQuantity(rs.getInt("totalQuantity"));
            inv.setStatus(rs.getInt("status")); 
            list.add(inv);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}
}
