package com.inventario.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventario_items",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_producto_sucursal",
                columnNames = {"producto_id", "sucursal_id"}
        )
)
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"producto", "sucursal"})
public class InventarioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @Column(name = "cantidad_disponible", nullable = false)
    private Integer cantidadDisponible = 0;

    @Column(name = "cantidad_reservada", nullable = false)
    private Integer cantidadReservada = 0;

    // Stock mínimo específico por sucursal
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 0;

    // Stock máximo para evitar sobreabastecimiento
    @Column(name = "stock_maximo")
    private Integer stockMaximo;

    // Costo promedio ponderado específico de esta sucursal
    @Column(name = "costo_promedio_local", precision = 12, scale = 2)
    private BigDecimal costoPromedioLocal = BigDecimal.ZERO;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;

    @PrePersist
    @PreUpdate
    public void preUpdate() {
        this.ultimaActualizacion = LocalDateTime.now();
    }

    // Método utilitario
    public Integer getCantidadReal() {
        return cantidadDisponible - cantidadReservada;
    }

    public boolean tieneStockBajo() {
        return cantidadDisponible <= stockMinimo;
    }
}