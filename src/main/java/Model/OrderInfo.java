package Model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class OrderInfo {

    private int orderID;
    private int userID;
    private int orderStatusID;
    private Timestamp orderDate;
    private int paymentMethodID;
    private Integer voucherID;
    private BigDecimal totalPrice;
    private BigDecimal finalPrice;
    private String fullName;
    private String deliveryAddress;
    private String phone;


    public OrderInfo() {
    }

    public OrderInfo(int orderID, int userID, int orderStatusID, Timestamp orderDate,
            int paymentMethodID, Integer voucherID, BigDecimal totalPrice,
            BigDecimal finalPrice, String fullName, String deliveryAddress,
            String phone) {
        this.orderID = orderID;
        this.userID = userID;
        this.orderStatusID = orderStatusID;
        this.orderDate = orderDate;
        this.paymentMethodID = paymentMethodID;
        this.voucherID = voucherID;
        this.totalPrice = totalPrice;
        this.finalPrice = finalPrice;
        this.fullName = fullName;
        this.deliveryAddress = deliveryAddress;
        this.phone = phone;
    }

    // Getter & Setter đầy đủ
    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getOrderStatusID() {
        return orderStatusID;
    }

    public void setOrderStatusID(int orderStatusID) {
        this.orderStatusID = orderStatusID;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public int getPaymentMethodID() {
        return paymentMethodID;
    }

    public void setPaymentMethodID(int paymentMethodID) {
        this.paymentMethodID = paymentMethodID;
    }

    public Integer getVoucherID() {
        return voucherID;
    }

    public void setVoucherID(Integer voucherID) {
        this.voucherID = voucherID;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
