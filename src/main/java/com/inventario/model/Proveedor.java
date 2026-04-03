package com.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "proveedores")
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "ordenesCompra")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "nit_ruc", length = 30, unique = true)
    private String nitRuc;

    @Email
    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(length = 255)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(name = "persona_contacto", length = 100)
    private String personaContacto;

    @Column(name = "plazo_pago_dias")
    private Integer plazoPagoDias;

    @Column(name = "calificacion_promedio")
    private Double calificacionPromedio;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY)
    private List<OrdenCompra> ordenesCompra;

    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
    }
}