package odms.controller.database;

public interface ReadOnlyDAO {

    /**
     * Interface for the querying of databases across platforms.
     *
     * @param query
     */
    void queryDatabase(String query);

}
