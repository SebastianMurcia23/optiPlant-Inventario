package com.inventario.service.interfaces;

import com.inventario.dto.response.AlertaDTO;

import java.util.List;

public interface AlertaServicio {
    List<AlertaDTO> listarActivasPorSucursal(Long sucursalId);
    void marcarComoLeida(Long alertaId) throws Exception;
    void marcarComoResuelta(Long alertaId) throws Exception;
    void generarAlertasStockBajo(Long sucursalId);
    void generarAlertasVencimiento(Long sucursalId);
    long contarActivasPorSucursal(Long sucursalId);
}