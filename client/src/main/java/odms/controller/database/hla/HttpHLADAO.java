package odms.controller.database.hla;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.HLAType;
import odms.controller.http.Request;
import odms.controller.http.Response;

@Slf4j
public class HttpHLADAO implements HLADAO {

    @Override
    public HLAType get(Integer profileID) {
        String url = Request.getUrl() + "hla/" + profileID;
        Request request = new Request(url, 0, new HashMap<>());

        Response response = null;
        try {
            response = request.get();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        HLAType hlaType = new HLAType();
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        if (response != null && response.getStatus() == 200) {
            hlaType = gson.fromJson(response.getBody(), HLAType.class);
        }

        return hlaType;
    }

    @Override
    public void add(Integer profileID, HLAType hlaType) {
        String url = Request.getUrl() + "hla/" + profileID;

        Gson gson = new Gson();
        String body = gson.toJson(hlaType);
        Request request = new Request(url, 0, new HashMap<>(), body);

        try {
            Response response = request.post();
            System.out.print(response.getStatus());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void edit(Integer profileID, HLAType hlaType) {
        String url = Request.getUrl() + "hla/" + profileID;
    }

    @Override
    public void delete(Integer profileID) {
        String url = Request.getUrl() + "hla/" + profileID;
    }

    public static void main(String args[]) {
        HttpHLADAO httpHLADAO = new HttpHLADAO();

        HLAType hlaType = new HLAType(0, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144);
        httpHLADAO.add(69, hlaType);
    }
}
