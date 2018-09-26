package odms.controller.database.common;

import odms.commons.model.InitializationException;

public interface CommonDAO {

    /**
     * Interface for the querying of databases across
     * platforms.
     * @param query
     */
    public void queryDatabase(String query);

    /**
     * Sends request to set up default user accounts on the server.
     * @throws InitializationException error.
     */
    void setup() throws InitializationException;

    void logout();
}
