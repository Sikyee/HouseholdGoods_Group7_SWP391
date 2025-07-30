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
import Model.Voucher;

import java.sql.*;
import java.util.*;

public class VoucherDAO {
    private Connection connection;

    public VoucherDAO() throws Exception {
        this.connection = DBConnection.getConnection();
    }

    public List<Voucher> getAllVouchers() throws SQLException {
        List<Voucher> vouchers = new ArrayList<>();
        String sql = "SELECT * FROM Voucher WHERE isActive = 1";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vouchers.add(extractVoucher(rs));
            }
        }
        return vouchers;
    }

    public List<Voucher> getDeletedVouchers() throws SQLException {
        List<Voucher> deleted = new ArrayList<>();
        String sql = "SELECT * FROM Voucher WHERE isActive = 0";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                deleted.add(extractVoucher(rs));
            }
        }
        return deleted;
    }

    public Voucher getVoucherById(int id) throws SQLException {
        String sql = "SELECT * FROM Voucher WHERE voucherID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extractVoucher(rs);
        }
        return null;
    }

    public Voucher getVoucherByCode(String code) throws SQLException {
        String sql = "SELECT * FROM Voucher WHERE code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extractVoucher(rs);
        }
        return null;
    }

    public void addVoucher(Voucher p) throws SQLException {
        String sql = "INSERT INTO Voucher (code, description, discountType, discountValue, startDate, endDate, minOrderValue, maxUsage, usedCount, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            prepareStatement(ps, p);
            ps.executeUpdate();
        }
    }

    public void updateVoucher(Voucher p) throws SQLException {
        String sql = "UPDATE Voucher SET code=?, description=?, discountType=?, discountValue=?, startDate=?, endDate=?, minOrderValue=?, maxUsage=?, usedCount=?, isActive=? WHERE voucherID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            prepareStatement(ps, p);
            ps.setInt(11, p.getVoucherID());
            ps.executeUpdate();
        }
    }

    public void softDeleteVoucher(int id) throws SQLException {
        String sql = "UPDATE Voucher SET isActive = 0 WHERE voucherID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void reactivateVoucher(int id) throws SQLException {
        String sql = "UPDATE Voucher SET isActive = 1 WHERE voucherID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Voucher extractVoucher(ResultSet rs) throws SQLException {
        Voucher p = new Voucher();
        p.setVoucherID(rs.getInt("voucherID"));
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

    private void prepareStatement(PreparedStatement ps, Voucher p) throws SQLException {
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
