package odms.data;

import java.text.NumberFormat;
import java.util.Locale;
import odms.Session;
import org.apache.commons.lang3.LocaleUtils;

/**
 * Defines the locale settings for the application instance.
 */
public final class DefaultLocale {

    private static Locale datetimeLocale;
    private static Locale numberLocale;

    /**
     * Constructor - throws UnsupportedOperationException due to the static structure.
     */
    private DefaultLocale() {
        throw new UnsupportedOperationException();
    }

    private static Locale getDefaultLocale() {
        return LocaleUtils.toLocale(Session.getDefaultLocation());
    }

    public static Locale getDatetimeLocale() {
        if (datetimeLocale == null) {
            return getDefaultLocale();
        } else {
            return datetimeLocale;
        }
    }

    public static void setDatetimeLocale(Locale locale) {
        datetimeLocale = locale;
    }

    public static Locale getNumberLocale() {
        if (numberLocale == null) {
            return getDefaultLocale();
        } else {
            return numberLocale;
        }
    }

    /**
     * Returns the number formatted by the locale set by the user.
     * @param num the number to be formatted.
     * @return the number formatted.
     */
    public static String format(Number num) {
        NumberFormat formatter = NumberFormat.getNumberInstance(getNumberLocale());
        return formatter.format(num);
    }

    public static void setNumberLocale(Locale locale) {
        numberLocale = locale;
    }

}
