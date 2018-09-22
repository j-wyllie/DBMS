package odms.controller.data.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.List;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.enums.UserType;
import odms.commons.model.profile.Profile;
import odms.commons.model.user.User;
import odms.commons.model.user.UserNotFoundException;
import odms.server.CommonTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.model.database.DAOFactory;
import server.model.database.profile.ProfileDAO;
import server.model.database.settings.MySqlSettingsDAO;
import server.model.database.settings.SettingsDAO;
import server.model.database.user.UserDAO;

public class MySqlSettingsDAOTest extends CommonTestUtils {

    // Data access object variables.
    private SettingsDAO settingsDAO = DAOFactory.getSettingsDAO();
    private ProfileDAO profileDAO = DAOFactory.getProfileDao();
    private UserDAO userDAO = DAOFactory.getUserDao();


    // Profile variables.
    private Profile profileA;
    private LocalDate genericDate = LocalDate.of(1998, 3, 3);

    // User variables.
    private User userA;

    // General variables.
    private int invalidId = 0;

    // Locale variables.
    private String localeA = "en-US";


    @Before
    public void setup() throws SQLException, UserNotFoundException {
        settingsDAO.updateCountries(CountriesEnum.NZ, false);

        profileA = new Profile("Alice", "Smith",
                genericDate, "LPO7236");
        profileDAO.add(profileA);
        profileA = profileDAO.get(profileA.getNhi());

        userA = new User(UserType.ADMIN, "Brooke", "Canterbury");
        userA.setUsername("brooker");
        userDAO.add(userA);
        userA = userDAO.get(userA.getUsername());
    }

    @Test
    public void testGetAll() {
        List<String> countries = settingsDAO.getAllCountries();
        // Changes this if our list of countries changes
        assertEquals(201, countries.size());
    }

    @Test
    public void testGetAllValid() {
        List<String> countries = settingsDAO.getAllCountries(true);
        assertEquals(settingsDAO.getAllCountries().size() - 1, countries.size());
    }

    @Test
    public void testGetAllInvalid() {
        List<String> countries = settingsDAO.getAllCountries(false);
        assertEquals(1, countries.size());
    }

    @Test
    public void testUpdate() {
        settingsDAO.updateCountries(CountriesEnum.NZ, true);
        List<String> countries = settingsDAO.getAllCountries(false);
        assertEquals(0, countries.size());
    }

    @Test
    public void testPopulateCountriesTable() {
        settingsDAO.populateCountriesTable();
        assertEquals(201, settingsDAO.getAllCountries().size());
    }

    @Test (expected = SQLIntegrityConstraintViolationException.class)
    public void setProfileDateTimeFormatInvalidId() throws SQLException {
        settingsDAO.setDateTimeFormat(invalidId, UserType.PROFILE, localeA);
        assertNull(settingsDAO.getDateTimeFormat(invalidId, UserType.PROFILE));
    }

    @Test (expected = SQLIntegrityConstraintViolationException.class)
    public void setUserDateTimeFormatInvalidId() throws SQLException {
        settingsDAO.setDateTimeFormat(invalidId, UserType.ADMIN, localeA);
        assertNull(settingsDAO.getDateTimeFormat(invalidId, UserType.ADMIN));
    }

    @Test (expected = SQLIntegrityConstraintViolationException.class)
    public void setProfileNumberFormatInvalidId() throws SQLException {
        settingsDAO.setNumberFormat(invalidId, UserType.PROFILE, localeA);
        assertNull(settingsDAO.getNumberFormat(invalidId, UserType.PROFILE));
    }

    @Test (expected = SQLIntegrityConstraintViolationException.class)
    public void setUserNumberFormatInvalidId() throws SQLException {
        settingsDAO.setNumberFormat(invalidId, UserType.ADMIN, localeA);
        assertNull(settingsDAO.getNumberFormat(invalidId, UserType.ADMIN));
    }

    @Test
    public void getSetProfileDateTimeFormatValid() throws SQLException {
        settingsDAO.setDateTimeFormat(profileA.getId(), UserType.PROFILE, localeA);
        assertEquals(localeA, settingsDAO.getDateTimeFormat(profileA.getId(), UserType.PROFILE));
    }

    @Test
    public void getSetProfileDateTimeFormatInvalid() {
        assertNull(settingsDAO.getDateTimeFormat(profileA.getId(), UserType.PROFILE));
    }

    @Test
    public void getSetUserDateTimeFormatValid() throws SQLException {
        settingsDAO.setDateTimeFormat(userA.getStaffID(), UserType.ADMIN, localeA);
        assertEquals(localeA, settingsDAO.getDateTimeFormat(userA.getStaffID(), UserType.ADMIN));
    }

    @Test
    public void getSetUserDateTimeFormatInvalid() {
        assertNull(settingsDAO.getDateTimeFormat(userA.getStaffID(), UserType.ADMIN));
    }

    @Test
    public void getSetProfileNumberFormatValid() throws SQLException {
        settingsDAO.setNumberFormat(profileA.getId(), UserType.PROFILE, localeA);
        assertEquals(localeA, settingsDAO.getNumberFormat(profileA.getId(), UserType.PROFILE));
    }

    @Test
    public void getSetProfileNumberFormatInvalid() {
        assertNull(settingsDAO.getNumberFormat(profileA.getId(), UserType.PROFILE));
    }

    @Test
    public void getSetUserNumberFormatValid() throws SQLException {
        settingsDAO.setNumberFormat(userA.getStaffID(), UserType.ADMIN, localeA);
        assertEquals(localeA, settingsDAO.getNumberFormat(userA.getStaffID(), UserType.ADMIN));
    }

    @Test
    public void getSetUserNumberFormatInvalid() {
        assertNull(settingsDAO.getNumberFormat(userA.getStaffID(), UserType.ADMIN));
    }

    @After
    public void tearDown() throws SQLException {
        // Locale teardown.
        settingsDAO.deleteLocale(profileA.getId(), UserType.PROFILE);
        settingsDAO.deleteLocale(userA.getStaffID(), UserType.ADMIN);

        // Profile teardown.
        profileDAO.remove(profileA);

        // User teardown.
        userDAO.remove(userA);
    }
}
