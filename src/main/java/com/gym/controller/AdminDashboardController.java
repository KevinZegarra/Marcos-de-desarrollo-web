package com.gym.controller;

import com.gym.service.DashboardService;
import com.gym.service.PagoService;
import com.gym.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;
    private final PagoService pagoService;
    private final ClienteService clienteService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("metricas", dashboardService.obtenerMetricas());
        model.addAttribute("ultimosPagos", pagoService.ultimosPagos(8));
        model.addAttribute("porVencer", clienteService.clientesProximosAVencer());
        return "admin/dashboard";
    }

    @GetMapping("/reportes")
    public String reportes(Model model) {
        model.addAttribute("metricas", dashboardService.obtenerMetricas());
        return "admin/reportes";
    }
}
