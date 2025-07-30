/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package DAO;

import DB.DBConnection;
import Model.ReplyFeedback;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author TriTN
 */
public class ReplyFeedbackDAO {

    public List<ReplyFeedback> getRepliesByFeedbackID(int feedbackID) {
        List<ReplyFeedback> list = new ArrayList<>();
        String sql = "SELECT * FROM ReplyFeedback WHERE feedbackID = ? ORDER BY createdAt ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, feedbackID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReplyFeedback r = new ReplyFeedback();
                r.setReplyID(rs.getInt("replyID"));
                r.setFeedbackID(rs.getInt("feedbackID"));
                r.setUserID(rs.getInt("userID"));
                r.setReplyText(rs.getString("replyText"));
                r.setCreatedAt(rs.getTimestamp("createdAt"));
                list.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insertReply(ReplyFeedback reply) {
        String sql = "INSERT INTO ReplyFeedback (feedbackID, userID, replyText, createdAt) VALUES (?, ?, ?, GETDATE())";

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

    public boolean updateReply(ReplyFeedback reply) {
        String sql = "UPDATE ReplyFeedback SET replyText = ? WHERE replyID = ?";

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

    public boolean deleteReply(int replyID) {
        String sql = "DELETE FROM ReplyFeedback WHERE replyID = ?";

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
