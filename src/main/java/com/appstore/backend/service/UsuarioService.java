package com.appstore.backend.service;



import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import com.appstore.backend.dtos.ActualizarUsuarioRequest;
import com.appstore.backend.dtos.CambiarPasswordRequest;
import com.appstore.backend.dtos.UsuarioResponse;


public interface UsuarioService extends UserDetailsService {
    Page<UsuarioResponse> listar(Pageable pageable);

    UsuarioResponse obtenerPorId(Long id);

    UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest request);

    UsuarioResponse actualizarFotoPerfil(Long id, MultipartFile file) throws IOException;

    void cambiarPassword(Long id, CambiarPasswordRequest request);

    void eliminar(Long id);
}
