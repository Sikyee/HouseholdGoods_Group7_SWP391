/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Model;

import java.util.Date;

/**
 *
 * @author Admin
 */

public class Order {
    
    private int orderID;
    private int userID;
    private int orderStatusID;
    private Date orderDate;
    private int paymentMethodID;
    private int voucherID;
    private float totalPrice;
    private float finalPrice;
    private String fullName;
    private String deliveryAddress;
    private String phone;

    public Order() {
    }

    public Order(int orderID, int userID, int orderStatusID, Date orderDate, int paymentMethodID, int voucherID, float totalPrice, float finalPrice, String fullName, String deliveryAddress, String phone) {
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

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public int getPaymentMethodID() {
        return paymentMethodID;
    }

    public void setPaymentMethodID(int paymentMethodID) {
        this.paymentMethodID = paymentMethodID;
    }

    public int getVoucherID() {
        return voucherID;
    }

    public void setVoucherID(int voucherID) {
        this.voucherID = voucherID;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public float getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(float finalPrice) {
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
