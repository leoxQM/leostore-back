package com.appstore.backend.config;

import com.appstore.backend.model.Usuario;
import com.appstore.backend.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner inicializarAdmin(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (!usuarioRepository.existsByUsername("admin")) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123")); // cámbiala después del primer login
                admin.setEmail("admin@leostore.com");
                admin.setRol("ADMIN");
                admin.setActivo(true);

                usuarioRepository.save(admin);
                System.out.println("✅ Usuario admin creado por defecto (username: admin / password: admin123)");
            }
        };
    }
}
