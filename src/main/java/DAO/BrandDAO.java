/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

/**
 *
 * @author TriTM
 */

import Model.Brand;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import DB.DBConnection;

public class BrandDAO {
    private Connection connection;

    public BrandDAO() throws Exception {
        this.connection = DBConnection.getConnection();
    }

    public List<Brand> getAllBrands() throws SQLException {
        List<Brand> list = new ArrayList<>();
        String sql = "SELECT brandID, brandName FROM Brand";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Brand(rs.getInt("brandID"), rs.getString("brandName")));
        }
        return list;
    }
}
