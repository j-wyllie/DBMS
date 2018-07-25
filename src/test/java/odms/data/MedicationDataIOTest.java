package odms.data;

import static odms.data.MedicationDataIO.getActiveIngredients;
import static odms.data.MedicationDataIO.getSuggestionList;
import static org.junit.Assert.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import odms.dao.DAOFactory;
import odms.medications.Interaction;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import org.junit.Before;
import org.junit.Test;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MedicationDataIO.class)

public class MedicationDataIOTest {

    private String substring1;
    private String substring2;
    private String substring3;
    private String drugName;
    private String drugName2;
    private Interaction interaction;
    private Object[] expectedList1;
    private Object[] expectedList2;
    private Object[] expectedList3;
    private String[] expectedList4;

    private String TEST_DATA = "./src/test/java/odms/data/medicationTestData/drugInteractionsSampleResponse.json";
    private Map<String, List<String>> ageEffects;
    private Map<String, Integer> coexistingEffects;
    private Map<String, List<String>> durationEffects;
    private Map<String, List<String>> genderEffects;
    private Interaction testGetInteractionExpected;
    private StringBuffer interactionData;

    private String drugOne;
    private String drugTwo;
    private String drugThree;
    private String drugFour;
    private String drugFive;
    private String drugSix;
    private String drugSeven;
    private String drugEight;
    private Map<String, String> interactions;

    @Before
    public void setUp() throws IOException {
        //Test for null or empty substrings.
        substring1 = "";
        substring2 = null;
        expectedList1 = Collections.emptyList().toArray();

        //Test for substring with valid value.
        substring3 = "res";
        expectedList2 = new ArrayList<>(
                Arrays.asList(
                        "Reserpine",
                        "Resectisol",
                        "Resectisol in plastic container",
                        "Restoril",
                        "Rescriptor",
                        "Restasis",
                        "Rescula",
                        "Reserpine and hydrochlorothiazide",
                        "Reserpine, hydralazine hydrochloride and hydrochlorothiazide",
                        "Reserpine, hydrochlorothiazide, and hydralazine hydrochloride",
                        "Reserpine and hydrochlorothiazide-50",
                        "Reserpine and hydroflumethiazide",
                        "Resporal"
                )).toArray();

        //Test for drug name with valid name.
        drugName = "Reserpine";
        expectedList3 = new ArrayList<>(
                Arrays.asList(
                        "Hydralazine hydrochloride; hydrochlorothiazide; reserpine",
                        "Hydrochlorothiazide; reserpine",
                        "Hydroflumethiazide; reserpine",
                        "Reserpine"
                )).toArray();

        drugName2 = "Dolophine hydrochloride";
        expectedList4 = new String[1];
        expectedList4[0] = "Methadone hydrochloride";

        // Test values for drug interactions test.
        drugOne = "acetaminophen";
        drugTwo = "warfarin-sodium";
        drugThree = "fake drug";
        drugFour = "another fake drug";
        drugFive = "maca";
        drugSix = null;
        drugSeven = "";
        drugEight = "warfarin sodium";
        interactions = new HashMap<>();

        interactions.put("international normalised ratio increased", "1 - 2 years");
        interactions.put("nausea", "not specified");
        interactions.put("renal failure acute", "< 1 month");
        interactions.put("hypotension", "not specified");
        interactions.put("cardiac failure congestive", "not specified");
        interactions.put("pneumonia", "1 - 2 years");
        interactions.put("fatigue", "not specified");
        interactions.put("dyspnoea", "5 - 10 years");
        interactions.put("pyrexia", "10+ years");
        interactions.put("anaemia", "1 - 6 months");
        interactions.put("drug ineffective", "not specified");
        interactions.put("neuropathy peripheral", "not specified");
        interactions.put("oedema peripheral", "1 - 6 months");
        interactions.put("infusion related reaction", "not specified");
        interactions.put("autoimmune hepatitis", "not specified");
        interactions.put("catheter site related reaction", "not specified");
        interactions.put("cerebrovascular accident", "1 - 2 years");
        interactions.put("cholestasis", "not specified");
        interactions.put("drug eruption", "not specified");
        interactions.put("hepatitis acute", "not specified");



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
    }


    @Test
    public void testEmptyOrNullStringGetSuggestionList() throws IOException {
        //Test for null or empty substrings.
        assertArrayEquals(expectedList1, getSuggestionList(substring1).toArray());
        assertArrayEquals(expectedList1, getSuggestionList(substring2).toArray());
    }

