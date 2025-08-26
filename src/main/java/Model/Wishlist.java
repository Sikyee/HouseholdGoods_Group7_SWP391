/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.Date;

/**
 *
 * @author you
 */
public class Wishlist {
    private int wishlistID;
    private int userID;
    private int productID;
    private Date dateAdded;

    // mapping sang Product để hiển thị chi tiết (nếu cần)
    private Product product;

    public Wishlist() {
    }

    public Wishlist(int wishlistID, int userID, int productID, Date dateAdded) {
        this.wishlistID = wishlistID;
        this.userID = userID;
        this.productID = productID;
        this.dateAdded = dateAdded;
    }

    public int getWishlistID() {
        return wishlistID;
    }

    public void setWishlistID(int wishlistID) {
        this.wishlistID = wishlistID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
