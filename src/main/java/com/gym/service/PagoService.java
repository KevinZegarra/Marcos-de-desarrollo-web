package com.gym.service;

import com.gym.entity.Pago;

import java.math.BigDecimal;
import java.util.List;

public interface PagoService {

    List<Pago> listarTodos();

    List<Pago> pagosPorCliente(Long clienteId);

    List<Pago> pagosMesActual();

    BigDecimal ingresosMesActual();

    BigDecimal ingresosAnioActual();

    BigDecimal[] ingresosMensualesAnio(int anio);

    long[] nuevosMiembrosMensuales(int anio);

    List<Pago> ultimosPagos(int cantidad);
}
