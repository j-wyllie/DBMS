package odms.controller.data.data;

import static odms.controller.data.MedicationDataIO.getActiveIngredients;
import static odms.controller.data.MedicationDataIO.getSuggestionList;
import static org.junit.Assert.*;

import odms.controller.data.MedicationDataIO;
import odms.controller.database.DAOFactory;
import odms.controller.database.MedicationInteractionsDAO;
import odms.model.medications.Interaction;
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
    private Object[] expectedList1;
    private Object[] expectedList2;
    private Object[] expectedList3;
    private String[] expectedList4;

    private String drugOne;
    private String drugTwo;
    private String drugThree;
    private String drugFour;
    private Map<String, String> interactions;
    private String path;

    @Before
    public void setUp() {
        //Test for null or empty substrings.
        substring1 = "";
        substring2 = null;
        expectedList1 = Collections.emptyList().toArray();
        path = "./src/test/resources/medicationTestData/";

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
        drugThree = null;
        drugFour = "";
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
        interactions.put("hepatitis acute", "not specified");    }


    @Test
    public void testEmptyOrNullStringGetSuggestionList() throws IOException {
        //Test for null or empty substrings.
        assertArrayEquals(expectedList1, getSuggestionList(substring1).toArray());
        assertArrayEquals(expectedList1, getSuggestionList(substring2).toArray());
    }

    @Test
    public void testValidStringGetSuggestionList() throws IOException {
        String line;
        // read json response into stringBuffer. Mocked makeRequest method will return the stringBuffer.
        BufferedReader bufferedReader = new BufferedReader(new FileReader(
                path + "suggestionListSampleResponse.json"));
        StringBuffer suggestionData = new StringBuffer();
        while ((line = bufferedReader.readLine()) != null) {
            suggestionData.append(line);
        }

        // Mock makeRequests method, returns json data of interactions in a stringBuffer
        PowerMockito.stub(PowerMockito.method(MedicationDataIO.class, "makeRequest"))
                .toReturn(suggestionData);

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
    public void testValidStringGetActiveIngredients() throws IOException {
        // read json response into stringBuffer. Mocked makeRequest method will return the stringBuffer.
        String line;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(
                path + "activeIngredientSampleResponse1.json"));
        StringBuffer ingredientData = new StringBuffer();
        while ((line = bufferedReader.readLine()) != null) {
            ingredientData.append(line);
        }

        // Mock makeRequests method, returns json data of interactions in a stringBuffer
        PowerMockito.stub(PowerMockito.method(MedicationDataIO.class, "makeRequest"))
                .toReturn(ingredientData);

        //Test for drug name with valid value.
        assertArrayEquals(expectedList3, getActiveIngredients(drugName).toArray());
    }

    @Test
    public void testValidStringGetActiveIngredientsWithSpaceInDrugName() throws IOException {
        // read json response into stringBuffer. Mocked makeRequest method will return the stringBuffer.
        String line;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(
                path + "activeIngredientSampleResponse2.json"));
        StringBuffer ingredientData = new StringBuffer();
        while ((line = bufferedReader.readLine()) != null) {
            ingredientData.append(line);
        }

        // Mock makeRequests method, returns json data of interactions in a stringBuffer
        PowerMockito.stub(PowerMockito.method(MedicationDataIO.class, "makeRequest"))
                .toReturn(ingredientData);

        //Test for drug name with valid value and it has a space in drug name.
        assertArrayEquals(expectedList4, getActiveIngredients(drugName2).toArray());
    }

    @Test
    public void testGetDrugInteractions() throws IOException{
        // Mock makeRequests method, returns json data of interactions in a stringBuffer.
        // Read json response into stringBuffer. Mocked makeRequest method will return the stringBuffer.
        String line;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(
                path + "drugInteractionsSampleResponse.json"));
        StringBuffer interactionData = new StringBuffer();
        while ((line = bufferedReader.readLine()) != null) {
            interactionData.append(line);
        }
        PowerMockito.stub(PowerMockito.method(MedicationDataIO.class, "makeRequest"))
                .toReturn(interactionData);

        // Build valid interaction object, using api response data that we have stored locally.
        MedicationInteractionsDAO medicalInteractions = DAOFactory.getMedicalInteractionsDao();
        medicalInteractions.setLocation("./cache/test_medication_interactions.json");
        Interaction interaction = medicalInteractions.get(drugOne, drugTwo);

        // Test valid request
        Map<String, String> results = MedicationDataIO.getDrugInteractions(interaction, "male", 29);
        assertEquals(interactions, results);
    }


    @Test
    public void testGetDrugInteractionsNullOrEmptyString() {
        //Test for null drug string
        Map<String, List<String>> map = new HashMap<>();
        Interaction interaction = new Interaction(drugOne, drugThree, map, new HashMap<>(), map, map);
        Map<String, String> results = MedicationDataIO.getDrugInteractions(interaction, "male", 29);
        assertTrue(results.isEmpty());

        // Test for empty drug string
        interaction = new Interaction(drugOne, drugFour, map, new HashMap<>(), map, map);
        results = MedicationDataIO.getDrugInteractions(interaction, "male", 29);
        assertTrue(results.isEmpty());
    }
}
