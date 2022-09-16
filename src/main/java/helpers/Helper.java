package helpers;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {

    public static long dayMSLONG = 86400000;

    public static boolean isAlphanumeric(String string) {
        return string.matches("^[a-zA-Z0-9]+$");
    }

    public static boolean isMcPseudo(String string) {
        return string.matches("^[a-zA-Z0-9_-]+{2,16}");
    }

    public static boolean isNumeric(String string) {
        return string.matches("^[0-9]+$");
    }

    public static Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static Timestamp convertStringToTimestamp(String strDate) {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            // you can change format of date
            Date date = formatter.parse(strDate);
            Timestamp timeStampDate = new Timestamp(date.getTime());

            return timeStampDate;
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }

    public static boolean isWithin24Hour(Timestamp comparator) {
        final long now = getTimestamp().getTime();
        final long end = comparator.getTime() + dayMSLONG ;
        return end - now > 0;
    }

}