    @Test
    public void testValidStringGetSuggestionList() throws Exception {
        // read json response into stringBuffer. Mocked makeRequest method will return the stringBuffer.
        BufferedReader bufferedReader = new BufferedReader(new FileReader("./src/test/java/odms/data/medicationTestData/suggestionListSampleResponse.json"));
        StringBuffer suggestionData = new StringBuffer();
        suggestionData.append(bufferedReader.readLine());


        // Mock makeRequests method, returns json data of interactions in a stringBuffer
        PowerMockito.stub(PowerMockito.method(MedicationDataIO.class, "makeRequest")).toReturn(suggestionData);

        //Test for substring with valid value.
        assertArrayEquals(expectedList2, getSuggestionList(substring3).toArray());
    }

    @Test
    public void testEmptyOrNullStringGetActiveIngredients() throws IOException {
        //Test for null or empty substrings.
        assertArrayEquals(expectedList1, getActiveIngredients(substring1).toArray());
        assertArrayEquals(expectedList1, getActiveIngredients(substring2).toArray());
    }

    @Test
    public void testValidStringGetActiveIngredients() throws Exception {
        // read json response into stringBuffer. Mocked makeRequest method will return the stringBuffer.
        BufferedReader bufferedReader = new BufferedReader(new FileReader("./src/test/java/odms/data/medicationTestData/activeIngredientSampleResponse1.json"));
        StringBuffer ingredientData = new StringBuffer();
        ingredientData.append(bufferedReader.readLine());

        // Mock makeRequests method, returns json data of interactions in a stringBuffer
        PowerMockito.stub(PowerMockito.method(MedicationDataIO.class, "makeRequest")).toReturn(ingredientData);

        //Test for drug name with valid value.
        assertArrayEquals(expectedList3, getActiveIngredients(drugName).toArray());
    }

    @Test
    public void testValidStringGetActiveIngredientsWithSpaceInDrugName() throws Exception {
        // read json response into stringBuffer. Mocked makeRequest method will return the stringBuffer.
        BufferedReader bufferedReader = new BufferedReader(new FileReader("./src/test/java/odms/data/medicationTestData/activeIngredientSampleResponse2.json"));
        StringBuffer ingredientData = new StringBuffer();
        ingredientData.append(bufferedReader.readLine());

        // Mock makeRequests method, returns json data of interactions in a stringBuffer
        PowerMockito.stub(PowerMockito.method(MedicationDataIO.class, "makeRequest")).toReturn(ingredientData);

        //Test for drug name with valid value and it has a space in drug name.
        assertArrayEquals(expectedList4, getActiveIngredients(drugName2).toArray());
    }

    @Test
    public void testGetDrugInteractions() throws Exception {
        // Test valid request
        Interaction interaction = new Interaction(drugOne, drugTwo, ageEffects,
                coexistingEffects, durationEffects, genderEffects);
        Map<String, String> results = MedicationDataIO.getDrugInteractions(interaction, "male", 29);
        assertEquals(interactions, results);

        // Test valid request with drug with space in name
        interaction = new Interaction(drugOne, drugEight, ageEffects,
                coexistingEffects, durationEffects, genderEffects);
        results = MedicationDataIO.getDrugInteractions(interaction, "male", 29);
        assertEquals(interactions, results);
    }

    @Test
    public void testGetDrugInteractionsWithSpaceInName() throws Exception {
        // Mock makeRequests method, returns json data of interactions in a stringBuffer
        PowerMockito.stub(PowerMockito.method(MedicationDataIO.class, "makeRequest")).toReturn(interactionData);

        // Test valid request with drug with space in name
        Interaction interaction = new Interaction(drugOne, drugEight, ageEffects,
                coexistingEffects, durationEffects, genderEffects);
        Map<String, String> results = MedicationDataIO.getDrugInteractions(interaction, "male", 29);
        assertEquals(interactions, results);
    }

    @Test
    public void testGetDrugInteractionsNullOrEmptyString() throws Exception {
        //Test for null drug string
        Interaction interaction = new Interaction(drugOne, drugSix, ageEffects,
                coexistingEffects, durationEffects, genderEffects);
        Map<String, String> results = MedicationDataIO.getDrugInteractions(interaction, "male", 29);
        assertTrue(results.isEmpty());

        // Test for empty drug string
        interaction = new Interaction(drugOne, drugSeven, ageEffects,
                coexistingEffects, durationEffects, genderEffects);
        results = MedicationDataIO.getDrugInteractions(interaction, "male", 29);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testGetDrugInteractionsValidStringsWithNoInteractions() throws Exception {
        // Test for two valid drugs, should return empty map.
        Interaction interaction = new Interaction(drugOne, drugFive, ageEffects,
                coexistingEffects, durationEffects, genderEffects);
        Map<String, String> results = MedicationDataIO.getDrugInteractions(interaction, "male", 29);
        assertTrue(results.isEmpty());

        // Test for invalid drugs, should return empty map.
        interaction = new Interaction(drugOne, drugFour, ageEffects,
                coexistingEffects, durationEffects, genderEffects);
        results = MedicationDataIO.getDrugInteractions(interaction, "male", 29);
        assertTrue(results.isEmpty());
    }
}
