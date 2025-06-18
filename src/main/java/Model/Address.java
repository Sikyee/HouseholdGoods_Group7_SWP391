package Model;

public class Address {

    private int addressID;
    private int userID;
    private boolean isDefault;
    private String addressDetail;

    public Address() {
    }

    public Address(int addressID, int userID, boolean isDefault, String addressDetail) {
        this.addressID = addressID;
        this.userID = userID;
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

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
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
