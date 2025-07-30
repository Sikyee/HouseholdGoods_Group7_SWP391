package Model;

public class Address {

    private int addressID;
    private int userID;
    private boolean isDefault;
    private String addressDetail;
    private String addressName;        // Tên gợi nhớ (Home, Office, etc.)
    private String recipientName;      // Tên người nhận
    private String phone;              // Số điện thoại
    private String province;           // Tỉnh/Thành phố
    private String district;           // Quận/Huyện
    private String ward;               // Phường/Xã
    private String specificAddress;    // Địa chỉ cụ thể
    private int displayOrder;          // Thứ tự hiển thị
    private boolean isActive;          // Trạng thái hoạt động
    private String addressType;        // Loại địa chỉ: HOME, OFFICE, OTHER

    // Constructors
    public Address() {
        this.isActive = true;
        this.addressType = "OTHER";
    }

    public Address(int userID, String addressDetail, String addressName,
            String recipientName, String phone, boolean isDefault) {
        this();
        this.userID = userID;
        this.addressDetail = addressDetail;
        this.addressName = addressName;
        this.recipientName = recipientName;
        this.phone = phone;
        this.isDefault = isDefault;
    }

    // Full constructor
    public Address(int addressID, int userID, boolean isDefault, String addressDetail,
            String addressName, String recipientName, String phone, String province,
            String district, String ward, String specificAddress,
            int displayOrder, String addressType) {
        this();
        this.addressID = addressID;
        this.userID = userID;
        this.isDefault = isDefault;
        this.addressDetail = addressDetail;
        this.addressName = addressName;
        this.recipientName = recipientName;
        this.phone = phone;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.specificAddress = specificAddress;
        this.displayOrder = displayOrder;
        this.addressType = addressType;
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

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getSpecificAddress() {
        return specificAddress;
    }

    public void setSpecificAddress(String specificAddress) {
        this.specificAddress = specificAddress;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    // Utility methods
    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();

        if (specificAddress != null && !specificAddress.isEmpty()) {
            fullAddress.append(specificAddress);
        }

        if (ward != null && !ward.isEmpty()) {
            if (fullAddress.length() > 0) {
                fullAddress.append(", ");
            }
            fullAddress.append(ward);
        }

        if (district != null && !district.isEmpty()) {
            if (fullAddress.length() > 0) {
                fullAddress.append(", ");
            }
            fullAddress.append(district);
        }

        if (province != null && !province.isEmpty()) {
            if (fullAddress.length() > 0) {
                fullAddress.append(", ");
            }
            fullAddress.append(province);
        }

        return fullAddress.toString();
    }

    public String getDisplayName() {
        if (addressName != null && !addressName.isEmpty()) {
            return addressName;
        }
        return "Address #" + addressID;
    }

    public String getFormattedPhone() {
        if (phone == null || phone.isEmpty()) {
            return "";
        }

        // Format phone number (example for Vietnamese phone numbers)
        String cleanPhone = phone.replaceAll("[^0-9+]", "");
        if (cleanPhone.startsWith("84") && cleanPhone.length() == 11) {
            return "+84 " + cleanPhone.substring(2, 5) + " "
                    + cleanPhone.substring(5, 8) + " " + cleanPhone.substring(8);
        } else if (cleanPhone.startsWith("0") && cleanPhone.length() == 10) {
            return cleanPhone.substring(0, 4) + " "
                    + cleanPhone.substring(4, 7) + " " + cleanPhone.substring(7);
        }

        return phone;
    }

    public boolean isValidForShipping() {
        return isActive
                && addressDetail != null && !addressDetail.trim().isEmpty()
                && recipientName != null && !recipientName.trim().isEmpty()
                && phone != null && !phone.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Address{"
                + "addressID=" + addressID
                + ", userID=" + userID
                + ", addressName='" + addressName + '\''
                + ", recipientName='" + recipientName + '\''
                + ", phone='" + phone + '\''
                + ", isDefault=" + isDefault
                + ", addressType='" + addressType + '\''
                + ", isActive=" + isActive
                + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Address address = (Address) obj;
        return addressID == address.addressID;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(addressID);
    }
}
