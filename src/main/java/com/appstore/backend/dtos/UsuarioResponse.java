package com.appstore.backend.dtos;

import java.time.LocalDateTime;


public record UsuarioResponse(
    Long id,
    String username,
    String email,
    String rol,
    boolean activo,
    String fotoPerfil,
    LocalDateTime fechaCreacion
) {


}
