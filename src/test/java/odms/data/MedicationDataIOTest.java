package odms.data;

import static odms.data.MedicationDataIO.getActiveIngredients;
import static odms.data.MedicationDataIO.getSuggestionList;
import static org.junit.Assert.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;

public class MedicationDataIOTest {

    private String substring1;
    private String substring2;
    private String substring3;
    private String drugName;
    private Object[] expectedList1;
    private Object[] expectedList2;
    private Object[] expectedList3;

    @Before
    public void setUp() {
        //Test for null or empty substrings.
        substring1 = "";
        substring2 = null;
        expectedList1 = Collections.emptyList().toArray();

        //Test for substring with valid value.
        substring3 = "res";
        expectedList2 = new ArrayList<>(Arrays.asList("Reserpine", "Resectisol", "Resectisol in plastic container",
                "Restoril", "Rescriptor", "Restasis", "Rescula", "Reserpine and hydrochlorothiazide",
                "Reserpine, hydralazine hydrochloride and hydrochlorothiazide",
                "Reserpine, hydrochlorothiazide, and hydralazine hydrochloride",
                "Reserpine and hydrochlorothiazide-50", "Reserpine and hydroflumethiazide",
                "Resporal")).toArray();

        //Test for drug name with valid name.
        drugName = "Reserpine";
        expectedList3 = new ArrayList<>(Arrays.asList("Hydralazine hydrochloride; hydrochlorothiazide; reserpine",
                "Hydrochlorothiazide; reserpine", "Hydroflumethiazide; reserpine", "Reserpine")).toArray();

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
}