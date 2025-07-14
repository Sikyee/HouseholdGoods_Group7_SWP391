/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author thong
 */
public class Attribute {

    private int attributeID;
    private String attributeName;

    public String getAttributeValue() {
        return AttributeValue;
    }

    public void setAttributeValue(String AttributeValue) {
        this.AttributeValue = AttributeValue;
    }

    public Attribute(String AttributeValue) {
        this.AttributeValue = AttributeValue;
    }
    private int productID;
    private String AttributeValue;

    // getters and setters
    public int getAttributeID() {
        return attributeID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public Attribute(int productID) {
        this.productID = productID;
    }

    public void setAttributeID(int attributeID) {
        this.attributeID = attributeID;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public Attribute() {
    }

    public Attribute(int attributeID, String attributeName) {
        this.attributeID = attributeID;
        this.attributeName = attributeName;
    }
}
