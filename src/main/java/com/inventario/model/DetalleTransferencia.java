package com.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Table(name = "detalles_transferencia")
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"transferencia", "producto"})
public class DetalleTransferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transferencia_id", nullable = false)
    private Transferencia transferencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Positive
    @Column(name = "cantidad_solicitada", nullable = false)
    private Integer cantidadSolicitada;

    @Column(name = "cantidad_enviada")
    private Integer cantidadEnviada;

    @Column(name = "cantidad_recibida")
    private Integer cantidadRecibida;

    // Si hay diferencia entre enviada y recibida
    @Column(name = "cantidad_faltante")
    private Integer cantidadFaltante = 0;

    @Column(name = "tratamiento_faltante", length = 100)
    private String tratamientoFaltante; // REENVIO, AJUSTE, RECLAMACION

    @Column(length = 300)
    private String observaciones;
}