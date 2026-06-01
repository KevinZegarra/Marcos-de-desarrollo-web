package com.gym.service;

import com.gym.entity.Pago;
import com.gym.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;

    @Override
    public List<Pago> listarTodos() {
        return pagoRepository.findAll();
    }

    @Override
    public List<Pago> pagosPorCliente(Long clienteId) {
        return pagoRepository.findByClienteIdOrderByFechaPagoDesc(clienteId);
    }

    @Override
    public List<Pago> pagosMesActual() {
        LocalDate hoy = LocalDate.now();
        return pagoRepository.findPagosPorMes(hoy.getYear(), hoy.getMonthValue());
    }

    @Override
    public BigDecimal ingresosMesActual() {
        LocalDate hoy = LocalDate.now();
        return pagoRepository.sumIngresosPorMes(hoy.getYear(), hoy.getMonthValue());
    }

    @Override
    public BigDecimal ingresosAnioActual() {
        return pagoRepository.sumIngresosPorAnio(LocalDate.now().getYear());
    }

    @Override
    public BigDecimal[] ingresosMensualesAnio(int anio) {
        BigDecimal[] result = new BigDecimal[12];
        for (int i = 0; i < 12; i++) result[i] = BigDecimal.ZERO;

        List<Object[]> rows = pagoRepository.sumIngresosMensuales(anio);
        for (Object[] row : rows) {
            int mes = ((Number) row[0]).intValue() - 1; // 0-based
            result[mes] = (BigDecimal) row[1];
        }
        return result;
    }

    @Override
    public long[] nuevosMiembrosMensuales(int anio) {
        long[] result = new long[12];
        List<Object[]> rows = pagoRepository.countMiembrosPorMes(anio);
        for (Object[] row : rows) {
            int mes = ((Number) row[0]).intValue() - 1;
            result[mes] = ((Number) row[1]).longValue();
        }
        return result;
    }

    @Override
    public List<Pago> ultimosPagos(int cantidad) {
        return pagoRepository.findUltimosPagos(PageRequest.of(0, cantidad));
    }
}
