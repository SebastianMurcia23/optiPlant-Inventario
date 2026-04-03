package com.inventario.dto.response;

import java.util.List;

public record PrediccionDemandaDTO(
        Long productoId,
        String productoNombre,
        Long sucursalId,
        String sucursalNombre,
        Double promedioVentasMensual,
        Integer prediccionProximoMes,
        Integer stockActual,
        Integer stockSugerido,
        boolean necesitaReabastecimiento,
        List<Double> historialUltimos6Meses
) {}