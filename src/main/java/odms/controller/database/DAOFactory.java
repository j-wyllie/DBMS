package odms.controller.database;

public class DAOFactory {

    /**
     * Gives the data access object class.
     *
     * @return database for particular database type.
     */
    public static ReadOnlyDAO getReadOnlyDao() {
        return new MySqlDAO();
    }
}
