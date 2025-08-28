package DAO;

import DB.DBConnection;
import Model.ReplyFeedback;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author TriTN
 */public class ReplyFeedbackDAO {

    // Lấy tất cả reply theo feedbackID
    public List<ReplyFeedback> getRepliesByFeedbackID(int feedbackID) {
        List<ReplyFeedback> list = new ArrayList<>();
        String sql = "SELECT r.*, u.roleID, u.fullName " +
                     "FROM ReplyFeedback r " +
                     "JOIN Users u ON r.userID = u.userID " +
                     "WHERE r.feedbackID = ? AND r.isDeleted = 0 " +
                     "ORDER BY r.createdAt ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, feedbackID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ReplyFeedback r = new ReplyFeedback();
                r.setReplyID(rs.getInt("replyID"));
                r.setFeedbackID(rs.getInt("feedbackID"));
                r.setUserID(rs.getInt("userID"));
                r.setRoleID(rs.getInt("roleID"));
                r.setReplyText(rs.getString("replyText"));
                r.setCreatedAt(rs.getTimestamp("createdAt"));
                r.setIsDeleted(rs.getBoolean("isDeleted"));
                r.setUserName(rs.getString("fullName")); // tên người gửi
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy feedbackID từ orderDetailID
    public int getFeedbackIDByOrderDetailID(int orderDetailID) {
        String sql = "SELECT feedbackID FROM Feedback WHERE orderDetailID = ? AND isDeleted = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderDetailID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("feedbackID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // không tìm thấy
    }

    // Thêm reply mới
    public boolean insertReply(ReplyFeedback reply) {
        String sql = "INSERT INTO ReplyFeedback (feedbackID, userID, replyText, createdAt, isDeleted) " +
                     "VALUES (?, ?, ?, GETDATE(), 0)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reply.getFeedbackID());
            ps.setInt(2, reply.getUserID());
            ps.setString(3, reply.getReplyText());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update reply
    public boolean updateReply(ReplyFeedback reply) {
        String sql = "UPDATE ReplyFeedback SET replyText = ? WHERE replyID = ? AND isDeleted = 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reply.getReplyText());
            ps.setInt(2, reply.getReplyID());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Delete reply
    public boolean deleteReply(int replyID) {
        String sql = "UPDATE ReplyFeedback SET isDeleted = 1 WHERE replyID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, replyID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
