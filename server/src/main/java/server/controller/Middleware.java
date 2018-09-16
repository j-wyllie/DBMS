package server.controller;

import java.util.Random;

public class Middleware {

    public Middleware() {
        throw new UnsupportedOperationException();
    }

    public static long authenticateUser(int userId) {
        long token = generateToken();

        return token;
    }

    public static long authenticateProfile(int profileId) {
        long token = generateToken();
        return token;
    }

    public static void logout(String username, int token) {

    }

    private static long generateToken() {
        Random random = new Random();
        return random.nextLong();
    }
}
