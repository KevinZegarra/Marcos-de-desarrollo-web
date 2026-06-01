package com.gym.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "galeria_imagenes")
@Getter
@Setter
@NoArgsConstructor
public class GalleryImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 250)
    private String descripcion;

    @Column(nullable = false, length = 200)
    private String filename;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "orden_numero")
    private Integer orden = 0;
}
