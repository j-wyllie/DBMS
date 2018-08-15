package odms.controller.data.data;

import odms.controller.data.UserDataIO;
import odms.model.data.UserDatabase;
import odms.model.user.User;
import odms.model.enums.UserType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserDataIOTest {

    private UserDatabase userDb;
    private User user1;
    private User user2;

    @Before
    public void setup() {
        userDb = new UserDatabase();

        user1 = new User(UserType.CLINICIAN, "John Smith", "Christchurch");
        user2 = new User(UserType.CLINICIAN, "Matt Smith", "Auckland");
        userDb.addUser(user1);
        userDb.addUser(user2);
    }

    @Test
    public void testSaveAndLoad() throws Exception {
        UserDatabase loadedDb;
        UserDataIO.saveUsers(userDb, "example/users.json");
        loadedDb = UserDataIO.loadData("example/users.json");
        assertEquals("John Smith", userDb.getUser(0).getName());
    }
}
