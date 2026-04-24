package com.campustrade.userservice.security;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class PasswordHasher {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String ENCODING_PREFIX = "pbkdf2";
    private static final int DEFAULT_ITERATIONS = 120000;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 256;

    private final SecureRandom secureRandom = new SecureRandom();

    public String hash(String rawPassword) {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        byte[] digest = pbkdf2(rawPassword.toCharArray(), salt, DEFAULT_ITERATIONS, KEY_LENGTH);

        return ENCODING_PREFIX
                + "$" + DEFAULT_ITERATIONS
                + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(digest);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }

        String[] parts = encodedPassword.split("\\$");
        if (parts.length != 4 || !ENCODING_PREFIX.equals(parts[0])) {
            return false;
        }

        int iterations;
        try {
            iterations = Integer.parseInt(parts[1]);
        } catch (NumberFormatException ex) {
            return false;
        }

        byte[] salt;
        byte[] expectedDigest;
        try {
            salt = Base64.getDecoder().decode(parts[2]);
            expectedDigest = Base64.getDecoder().decode(parts[3]);
        } catch (IllegalArgumentException ex) {
            return false;
        }

        byte[] actualDigest = pbkdf2(rawPassword.toCharArray(), salt, iterations, expectedDigest.length * 8);
        return MessageDigest.isEqual(expectedDigest, actualDigest);
    }

    private byte[] pbkdf2(char[] rawPassword, byte[] salt, int iterations, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(rawPassword, salt, iterations, keyLength);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            return keyFactory.generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Password hashing failed.", ex);
        }
    }
}
