package DAO;

import DB.DBConnection;
import Model.Address;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AddressDAO {

    // Regex patterns for validation
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+84|0)[3-9][0-9]{8}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\s.'-]{2,100}$");

    // Validation constants
    private static final int MAX_ADDRESS_NAME_LENGTH = 100;
    private static final int MAX_RECIPIENT_NAME_LENGTH = 100;
    private static final int MAX_ADDRESS_DETAIL_LENGTH = 255;
    private static final int MAX_ADDRESS_TYPE_LENGTH = 20;
    private static final int MAX_ADDRESSES_PER_USER = 10;

    /**
     * Validates an Address object
     *
     * @param address Address to validate
     * @param isUpdate true if this is an update operation
     * @return ValidationResult containing validation status and errors
     */
    public ValidationResult validateAddress(Address address, boolean isUpdate) {
        ValidationResult result = new ValidationResult();

        if (address == null) {
            result.addError("Address object cannot be null");
            return result;
        }

        // Validate userID
        if (address.getUserID() <= 0) {
            result.addError("User ID must be greater than 0");
        }

        // Validate addressID for updates
        if (isUpdate && address.getAddressID() <= 0) {
            result.addError("Address ID must be greater than 0 for update operations");
        }

        // Validate address name
        if (isNullOrEmpty(address.getAddressName())) {
            result.addError("Address name is required");
        } else if (address.getAddressName().length() > MAX_ADDRESS_NAME_LENGTH) {
            result.addError("Address name must not exceed " + MAX_ADDRESS_NAME_LENGTH + " characters");
        }

        // Validate recipient name
        if (isNullOrEmpty(address.getRecipientName())) {
            result.addError("Recipient name is required");
        } else if (address.getRecipientName().length() > MAX_RECIPIENT_NAME_LENGTH) {
            result.addError("Recipient name must not exceed " + MAX_RECIPIENT_NAME_LENGTH + " characters");
        } else if (!NAME_PATTERN.matcher(address.getRecipientName().trim()).matches()) {
            result.addError("Recipient name contains invalid characters");
        }

        // Validate phone number
        if (isNullOrEmpty(address.getPhone())) {
            result.addError("Phone number is required");
        } else if (!PHONE_PATTERN.matcher(address.getPhone().replaceAll("\\s", "")).matches()) {
            result.addError("Phone number format is invalid (must be Vietnamese phone number)");
        }

        // Validate address detail
        if (isNullOrEmpty(address.getAddressDetail())) {
            result.addError("Address detail is required");
        } else if (address.getAddressDetail().length() > MAX_ADDRESS_DETAIL_LENGTH) {
            result.addError("Address detail must not exceed " + MAX_ADDRESS_DETAIL_LENGTH + " characters");
        }

        // Validate address type
        if (!isNullOrEmpty(address.getAddressType())
                && address.getAddressType().length() > MAX_ADDRESS_TYPE_LENGTH) {
            result.addError("Address type must not exceed " + MAX_ADDRESS_TYPE_LENGTH + " characters");
        }

        return result;
    }

    /**
     * Validates if user can add more addresses
     *
     * @param userID User ID to check
     * @return true if user can add more addresses
     */
    private boolean canAddMoreAddresses(int userID) {
        return countAddressesByUser(userID) < MAX_ADDRESSES_PER_USER;
    }

    /**
     * Checks if user has a default address
     *
     * @param userID User ID to check
     * @return true if user has a default address
     */
    private boolean hasDefaultAddress(int userID) {
        return getDefaultAddress(userID) != null;
    }

    /**
     * Checks if the address is the default address
     *
     * @param addressID Address ID to check
     * @return true if this is the default address
     */
    private boolean isDefaultAddress(int addressID) {
        Address address = getAddressByID(addressID);
        return address != null && address.isDefault();
    }

    /**
     * Counts active addresses for a user
     *
     * @param userID User ID to check
     * @return number of active addresses
     */
    private int countActiveAddresses(int userID) {
        String sql = "SELECT COUNT(*) FROM address WHERE userID = ? AND isActive = 1";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            System.out.println("Error in countActiveAddresses: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Checks if user has permission to modify the address
     *
     * @param addressID Address ID to check
     * @param userID User ID to verify ownership
     * @return true if user owns the address
     */
    private boolean hasAddressPermission(int addressID, int userID) {
        Address address = getAddressByID(addressID);
        return address != null && address.getUserID() == userID;
    }

    /**
     * Helper method to check if string is null or empty
     */
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Retrieves all addresses for a specific user, ordered by addressID in descending order
    public List<Address> getAddressesByUserID(int userID) {
        if (userID <= 0) {
            System.out.println("Invalid userID: " + userID);
            return new ArrayList<>();
        }

        List<Address> addressList = new ArrayList<>();
        String sql = "SELECT * FROM address WHERE userID = ? ORDER BY addressID DESC";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Address address = mapResultSetToAddress(rs);
                if (address != null) {
                    addressList.add(address);
                }
            }

        } catch (Exception e) {
            System.out.println("Error in getAddressesByUserID: " + e.getMessage());
            e.printStackTrace();
        }

        return addressList;
    }

    // Retrieves a single address by its ID
    public Address getAddressByID(int addressID) {
        if (addressID <= 0) {
            System.out.println("Invalid addressID: " + addressID);
            return null;
        }

        String sql = "SELECT * FROM address WHERE addressID = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, addressID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToAddress(rs);
            }

        } catch (Exception e) {
            System.out.println("Error in getAddressByID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // UPDATED: Removed province, district, ward fields
    public boolean addAddress(Address address) {
        // Validate input
        ValidationResult validation = validateAddress(address, false);
        if (!validation.isValid()) {
            System.out.println("Validation failed: " + validation.getErrorsAsString());
            return false;
        }

        // Check if user can add more addresses
        if (!canAddMoreAddresses(address.getUserID())) {
            System.out.println("User has reached maximum number of addresses (" + MAX_ADDRESSES_PER_USER + ")");
            return false;
        }

        // If user doesn't have any address yet, make this address default
        boolean shouldSetAsDefault = !hasDefaultAddress(address.getUserID());
        if (shouldSetAsDefault) {
            address.setDefault(true);
        }

        String sql = "INSERT INTO address (userID, addressName, recipientName, phone, addressDetail, "
                + "addressType, isDefault, isActive, displayOrder) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            // Unset other default addresses if this one is default
            if (address.isDefault()) {
                unsetOtherDefaultAddresses(address.getUserID());
            }

            ps.setInt(1, address.getUserID());
            ps.setString(2, address.getAddressName().trim());
            ps.setString(3, address.getRecipientName().trim());
            ps.setString(4, address.getPhone().replaceAll("\\s", ""));
            ps.setString(5, address.getAddressDetail().trim());
            ps.setString(6, address.getAddressType() != null ? address.getAddressType().trim() : null);
            ps.setBoolean(7, address.isDefault());
            ps.setBoolean(8, true); // all new addresses are active by default
            ps.setInt(9, 0); // displayOrder default value

            boolean success = ps.executeUpdate() > 0;

            if (success && shouldSetAsDefault) {
                System.out.println("Address added and set as default (user's first address)");
            }

            return success;

        } catch (Exception e) {
            System.out.println("Error in addAddress: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // UPDATED: Removed province, district, ward fields
    public boolean updateAddress(Address address) {
        // Validate input
        ValidationResult validation = validateAddress(address, true);
        if (!validation.isValid()) {
            System.out.println("Validation failed: " + validation.getErrorsAsString());
            return false;
        }

        // Check permission
        if (!hasAddressPermission(address.getAddressID(), address.getUserID())) {
            System.out.println("User does not have permission to update this address");
            return false;
        }

        // Get current address to check if it's currently default
        Address currentAddress = getAddressByID(address.getAddressID());
        if (currentAddress == null) {
            System.out.println("Address not found");
            return false;
        }

        // If trying to unset default flag from a default address, check if there are other addresses
        if (currentAddress.isDefault() && !address.isDefault()) {
            int activeAddressCount = countActiveAddresses(address.getUserID());
            if (activeAddressCount <= 1) {
                System.out.println("Cannot unset default flag: User must have at least one default address");
                return false;
            }
        }

        String sql = "UPDATE address SET addressName = ?, recipientName = ?, phone = ?, "
                + "addressDetail = ?, addressType = ?, isDefault = ? WHERE addressID = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            if (address.isDefault()) {
                unsetOtherDefaultAddresses(address.getUserID());
            }

            ps.setString(1, address.getAddressName().trim());
            ps.setString(2, address.getRecipientName().trim());
            ps.setString(3, address.getPhone().replaceAll("\\s", ""));
            ps.setString(4, address.getAddressDetail().trim());
            ps.setString(5, address.getAddressType() != null ? address.getAddressType().trim() : null);
            ps.setBoolean(6, address.isDefault());
            ps.setInt(7, address.getAddressID());

            boolean success = ps.executeUpdate() > 0;

            // If we just unset the default flag and update was successful, 
            // we need to set another address as default
            if (success && currentAddress.isDefault() && !address.isDefault()) {
                setFirstActiveAddressAsDefault(address.getUserID());
            }

            return success;

        } catch (Exception e) {
            System.out.println("Error in updateAddress: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Deletes an address with validation
    public boolean deleteAddress(int addressID, int userID) {
        if (addressID <= 0) {
            System.out.println("Invalid addressID: " + addressID);
            return false;
        }

        if (userID <= 0) {
            System.out.println("Invalid userID: " + userID);
            return false;
        }

        // Check permission
        if (!hasAddressPermission(addressID, userID)) {
            System.out.println("User does not have permission to delete this address");
            return false;
        }

        // Check if this is the default address
        if (isDefaultAddress(addressID)) {
            int activeAddressCount = countActiveAddresses(userID);
            if (activeAddressCount <= 1) {
                System.out.println("Cannot delete the only address. User must have at least one address.");
                return false;
            } else {
                System.out.println("Cannot delete default address. Please set another address as default first.");
                return false;
            }
        }

        String sql = "DELETE FROM address WHERE addressID = ? AND userID = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, addressID);
            ps.setInt(2, userID);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error in deleteAddress: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Soft delete an address (set as inactive) - Alternative to hard delete
     * This method can be used if you want to keep address history
     */
    public boolean deactivateAddress(int addressID, int userID) {
        if (addressID <= 0 || userID <= 0) {
            System.out.println("Invalid addressID or userID");
            return false;
        }

        // Check permission
        if (!hasAddressPermission(addressID, userID)) {
            System.out.println("User does not have permission to deactivate this address");
            return false;
        }

        // Check if this is the default address
        if (isDefaultAddress(addressID)) {
            int activeAddressCount = countActiveAddresses(userID);
            if (activeAddressCount <= 1) {
                System.out.println("Cannot deactivate the only active address. User must have at least one active address.");
                return false;
            } else {
                System.out.println("Cannot deactivate default address. Please set another address as default first.");
                return false;
            }
        }

        String sql = "UPDATE address SET isActive = 0 WHERE addressID = ? AND userID = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, addressID);
            ps.setInt(2, userID);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error in deactivateAddress: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Gets the default address for a user
    public Address getDefaultAddress(int userID) {
        if (userID <= 0) {
            System.out.println("Invalid userID: " + userID);
            return null;
        }

        String sql = "SELECT * FROM address WHERE userID = ? AND isDefault = 1 AND isActive = 1";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToAddress(rs);
            }

        } catch (Exception e) {
            System.out.println("Error in getDefaultAddress: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Sets a specific address as the default for the user with validation
    public boolean setDefaultAddress(int userID, int addressID) {
        if (userID <= 0 || addressID <= 0) {
            System.out.println("Invalid userID or addressID");
            return false;
        }

        // Check permission
        if (!hasAddressPermission(addressID, userID)) {
            System.out.println("User does not have permission to set this address as default");
            return false;
        }

        try ( Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Step 1: unset all current default addresses
                String sql1 = "UPDATE address SET isDefault = 0 WHERE userID = ?";
                PreparedStatement ps1 = conn.prepareStatement(sql1);
                ps1.setInt(1, userID);
                ps1.executeUpdate();

                // Step 2: set the selected address as default
                String sql2 = "UPDATE address SET isDefault = 1 WHERE addressID = ? AND userID = ?";
                PreparedStatement ps2 = conn.prepareStatement(sql2);
                ps2.setInt(1, addressID);
                ps2.setInt(2, userID);

                int rowsAffected = ps2.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }

            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            System.out.println("Error in setDefaultAddress: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Helper method to unset all default addresses for a user
    private void unsetOtherDefaultAddresses(int userID) {
        String sql = "UPDATE address SET isDefault = 0 WHERE userID = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error in unsetOtherDefaultAddresses: " + e.getMessage());
        }
    }

    /**
     * Helper method to set the first active address as default when current
     * default is removed
     *
     * @param userID User ID
     */
    private void setFirstActiveAddressAsDefault(int userID) {
        String sql = "UPDATE address SET isDefault = 1 WHERE addressID = "
                + "(SELECT addressID FROM (SELECT addressID FROM address WHERE userID = ? "
                + "AND isActive = 1 ORDER BY addressID ASC LIMIT 1) AS temp)";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Automatically set first active address as default for user: " + userID);
            }

        } catch (Exception e) {
            System.out.println("Error in setFirstActiveAddressAsDefault: " + e.getMessage());
            // Fallback: try a simpler approach
            try {
                List<Address> addresses = getAddressesByUserID(userID);
                for (Address addr : addresses) {
                    if (addr.isActive() && !addr.isDefault()) {
                        setDefaultAddress(userID, addr.getAddressID());
                        break;
                    }
                }
            } catch (Exception ex) {
                System.out.println("Fallback method also failed: " + ex.getMessage());
            }
        }
    }

    // Counts total number of addresses for a user
    public int countAddressesByUser(int userID) {
        if (userID <= 0) {
            System.out.println("Invalid userID: " + userID);
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM address WHERE userID = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            System.out.println("Error in countAddressesByUser: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Helper method to map ResultSet to Address object UPDATED: Removed
     * province, district, ward fields
     */
    private Address mapResultSetToAddress(ResultSet rs) {
        try {
            Address address = new Address();
            address.setAddressID(rs.getInt("addressID"));
            address.setUserID(rs.getInt("userID"));
            address.setAddressName(rs.getString("addressName"));
            address.setRecipientName(rs.getString("recipientName"));
            address.setPhone(rs.getString("phone"));
            address.setAddressDetail(rs.getString("addressDetail"));
            address.setAddressType(rs.getString("addressType"));

            try {
                address.setDefault(rs.getBoolean("isDefault"));
            } catch (Exception e) {
                address.setDefault(false);
            }

            try {
                address.setActive(rs.getBoolean("isActive"));
            } catch (Exception e) {
                address.setActive(true);
            }

            try {
                address.setDisplayOrder(rs.getInt("displayOrder"));
            } catch (Exception e) {
                address.setDisplayOrder(0);
            }

            return address;
        } catch (Exception e) {
            System.out.println("Error mapping ResultSet to Address: " + e.getMessage());
            return null;
        }
    }

    /**
     * Inner class for validation results
     */
    public static class ValidationResult {

        private List<String> errors = new ArrayList<>();

        public void addError(String error) {
            errors.add(error);
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getErrorsAsString() {
            return String.join(", ", errors);
        }
    }
}
