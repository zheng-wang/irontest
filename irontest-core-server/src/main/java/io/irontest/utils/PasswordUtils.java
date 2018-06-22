package io.irontest.utils;

import io.irontest.core.HashedPassword;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import static io.irontest.IronTestConstants.*;

public final class PasswordUtils {
    private static SecureRandom random = new SecureRandom();

    /**
     * Hash the password with a randomly generated salt.
     * @param password
     * @return
     */
    public static HashedPassword hashPassword(String password) {
        byte[] salt = new byte[PASSWORD_SALT_LENGTH_IN_BYTES];
        random.nextBytes(salt);
        String hashedPassword = hashPassword(password, salt);
        return new HashedPassword(hashedPassword, Base64.encodeBase64String(salt));
    }

    /**
     * Hash the password with a provided salt.
     * @param password
     * @param base64EncodedSalt
     * @return
     */
    public static String hashPassword(String password, String base64EncodedSalt) {
        byte[] salt = Base64.decodeBase64(base64EncodedSalt);
        return hashPassword(password, salt);
    }

    private static String hashPassword(String password, byte[] salt) {
        SecretKey secretKey = null;
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(DEFAULT_PASSWORD_HASHING_ALGORITHM);
            PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, DEFAULT_KDF_ITERATIONS, KDF_KEY_LENGTH);
            secretKey = secretKeyFactory.generateSecret(keySpec);
        } catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        byte[] derivedKey = secretKey.getEncoded();
        return Base64.encodeBase64String(derivedKey);
    }
}
