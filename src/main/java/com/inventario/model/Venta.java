package com.inventario.model;

import com.inventario.model.enums.EstadoVenta;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ventas")
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"sucursal", "vendedor", "detalles"})
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Usuario vendedor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoVenta estado = EstadoVenta.PENDIENTE;

    @Column(name = "cliente_nombre", length = 150)
    private String clienteNombre;

    @Column(name = "cliente_documento", length = 30)
    private String clienteDocumento;

    @Column(name = "subtotal", precision = 14, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "descuento_total", precision = 14, scale = 2)
    private BigDecimal descuentoTotal = BigDecimal.ZERO;

    @Column(name = "total", precision = 14, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(length = 300)
    private String observaciones;

    @Column(name = "fecha_venta", nullable = false, updatable = false)
    private LocalDateTime fechaVenta;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles;

    @PrePersist
    public void prePersist() {
        this.fechaVenta = LocalDateTime.now();
    }
}