package odms.controller.data.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;
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

    // Data access object varibles.
    private SettingsDAO settingsDAO = DAOFactory.getSettingsDAO();
    private ProfileDAO profileDAO = DAOFactory.getProfileDao();
    private UserDAO userDAO = DAOFactory.getUserDao();


    // Profile variables.
    private Profile profileA;
    private Profile profileB;
    private Profile profileC;
    private LocalDate genericDate = LocalDate.of(1998, 3, 3);

    // User variables.
    private User userA;
    private User userB;
    private User userC;


    @Before
    public void setup() throws SQLException, UserNotFoundException {
        settingsDAO.updateCountries(CountriesEnum.NZ, false);

        profileA = new Profile("Alice", "Smith",
                genericDate, "LPO7236");
        profileDAO.add(profileA);
        profileA = profileDAO.get(profileA.getNhi());

        profileB = new Profile("Ben", "Smith",
                genericDate, "YSL9939");
        profileDAO.add(profileB);
        profileB = profileDAO.get(profileB.getNhi());

        profileC = new Profile("Bob", "Marshall",
                genericDate, "GSK726");
        profileDAO.add(profileC);
        profileC = profileDAO.get(profileC.getNhi());

        userA = new User(UserType.ADMIN, "Brooke", "Canterbury");
        userA.setUsername("brooker");
        userDAO.add(userA);
        userA = userDAO.get(userA.getUsername());

        userB = new User(UserType.CLINICIAN, "Tim", "Hamblin");
        userB.setUsername("timh");
        userDAO.add(userB);
        userB = userDAO.get(userB.getUsername());

        userC = new User(UserType.CLINICIAN, "Josh", "Wyllie");
        userC.setUsername("joshw");
        userDAO.add(userC);
        userC = userDAO.get(userC.getUsername());

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

    @Test
    public void getProfileDateTimeFormatValid() throws SQLException {
        assertNull(settingsDAO.getDateTimeFormat(profileA.getId(), UserType.PROFILE));
    }

    @Test
    public void getProfileDateTimeFormatInvalid() {

    }

    @Test
    public void getUserDateTimeFormatValid() {

    }

    @Test
    public void getUserDateTimeFormatInvalid() {

    }

    @Test
    public void getProfileNumberFormatValid() {

    }

    @Test
    public void getProfileNumberFormatInvalid() {

    }

    @Test
    public void getUserNumberFormatValid() {

    }

    @Test
    public void getUserNumberFormatInvalid() {

    }

    @Test
    public void setProfileDateTimeFormatValid() {

    }

    @Test
    public void setProfileDateTimeFormatInvalid() {

    }

    @Test
    public void setUserDateTimeFormatValid() {

    }

    @Test
    public void setUserDateTimeFormatInvalid() {

    }

    @Test
    public void setProfileNumberFormatValid() {

    }

    @Test
    public void setProfileNumberFormatInvalid() {

    }

    @Test
    public void setUserNumberFormatValid() {

    }

    @Test
    public void setUserNumberFormatInvalid() {

    }

    @After
    public void tearDown() throws SQLException {
        // Profile teardown.
        profileDAO.remove(profileA);
        profileDAO.remove(profileB);
        profileDAO.remove(profileC);

        // User teardown.
        userDAO.remove(userA);
        userDAO.remove(userB);
        userDAO.remove(userC);

    }
}
