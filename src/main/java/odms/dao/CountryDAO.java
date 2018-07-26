package odms.dao;

import java.util.List;
import odms.enums.CountriesEnum;

public interface CountryDAO {

    /**
     * Gets all countries from the database.
     * @return a list of countries.
     */
    List<CountriesEnum> getAll();

    /**
     * Gets all valid or invalid countries from the database.
     * @param valid true if valid countries are required.
     * @return a list of countries.
     */
    List<CountriesEnum> getAll(boolean valid);
}
