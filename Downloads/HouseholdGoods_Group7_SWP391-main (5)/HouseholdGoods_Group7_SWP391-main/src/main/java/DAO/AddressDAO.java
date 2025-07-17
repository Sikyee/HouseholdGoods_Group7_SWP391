package DAO;

import DB.DBConnection;
import Model.Address;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO {

    // Retrieves all addresses for a specific user, ordered by addressID in descending order
    public List<Address> getAddressesByUserID(int userID) {
        List<Address> addressList = new ArrayList<>();
        String sql = "SELECT * FROM address WHERE userID = ? ORDER BY addressID DESC";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Address address = new Address();
                address.setAddressID(rs.getInt("addressID"));
                address.setUserID(rs.getInt("userID"));
                address.setAddressName(rs.getString("addressName"));
                address.setRecipientName(rs.getString("recipientName"));
                address.setPhone(rs.getString("phone"));
                address.setAddressDetail(rs.getString("addressDetail"));
                address.setProvince(rs.getString("province"));
                address.setDistrict(rs.getString("district"));
                address.setWard(rs.getString("ward"));
                address.setAddressType(rs.getString("addressType"));

                try {
                    address.setDefault(rs.getBoolean("isDefault"));
                } catch (Exception e) {
                    address.setDefault(false);
                }

                try {
                    address.setActive(rs.getBoolean("isActive"));
                } catch (Exception e) {
                    address.setActive(true); // default to true
                }

                addressList.add(address);
            }

        } catch (Exception e) {
            System.out.println("Error in getAddressesByUserID: " + e.getMessage());
            e.printStackTrace();
        }

        return addressList;
    }

    // Retrieves a single address by its ID
    // Returns null if the address is not found
    public Address getAddressByID(int addressID) {
        String sql = "SELECT * FROM address WHERE addressID = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, addressID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Address address = new Address();
                address.setAddressID(rs.getInt("addressID"));
                address.setUserID(rs.getInt("userID"));
                address.setAddressName(rs.getString("addressName"));
                address.setRecipientName(rs.getString("recipientName"));
                address.setPhone(rs.getString("phone"));
                address.setAddressDetail(rs.getString("addressDetail"));
                address.setProvince(rs.getString("province"));
                address.setDistrict(rs.getString("district"));
                address.setWard(rs.getString("ward"));
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

                return address;
            }

        } catch (Exception e) {
            System.out.println("Error in getAddressByID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Inserts a new address into the database
    // Ensures that only one address is set as default per user
    public boolean addAddress(Address address) {
        String sql = "INSERT INTO address (userID, addressName, recipientName, phone, addressDetail, "
                + "province, district, ward, addressType, isDefault, isActive) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            // Unset other default addresses if this one is default
            if (address.isDefault()) {
                unsetOtherDefaultAddresses(address.getUserID());
            }

            ps.setInt(1, address.getUserID());
            ps.setString(2, address.getAddressName());
            ps.setString(3, address.getRecipientName());
            ps.setString(4, address.getPhone());
            ps.setString(5, address.getAddressDetail());
            ps.setString(6, address.getProvince());
            ps.setString(7, address.getDistrict());
            ps.setString(8, address.getWard());
            ps.setString(9, address.getAddressType());
            ps.setBoolean(10, address.isDefault());
            ps.setBoolean(11, true); // all new addresses are active by default

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error in addAddress: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Updates an existing address
    // Updates default flag and address details, but not active status
    public boolean updateAddress(Address address) {
        String sql = "UPDATE address SET addressName = ?, recipientName = ?, phone = ?, "
                + "addressDetail = ?, province = ?, district = ?, ward = ?, addressType = ?, "
                + "isDefault = ? WHERE addressID = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            if (address.isDefault()) {
                unsetOtherDefaultAddresses(address.getUserID());
            }

            ps.setString(1, address.getAddressName());
            ps.setString(2, address.getRecipientName());
            ps.setString(3, address.getPhone());
            ps.setString(4, address.getAddressDetail());
            ps.setString(5, address.getProvince());
            ps.setString(6, address.getDistrict());
            ps.setString(7, address.getWard());
            ps.setString(8, address.getAddressType());
            ps.setBoolean(9, address.isDefault());
            ps.setInt(10, address.getAddressID());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error in updateAddress: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Deletes an address permanently (hard delete)
    public boolean deleteAddress(int addressID) {
        String sql = "DELETE FROM address WHERE addressID = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, addressID);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error in deleteAddress: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Gets the default address for a user
    // Returns the first address where isDefault = true
    public Address getDefaultAddress(int userID) {
        String sql = "SELECT * FROM address WHERE userID = ? AND isDefault = 1";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Address address = new Address();
                address.setAddressID(rs.getInt("addressID"));
                address.setUserID(rs.getInt("userID"));
                address.setAddressName(rs.getString("addressName"));
                address.setRecipientName(rs.getString("recipientName"));
                address.setPhone(rs.getString("phone"));
                address.setAddressDetail(rs.getString("addressDetail"));
                address.setProvince(rs.getString("province"));
                address.setDistrict(rs.getString("district"));
                address.setWard(rs.getString("ward"));
                address.setAddressType(rs.getString("addressType"));
                address.setDefault(true);

                try {
                    address.setActive(rs.getBoolean("isActive"));
                } catch (Exception e) {
                    address.setActive(true);
                }

                return address;
            }

        } catch (Exception e) {
            System.out.println("Error in getDefaultAddress: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Sets a specific address as the default for the user
    // Step 1: unset all current default addresses
    // Step 2: set the specified address as default
    public boolean setDefaultAddress(int userID, int addressID) {
        try ( Connection conn = DBConnection.getConnection()) {

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

            return ps2.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error in setDefaultAddress: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Helper method to unset all default addresses for a user
    // Used to ensure only one default address per user
    private void unsetOtherDefaultAddresses(int userID) {
        String sql = "UPDATE address SET isDefault = 0 WHERE userID = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error in unsetOtherDefaultAddresses: " + e.getMessage());
        }
    }

    // Counts total number of addresses for a user
    // Includes both active and inactive addresses
    public int countAddressesByUser(int userID) {
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
}