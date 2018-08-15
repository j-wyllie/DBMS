package odms.controller.database.procedure;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Procedure;
import odms.commons.model.profile.Profile;
import odms.controller.http.Request;
import odms.controller.http.Response;

public class HttpProcedureDAO implements ProcedureDAO {

    @Override
    public List<Procedure> getAll(Profile profile, Boolean pending) {
        String url = Request.getUrl() + String.format("profiles/%s/procedures", profile.getId());
        List<Procedure> procedures = new ArrayList<>();
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("pending", String.valueOf(pending));
        Request request = new Request(url, 0, queryParams);

        Response response = null;
        try {
            response = request.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        if (response.getStatus() == 200) {
            JsonArray results = parser.parse(response.getBody().toString()).getAsJsonArray();
            for (JsonElement result : results) {
                procedures.add(gson.fromJson(result, Procedure.class));
            }
        }

        return procedures;
    }

    @Override
    public void add(Profile profile, Procedure procedure) {
        Gson gson = new Gson();
        String url = Request.getUrl() + String.format("profiles/%s/procedures", profile.getId());
        String body = gson.toJson(procedure);
        Request request = new Request(url, 0, new HashMap<>(), body);
        try {
            request.post();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Procedure procedure) {
        Gson gson = new Gson();
        // profile id does not matter
        String url = Request.getUrl() + "procedures/" + procedure.getId();
        String body = gson.toJson(procedure);
        Request request = new Request(url, 0, new HashMap<>());
        try {
            request.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Profile profile, Procedure procedure) {
        Gson gson = new Gson();
        String url = Request.getUrl() + "procedures/" + profile.getId();
        String body = gson.toJson(procedure);
        Request request = new Request(url, 0, new HashMap<>(), body);
        try {
            request.patch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<OrganEnum> getAffectedOrgans(int procedureId) {
        List<OrganEnum> affectedOrgans = new ArrayList<OrganEnum>();
        String url = Request.getUrl() + "procedures/" + procedureId + "/organs";
        Request request = new Request(url, 0, new HashMap<>());
        Response response = null;
        try {
            response = request.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        if (response.getStatus() == 200) {
            JsonArray results = parser.parse(response.getBody().toString()).getAsJsonArray();
            for (JsonElement result : results) {
                affectedOrgans.add(gson.fromJson(result, OrganEnum.class));
            }
        }
        return affectedOrgans;
    }

    @Override
    public void addAffectedOrgan(Procedure procedure, OrganEnum organ) {
        Gson gson = new Gson();
        String url = Request.getUrl() + "procedures/" + procedure.getId() + "/organs";
        String body = gson.toJson(organ);
        Request request = new Request(url,0, new HashMap<>(), body);
        try {
            request.post();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeAffectedOrgan(Procedure procedure, OrganEnum organ) {
        String url = Request.getUrl() + "procedures/" + procedure.getId() + "/organs";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("name", organ);
        Request request = new Request(url, 0, queryParams);
        try {
            request.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
