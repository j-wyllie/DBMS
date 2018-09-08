package odms.controller;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
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
    public Map<String, String> getLanguageOptions() {
        Map<String, String> availableLanguages = new HashMap<>();
        List<Locale> numberLocales = Arrays.asList(NumberFormat.getAvailableLocales());
        List<Locale> dateLocales = Arrays.asList(DateFormat.getAvailableLocales());
        StringBuilder builder = new StringBuilder();

        for (Locale locale : numberLocales) {
            if (dateLocales.contains(locale) && locale != null) {

                builder.append(locale.getDisplayLanguage());
                if (locale.getDisplayCountry() != "") {
                    builder.append(String.format(", %s", locale.getDisplayCountry()));

                }
                String value = locale.toString();
                if (builder.toString() != "") {
                    availableLanguages.put(builder.toString(), value);
                }
            }
        }
        return availableLanguages;
    }


    /**
     * Gives a list of time zones for the user to select from.     *
     * @return a list of available time zones.
     */
    public Map<String, TimeZone> getTimeZoneOptions() {
        List<TimeZone> timezones = new ArrayList<>();
        Map<String, TimeZone> formattedTimeZones = new HashMap<>();

        for (String id : TimeZone.getAvailableIDs()) {
            timezones.add(TimeZone.getTimeZone(id));
        }
        timezones.sort(Comparator.comparingInt(TimeZone::getRawOffset));

        for (TimeZone tz : timezones) {
            formattedTimeZones.put(formatTimeZone(tz), tz);
        }
        return formattedTimeZones;
    }

    /**
     * Formats a time zone from the timezone id in the format (GMT+ X:XX) Country/City.     *
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
}
