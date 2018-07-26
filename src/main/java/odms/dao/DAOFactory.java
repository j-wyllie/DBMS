package odms.dao;

public class DAOFactory {

    /**
     * Gives the data access object class.
     * @return dao for particular database type.
     */
    public static ReadOnlyDAO getReadOnlyDao() {
        return new MySqlDAO();
    }

    /**
     * Gives the data access object class for the Countries enum.
     * @return dao for particular database type.
     */
    public static CountryDAO getCountryDAO() {
        return new MySqlCountryDAO();
    }

    /**
     * Gives the data access object class for the medical interactions.
     * @return dao for the data IO type.
     */
    public static MedicationInteractionsDAO getMedicalInteractionsDao() { return new JsonMedicationInteractionsDAO(); }


}


