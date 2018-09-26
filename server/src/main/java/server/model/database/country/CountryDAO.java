package server.model.database.country;

import java.sql.SQLException;
import java.util.List;
import odms.commons.model.enums.CountriesEnum;

public interface CountryDAO {

    /**
     * Gets all countries from the database.
     * @return a list of countries.
     */
    List<String> getAll();

    /**
     * Gets all valid or invalid countries from the database.
     * @param valid true if valid countries are required.
     * @return a list of countries.
     */
    List<String> getAll(boolean valid);

    /**
     * Updates a country to valid or invalid.
     * @param country to update.
     * @param valid state to update.
     */
    void update(CountriesEnum country, boolean valid) throws SQLException;

}
