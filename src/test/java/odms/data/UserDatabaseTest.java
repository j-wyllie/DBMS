package odms.data;

import static org.junit.Assert.assertEquals;

import odms.user.User;
import odms.user.UserType;
import org.junit.Before;
import org.junit.Test;

public class UserDatabaseTest {

    private UserDatabase userDb;
    private User user1;
    private User user2;

    @Before
    public void setup() {
        userDb = new UserDatabase();

        user1 = new User(UserType.CLINICIAN, "John Smith","Christchurch");
        user2 = new User(UserType.CLINICIAN, "Matt Smith", "Auckland");
    }

    @Test
    public void testAddUser() {
            userDb.addUser(user1);
            assertEquals("John Smith", userDb.getUser(0).getName());
            assertEquals("Christchurch", userDb.getUser(0).getRegion());

    }
}
