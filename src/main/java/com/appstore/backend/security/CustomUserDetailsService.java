// package com.appstore.backend.security;

// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;

// import com.appstore.backend.model.Usuario;
// import com.appstore.backend.repository.UsuarioRepository;

// public class CustomUserDetailsService implements UserDetailsService {
//     private final UsuarioRepository usuarioRepository;
 
//     public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
//         this.usuarioRepository = usuarioRepository;
//     }
 
//     @Override
//     public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//         Usuario usuario = usuarioRepository.findByEmail(email)
//                 .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
 
//         return new UserDetailsImpl(usuario);
//     }
// }
