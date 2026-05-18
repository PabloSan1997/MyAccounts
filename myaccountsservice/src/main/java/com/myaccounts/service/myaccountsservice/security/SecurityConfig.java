package com.myaccounts.service.myaccountsservice.security;

import com.myaccounts.service.myaccountsservice.security.filter.JwtValidationTokenFilter;
import com.myaccounts.service.myaccountsservice.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private  AuthenticationConfiguration authenticationConfiguration;
    @Autowired
    private  JwtService jwtService;



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(a -> a
                .requestMatchers(HttpMethod.POST, "/api/user/login", "/api/user/register", "/api/user/refresh", "/api/user/logout").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/user/userinfo", "/api/initCapital", "/api/periods", "/api/periods/*").hasRole("USER")
                .requestMatchers(HttpMethod.PATCH, "/api/initCapital", "/api/periods/*/costfixed/*", "/api/periods/*/incomefixed/*", "/api/periods/*/costvariable/*", "/api/periods/*/incomevariable/*").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/periods", "/api/periods/*/costfixed", "/api/periods/*/incomefixed", "/api/periods/*/costvariable", "/api/periods/*/incomevariable").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/api/periods/*", "/api/periods/*/costfixed/*", "/api/periods/*/incomefixed/*", "/api/periods/*/costvariable/*", "/api/periods/*/incomevariable/*").hasRole("USER")
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtValidationTokenFilter(authenticationManager(), jwtService),
                    UsernamePasswordAuthenticationFilter.class)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}