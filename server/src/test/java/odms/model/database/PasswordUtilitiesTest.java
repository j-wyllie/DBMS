package odms.model.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.junit.Test;
import server.model.database.PasswordUtilities;

public class PasswordUtilitiesTest {

    String PASSWORD = "password";

    @Test
    public void testGetSaltedHash() throws InvalidKeySpecException, NoSuchAlgorithmException {
        String hashedPassword = PasswordUtilities.getSaltedHash(PASSWORD);
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
        Boolean valid = PasswordUtilities.check(PASSWORD, PasswordUtilities.getSaltedHash(PASSWORD));
        assertTrue(valid);
    }

    @Test
    public void testCheckHash() throws InvalidKeySpecException, NoSuchAlgorithmException {
        assertNotEquals(PasswordUtilities.getSaltedHash(PASSWORD), PasswordUtilities.getSaltedHash(PASSWORD));
    }

}
