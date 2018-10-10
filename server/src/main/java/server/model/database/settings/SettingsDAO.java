package server.model.database.settings;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
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

    /**
     * Method to be called to repopulate the countries table.
     */
    void populateCountriesTable();

    /**
     * Gets the locale setting for a user for date time formatting.
     * @param id of the user.
     * @param userType type of user.
     * @return the locale setting.
     */
    String getDateTimeFormat(int id, UserType userType);

    /**
     * Gets the locale setting for a user for number formatting.
     * @param id of the user.
     * @param userType type of user.
     * @return the locale setting.
     */
    String getNumberFormat(int id, UserType userType);

    /**
     * Set the DateTimeFormat.
     * @param id the user or profile ID.
     * @param userType user or profile.
     * @param locale the locale settings.
     */
    void setDateTimeFormat(int id, UserType userType, String locale) throws SQLException;

    /**
     * Set the NumberFormat.
     * @param id the user or profile ID.
     * @param userType user or profile.
     * @param locale the locale settings.
     */
    void setNumberFormat(int id, UserType userType, String locale) throws SQLException;

    /**
     * Deletes the locale settings for a user.
     * @param id the user or profile ID.
     * @param userType user or profile.
     */
    void deleteLocale(int id, UserType userType);
}
