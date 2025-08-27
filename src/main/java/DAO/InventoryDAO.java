/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DB.DBConnection;
import Model.Inventory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pts03
 */
public class InventoryDAO {
   public List<Inventory> getAllProducts() {
    List<Inventory> list = new ArrayList<>();
    String sql = "SELECT productID, productName, stonk_Quantity, status FROM Product WHERE status = 1";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            Inventory p = new Inventory(
                rs.getInt("productID"),
                rs.getString("productName"),
                rs.getInt("stonk_Quantity"),
                rs.getInt("status")
            );
            list.add(p);
            System.out.println("DEBUG DAO → " + p.getProductName() + " | Qty: " + p.getQuantity());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    System.out.println("DEBUG DAO → total rows fetched = " + list.size());
    return list;
}
}
