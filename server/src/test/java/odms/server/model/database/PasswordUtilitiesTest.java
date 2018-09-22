package odms.server.model.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.junit.BeforeClass;
import org.junit.Test;
import server.model.database.PasswordUtilities;

public class PasswordUtilitiesTest {

    static String PASSWORD = "password";
    static String hashedPassword;

    /**
     * Creates a hashed password to be used in the tests. Saves computing power.
     */
    @BeforeClass
    public static void setup() throws InvalidKeySpecException, NoSuchAlgorithmException {
        hashedPassword = PasswordUtilities.getSaltedHash(PASSWORD);
    }

    @Test
    public void testGetSaltedHash() {
        assertEquals(2, hashedPassword.split("\\$").length);
    }

    @Test
    public void testCheckInvalid() throws InvalidKeySpecException, NoSuchAlgorithmException {
        Boolean valid = PasswordUtilities.check(PASSWORD, "ye$et");
        assertFalse(valid);
    }

    @Test (expected = IllegalStateException.class)
    public void testCheckEmpty() throws InvalidKeySpecException, NoSuchAlgorithmException {
        PasswordUtilities.check(PASSWORD, "");
    }

    @Test
    public void testCheckValid() throws InvalidKeySpecException, NoSuchAlgorithmException {
        assertTrue(PasswordUtilities.check(PASSWORD, hashedPassword));
    }

    @Test
    public void testCheckHash() throws InvalidKeySpecException, NoSuchAlgorithmException {
        assertNotEquals(hashedPassword, PasswordUtilities.getSaltedHash(PASSWORD));
    }

    @Test
    public void testPasswordHashed() {
        assertNotEquals(hashedPassword, PASSWORD);
    }

}
