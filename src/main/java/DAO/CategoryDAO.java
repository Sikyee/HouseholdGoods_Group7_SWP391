/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package DAO;

import DB.DBConnection;
import Model.Category;
import Model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class CategoryDAO {

    public List<Category> getAllCategories() throws Exception {
        List<Category> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT c.categoryName, sc.subCategoryName, sc.subCategoryID, sc.categoryID FROM [dbo].[Category] c LEFT JOIN [dbo].[SubCategory] sc ON c.categoryID = sc.categoryID WHERE c.categoryID = sc.categoryID AND sc.isDelete = 0;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Category c = new Category();
            c.setSubCategoryID(rs.getInt("subCategoryID"));
            c.setCategoryName(rs.getString("categoryName"));
            c.setSubCategoryName(rs.getString("subCategoryName"));
            list.add(c);
        }
        return list;
    }

    public void addCategory(Category c) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO SubCategory (categoryID, subCategoryName) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, c.getCategoryID());
        ps.setString(2, c.getSubCategoryName());
        ps.executeUpdate();
    }

    public void deleteCategory(int id) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "UPDATE subCategory SET isDelete = 1 WHERE subCategoryID = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public Category getSubCategoryByID(int id) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT c.categoryName, sc.subCategoryName, sc.subCategoryID, sc.categoryID "
                + "FROM [dbo].[Category] c "
                + "JOIN [dbo].[SubCategory] sc ON c.categoryID = sc.categoryID "
                + "WHERE sc.subCategoryID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Category c = new Category();
            c.setSubCategoryID(rs.getInt("subCategoryID"));
            c.setCategoryID(rs.getInt("categoryID"));
            c.setCategoryName(rs.getString("categoryName"));
            c.setSubCategoryName(rs.getString("subCategoryName"));
            return c;
        }

        return null;
    }

    public List<Category> getAllParentCategories() throws Exception {
        List<Category> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT categoryID, categoryName FROM Category";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Category c = new Category();
            c.setCategoryID(rs.getInt("categoryID"));
            c.setCategoryName(rs.getString("categoryName"));
            list.add(c);
        }

        return list;
    }

    public void updateCategory(Category c) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "UPDATE SubCategory SET categoryID=?, subcategoryName=? WHERE subCategoryID=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, c.getCategoryID());
        ps.setString(2, c.getSubCategoryName());
        ps.setInt(3, c.getSubCategoryID());
        ps.executeUpdate();
    }

    public List<Category> getAllMainCategories() throws Exception {
        List<Category> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM Category WHERE isDelete = 0";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Category c = new Category();
            c.setCategoryID(rs.getInt("categoryID"));
            c.setCategoryName(rs.getString("categoryName"));
            list.add(c);
        }
        return list;
    }

    public Category getMainCategoryById(int id) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM Category WHERE categoryID=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Category c = new Category();
            c.setCategoryID(rs.getInt("categoryID"));
            c.setCategoryName(rs.getString("categoryName"));
            return c;
        }
        return null;
    }

    public void addMainCategory(Category p) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO Category (categoryName) VALUES (?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, p.getCategoryName());
        ps.executeUpdate();
    }

    public void updateMainCategory(Category p) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "UPDATE Category SET categoryName=? WHERE categoryID=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, p.getCategoryName());
        ps.setInt(2, p.getCategoryID());
        ps.executeUpdate();
    }

    public void deleteMainCategory(int id) throws Exception {
        Connection conn = DBConnection.getConnection();

        // Xóa mềm Category
        String sql1 = "UPDATE Category SET isDelete = 1 WHERE categoryID = ?";
        PreparedStatement ps1 = conn.prepareStatement(sql1);
        ps1.setInt(1, id);
        ps1.executeUpdate();

        // Xóa mềm tất cả SubCategory thuộc Category đó
        String sql2 = "UPDATE SubCategory SET isDelete = 1 WHERE categoryID = ?";
        PreparedStatement ps2 = conn.prepareStatement(sql2);
        ps2.setInt(1, id);
        ps2.executeUpdate();

        // Đóng kết nối
        ps1.close();
        ps2.close();
        conn.close();
    }
    

}
