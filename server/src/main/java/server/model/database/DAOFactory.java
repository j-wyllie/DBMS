package server.model.database;

import server.model.database.common.CommonDAO;
import server.model.database.common.MySqlCommonDAO;
import server.model.database.condition.ConditionDAO;
import server.model.database.condition.MySqlConditionDAO;
import server.model.database.settings.SettingsDAO;
import server.model.database.settings.MySqlSettingsDAO;
import server.model.database.hla.HLADAO;
import server.model.database.hla.MySqlHLADao;
import server.model.database.locations.HospitalDAO;
import server.model.database.locations.MySqlHospitalDAO;
import server.model.database.medication.MedicationDAO;
import server.model.database.medication.MySqlMedicationDAO;
import server.model.database.middleware.MiddlewareDAO;
import server.model.database.middleware.MySqlMiddlewareDAO;
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
     * Gives the data access object class for the user object.
     * @return dao for user object database transactions.
     */
    public static UserDAO getUserDao() {
        return new MySqlUserDAO();
    }

    /**
     * Gives the data access object class for the profile object.
     * @return dao for the profile object database transactions.
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
    public static SettingsDAO getSettingsDAO() {
        return new MySqlSettingsDAO();
    }

    /**
     * Gives the data access object class for the Hospital object.
     * @return dao for particular database type.
     */
    public static HospitalDAO getHospitalDAO() {
        return new MySqlHospitalDAO();
    }

    /**
     * Gives the data access object class for the Middleware class attributes.
     * @return dao for particular database type.
     */
    public static MiddlewareDAO getMiddlewareDAO() { return new MySqlMiddlewareDAO(); }

    /**
     * Gives the data access object class for the HLA object.
     * @return dao for particular database type.
     */
    public static HLADAO getHLADAO() {
        return new MySqlHLADao();
    }

}
