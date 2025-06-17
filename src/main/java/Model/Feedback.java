/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author thach
 */


public class Feedback {
    private int id;
    private int customerId;
    private String content;
    private String reply; // Có thể null
    private String createdAt;

    // Constructor
    public Feedback() {}

    public Feedback(int id, int customerId, String content, String reply, String createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.content = content;
        this.reply = reply;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
