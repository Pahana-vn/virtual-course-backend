package com.mytech.virtualcourse.configs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000") // Đảm bảo chỉ định đúng URL frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*");
            }
        };
    }
}

// src/main/java/com/mytech/virtualcourse/configs/CorsConfig.java
//package com.mytech.virtualcourse.configs;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.*;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // Sử dụng non-reactive
//
//import java.util.Arrays;
//
//@Configuration
//public class CorsConfig {
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        // Cho phép origin từ frontend React
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
//
//        // Cho phép các phương thức HTTP
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//
//        // Cho phép các header cần thiết
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
//
//        // Cho phép gửi credentials (cookies, authorization headers, ...)
//        configuration.setAllowCredentials(true);
//
//        // Expose headers nếu cần (ví dụ: Authorization)
//        configuration.setExposedHeaders(Arrays.asList("Authorization"));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}
