package odms.data;

import odms.enums.CountriesEnum;
import org.junit.Assert;
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
        assert(AddressIO.checkValidCountry("1600 Amphitheatre Parkway", validCountries));
    }

}
