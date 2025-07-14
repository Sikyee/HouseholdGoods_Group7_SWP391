/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

/**
 *
 * @author TriTM
 */
import DB.DBConnection;
import Model.Promotion;

import java.sql.*;
import java.util.*;

public class PromotionDAO {
    private Connection connection;

    public PromotionDAO() throws Exception {
        this.connection = DBConnection.getConnection();
    }

    public List<Promotion> getAllPromotions() throws SQLException {
        List<Promotion> promotions = new ArrayList<>();
        String sql = "SELECT * FROM Promotion WHERE isActive = 1";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                promotions.add(extractPromotion(rs));
            }
        }
        return promotions;
    }

    public List<Promotion> getDeletedPromotions() throws SQLException {
        List<Promotion> deleted = new ArrayList<>();
        String sql = "SELECT * FROM Promotion WHERE isActive = 0";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                deleted.add(extractPromotion(rs));
            }
        }
        return deleted;
    }

    public Promotion getPromotionById(int id) throws SQLException {
        String sql = "SELECT * FROM Promotion WHERE voucherID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extractPromotion(rs);
        }
        return null;
    }

    public Promotion getPromotionByCode(String code) throws SQLException {
        String sql = "SELECT * FROM Promotion WHERE code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extractPromotion(rs);
        }
        return null;
    }

    public void addPromotion(Promotion p) throws SQLException {
        String sql = "INSERT INTO Promotion (code, description, discountType, discountValue, startDate, endDate, minOrderValue, maxUsage, usedCount, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            prepareStatement(ps, p);
            ps.executeUpdate();
        }
    }

    public void updatePromotion(Promotion p) throws SQLException {
        String sql = "UPDATE Promotion SET code=?, description=?, discountType=?, discountValue=?, startDate=?, endDate=?, minOrderValue=?, maxUsage=?, usedCount=?, isActive=? WHERE voucherID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            prepareStatement(ps, p);
            ps.setInt(11, p.getPromotionID());
            ps.executeUpdate();
        }
    }

    public void softDeletePromotion(int id) throws SQLException {
        String sql = "UPDATE Promotion SET isActive = 0 WHERE voucherID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void reactivatePromotion(int id) throws SQLException {
        String sql = "UPDATE Promotion SET isActive = 1 WHERE voucherID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Promotion extractPromotion(ResultSet rs) throws SQLException {
        Promotion p = new Promotion();
        p.setPromotionID(rs.getInt("voucherID"));
        p.setCode(rs.getString("code"));
        p.setDescription(rs.getString("description"));
        p.setDiscountType(rs.getString("discountType"));
        p.setDiscountValue(rs.getLong("discountValue"));
        p.setStartDate(rs.getDate("startDate"));
        p.setEndDate(rs.getDate("endDate"));
        p.setMinOrderValue(rs.getLong("minOrderValue"));
        p.setMaxUsage(rs.getInt("maxUsage"));
        p.setUsedCount(rs.getInt("usedCount"));
        p.setIsActive(rs.getBoolean("isActive"));
        return p;
    }

    private void prepareStatement(PreparedStatement ps, Promotion p) throws SQLException {
        ps.setString(1, p.getCode());
        ps.setString(2, p.getDescription());
        ps.setString(3, p.getDiscountType());
        ps.setLong(4, p.getDiscountValue());
        ps.setDate(5, p.getStartDate());
        ps.setDate(6, p.getEndDate());
        ps.setLong(7, p.getMinOrderValue());
        ps.setInt(8, p.getMaxUsage());
        ps.setInt(9, p.getUsedCount());
        ps.setBoolean(10, p.isIsActive());
    }
}
