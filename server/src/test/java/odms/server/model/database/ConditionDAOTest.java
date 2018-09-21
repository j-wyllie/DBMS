package odms.server.model.database;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import odms.commons.model.profile.Condition;
import odms.commons.model.profile.Profile;
import odms.server.CommonTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.model.database.DAOFactory;
import server.model.database.condition.ConditionDAO;
import server.model.database.profile.ProfileDAO;

public class ConditionDAOTest extends CommonTestUtils {

    private Profile testProfile1;
    private Profile testProfile2;
    private Condition condition1;
    private Condition condition2;

    ConditionDAO conditionDao = DAOFactory.getConditionDao();
    ProfileDAO profileDao = DAOFactory.getProfileDao();

    @Before
    public void setup() throws SQLException {
        condition1 = new Condition(1, "HIV", LocalDate.now(), false, true, LocalDate.now());
        condition2 = new Condition(2, "HIB", LocalDate.now(), false, true, LocalDate.now());

        testProfile1 = new Profile("Jack", "Haaay", LocalDate.of(1998, 2, 27), "ABC1234");
        testProfile2 = new Profile("Tim", "Hamb-lame", LocalDate.of(1998, 2, 27), "ABC2345");

        profileDao.add(testProfile1);
        testProfile1 = profileDao.get("ABC1234");

        profileDao.add(testProfile2);
        testProfile2 = profileDao.get("ABC2345");
        conditionDao.add(testProfile2.getId(), condition2);

        condition2 = conditionDao.getAll(testProfile2.getId(), true).get(0);
    }

    @After
    public void tearDown() throws SQLException {
        List<Condition> allConditions = new ArrayList<>();
        allConditions.addAll(conditionDao.getAll(testProfile1.getId(), true));
        allConditions.addAll(conditionDao.getAll(testProfile2.getId(), true));

        if (!allConditions.isEmpty()) {
            for (Condition condition : allConditions) {
                conditionDao.remove(condition);
            }
        }

        profileDao.remove(testProfile1);
        profileDao.remove(testProfile2);
    }

    @Test
    public void testAddCondition() {
        conditionDao.add(testProfile1.getId(), condition1);
        condition1 = conditionDao.getAll(testProfile1.getId(), true).get(0);
        assertEquals(1, conditionDao.getAll(testProfile1.getId(), true).size());
    }

    @Test
    public void testGetAll() {
        assertEquals(1, conditionDao.getAll(testProfile2.getId(), true).size());
    }

    @Test
    public void testRemoveCondition() {
        conditionDao.remove(condition2);
        assertEquals(0, conditionDao.getAll(testProfile2.getId(), true).size());
    }

    @Test
    public void testConditionUpdate() {
        condition2.setName("Psyc");
        conditionDao.update(condition2);
        assertEquals("Psyc", conditionDao.getAll(testProfile2.getId(), true).get(0).getName());
    }

}
