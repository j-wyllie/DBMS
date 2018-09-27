package odms.controller.database.medication;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.medications.Drug;
import odms.commons.model.profile.Profile;
import odms.controller.http.Request;
import odms.controller.http.Response;

@Slf4j
public class HttpMedicationDAO implements MedicationDAO {

    @Override
    public List<Drug> getAll(Profile profile, Boolean current) {
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/drugs", profile.getId());
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("current", current);
        return getArrayRequest(url, queryParams);
    }

    @Override
    public void add(Drug drug, Profile profile, Boolean current) {
        Gson gson = new Gson();
        String url = String.format("http://localhost:6969/api/v1/profiles/%s/drugs", profile.getId());
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("current", current);

        String body = gson.toJson(drug);
        Request request = new Request(url, queryParams, body);
        try {
            request.post();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void remove(Drug drug) {
        String url = String.format("http://localhost:6969/api/v1/drugs/%s", drug.getId());
        Map<String, Object> queryParams = new HashMap<>();
        Request request = new Request(url, queryParams);
        try {
            request.delete();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void update(Drug drug, Boolean current) {
        Gson gson = new Gson();
        String url = String.format("http://localhost:6969/api/v1/drugs/%s", drug.getId());
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("current", current);

        String body = gson.toJson(drug);
        Request request = new Request(url, queryParams, body);
        try {
            request.patch();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<Drug> getArrayRequest(String url, Map<String, Object> queryParams) {
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        Response response = null;
        Request request = new Request(url, queryParams);
        try {
            response = request.get();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        List<Drug> drugs = new ArrayList<>();
        if (response != null && response.getStatus() == 200) {
            JsonArray results = parser.parse(response.getBody()).getAsJsonArray();
            for (JsonElement result : results) {
                drugs.add(gson.fromJson(result, Drug.class));
            }
        }
        return drugs;
    }
}
