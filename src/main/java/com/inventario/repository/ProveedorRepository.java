package com.inventario.repository;

import com.inventario.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    Optional<Proveedor> findByNitRuc(String nitRuc);

    List<Proveedor> findByActivoTrue();

    boolean existsByNitRuc(String nitRuc);

    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT p FROM Proveedor p WHERE p.activo = true ORDER BY p.calificacionPromedio DESC NULLS LAST")
    List<Proveedor> findActivosOrdenadosPorCalificacion();
}