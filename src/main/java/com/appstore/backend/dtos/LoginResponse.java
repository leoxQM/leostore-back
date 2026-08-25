package com.appstore.backend.dtos;

public record LoginResponse (

    String token,
    String username,
    String rol


) {

}
