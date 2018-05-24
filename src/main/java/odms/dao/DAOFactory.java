package odms.dao;

public class DAOFactory {

    /**
     * Gives the data access object class.
     * @return dao for particular database type.
     */
    public static ReadOnlyDAO getReadOnlyDao() {
        return new MySqlDAO();
    }
}
