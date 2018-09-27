package odms.controller.database.country;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.enums.OrganEnum;
import odms.controller.http.Request;
import odms.controller.http.Response;
import org.sonar.api.internal.google.gson.Gson;
import org.sonar.api.internal.google.gson.JsonArray;
import org.sonar.api.internal.google.gson.JsonElement;
import org.sonar.api.internal.google.gson.JsonParser;

@Slf4j
public class HttpCountryDAO implements CountryDAO {

    private static final String COUNTRIES_URL = "http://localhost:6969/api/v1/countries";

    @Override
    public List<String> getAll() {
        return getListRequest(new HashMap<>());
    }

    @Override
    public List<String> getAll(boolean valid) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("valid", valid);

        return getListRequest(queryParams);
    }

    @Override
    public void update(CountriesEnum country, boolean valid) {
        Gson gson = new Gson();
        Map<String, Object> body = new HashMap<>();
        body.put("name", country);
        body.put("valid", valid);

        String responseBody = gson.toJson(body);

        Request request = new Request(COUNTRIES_URL, new HashMap<>(), responseBody);
        try {
            request.patch();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> getListRequest(Map<String, Object> queryParams) {
        JsonParser parser = new JsonParser();
        Response response = null;
        Request request = new Request(COUNTRIES_URL,  queryParams);
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
}
