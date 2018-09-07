package odms.controller;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import odms.controller.database.DAOFactory;
import odms.controller.database.country.CountryDAO;
import odms.view.SettingsPopup;

public class SettingsPopupController {

    private SettingsPopup view;

    public SettingsPopupController(SettingsPopup view) { this.view = view; }

    /**
     * Gets a list of available languages the user can select.
     * @return a list of languages.
     */
    public List<Entry<String, String>> getLanguageOptions() {
        List<Entry<String, String>> availableLanguages = new ArrayList<>();
        List<Locale> numberLocales = Arrays.asList(NumberFormat.getAvailableLocales());
        List<Locale> dateLocales = Arrays.asList(DateFormat.getAvailableLocales());

        for (Locale locale : numberLocales) {
            if (dateLocales.contains(locale)) {
                availableLanguages.add(new SimpleEntry<>(locale.getDisplayLanguage(), locale.getLanguage()));
            }
        }
        return availableLanguages;
    }

    /**
     * Gives a list of time zones for the user to select from.
     * @return a list of available time zones.
     */
    public List<String> getTimeZoneOptions() {
        return new ArrayList<>();
    }

    /**
     * Gives a list of date time formats for the user to select from.
     * @return a list of available date time formats.
     */
    public List<String> getDateTimeFormatOptions() {
        // todo datetime format options.
        return new ArrayList<>();
    }

    /**
     * Gives a list of number formats for the user to select from.
     * @return a list of available number formats.
     */
    public List<String> getNumberFormatOptions() {
        // todo number format options.
        return new ArrayList<>();
    }
}
