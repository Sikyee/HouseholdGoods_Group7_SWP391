package DAO;

import DB.DBConnection;
import Model.Address;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO {

    // Thêm địa chỉ mới
    public boolean addAddress(Address address) {
        String unsetDefaultSql = "UPDATE address SET isDefault = 0 WHERE userID = ?";
        String insertSql = "INSERT INTO address (userID, isDefault, addressDetail) VALUES (?, ?, ?)";

        try ( Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Giao dịch

            try ( PreparedStatement ps1 = conn.prepareStatement(unsetDefaultSql);  PreparedStatement ps2 = conn.prepareStatement(insertSql)) {

                if (address.isDefault()) {
                    ps1.setInt(1, address.getUserID());
                    ps1.executeUpdate();
                }

                ps2.setInt(1, address.getUserID());
                ps2.setBoolean(2, address.isDefault());
                ps2.setString(3, address.getAddressDetail());
                ps2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Cập nhật địa chỉ
    public boolean updateAddress(Address address) {
        String unsetDefaultSql = "UPDATE address SET isDefault = 0 WHERE userID = ?";
        String updateSql = "UPDATE address SET addressDetail = ?, isDefault = ? WHERE addressID = ?";

        try ( Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try ( PreparedStatement ps1 = conn.prepareStatement(unsetDefaultSql);  PreparedStatement ps2 = conn.prepareStatement(updateSql)) {

                if (address.isDefault()) {
                    ps1.setInt(1, address.getUserID());
                    ps1.executeUpdate();
                }

                ps2.setString(1, address.getAddressDetail());
                ps2.setBoolean(2, address.isDefault());
                ps2.setInt(3, address.getAddressID());
                ps2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Xoá địa chỉ
    public boolean deleteAddress(int addressID) {
        String sql = "DELETE FROM address WHERE addressID = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, addressID);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy danh sách địa chỉ theo userID
    public List<Address> getAddressesByUserID(int userID) {
        List<Address> list = new ArrayList<>();
        String sql = "SELECT * FROM address WHERE userID = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Address address = new Address();
                address.setAddressID(rs.getInt("addressID"));
                address.setUserID(rs.getInt("userID"));
                address.setDefault(rs.getBoolean("isDefault"));
                address.setAddressDetail(rs.getString("addressDetail"));
                list.add(address);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Lấy địa chỉ theo ID
    public Address getAddressByID(int addressID) {
        String sql = "SELECT * FROM address WHERE addressID = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, addressID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Address address = new Address();
                address.setAddressID(rs.getInt("addressID"));
                address.setUserID(rs.getInt("userID"));
                address.setDefault(rs.getBoolean("isDefault"));
                address.setAddressDetail(rs.getString("addressDetail"));
                return address;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
