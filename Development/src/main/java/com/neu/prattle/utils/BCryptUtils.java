package com.neu.prattle.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * This class provides helper functions to hash/verify hash of a string.
 */
public class BCryptUtils {
    private static final int LOG_ROUNDS = 5;

    private BCryptUtils() {
    }

    /**
     * Hash the provided password.
     *
     * @param password - password to be hashed.
     * @return - hashed version of the password.
     */
    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(LOG_ROUNDS));
    }

    /**
     * Verify password string against a hash value provided.
     *
     * @param password - password to be validated.
     * @param hash     - hash value to be validated against.
     * @return - return true if the hash matches, false otherwise.
     */
    public static boolean verifyHash(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
