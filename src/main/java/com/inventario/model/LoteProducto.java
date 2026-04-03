package com.inventario.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lotes_producto")
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"producto", "sucursal"})
public class LoteProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "numero_lote", nullable = false, length = 50)
    private String numeroLote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @Column(name = "cantidad_inicial", nullable = false)
    private Integer cantidadInicial;

    @Column(name = "cantidad_actual", nullable = false)
    private Integer cantidadActual;

    @Column(name = "fecha_fabricacion")
    private LocalDate fechaFabricacion;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_ingreso", nullable = false, updatable = false)
    private LocalDateTime fechaIngreso;

    @Column(nullable = false)
    private boolean activo = true;

    @PrePersist
    public void prePersist() {
        this.fechaIngreso = LocalDateTime.now();
    }

    public boolean estaVencido() {
        return fechaVencimiento != null && LocalDate.now().isAfter(fechaVencimiento);
    }

    public boolean proximoAVencer(int diasAnticipacion) {
        return fechaVencimiento != null &&
                !estaVencido() &&
                LocalDate.now().plusDays(diasAnticipacion).isAfter(fechaVencimiento);
    }
}