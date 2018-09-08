package odms.controller;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
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
    public Map<String, String> getLanguageOptions() {
        Map<String, String> availableLanguages = new HashMap<>();
        List<Locale> numberLocales = Arrays.asList(NumberFormat.getAvailableLocales());
        List<Locale> dateLocales = Arrays.asList(DateFormat.getAvailableLocales());

        for (Locale locale : numberLocales) {
            if (dateLocales.contains(locale) && locale != null) {
                String display = locale.getDisplayLanguage();
                if (locale.getDisplayCountry() != "") {
                    display += ", " + locale.getDisplayCountry();
                }
                String value = locale.toString();
                if (display != "") {
                    availableLanguages.put(display, value);
                }
            }
        }
        return availableLanguages;
    }


    /**
     * Gives a list of time zones for the user to select from.
     * @return a list of available time zones.
     */
    public List<String> getTimeZoneOptions() {
        List<String> timezones = new ArrayList<>();
        for (String id : TimeZone.getAvailableIDs()) {
            timezones.add(formatTimeZone(TimeZone.getTimeZone(id)));
        }
        timezones.sort(String::compareToIgnoreCase);
        return timezones;
    }

    /**
     * Formats a time zone from the timezone id in the format (GMT+ X:XX) Country/City
     * @param tz timezone id.
     * @return formatted string value.
     */
    private static String formatTimeZone(TimeZone tz) {

        long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset())
                - TimeUnit.HOURS.toMinutes(hours);
        // avoid -4:-30 issue
        minutes = Math.abs(minutes);

        String result;
        if (hours > 0) {
            result = String.format("(GMT+%d:%02d) %s", hours, minutes, tz.getID());
        } else {
            result = String.format("(GMT%d:%02d) %s", hours, minutes, tz.getID());
        }

        return result;

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
