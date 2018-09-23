package odms.controller.database.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import odms.Session;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.enums.UserType;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.controller.http.Request;
import odms.controller.http.Response;
import odms.data.DefaultLocale;
import org.apache.commons.lang3.LocaleUtils;
import org.sonar.api.internal.google.gson.Gson;
import org.sonar.api.internal.google.gson.JsonArray;
import org.sonar.api.internal.google.gson.JsonElement;
import org.sonar.api.internal.google.gson.JsonObject;
import org.sonar.api.internal.google.gson.JsonParser;

@Slf4j
public class HttpSettingsDAO implements SettingsDAO {

    private static final String COUNTRIES_URL = "http://localhost:6969/api/v1/settings/countries";
    private static final String LOCALE_URL = "http://localhost:6969/api/v1/settings/locale";

    /**
     * Gets all countries from the database.
     * @return a list of countries.
     */
    @Override
    public List<String> getAllCountries() {
        return getListRequest(new HashMap<>());
    }

    /**
     * Gets all valid or invalid countries from the database.
     * @param valid true if valid countries are required.
     * @return a list of countries.
     */
    @Override
    public List<String> getAllCountries(boolean valid) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("valid", valid);

        return getListRequest(queryParams);
    }

    /**
     * Updates a settings to valid or invalid.
     * @param country to updateCountry.
     * @param valid state to updateCountry.
     */
    @Override
    public void updateCountry(CountriesEnum country, boolean valid) {
        Gson gson = new Gson();
        Map<String, Object> body = new HashMap<>();
        body.put("name", country);
        body.put("valid", valid);

        String responseBody = gson.toJson(body);

        Request request = new Request(COUNTRIES_URL, 0, new HashMap<>(), responseBody);
        try {
            request.patch();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Executes an http request and converts the response
     * body into a list.
     * @param queryParams added to the request url.
     * @return the list converted from the response body.
     */
    private List<String> getListRequest(Map<String, Object> queryParams) {
        JsonParser parser = new JsonParser();
        Response response = null;
        Request request = new Request(COUNTRIES_URL, 0, queryParams);
        try {
            response = request.get();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        List<String> countries = new ArrayList<>();
        if (response != null && response.getStatus() == 200) {
            JsonArray results = parser.parse(response.getBody()).getAsJsonArray();
            for (JsonElement result : results) {
                countries.add(result.getAsString());
            }
        }
        return countries;
    }

    /**
     * Gets the current users locale setting from the server.
     */
    @Override
    public void getLocale() {
        JsonParser parser = new JsonParser();

        Request request = new Request(LOCALE_URL, 0, getLocaleQueryParams());
        Response response = null;
        try {
            response = request.get();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        if (response != null && response.getStatus() == 200) {
            JsonObject results = parser.parse(response.getBody()).getAsJsonObject();
            DefaultLocale.setDatetimeLocale(LocaleUtils.toLocale(results.get("DateTimeLocale").toString()));
            DefaultLocale.setNumberLocale(LocaleUtils.toLocale(results.get("NumberLocale").toString()));
        }
    }

    /**
     * Sets the locale setting for a user in the server.
     */
    @Override
    public void setLocale() {
        Locale dateTimeLocale = DefaultLocale.getDatetimeLocale();
        Locale numberLocale = DefaultLocale.getNumberLocale();

        Gson gson = new Gson();
        Map<String, Object> body = new HashMap<>();
        body.put("DateTimeLocale", dateTimeLocale);
        body.put("NumberLocale", numberLocale);

        String requestBody = gson.toJson(body);

        Request request = new Request(LOCALE_URL, 0, getLocaleQueryParams(), requestBody);
        try {
            request.post();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Gets the url query parameters to get and set locale.
     * @return the parameters.
     */
    private Map<String, Object> getLocaleQueryParams() {
        int id = 0;
        UserType userType = Session.getCurrentUser().getValue();
        if (userType == UserType.ADMIN || userType == UserType.CLINICIAN) {
            User user = (User) Session.getCurrentUser().getKey();
            id = user.getId();
        } else {
            Profile profile = (Profile) Session.getCurrentUser().getKey();
            id = profile.getId();
        }

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("id", id);
        queryParams.put("UserType", userType);
        return queryParams;
    }

}
