package odms.data;

import java.util.Locale;

public class DefaultLocale {

    private static Locale locale = new Locale("en", "US");

    private DefaultLocale() {
        throw new UnsupportedOperationException();
    }

    public static void setLocale(Locale currentLocale) { locale = currentLocale; }

    public static Locale getLocale() { return locale; }
}
