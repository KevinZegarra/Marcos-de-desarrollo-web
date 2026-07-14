package com.gym.repository;

import com.gym.entity.Cliente;
import com.gym.entity.Cliente.EstadoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);

    Cliente findByDni(String dni);

    List<Cliente> findByEstado(EstadoCliente estado);

    long countByEstado(EstadoCliente estado);

    @Query("SELECT c FROM Cliente c WHERE c.fechaVencimiento BETWEEN :hoy AND :limite")
    List<Cliente> findClientesProximosAVencer(
        @Param("hoy") LocalDate hoy,
        @Param("limite") LocalDate limite
    );

    @Query("SELECT c FROM Cliente c WHERE c.fechaVencimiento < :hoy")
    List<Cliente> findClientesVencidos(@Param("hoy") LocalDate hoy);

    @Query("SELECT COUNT(c) FROM Cliente c WHERE YEAR(c.fechaInscripcion) = :anio AND MONTH(c.fechaInscripcion) = :mes")
    long countNuevosClientesPorMes(@Param("anio") int anio, @Param("mes") int mes);

    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.fechaVencimiento >= :hoy")
    long countClientesActivos(@Param("hoy") LocalDate hoy);

    // Buscar por plan
    List<Cliente> findByPlanId(Long planId);
}
