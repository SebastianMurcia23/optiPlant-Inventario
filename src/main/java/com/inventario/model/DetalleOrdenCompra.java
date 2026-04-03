package com.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "detalles_orden_compra")
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"ordenCompra", "producto"})
public class DetalleOrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_compra_id", nullable = false)
    private OrdenCompra ordenCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Positive
    @Column(name = "cantidad_solicitada", nullable = false)
    private Integer cantidadSolicitada;

    @Column(name = "cantidad_recibida")
    private Integer cantidadRecibida = 0;

    @Positive
    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "porcentaje_descuento", precision = 5, scale = 2)
    private BigDecimal porcentajeDescuento = BigDecimal.ZERO;

    @Column(name = "subtotal", precision = 14, scale = 2)
    private BigDecimal subtotal;

    public void calcularSubtotal() {
        BigDecimal descuento = precioUnitario
                .multiply(BigDecimal.valueOf(cantidadSolicitada))
                .multiply(porcentajeDescuento)
                .divide(BigDecimal.valueOf(100));
        this.subtotal = precioUnitario
                .multiply(BigDecimal.valueOf(cantidadSolicitada))
                .subtract(descuento);
    }
}