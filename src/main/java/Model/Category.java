/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Model;

/**
 *
 * @author Admin
 */
public class Category {
    
    private int categoryID;
    private int subCategoryID;
    private String categoryName;
    private String subCategoryName;

    public Category() {
    }

    public Category(int categoryID, int subCategoryID, String categoryName, String subCategoryName) {
        this.categoryID = categoryID;
        this.subCategoryID = subCategoryID;
        this.categoryName = categoryName;
        this.subCategoryName = subCategoryName;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public int getSubCategoryID() {
        return subCategoryID;
    }

    public void setSubCategoryID(int categoryID) {
        this.subCategoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

}
