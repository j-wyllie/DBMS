package odms.data;

public class Locale {

    private static Locale locale;

    public Locale() {
        throw new UnsupportedOperationException();
    }

    public void setLocale(Locale currentLocale) { this.locale = new Locale(); }

    public Locale getLocale() { return this.locale; }
}
