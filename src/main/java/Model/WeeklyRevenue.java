// Model/dto/WeeklyRevenue.java
package Model.dto;

import java.time.LocalDate;

public class WeeklyRevenue {
    private final LocalDate date; // ngày thật
    private final double total;   // tổng tiền

    public WeeklyRevenue(LocalDate date, double total) {
        this.date = date;
        this.total = total;
    }
    public LocalDate getDate() { return date; }
    public double getTotal() { return total; }
}
