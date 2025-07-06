package com.gardengroup.agroplantationapp.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Inyección de dependencia para el manejador de autenticación de JWT
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // Configuración del AuthenticationManager
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Configuración del codificador de contraseñas (BCryptPasswordEncoder)
    @Bean
    PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // Creación de un filtro personalizado para autenticación basada en JWT
    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();

        // Permitir todos los orígenes
        configuration.setAllowedOrigins(Arrays.asList("*"));

        // Permitir todos los métodos
        configuration.setAllowedMethods(Arrays.asList("*"));

        // Permitir todas las cabeceras
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Permitir credenciales
        configuration.setAllowCredentials(true);

        // Aplicar la configuración de CORS en todos los caminos
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // Configuración de la cadena de filtros de seguridad HTTP
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/publication/publications/top",
                                "/swagger-ui/**", "/swagger-ui.html/**",
                                "/v3/api-docs/**", "/logs/**")
                        .permitAll()
                        .requestMatchers("/v1/publication/**", "/v1/user/**")
                        .hasAnyAuthority("USER", "ADMIN", "PRODUCER")
                        .requestMatchers("/configuracion").hasAuthority("ADMINISTRATOR")
                        .anyRequest().authenticated())
                .httpBasic(withDefaults());

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}