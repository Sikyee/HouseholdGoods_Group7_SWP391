package Model;

public class OrderDetail {
    private int orderDetailID;
    private int productID;
    private int orderID;
    private String orderName;
    private int quantity;
    private long totalPrice;
    private String productImage;

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public OrderDetail() {}

    public OrderDetail(int orderDetailID, int productID, int orderID, String orderName, int quantity, long totalPrice) {
        this.orderDetailID = orderDetailID;
        this.productID = productID;
        this.orderID = orderID;
        this.orderName = orderName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    
    public int getOrderDetailID() {
        return orderDetailID;
    }

    public void setOrderDetailID(int orderDetailID) {
        this.orderDetailID = orderDetailID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }
}
