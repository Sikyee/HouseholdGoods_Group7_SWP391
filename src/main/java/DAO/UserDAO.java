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
                user.setPhone(rs.getString("phone"));
                user.setStatus(rs.getInt("status"));
                user.setDob(rs.getDate("dob"));
                user.setGender(rs.getString("gender"));
                return user;
            }
        } catch (Exception e) {
            System.err.println("Error in getUserByUsername: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET fullName = ?, email = ?, phone = ?, dob = ?, gender = ? WHERE userID = ?";
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
            ps.setInt(6, user.getUserID());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in updateUser: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("userID"));
                user.setFullName(rs.getString("fullName"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setUserName(rs.getString("userName"));
                user.setRoleID(rs.getInt("roleID"));
                user.setPhone(rs.getString("phone"));
                user.setStatus(rs.getInt("status"));
                user.setDob(rs.getDate("dob"));
                user.setGender(rs.getString("gender"));
                return user;
            }
        } catch (Exception e) {
            System.err.println("Error in getUserByEmail: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getUsersByRole(int roleID) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE roleID = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roleID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs)); // sử dụng method đã có để convert từ ResultSet -> User
            }
        } catch (Exception e) {
            System.err.println("Error in getUsersByRole: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public boolean setUserStatus(int userID, boolean status) {
        String sql = "UPDATE users SET status = ? WHERE userID = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, status ? 1 : 0);
            ps.setInt(2, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in setUserStatus: " + e.getMessage());
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
                user.setPhone(rs.getString("phone"));
                user.setStatus(rs.getInt("status"));
                user.setDob(rs.getDate("dob"));
                user.setGender(rs.getString("gender"));
                list.add(user);
            }
        } catch (Exception e) {
            System.err.println("Error in getAllUsers: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public User login(String username, String password) throws Exception {
        String sql = "SELECT * FROM users WHERE userName = ? AND password = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

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
                user.setDob(rs.getDate("dob"));
                user.setGender(rs.getString("gender"));
                return user;
            }
        } catch (Exception e) {
            System.err.println("Error in login: " + e.getMessage());
            throw e;
        }
        return null;
    }

    public User getUserByID(int id) {
        String sql = "SELECT * FROM users WHERE userID = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("userID"));
                user.setUserName(rs.getString("userName"));
                user.setFullName(rs.getString("fullName"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setDob(rs.getDate("dob"));
                user.setGender(rs.getString("gender"));
                user.setStatus(rs.getInt("status"));
                user.setRoleID(rs.getInt("roleID"));
                user.setPassword(rs.getString("password"));
                return user;
            }
        } catch (Exception e) {
            System.err.println("Error in getUserByID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE userID = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in deleteUser: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<User> searchUsers(String keyword) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE fullName LIKE ? OR email LIKE ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error in searchUsers: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public boolean changePassword(int userID, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE userID = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();

            // Disable auto-commit to handle transaction manually
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setInt(2, userID);

            // Add debug logging
            System.out.println("Executing password update query for userID: " + userID);
            System.out.println("SQL: " + sql);

            int rowsAffected = ps.executeUpdate();

            System.out.println("Rows affected: " + rowsAffected);

            if (rowsAffected > 0) {
                conn.commit(); // Commit the transaction
                System.out.println("Password update committed successfully");
                return true;
            } else {
                conn.rollback(); // Rollback if no rows were affected
                System.out.println("No rows affected, rolling back");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error in changePassword: " + e.getMessage());
            e.printStackTrace();

            // Rollback transaction on error
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transaction rolled back due to error");
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            return false;
        } finally {
            // Close resources
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    private User mapResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserID(rs.getInt("userID"));
        user.setFullName(rs.getString("fullName"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setUserName(rs.getString("userName"));
        user.setRoleID(rs.getInt("roleID"));
        user.setPhone(rs.getString("phone"));
        user.setStatus(rs.getInt("status"));
        user.setDob(rs.getDate("dob"));
        user.setGender(rs.getString("gender"));
        return user;
    }

    public void insert(User user) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO Users (fullName, email, password, userName, roleID, phone, status, dob, gender) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, user.getFullName());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());
        ps.setString(4, user.getUserName());
        ps.setInt(5, user.getRoleID());
        ps.setString(6, user.getPhone());
        ps.setInt(7, user.getStatus());
        if (user.getDob() != null) {
            ps.setDate(8, new java.sql.Date(user.getDob().getTime()));
        } else {
            ps.setNull(8, Types.DATE);
        }
        ps.setString(9, user.getGender());
        ps.executeUpdate();
    }
    public void insert1(User user) throws Exception {
    Connection conn = DBConnection.getConnection();
    String sql = "INSERT INTO users (fullName, email, password, userName, roleID, phone, status, dob, gender) "
               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setString(1, user.getFullName());
    ps.setString(2, user.getEmail());
    ps.setString(3, user.getPassword());
    ps.setString(4, user.getUserName());
    ps.setInt(5, user.getRoleID());
    ps.setString(6, user.getPhone());
    ps.setInt(7, user.getStatus());

    if (user.getDob() != null) {
        ps.setDate(8, new java.sql.Date(user.getDob().getTime()));
    } else {
        ps.setNull(8, Types.DATE);
    }

    ps.setString(9, user.getGender());

    System.out.println("Executing SQL: " + ps.toString()); // log câu query
    int rows = ps.executeUpdate();
    System.out.println("Rows inserted: " + rows);

    ps.close();
    conn.close();
}

}
