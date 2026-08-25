package com.appstore.backend.dtos;

public record RegisterResponse(
    Long id,
    String username,
    String email,
    String rol
) {

}
