/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;
/**
 *
 * @author TriTM
 */

import java.sql.Date;

public class Voucher {
    private int voucherID;
    private String code;
    private String description;
    private String discountType; // "percentage" or "fixed"
    private int discountValue;
    private Date startDate;
    private Date endDate;
    private int minOrderValue;
    private int maxUsage;
    private int usedCount;
    private boolean isActive;

    // Getters and Setters
    public int getVoucherID() { return voucherID; }
    public void setVoucherID(int voucherID) { this.voucherID = voucherID; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public int getDiscountValue() { return discountValue; }
    public void setDiscountValue(int discountValue) { this.discountValue = discountValue; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public int getMinOrderValue() { return minOrderValue; }
    public void setMinOrderValue(int minOrderValue) { this.minOrderValue = minOrderValue; }

    public int getMaxUsage() { return maxUsage; }
    public void setMaxUsage(int maxUsage) { this.maxUsage = maxUsage; }

    public int getUsedCount() { return usedCount; }
    public void setUsedCount(int usedCount) { this.usedCount = usedCount; }

    public boolean isIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
}

