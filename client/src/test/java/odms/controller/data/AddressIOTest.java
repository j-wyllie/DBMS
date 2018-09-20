package odms.controller.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import odms.controller.data.AddressIO;
import org.junit.Test;

import java.util.ArrayList;

public class AddressIOTest {

    private static final String VALID_CITY = "Christchurch";
    private static final String VALID_COUNTRY = "New Zealand";
    private static final String VALID_REGION = "Canterbury";

    @Test
    public void checkInvalidCountry() {
        assertFalse(AddressIO.checkValidCountry(VALID_CITY, "Mars"));
    }

    @Test
    public void checkNoCountry() {
        assertFalse(AddressIO.checkValidCountry(null, VALID_COUNTRY));

    }

    @Test
    public void checkValidCountry() {
        assertTrue(AddressIO.checkValidCountry(VALID_CITY, VALID_COUNTRY));
    }

    @Test
    public void checkInvalidRegion() {
        assertFalse(AddressIO.checkValidRegion(VALID_CITY, "Victoria", VALID_COUNTRY));
    }

    @Test
    public void checkValidRegion() {
        assertTrue(AddressIO.checkValidRegion(VALID_CITY, VALID_REGION, VALID_COUNTRY));
    }

    @Test
    public void checkNullRegion() {
        assertFalse(AddressIO.checkValidRegion(VALID_CITY, null, VALID_COUNTRY));
    }

    @Test
    public void checkInvalidCity() {
        assertFalse(AddressIO.checkValidCity("Canberra", "Australian Capital Territory", VALID_COUNTRY));
    }

    @Test
    public void checkValidCity() {
        assertTrue(AddressIO.checkValidCity("Auckland,Auckland,New Zealand", "Auckland", "New Zealand"));
    }

    @Test
    public void checkNullCity() {
        assertFalse(AddressIO.checkValidCity(null, "Australian Capital Territory", "Australia"));

    }

    @Test
    public void checkLatLongRegion() {
        ArrayList<Double> wellingtonCheck = new ArrayList<>();
        ArrayList<Double> aucklandCheck = new ArrayList<>();
        wellingtonCheck.add(-41.2864603);
        wellingtonCheck.add(174.776236);
        assertEquals(AddressIO.getLongLatRegion("Wellington", "New Zealand"), wellingtonCheck);
        aucklandCheck.add(-36.8484597);
        aucklandCheck.add(174.7633315);
        assertEquals(AddressIO.getLongLatRegion("Auckland", "New Zealand"), aucklandCheck);
    }

}
