package DAO;

import DB.DBConnection;
import Model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User getUserByUsername(String userName) {
        String sql = "SELECT * FROM users WHERE userName = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("userID"));
                user.setFullName(rs.getString("fullName"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setUserName(rs.getString("userName"));
                user.setRoleID(rs.getInt("roleID"));
                user.setRegistrationDate(rs.getTimestamp("registrationDate"));
                user.setPhone(rs.getString("phone"));
                user.setStatus(rs.getInt("status"));
                user.setDob(rs.getDate("dob"));
                user.setGender(rs.getString("gender"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET fullName = ?, email = ?, phone = ?, dob = ?, gender = ? WHERE userName = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());

            if (user.getDob() != null) {
                ps.setDate(4, new java.sql.Date(user.getDob().getTime()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setString(5, user.getGender());
            ps.setString(6, user.getUserName());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setUserStatus(int userID, boolean status) {
        String sql = "UPDATE users SET status = ? WHERE userID = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status ? 1 : 0);
            ps.setInt(2, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("userID"));
                user.setFullName(rs.getString("fullName"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setUserName(rs.getString("userName"));
                user.setRoleID(rs.getInt("roleID"));
                user.setRegistrationDate(rs.getTimestamp("registrationDate"));
                user.setPhone(rs.getString("phone"));
                user.setStatus(rs.getInt("status"));
                user.setDob(rs.getDate("dob"));
                user.setGender(rs.getString("gender"));
                list.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public User login(String username, String password) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM users WHERE userName = ? AND password = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            User user = new User();
            user.setUserID(rs.getInt("userID"));
            user.setUserName(rs.getString("userName"));
            user.setPassword(rs.getString("password"));
            user.setFullName(rs.getString("fullName"));
            user.setEmail(rs.getString("email"));
            user.setRoleID(rs.getInt("roleID"));
            user.setPhone(rs.getString("phone"));
            user.setStatus(rs.getInt("status"));
            return user;
        }
        return null;
    }
}
