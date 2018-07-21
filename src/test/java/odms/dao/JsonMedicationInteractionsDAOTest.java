package odms.dao;

import static org.junit.Assert.assertEquals;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import odms.data.MedicationDataIO;
import odms.medications.Interaction;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest(JsonMedicationInteractionsDAO.class)
public class JsonMedicationInteractionsDAOTest {

    private MedicationInteractionsDAO interactionsTestDb;
    private String TEST_DATA = "./src/test/java/odms/data/medicationTestData/drugInteractionsSampleResponse.json";
    private String TEST_LOCATION = "./cache/test_medication_interactions.json";

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
    public void setup() {
        interactionsTestDb = DAOFactory.getMedicalInteractionsDao();
        interactionsTestDb.setLocation(TEST_LOCATION);
        drugNameA = "acetaminophen";
        drugNameB = "warfarin-sodium";
        drugNameC = "acetaminophen"; //todo: change
        drugNameD = "warfarin-sodium"; //todo: change
        testGetInteractionExpected = new Interaction(drugNameA, drugNameB, ageEffects,
                coexistingEffects, durationEffects, genderEffects);
    }

    @Test
    public void testGet() throws IOException {
        // read json response into stringBuffer. Mocked makeRequest method will return the stringBuffer.
        BufferedReader bufferedReader = new BufferedReader(new FileReader(TEST_DATA));
        StringBuffer interactionData = new StringBuffer();
        interactionData.append(bufferedReader.readLine());

        // Mock makeRequests method, returns json data of interactions in a stringBuffer
        PowerMockito.stub(PowerMockito.method(MedicationDataIO.class, "makeRequest"))
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
        testLoadInteraction = interactionsTestDb.get(drugNameA, drugNameB);

        interactionsTestDb.save();
        interactionsTestDb.load();

        assertEquals(1, interactionsTestDb.getAll().size());
    }

    @Test
    public void testSave() throws IOException {
        interactionsTestDb.clear();
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
