package Model;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
import java.util.Objects;
import java.text.SimpleDateFormat;

/**
 * Utils for date formatting & voucher status.
 */
public class Utils {

    /* ===================== STATUS ===================== */

    /** Status cho java.sql.Date */
    public static String getStatus(Date endDate) {
        if (endDate == null) return "";
        return getStatus(endDate.toLocalDate());
    }

    /** Status cho LocalDate (core) */
    public static String getStatus(LocalDate end) {
        if (end == null) return "";
        LocalDate today = LocalDate.now();

        if (today.isAfter(end)) return "Expired";
        if (today.isEqual(end)) return "Today";

        long days = ChronoUnit.DAYS.between(today, end);
        if (days < 30) {
            return days + "d";                // dưới 30 ngày
        } else if (days < 365) {
            long months = days / 30;          // xấp xỉ theo 30 ngày/tháng
            return months + "m";
        } else {
            long years = days / 365;          // xấp xỉ theo 365 ngày/năm
            return years + "y";
        }
    }

    /* ===================== DATE FORMATTING ===================== */

    // SimpleDateFormat là non-threadsafe, nhưng dùng theo-lần-gọi trong method (OK).
    private static final String HTML_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DISPLAY_DATE_PATTERN = "dd/MM/yyyy";

    /** yyyy-MM-dd cho java.util.Date */
    public static String toHtmlDate(java.util.Date d) {
        if (d == null) return "";
        return new SimpleDateFormat(HTML_DATE_PATTERN).format(d);
    }

    /** yyyy-MM-dd cho java.sql.Date */
    public static String toHtmlDate(Date d) {
        if (d == null) return "";
        return new SimpleDateFormat(HTML_DATE_PATTERN).format(d);
    }

    /** dd/MM/yyyy cho java.util.Date */
    public static String toDisplayDate(java.util.Date d) {
        if (d == null) return "";
        return new SimpleDateFormat(DISPLAY_DATE_PATTERN).format(d);
    }

    /** dd/MM/yyyy cho java.sql.Date */
    public static String toDisplayDate(Date d) {
        if (d == null) return "";
        return new SimpleDateFormat(DISPLAY_DATE_PATTERN).format(d);
    }

    /* ===================== (Optional) CONVENIENCE ===================== */

    /** Chuyển java.util.Date -> LocalDate theo system default zone */
    public static LocalDate toLocalDate(java.util.Date d) {
        if (d == null) return null;
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
