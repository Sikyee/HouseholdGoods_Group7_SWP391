/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author Admin
 */
import java.time.YearMonth;

public class MonthlyRevenue {

    private YearMonth month;
    private double total;

    public MonthlyRevenue(YearMonth month, double total) {
        this.month = month;
        this.total = total;
    }

    public YearMonth getMonth() {
        return month;
    }

    public double getTotal() {
        return total;
    }
}
