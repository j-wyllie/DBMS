package odms.model.data;

import java.util.Collection;
import odms.controller.user.UserNotFoundException;
import odms.model.user.User;
import odms.model.enums.UserType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserDatabaseTest {

    private UserDatabase userDb;
    private User clinician1;
    private User clinician2;
    private User admin1;

    @Before
    public void setup() {
        userDb = new UserDatabase();

        clinician1 = new User(UserType.CLINICIAN, "John Smith",
                "Christchurch", "username", "password");
        clinician2 = new User(UserType.CLINICIAN, "Matt Smith",
                "Auckland", "username2", "password");
        admin1 = new User(UserType.ADMIN, "Matt Smith",
                "Auckland", "username2", "password");
    }

    @Test
    public void testAddUser() throws UserNotFoundException {
        userDb.addUser(clinician1);
        assertEquals("John Smith", userDb.getUser(0).getName());
        assertEquals("Christchurch", userDb.getUser(0).getRegion());
    }

    @Test
    public void testCheckUniqueUser() {
        userDb.addUser(clinician1);
        assertTrue(userDb.checkUniqueUsername("username69"));
    }

    @Test
    public void testCheckDuplicateUser() {
        userDb.addUser(clinician1);
        assertFalse(userDb.checkUniqueUsername("username"));
    }

    @Test
    public void testSearchNamesInvalid() {
        userDb.addUser(clinician1);
        Collection<User> results = userDb.searchNames("John Ree");
        assertEquals(0, results.size());
    }

    @Test
    public void testSearchNamesValid() {
        userDb.addUser(clinician1);
        Collection<User> results = userDb.searchNames("John Smith");
        assertEquals(1, results.size());
    }

    @Test
    public void testSearchStaffIDInvalid() {
        clinician1.setStaffID(1);
        userDb.addUser(clinician1);
        Collection<User> results = userDb.searchStaffID(2);
        assertEquals(0, results.size());
    }

    @Test
    public void testSearchStaffIDValid() {
        clinician1.setStaffID(1);
        userDb.addUser(clinician1);
        Collection<User> results = userDb.searchStaffID(0);
        assertEquals(1, results.size());
    }

    @Test
    public void testGetUsersViaUsernameValid() throws UserNotFoundException {
        userDb.addUser(clinician1);
        userDb.addUser(clinician2);
        assertEquals(clinician1, userDb.getUser("username"));
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUsersViaUsernameInvalid() throws UserNotFoundException {
        userDb.addUser(clinician1);
        userDb.getUser("username2");
    }

    @Test
    public void testGetUsersViaIDValid() throws UserNotFoundException {
        userDb.addUser(clinician1);
        userDb.addUser(clinician2);
        assertEquals(clinician2, userDb.getUser(1));
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUsersViaIDInvalid() throws UserNotFoundException {
        userDb.addUser(clinician1);
        userDb.getUser(69);
    }

    @Test
    public void testIsUserViaUsernameValid() {
        userDb.addUser(clinician1);
        assertTrue(userDb.isUser("username"));
    }

    @Test
    public void testIsUserViaUsernameInvalid() {
        userDb.addUser(clinician1);
        assertFalse(userDb.isUser("username2"));
    }

    @Test
    public void testIsUserViaIDValid() {
        userDb.addUser(clinician1);
        assertTrue(userDb.isUser(0));
    }

    @Test
    public void testIsUserViaIDInvalid() {
        userDb.addUser(clinician1);
        assertFalse(userDb.isUser(69));
    }

    @Test
    public void testGetUsers() {
        userDb.addUser(clinician1);
        userDb.addUser(clinician2);
        assertEquals(2, userDb.getUsers().size());
    }

    @Test
    public void testGetUsersAsArrayList() {
        userDb.addUser(clinician1);
        userDb.addUser(clinician2);
        assertEquals(2, userDb.getUsersAsArrayList().size());
    }

    @Test
    public void testDeleteUserInvalid() {
        userDb.addUser(clinician1);
        userDb.addUser(clinician2);
        assertFalse(userDb.deleteUser(3));
    }

    @Test
    public void testDeleteUserValid() {
        userDb.addUser(clinician1);
        userDb.addUser(clinician2);
        assertTrue(userDb.deleteUser(1));
    }

    @Test
    public void testGetClinicians() {
        userDb.addUser(clinician1);
        userDb.addUser(clinician2);
        userDb.addUser(admin1);
        assertEquals(2, userDb.getClinicians().size());
    }

    @Test
    public void testSearchUsersInvalid() {
        userDb.addUser(clinician1);
        userDb.addUser(admin1);
        assertEquals(0, userDb.searchUsers("Triceratops").size());
    }

    @Test
    public void testSearchUsersValid() {
        userDb.addUser(clinician1);
        userDb.addUser(admin1);
        assertEquals(1, userDb.searchUsers("John Rith").size());
    }

    @Test
    public void testSearchUsersEmptySearch() {
        userDb.addUser(clinician1);
        userDb.addUser(clinician2);
        userDb.addUser(admin1);
        assertEquals(3, userDb.searchUsers("").size());
    }

    @Test
    public void testRestoreProfile() {
        userDb.addUser(clinician1);
        userDb.addUser(clinician2);
        userDb.deleteUser(1);
        userDb.restoreProfile(1, clinician1);
        assertEquals(2, userDb.getUsers().size());
    }
}
