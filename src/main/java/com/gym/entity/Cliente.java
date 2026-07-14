package com.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(unique = true, length = 20)
    private String dni;

    @Column(length = 20)
    private String telefono;

    @Email
    @Column(unique = true, length = 150)
    private String correo;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCliente estado = EstadoCliente.ACTIVO;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pago> pagos;

    @Transient
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    @Transient
    public long getDiasRestantes() {
        if (fechaVencimiento == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaVencimiento);
    }

    @Transient
    public EstadoCliente getEstadoDinamico() {
        long dias = getDiasRestantes();
        if (dias < 0) return EstadoCliente.VENCIDO;
        if (dias <= 7) return EstadoCliente.POR_VENCER;
        return EstadoCliente.ACTIVO;
    }

    public enum EstadoCliente {
        ACTIVO, POR_VENCER, VENCIDO, INACTIVO
    }
}
