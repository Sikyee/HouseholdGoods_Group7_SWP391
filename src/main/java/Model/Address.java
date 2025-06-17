package Model;

public class Address {
    private int addressID;
    private int customerID;
    private boolean isDefault;
    private String addressDetail;

    public Address() {}

    public Address(int addressID, int customerID, boolean isDefault, String addressDetail) {
        this.addressID = addressID;
        this.customerID = customerID;
        this.isDefault = isDefault;
        this.addressDetail = addressDetail;
    }
    
    // Getters and Setters
    
    public int getAddressID() {
        return addressID;
    }

    public void setAddressID(int addressID) {
        this.addressID = addressID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }
}
