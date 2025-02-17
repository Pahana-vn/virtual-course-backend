package com.mytech.virtualcourse.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    public String generateJwtToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        claims.put("accountId", userDetails.getAccountId());  // ✅ Thêm accountId
        claims.put("studentId", userDetails.getStudentId());  // ✅ Thêm studentId
        claims.put("instructorId", userDetails.getInstructorId());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // Thêm phương thức để lấy accountId từ JWT
    public Long getAccountIdFromJwtToken(String token) {
        Object accountId = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .get("accountId");
        return accountId != null ? Long.valueOf(accountId.toString()) : null;
    }

    // Thêm phương thức để lấy studentId từ JWT
    public Long getStudentIdFromJwtToken(String token) {
        Object studentId = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .get("studentId");
        return studentId != null ? Long.valueOf(studentId.toString()) : null;
    }

    public Long getInstructorIdFromJwtToken(String token) {
        Object instructorId = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .get("instructorId");
        return instructorId != null ? Long.valueOf(instructorId.toString()) : null;
    }

    public List<String> getRolesFromJwtToken(String token) {
        Object roles = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .get("roles");

        if (roles instanceof List<?>) {
            return ((List<?>) roles).stream()
                    .map(role -> ((Map<String, String>) role).get("authority")) // Lấy `authority`
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    // Get username from JWT
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            // Invalid JWT signature
            // Log hoặc xử lý theo ý bạn
        } catch (MalformedJwtException e) {
            // Invalid JWT token
        } catch (ExpiredJwtException e) {
            // JWT token is expired
        } catch (UnsupportedJwtException e) {
            // JWT token is unsupported
        } catch (IllegalArgumentException e) {
            // JWT claims string is empty
        }

        return false;
    }

    public String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }
}
