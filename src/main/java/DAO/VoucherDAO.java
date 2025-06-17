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
import java.util.ArrayList;
import java.util.List;

public class VoucherDAO {

    public List<Voucher> getAll() {
        List<Voucher> list = new ArrayList<>();
        String sql = "SELECT * FROM vouchers";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Voucher v = new Voucher();
                v.setVoucherID(rs.getInt("voucherID"));
                v.setCode(rs.getString("code"));
                v.setDescription(rs.getString("description"));
                v.setDiscountType(rs.getString("discountType"));
                v.setDiscountValue(rs.getInt("discountValue"));
                v.setStartDate(rs.getDate("startDate"));
                v.setEndDate(rs.getDate("endDate"));
                v.setMinOrderValue(rs.getInt("minOrderValue"));
                v.setMaxUsage(rs.getInt("maxUsage"));
                v.setUsedCount(rs.getInt("usedCount"));
                v.setIsActive(rs.getBoolean("isActive"));
                list.add(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Voucher getById(int id) {
        String sql = "SELECT * FROM vouchers WHERE voucherID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Voucher v = new Voucher();
                v.setVoucherID(rs.getInt("voucherID"));
                v.setCode(rs.getString("code"));
                v.setDescription(rs.getString("description"));
                v.setDiscountType(rs.getString("discountType"));
                v.setDiscountValue(rs.getInt("discountValue"));
                v.setStartDate(rs.getDate("startDate"));
                v.setEndDate(rs.getDate("endDate"));
                v.setMinOrderValue(rs.getInt("minOrderValue"));
                v.setMaxUsage(rs.getInt("maxUsage"));
                v.setUsedCount(rs.getInt("usedCount"));
                v.setIsActive(rs.getBoolean("isActive"));
                return v;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Voucher v) {
        String sql = "INSERT INTO vouchers (code, description, discountType, discountValue, startDate, endDate, minOrderValue, maxUsage, usedCount, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getCode());
            ps.setString(2, v.getDescription());
            ps.setString(3, v.getDiscountType());
            ps.setInt(4, v.getDiscountValue());
            ps.setDate(5, v.getStartDate());
            ps.setDate(6, v.getEndDate());
            ps.setInt(7, v.getMinOrderValue());
            ps.setInt(8, v.getMaxUsage());
            ps.setInt(9, v.getUsedCount());
            ps.setBoolean(10, v.isIsActive());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Voucher v) {
        String sql = "UPDATE vouchers SET code=?, description=?, discountType=?, discountValue=?, startDate=?, endDate=?, minOrderValue=?, maxUsage=?, usedCount=?, isActive=? WHERE voucherID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getCode());
            ps.setString(2, v.getDescription());
            ps.setString(3, v.getDiscountType());
            ps.setInt(4, v.getDiscountValue());
            ps.setDate(5, v.getStartDate());
            ps.setDate(6, v.getEndDate());
            ps.setInt(7, v.getMinOrderValue());
            ps.setInt(8, v.getMaxUsage());
            ps.setInt(9, v.getUsedCount());
            ps.setBoolean(10, v.isIsActive());
            ps.setInt(11, v.getVoucherID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM vouchers WHERE voucherID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

