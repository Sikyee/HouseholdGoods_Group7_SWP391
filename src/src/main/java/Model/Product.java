/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.List;

/**
 *
 * @author thong
 */
public class Product {

    private int productID;
    private String productName;
    private String description;
    private int subCategory;
    private long price;
    private int stonkQuantity;
    private int brandID;
    private String image;
    private List<Attribute> attributes;

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Product(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Product(int productID, String productName, String description, int subCategory, long price, int stonkQuantity, int brandID, String image) {
        this.productID = productID;
        this.productName = productName;
        this.description = description;
        this.subCategory = subCategory;
        this.price = price;
        this.stonkQuantity = stonkQuantity;
        this.brandID = brandID;
        this.image = image;
    }

    public Product() {
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(int subCategory) {
        this.subCategory = subCategory;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getStonkQuantity() {
        return stonkQuantity;
    }

    public void setStonkQuantity(int stonkQuantity) {
        this.stonkQuantity = stonkQuantity;
    }

    public int getBrandID() {
        return brandID;
    }

    public void setBrandID(int brandID) {
        this.brandID = brandID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
