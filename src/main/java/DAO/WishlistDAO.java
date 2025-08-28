/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DB.DBConnection;
import Model.Wishlist;
import Model.Product;
import java.sql.*;
import java.util.*;

/**
 *
 * @author thach
 */

public class WishlistDAO {

  public void addToWishlist(Wishlist w) throws Exception {
    String checkSql = "SELECT quantity FROM Wishlist WHERE userID=? AND productID=?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
        psCheck.setInt(1, w.getUserID());
        psCheck.setInt(2, w.getProductID());
        ResultSet rs = psCheck.executeQuery();
        if (rs.next()) {
            // tồn tại → update
            int newQty = rs.getInt("quantity") + w.getQuantity();
            String updateSql = "UPDATE Wishlist SET quantity=? WHERE userID=? AND productID=?";
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setInt(1, newQty);
                psUpdate.setInt(2, w.getUserID());
                psUpdate.setInt(3, w.getProductID());
                psUpdate.executeUpdate();
            }
        } else {
            // chưa tồn tại → insert
            String insertSql = "INSERT INTO Wishlist(userID, productID, quantity) VALUES(?,?,?)";
            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setInt(1, w.getUserID());
                psInsert.setInt(2, w.getProductID());
                psInsert.setInt(3, w.getQuantity());
                psInsert.executeUpdate();
            }
        }
    }
}


    public List<Wishlist> getWishlistByUser(int userID) throws Exception {
        String sql = "SELECT w.*, p.productName, p.price, p.image FROM Wishlist w " +
                     "JOIN Product p ON w.productID=p.productID WHERE w.userID=?";
        List<Wishlist> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Wishlist w = new Wishlist();
                w.setWishlistID(rs.getInt("wishlistID"));
                w.setUserID(rs.getInt("userID"));
                w.setProductID(rs.getInt("productID"));
                w.setQuantity(rs.getInt("quantity"));

                Product p = new Product();
                p.setProductID(rs.getInt("productID"));
                p.setProductName(rs.getString("productName"));
                p.setPrice(rs.getLong("price"));
                p.setImage(rs.getString("image"));

                w.setProduct(p);
                list.add(w);
            }
        }
        return list;
    }

    public void removeFromWishlist(int wishlistID) throws Exception {
        String sql = "DELETE FROM Wishlist WHERE wishlistID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wishlistID);
            ps.executeUpdate();
        }
    }
    
    public void updateQuantity(int wishlistID, int quantity) throws Exception {
    String sql = "UPDATE Wishlist SET quantity=? WHERE wishlistID=?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, quantity);
        ps.setInt(2, wishlistID);
        ps.executeUpdate();
    }
}

}
