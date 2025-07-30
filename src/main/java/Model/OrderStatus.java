package Model;

public class OrderStatus {
    private int orderStatusID;
    private String statusName;

    public OrderStatus() {
    }

    public OrderStatus(int orderStatusID, String statusName) {
        this.orderStatusID = orderStatusID;
        this.statusName = statusName;
    }

    public int getOrderStatusID() {
        return orderStatusID;
    }

    public void setOrderStatusID(int orderStatusID) {
        this.orderStatusID = orderStatusID;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
