package com.mytech.virtualcourse.security;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtUtil jwtUtil;

    public CustomHandshakeHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // Lấy token từ query parameter
        String query = request.getURI().getQuery();
        if (query == null || !query.contains("token=")) {
            return null; // Không có token, từ chối kết nối
        }

        String token = query.split("token=")[1];
        if (token != null && jwtUtil.validateJwtToken(token)) {
            String username = jwtUtil.getUsernameFromJwtToken(token);
            Long accountId = jwtUtil.getAccountIdFromJwtToken(token);
            Long studentId = jwtUtil.getStudentIdFromJwtToken(token);
            Long instructorId = jwtUtil.getInstructorIdFromJwtToken(token);
            List<String> roles = jwtUtil.getRolesFromJwtToken(token);
            logger.info("Token: " + token);
            logger.info("Username: " + username);

            return new CustomPrincipal(username, accountId, studentId, instructorId, roles);
        }

        return null; // Token không hợp lệ, từ chối kết nối
    }
}