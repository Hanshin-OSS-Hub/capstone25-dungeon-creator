package com.capstone.game_backend.global.util;

public class PasswordUtil {

    public static String hash(String raw) {
        return Integer.toHexString(raw.hashCode());
    }
}
