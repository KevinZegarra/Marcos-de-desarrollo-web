package com.gym.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "promociones")
@Data
@NoArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(name = "boton_texto", length = 50)
    private String botonTexto;

    @Column(length = 250)
    private String enlace;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "orden_numero")
    private Integer orden = 0;
}
