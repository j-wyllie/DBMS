package odms.dao;

public class DAOFactory {

    public static ReadOnlyDAO getReadOnlyDao() {
        return new MySqlDAO();
    }
}
