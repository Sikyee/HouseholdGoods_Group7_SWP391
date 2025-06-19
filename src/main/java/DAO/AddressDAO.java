package DAO;

import DB.DBConnection;
import Model.Address;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class AddressDAO {
    
    private static final Logger logger = Logger.getLogger(AddressDAO.class.getName());

    // Thêm địa chỉ mới
    public boolean addAddress(Address address) {
        String unsetDefaultSql = "UPDATE address SET isDefault = 0 WHERE userID = ? AND isDefault = 1";
        String getMaxOrderSql = "SELECT COALESCE(MAX(displayOrder), 0) + 1 FROM address WHERE userID = ?";
        String insertSql = "INSERT INTO address (userID, isDefault, addressDetail, addressName, recipientName, " +
                   "phone, province, district, ward, specificAddress, displayOrder, addressType, isActive) " +
                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Nếu set làm default, bỏ default của các địa chỉ khác
                if (address.isDefault()) {
                    try (PreparedStatement ps1 = conn.prepareStatement(unsetDefaultSql)) {
                        ps1.setInt(1, address.getUserID());
                        ps1.executeUpdate();
                    }
                }

                // Lấy displayOrder tiếp theo
                int nextOrder = 1;
                try (PreparedStatement psOrder = conn.prepareStatement(getMaxOrderSql)) {
                    psOrder.setInt(1, address.getUserID());
                    ResultSet rs = psOrder.executeQuery();
                    if (rs.next()) {
                        nextOrder = rs.getInt(1);
                    }
                }

                // Thêm địa chỉ mới
                try (PreparedStatement ps2 = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps2.setInt(1, address.getUserID());
                    ps2.setBoolean(2, address.isDefault());
                    ps2.setString(3, address.getAddressDetail());
                    ps2.setString(4, address.getAddressName());
                    ps2.setString(5, address.getRecipientName());
                    ps2.setString(6, address.getPhone());
                    ps2.setString(7, address.getProvince());
                    ps2.setString(8, address.getDistrict());
                    ps2.setString(9, address.getWard());
                    ps2.setString(10, address.getSpecificAddress());
                    ps2.setInt(11, nextOrder);
                    ps2.setString(12, address.getAddressType());
                    ps2.setBoolean(13, address.isActive());

                    int rowsAffected = ps2.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        ResultSet generatedKeys = ps2.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            address.setAddressID(generatedKeys.getInt(1));
                        }
                    }
                }

                conn.commit();
                return true;
                
            } catch (SQLException ex) {
                conn.rollback();
                logger.log(Level.SEVERE, "Error adding address", ex);
                throw ex;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Database connection error", e);
        }

        return false;
    }

    // Cập nhật địa chỉ
    public boolean updateAddress(Address address) {
        String unsetDefaultSql = "UPDATE address SET isDefault = 0 WHERE userID = ? AND addressID != ?";
        String updateSql = "UPDATE address SET addressDetail = ?, addressName = ?, recipientName = ?, " +
                   "phone = ?, province = ?, district = ?, ward = ?, specificAddress = ?, " +
                   "isDefault = ?, addressType = ?, isActive = ? WHERE addressID = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Nếu set làm default, bỏ default của các địa chỉ khác
                if (address.isDefault()) {
                    try (PreparedStatement ps1 = conn.prepareStatement(unsetDefaultSql)) {
                        ps1.setInt(1, address.getUserID());
                        ps1.setInt(2, address.getAddressID());
                        ps1.executeUpdate();
                    }
                }

                try (PreparedStatement ps2 = conn.prepareStatement(updateSql)) {
                    ps2.setString(1, address.getAddressDetail());
                    ps2.setString(2, address.getAddressName());
                    ps2.setString(3, address.getRecipientName());
                    ps2.setString(4, address.getPhone());
                    ps2.setString(5, address.getProvince());
                    ps2.setString(6, address.getDistrict());
                    ps2.setString(7, address.getWard());
                    ps2.setString(8, address.getSpecificAddress());
                    ps2.setBoolean(9, address.isDefault());
                    ps2.setString(10, address.getAddressType());
                    ps2.setBoolean(11, address.isActive());
                    ps2.setInt(12, address.getAddressID());

                    int rowsAffected = ps2.executeUpdate();
                    conn.commit();
                    return rowsAffected > 0;
                }

            } catch (SQLException ex) {
                conn.rollback();
                logger.log(Level.SEVERE, "Error updating address", ex);
                throw ex;
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Database connection error", e);
        }

        return false;
    }

    // Set địa chỉ mặc định
    public boolean setDefaultAddress(int userID, int addressID) {
        String unsetAllDefaultSql = "UPDATE address SET isDefault = 0 WHERE userID = ?";
        String setDefaultSql = "UPDATE address SET isDefault = 1 WHERE addressID = ? AND userID = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Bỏ default tất cả địa chỉ của user
                try (PreparedStatement ps1 = conn.prepareStatement(unsetAllDefaultSql)) {
                    ps1.setInt(1, userID);
                    ps1.executeUpdate();
                }

                // Set địa chỉ được chọn làm default
                try (PreparedStatement ps2 = conn.prepareStatement(setDefaultSql)) {
                    ps2.setInt(1, addressID);
                    ps2.setInt(2, userID);
                    int rowsAffected = ps2.executeUpdate();
                    
                    conn.commit();
                    return rowsAffected > 0;
                }

            } catch (SQLException ex) {
                conn.rollback();
                logger.log(Level.SEVERE, "Error setting default address", ex);
                throw ex;
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Database connection error", e);
        }

        return false;
    }

    // Xoá địa chỉ (soft delete)
    public boolean deleteAddress(int addressID) {
        String sql = "UPDATE address SET isActive = 0 WHERE addressID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, addressID);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting address", e);
        }
        return false;
    }

    // Xoá vĩnh viễn địa chỉ
    public boolean hardDeleteAddress(int addressID) {
        String sql = "DELETE FROM address WHERE addressID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, addressID);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error hard deleting address", e);
        }
        return false;
    }

    // Bulk delete addresses
    public int bulkDeleteAddresses(int userID, int[] addressIDs) {
        String sql = "UPDATE address SET isActive = 0 WHERE addressID = ? AND userID = ? AND isDefault = 0";
        int deletedCount = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int addressID : addressIDs) {
                ps.setInt(1, addressID);
                ps.setInt(2, userID);
                
                if (ps.executeUpdate() > 0) {
                    deletedCount++;
                }
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error bulk deleting addresses", e);
        }

        return deletedCount;
    }

    // Sắp xếp lại thứ tự địa chỉ
    public boolean reorderAddresses(int userID, String[] orderedAddressIDs) {
        String sql = "UPDATE address SET displayOrder = ? WHERE addressID = ? AND userID = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < orderedAddressIDs.length; i++) {
                    ps.setInt(1, i + 1);
                    ps.setInt(2, Integer.parseInt(orderedAddressIDs[i]));
                    ps.setInt(3, userID);
                    ps.addBatch();
                }

                int[] results = ps.executeBatch();
                conn.commit();
                
                // Kiểm tra tất cả updates thành công
                for (int result : results) {
                    if (result <= 0) return false;
                }
                
                return true;

            } catch (SQLException ex) {
                conn.rollback();
                logger.log(Level.SEVERE, "Error reordering addresses", ex);
                throw ex;
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Database connection error", e);
        }

        return false;
    }

    // Lấy danh sách địa chỉ theo userID (chỉ các địa chỉ active)
    public List<Address> getAddressesByUserID(int userID) {
        return getAddressesByUserID(userID, true);
    }

    // Lấy danh sách địa chỉ theo userID với option includeInactive
    public List<Address> getAddressesByUserID(int userID, boolean activeOnly) {
        List<Address> list = new ArrayList<>();
        String sql = "SELECT * FROM address WHERE userID = ?" + 
                    (activeOnly ? " AND isActive = 1" : "") + 
                    " ORDER BY displayOrder ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Address address = mapResultSetToAddress(rs);
                list.add(address);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting addresses by user ID", e);
        }

        return list;
    }

    // Lấy địa chỉ mặc định của user
    public Address getDefaultAddress(int userID) {
        String sql = "SELECT * FROM address WHERE userID = ? AND isDefault = 1 AND isActive = 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToAddress(rs);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting default address", e);
        }

        return null;
    }

    // Lấy địa chỉ theo ID
    public Address getAddressByID(int addressID) {
        String sql = "SELECT * FROM address WHERE addressID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, addressID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToAddress(rs);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting address by ID", e);
        }

        return null;
    }

    // Tìm kiếm địa chỉ
    public List<Address> searchAddresses(int userID, String keyword) {
        List<Address> list = new ArrayList<>();
        String sql = "SELECT * FROM address " +
                    "WHERE userID = ? AND isActive = 1 " +
                    "AND (addressName LIKE ? OR addressDetail LIKE ? OR recipientName LIKE ? OR phone LIKE ?) " +
                    "ORDER BY displayOrder ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            ps.setInt(1, userID);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ps.setString(5, searchPattern);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Address address = mapResultSetToAddress(rs);
                list.add(address);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error searching addresses", e);
        }

        return list;
    }

    // Lấy địa chỉ theo loại
    public List<Address> getAddressesByType(int userID, String addressType) {
        List<Address> list = new ArrayList<>();
        String sql = "SELECT * FROM address WHERE userID = ? AND addressType = ? AND isActive = 1 ORDER BY displayOrder ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ps.setString(2, addressType);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Address address = mapResultSetToAddress(rs);
                list.add(address);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting addresses by type", e);
        }

        return list;
    }

    // Đếm số lượng địa chỉ của user
    public int countAddressesByUser(int userID) {
        String sql = "SELECT COUNT(*) FROM address WHERE userID = ? AND isActive = 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error counting addresses", e);
        }

        return 0;
    }

    // Kiểm tra user có địa chỉ mặc định chưa
    public boolean userHasDefaultAddress(int userID) {
        String sql = "SELECT COUNT(*) FROM address WHERE userID = ? AND isDefault = 1 AND isActive = 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking default address", e);
        }

        return false;
    }

    // Map ResultSet to Address object
    private Address mapResultSetToAddress(ResultSet rs) throws SQLException {
        Address address = new Address();
        address.setAddressID(rs.getInt("addressID"));
        address.setUserID(rs.getInt("userID"));
        address.setDefault(rs.getBoolean("isDefault"));
        address.setAddressDetail(rs.getString("addressDetail"));
        address.setAddressName(rs.getString("addressName"));
        address.setRecipientName(rs.getString("recipientName"));
        address.setPhone(rs.getString("phone"));
        address.setProvince(rs.getString("province"));
        address.setDistrict(rs.getString("district"));
        address.setWard(rs.getString("ward"));
        address.setSpecificAddress(rs.getString("specificAddress"));
        address.setDisplayOrder(rs.getInt("displayOrder"));
        address.setAddressType(rs.getString("addressType"));
        address.setActive(rs.getBoolean("isActive"));
        
        return address;
    }
}