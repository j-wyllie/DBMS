package odms.controller.database;

import odms.controller.database.condition.ConditionDAO;
import odms.controller.database.condition.MySqlConditionDAO;
import odms.controller.database.country.CountryDAO;
import odms.controller.database.country.MySqlCountryDAO;
import odms.controller.database.common.CommonDAO;
import odms.controller.database.common.MySqlCommonDAO;
import odms.controller.database.interactions.JsonMedicationInteractionsDAO;
import odms.controller.database.interactions.MedicationInteractionsDAO;
import odms.controller.database.medication.MedicationDAO;
import odms.controller.database.medication.MySqlMedicationDAO;
import odms.controller.database.organ.MySqlOrganDAO;
import odms.controller.database.organ.OrganDAO;
import odms.controller.database.procedure.MySqlProcedureDAO;
import odms.controller.database.procedure.ProcedureDAO;
import odms.controller.database.profile.MySqlProfileDAO;
import odms.controller.database.profile.ProfileDAO;
import odms.controller.database.user.UserDAO;

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
    public static UserDAO getUserDao() { return new odms.controller.database.MySqlUserDAO(); }

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

    /**
     * Gives the data access object class for the medication interactions.
     * @return dao for the data IO type.
     */
    public static MedicationInteractionsDAO getMedicalInteractionsDao() { return new JsonMedicationInteractionsDAO(); }

}
