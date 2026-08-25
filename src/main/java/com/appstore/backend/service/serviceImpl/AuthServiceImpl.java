package com.appstore.backend.service.serviceImpl;

import com.appstore.backend.dtos.LoginRequest;
import com.appstore.backend.dtos.LoginResponse;
import com.appstore.backend.dtos.RegisterRequest;
import com.appstore.backend.dtos.RegisterResponse;
import com.appstore.backend.model.Usuario;
import com.appstore.backend.repository.UsuarioRepository;
import com.appstore.backend.service.AuthService;
import com.appstore.backend.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor

public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        Usuario usuario = usuarioRepository.findByUsername(request.username())
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String token = jwtService.generarToken(usuario);

        return new LoginResponse(token, usuario.getUsername(), usuario.getRol());
    }

    @Override
    public RegisterResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("El username ya está en uso");
        }
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.username());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setEmail(request.email());
        usuario.setRol(request.rol());
        usuario.setActivo(true);

        Usuario guardado = usuarioRepository.save(usuario);

        return new RegisterResponse(
            guardado.getId(),
            guardado.getUsername(),
            guardado.getEmail(),
            guardado.getRol()
        );
    }
}
