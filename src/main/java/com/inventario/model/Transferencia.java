package com.inventario.model;

import com.inventario.model.enums.EstadoTransferencia;
import com.inventario.model.enums.PrioridadTransferencia;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transferencias")
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"sucursalOrigen", "sucursalDestino", "solicitante", "despachador", "detalles"})
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_origen_id", nullable = false)
    private Sucursal sucursalOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_destino_id", nullable = false)
    private Sucursal sucursalDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "despachador_id")
    private Usuario despachador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private EstadoTransferencia estado = EstadoTransferencia.SOLICITADA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PrioridadTransferencia prioridad = PrioridadTransferencia.NORMAL;

    @Column(length = 100)
    private String transportista;

    @Column(name = "fecha_solicitud", nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_despacho")
    private LocalDateTime fechaDespacho;

    @Column(name = "fecha_estimada_llegada")
    private LocalDate fechaEstimadaLlegada;

    @Column(name = "fecha_recepcion")
    private LocalDateTime fechaRecepcion;

    // Para calcular tiempos reales vs estimados
    @Column(name = "tiempo_estimado_horas")
    private Integer tiempoEstimadoHoras;

    @Column(name = "tiempo_real_horas")
    private Integer tiempoRealHoras;

    @Column(length = 500)
    private String observaciones;

    @Column(name = "observaciones_recepcion", length = 500)
    private String observacionesRecepcion;

    @OneToMany(mappedBy = "transferencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleTransferencia> detalles;

    @PrePersist
    public void prePersist() {
        this.fechaSolicitud = LocalDateTime.now();
    }
}