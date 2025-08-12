/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DB.DBConnection;
import Model.Attribute;
import Model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thong
 */
public class ProductDAO {

    public List<Product> getAllProducts() throws Exception {
        List<Product> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM Product WHERE status = 1";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Product p = new Product();
            p.setProductID(rs.getInt("productID"));
            p.setProductName(rs.getString("productName"));
            p.setDescription(rs.getString("description"));
            p.setSubCategory(rs.getInt("subCategory"));
            p.setPrice(rs.getLong("price"));
            p.setStonkQuantity(rs.getInt("stonk_Quantity"));
            p.setBrandID(rs.getInt("brandID"));
            p.setImage(rs.getString("image"));
            list.add(p);
            // Load attributes theo productID
            List<Attribute> attributes = getAttributesByProductId(p.getProductID());

            p.setAttributes(attributes);
        }
        return list;
    }

    public void addProduct(Product p) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO Product (productName, description, subCategory, price, stonk_Quantity, brandID, image) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, p.getProductName());
        ps.setString(2, p.getDescription());
        ps.setInt(3, p.getSubCategory());
        ps.setLong(4, p.getPrice());
        ps.setInt(5, p.getStonkQuantity());
        ps.setInt(6, p.getBrandID());
        ps.setString(7, p.getImage());
        ps.executeUpdate();
    }

    public void updateProduct(Product p) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "UPDATE Product SET productName=?, description=?, subCategory=?, price=?, stonk_Quantity=?, brandID=?, image=? WHERE productID=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, p.getProductName());
        ps.setString(2, p.getDescription());
        ps.setInt(3, p.getSubCategory());
        ps.setLong(4, p.getPrice());
        ps.setInt(5, p.getStonkQuantity());
        ps.setInt(6, p.getBrandID());
        ps.setString(7, p.getImage());
        ps.setInt(8, p.getProductID());
        ps.executeUpdate();
    }

    public void softDeleteProduct(int productId) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "UPDATE Product SET status = 0 WHERE productID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, productId);
        ps.executeUpdate();
    }

    public Product getProductById(int id) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM Product WHERE productID=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Product p = new Product();
            p.setProductID(rs.getInt("productID"));
            p.setProductName(rs.getString("productName"));
            p.setDescription(rs.getString("description"));
            p.setSubCategory(rs.getInt("subCategory"));
            p.setPrice(rs.getLong("price"));
            p.setStonkQuantity(rs.getInt("stonk_Quantity"));
            p.setBrandID(rs.getInt("brandID"));
            p.setImage(rs.getString("image"));
            return p;
        }
        return null;
    }

    public List<Attribute> getAttributesByProductId(int productId) throws Exception {
        List<Attribute> list = new ArrayList<>();
        String query = "SELECT a.attributeID, a.attributeName, pa.value "
                + "FROM Attribute a "
                + "JOIN ProductAttribute pa ON a.attributeID = pa.attributeID "
                + "WHERE a.productID = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Attribute a = new Attribute();
                a.setAttributeID(rs.getInt("attributeID"));
                a.setAttributeName(rs.getString("attributeName"));
                a.setAttributeValue(rs.getString("value"));
                list.add(a);
            }
        }
        return list;
    }

    public void updateProductAttributes(int productID, String[] names, String[] values) throws Exception {
        String delete = "DELETE FROM ProductAttribute WHERE productID = ?";
        String insertAttr = "INSERT INTO Attribute (attributeName, productID) VALUES (?, ?)";
        String insertProductAttr = "INSERT INTO ProductAttribute (productID, attributeID, value) VALUES (?, ?, ?)";

        try ( Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try ( PreparedStatement psDel = conn.prepareStatement(delete)) {
                psDel.setInt(1, productID);
                psDel.executeUpdate();
            }

            for (int i = 0; i < names.length; i++) {
                int attrID = -1;

                try ( PreparedStatement psInsertAttr = conn.prepareStatement(insertAttr, Statement.RETURN_GENERATED_KEYS)) {
                    psInsertAttr.setString(1, names[i]);
                    psInsertAttr.setInt(2, productID);
                    psInsertAttr.executeUpdate();
                    ResultSet rs = psInsertAttr.getGeneratedKeys();
                    if (rs.next()) {
                        attrID = rs.getInt(1);
                    }
                }

                try ( PreparedStatement psInsertVal = conn.prepareStatement(insertProductAttr)) {
                    psInsertVal.setInt(1, productID);
                    psInsertVal.setInt(2, attrID);
                    psInsertVal.setString(3, values[i]);
                    psInsertVal.executeUpdate();
                }
            }

            conn.commit();
        }
    }

    public int getLastInsertedProductIdByName(String name) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT TOP 1 productID FROM Product WHERE productName = ? ORDER BY productID DESC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }

    public List<Product> searchProducts(String keyword) throws Exception {
        List<Product> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM Product WHERE productName LIKE ? OR description LIKE ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, "%" + keyword + "%");
        ps.setString(2, "%" + keyword + "%");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Product p = new Product();
            p.setProductID(rs.getInt("productID"));
            p.setProductName(rs.getString("productName"));
            p.setDescription(rs.getString("description"));
            p.setSubCategory(rs.getInt("subCategory"));
            p.setPrice(rs.getLong("price"));
            p.setStonkQuantity(rs.getInt("stonk_Quantity"));
            p.setBrandID(rs.getInt("brandID"));
            p.setImage(rs.getString("image"));
            list.add(p);
        }

        return list;
    }

    public List<Product> filterProducts(Integer subCategory, Integer brandID, Long minPrice, Long maxPrice) throws Exception {
        List<Product> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();

        StringBuilder sql = new StringBuilder("SELECT * FROM Product WHERE status != 0");
        List<Object> params = new ArrayList<>();

        if (subCategory != null) {
            sql.append(" AND subCategory = ?");
            params.add(subCategory);
        }

        if (brandID != null) {
            sql.append(" AND brandID = ?");
            params.add(brandID);
        }

        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }

        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }

        PreparedStatement ps = conn.prepareStatement(sql.toString());

        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Product p = new Product();
            p.setProductID(rs.getInt("productID"));
            p.setProductName(rs.getString("productName"));
            p.setDescription(rs.getString("description"));
            p.setSubCategory(rs.getInt("subCategory"));
            p.setPrice(rs.getLong("price"));
            p.setStonkQuantity(rs.getInt("stonk_Quantity"));
            p.setBrandID(rs.getInt("brandID"));
            p.setImage(rs.getString("image"));
            p.setAttributes(getAttributesByProductId(p.getProductID())); // nếu có
            list.add(p);
        }

        return list;
    }

    public void updateStockAfterPurchase(int productId, int quantityPurchased) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "UPDATE Product SET stonk_Quantity = stonk_Quantity - ? WHERE productID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, quantityPurchased);
        ps.setInt(2, productId);
        ps.executeUpdate();
    }
}
