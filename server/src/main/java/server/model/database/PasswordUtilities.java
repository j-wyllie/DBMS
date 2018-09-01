package server.model.database;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 * Used to hash passwords.
 */
public final class PasswordUtilities {

    // The higher the number of ITERATIONS the more
    // expensive computing the hash is for us and
    // also for an attacker.
    private static final int ITERATIONS = 20 * 1000;
    private static final int SALT_LEN = 32;
    private static final int DESIRED_KEY_LEN = 256;

    /**
     * Private constructor that isn't called for checkstyle rules.
     */
    private PasswordUtilities() {
        //not called
    }

    /**
     * Computes a salted PBKDF2 hash of given plaintext password
     * suitable for storing in a database.
     * Empty passwords are not supported.
     * @param password the password to get hashed.
     * @throws NoSuchAlgorithmException thrown when algorithm not found.
     * @throws InvalidKeySpecException .
     * @return hashed password.
     */
    public static String getSaltedHash(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(SALT_LEN);
        // store the salt with the password
        return Base64.encodeBase64String(salt) + "$" + hash(password, salt);
    }

    /**
     * Checks whether given plaintext password corresponds
     * to a stored salted hash of the password.
     * @param password password to be checked that they entered.
     * @param stored password stored in db.
     * @return true if passwords match.
     * @throws InvalidKeySpecException .
     * @throws NoSuchAlgorithmException .
     */
    public static boolean check(String password, String stored)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        String[] saltAndPass = stored.split("\\$");
        if (saltAndPass.length != 2) {
            throw new IllegalStateException(
                    "The stored password have the form 'salt$hash'");
        }
        String hashOfInput = hash(password, Base64.decodeBase64(saltAndPass[0]));
        return hashOfInput.equals(saltAndPass[1]);
    }

    /**
     * Hashes a password.
     * @param password password to be hashed.
     * @param salt complimentary salt.
     * @return the hashed password.
     * @throws NoSuchAlgorithmException .
     * @throws InvalidKeySpecException .
     */
    private static String hash(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (password == null || password.length() == 0) {
            throw new IllegalArgumentException("Empty passwords are not supported.");
        }
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(
                password.toCharArray(), salt, ITERATIONS, DESIRED_KEY_LEN)
        );
        return Base64.encodeBase64String(key.getEncoded());
    }
}
