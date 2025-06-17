package DAO;

import DB.DBConnection;
import Model.Address;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO {

    public boolean addAddress(Address address) {
        String unsetDefaultSql = "UPDATE address SET isDefault = 0 WHERE customerID = ?";
        String insertSql = "INSERT INTO address (customerID, isDefault, addressDetail) VALUES (?, ?, ?)";

        try ( Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Giao dịch

            try ( PreparedStatement ps1 = conn.prepareStatement(unsetDefaultSql);  PreparedStatement ps2 = conn.prepareStatement(insertSql)) {

                // Nếu là default thì reset tất cả trước
                if (address.isDefault()) {
                    ps1.setInt(1, address.getCustomerID());
                    ps1.executeUpdate();
                }

                ps2.setInt(1, address.getCustomerID());
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

    public List<Address> getAddressesByCustomerID(int customerID) {
        List<Address> list = new ArrayList<>();
        String sql = "SELECT * FROM address WHERE customerID = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Address address = new Address();
                address.setAddressID(rs.getInt("addressID"));
                address.setCustomerID(rs.getInt("customerID"));
                address.setDefault(rs.getBoolean("isDefault"));
                address.setAddressDetail(rs.getString("addressDetail"));
                list.add(address);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
