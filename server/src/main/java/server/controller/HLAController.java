package server.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.HLAType;
import org.sonar.api.internal.google.gson.Gson;
import server.model.database.DAOFactory;
import server.model.database.hla.HLA;
import server.model.database.hla.HLADAO;
import server.model.enums.DataTypeEnum;
import spark.Request;
import spark.Response;

@Slf4j
public class HLAController {

    /**
     * Prevent instantiation of static class.
     */
    private HLAController() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets a HLAType for a given profile.
     *
     * @param req the get request object with no body.
     * @param res the response object with HLAType as body.
     * @return the response body, a HLAType.
     */
    public static String get(Request req, Response res) {
        Gson gson = new Gson();
        HLADAO database = DAOFactory.getHLADAO();
        Integer profileID = Integer.valueOf(req.params("id"));

        HLAType hlaType = new HLAType();

        try {
            List<HLA> hlaGroupX = database.getGroupX(profileID);
            List<HLA> hlaGgroupY = database.getGroupY(profileID);
            List<HLA> hlaSecondary = database.getNonPrimary(profileID);

            Map<String, Integer> hlaTypeGroupX = new HashMap<>();
            for (HLA hla : hlaGroupX) {
                hlaTypeGroupX.put(hla.getAlphaValue(), hla.getNumericValue());
            }
            hlaType.setGroupX(hlaTypeGroupX);

            Map<String, Integer> hlaTypeGroupY = new HashMap<>();
            for (HLA hla : hlaGgroupY) {
                hlaTypeGroupY.put(hla.getAlphaValue(), hla.getNumericValue());
            }
            hlaType.setGroupY(hlaTypeGroupY);

            Map<String, Integer> hlaTypeSecondary = new HashMap<>();
            for (HLA hla : hlaSecondary) {
                hlaTypeSecondary.put(hla.getAlphaValue(), hla.getNumericValue());
            }
            hlaType.setSecondaryAntigens(hlaTypeSecondary);
        } catch (Exception e) {
            res.status(500);
            return e.getMessage();
        }

        String responseBody = gson.toJson(hlaType);

        res.type(DataTypeEnum.JSON.toString());
        res.status(200);
        return responseBody;
    }

    /**
     * Adds a HLAType to the database.
     *
     * @param req the request object with HLAType as body.
     * @param res the empty response object.
     * @return the response body.
     */
    public static String add(Request req, Response res) {
        Gson gson = new Gson();
        HLADAO database = DAOFactory.getHLADAO();
        Integer profileID = Integer.valueOf(req.params("id"));

        HLAType hlaType = gson.fromJson(req.body(), HLAType.class);

        try {
            Map<String, Integer> hlaTypeX = hlaType.getGroupX();
            for (String key : hlaTypeX.keySet()) {
                HLA hla = new HLA(key, hlaTypeX.get(key), true, false);
                database.add(profileID, hla);
            }

            Map<String, Integer> hlaTypeY = hlaType.getGroupY();
            for (String key : hlaTypeY.keySet()) {
                HLA hla = new HLA(key, hlaTypeY.get(key), false, true);
                database.add(profileID, hla);
            }

            Map<String, Integer> hlaTypeSecondary = hlaType.getSecondaryAntigens();
            for (String key : hlaTypeSecondary.keySet()) {
                HLA hla = new HLA(key, hlaTypeSecondary.get(key), false, false);
                database.add(profileID, hla);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return e.getMessage();
        }

        res.status(201);
        return "HLAType added";
    }

    /**
     * Deletes the given HLAType.
     *
     * @param req the empty request body.
     * @param res the empty response.
     * @return the response body.
     */
    public static String delete(Request req, Response res) {
        HLADAO database = DAOFactory.getHLADAO();
        Integer profileID = Integer.valueOf(req.params("id"));

        try {
            database.removeAll(profileID);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.status(500);
            return e.getMessage();
        }

        res.status(201);
        return "HLAs deleted";
    }
}
