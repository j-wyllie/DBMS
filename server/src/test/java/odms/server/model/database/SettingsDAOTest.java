package odms.server.model.database;

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

public class SettingsDAOTest extends CommonTestUtils {

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
    private int invalidId = -1;

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
        userA.setPassword("test");
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
    public void testSetProfileDateTimeFormatInvalidId() throws SQLException {
        settingsDAO.setDateTimeFormat(invalidId, UserType.PROFILE, localeA);
        assertNull(settingsDAO.getDateTimeFormat(invalidId, UserType.PROFILE));
    }

    @Test (expected = SQLIntegrityConstraintViolationException.class)
    public void testSetUserDateTimeFormatInvalidId() throws SQLException {
        settingsDAO.setDateTimeFormat(invalidId, UserType.ADMIN, localeA);
        assertNull(settingsDAO.getDateTimeFormat(invalidId, UserType.ADMIN));
    }

    @Test (expected = SQLIntegrityConstraintViolationException.class)
    public void testSetProfileNumberFormatInvalidId() throws SQLException {
        settingsDAO.setNumberFormat(invalidId, UserType.PROFILE, localeA);
        assertNull(settingsDAO.getNumberFormat(invalidId, UserType.PROFILE));
    }

    @Test (expected = SQLIntegrityConstraintViolationException.class)
    public void testSetUserNumberFormatInvalidId() throws SQLException {
        settingsDAO.setNumberFormat(invalidId, UserType.ADMIN, localeA);
        assertNull(settingsDAO.getNumberFormat(invalidId, UserType.ADMIN));
    }

    @Test
    public void testGetSetProfileDateTimeFormatValid() throws SQLException {
        settingsDAO.setDateTimeFormat(profileA.getId(), UserType.PROFILE, localeA);
        assertEquals(localeA, settingsDAO.getDateTimeFormat(profileA.getId(), UserType.PROFILE));
    }

    @Test
    public void testGetSetProfileDateTimeFormatInvalid() {
        assertNull(settingsDAO.getDateTimeFormat(profileA.getId(), UserType.PROFILE));
    }

    @Test
    public void testGetSetUserDateTimeFormatValid() throws SQLException {
        settingsDAO.setDateTimeFormat(userA.getId(), UserType.ADMIN, localeA);
        assertEquals(localeA, settingsDAO.getDateTimeFormat(userA.getId(), UserType.ADMIN));
    }

    @Test
    public void testGetSetUserDateTimeFormatInvalid() {
        assertNull(settingsDAO.getDateTimeFormat(userA.getId(), UserType.ADMIN));
    }

    @Test
    public void testGetSetProfileNumberFormatValid() throws SQLException {
        settingsDAO.setNumberFormat(profileA.getId(), UserType.PROFILE, localeA);
        assertEquals(localeA, settingsDAO.getNumberFormat(profileA.getId(), UserType.PROFILE));
    }

    @Test
    public void testGetSetProfileNumberFormatInvalid() {
        assertNull(settingsDAO.getNumberFormat(profileA.getId(), UserType.PROFILE));
    }

    @Test
    public void testGetSetUserNumberFormatValid() throws SQLException {
        settingsDAO.setNumberFormat(userA.getId(), UserType.ADMIN, localeA);
        assertEquals(localeA, settingsDAO.getNumberFormat(userA.getId(), UserType.ADMIN));
    }

    @Test
    public void testGetSetUserNumberFormatInvalid() {
        assertNull(settingsDAO.getNumberFormat(userA.getId(), UserType.ADMIN));
    }

    @After
    public void tearDown() throws SQLException {
        // Locale teardown.
        settingsDAO.deleteLocale(profileA.getId(), UserType.PROFILE);
        settingsDAO.deleteLocale(userA.getId(), UserType.ADMIN);

        // Profile teardown.
        profileDAO.remove(profileA);

        // User teardown.
        userDAO.remove(userA);
    }
}
