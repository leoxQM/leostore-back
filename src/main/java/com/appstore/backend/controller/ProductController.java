package com.appstore.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.appstore.backend.dtos.ProductRequest;
import com.appstore.backend.dtos.ProductResponse;
import com.appstore.backend.service.ProductService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@AllArgsConstructor
@RestController
@RequestMapping("/api/appstore/productos")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> listar(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(productService.listar(pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productService.obtenerPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<ProductResponse>> buscarPorNombre(
            @RequestParam String nombre,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(productService.buscarXNombre(nombre, pageable));
    }

    @PostMapping()
    public ResponseEntity<ProductResponse> crear(@Valid @RequestBody ProductRequest request) {
        ProductResponse creado = productService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ProductRequest request){
        return ResponseEntity.ok(productService.actualizar(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/imagenes")
    public ResponseEntity<ProductResponse> subirImagenes(
            @PathVariable Long id,
            @RequestParam("file") List<MultipartFile> files) throws IOException {

        if (files.isEmpty()) {
            throw new IllegalArgumentException("No se enviaron archivos");
        }

        return ResponseEntity.ok(productService.agregarImagenes(id, files));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<Page<ProductResponse>> listarPorCategoria(
            @PathVariable String categoria,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(productService.listarPorCategoria(categoria, pageable));
    }

    @DeleteMapping("/{id}/imagen")
    public ResponseEntity<Void> eliminarImagen(
            @PathVariable Long id,
            @RequestParam String nombreImagen) {
        productService.eliminarImagen(id, nombreImagen);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> obtenerCategorias() {
        return ResponseEntity.ok(productService.obtenerCategoriasDistintas());
    }

    @GetMapping("/colores")
    public ResponseEntity<List<String>> listarColores() {
        return ResponseEntity.ok(productService.obtenerColoresDistintos());
    }
}
