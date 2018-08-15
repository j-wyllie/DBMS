package odms.controller.database.country;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.enums.OrganEnum;
import odms.controller.http.Request;
import odms.controller.http.Response;
import org.sonar.api.internal.google.gson.Gson;
import org.sonar.api.internal.google.gson.JsonArray;
import org.sonar.api.internal.google.gson.JsonElement;
import org.sonar.api.internal.google.gson.JsonParser;

public class HttpCountryDAO implements CountryDAO {

    @Override
    public List<String> getAll() {
        String url = String.format("http://localhost:6969/api/v1/countries");
        return getListRequest(url, new HashMap<>());
    }

    @Override
    public List<String> getAll(boolean valid) {
        String url = String.format("http://localhost:6969/api/v1/countries");
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("valid", valid);

        return getListRequest(url, queryParams);
    }

    @Override
    public void update(CountriesEnum country, boolean valid) {
        String url = String.format("http://localhost:6969/api/v1/countries");
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("country", country.getName());
        queryParams.put("valid", valid);

        Request request = new Request(url, 0, queryParams);
        try {
            request.patch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getListRequest(String url, Map<String, Object> queryParams) {
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        Response response = null;
        Request request = new Request(url, 0, queryParams);
        try {
            response = request.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> countries = new ArrayList<>();
        if (response.getStatus() == 200) {
            JsonArray results = parser.parse(response.getBody().toString()).getAsJsonArray();
            for (JsonElement result : results) {
                countries.add(result.getAsString());
            }
        }
        return countries;
    }
}
