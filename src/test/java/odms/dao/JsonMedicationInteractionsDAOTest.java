package odms.dao;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import odms.data.MedicationDataIO;
import odms.medications.Interaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JsonMedicationInteractionsDAO.class)
public class JsonMedicationInteractionsDAOTest {

    private MedicationInteractionsDAO interactionsTestDb;
    private String TEST_DATA = "./src/test/java/odms/data/medicationTestData/drugInteractionsSampleResponse.json";
    private String TEST_LOCATION = "./cache/test_medication_interactions.json";
    private StringBuffer interactionData;

    private String drugNameA;
    private String drugNameB;
    private String drugNameC;
    private String drugNameD;

    private Map<String, List<String>> ageEffects;
    private Map<String, Integer> coexistingEffects;
    private Map<String, List<String>> durationEffects;
    private Map<String, List<String>> genderEffects;

    private Interaction testGetInteractionActual;
    private Interaction testGetInteractionExpected;

    private Interaction testLoadInteraction;
    private Interaction testSaveInteraction;

    @Before
    public void setup() throws IOException {
        interactionsTestDb = DAOFactory.getMedicalInteractionsDao();
        interactionsTestDb.setLocation(TEST_LOCATION);
        drugNameA = "acetaminophen";
        drugNameB = "warfarin-sodium";
        drugNameC = "reserpine";
        drugNameD = "serpate";

        // Read json response into stringBuffer. Mocked makeRequest method will return the stringBuffer.
        BufferedReader bufferedReader = new BufferedReader(new FileReader(TEST_DATA));
        interactionData = new StringBuffer();
        interactionData.append(bufferedReader.readLine());

        // Get the file as json object to parse into the variables.
        JsonParser parser = new JsonParser();
        JsonObject jsonData = (JsonObject) parser.parse(new FileReader(TEST_DATA));

        // testGet() setup.
        Gson gson = new Gson();

        if (!(jsonData.get("age_interaction").isJsonNull())) {
            ageEffects = new HashMap<>();
            JsonObject age = jsonData.get("age_interaction").getAsJsonObject();
            age.keySet().forEach(key -> {
                ArrayList value = gson.fromJson(
                        age.get(key).getAsJsonArray(),
                        ArrayList.class
                );
                ageEffects.put(key, value);
            });
        }

        if (!(jsonData.get("co_existing_conditions").isJsonNull())) {
            coexistingEffects = new HashMap<>();
            JsonObject coexisting = jsonData.get("co_existing_conditions").getAsJsonObject();
            coexisting.keySet().forEach(key -> {
                Integer value = gson.fromJson(coexisting.get(key), Integer.class);
                coexistingEffects.put(key, value);
            });
        }

        if (!(jsonData.get("duration_interaction").isJsonNull())) {
            durationEffects = new HashMap<>();
            JsonObject duration = jsonData.get("duration_interaction").getAsJsonObject();
            duration.keySet().forEach(key -> {
                ArrayList value = gson.fromJson(
                        duration.get(key).getAsJsonArray(),
                        ArrayList.class
                );
                durationEffects.put(key, value);
            });
        }

        if (!(jsonData.get("gender_interaction").isJsonNull())) {
            genderEffects = new HashMap<>();
            JsonObject gender = jsonData.get("gender_interaction").getAsJsonObject();
            gender.keySet().forEach(key -> {
                ArrayList value = gson.fromJson(
                        gender.get(key).getAsJsonArray(),
                        ArrayList.class
                );
                genderEffects.put(key, value);
            });
        }
        testGetInteractionExpected = new Interaction(drugNameA, drugNameB, ageEffects,
                coexistingEffects, durationEffects, genderEffects);
    }

    @Test
    public void testGet() throws IOException {
        // Mock makeRequests method, returns json data of interactions in a stringBuffer.
        PowerMockito.stub(PowerMockito.method(JsonMedicationInteractionsDAO.class, "getResponse"))
                .toReturn(interactionData);
        testGetInteractionActual = interactionsTestDb.get(drugNameA, drugNameB);

        assertEquals(testGetInteractionExpected.getDrugA(),
                testGetInteractionActual.getDrugA());
        assertEquals(testGetInteractionExpected.getDrugB(),
                testGetInteractionActual.getDrugB());
        assertEquals(testGetInteractionExpected.getAgeInteractions(),
                testGetInteractionActual.getAgeInteractions());
        assertEquals(testGetInteractionExpected.getCoexistingConditions(),
                testGetInteractionActual.getCoexistingConditions());
        assertEquals(testGetInteractionExpected.getDurationInteractions(),
                testGetInteractionActual.getDurationInteractions());
        assertEquals(testGetInteractionExpected.getGenderInteractions(),
                testGetInteractionActual.getGenderInteractions());
    }

    @Test
    public void testLoad() throws IOException {
        interactionsTestDb.clear();

        // Mock makeRequests method, returns json data of interactions in a stringBuffer
        PowerMockito.stub(PowerMockito.method(MedicationDataIO.class, "makeRequest"))
                .toReturn(interactionData);
        testLoadInteraction = interactionsTestDb.get(drugNameA, drugNameB);

        interactionsTestDb.save();
        interactionsTestDb.load();

        assertEquals(1, interactionsTestDb.getAll().size());
    }

    @Test
    public void testSave() throws IOException {
        interactionsTestDb.clear();

        // Mock makeRequests method, returns json data of interactions in a stringBuffer
        PowerMockito.stub(PowerMockito.method(MedicationDataIO.class, "makeRequest"))
                .toReturn(interactionData);
        testSaveInteraction = interactionsTestDb.get(drugNameA, drugNameB);
        testSaveInteraction = interactionsTestDb.get(drugNameC, drugNameD);

        interactionsTestDb.save();
        interactionsTestDb.load();

        assertEquals(2, interactionsTestDb.getAll().size());
    }

    @Test
    public void testClear() {
        interactionsTestDb.clear();
        assertEquals(0, interactionsTestDb.getAll().size());
    }
}
