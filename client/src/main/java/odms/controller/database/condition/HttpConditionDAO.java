package odms.controller.database.condition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.Condition;
import odms.commons.model.profile.Profile;
import odms.controller.http.Request;
import odms.controller.http.Response;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Slf4j
public class HttpConditionDAO implements ConditionDAO {

    @Override
    public List<Condition> getAll(Profile profile, boolean chronic) {
        String url = String.format("http://csse-s302g2:8080/api/v1/profiles/%s/conditions", profile.getId());
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("chronic", chronic);
        return getArrayRequest(url, queryParams);
    }

    @Override
    public void add(Profile profile, Condition condition) {
        Gson gson = new Gson();
        String url = String.format("http://csse-s302g2:8080/api/v1/profiles/%s/conditions", profile.getId());
        Map<String, Object> queryParams = new HashMap<>();

        String body = gson.toJson(condition);
        Request request = new Request(url, queryParams, body);
        try {
            request.post();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void remove(Condition condition) {
        String url = String.format("http://csse-s302g2:8080/api/v1/conditions/%s", condition.getId());
        Map<String, Object> queryParams = new HashMap<>();
        Request request = new Request(url, queryParams);
        try {
            request.delete();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void update(Condition condition) {
        Gson gson = new Gson();
        String url = String.format("http://csse-s302g2:8080/api/v1/conditions/%s", condition.getId());

        String body = gson.toJson(condition);
        Request request = new Request(url, new HashMap<>(), body);
        try {
            request.patch();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<Condition> getArrayRequest(String url, Map<String, Object> queryParams) {
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        Response response = null;
        Request request = new Request(url, queryParams);
        try {
            response = request.get();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        List<Condition> conditions = new ArrayList<>();
        if (response != null && response.getStatus() == 200) {
            JsonArray results = parser.parse(response.getBody()).getAsJsonArray();
            for (JsonElement result : results) {
                conditions.add(gson.fromJson(result, Condition.class));
            }
        }
        return conditions;
    }
}
