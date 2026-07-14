package com.gym.repository;

import com.gym.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByClienteIdOrderByFechaPagoDesc(Long clienteId);

    @Query("SELECT p FROM Pago p WHERE YEAR(p.fechaPago) = :anio AND MONTH(p.fechaPago) = :mes ORDER BY p.fechaPago DESC")
    List<Pago> findPagosPorMes(@Param("anio") int anio, @Param("mes") int mes);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE YEAR(p.fechaPago) = :anio AND MONTH(p.fechaPago) = :mes AND p.estado = 'PAGADO'")
    BigDecimal sumIngresosPorMes(@Param("anio") int anio, @Param("mes") int mes);

    // Ingresos del año
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE YEAR(p.fechaPago) = :anio AND p.estado = 'PAGADO'")
    BigDecimal sumIngresosPorAnio(@Param("anio") int anio);

    // Ingresos por mes (para gráfico)
    @Query("SELECT MONTH(p.fechaPago), COALESCE(SUM(p.monto), 0) FROM Pago p WHERE YEAR(p.fechaPago) = :anio AND p.estado = 'PAGADO' GROUP BY MONTH(p.fechaPago) ORDER BY MONTH(p.fechaPago)")
    List<Object[]> sumIngresosMensuales(@Param("anio") int anio);

    @Query("SELECT MONTH(p.fechaPago), COUNT(DISTINCT p.cliente.id) FROM Pago p WHERE YEAR(p.fechaPago) = :anio GROUP BY MONTH(p.fechaPago) ORDER BY MONTH(p.fechaPago)")
    List<Object[]> countMiembrosPorMes(@Param("anio") int anio);

    @Query("SELECT p FROM Pago p ORDER BY p.fechaPago DESC")
    List<Pago> findUltimosPagos(org.springframework.data.domain.Pageable pageable);
}
