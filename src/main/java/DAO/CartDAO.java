/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package DAO;

import DB.DBConnection;
import Model.Cart;
import Model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartDAO {

    public List<Cart> getProductInCart(int userID) throws Exception {
        List<Cart> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();

        String sql = "SELECT c.cartID, c.quantity, c.productID, c.userID, "
                + "p.productName, p.price, p.image, p.description, p.subCategory, p.brandID, p.stonk_Quantity "
                + "FROM Cart c JOIN Product p ON c.productID = p.productID "
                + "WHERE c.userID = ?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userID);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Product p = new Product();
            p.setProductID(rs.getInt("productID"));
            p.setProductName(rs.getString("productName"));
            p.setPrice(rs.getLong("price"));
            p.setImage(rs.getString("image"));
            p.setDescription(rs.getString("description"));
            p.setSubCategory(rs.getInt("subCategory"));
            p.setBrandID(rs.getInt("brandID"));
            p.setStonkQuantity(rs.getInt("stonk_Quantity"));

            Cart cart = new Cart();
            cart.setCartID(rs.getInt("cartID"));
            cart.setProductID(rs.getInt("productID"));
            cart.setUserID(rs.getInt("userID"));
            cart.setQuantity(rs.getInt("quantity"));
            cart.setProduct(p);

            list.add(cart);
        }

        rs.close();
        ps.close();
        conn.close();

        return list;
    }

    public void deleteProductInCart(int id) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "DELETE FROM Cart WHERE cartID = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public void addProductToCart(Cart t) throws Exception {
        Connection conn = DBConnection.getConnection();

        // Check if product already in cart
        String checkSql = "SELECT quantity FROM Cart WHERE productID = ? AND userID = ?";
        PreparedStatement checkPs = conn.prepareStatement(checkSql);
        checkPs.setInt(1, t.getProductID());
        checkPs.setInt(2, t.getUserID());
        ResultSet rs = checkPs.executeQuery();

        if (rs.next()) {
            int existingQty = rs.getInt("quantity");
            String updateSql = "UPDATE Cart SET quantity = ? WHERE productID = ? AND userID = ?";
            PreparedStatement updatePs = conn.prepareStatement(updateSql);
            updatePs.setInt(1, existingQty + t.getQuantity());
            updatePs.setInt(2, t.getProductID());
            updatePs.setInt(3, t.getUserID());
            updatePs.executeUpdate();
            updatePs.close();
        } else {
            String insertSql = "INSERT INTO Cart (productID, quantity, userID) VALUES (?, ?, ?)";
            PreparedStatement insertPs = conn.prepareStatement(insertSql);
            insertPs.setInt(1, t.getProductID());
            insertPs.setInt(2, t.getQuantity());
            insertPs.setInt(3, t.getUserID());
            insertPs.executeUpdate();
            insertPs.close();
        }

        rs.close();
        checkPs.close();
        conn.close();
    }

    public void increaseQuantityInCart(Cart t) throws Exception {
        Connection conn = DBConnection.getConnection();

        // Check if product already in cart
        String checkSql = "SELECT quantity FROM Cart WHERE cartID = ?";
        PreparedStatement checkPs = conn.prepareStatement(checkSql);
        checkPs.setInt(1, t.getCartID());
        ResultSet rs = checkPs.executeQuery();

        if (rs.next()) {
            int existingQty = rs.getInt("quantity");
            String updateSql = "UPDATE Cart SET quantity = ? WHERE cartID = ?";
            PreparedStatement updatePs = conn.prepareStatement(updateSql);
            updatePs.setInt(1, existingQty + 1);
            updatePs.setInt(2, t.getCartID());
            updatePs.executeUpdate();
            updatePs.close();
        }
    }

    public void decreaseQuantityByCartID(int cartID) throws Exception {
        Connection conn = DBConnection.getConnection();

        String checkSql = "SELECT quantity FROM Cart WHERE cartID = ?";
        PreparedStatement checkPs = conn.prepareStatement(checkSql);
        checkPs.setInt(1, cartID);
        ResultSet rs = checkPs.executeQuery();

        if (rs.next()) {
            int qty = rs.getInt("quantity");
            if (qty > 1) {
                String updateSql = "UPDATE Cart SET quantity = ? WHERE cartID = ?";
                PreparedStatement updatePs = conn.prepareStatement(updateSql);
                updatePs.setInt(1, qty - 1);
                updatePs.setInt(2, cartID);
                updatePs.executeUpdate();
                updatePs.close();
            } else {
                String deleteSql = "DELETE FROM Cart WHERE cartID = ?";
                PreparedStatement deletePs = conn.prepareStatement(deleteSql);
                deletePs.setInt(1, cartID);
                deletePs.executeUpdate();
                deletePs.close();
            }
        }

        rs.close();
        checkPs.close();
        conn.close();
    }

    public void deleteCartItemsByIds(int userId, List<Integer> cartIds) throws Exception {
        if (cartIds == null || cartIds.isEmpty()) {
            return;
        }

        String placeholders = String.join(",", Collections.nCopies(cartIds.size(), "?"));
        String sql = "DELETE FROM Cart WHERE userID = ? AND cartID IN (" + placeholders + ")";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int idx = 2;
            for (Integer id : cartIds) {
                ps.setInt(idx++, id);
            }
            ps.executeUpdate();
        }
    }

    public Cart getCartItemById(int cartId) throws Exception {
        String sql = " SELECT c.CartID, c.UserID, c.ProductID, c.Quantity, p.ProductName, p.Price, p.Image, p.stonk_Quantity FROM Cart c JOIN Product p ON p.ProductID = c.ProductID WHERE c.CartID = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cartId);

            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // map Product
                    Product p = new Product();
                    p.setProductID(rs.getInt("productID"));
                    p.setProductName(rs.getString("productName"));
                    p.setPrice(rs.getLong("price"));
                    p.setImage(rs.getString("image"));
                    p.setStonkQuantity(rs.getInt("stonk_Quantity"));

                    // map Cart
                    Cart cart = new Cart();
                    cart.setCartID(rs.getInt("cartID"));
                    cart.setUserID(rs.getInt("userID"));
                    cart.setProductID(rs.getInt("productID"));
                    cart.setQuantity(rs.getInt("quantity"));
                    cart.setProduct(p);

                    return cart;
                }
            }
        }
        return null; // không tìm thấy
    }
}
