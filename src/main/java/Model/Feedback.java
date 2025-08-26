/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author TriTN
 */
package Model;

import java.sql.Timestamp;

public class Feedback {
    private int feedbackID;
    private int orderDetailID;
    private int rating;
    private String comment;
    private boolean isDeleted;
    private Timestamp createdAt;
    private String status;
    private int userID;
    private String userName; // từ bảng Customer
    private String productName;
        private String image;
 
    




    public int getFeedbackID() { return feedbackID; }
    public void setFeedbackID(int feedbackID) { this.feedbackID = feedbackID; }

    public int getOrderDetailID() { return orderDetailID; }
    public void setOrderDetailID(int orderDetailID) { this.orderDetailID = orderDetailID; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
   
   
    public int getUserID() {return userID;}
    public void setUserID(int userID) {this.userID = userID;}

    public String getUserName() {return userName;}
    public void setUserName(String userName) { this.userName = userName;}
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
  

}
