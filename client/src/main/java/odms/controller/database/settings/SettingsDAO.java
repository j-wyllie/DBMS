package odms.controller.database.settings;

import java.util.List;
import odms.commons.model.enums.CountriesEnum;

public interface SettingsDAO {

    /**
     * Gets all countries from the database.
     * @return a list of countries.
     */
    List<String> getAllCountries();

    /**
     * Gets all valid or invalid countries from the database.
     * @param valid true if valid countries are required.
     * @return a list of countries.
     */
    List<String> getAllCountries(boolean valid);

    /**
     * Updates a settings to valid or invalid.
     * @param country to updateCountry.
     * @param valid state to updateCountry.
     */
    void updateCountry(CountriesEnum country, boolean valid);

    void getLocale();

    void setLocale();
}
