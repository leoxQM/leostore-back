package com.appstore.backend.service.serviceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.appstore.backend.dtos.ActualizarUsuarioRequest;
import com.appstore.backend.dtos.CambiarPasswordRequest;
import com.appstore.backend.dtos.RegisterRequest;
import com.appstore.backend.dtos.UsuarioResponse;
import com.appstore.backend.exception.ResourceNotFoundException;
import com.appstore.backend.model.Usuario;
import com.appstore.backend.repository.UsuarioRepository;
import com.appstore.backend.service.AlmacenamientoService;
import com.appstore.backend.service.UsuarioService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Service
@AllArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AlmacenamientoService almacenamientoService;
    private final PasswordEncoder passwordEncoder;
    private static final String UPLOAD_DIR_USUARIOS = "uploads/usuarios/";
    private static final List<String> EXTENSIONES_PERMITIDAS = List.of(".jpg", ".jpeg", ".png");

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    @Override
    public Page<UsuarioResponse> listar(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
            .map(this::mapearAResponse);
    }

    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = buscarUsuarioOrThrow(id);
        return mapearAResponse(usuario);
    }

    @Override
public UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest request) {
    Usuario usuario = buscarUsuarioOrThrow(id);
    Usuario usuarioAutenticado = obtenerUsuarioAutenticado();

    // Regla 1: nadie puede cambiar su propio rol
    if (usuario.getId().equals(usuarioAutenticado.getId())
            && !usuario.getRol().equals(request.rol())) {
        throw new IllegalArgumentException("No puedes cambiar tu propio rol");
    }

    // Regla 2: si se va a quitar el rol ADMIN a este usuario, verifica que quede al menos otro admin activo
    boolean eraAdmin = "ADMIN".equals(usuario.getRol());
    boolean dejaDeSerAdmin = eraAdmin && !"ADMIN".equals(request.rol());

    if (dejaDeSerAdmin) {
        long cantidadAdminsActivos = usuarioRepository.countByRolAndActivoTrue("ADMIN");
        if (cantidadAdminsActivos <= 1) {
            throw new IllegalArgumentException("Debe existir al menos un administrador activo en el sistema");
        }
    }

    usuario.setUsername(request.username());
    usuario.setEmail(request.email());
    usuario.setRol(request.rol());
    if (request.activo() != null) {
        // Regla 2 también aplica si se está DESACTIVANDO a un admin (no solo cambiando el rol)
        boolean seVaADesactivar = eraAdmin && Boolean.FALSE.equals(request.activo());
        if (seVaADesactivar) {
            long cantidadAdminsActivos = usuarioRepository.countByRolAndActivoTrue("ADMIN");
            if (cantidadAdminsActivos <= 1) {
                throw new IllegalArgumentException("Debe existir al menos un administrador activo en el sistema");
            }
        }
        usuario.setActivo(request.activo());
    }

    Usuario actualizado = usuarioRepository.save(usuario);
    return mapearAResponse(actualizado);
}

    @Override
    public void cambiarPassword(Long id, CambiarPasswordRequest request) {
        Usuario usuario = buscarUsuarioOrThrow(id);
        usuario.setPassword(passwordEncoder.encode(request.nuevaPassword()));
        usuarioRepository.save(usuario);
    }

    @Override
    public void eliminar(Long id) {
        Usuario usuario = buscarUsuarioOrThrow(id);
    Usuario usuarioAutenticado = obtenerUsuarioAutenticado();

    if (usuario.getId().equals(usuarioAutenticado.getId())) {
        throw new IllegalArgumentException("No puedes eliminar tu propia cuenta");
    }

    if ("ADMIN".equals(usuario.getRol())) {
        long cantidadAdminsActivos = usuarioRepository.countByRolAndActivoTrue("ADMIN");
        if (cantidadAdminsActivos <= 1) {
            throw new IllegalArgumentException("Debe existir al menos un administrador activo en el sistema");
        }
    }

    usuarioRepository.delete(usuario);
    }

    @Override
    public UsuarioResponse actualizarFotoPerfil(Long id, MultipartFile file) throws IOException {
        Usuario usuario = buscarUsuarioOrThrow(id);

        validarExtension(file.getOriginalFilename());

        // Si ya tenía una foto anterior en Supabase, la elimina antes de subir la nueva
        if (usuario.getFotoPerfil() != null) {
            String rutaVieja = extraerRutaRelativa(usuario.getFotoPerfil());
            almacenamientoService.eliminarArchivo(rutaVieja);
        }

        String extension = obtenerExtension(file.getOriginalFilename());
        String nombreArchivo = System.currentTimeMillis() + "_" + id + extension;

        String urlPublica = almacenamientoService.subirArchivo(file, "usuarios/" + nombreArchivo);

        usuario.setFotoPerfil(urlPublica);   // ahora guarda la URL COMPLETA de Supabase
        Usuario actualizado = usuarioRepository.save(usuario);

        return mapearAResponse(actualizado);
    }

    private void validarExtension(String nombreOriginal) {
        String extension = obtenerExtension(nombreOriginal).toLowerCase();
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new IllegalArgumentException("Formato de imagen no permitido: " + extension);
        }
    }

    private String obtenerExtension(String nombreOriginal) {
        return (nombreOriginal != null && nombreOriginal.contains("."))
            ? nombreOriginal.substring(nombreOriginal.lastIndexOf("."))
            : "";
    }

    private Usuario buscarUsuarioOrThrow(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    private UsuarioResponse mapearAResponse(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getUsername(),
            usuario.getEmail(),
            usuario.getRol(),
            usuario.getActivo(),
            usuario.getFotoPerfil(),
            usuario.getFechaCreacion()
        );
    }

    private Usuario obtenerUsuarioAutenticado(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
               .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));
    }

    private String extraerRutaRelativa(String urlCompleta) {
        // Extrae solo la parte después del nombre del bucket, ej: "usuarios/123_1.jpg"
        int index = urlCompleta.indexOf("/public/") ;
        if (index == -1) return urlCompleta;
        String despuesDelBucket = urlCompleta.substring(index + "/public/".length());
        // despuesDelBucket ahora es algo como "productos/usuarios/123_1.jpg" (bucket/ruta)
        int primeraBarra = despuesDelBucket.indexOf('/');
        return despuesDelBucket.substring(primeraBarra + 1); // quita el nombre del bucket, deja solo la ruta
    }

}
