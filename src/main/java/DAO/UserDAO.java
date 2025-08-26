package DAO;

import DB.DBConnection;
import Model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Tên table phù hợp với database SWP391_DB_Group7.dbo.Users
    private static final String TABLE_NAME = "Users"; // Viết hoa U để phù hợp với SQL Server

    public User getUserByUsername(String userName) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE userName = ?";
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
        String sql = "UPDATE " + TABLE_NAME + " SET fullName = ?, email = ?, phone = ?, dob = ?, gender = ? WHERE userID = ?";
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
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE email = ?";
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
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE roleID = ?";
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
        String sql = "UPDATE " + TABLE_NAME + " SET status = ? WHERE userID = ?";
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
        String sql = "SELECT * FROM " + TABLE_NAME;
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
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE userName = ? AND password = ?";
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
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE userID = ?";
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
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE userID = ?";
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
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE fullName LIKE ? OR email LIKE ?";
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

    /**
     * Method bổ sung: kiểm tra xem email có tồn tại không (để validation)
     */
    public boolean isEmailExists(String email, int excludeUserID) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE email = ? AND userID != ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setInt(2, excludeUserID);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.err.println("Error in isEmailExists: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Method bổ sung: kiểm tra xem username có tồn tại không (để validation)
     */
    public boolean isUsernameExists(String userName, int excludeUserID) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE userName = ? AND userID != ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userName);
            ps.setInt(2, excludeUserID);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.err.println("Error in isUsernameExists: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Method bổ sung: lấy danh sách user theo status
     */
    public List<User> getUsersByStatus(int status) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE status = ? ORDER BY fullName";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, status);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (Exception e) {
            System.err.println("Error in getUsersByStatus: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Method bổ sung: đếm số lượng user theo role
     */
    public int countUsersByRole(int roleID) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE roleID = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roleID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Error in countUsersByRole: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Method bổ sung: lấy user với phân trang
     */
    public List<User> getUsersWithPagination(int offset, int limit) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY userID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, offset);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (Exception e) {
            System.err.println("Error in getUsersWithPagination: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Method bổ sung: đếm tổng số user
     */
    public int getTotalUsersCount() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Error in getTotalUsersCount: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Helper method để mapping từ ResultSet sang User object
     */
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

    /**
     * Method insert user - giữ nguyên code cũ
     */
    public void insert(User user) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO " + TABLE_NAME + " (fullName, email, password, userName, roleID, phone, status, dob, gender) "
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

    /**
     * Update user password
     */
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE " + TABLE_NAME + " SET password = ? WHERE userID = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in updatePassword: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update user password with old password verification
     */
    public boolean updatePasswordWithVerification(int userId, String oldPassword, String newPassword) {
        String sql = "UPDATE " + TABLE_NAME + " SET password = ? WHERE userID = ? AND password = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            ps.setString(3, oldPassword);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in updatePasswordWithVerification: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
