package com.gym.controller;

import com.gym.service.GalleryImageService;
import com.gym.service.PlanService;
import com.gym.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PlanService planService;
    private final PromotionService promotionService;
    private final GalleryImageService galleryImageService;

    /** Landing page pública */
    @GetMapping({"/", "/index", "/home"})
    public String index(Model model) {
        model.addAttribute("planes", planService.listarActivos());
        model.addAttribute("promociones", promotionService.listarActivas());
        model.addAttribute("galeria", galleryImageService.listarActivas());
        return "index";
    }

    /** Página de login personalizada */
    @GetMapping("/login")
    public String login(
        @RequestParam(value = "error", required = false) String error,
        @RequestParam(value = "logout", required = false) String logout,
        Model model
    ) {
        if (error != null)  model.addAttribute("error", "Usuario o contraseña incorrectos.");
        if (logout != null) model.addAttribute("msg", "Sesión cerrada correctamente.");
        return "auth/login";
    }
}
