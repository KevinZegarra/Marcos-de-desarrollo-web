package com.gym.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "planes")
@Data
@NoArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "duracion_dias", nullable = false)
    private Integer duracionDias;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "beneficios", columnDefinition = "TEXT")
    private String beneficios;

    // Excluir clientes del toString/equals para evitar ciclo infinito con JPA
    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY)
    private List<Cliente> clientes;

    // Constructor conveniente usado en DataInitializer
    public Plan(String nombre, BigDecimal precio, Integer duracionDias, String descripcion, String beneficios) {
        this.nombre = nombre;
        this.precio = precio;
        this.duracionDias = duracionDias;
        this.descripcion = descripcion;
        this.beneficios = beneficios;
        this.activo = true;
    }
}
