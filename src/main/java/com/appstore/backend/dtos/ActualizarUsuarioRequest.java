package com.appstore.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ActualizarUsuarioRequest (

    @NotBlank(message = "El nombre de usuario es obligatorio")
    String username,

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    String email,

    @NotBlank(message = "El rol es obligatorio")
    String rol,

    Boolean activo
) {

}
