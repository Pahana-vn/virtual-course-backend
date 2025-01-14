// src/main/java/com/mytech/virtualcourse/configs/SecurityConfig.java

package com.mytech.virtualcourse.configs;

import com.mytech.virtualcourse.security.AccountDetailsServiceImpl;
import com.mytech.virtualcourse.security.AuthEntryPointJwt;
import com.mytech.virtualcourse.security.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity // Thay thế @EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final AuthEntryPointJwt unauthorizedHandler;
    private final AccountDetailsServiceImpl userDetailsService;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(); // Inject dependencies qua constructor nếu cần
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Bean để map GrantedAuthority mà không thêm tiền tố ROLE_
     */
    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        SimpleAuthorityMapper authorityMapper = new SimpleAuthorityMapper();
        authorityMapper.setConvertToUpperCase(true); // Chuyển đổi tên vai trò thành chữ in hoa
        authorityMapper.setPrefix(""); // Không thêm tiền tố (vì chúng ta đã thêm ở AccountDetailsImpl)
        return authorityMapper;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // Sử dụng AccountDetailsServiceImpl thay vì AccountDetailsImpl
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        // Sử dụng GrantedAuthoritiesMapper để không thêm tiền tố ROLE_ (đã thêm ở AccountDetailsImpl)
        authProvider.setAuthoritiesMapper(grantedAuthoritiesMapper());

        return authProvider;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Kích hoạt CORS và cấu hình Spring Security để sử dụng CorsConfigurationSource bean từ CorsConfig.java
            .cors(Customizer.withDefaults())
            // Tắt CSRF vì chúng ta sử dụng JWT
            .csrf(csrf -> csrf.disable())
            // Xử lý ngoại lệ
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(unauthorizedHandler))
            // Không sử dụng session, vì chúng ta sử dụng JWT
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Cấu hình quyền truy cập
            .authorizeHttpRequests(authorize -> authorize
                    // Các endpoint public
                    .requestMatchers(
                            "/api/auth/**",
                            "/swagger-ui/**",
                            "/swagger-resources/**",
                            "/v3/api-docs/**",
                            "/webjars/**",
                            "/uploads/**",
                            "/api/statistics/**",
                            "/api/roles/**",
                            "/actuator/health",
                            "/actuator/info"
                    ).permitAll()
                    // Các endpoint yêu cầu ADMIN
                    .requestMatchers("/actuator/**").hasRole("ADMIN")
                    .requestMatchers("/api/accounts/**").hasRole("ADMIN")
                    .requestMatchers("/api/instructors/**").hasRole("ADMIN")
                    .requestMatchers("/api/categories/**").hasRole("ADMIN")
                    .requestMatchers("/api/students/**").hasRole("ADMIN")
                    .requestMatchers("/api/courses/**").hasRole("ADMIN")
                    .requestMatchers("/api/wallets/**").hasRole("ADMIN")
                    .requestMatchers("/api/transactions/**").hasRole("ADMIN")
                    .requestMatchers("/api/tickets/**").hasRole("ADMIN")
                    .requestMatchers("/api/reviews/**").hasRole("ADMIN")
                    .requestMatchers("/api/notifications/**").hasRole("ADMIN")
                    // Các endpoint khác yêu cầu xác thực
                    .anyRequest().authenticated())
            // Đăng ký AuthenticationProvider
            .authenticationProvider(authenticationProvider());

        // Thêm JWT Token filter trước UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
