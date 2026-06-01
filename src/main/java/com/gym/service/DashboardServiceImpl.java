package com.gym.service;

import com.gym.dto.DashboardDTO;
import com.gym.repository.ClienteRepository;
import com.gym.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ClienteRepository clienteRepository;
    private final PlanRepository planRepository;
    private final ClienteService clienteService;
    private final PagoService pagoService;

    @Override
    public DashboardDTO obtenerMetricas() {
        int anio = LocalDate.now().getYear();
        DashboardDTO dto = new DashboardDTO();

        dto.setTotalClientes(clienteRepository.count());
        dto.setClientesActivos(clienteService.countActivos());
        dto.setClientesPorVencer(clienteService.countPorVencer());
        dto.setClientesVencidos(clienteService.countVencidos());

        dto.setIngresosMes(pagoService.ingresosMesActual());
        dto.setIngresosAnio(pagoService.ingresosAnioActual());

        dto.setIngresosMensuales(pagoService.ingresosMensualesAnio(anio));
        dto.setNuevosMiembrosMes(pagoService.nuevosMiembrosMensuales(anio));

        // Distribución por plan
        planRepository.findByNombre("Básico")
            .ifPresent(p -> dto.setCountBasico(clienteRepository.findByPlanId(p.getId()).size()));
        planRepository.findByNombre("Pro")
            .ifPresent(p -> dto.setCountPro(clienteRepository.findByPlanId(p.getId()).size()));
        planRepository.findByNombre("Elite")
            .ifPresent(p -> dto.setCountElite(clienteRepository.findByPlanId(p.getId()).size()));

        return dto;
    }
}
