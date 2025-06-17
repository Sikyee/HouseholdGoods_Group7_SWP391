/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import Model.Feedback;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author thach
 */


public class FeedbackDAO {
    private Connection conn;

    public FeedbackDAO(Connection conn) {
        this.conn = conn;
    }

    public ArrayList<Feedback> getAllFeedbackByCustomerId(int customerId) throws SQLException {
        ArrayList<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM Feedback WHERE customerId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Feedback getFeedbackById(int id) throws SQLException {
        String sql = "SELECT * FROM Feedback WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    public boolean updateFeedback(int id, String content) throws SQLException {
        String sql = "UPDATE Feedback SET content = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, content);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteFeedback(int id) throws SQLException {
        String sql = "DELETE FROM Feedback WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean replyToFeedback(int id, String replyText) throws SQLException {
        String sql = "UPDATE Feedback SET reply = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, replyText);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Feedback mapRow(ResultSet rs) throws SQLException {
        return new Feedback(
            rs.getInt("id"),
            rs.getInt("customerId"),
            rs.getString("content"),
            rs.getString("reply"),
            rs.getString("createdAt")
        );
    }
}
