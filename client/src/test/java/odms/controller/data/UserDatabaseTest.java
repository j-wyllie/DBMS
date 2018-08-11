package odms.controller.data;

import odms.controller.user.UserNotFoundException;
import odms.data.UserDatabase;
import odms.commons.model.user.User;
import odms.commons.model.enums.UserType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserDatabaseTest {

    private UserDatabase userDb;
    private User user1;
    private User user2;

    @Before
    public void setup() {
        userDb = new UserDatabase();

        user1 = new User(UserType.CLINICIAN, "John Smith", "Christchurch");
        user2 = new User(UserType.CLINICIAN, "Matt Smith", "Auckland");
    }

    @Test
    public void testAddUser() throws UserNotFoundException {
        userDb.addUser(user1);
        assertEquals("John Smith", userDb.getUser(0).getName());
        assertEquals("Christchurch", userDb.getUser(0).getRegion());

    }
}
