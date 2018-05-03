package odms.data;

import static org.junit.Assert.assertEquals;

import odms.user.User;
import odms.user.UserType;
import org.junit.Before;
import org.junit.Test;

public class UserDataIOTest {

    private UserDatabase userDb;
    private User user1;
    private User user2;

    @Before
    public void setup() {
        userDb = new UserDatabase();

        user1 = new User(UserType.CLINICIAN, "John Smith","Christchurch");
        user2 = new User(UserType.CLINICIAN, "Matt Smith", "Auckland");
        userDb.addClinician(user1);
        userDb.addClinician(user2);
    }

    @Test
    public void testSaveAndLoad() {
        UserDatabase loadedDb;
        UserDataIO.saveUsers(userDb, "example/users.json");
        loadedDb = UserDataIO.loadData("example/users.json");
        assertEquals("John Smith", userDb.getClinician(0).getName());
    }
}
