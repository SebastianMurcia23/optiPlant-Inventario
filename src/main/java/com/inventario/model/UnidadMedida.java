package com.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "unidades_medida")
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "productos")
public class UnidadMedida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @NotBlank
    @Column(nullable = false, unique = true, length = 10)
    private String abreviatura;

    @OneToMany(mappedBy = "unidadMedida", fetch = FetchType.LAZY)
    private List<Producto> productos;
}