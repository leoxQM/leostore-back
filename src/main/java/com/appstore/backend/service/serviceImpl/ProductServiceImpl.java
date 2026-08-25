package com.appstore.backend.service.serviceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.appstore.backend.dtos.ProductRequest;
import com.appstore.backend.dtos.ProductResponse;
import com.appstore.backend.exception.ResourceNotFoundException;
import com.appstore.backend.model.Product;
import com.appstore.backend.repository.ProductRepository;
import com.appstore.backend.service.AlmacenamientoService;
import com.appstore.backend.service.ProductService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final AlmacenamientoService almacenamientoService;   // ← la interfaz, no una clase concreta

    private static final String UPLOAD_DIR = "uploads/productos/";
    private static final List<String> EXTENSIONES_PERMITIDAS = List.of(".jpg", ".jpeg", ".png");

    @Override
    public Page<ProductResponse> listar(Pageable pageable) {
        return productRepository.findAll(pageable)
            .map(this::mapearAResponse);
    }

    @Override
    public ProductResponse obtenerPorId(Long id) {
        Product producto = buscarProductOrThrow(id);
        return mapearAResponse(producto);
    }

    @Override
    public Page<ProductResponse> buscarXNombre(String nombre, Pageable pageable){
        return productRepository.findByNombreContainingIgnoreCase(nombre, pageable)
        .map(this::mapearAResponse);
    }

    @Override
    public ProductResponse crear(ProductRequest request) {
        Product producto = new Product();
        mapearRequestAEntidad(request, producto);

        Product guardado = productRepository.save(producto);
        return mapearAResponse(guardado);
    }

    @Override
    public ProductResponse actualizar(Long id, ProductRequest request) {
        Product producto = buscarProductOrThrow(id);
        mapearRequestAEntidad(request, producto);

        Product actualizado = productRepository.save(producto);
        return mapearAResponse(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        Product producto = buscarProductOrThrow(id);
        productRepository.delete(producto);
    }

    @Override
    public ProductResponse agregarImagenes(Long id, List<MultipartFile> files) throws IOException {

        Product producto = buscarProductOrThrow(id);

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            validarExtension(file.getOriginalFilename());

            String extension = obtenerExtension(file.getOriginalFilename());
            String nombreArchivo = System.currentTimeMillis() + "_" + id + "_" + producto.getImagenes().size() + extension;
            String rutaRelativa = "productos/" + nombreArchivo;

            String resultado = almacenamientoService.subirArchivo(file, rutaRelativa);
            producto.getImagenes().add(resultado);
        }

        Product actualizado = productRepository.save(producto);
        return mapearAResponse(actualizado);
    }

    @Override
    public Page<ProductResponse> listarPorCategoria(String categoria, Pageable pageable) {
        return productRepository.findByCategoria(categoria, pageable)
            .map(this::mapearAResponse);
    }

    @Override
    public void eliminarImagen(Long id, String urlImagen) {
        Product producto = buscarProductOrThrow(id);

        boolean eliminado = producto.getImagenes().remove(urlImagen);
        if (!eliminado) {
            throw new ResourceNotFoundException("La imagen no pertenece a este producto: " + urlImagen);
        }

        // Extrae la ruta relativa de la URL completa para poder eliminarla en Supabase
        String rutaRelativa = urlImagen.substring(urlImagen.indexOf("/productos/") + 1);
        almacenamientoService.eliminarArchivo(rutaRelativa);

        productRepository.save(producto);
    }

    @Override
    public List<String> obtenerCategoriasDistintas(){
        return productRepository.obtenerCategoriasDistintas();
    }

    @Override
    public List<String> obtenerColoresDistintos(){
        return productRepository.obtenerColoresDistintos();
    }

    private Product buscarProductOrThrow(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    private void mapearRequestAEntidad(ProductRequest request, Product producto) {
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setMoneda(request.moneda());
        producto.setCategoria(request.categoria());
        producto.setStock(request.stock());
        producto.setTallas(request.tallas());
        producto.setColores(request.colores());
    }

    private ProductResponse mapearAResponse(Product product) {
        String imagenPrincipal = product.getImagenes().isEmpty()
            ? null
            : product.getImagenes().get(0);

        return new ProductResponse(
            product.getId(),
            product.getNombre(),
            product.getDescripcion(),
            product.getPrecio(),
            product.getMoneda(),
            product.getCategoria(),
            product.getTallas(),
            product.getColores(),
            imagenPrincipal,
            product.getImagenes(),
            product.getStock()
        );
    }

    private String obtenerExtension(String nombreOriginal) {
        return (nombreOriginal != null && nombreOriginal.contains("."))
            ? nombreOriginal.substring(nombreOriginal.lastIndexOf("."))
            : "";
    }

    private void validarExtension(String nombreOriginal) {
    String extension = obtenerExtension(nombreOriginal).toLowerCase();
    if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
        throw new IllegalArgumentException(
            "Formato de imagen no permitido: " + extension + ". Solo se permiten: jpg, jpeg, png"
        );
    }
}
}
