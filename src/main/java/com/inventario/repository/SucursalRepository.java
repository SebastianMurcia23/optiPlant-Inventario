package com.inventario.repository;

import com.inventario.model.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

    Optional<Sucursal> findByNombre(String nombre);

    List<Sucursal> findByActivaTrue();

    boolean existsByNombre(String nombre);

    @Query("SELECT s FROM Sucursal s WHERE s.activa = true AND s.id <> :idExcluir")
    List<Sucursal> findActivasExcluyendo(Long idExcluir);
}