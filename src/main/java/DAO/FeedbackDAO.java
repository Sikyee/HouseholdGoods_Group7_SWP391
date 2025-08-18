/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DB.DBConnection;
import Model.Feedback;
import Model.OrderDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    // Lấy feedback theo trang
    public List<Feedback> getFeedbackByPage(int page, int limit) {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT f.*, u.fullName AS userName " +
                     "FROM Feedback f JOIN Users u ON f.userID = u.userID " +
                     "WHERE f.isDeleted = 0 " +
                     "ORDER BY f.feedbackID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, (page - 1) * limit);
            ps.setInt(2, limit);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultToFeedback(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy tất cả feedback (dùng cho Export Excel)
    public List<Feedback> getAllFeedback() {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT f.*, u.fullName AS userName " +
                     "FROM Feedback f JOIN Users u ON f.userID = u.userID " +
                     "WHERE f.isDeleted = 0 " +
                     "ORDER BY f.feedbackID DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultToFeedback(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Đếm tất cả feedback
    public int countAllFeedback() {
        String sql = "SELECT COUNT(*) FROM Feedback WHERE isDeleted = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Xóa mềm
    public boolean softDeleteFeedback(int id) {
        String sql = "UPDATE Feedback SET isDeleted = 1 WHERE feedbackID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật trạng thái
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE Feedback SET status = ? WHERE feedbackID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tìm kiếm có phân trang
    public List<Feedback> searchFeedback(String userName, String keyword, String status, String date, int page, int limit) {
        List<Feedback> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT f.*, u.fullName AS userName " +
            "FROM Feedback f JOIN Users u ON f.userID = u.userID WHERE f.isDeleted = 0"
        );

        List<Object> params = new ArrayList<>();

        if (userName != null && !userName.trim().isEmpty()) {
            sql.append(" AND u.fullName LIKE ?");
            params.add("%" + userName + "%");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND f.comment LIKE ?");
            params.add("%" + keyword + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND f.status = ?");
            params.add(status);
        }
        if (date != null && !date.trim().isEmpty()) {
            sql.append(" AND CAST(f.createdAt AS DATE) = ?");
            params.add(Date.valueOf(date));
        }

        sql.append(" ORDER BY f.feedbackID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ps.setInt(params.size() + 1, (page - 1) * limit);
            ps.setInt(params.size() + 2, limit);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultToFeedback(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Đếm bản ghi sau khi lọc
    public int countFilteredFeedback(String userName, String keyword, String status, String date) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM Feedback f JOIN Users u ON f.userID = u.userID WHERE f.isDeleted = 0"
        );

        List<Object> params = new ArrayList<>();

        if (userName != null && !userName.trim().isEmpty()) {
            sql.append(" AND u.fullName LIKE ?");
            params.add("%" + userName + "%");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND f.comment LIKE ?");
            params.add("%" + keyword + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND f.status = ?");
            params.add(status);
        }
        if (date != null && !date.trim().isEmpty()) {
            sql.append(" AND CAST(f.createdAt AS DATE) = ?");
            params.add(Date.valueOf(date));
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Dùng chung để map từ ResultSet sang Feedback
    private Feedback mapResultToFeedback(ResultSet rs) throws SQLException {
        Feedback fb = new Feedback();
        fb.setFeedbackID(rs.getInt("feedbackID"));
        fb.setUserID(rs.getInt("userID"));
        fb.setUserName(rs.getString("userName"));
        fb.setOrderDetailID(rs.getInt("orderDetailID"));
        fb.setRating(rs.getInt("rating"));
        fb.setComment(rs.getString("comment"));
        fb.setCreatedAt(rs.getTimestamp("createdAt"));
        fb.setStatus(rs.getString("status"));
        fb.setDeleted(rs.getBoolean("isDeleted"));
        return fb;
    }

    // Insert Feedback
    public boolean insertFeedback(Feedback fb) {
        String sql = "INSERT INTO Feedback (userID, orderDetailID, rating, comment, status, isDeleted, createdAt) " +
                     "VALUES (?, ?, ?, ?, 'Pending', 0, GETDATE())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fb.getUserID());
            ps.setInt(2, fb.getOrderDetailID());
            ps.setInt(3, fb.getRating());
            ps.setString(4, fb.getComment());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    
    public List<OrderDetail> getOrdersWithoutFeedback(int userID) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT od.* FROM OrderDetail od "
                   + "JOIN OrderInfo oi ON od.orderID = oi.orderID "
                   + "WHERE oi.userID = ? "
                   + "AND od.orderDetailID NOT IN (SELECT orderDetailID FROM Feedback)";

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderDetail od = new OrderDetail();
                od.setOrderDetailID(rs.getInt("orderDetailID"));
                od.setProductID(rs.getInt("productID"));
                od.setOrderID(rs.getInt("orderID"));
                od.setOrderName(rs.getString("orderName"));
                od.setQuantity(rs.getInt("quantity"));
                od.setTotalPrice(rs.getLong("totalPrice"));
                list.add(od);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<OrderDetail> getOrdersWithFeedback(int userID) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT od.* FROM OrderDetail od "
                   + "JOIN OrderInfo oi ON od.orderID = oi.orderID "
                   + "WHERE oi.userID = ? "
                   + "AND od.orderDetailID IN (SELECT orderDetailID FROM Feedback)";

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderDetail od = new OrderDetail();
                od.setOrderDetailID(rs.getInt("orderDetailID"));
                od.setProductID(rs.getInt("productID"));
                od.setOrderID(rs.getInt("orderID"));
                od.setOrderName(rs.getString("orderName"));
                od.setQuantity(rs.getInt("quantity"));
                od.setTotalPrice(rs.getLong("totalPrice"));
                list.add(od);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
        //ghi gì dó de commit lai
    }
    
}
