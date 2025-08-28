package DAO;

import DB.DBConnection;
import Model.Promotion;

import java.sql.*;
import java.util.*;

public class PromotionDAO {

    private final Connection connection;

    public PromotionDAO() throws Exception {
        this.connection = DBConnection.getConnection();
    }

    /* ===================== LISTING (full) ===================== */
    /** Trả về tất cả promotions (không lọc theo isActive). */
    public List<Promotion> getAllPromotions() throws SQLException {
        List<Promotion> promotions = new ArrayList<>();
        String sql =
            "SELECT p.*, b.brandName " +
            "FROM Promotion p " +
            "LEFT JOIN Brand b ON TRY_CAST(p.brand AS INT) = b.brandID " +
            "ORDER BY p.promotionID ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) promotions.add(extractPromotion(rs));
        }
        return promotions;
    }

    /** (Tuỳ chọn) Danh sách inactive (legacy) */
    public List<Promotion> getDeletedPromotions() throws SQLException {
        List<Promotion> deleted = new ArrayList<>();
        String sql =
            "SELECT p.*, b.brandName " +
            "FROM Promotion p " +
            "LEFT JOIN Brand b ON TRY_CAST(p.brand AS INT) = b.brandID " +
            "WHERE p.isActive = 0 " +
            "ORDER BY p.promotionID ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) deleted.add(extractPromotion(rs));
        }
        return deleted;
    }

    /* ===================== (Legacy) PAGINATION (active only) ===================== */
    public int countActivePromotions() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Promotion WHERE isActive = 1";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public List<Promotion> getActivePromotionsPage(int page, int pageSize) throws SQLException {
        List<Promotion> promotions = new ArrayList<>();
        int safePage = Math.max(page, 1);
        int offset = (safePage - 1) * pageSize;

        String sql =
            "SELECT p.*, b.brandName " +
            "FROM Promotion p " +
            "LEFT JOIN Brand b ON TRY_CAST(p.brand AS INT) = b.brandID " +
            "WHERE p.isActive = 1 " +
            "ORDER BY p.promotionID ASC " +
            "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) promotions.add(extractPromotion(rs));
            }
        }
        return promotions;
    }

    /* ===================== SINGLE FETCH ===================== */
    public Promotion getPromotionById(int id) throws SQLException {
        String sql =
            "SELECT p.*, b.brandName " +
            "FROM Promotion p " +
            "LEFT JOIN Brand b ON TRY_CAST(p.brand AS INT) = b.brandID " +
            "WHERE p.promotionID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractPromotion(rs);
            }
        }
        return null;
    }

    public Promotion getPromotionByCode(String code) throws SQLException {
        String sql =
            "SELECT p.*, b.brandName " +
            "FROM Promotion p " +
            "LEFT JOIN Brand b ON TRY_CAST(p.brand AS INT) = b.brandID " +
            "WHERE p.code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractPromotion(rs);
            }
        }
        return null;
    }

    /* ===================== CUD ===================== */
    public void addPromotion(Promotion p) throws SQLException {
        String sql =
            "INSERT INTO Promotion " +
            "(code, title, description, discountType, discountValue, startDate, endDate, minOrderValue, isActive, brand) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            prepareStatement(ps, p);
            ps.executeUpdate();
        }
    }

    public void updatePromotion(Promotion p) throws SQLException {
        String sql =
            "UPDATE Promotion " +
            "SET code=?, title=?, description=?, discountType=?, discountValue=?, startDate=?, endDate=?, minOrderValue=?, isActive=?, brand=? " +
            "WHERE promotionID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            prepareStatement(ps, p);
            ps.setInt(11, p.getPromotionID());
            ps.executeUpdate();
        }
    }

    /** Legacy naming (giữ để tương thích) */
    public void softDeletePromotion(int id) throws SQLException { setActive(id, false); }
    public void reactivatePromotion(int id) throws SQLException { setActive(id, true); }

    /** Bật/tắt promotion */
    public void setActive(int id, boolean active) throws SQLException {
        String sql = "UPDATE Promotion SET isActive = ? WHERE promotionID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    /* ===================== APPLICABILITY HELPERS ===================== */
    /**
     * Kiểm tra promotion có áp dụng cho đơn hàng không:
     * - isActive == true
     * - onDate nằm trong [startDate, endDate] (nếu các trường này khác null)
     * - brand khớp (nếu promotion có brandID)
     * - orderTotal >= minOrderValue
     */
    public boolean isApplicableForOrder(Promotion p, Integer brandId, long orderTotal, java.sql.Date onDate) {
        if (p == null) return false;
        if (!p.isIsActive()) return false;

        java.sql.Date today = (onDate != null) ? onDate : new java.sql.Date(System.currentTimeMillis());
        if (p.getStartDate() != null && today.before(p.getStartDate())) return false;
        if (p.getEndDate() != null && today.after(p.getEndDate())) return false;

        // brand: nếu promo có brandID thì phải trùng
        if (p.getBrandID() != null) {
            if (brandId == null || !p.getBrandID().equals(brandId)) return false;
        }
        // Min Price
        long min = (p.getMinOrderValue() != 0L) ? p.getMinOrderValue() : 0L;
        if (orderTotal < min) return false;

        return true;
    }

    /** Lấy promotion theo ID, chỉ trả về nếu thỏa điều kiện áp dụng (tiện dùng ở checkout). */
    public Promotion getPromotionIfApplicable(int promotionId, Integer brandId, long orderTotal, java.sql.Date onDate) throws SQLException {
        Promotion p = getPromotionById(promotionId);
        return isApplicableForOrder(p, brandId, orderTotal, onDate) ? p : null;
    }

    /* ===================== HELPERS ===================== */
    private Promotion extractPromotion(ResultSet rs) throws SQLException {
        Promotion p = new Promotion();
        p.setPromotionID(rs.getInt("promotionID"));
        p.setCode(rs.getString("code"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setDiscountType(rs.getString("discountType"));
        p.setDiscountValue(rs.getLong("discountValue"));
        p.setStartDate(rs.getDate("startDate"));
        p.setEndDate(rs.getDate("endDate"));
        p.setMinOrderValue(rs.getLong("minOrderValue"));
        p.setIsActive(rs.getBoolean("isActive"));

        // brand (NVARCHAR) -> parse thành Integer nếu là số
        String brandStr = rs.getString("brand");
        if (brandStr != null && brandStr.matches("\\d+")) {
            p.setBrandID(Integer.parseInt(brandStr));
        } else {
            p.setBrandID(null);
        }

        try {
            p.setBrandName(rs.getString("brandName"));
        } catch (SQLException ignore) {
            p.setBrandName(null);
        }
        return p;
    }

    private void prepareStatement(PreparedStatement ps, Promotion p) throws SQLException {
        ps.setString(1, p.getCode());
        ps.setString(2, p.getTitle());
        ps.setString(3, p.getDescription());
        ps.setString(4, p.getDiscountType());          // "percentage" | "fixed"
        ps.setLong(5, p.getDiscountValue());
        ps.setDate(6, p.getStartDate());
        ps.setDate(7, p.getEndDate());
        ps.setLong(8, p.getMinOrderValue());
        ps.setBoolean(9, p.isIsActive());

        // brand là NVARCHAR trong DB
        if (p.getBrandID() != null) ps.setString(10, String.valueOf(p.getBrandID()));
        else ps.setNull(10, Types.NVARCHAR);
    }
}
