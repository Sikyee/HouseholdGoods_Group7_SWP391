/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.Timestamp;
/**
 *
 * @author thach
 */


public class ReplyFeedback {
    private int replyID;
    private int feedbackID;
    private int userID;
    private String replyText;
    private Timestamp createdAt;

    public int getReplyID() { return replyID; }
    public void setReplyID(int replyID) { this.replyID = replyID; }

    public int getFeedbackID() { return feedbackID; }
    public void setFeedbackID(int feedbackID) { this.feedbackID = feedbackID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public String getReplyText() { return replyText; }
    public void setReplyText(String replyText) { this.replyText = replyText; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
