package com.crm.auth.util;

public class JwtTokenHolder {
    private static final ThreadLocal<String> jwtToken = new ThreadLocal<>();

    public static void setToken(String token) {
        jwtToken.set(token);
    }

    public static String getToken() {
        return jwtToken.get();
    }

    public static void clear() {
        jwtToken.remove();
    }
}

