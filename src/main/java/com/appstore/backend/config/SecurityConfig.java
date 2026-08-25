package com.appstore.backend.config;

import com.appstore.backend.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UsuarioService usuarioService;
    private final JwtAuthFilter jwtAuthFilter;
    private final PasswordEncoder passwordEncoder;   // ← ahora lo INYECTA, no lo declara aquí


    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(usuarioService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {})
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Login público
                .requestMatchers("/auth/login").permitAll()

                // Cualquiera puede VER productos (tienda pública)
                .requestMatchers(HttpMethod.GET, "/api/appstore/productos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                // Crear, editar, eliminar productos: solo ADMIN
                .requestMatchers(HttpMethod.POST, "/api/appstore/productos/**").hasAnyRole("ADMIN","USER")
                .requestMatchers(HttpMethod.PUT, "/api/appstore/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/appstore/productos/**").hasRole("ADMIN")

                // Registro de usuarios: solo ADMIN (ya protegido también con @PreAuthorize en el controller)
                .requestMatchers(HttpMethod.POST, "/api/appstore/productos/*/imagen*").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.DELETE, "/api/appstore/productos/*/imagen*").hasRole("ADMIN")

                .requestMatchers("/auth/register").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
