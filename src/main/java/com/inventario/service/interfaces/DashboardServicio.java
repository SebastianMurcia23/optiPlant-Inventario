package com.inventario.service.interfaces;

import com.inventario.dto.response.DashboardDTO;
import com.inventario.dto.response.PrediccionDemandaDTO;

import java.util.List;

public interface DashboardServicio {
    DashboardDTO obtenerDashboardSucursal(Long sucursalId);
    DashboardDTO obtenerDashboardGeneral();
    PrediccionDemandaDTO predecirDemanda(Long productoId, Long sucursalId);
    List<PrediccionDemandaDTO> predecirDemandaSucursal(Long sucursalId);
}