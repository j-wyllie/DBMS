package server.model.database.common;

public interface CommonDAO {

    /**
     * Interface for the querying of databases across
     * platforms.
     * @param query
     */
    public void queryDatabase(String query);
}
