package com.gym.controller;

import com.gym.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/pagos")
@RequiredArgsConstructor
public class AdminPagoController {

    private final PagoService pagoService;

    @GetMapping
    public String pagos(Model model) {
        model.addAttribute("pagos", pagoService.listarTodos());
        model.addAttribute("ingresosMes", pagoService.ingresosMesActual());
        model.addAttribute("ingresosAnio", pagoService.ingresosAnioActual());
        return "admin/pagos";
    }
}
