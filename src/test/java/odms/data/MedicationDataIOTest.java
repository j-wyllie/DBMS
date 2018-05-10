package odms.data;

import static odms.data.MedicationDataIO.getActiveIngredients;
import static odms.data.MedicationDataIO.getSuggestionList;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


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
    private String drugFive;
    private String drugSix;
    private String drugSeven;
    private String drugEight;
    private Map<String, String> interactions;


    private StringBuffer response;
    private StringBuffer response1;
    private StringBuffer response1_1;
    private StringBuffer response2;

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

        response = new StringBuffer();
        response.append("{\"query\":\"res\",\"suggestions\":[\"Reserpine\",\"Resectisol\",\"Resectisol " +
                "in plastic container\",\"Restoril\",\"Rescriptor\",\"Restasis\",\"Rescula\",\"Reserpine " +
                "and hydrochlorothiazide\",\"Reserpine, hydralazine hydrochloride and hydrochlorothiazide\"" +
                ",\"Reserpine, hydrochlorothiazide, and hydralazine hydrochloride\",\"Reserpine and " +
                "hydrochlorothiazide-50\",\"Reserpine and hydroflumethiazide\",\"Resporal\"]}");

        response1 = new StringBuffer();
        response1.append("[\"Hydralazine hydrochloride; hydrochlorothiazide; reserpine\",\"Hydrochlorothiazide; " +
                "reserpine\",\"Hydroflumethiazide; reserpine\",\"Reserpine\"]");

        response1_1 = new StringBuffer();
        response1_1.append("[\"Methadone hydrochloride\"]");

        response2 = new StringBuffer();
        response2.append("{\n" +
                "  \"age_interaction\": {\n" +
                "    \"0-1\": [\n" +
                "      \"orthostatic hypotension\"\n" +
                "    ], \n" +
                "    \"10-19\": [\n" +
                "      \"dizziness\", \n" +
                "      \"phlebitis\", \n" +
                "      \"tachycardia\", \n" +
                "      \"thrombosis\", \n" +
                "      \"convulsion\", \n" +
                "      \"abdominal pain upper\", \n" +
                "      \"chest pain\", \n" +
                "      \"cough\", \n" +
                "      \"drug ineffective\", \n" +
                "      \"dyspnoea\"\n" +
                "    ], \n" +
                "    \"2-9\": [\n" +
                "      \"glomerulonephritis membranous\", \n" +
                "      \"nephritic syndrome\", \n" +
                "      \"disease recurrence\", \n" +
                "      \"mucopolysaccharidosis\", \n" +
                "      \"nephrotic syndrome\", \n" +
                "      \"procedural pain\"\n" +
                "    ], \n" +
                "    \"20-29\": [\n" +
                "      \"drug ineffective\", \n" +
                "      \"neuropathy peripheral\", \n" +
                "      \"oedema peripheral\", \n" +
                "      \"infusion related reaction\", \n" +
                "      \"autoimmune hepatitis\", \n" +
                "      \"catheter site related reaction\", \n" +
                "      \"cerebrovascular accident\", \n" +
                "      \"cholestasis\", \n" +
                "      \"drug eruption\", \n" +
                "      \"hepatitis acute\"\n" +
                "    ], \n" +
                "    \"30-39\": [\n" +
                "      \"pulmonary embolism\", \n" +
                "      \"anxiety\", \n" +
                "      \"injury\", \n" +
                "      \"vomiting\", \n" +
                "      \"anhedonia\", \n" +
                "      \"nausea\", \n" +
                "      \"pain in extremity\", \n" +
                "      \"chest pain\", \n" +
                "      \"dyspnoea\", \n" +
                "      \"cough\"\n" +
                "    ], \n" +
                "    \"40-49\": [\n" +
                "      \"international normalised ratio increased\", \n" +
                "      \"hypotension\", \n" +
                "      \"anaemia\", \n" +
                "      \"thrombosis\", \n" +
                "      \"cellulitis\", \n" +
                "      \"dyspnoea\", \n" +
                "      \"oedema peripheral\", \n" +
                "      \"alanine aminotransferase increased\", \n" +
                "      \"aspartate aminotransferase increased\", \n" +
                "      \"chest pain\"\n" +
                "    ], \n" +
                "    \"50-59\": [\n" +
                "      \"international normalised ratio increased\", \n" +
                "      \"renal failure acute\", \n" +
                "      \"nausea\", \n" +
                "      \"gastrointestinal haemorrhage\", \n" +
                "      \"fatigue\", \n" +
                "      \"vomiting\", \n" +
                "      \"cardiac failure congestive\", \n" +
                "      \"diarrhoea\", \n" +
                "      \"hypotension\", \n" +
                "      \"rash\"\n" +
                "    ], \n" +
                "    \"60+\": [\n" +
                "      \"international normalised ratio increased\", \n" +
                "      \"dyspnoea\", \n" +
                "      \"renal failure acute\", \n" +
                "      \"anaemia\", \n" +
                "      \"hypotension\", \n" +
                "      \"pneumonia\", \n" +
                "      \"cardiac failure congestive\", \n" +
                "      \"nausea\", \n" +
                "      \"pyrexia\", \n" +
                "      \"fall\"\n" +
                "    ], \n" +
                "    \"nan\": [\n" +
                "      \"international normalised ratio increased\", \n" +
                "      \"death\", \n" +
                "      \"nausea\", \n" +
                "      \"arthralgia\", \n" +
                "      \"anaemia\", \n" +
                "      \"dyspnoea\", \n" +
                "      \"hypertension\", \n" +
                "      \"injury\", \n" +
                "      \"joint swelling\", \n" +
                "      \"osteonecrosis of jaw\"\n" +
                "    ]\n" +
                "  }, \n" +
                "  \"co_existing_conditions\": null, \n" +
                "  \"duration_interaction\": {\n" +
                "    \"1 - 2 years\": [\n" +
                "      \"cerebrovascular accident\", \n" +
                "      \"international normalised ratio increased\", \n" +
                "      \"gastrointestinal haemorrhage\", \n" +
                "      \"pneumonia\", \n" +
                "      \"blood electrolytes abnormal\", \n" +
                "      \"blood pressure increased\", \n" +
                "      \"coma\", \n" +
                "      \"death\", \n" +
                "      \"dysstasia\", \n" +
                "      \"epistaxis\"\n" +
                "    ], \n" +
                "    \"1 - 6 months\": [\n" +
                "      \"international normalised ratio increased\", \n" +
                "      \"dehydration\", \n" +
                "      \"dizziness\", \n" +
                "      \"epigastric discomfort\", \n" +
                "      \"inferior vena caval occlusion\", \n" +
                "      \"oedema peripheral\", \n" +
                "      \"superior vena cava syndrome\", \n" +
                "      \"thrombosis\", \n" +
                "      \"anaemia\", \n" +
                "      \"confusional state\"\n" +
                "    ], \n" +
                "    \"10+ years\": [\n" +
                "      \"blood electrolytes abnormal\", \n" +
                "      \"blood pressure increased\", \n" +
                "      \"coma\", \n" +
                "      \"death\", \n" +
                "      \"dysstasia\", \n" +
                "      \"pyrexia\", \n" +
                "      \"serotonin syndrome\", \n" +
                "      \"unresponsive to stimuli\", \n" +
                "      \"fall\", \n" +
                "      \"haematoma\"\n" +
                "    ], \n" +
                "    \"2 - 5 years\": [\n" +
                "      \"myelodysplastic syndrome\", \n" +
                "      \"anaemia\", \n" +
                "      \"epistaxis\", \n" +
                "      \"thrombocytopenia\", \n" +
                "      \"faeces discoloured\", \n" +
                "      \"haematocrit decreased\", \n" +
                "      \"haemoglobin decreased\", \n" +
                "      \"melaena\", \n" +
                "      \"abdominal pain upper\", \n" +
                "      \"abortion induced\"\n" +
                "    ], \n" +
                "    \"5 - 10 years\": [\n" +
                "      \"anaemia\", \n" +
                "      \"cerebrovascular accident\", \n" +
                "      \"international normalised ratio increased\", \n" +
                "      \"chest pain\", \n" +
                "      \"duodenal ulcer\", \n" +
                "      \"dyspnoea\", \n" +
                "      \"dyspnoea exertional\", \n" +
                "      \"faeces discoloured\", \n" +
                "      \"haematemesis\", \n" +
                "      \"melaena\"\n" +
                "    ], \n" +
                "    \"6 - 12 months\": [\n" +
                "      \"international normalised ratio increased\", \n" +
                "      \"anhedonia\", \n" +
                "      \"anxiety\", \n" +
                "      \"cyst\", \n" +
                "      \"disability\", \n" +
                "      \"dysphagia\", \n" +
                "      \"epistaxis\", \n" +
                "      \"gingival swelling\", \n" +
                "      \"injury\", \n" +
                "      \"lung disorder\"\n" +
                "    ], \n" +
                "    \"< 1 month\": [\n" +
                "      \"international normalised ratio increased\", \n" +
                "      \"pulmonary embolism\", \n" +
                "      \"urinary tract infection\", \n" +
                "      \"death\", \n" +
                "      \"multi-organ failure\", \n" +
                "      \"renal failure acute\", \n" +
                "      \"acute respiratory failure\", \n" +
                "      \"blood pressure decreased\", \n" +
                "      \"renal failure\", \n" +
                "      \"thrombocytopenia\"\n" +
                "    ], \n" +
                "    \"not specified\": [\n" +
                "      \"international normalised ratio increased\", \n" +
                "      \"dyspnoea\", \n" +
                "      \"nausea\", \n" +
                "      \"anaemia\", \n" +
                "      \"renal failure acute\", \n" +
                "      \"hypotension\", \n" +
                "      \"oedema peripheral\", \n" +
                "      \"vomiting\", \n" +
                "      \"cardiac failure congestive\", \n" +
                "      \"fatigue\"\n" +
                "    ]\n" +
                "  }, \n" +
                "  \"gender_interaction\": {\n" +
                "    \"female\": [\n" +
                "      \"international normalised ratio increased\", \n" +
                "      \"dyspnoea\", \n" +
                "      \"oedema peripheral\", \n" +
                "      \"nausea\", \n" +
                "      \"anaemia\", \n" +
                "      \"vomiting\", \n" +
                "      \"pneumonia\", \n" +
                "      \"fall\", \n" +
                "      \"diarrhoea\", \n" +
                "      \"asthenia\"\n" +
                "    ], \n" +
                "    \"male\": [\n" +
                "      \"international normalised ratio increased\", \n" +
                "      \"dyspnoea\", \n" +
                "      \"renal failure acute\", \n" +
                "      \"hypotension\", \n" +
                "      \"anaemia\", \n" +
                "      \"cardiac failure congestive\", \n" +
                "      \"nausea\", \n" +
                "      \"pyrexia\", \n" +
                "      \"fatigue\", \n" +
                "      \"pneumonia\"\n" +
                "    ]\n" +
                "  }, \n" +
                "  \"reports\": {\n" +
                "    \"amount\": 1853\n" +
                "  }\n" +
                "}\n");
    }

    @Test
    public void testEmptyOrNullStringGetSuggestionList() throws IOException {
        //Test for null or empty substrings.
        assertArrayEquals(expectedList1, getSuggestionList(substring1).toArray());
        assertArrayEquals(expectedList1, getSuggestionList(substring2).toArray());
    }

    @Test
    public void testValidStringGetSuggestionList() throws Exception {
        // Mock makeRequests method
        PowerMockito.spy(MedicationDataIO.class);
        PowerMockito.doReturn(response).when(MedicationDataIO.class, "makeRequest", "String");

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
        // Mock makeRequests method
        PowerMockito.spy(MedicationDataIO.class);
        PowerMockito.doReturn(response1).when(MedicationDataIO.class, "makeRequest", "String");

        //Test for drug name with valid value.
        assertArrayEquals(expectedList3, getActiveIngredients(drugName).toArray());
    }

    @Test
    public void testValidStringGetActiveIngredientsWithSpaceInDrugName() throws Exception {
        // Mock makeRequests method
        PowerMockito.spy(MedicationDataIO.class);
        PowerMockito.doReturn(response1_1).when(MedicationDataIO.class, "makeRequest", "String");

        //Test for drug name with valid value and it has a space in drug name.
        assertArrayEquals(expectedList4, getActiveIngredients(drugName2).toArray());
    }

    @Test
    public void testGetDrugInteractions() throws Exception {
        // Mock makeRequests method
        PowerMockito.spy(MedicationDataIO.class);
        PowerMockito.doReturn(response2).when(MedicationDataIO.class, "makeRequest", "String");

        // Test valid request
        Map<String, String> results = MedicationDataIO.getDrugInteractions(drugOne, drugTwo, "male", 29);
        for (Map.Entry<String, String> entry : interactions.entrySet()) {
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
        for (Map.Entry<String, String> entry : interactions.entrySet()) {
            assertTrue(results.containsKey(entry.getKey()));
            assertEquals(results.get(entry.getKey()), entry.getValue());
        }
    }

}
