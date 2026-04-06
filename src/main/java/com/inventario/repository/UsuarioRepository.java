package com.inventario.repository;

import com.inventario.model.Usuario;
import com.inventario.model.enums.EstadoUsuario;
import com.inventario.model.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailAndEstadoNot(String email, EstadoUsuario estado); // ← AGREGAR

    boolean existsByEmail(String email);

    List<Usuario> findBySucursalId(Long sucursalId);

    List<Usuario> findByRol(RolUsuario rol);

    List<Usuario> findByEstado(EstadoUsuario estado);

    List<Usuario> findBySucursalIdAndEstado(Long sucursalId, EstadoUsuario estado);

    @Query("SELECT u FROM Usuario u WHERE u.sucursal.id = :sucursalId AND u.rol = :rol AND u.estado = 'ACTIVO'")
    List<Usuario> findActivosBySucursalAndRol(Long sucursalId, RolUsuario rol);
}