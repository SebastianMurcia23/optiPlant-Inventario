package com.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "productos")
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"categoria", "unidadMedida", "inventarios"})
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_medida_id", nullable = false)
    private UnidadMedida unidadMedida;

    // Precio de referencia para ventas
    @Positive
    @Column(name = "precio_venta_referencia", precision = 12, scale = 2)
    private BigDecimal precioVentaReferencia;

    // Costo promedio ponderado calculado dinámicamente
    @Column(name = "costo_promedio", precision = 12, scale = 2)
    private BigDecimal costoPromedio = BigDecimal.ZERO;

    // Stock mínimo global (puede sobrescribirse por sucursal en InventarioItem)
    @Column(name = "stock_minimo_global")
    private Integer stockMinimoGlobal = 0;

    // Control de caducidad
    @Column(name = "tiene_fecha_vencimiento", nullable = false)
    private boolean tieneFechaVencimiento = false;

    @Column(name = "dias_alerta_vencimiento")
    private Integer diasAlertaVencimiento = 30;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<InventarioItem> inventarios;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
    }
}