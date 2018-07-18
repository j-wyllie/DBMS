package odms.controller.database;

public final class DAOFactory {

    private DAOFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gives the data access object class.
     *
     * @return database for particular database type.
     */
    public static ReadOnlyDAO getReadOnlyDao() {
        return new MySqlDAO();
    }
}
