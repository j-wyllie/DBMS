package odms.controller.database;

import odms.controller.database.common.CommonDAO;
import odms.controller.database.common.HttpCommonDAO;
import odms.controller.database.condition.ConditionDAO;
import odms.controller.database.condition.HttpConditionDAO;
import odms.controller.database.country.CountryDAO;
import odms.controller.database.country.HttpCountryDAO;
import odms.controller.database.interactions.JsonMedicationInteractionsDAO;
import odms.controller.database.interactions.MedicationInteractionsDAO;
import odms.controller.database.medication.HttpMedicationDAO;
import odms.controller.database.medication.MedicationDAO;
import odms.controller.database.organ.HttpOrganDAO;
import odms.controller.database.organ.OrganDAO;
import odms.controller.database.procedure.HttpProcedureDAO;
import odms.controller.database.procedure.ProcedureDAO;
import odms.controller.database.profile.HttpProfileDAO;
import odms.controller.database.profile.ProfileDAO;
import odms.controller.database.user.HttpUserDAO;
import odms.controller.database.user.UserDAO;


public class DAOFactory {

    /**
     * Gives the data access object class for other common transactions.
     * @return dao for the common http transactions.
     */
    public static CommonDAO getCommonDao() { return new HttpCommonDAO(); }

    /**
     * Gives the data access object class for the User object.
     * @return dao for User object http transactions.
     */
    public static UserDAO getUserDao() { return new HttpUserDAO(); }

    /**
     * Gives the data access object class for the Profile object.
     * @return dao for the Profile object http transactions.
     */
    public static ProfileDAO getProfileDao() { return new HttpProfileDAO(); }

    /**
     * Gives the data access object class for the Drug object.
     * @return dao for the Drug object http transactions.
     */
    public static MedicationDAO getMedicationDao() { return new HttpMedicationDAO(); }

    /**
     * Gives the data access object class for the Condition object.
     * @return dao for the Condition object http transactions.
     */
    public static ConditionDAO getConditionDao() { return new HttpConditionDAO(); }

    /**
     * Gives the data access object class for the Procedure object.
     * @return dao for the Procedure object http transactions.
     */
    public static ProcedureDAO getProcedureDao() { return new HttpProcedureDAO(); }

    /**
     * Gives the data access object class for the Organ object.
     * @return dao for the Organ object http transactions.
     */
    public static OrganDAO getOrganDao() { return new HttpOrganDAO(); }

    /**
     * Gives the data access object class for the Countries enum.
     * @return dao for particular http type.
     */
    public static CountryDAO getCountryDAO() {
        return new HttpCountryDAO();
    }

    /**
     * Gives the data access object class for the medication interactions.
     * @return dao for the data IO type.
     */
    public static MedicationInteractionsDAO getMedicalInteractionsDao() { return new JsonMedicationInteractionsDAO(); }

}
