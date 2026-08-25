package com.appstore.backend.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.appstore.backend.dtos.ProductRequest;
import com.appstore.backend.dtos.ProductResponse;

public interface ProductService {
    
    Page<ProductResponse> listar(Pageable pageable);
    ProductResponse obtenerPorId(Long id);
    Page<ProductResponse> buscarXNombre(String nombre, Pageable pageable);
    ProductResponse crear(ProductRequest request);
    ProductResponse actualizar(Long id, ProductRequest request);
    void eliminar(Long id);
    ProductResponse agregarImagenes(Long id, List<MultipartFile> files) throws IOException;
    Page<ProductResponse> listarPorCategoria(String categoria, Pageable pageable);
    void eliminarImagen(Long id, String nombreImagen);
    List<String> obtenerCategoriasDistintas();
    List<String> obtenerColoresDistintos();


}
