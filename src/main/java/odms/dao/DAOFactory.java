package odms.dao;

public class DAOFactory {

    /**
     * Gives the data access object class for other common transactions.
     * @return dao for the common database transactions.
     */
    public static CommonDAO getCommonDao() { return new MySqlCommonDAO(); }

    /**
     * Gives the data access object class for the User object.
     * @return dao for User object database transactions.
     */
    public static UserDAO getUserDao() {
        return new MySqlUserDAO();
    }

    /**
     * Gives the data access object class for the Profile object.
     * @return dao for the Profile object database transactions.
     */
    public static ProfileDAO getProfileDao() { return new MySqlProfileDAO(); }

    /**
     * Gives the data access object class for the Drug object.
     * @return dao for the Drug object database transactions.
     */
    public static MedicationDAO getMedicationDao() { return new MySqlMedicationDAO(); }

    /**
     * Gives the data access object class for the Condition object.
     * @return dao for the Condition object database transactions.
     */
    public static ConditionDAO getConditionDao() { return new MySqlConditionDAO(); }

    /**
     * Gives the data access object class for the Procedure object.
     * @return dao for the Procedure object database transactions.
     */
    public static ProcedureDAO getProcedureDao() { return new MySqlProcedureDAO(); }

    /**
     * Gives the data access object class for the Organ object.
     * @return dao for the Organ object database transactions.
     */
    public static OrganDAO getOrganDao() { return new MySqlOrganDAO(); }

}
