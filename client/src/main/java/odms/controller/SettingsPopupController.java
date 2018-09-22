package odms.controller;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import odms.data.DefaultLocale;
import odms.view.SettingsPopup;

public class SettingsPopupController {

    private SettingsPopup view;

    public SettingsPopupController(SettingsPopup view) {
        this.view = view;
    }

    /**
     * Gets a list of available languages the user can select.
     * @return a list of languages.
     */
    public Map<String, Locale> getLocaleOptions() {
        Map<String, Locale> availableLanguages = new HashMap<>();
        List<Locale> numberLocales = Arrays.asList(NumberFormat.getAvailableLocales());
        List<Locale> dateLocales = Arrays.asList(DateFormat.getAvailableLocales());

        for (Locale locale : numberLocales) {
            StringBuilder builder = new StringBuilder();

            if (dateLocales.contains(locale) && locale != null) {

                builder.append(locale.getDisplayLanguage());
                if (!locale.getDisplayCountry().equalsIgnoreCase("")) {
                    builder.append(String.format(", %s", locale.getDisplayCountry()));

                }
                if (!builder.toString().trim().equalsIgnoreCase("")) {
                    availableLanguages.put(builder.toString(), locale);
                }
            }
        }
        return availableLanguages;
    }

    /**
     * Updates the users default locale settings.
     */
    public void updateLocales() {
        Map<String, Locale> languages = getLocaleOptions();
        Locale value;

        value = languages.get(view.getDatetimeSelector().getValue().toString());
        DefaultLocale.setDatetimeLocale(value);

        value = languages.get(view.getNumberSelector().getValue().toString());
        DefaultLocale.setNumberLocale(value);
    }
}
