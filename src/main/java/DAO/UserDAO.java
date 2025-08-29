package DAO;

import DB.DBConnection;
import Model.User;
import Security.PasswordHasher;
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
                return mapResultSet(rs);
            }
        } catch (Exception e) {
            System.err.println("Error in getUserByUsername: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Updated login method with secure password verification
    public User login(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }

        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE userName = ? AND status = 1";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                // Check if password is hashed (contains colons for PBKDF2 format)
                boolean isValidPassword;
                if (storedPassword.contains(":")) {
                    // New secure format: salt:iterations:hash
                    try {
                        PasswordHasher.SecurePasswordHash passwordHash
                                = PasswordHasher.SecurePasswordHash.deserialize(storedPassword);
                        isValidPassword = passwordHash.verify(password);
                    } catch (Exception e) {
                        System.err.println("Password verification error: " + e.getMessage());
                        // Fallback to MD5 if deserialization fails
                        isValidPassword = storedPassword.equals(PasswordHasher.hashMD5(password));
                    }
                } else {
                    // Legacy format - assume MD5 or plain text
                    if (storedPassword.length() == 32) {
                        // MD5 hash (32 hex characters)
                        isValidPassword = storedPassword.equals(PasswordHasher.hashMD5(password));
                    } else {
                        // Plain text (legacy)
                        isValidPassword = storedPassword.equals(password);

                        // Migrate to secure hashing if login successful
                        if (isValidPassword) {
                            migratePasswordToSecure(rs.getInt("userID"), password);
                        }
                    }
                }

                if (isValidPassword) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in login: " + e.getMessage());
            throw new Exception("Lỗi kết nối cơ sở dữ liệu. Vui lòng thử lại sau.", e);
        } catch (Exception e) {
            System.err.println("Error in login: " + e.getMessage());
            throw e;
        }
        return null;
    }

    // Migrate old password to secure format
    private void migratePasswordToSecure(int userId, String plainPassword) {
        try {
            PasswordHasher.SecurePasswordHash passwordHash
                    = new PasswordHasher.SecurePasswordHash(plainPassword);
            updatePasswordSecure(userId, passwordHash.serialize());
            System.out.println("Password migrated to secure format for user ID: " + userId);
        } catch (Exception e) {
            System.err.println("Failed to migrate password for user ID: " + userId + " - " + e.getMessage());
        }
    }

    public boolean updateUser(User user) {
        if (user == null || user.getUserID() <= 0) {
            return false;
        }

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
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE email = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim().toLowerCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
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
                list.add(mapResultSet(rs));
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
                list.add(mapResultSet(rs));
            }
        } catch (Exception e) {
            System.err.println("Error in getAllUsers: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public User getUserByID(int id) {
        if (id <= 0) {
            return null;
        }

        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE userID = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (Exception e) {
            System.err.println("Error in getUserByID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteUser(int id) {
        if (id <= 0) {
            return false;
        }

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
        if (keyword == null || keyword.trim().isEmpty()) {
            return list;
        }

        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE fullName LIKE ? OR email LIKE ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword.trim() + "%";
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

    public boolean isEmailExists(String email, int excludeUserID) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE email = ? AND userID != ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim().toLowerCase());
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

    public boolean isUsernameExists(String userName, int excludeUserID) {
        if (userName == null || userName.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE userName = ? AND userID != ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userName.trim());
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

    public List<User> getUsersWithPagination(int offset, int limit) {
        List<User> list = new ArrayList<>();
        if (offset < 0 || limit <= 0) {
            return list;
        }

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

    // Updated insert method with comprehensive error handling and validation
    public void insert(User user) throws Exception {
        // Validate input
        if (user == null) {
            throw new IllegalArgumentException("Thông tin người dùng không được để trống");
        }

        // Validate required fields
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }

        if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();

            // Test connection
            if (conn == null || conn.isClosed()) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu");
            }

            // Double check for duplicates before inserting
            if (getUserByEmail(user.getEmail()) != null) {
                throw new Exception("Email '" + user.getEmail() + "' đã được sử dụng bởi tài khoản khác");
            }

            if (getUserByUsername(user.getUserName()) != null) {
                throw new Exception("Tên đăng nhập '" + user.getUserName() + "' đã được sử dụng bởi tài khoản khác");
            }

            String sql = "INSERT INTO " + TABLE_NAME + " (fullName, email, password, userName, roleID, phone, status, dob, gender) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getFullName().trim());
            ps.setString(2, user.getEmail().toLowerCase().trim());

            // Hash password securely before storing
            try {
                PasswordHasher.SecurePasswordHash passwordHash
                        = new PasswordHasher.SecurePasswordHash(user.getPassword());
                ps.setString(3, passwordHash.serialize());
            } catch (Exception e) {
                System.err.println("Password hashing error: " + e.getMessage());
                throw new Exception("Lỗi mã hóa mật khẩu. Vui lòng thử lại.", e);
            }

            ps.setString(4, user.getUserName().trim());
            ps.setInt(5, user.getRoleID());
            ps.setString(6, user.getPhone());
            ps.setInt(7, user.getStatus());

            if (user.getDob() != null) {
                ps.setDate(8, new java.sql.Date(user.getDob().getTime()));
            } else {
                ps.setNull(8, Types.DATE);
            }

            ps.setString(9, user.getGender());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Không thể thêm người dùng vào cơ sở dữ liệu");
            }

            System.out.println("User successfully registered: " + user.getUserName() + " (" + user.getEmail() + ")");

        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Constraint violation in insert: " + e.getMessage());
            if (e.getMessage().contains("email")) {
                throw new Exception("Email đã được sử dụng bởi tài khoản khác");
            } else if (e.getMessage().contains("userName")) {
                throw new Exception("Tên đăng nhập đã được sử dụng bởi tài khoản khác");
            } else {
                throw new Exception("Thông tin đăng ký đã tồn tại trong hệ thống");
            }
        } catch (SQLException e) {
            System.err.println("SQL error in insert: " + e.getMessage());
            e.printStackTrace();

            if (e.getMessage().contains("duplicate") || e.getMessage().contains("UNIQUE")) {
                throw new Exception("Thông tin đăng ký (email hoặc tên đăng nhập) đã tồn tại");
            } else if (e.getMessage().contains("connection") || e.getMessage().contains("timeout")) {
                throw new Exception("Lỗi kết nối cơ sở dữ liệu. Vui lòng thử lại sau");
            } else {
                throw new Exception("Lỗi cơ sở dữ liệu: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("General error in insert: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            // Clean up resources
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }
    }

    // Updated password methods with secure hashing
    public boolean updatePassword(int userId, String newPassword) {
        if (userId <= 0 || newPassword == null || newPassword.isEmpty()) {
            return false;
        }

        try {
            PasswordHasher.SecurePasswordHash passwordHash
                    = new PasswordHasher.SecurePasswordHash(newPassword);
            return updatePasswordSecure(userId, passwordHash.serialize());
        } catch (Exception e) {
            System.err.println("Error in updatePassword: " + e.getMessage());
            return false;
        }
    }

    private boolean updatePasswordSecure(int userId, String hashedPassword) {
        String sql = "UPDATE " + TABLE_NAME + " SET password = ? WHERE userID = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error in updatePasswordSecure: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePasswordWithVerification(int userId, String oldPassword, String newPassword) {
        if (userId <= 0 || oldPassword == null || oldPassword.isEmpty()
                || newPassword == null || newPassword.isEmpty()) {
            return false;
        }

        try {
            // First verify the old password
            User user = getUserByID(userId);
            if (user == null) {
                return false;
            }

            String storedPassword = user.getPassword();
            boolean isOldPasswordValid;

            // Check password format and verify
            if (storedPassword.contains(":")) {
                // Secure format
                PasswordHasher.SecurePasswordHash passwordHash
                        = PasswordHasher.SecurePasswordHash.deserialize(storedPassword);
                isOldPasswordValid = passwordHash.verify(oldPassword);
            } else {
                // Legacy format
                if (storedPassword.length() == 32) {
                    isOldPasswordValid = storedPassword.equals(PasswordHasher.hashMD5(oldPassword));
                } else {
                    isOldPasswordValid = storedPassword.equals(oldPassword);
                }
            }

            if (isOldPasswordValid) {
                return updatePassword(userId, newPassword);
            }

        } catch (Exception e) {
            System.err.println("Error in updatePasswordWithVerification: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // DEPRECATED: MD5-only methods (for backwards compatibility)
    @Deprecated
    public String hashPasswordMD5(String password) {
        return PasswordHasher.hashMD5(password);
    }

    @Deprecated
    public boolean verifyPasswordMD5(String password, String hashedPassword) {
        return PasswordHasher.hashMD5(password).equals(hashedPassword);
    }
}
