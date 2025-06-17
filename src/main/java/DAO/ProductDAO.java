/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DB.DBConnection;
import Model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thong
 */
public class ProductDAO {

    public List<Product> getAllProducts() throws Exception {
        List<Product> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM Product";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Product p = new Product();
            p.setProductID(rs.getInt("productID"));
            p.setProductName(rs.getString("productName"));
            p.setDescription(rs.getString("description"));
            p.setSubCategory(rs.getInt("subCategory"));
            p.setPrice(rs.getLong("price"));
            p.setStonkQuantity(rs.getInt("stonk_Quantity"));
            p.setBrandID(rs.getInt("brandID"));
            p.setImage(rs.getString("image"));
            list.add(p);
        }
        return list;
    }

    public void addProduct(Product p) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO Product (productName, description, subCategory, price, stonk_Quantity, brandID, image) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, p.getProductName());
        ps.setString(2, p.getDescription());
        ps.setInt(3, p.getSubCategory());
        ps.setLong(4, p.getPrice());
        ps.setInt(5, p.getStonkQuantity());
        ps.setInt(6, p.getBrandID());
        ps.setString(7, p.getImage());
        ps.executeUpdate();
    }

    public void updateProduct(Product p) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "UPDATE Product SET productName=?, description=?, subCategory=?, price=?, stonk_Quantity=?, brandID=?, image=? WHERE productID=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, p.getProductName());
        ps.setString(2, p.getDescription());
        ps.setInt(3, p.getSubCategory());
        ps.setLong(4, p.getPrice());
        ps.setInt(5, p.getStonkQuantity());
        ps.setInt(6, p.getBrandID());
        ps.setString(7, p.getImage());
        ps.setInt(8, p.getProductID());
        ps.executeUpdate();
    }

    public void deleteProduct(int id) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "DELETE FROM Product WHERE productID=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public Product getProductById(int id) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM Product WHERE productID=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Product p = new Product();
            p.setProductID(rs.getInt("productID"));
            p.setProductName(rs.getString("productName"));
            p.setDescription(rs.getString("description"));
            p.setSubCategory(rs.getInt("subCategory"));
            p.setPrice(rs.getLong("price"));
            p.setStonkQuantity(rs.getInt("stonk_Quantity"));
            p.setBrandID(rs.getInt("brandID"));
            p.setImage(rs.getString("image"));
            return p;
        }
        return null;
    }
}
