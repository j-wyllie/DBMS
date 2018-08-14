package odms.controller.data.database;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.time.LocalDate;
import odms.commons.model.profile.Condition;
import odms.commons.model.profile.Profile;
import odms.controller.database.condition.MySqlConditionDAO;
import odms.controller.database.profile.MySqlProfileDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MySqlConditionDaoTest extends MySqlCommonTests {

    private Profile testProfile1;
    private Profile testProfile2;
    Condition condition1;
    Condition condition2;

    MySqlConditionDAO mySqlConditionDAO = new MySqlConditionDAO();
    MySqlProfileDAO mySqlProfileDAO = new MySqlProfileDAO();


    @Before
    public void setup() throws SQLException {
        condition1 = new Condition(1, "HIV", LocalDate.now(), false, true, LocalDate.now());
        condition2 = new Condition(2, "HIB", LocalDate.now(), false, true, LocalDate.now());

        testProfile1 = new Profile("Jack", "Haaay", LocalDate.of(1998, 2, 27), "ABC1234");
        testProfile2 = new Profile("Tim", "Hamb-lame", LocalDate.of(1998, 2, 27), "ABC2345");

        mySqlProfileDAO.add(testProfile1);
        testProfile1 = mySqlProfileDAO.get("ABC1234");

        mySqlProfileDAO.add(testProfile2);
        testProfile2 = mySqlProfileDAO.get("ABC2345");
        mySqlConditionDAO.add(testProfile2, condition2);
        condition2 = mySqlConditionDAO.getAll(testProfile2, true).get(0);
    }

    @Test
    public void testAddCondition() {
        mySqlConditionDAO.add(testProfile1, condition1);
        condition1 = mySqlConditionDAO.getAll(testProfile1, true).get(0);
        assertEquals(1, mySqlConditionDAO.getAll(testProfile1, true).size());
    }

    @Test
    public void testGetAll() {
        assertEquals(1, mySqlConditionDAO.getAll(testProfile2, true).size());
    }

    @Test
    public void testRemoveCondition() {
        mySqlConditionDAO.remove(condition2);
        assertEquals(0, mySqlConditionDAO.getAll(testProfile2, true).size());
    }

    @Test
    public void testConditionUpdate() {
        condition2.setName("Psyc");
        mySqlConditionDAO.update(condition2);
        assertEquals("Psyc", mySqlConditionDAO.getAll(testProfile2, true).get(0).getName());
    }

    @After
    public void tearDown() throws SQLException {
        mySqlConditionDAO.remove(condition2);
        mySqlConditionDAO.remove(condition1);

        mySqlProfileDAO.remove(testProfile1);

        mySqlProfileDAO.remove(testProfile2);
    }
}
