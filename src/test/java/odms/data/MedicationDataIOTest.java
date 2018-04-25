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
    private Map<String, String> interactions;

    @Before
    public void setUp() {
        drugOne = "acetaminophen";
        drugTwo = "warfarin-sodium";
        interactions = new HashMap<>();
        interactions.put("\"international normalised ratio increased\"", "not specified");
        interactions.put("\"nausea\"", "not specified");
        interactions.put("\"renal failure acute\"", "not specified");
        interactions.put("\"hypotension\"", "not specified");
        interactions.put("\"cardiac failure congestive\"", "not specified");
        interactions.put("\"pneumonia\"", "1 - 2 years");
        interactions.put("\"fatigue\"", "not specified");
        interactions.put("\"dyspnoea\"", "not specified");
        interactions.put("\"vomiting\"", "not specified");
        interactions.put("\"anaemia\"", "not specified");
    }

    @Test
    public void getDrugInteractions() throws IOException {
        Map<String, String> results = MedicationDataIO.getDrugInteractions(drugOne, drugTwo, "male", 29);
        for (Map.Entry<String, String> entry : results.entrySet()) {
            assertTrue(interactions.containsKey(entry.getKey()));
            System.out.println(entry.getKey() + " / " + entry.getValue());
        }
    }
}