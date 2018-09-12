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

    private static Locale getDefaultLanguage() { return new Locale("en", "US"); }
    private static Locale getDefaultDatetime() { return new Locale("en", "US"); }
    private static Locale getDefaultNumber() { return new Locale("en", "US"); }
    private static TimeZone getDefaultTimeZone() { return null; }


    public static void setLanguageLocale(Locale locale) { languageLocale = locale; }

    public static Locale getLanguageLocale() {
        if (languageLocale == null) {
            return getDefaultLanguage();
        } else {
            return languageLocale;
        }
    }

    public static Locale getDatetimeLocale() {
        if (datetimeLocale == null) {
            return getDefaultDatetime();
        } else {
            return datetimeLocale;
        }
    }

    public static void setDatetimeLocale(Locale locale) { datetimeLocale = locale; }

    public static Locale getNumberLocale() {
        if (numberLocale == null) {
            return getDefaultNumber();
        } else {
            return numberLocale;
        }
    }

    public static String format(Number num) {
        NumberFormat formatter = NumberFormat.getNumberInstance(getNumberLocale());
        return formatter.format(num);
    }

    public static void setNumberLocale(Locale locale) { numberLocale = locale; }

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
