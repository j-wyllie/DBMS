package odms.data;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class MedicationDataIOTest {

    private String drugOne;
    private String drugTwo;
    private String drugThree;
    private String drugFour;
    private String drugFive;
    private String drugSix;
    private String drugSeven;
    private Map<String, String> interactions;

    @Before
    public void setUp() {
        drugOne = "acetaminophen";
        drugTwo = "warfarin-sodium";
        drugThree = "fake drug";
        drugFour = "another fake drug";
        drugFive = "maca";
        drugSix = null;
        drugSeven = "";
        interactions = new HashMap<>();

        interactions.put("\"international normalised ratio increased\"", "1 - 2 years");
        interactions.put("\"nausea\"", "not specified");
        interactions.put("\"renal failure acute\"", "< 1 month");
        interactions.put("\"hypotension\"", "not specified");
        interactions.put("\"cardiac failure congestive\"", "not specified");
        interactions.put("\"pneumonia\"", "1 - 2 years");
        interactions.put("\"fatigue\"", "not specified");
        interactions.put("\"dyspnoea\"", "5 - 10 years");
        interactions.put("\"vomiting\"", "not specified");
        interactions.put("\"anaemia\"", "1 - 6 months");
        interactions.put("\"drug ineffective\"", "");
        interactions.put("\"neuropathy peripheral\"", "");
        interactions.put("\"oedema peripheral\"", "1 - 6 months");
        interactions.put("\"infusion related reaction\"", "");
        interactions.put("\"autoimmune hepatitis\"", "");
        interactions.put("\"catheter site related reaction\"", "");
        interactions.put("\"cerebrovascular accident\"", "1 - 2 years");
        interactions.put("\"cholestasis\"", "");
        interactions.put("\"drug eruption\"", "");
        interactions.put("\"hepatitis acute\"", "");

    }

    @Test
    public void testGetDrugInteractions() throws IOException {
        Map<String, String> results = MedicationDataIO.getDrugInteractions(drugOne, drugTwo, "male", 29);
        for (Map.Entry<String, String> entry : results.entrySet()) {
            assertTrue(results.containsKey(entry.getKey()));
            assertEquals(results.get(entry.getKey()), entry.getValue());
            System.out.println(entry.getKey() + " / " + entry.getValue());
        }

        //Test for null drug string
        results = MedicationDataIO.getDrugInteractions(drugOne, drugSix, "male", 29);
        assertTrue(results.isEmpty());

        // Test for empty drug string
        results = MedicationDataIO.getDrugInteractions(drugOne, drugSeven, "male", 29);
        assertTrue(results.isEmpty());

        // Test for two valid drugs, should return empty map.
        results = MedicationDataIO.getDrugInteractions(drugOne, drugFive, "male", 29);
        assertTrue(results.isEmpty());

        // Test for invalid drugs, should return empty map.
        results = MedicationDataIO.getDrugInteractions(drugThree, drugFour, "male", 29);
        assertTrue(results.isEmpty());
    }
}