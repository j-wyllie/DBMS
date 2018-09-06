package odms.data;

public class Locale {

    private static Locale locale;

    public Locale() {
        throw new UnsupportedOperationException();
    }

    public static void setLocale(Locale currentLocale) { locale = currentLocale; }

    public Locale getLocale() { return locale; }
}
