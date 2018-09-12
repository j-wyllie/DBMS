package odms.data;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Defines the locale settings for the application instance.
 */
public final class DefaultLocale {

    private static Locale languageLocale;
    private static Locale datetimeLocale;
    private static Locale numberLocale;
    private static TimeZone timeZoneLocale;


    /**
     * Constructor - throws UnsupportedOperationException due to the static structure.
     */
    private DefaultLocale() {
        throw new UnsupportedOperationException();
    }

    private static Locale getDefaultLocale() {
        return new Locale("en", "US");
    }

    private static TimeZone getDefaultTimeZone() {
        return null;
    }

    public static void setLanguageLocale(Locale locale) {
        languageLocale = locale;
    }

    public static Locale getLanguageLocale() {
        if (languageLocale == null) {
            return getDefaultLocale();
        } else {
            return languageLocale;
        }
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

    public static TimeZone getTimeZoneLocale() {
        if (timeZoneLocale == null) {
            return getDefaultTimeZone();
        } else {
            return timeZoneLocale;
        }
    }

    public static void setTimeZoneLocale(TimeZone locale) {
        timeZoneLocale = locale;
    }

}
