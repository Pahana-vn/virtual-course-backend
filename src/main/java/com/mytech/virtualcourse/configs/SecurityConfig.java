package com.mytech.virtualcourse.configs;

import com.mytech.virtualcourse.security.CustomOAuth2UserService;
import com.mytech.virtualcourse.security.OAuth2AuthenticationSuccessHandler;
import com.mytech.virtualcourse.security.CustomUserDetailsService;
import com.mytech.virtualcourse.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(
                            "http://localhost:3000",
                            "http://127.0.0.1:8080",
                            "http://10.0.2.2:8080",
                            "http://192.168.1.100:8080"
                    ));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Ensure stateless session
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/courses/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/courses/**").hasAuthority("ROLE_INSTRUCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/**").hasAuthority("ROLE_INSTRUCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**").hasAuthority("ROLE_INSTRUCTOR")
                        .requestMatchers("/api/categories/**").permitAll()
                        .requestMatchers("/api/files/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/instructors/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/instructors/**").hasAuthority("ROLE_INSTRUCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/instructors/**").hasAuthority("ROLE_INSTRUCTOR")
                        .requestMatchers("/api/sections/**").permitAll()
                        .requestMatchers("/api/students/**").hasAuthority("ROLE_STUDENT")
                        .requestMatchers("/api/payment/**").permitAll()
                        .requestMatchers("/api/transactions/**").permitAll()
                        .requestMatchers("/api/wallet/**").permitAll()
                        .requestMatchers("/api/instructor-transaction/**").permitAll()
                        .requestMatchers("/api/tests/**").permitAll()
                        .requestMatchers("/api/questions/**").permitAll()
                        .requestMatchers("/swagger-ui/**","/swagger-resources/**","/v3/api-docs/**", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
//                .oauth2Login(oauth2 -> oauth2
//                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
//                        .successHandler(oAuth2AuthenticationSuccessHandler)
//                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
