/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author thach
 */

public class Wishlist {
    private int wishlistID;
    private int userID;
    private int productID;
    private int quantity;
    private Product product; // để join hiển thị chi tiết sản phẩm

    // Getters & Setters
    public int getWishlistID() { return wishlistID; }
    public void setWishlistID(int wishlistID) { this.wishlistID = wishlistID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
