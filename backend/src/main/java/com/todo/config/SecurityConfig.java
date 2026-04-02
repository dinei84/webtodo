package com.todo.config;

import com.todo.security.FirebaseAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Configuração de segurança da aplicação.
 * Define políticas de autenticação via Firebase Token e CORS.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    private final FirebaseAuthenticationFilter firebaseAuthenticationFilter;

    public SecurityConfig(FirebaseAuthenticationFilter firebaseAuthenticationFilter) {
        this.firebaseAuthenticationFilter = firebaseAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/health", "/api/public/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .addFilterBefore(firebaseAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permite múltiplas origens separadas por vírgula
        if (allowedOrigins != null && allowedOrigins.length > 0) {
            List<String> normalizedOrigins = Arrays.stream(allowedOrigins)
                    .map(origin -> origin == null ? "" : origin.trim())
                    .filter(origin -> !origin.isBlank())
                    // Evita mismatch entre "https://site.com/" e Origin "https://site.com"
                    .map(origin -> origin.endsWith("/") ? origin.substring(0, origin.length() - 1) : origin)
                    .collect(Collectors.toList());

            System.out.println("CORS Origins from env: " + normalizedOrigins);
            configuration.setAllowedOrigins(normalizedOrigins);
        } else {
            // Fallback para desenvolvimento
            System.out.println("CORS using fallback origins");
            configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://webtodo-pink.vercel.app"));
        }
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
