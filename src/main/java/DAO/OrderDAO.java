package DAO;

import DB.DBConnection;
import Model.MonthlyRevenue;
import Model.Order;
import Model.OrderDetail;
import Model.OrderInfo;
import Model.StatusCount;
import Model.dto.WeeklyRevenue;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderDAO {

    // ===================== Utilities =====================

    /** Dò tên cột tồn kho thật sự trong bảng Product */
    private String detectProductQtyColumn(Connection conn) throws SQLException {
        String sql = "SELECT COLUMN_NAME " +
                     "FROM INFORMATION_SCHEMA.COLUMNS " +
                     "WHERE TABLE_NAME = 'Product' " +
                     "  AND COLUMN_NAME IN ('stonkQuantity','stockQuantity','quantity','qty','stock')";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // Ưu tiên theo thứ tự đã nêu
            List<String> found = new ArrayList<>();
            while (rs.next()) found.add(rs.getString(1));

            String[] priority = {"stonkQuantity","stockQuantity","quantity","qty","stock"};
            for (String c : priority) {
                if (found.contains(c)) return c;
            }
        }
        throw new SQLException("No stock column found on table Product. Expected one of stonkQuantity/stockQuantity/quantity/qty/stock");
    }

    /** Hoàn kho cho toàn bộ item của 1 order vào bảng Product.<qtyCol> */
    private void restockOrderItems(Connection conn, int orderID) throws SQLException {
        String qtyCol = detectProductQtyColumn(conn);

        String selectDetailsSql  = "SELECT productID, quantity FROM OrderDetail WHERE orderID = ?";
        String restockProductSql = "UPDATE Product SET " + qtyCol + " = " + qtyCol + " + ? WHERE productID = ?";

        try (PreparedStatement psDtl = conn.prepareStatement(selectDetailsSql);
             PreparedStatement psProd = conn.prepareStatement(restockProductSql)) {

            psDtl.setInt(1, orderID);
            try (ResultSet rs = psDtl.executeQuery()) {
                while (rs.next()) {
                    int productID = rs.getInt("productID");
                    int qty       = rs.getInt("quantity");

                    psProd.setInt(1, qty);
                    psProd.setInt(2, productID);
                    psProd.addBatch();
                }
            }
            psProd.executeBatch();
        }
    }

    private OrderInfo extractOrderFromResultSet(ResultSet rs) throws SQLException {
        OrderInfo order = new OrderInfo();
        order.setOrderID(rs.getInt("orderID"));
        order.setUserID(rs.getInt("userID"));
        order.setOrderStatusID(rs.getInt("orderStatusID"));
        order.setOrderDate(rs.getTimestamp("orderDate"));
        order.setPaymentMethodID(rs.getInt("paymentMethodID"));
        order.setVoucherID(rs.getObject("voucherID") != null ? rs.getInt("voucherID") : null);
        order.setTotalPrice(rs.getDouble("totalPrice"));
        order.setFinalPrice(rs.getDouble("finalPrice"));
        order.setFullName(rs.getString("fullName"));
        order.setDeliveryAddress(rs.getString("deliveryAddress"));
        order.setPhone(rs.getString("phone"));
        return order;
    }

    // ===================== Business =====================

    public List<OrderInfo> getOrdersByUserAndStatus(int userID, int statusID) {
        List<OrderInfo> list = new ArrayList<>();
        String sql = "SELECT * FROM OrderInfo WHERE userID = ? AND orderStatusID = ? ";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            ps.setInt(2, statusID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(extractOrderFromResultSet(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public OrderInfo getOrderByID(int orderID) {
        String sql = "SELECT * FROM OrderInfo WHERE orderID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractOrderFromResultSet(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /** Cập nhật trạng thái; nếu chuyển sang Canceled(6) lần đầu thì hoàn kho vào Product.<qtyCol> */
    public void updateOrderStatus(int orderID, int newStatusID) {
        final int CANCELED = 6;

        String sqlSelectStatus = "SELECT orderStatusID FROM OrderInfo WITH (UPDLOCK, ROWLOCK) WHERE orderID = ?";
        String sqlUpdateStatus = "UPDATE OrderInfo SET orderStatusID = ? WHERE orderID = ?";

        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int oldStatus;
            try (PreparedStatement psSel = conn.prepareStatement(sqlSelectStatus)) {
                psSel.setInt(1, orderID);
                rs = psSel.executeQuery();
                if (!rs.next()) { conn.rollback(); return; }
                oldStatus = rs.getInt(1);
            } finally { if (rs != null) try { rs.close(); } catch (Exception ignore) {} }

            if (oldStatus == newStatusID) { conn.rollback(); return; }

            try (PreparedStatement psUpd = conn.prepareStatement(sqlUpdateStatus)) {
                psUpd.setInt(1, newStatusID);
                psUpd.setInt(2, orderID);
                if (psUpd.executeUpdate() == 0) { conn.rollback(); return; }
            }

            if (newStatusID == CANCELED && oldStatus != CANCELED) {
                restockOrderItems(conn, orderID);
            }

            conn.commit();
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignore) {}
            e.printStackTrace();
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (Exception ignore) {}
        }
    }

    public List<OrderInfo> getAllOrdersByStatus(int statusID) {
        List<OrderInfo> list = new ArrayList<>();
        String sql = "SELECT * FROM OrderInfo WHERE orderStatusID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statusID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(extractOrderFromResultSet(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int createOrder(OrderInfo order) throws Exception {
        String sql = "INSERT INTO OrderInfo (userID, orderStatusID, orderDate, paymentMethodID, voucherID, totalPrice, finalPrice, fullName, deliveryAddress, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getUserID());
            ps.setInt(2, order.getOrderStatusID());
            ps.setTimestamp(3, new Timestamp(order.getOrderDate().getTime()));
            ps.setInt(4, order.getPaymentMethodID());
            if (order.getVoucherID() == null || order.getVoucherID() == 0) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, order.getVoucherID());
            }
            ps.setDouble(6, order.getTotalPrice());
            ps.setDouble(7, order.getFinalPrice());
            ps.setString(8, order.getFullName());
            ps.setString(9, order.getDeliveryAddress());
            ps.setString(10, order.getPhone());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public boolean updateOrderStatus(OrderInfo order) throws Exception {
        String sql = "UPDATE [dbo].[OrderInfo]\n"
                + "   SET [orderStatusID] = ?\n"
                + " WHERE orderId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, order.getOrderStatusID());
            st.setInt(2, order.getOrderID());
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return false;
    }

    public void cancelOrder(int orderID) {
        try {
            updateOrderStatus(orderID, 6); // Canceled
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRevenueToday() {
        String sql = "SELECT SUM(finalPrice) AS total FROM OrderInfo WHERE orderStatusID = 1 AND orderDate BETWEEN ? AND ?";
        int total = 0;
        LocalDate today = LocalDate.now();
        Timestamp startOfDay = Timestamp.valueOf(today.atStartOfDay());
        Timestamp endOfDay = Timestamp.valueOf(today.atTime(LocalTime.MAX));
        try (Connection connect = DBConnection.getConnection();
             PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setTimestamp(1, startOfDay);
            ps.setTimestamp(2, endOfDay);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) total = rs.getInt("total");
            }
        } catch (Exception ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return total;
    }

    public int getRevenueLastDay() {
        String sql = "SELECT SUM(finalPrice) AS total FROM OrderInfo WHERE orderStatusID = 1 AND orderDate BETWEEN ? AND ?";
        int total = 0;
        LocalDate today = LocalDate.now();
        Timestamp startOfDay = Timestamp.valueOf(today.atStartOfDay());
        Timestamp endOfDay = Timestamp.valueOf(today.atTime(LocalTime.MAX));
        try (Connection connect = DBConnection.getConnection();
             PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setTimestamp(1, startOfDay);
            ps.setTimestamp(2, endOfDay);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) total = rs.getInt("total");
            }
        } catch (Exception ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return total;
    }

    public List<WeeklyRevenue> getRevenueLast7Days() {
        String sql
                = "WITH d AS ( "
                + "  SELECT CAST(CAST(GETDATE() AS date) AS datetime) AS d, 0 AS n "
                + "  UNION ALL "
                + "  SELECT DATEADD(day, -1, d), n + 1 FROM d WHERE n < 6 "
                + ") "
                + "SELECT CAST(d.d AS date) AS orderDay, ISNULL(SUM(oi.finalPrice), 0) AS totalRevenue "
                + "FROM d "
                + "LEFT JOIN OrderInfo oi "
                + "  ON CAST(oi.orderDate AS date) = CAST(d.d AS date) "
                + " AND oi.orderStatusID = 1 "
                + "GROUP BY CAST(d.d AS date) "
                + "ORDER BY orderDay ASC "
                + "OPTION (MAXRECURSION 100);";

        List<WeeklyRevenue> result = new ArrayList<>();
        try (Connection connect = DBConnection.getConnection();
             PreparedStatement ps = connect.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LocalDate day = rs.getDate("orderDay").toLocalDate();
                double total = rs.getDouble("totalRevenue");
                result.add(new WeeklyRevenue(day, total));
            }
        } catch (Exception ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public List<WeeklyRevenue> getWeeklyRevenue(LocalDate startDate, LocalDate endDate, int statusId) {
        Timestamp startTs = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTsExclusive = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());

        String sql
                = "WITH weeks AS ( "
                + "  SELECT CAST(DATEADD(day, -((DATEPART(weekday, ?)+@@DATEFIRST-2)%7), CAST(? AS date)) AS date) AS weekStart "
                + "  UNION ALL "
                + "  SELECT DATEADD(week, 1, weekStart) FROM weeks WHERE weekStart < ? "
                + ") "
                + "SELECT w.weekStart AS weekStart, ISNULL(SUM(oi.finalPrice),0) AS total "
                + "FROM weeks w "
                + "LEFT JOIN ( "
                + "  SELECT TRY_CONVERT(date, orderDate) AS d, finalPrice "
                + "  FROM OrderInfo "
                + "  WHERE orderStatusID = ? AND orderDate >= ? AND orderDate < ? "
                + ") oi ON oi.d >= w.weekStart AND oi.d < DATEADD(week, 1, w.weekStart) "
                + "GROUP BY w.weekStart "
                + "ORDER BY w.weekStart "
                + "OPTION (MAXRECURSION 0);";

        List<WeeklyRevenue> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(startDate));
            ps.setDate(3, java.sql.Date.valueOf(endDate));

            ps.setInt(4, statusId);
            ps.setTimestamp(5, startTs);
            ps.setTimestamp(6, endTsExclusive);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate weekStart = rs.getDate("weekStart").toLocalDate();
                    double total = rs.getDouble("total");
                    list.add(new WeeklyRevenue(weekStart, total));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<MonthlyRevenue> getMonthlyRevenue(LocalDate startDate, LocalDate endDate, int statusId) throws SQLException {
        Timestamp startTs = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTsExclusive = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());

        String sql
                = "WITH o AS ( "
                + "  SELECT TRY_CONVERT(datetime, orderDate) AS dt, finalPrice, orderStatusID "
                + "  FROM OrderInfo "
                + ") "
                + "SELECT YEAR(dt) AS y, MONTH(dt) AS m, ISNULL(SUM(finalPrice),0) AS total "
                + "FROM o "
                + "WHERE dt IS NOT NULL "
                + "  AND orderStatusID = ? "
                + "  AND dt >= ? AND dt < ? "
                + "GROUP BY YEAR(dt), MONTH(dt) "
                + "ORDER BY y, m";

        List<MonthlyRevenue> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, statusId);
            ps.setTimestamp(2, startTs);
            ps.setTimestamp(3, endTsExclusive);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int y = rs.getInt("y");
                    int m = rs.getInt("m");
                    double total = rs.getDouble("total");
                    list.add(new MonthlyRevenue(YearMonth.of(y, m), total));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "getMonthlyRevenue error", ex);
            throw ex;
        } catch (Exception ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<Order> getOrdersByUser(int userID) {
        List<Order> list = new ArrayList<>();
        String sql
                = "SELECT orderID, userID, orderStatusID, orderDate, paymentMethodID, "
                + "voucherID, totalPrice, finalPrice, fullName, deliveryAddress, phone "
                + "FROM OrderInfo "
                + "WHERE userID = ? "
                + "ORDER BY orderDate DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setOrderID(rs.getInt("orderID"));
                    order.setUserID(rs.getInt("userID"));
                    order.setOrderStatusID(rs.getInt("orderStatusID"));
                    order.setOrderDate(rs.getDate("orderDate"));
                    order.setPaymentMethodID(rs.getInt("paymentMethodID"));
                    order.setVoucherID(rs.getInt("voucherID"));
                    order.setTotalPrice(rs.getFloat("totalPrice"));
                    order.setFinalPrice(rs.getFloat("finalPrice"));
                    order.setFullName(rs.getString("fullName"));
                    order.setDeliveryAddress(rs.getString("deliveryAddress"));
                    order.setPhone(rs.getString("phone"));
                    order.setOrderDetails(getOrderDetailsByOrder(order.getOrderID()));
                    list.add(order);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<StatusCount> getOrderCountByStatus(LocalDate startDate, LocalDate endDate) {
        Timestamp startTs = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTsExclusive = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());

        String sql
                = "SELECT os.statusName, oi.orderStatusID, COUNT(*) AS cnt\n"
                + "FROM OrderInfo oi\n"
                + "JOIN OrderStatus os ON oi.orderStatusID = os.orderStatusID\n"
                + "WHERE oi.orderDate >= ? AND oi.orderDate < ?\n"
                + "GROUP BY oi.orderStatusID, os.statusName\n"
                + "ORDER BY oi.orderStatusID;";

        List<StatusCount> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, startTs);
            ps.setTimestamp(2, endTsExclusive);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new StatusCount(rs.getInt("orderStatusID"),
                                             rs.getLong("cnt"),
                                             rs.getString("statusName")));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<OrderDetail> getOrderDetailsByOrder(int orderID) {
        List<OrderDetail> list = new ArrayList<>();
        String sql
                = "SELECT d.orderDetailID, d.productID, d.quantity, d.totalPrice, "
                + "p.productName, p.image "
                + "FROM OrderDetail d "
                + "JOIN Product p ON d.productID = p.productID "
                + "WHERE d.orderID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail od = new OrderDetail();
                    od.setOrderDetailID(rs.getInt("orderDetailID"));
                    od.setProductID(rs.getInt("productID"));
                    od.setQuantity(rs.getInt("quantity"));
                    od.setTotalPrice(rs.getInt("totalPrice"));
                    od.setOrderName(rs.getString("productName"));
                    od.setProductImage(rs.getString("image"));
                    list.add(od);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ==== Các hàm phục vụ flow trạng thái ====

    /** Pending/Paid -> Canceled + hoàn kho */
    public boolean cancelOrderAndRestock(int orderID) {
        final int CANCELED = 6;
        String sqlSelectStatus = "SELECT orderStatusID FROM OrderInfo WITH (UPDLOCK, ROWLOCK) WHERE orderID = ?";
        String sqlUpdateStatus = "UPDATE OrderInfo SET orderStatusID = ? WHERE orderID = ?";

        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int oldStatus;
            try (PreparedStatement psSel = conn.prepareStatement(sqlSelectStatus)) {
                psSel.setInt(1, orderID);
                rs = psSel.executeQuery();
                if (!rs.next()) { conn.rollback(); return false; }
                oldStatus = rs.getInt(1);
            } finally { if (rs != null) try { rs.close(); } catch (Exception ignore) {} }

            if (oldStatus == CANCELED) { conn.rollback(); return false; }

            try (PreparedStatement psUpd = conn.prepareStatement(sqlUpdateStatus)) {
                psUpd.setInt(1, CANCELED);
                psUpd.setInt(2, orderID);
                if (psUpd.executeUpdate() == 0) { conn.rollback(); return false; }
            }

            restockOrderItems(conn, orderID);

            conn.commit();
            return true;

        } catch (Exception ex) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignore) {}
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (Exception ignore) {}
        }
    }

    /** Processing -> Completed (khách trả hàng) + hoàn kho */
    public boolean completeReturnAndRestock(int orderID) {
        final int PROCESSING = 2;
        final int COMPLETED  = 5;

        String sqlSelectStatus = "SELECT orderStatusID FROM OrderInfo WITH (UPDLOCK, ROWLOCK) WHERE orderID = ?";
        String sqlUpdateStatus = "UPDATE OrderInfo SET orderStatusID = ? WHERE orderID = ?";

        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int oldStatus;
            try (PreparedStatement psSel = conn.prepareStatement(sqlSelectStatus)) {
                psSel.setInt(1, orderID);
                rs = psSel.executeQuery();
                if (!rs.next()) { conn.rollback(); return false; }
                oldStatus = rs.getInt(1);
            } finally { if (rs != null) try { rs.close(); } catch (Exception ignore) {} }

            if (oldStatus != PROCESSING) { conn.rollback(); return false; }

            try (PreparedStatement psUpd = conn.prepareStatement(sqlUpdateStatus)) {
                psUpd.setInt(1, COMPLETED);
                psUpd.setInt(2, orderID);
                if (psUpd.executeUpdate() == 0) { conn.rollback(); return false; }
            }

            restockOrderItems(conn, orderID);

            conn.commit();
            return true;

        } catch (Exception ex) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignore) {}
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (Exception ignore) {}
        }
    }

    /** Auto: Shipping(3) -> Delivered(4) sau N ngày; Delivered(4) -> Completed(5) sau N+M ngày (tính từ orderDate) */
    public int autoAdvanceByOrderDate(int shipDays, int deliveredDays) {
        int affected = 0;
        String sqlShip = "UPDATE OrderInfo SET orderStatusID = 4 " +
                         "WHERE orderStatusID = 3 AND DATEDIFF(day, orderDate, GETDATE()) >= ?";
        String sqlDone = "UPDATE OrderInfo SET orderStatusID = 5 " +
                         "WHERE orderStatusID = 4 AND DATEDIFF(day, orderDate, GETDATE()) >= ?";
        try (Connection c = DBConnection.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(sqlShip)) {
                ps.setInt(1, shipDays);
                affected += ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(sqlDone)) {
                ps.setInt(1, shipDays + deliveredDays);
                affected += ps.executeUpdate();
            }
        } catch (Exception ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "autoAdvanceByOrderDate error", ex);
        }
        return affected;
    }
}
