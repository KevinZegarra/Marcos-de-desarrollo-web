package com.gym.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO con todas las métricas del Dashboard.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {

    // KPIs principales
    private long totalClientes;
    private long clientesActivos;
    private long clientesPorVencer;  // vencen en ≤7 días
    private long clientesVencidos;

    // Ingresos
    private BigDecimal ingresosMes;
    private BigDecimal ingresosAnio;

    // Gráficos — arrays de 12 posiciones (índice 0 = Enero)
    private BigDecimal[] ingresosMensuales;   // S/ por mes
    private long[] nuevosMiembrosMes;          // cantidad por mes

    // Distribución por plan
    private long countBasico;
    private long countPro;
    private long countElite;
}
