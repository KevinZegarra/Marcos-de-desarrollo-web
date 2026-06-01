package com.gym.controller;

import com.gym.entity.Plan;
import com.gym.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/planes")
@RequiredArgsConstructor
public class AdminPlanController {

    private final PlanService planService;

    @GetMapping
    public String planes(Model model) {
        model.addAttribute("planes", planService.listarTodos());
        return "admin/planes";
    }

    @GetMapping("/nuevo")
    public String nuevoPlanForm(Model model) {
        model.addAttribute("plan", new Plan());
        model.addAttribute("editando", false);
        return "admin/plan-form";
    }

    @PostMapping("/nuevo")
    public String guardarPlan(@Valid @ModelAttribute Plan plan,
                              BindingResult result,
                              Model model,
                              RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("editando", false);
            return "admin/plan-form";
        }
        planService.guardar(plan);
        flash.addFlashAttribute("exito", "Plan guardado correctamente.");
        return "redirect:/admin/planes";
    }

    @GetMapping("/editar/{id}")
    public String editarPlanForm(@PathVariable Long id, Model model) {
        model.addAttribute("plan", planService.buscarPorId(id));
        model.addAttribute("editando", true);
        return "admin/plan-form";
    }

    @PostMapping("/editar/{id}")
    public String actualizarPlan(@PathVariable Long id,
                                 @Valid @ModelAttribute Plan plan,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("editando", true);
            return "admin/plan-form";
        }
        plan.setId(id);
        planService.guardar(plan);
        flash.addFlashAttribute("exito", "Plan actualizado correctamente.");
        return "redirect:/admin/planes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarPlan(@PathVariable Long id, RedirectAttributes flash) {
        try {
            planService.eliminar(id);
            flash.addFlashAttribute("exito", "Plan eliminado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se pudo eliminar el plan: " + e.getMessage());
        }
        return "redirect:/admin/planes";
    }
}
