/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import DB.DBConnection;
import Model.Actor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Le Quang Giang - CE182527
 */
public class ActorDAO {

    public List<Actor> getAllActors() {
        List<Actor> list = new ArrayList<>();
        String sql = "SELECT * FROM actors";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Actor actor = new Actor();
                actor.setActorID(rs.getInt("actorID"));
                actor.setFullName(rs.getString("fullName"));
                actor.setEmail(rs.getString("email"));
                actor.setPhone(rs.getString("phone"));
                actor.setStatus(rs.getInt("status"));
                list.add(actor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Actor getActorByID(int id) {
        String sql = "SELECT * FROM actors WHERE actorID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Actor actor = new Actor();
                actor.setActorID(rs.getInt("actorID"));
                actor.setFullName(rs.getString("fullName"));
                actor.setEmail(rs.getString("email"));
                actor.setPhone(rs.getString("phone"));
                actor.setStatus(rs.getInt("status"));
                return actor;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateActor(Actor actor) {
        String sql = "UPDATE actors SET fullName = ?, email = ?, phone = ? WHERE actorID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, actor.getFullName());
            ps.setString(2, actor.getEmail());
            ps.setString(3, actor.getPhone());
            ps.setInt(4, actor.getActorID());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteActor(int id) {
        String sql = "DELETE FROM actors WHERE actorID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setActorStatus(int id, boolean active) {
        String sql = "UPDATE actors SET status = ? WHERE actorID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, active ? 1 : 0);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Actor> searchActors(String keyword) {
        List<Actor> list = new ArrayList<>();
        String sql = "SELECT * FROM actors WHERE fullName LIKE ? OR email LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Actor actor = new Actor();
                actor.setActorID(rs.getInt("actorID"));
                actor.setFullName(rs.getString("fullName"));
                actor.setEmail(rs.getString("email"));
                actor.setPhone(rs.getString("phone"));
                actor.setStatus(rs.getInt("status"));
                list.add(actor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
