package DAO;

import DB.DBConnection;
import Model.User;
import java.sql.*;

public class ChangePasswordDAO {

    private static final String TABLE_NAME = "Users";

    public boolean changePassword(int userID, String newPassword) {
        String sql = "UPDATE " + TABLE_NAME + " SET password = ? WHERE userID = ?";
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

    /**
     * Check if password is being used by other users
     *
     * @param currentUserID Current user's ID (to exclude from check)
     * @param password Password to check
     * @return true if password exists for other users, false if not
     */
    public boolean isPasswordExistsForOtherUsers(int currentUserID, String password) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE userID != ? AND password = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, currentUserID);
            ps.setString(2, password);

            rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Password duplicate check - Found " + count + " other users with same password");
                return count > 0;
            }

        } catch (Exception e) {
            System.err.println("Error checking password duplicate for other users: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }

        return false;
    }

    /**
     * Get user information by ID for validation
     *
     * @param userID User's ID
     * @return User object or null if not found
     */
    public User getUserForPasswordValidation(int userID) {
        String sql = "SELECT userID, password, status, userName FROM " + TABLE_NAME + " WHERE userID = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("userID"));
                user.setPassword(rs.getString("password"));
                user.setStatus(rs.getInt("status"));
                user.setUserName(rs.getString("userName"));
                return user;
            }
        } catch (Exception e) {
            System.err.println("Error in getUserForPasswordValidation: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Verify if current password is correct
     *
     * @param userID User's ID
     * @param currentPassword Current password to verify
     * @return true if password is correct, false if wrong
     */
    public boolean verifyCurrentPassword(int userID, String currentPassword) {
        String sql = "SELECT password FROM " + TABLE_NAME + " WHERE userID = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            rs = ps.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                boolean isValid = currentPassword.equals(storedPassword);
                System.out.println("Current password verification for userID " + userID + ": " + isValid);
                return isValid;
            }
        } catch (Exception e) {
            System.err.println("Error in verifyCurrentPassword: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Check if account is active
     *
     * @param userID User's ID
     * @return true if account is active (status = 1), false if not
     */
    public boolean isAccountActive(int userID) {
        String sql = "SELECT status FROM " + TABLE_NAME + " WHERE userID = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            rs = ps.executeQuery();

            if (rs.next()) {
                int status = rs.getInt("status");
                boolean isActive = (status == 1);
                System.out.println("Account status check for userID " + userID + ": " + (isActive ? "Active" : "Inactive"));
                return isActive;
            }
        } catch (Exception e) {
            System.err.println("Error in isAccountActive: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Log password change activity (removed IP parameter)
     *
     * @param userID User's ID
     * @param success Whether password change was successful
     */
    public void logPasswordChangeActivity(int userID, boolean success) {
        try {
            // Currently only log to console, can be extended to database logging later
            System.out.println("=== PASSWORD CHANGE LOG ===");
            System.out.println("UserID: " + userID);
            System.out.println("Timestamp: " + new java.util.Date());
            System.out.println("Success: " + success);
            System.out.println("============================");

            // TODO: Can implement writing to audit_log table
            // INSERT INTO audit_log (user_id, action, success, timestamp) VALUES (...)
        } catch (Exception e) {
            System.err.println("Error logging password change activity: " + e.getMessage());
        }
    }

    /**
     * Get recent password changes history (if audit table exists)
     *
     * @param userID User's ID
     * @param limitDays Number of recent days
     * @return Number of password changes in the time period
     */
    public int getRecentPasswordChangesCount(int userID, int limitDays) {
        // TODO: Implement when audit_log table is available
        // Currently return 0 to not block user
        System.out.println("Password change frequency check not implemented - allowing change");
        return 0;
    }

    /**
     * Check if password is in common/weak passwords list
     *
     * @param password Password to check
     * @return true if it's a common password, false if not
     */
    public boolean isCommonPassword(String password) {
        // List of common passwords (can be extended or read from file)
        String[] commonPasswords = {
            "12345678", "password", "123456789", "12345", "1234567890",
            "qwerty123", "abc123", "password123", "admin123", "letmein",
            "welcome123", "monkey123", "dragon123", "master123", "shadow123"
        };

        String lowerPassword = password.toLowerCase();
        for (String common : commonPasswords) {
            if (lowerPassword.equals(common.toLowerCase())) {
                System.out.println("Password rejected - found in common passwords list");
                return true;
            }
        }
        return false;
    }

    /**
     * Check if password contains user's personal information
     *
     * @param userID User's ID
     * @param password Password to check
     * @return true if contains personal info, false if not
     */
    public boolean containsPersonalInfo(int userID, String password) {
        String sql = "SELECT userName, fullName, email FROM " + TABLE_NAME + " WHERE userID = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userID);
            rs = ps.executeQuery();

            if (rs.next()) {
                String userName = rs.getString("userName");
                String fullName = rs.getString("fullName");
                String email = rs.getString("email");

                String lowerPassword = password.toLowerCase();

                // Check username
                if (userName != null && lowerPassword.contains(userName.toLowerCase())) {
                    System.out.println("Password rejected - contains username");
                    return true;
                }

                // Check name parts in fullName
                if (fullName != null) {
                    String[] nameParts = fullName.toLowerCase().split("\\s+");
                    for (String part : nameParts) {
                        if (part.length() >= 3 && lowerPassword.contains(part)) {
                            System.out.println("Password rejected - contains name part: " + part);
                            return true;
                        }
                    }
                }

                // Check email (part before @)
                if (email != null && email.contains("@")) {
                    String emailPrefix = email.substring(0, email.indexOf("@")).toLowerCase();
                    if (lowerPassword.contains(emailPrefix)) {
                        System.out.println("Password rejected - contains email prefix");
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in containsPersonalInfo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return false;
    }
}
