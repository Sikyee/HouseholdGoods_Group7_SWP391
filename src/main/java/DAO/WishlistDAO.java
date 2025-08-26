package DAO;

import DB.DBConnection;
import Model.Wishlist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO {
    public boolean addWishlist(int userID, int productID) {
        String sql = "INSERT INTO Wishlist(userID, productID, createdDate) VALUES (?, ?, GETDATE())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            ps.setInt(2, productID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Wishlist> getWishlistByUser(int userID) {
        List<Wishlist> list = new ArrayList<>();
        String sql = "SELECT * FROM Wishlist WHERE userID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Wishlist w = new Wishlist(
                    rs.getInt("wishlistID"),
                    rs.getInt("userID"),
                    rs.getInt("productID"),
                    rs.getDate("createdDate")
                );
                list.add(w);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteWishlist(int wishlistID) {
        String sql = "DELETE FROM Wishlist WHERE wishlistID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wishlistID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
