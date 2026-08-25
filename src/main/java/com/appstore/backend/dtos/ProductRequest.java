package com.appstore.backend.dtos;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductRequest (
    @NotBlank(message = "El nombre es obligatorio")
    String nombre,
    String descripcion,
    @NotNull @Positive(message = "El precio debe ser mayor a 0")
    Double precio,
    String moneda,
    @NotBlank(message = "La categoría es obligatoria")
    String categoria,
    @NotNull @PositiveOrZero(message = "El stock no puede ser negativo")
    Integer stock,
    List<String> tallas,
    List<String> colores
) {

}
