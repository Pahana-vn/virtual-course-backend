package com.mytech.virtualcourse.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    private static final int ONE_DAY = 24 * 60 * 60; // 1 ngày tính bằng giây

    // Hàm tạo cookie token
    public static void addTokenCookie(HttpServletResponse response, String jwt) {
        Cookie cookie = new Cookie("token", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);   // ⚠ Nếu deploy trên HTTPS, hãy set true
        cookie.setPath("/");
        cookie.setMaxAge(ONE_DAY);
        response.addCookie(cookie);
    }

    // Hàm xóa cookie token (khi logout)
    public static void clearTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // Set maxAge = 0 để xóa ngay lập tức
        response.addCookie(cookie);
    }
}
