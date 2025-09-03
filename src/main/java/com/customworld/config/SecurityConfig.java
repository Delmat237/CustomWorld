package com.customworld.config;

import com.customworld.security.JwtAuthenticationEntryPoint;
import com.customworld.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsFilter corsFilter;

    private static final String[] AUTH_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/api/auth/**"
    };

    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                         JwtAuthenticationFilter jwtAuthenticationFilter,
                         CorsFilter corsFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsFilter = corsFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class) // Ajouter CorsFilter avant
            .cors(cors -> cors.configure(http))
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Autoriser toutes les requêtes OPTIONS
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .requestMatchers("/api/payments/notify","/api/customer/products","/api/customer/context","/api/customer/categories","/api/customer/products/by-category").permitAll() // Autoriser explicitement /api/payments/notify
                .requestMatchers("/api/customer/cart/**","/api/customer/orders/**").hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers("/api/vendor/**").hasAnyRole("VENDOR", "ADMIN")
                .requestMatchers("/api/delivery/**").hasAnyRole("DELIVERER", "ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}