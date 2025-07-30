package Model;

import java.sql.Date;
import java.time.*;

public class Utils {
    public static String getStatus(Date endDate) {
        LocalDate now = LocalDate.now();
        LocalDate end = endDate.toLocalDate();
        if (now.isAfter(end)) return "Expired";
        Period period = Period.between(now, end);
        return period.getDays() + "d";
    }
}