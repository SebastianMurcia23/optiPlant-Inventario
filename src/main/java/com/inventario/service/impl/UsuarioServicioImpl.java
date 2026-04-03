package com.inventario.service.impl;

import com.inventario.dto.request.CrearUsuarioDTO;
import com.inventario.dto.request.EditarUsuarioDTO;
import com.inventario.dto.response.UsuarioDTO;
import com.inventario.model.Sucursal;
import com.inventario.model.Usuario;
import com.inventario.model.enums.EstadoUsuario;
import com.inventario.repository.UsuarioRepository;
import com.inventario.service.interfaces.UsuarioServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioServicioImpl implements UsuarioServicio {

    private final UsuarioRepository usuarioRepository;
    private final SucursalServicioImpl sucursalServicio;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UsuarioDTO crear(CrearUsuarioDTO dto) throws Exception {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new Exception("El email ya está registrado: " + dto.email());
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.nombre());
        usuario.setEmail(dto.email());
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setTelefono(dto.telefono());
        usuario.setRol(dto.rol());
        usuario.setEstado(EstadoUsuario.ACTIVO);

        if (dto.sucursalId() != null) {
            Sucursal sucursal = sucursalServicio.obtenerEntidad(dto.sucursalId());
            usuario.setSucursal(sucursal);
        }

        return mapearDTO(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioDTO editar(EditarUsuarioDTO dto) throws Exception {
        Usuario usuario = obtenerEntidad(dto.id());
        usuario.setNombre(dto.nombre());
        usuario.setTelefono(dto.telefono());
        if (dto.nuevaPassword() != null && !dto.nuevaPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.nuevaPassword()));
        }
        return mapearDTO(usuarioRepository.save(usuario));
    }

    @Override
    public void eliminar(Long id) throws Exception {
        Usuario usuario = obtenerEntidad(id);
        usuario.setEstado(EstadoUsuario.ELIMINADO);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorId(Long id) throws Exception {
        return mapearDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarPorSucursal(Long sucursalId) {
        return usuarioRepository.findBySucursalId(sucursalId)
                .stream().map(this::mapearDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream().map(this::mapearDTO).toList();
    }

    // ── Métodos de apoyo ──────────────────────────────────
    public Usuario obtenerEntidad(Long id) throws Exception {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado con id: " + id));
    }

    public UsuarioDTO mapearDTO(Usuario u) {
        return new UsuarioDTO(
                u.getId(), u.getNombre(), u.getEmail(), u.getTelefono(),
                u.getRol(), u.getEstado(),
                u.getSucursal() != null ? u.getSucursal().getId() : null,
                u.getSucursal() != null ? u.getSucursal().getNombre() : null,
                u.getFechaRegistro()
        );
    }
}