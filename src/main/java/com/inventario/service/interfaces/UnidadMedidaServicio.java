package com.inventario.service.interfaces;

import com.inventario.dto.request.CrearUnidadMedidaDTO;
import com.inventario.model.UnidadMedida;

import java.util.List;

public interface UnidadMedidaServicio {
    UnidadMedida crear(CrearUnidadMedidaDTO dto) throws Exception;
    UnidadMedida editar(Long id, CrearUnidadMedidaDTO dto) throws Exception;
    UnidadMedida obtenerPorId(Long id) throws Exception;
    List<UnidadMedida> listarTodas();
}