package odms.data;

import odms.enums.CountriesEnum;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class AddressIOTest {
    private ArrayList<CountriesEnum> validCountries = new ArrayList<>();

    @Before
    public void setValidCountries() {
        validCountries.add(CountriesEnum.US);
    }
    @Test
    public void addressValidCountryTest() {
        assert(AddressIO.checkValidCountry("1600 Amphitheatre Pkwy", "USA"));
    }
    @Test
    public void addressInvalidCountryTest() {
        assertFalse(AddressIO.checkValidCountry("2 Riccarton Road", "USA"));
    }
    @Test
    public void addressValidRegionTest() {
        assert(AddressIO.checkValidRegion("15 Riccarton Road", "Canterbury", ""));
    }
    @Test
    public void addressInvalidRegionTest() {
        assertFalse(AddressIO.checkValidRegion("1600 Amphitheatre Parkway", "Canterbury", ""));
    }
    @Test
    public void addressValidCityTest() {
        assert(AddressIO.checkValidCity("15 Riccarton Road", "Christchurch", ""));
    }
    @Test
    public void addressInvalidCityTest() {
        assertFalse(AddressIO.checkValidCity("1600 Amphitheatre Parkway", "Christchurch", ""));
    }

}
