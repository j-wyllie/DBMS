package odms.data;

import static odms.data.MedicationDataIO.GetSuggestionList;
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
    private Object[] expectedList1;
    private Object[] expectedList2;

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
    }

    @Test
    public void testEmptyOrNullStringGetSuggestionList() throws IOException {
        //Test for null or empty substrings.
        assertArrayEquals(expectedList1, GetSuggestionList(substring1).toArray());
        assertArrayEquals(expectedList1, GetSuggestionList(substring2).toArray());
    }

    @Test
    public void testValidStringGetSuggestionList() throws IOException {
        //Test for substring with valid value.
        assertArrayEquals(expectedList2, GetSuggestionList(substring3).toArray());
    }
}