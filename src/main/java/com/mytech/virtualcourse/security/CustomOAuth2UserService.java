package com.mytech.virtualcourse.security;

import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Role;
import com.mytech.virtualcourse.enums.AuthenticationType;
import com.mytech.virtualcourse.enums.EAccountStatus;
import com.mytech.virtualcourse.repositories.AccountRepository;
import com.mytech.virtualcourse.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Delegate to the default OAuth2UserService to fetch user details
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Extract provider details
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = null;
        String name = null;

        if (registrationId.equalsIgnoreCase("google")) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        } else if (registrationId.equalsIgnoreCase("facebook")) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        }

        if (email == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_email"), "Email not found from OAuth2 provider");
        }

        // Check if the user already exists
        Optional<Account> userOpt = accountRepository.findByUsernameOrEmail(name, email);
        Account user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
            // Optionally update user details if necessary
        } else {
            // Create a new user
            user = new Account();
            user.setUsername(name);
            user.setEmail(email);
            user.setPassword(""); // No password as it's OAuth2
            user.setVerifiedEmail(true);
            user.setStatus(EAccountStatus.ACTIVE);
            user.setAuthenticationType(
                    registrationId.equalsIgnoreCase("google") ? AuthenticationType.GOOGLE : AuthenticationType.FACEBOOK
            );
            user.setVersion(1);

            // Assign default role (e.g., STUDENT)
            Role role = roleRepository.findByName("STUDENT")
                    .orElseThrow(() -> new RuntimeException("Error: Role STUDENT is not found."));
            user.setRoles(Collections.singletonList(role));

            accountRepository.save(user);
        }

        return new CustomOAuth2User(user, attributes);
    }
}
