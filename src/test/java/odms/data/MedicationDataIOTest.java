package odms.data;

import static odms.data.MedicationDataIO.getActiveIngredients;
import static odms.data.MedicationDataIO.getSuggestionList;
import static org.junit.Assert.*;
import java.io.IOException;
import java.util.*;

import org.junit.Before;
import org.junit.Test;

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
    private String drugFive;
    private String drugSix;
    private String drugSeven;
    private String drugEight;
    private Map<String, String> interactions;

    @Before
    public void setUp() {
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
        interactions.put("vomiting", "not specified");
        interactions.put("anaemia", "1 - 6 months");
        interactions.put("drug ineffective", "");
        interactions.put("neuropathy peripheral", "");
        interactions.put("oedema peripheral", "1 - 6 months");
        interactions.put("infusion related reaction", "");
        interactions.put("autoimmune hepatitis", "");
        interactions.put("catheter site related reaction", "");
        interactions.put("cerebrovascular accident", "1 - 2 years");
        interactions.put("cholestasis", "");
        interactions.put("drug eruption", "");
        interactions.put("hepatitis acute", "");
    }

    @Test
    public void testEmptyOrNullStringGetSuggestionList() throws IOException {
        //Test for null or empty substrings.
        assertArrayEquals(expectedList1, getSuggestionList(substring1).toArray());
        assertArrayEquals(expectedList1, getSuggestionList(substring2).toArray());
    }

    @Test
    public void testValidStringGetSuggestionList() throws IOException {
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
        //Test for drug name with valid value.
        assertArrayEquals(expectedList3, getActiveIngredients(drugName).toArray());
    }

    @Test
    public void testValidStringGetActiveIngredientsWithSpaceInDrugName() throws IOException {
        //Test for drug name with valid value and it has a space in drug name.
        assertArrayEquals(expectedList4, getActiveIngredients(drugName2).toArray());
    }

    @Test
    public void testGetDrugInteractions() throws IOException {
        // Test valid request
        Map<String, String> results = MedicationDataIO.getDrugInteractions(drugOne, drugTwo, "male", 29);
        for (Map.Entry<String, String> entry : results.entrySet()) {
            assertTrue(results.containsKey(entry.getKey()));
            assertEquals(results.get(entry.getKey()), entry.getValue());
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

        // Test valid request with drug with space in name
        results = MedicationDataIO.getDrugInteractions(drugOne, drugEight, "male", 29);
        for (Map.Entry<String, String> entry : results.entrySet()) {
            assertTrue(results.containsKey(entry.getKey()));
            assertEquals(results.get(entry.getKey()), entry.getValue());
        }
    }
}
