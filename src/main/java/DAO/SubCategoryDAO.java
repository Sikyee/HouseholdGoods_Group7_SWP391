/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DB.DBConnection;
import Model.SubCategory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pts03
 */
public class SubCategoryDAO extends DBConnection{
     public List<SubCategory> getAllSubCategories() throws Exception {
        List<SubCategory> list = new ArrayList<>();
        String sql = "SELECT SubCategoryID, SubCategoryName FROM SubCategory";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SubCategory sc = new SubCategory(
                        rs.getInt("SubCategoryID"),
                        rs.getString("SubCategoryName")
                );
                list.add(sc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public SubCategory getSubCategoryById(int id) throws Exception {
        String sql = "SELECT SubCategoryID, SubCategoryName FROM SubCategories WHERE SubCategory=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new SubCategory(rs.getInt("SubCategoryID"), rs.getString("SubCategoryName"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
