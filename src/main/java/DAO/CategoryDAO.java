/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package DAO;

import DB.DBConnection;
import Model.Category;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        String sql = "SELECT \n"
                + "    c.categoryName,\n"
                + "    sc.subCategoryName"
                + "FROM \n"
                + "    [dbo].[Category] c"
                + "LEFT JOIN [dbo].[SubCategory] sc ON c.categoryID = sc.categoryID"
                + "WHERE \n"
                + "    c.categoryID = sc.categoryID;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Category c = new Category();
            c.setCategoryID(rs.getInt("categoryID"));
            c.setCategoryName(rs.getString("categoryName"));
            c.setSubCategoryName(rs.getString("subCategoryName"));
            list.add(c);
        }
        return list;
    }

}
