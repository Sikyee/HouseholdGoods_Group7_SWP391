/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author pts03
 */
public class Inventory {
    private int productID;
    private String productName;
    private int quantity;
    private int status; // 1 = active, 0 = inactive
    private String brandName;

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Inventory(int productID, String productName, int quantity, int status, String brandName) {
        this.productID = productID;
        this.productName = productName;
        this.quantity = quantity;
        this.status = status;
        this.brandName = brandName;
    }

    public Inventory() {}

    public Inventory(int productID, String productName, int quantity, int status) {
        this.productID = productID;
        this.productName = productName;
        this.quantity = quantity;
        this.status = status;
    }

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}
