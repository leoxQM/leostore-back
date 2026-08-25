package com.appstore.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.appstore.backend.model.Product;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Page<Product> findByCategoria(String categoria, Pageable pageable);

    Page<Product> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Page<Product> findByStockGreaterThan(Integer stock, Pageable pageable);

    @Query("SELECT DISTINCT p.categoria FROM Product p ORDER BY p.categoria")
    List<String> obtenerCategoriasDistintas();

    @Query("SELECT DISTINCT c FROM Product p JOIN p.colores c ORDER BY c")
    List<String> obtenerColoresDistintos();

    @Query("SELECT p FROM Product p WHERE p.precio BETWEEN :min AND :max")
    Page<Product> buscarPorRangoDePrecio(@Param("min") Double min, @Param("max") Double max, Pageable pageable);
}
