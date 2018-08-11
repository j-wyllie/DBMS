package server.model.database;

import server.model.database.condition.ConditionDAO;
import server.model.database.condition.MySqlConditionDAO;
import server.model.database.country.CountryDAO;
import server.model.database.country.MySqlCountryDAO;
import server.model.database.medication.MedicationDAO;
import server.model.database.medication.MedicationInteractionsDAO;
import server.model.database.medication.MySqlMedicationDAO;
import server.model.database.organ.MySqlOrganDAO;
import server.model.database.organ.OrganDAO;
import server.model.database.procedure.MySqlProcedureDAO;
import server.model.database.procedure.ProcedureDAO;
import server.model.database.profile.MySqlProfileDAO;
import server.model.database.profile.ProfileDAO;
import server.model.database.user.MySqlUserDAO;
import server.model.database.user.UserDAO;

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

    /**
     * Gives the data access object class for the Countries enum.
     * @return dao for particular database type.
     */
    public static CountryDAO getCountryDAO() {
        return new MySqlCountryDAO();
    }

//    /**
//     * Gives the data access object class for the medication interactions.
//     * @return dao for the data IO type.
//     */
//    public static MedicationInteractionsDAO getMedicalInteractionsDao() { return new JsonMedicationInteractionsDAO(); }

}
