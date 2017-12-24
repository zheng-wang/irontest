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

/**
 * Created by Zheng on 24/12/2017.
 */
public class PasswordUtils {
    private static SecureRandom random = new SecureRandom();

    public static HashedPassword hashPassword(String password) {
        byte[] salt = new byte[PASSWORD_SALT_LENGTH_IN_BYTES];
        random.nextBytes(salt);
        SecretKey secretKey = null;
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(PASSWORD_HASHING_ALGORITHM);
            PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, KDF_ITERATIONS, KDF_KEY_LENGTH);
            secretKey = secretKeyFactory.generateSecret(keySpec);
        } catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        byte[] derivedKey = secretKey.getEncoded();
        return new HashedPassword(Base64.encodeBase64String(derivedKey), Base64.encodeBase64String(salt));
    }
}
