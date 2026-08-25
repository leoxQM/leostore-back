package com.appstore.backend.dtos;

import java.util.List;

public record ProductResponse (
    Long id,
    String nombre,
    String descripcion,
    Double precio,
    String moneda,
    String categoria,
    List<String> tallas,
    List<String> colores,
    String imagen,
    List<String> imagenes,
    Integer stock
) {

}
