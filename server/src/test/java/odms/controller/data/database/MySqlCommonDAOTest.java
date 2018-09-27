package odms.controller.data.database;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.time.LocalDate;
import odms.commons.model.profile.Profile;
import odms.server.CommonTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.model.database.DAOFactory;
import server.model.database.common.CommonDAO;
import server.model.database.profile.ProfileDAO;

/**
 * MySqlCommonDao Tests.
 */
public class MySqlCommonDAOTest extends CommonTestUtils {
    private CommonDAO commonDAO = DAOFactory.getCommonDao();
    private ProfileDAO profileDAO = DAOFactory.getProfileDao();

    private PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();


    private String expectedBadOutput = "Please enter a valid read-only query.";

    private Profile testProfile0;

    @Before
    public void setup() throws SQLException {
        testProfile0 = new Profile(
                "Joshua",
                "Wyllie",
                LocalDate.of(1997, 7, 18),
                "ABC1234"
        );
        profileDAO.add(testProfile0);
        testProfile0 = profileDAO.get(testProfile0.getNhi());
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStream() throws SQLException {
        System.setOut(originalOut);
        profileDAO.remove(testProfile0);
    }

    @Test
    public void testNotReadOnlyQuery() {
        String query = "DROP TABLE countries";
        commonDAO.queryDatabase(query);
        assertEquals(expectedBadOutput, outContent.toString().trim());
    }

    @Test
    public void testReadOnlyQuery() {
        String expectedGoodOutput = String.format(
                "+--------------------------+--------------------------------------------------------+----------------------------+----------------------------+------------------------------------+--------------------------------------------------------+-------------------------+%n"
                        +
                        "| NHI                      | GivenNames                                             | Height                     | Weight                     | Gender                             | Lastnames                                              | Dod                     |%n"
                        +
                        "+--------------------------+--------------------------------------------------------+----------------------------+----------------------------+------------------------------------+--------------------------------------------------------+-------------------------+%n"
                        +
                        "| ABC1234                  | Joshua                                                 | 0.0                        | 0.0                        | null                               | Wyllie                                                 | null                    |%n"
                        +
                        "+--------------------------+--------------------------------------------------------+----------------------------+----------------------------+------------------------------------+--------------------------------------------------------+-------------------------+"
        );
        String query = "SELECT NHI, " +
                "GivenNames, " +
                "Height, " +
                "Weight, " +
                "Gender, " +
                "Lastnames, " +
                "dod " +
                "FROM profiles";
        commonDAO.queryDatabase(query);
        assertEquals(expectedGoodOutput, outContent.toString().trim());
    }

    @Test
    public void TestMalformedQuery() {
        String query = "SELECT * FROM tim_likes_cookies";
        commonDAO.queryDatabase(query);
        assertEquals(expectedBadOutput, outContent.toString().trim());
    }
}
