package com.mytech.virtualcourse.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String jwt = jwtUtil.generateJwtToken(userDetails);

        // Determine the target URL (frontend URL to redirect with token)
        String targetUrl = determineTargetUrl(request, response, jwt);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        // Redirect to the target URL with the JWT token as a query parameter
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, String token) {
        // Optionally, you can get the redirect URI from a request parameter
        String redirectUri = request.getParameter("redirect_uri");
        if (StringUtils.hasText(redirectUri)) {
            return UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("token", token)
                    .build().toUriString();
        }
        // Default redirect if no redirect_uri is provided
        return "/";
    }
}
