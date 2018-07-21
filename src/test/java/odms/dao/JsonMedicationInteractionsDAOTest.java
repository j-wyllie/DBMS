package odms.dao;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import odms.data.MedicationDataIO;
import odms.medications.Interaction;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest(JsonMedicationInteractionsDAO.class)
public class JsonMedicationInteractionsDAOTest {

    private MedicationInteractionsDAO interactionsDb;
    private String FILE_LOCATION = "./src/test/java/odms/data/medicationTestData/drugInteractionsSampleResponse.json";

    private String drugNameA;
    private String drugNameB;
    private Interaction testGetInteractionActual;
    private Interaction testGetInteractionExpected;


    @Before
    public void setup() {
        interactionsDb = DAOFactory.getMedicalInteractionsDao();
        drugNameA = "acetaminophen";
        drugNameB = "warfarin-sodium";
        //testGetInteractionExpected = new Interaction(drugNameA, drugNameB, );
    }

    @Test
    public void testGet() throws IOException {
        // read json response into stringBuffer. Mocked makeRequest method will return the stringBuffer.
        BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_LOCATION));
        StringBuffer interactionData = new StringBuffer();
        interactionData.append(bufferedReader.readLine());

        // Mock makeRequests method, returns json data of interactions in a stringBuffer
        PowerMockito.stub(PowerMockito.method(MedicationDataIO.class, "makeRequest"))
                .toReturn(interactionData);
        testGetInteractionActual = interactionsDb.get(drugNameA, drugNameB);

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
    public void testLoad() {

    }

    @Test
    public void testSave() {

    }

    @Test
    public void testClear() {

    }
}
