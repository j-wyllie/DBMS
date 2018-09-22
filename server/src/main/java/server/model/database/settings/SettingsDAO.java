package server.model.database.settings;

import java.util.List;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.enums.UserType;

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
     * Updates a country to valid or invalid.
     * @param country to updateCountries.
     * @param valid state to updateCountries.
     */
    void updateCountries(CountriesEnum country, boolean valid);

    void setDateTimeFormat(int id, UserType userType, String locale);

    void setNumberFormat(int id, UserType userType, String locale);
}
