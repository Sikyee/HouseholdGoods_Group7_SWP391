/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DB.DBConnection;
import Model.CancelReason;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TriTM
 */
public class CancelReasonDAO {

    // Thêm lý do huỷ đơn hàng
    public void insertCancelReason(CancelReason reason) {
        String sql = "INSERT INTO CancelReason (orderID, reason) VALUES (?, ?)";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, reason.getOrderID());
            ps.setString(2, reason.getReason());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy lý do huỷ theo orderID
    public CancelReason getCancelReasonByOrderID(int orderID) {
        String sql = "SELECT * FROM CancelReason WHERE orderID = ?";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CancelReason reason = new CancelReason();
                reason.setCancelID(rs.getInt("cancelID"));
                reason.setOrderID(rs.getInt("orderID"));
                reason.setReason(rs.getString("reason"));
                return reason;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy tất cả lý do huỷ (nếu cần admin xem)
    public List<CancelReason> getAllCancelReasons() {
        List<CancelReason> list = new ArrayList<CancelReason>();
        String sql = "SELECT * FROM CancelReason";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CancelReason reason = new CancelReason();
                reason.setCancelID(rs.getInt("cancelID"));
                reason.setOrderID(rs.getInt("orderID"));
                reason.setReason(rs.getString("reason"));
                list.add(reason);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
