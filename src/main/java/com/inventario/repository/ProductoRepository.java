package com.inventario.repository;

import com.inventario.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Producto> findByActivoTrue();

    List<Producto> findByCategoriaId(Long categoriaId);

    List<Producto> findByTieneFechaVencimientoTrue();

    Page<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.categoria.id = :categoriaId")
    List<Producto> findActivosByCategoria(Long categoriaId);

    @Query("""
        SELECT p FROM Producto p
        JOIN InventarioItem i ON i.producto = p
        WHERE i.sucursal.id = :sucursalId
        AND i.cantidadDisponible <= i.stockMinimo
        AND p.activo = true
    """)
    List<Producto> findProductosConStockBajoEnSucursal(Long sucursalId);
}