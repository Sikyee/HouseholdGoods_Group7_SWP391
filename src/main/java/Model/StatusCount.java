/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author Admin
 */
public class StatusCount {

    private int statusId;
    private long count;
    private String statusName;

    public StatusCount(int statusId, long count, String statusName) {
        this.statusId = statusId;
        this.count = count;
        this.statusName = statusName;
    }

    public int getStatusId() {
        return statusId;
    }

    public long getCount() {
        return count;
    }

    public String getStatusName() {
        return statusName;
    }
    
    
}
