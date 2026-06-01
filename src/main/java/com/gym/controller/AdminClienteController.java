package com.gym.controller;

import com.gym.dto.ClienteDTO;
import com.gym.entity.Cliente;
import com.gym.service.ClienteService;
import com.gym.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/clientes")
@RequiredArgsConstructor
public class AdminClienteController {

    private final ClienteService clienteService;
    private final PlanService planService;

    @GetMapping
    public String clientes(@RequestParam(required = false) String q, Model model) {
        List<Cliente> lista = (q != null && !q.isBlank())
                ? clienteService.buscar(q)
                : clienteService.listarTodos();

        model.addAttribute("clientes", lista);
        model.addAttribute("q", q);
        model.addAttribute("planes", planService.listarActivos());
        return "admin/clientes";
    }

    @GetMapping("/nuevo")
    public String nuevoClienteForm(Model model) {
        model.addAttribute("clienteDTO", new ClienteDTO());
        model.addAttribute("planes", planService.listarActivos());
        model.addAttribute("editando", false);
        return "admin/cliente-form";
    }

    @PostMapping("/nuevo")
    public String guardarCliente(@Valid @ModelAttribute("clienteDTO") ClienteDTO clienteDTO,
            BindingResult result,
            Model model,
            RedirectAttributes flash) {

        // Validar metodoPago solo en creación
        if (clienteDTO.getMetodoPago() == null || clienteDTO.getMetodoPago().isBlank()) {
            result.rejectValue("metodoPago", "NotBlank", "Selecciona un método de pago");
        }

        if (result.hasErrors()) {
            model.addAttribute("planes", planService.listarActivos());
            model.addAttribute("editando", false);

            System.out.println("clienteDTO = " + clienteDTO);

            return "admin/cliente-form";
        }
        try {
            clienteService.crear(clienteDTO);
            flash.addFlashAttribute("exito", "✅ Cliente registrado exitosamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "❌ Error al registrar: " + e.getMessage());
        }
        return "redirect:/admin/clientes";
    }

    @GetMapping("/editar/{id}")
    public String editarClienteForm(@PathVariable Long id, Model model) {
        Cliente c = clienteService.buscarPorId(id);

        ClienteDTO dto = new ClienteDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setApellido(c.getApellido());
        dto.setDni(c.getDni());
        dto.setTelefono(c.getTelefono());
        dto.setCorreo(c.getCorreo());
        dto.setPlanId(c.getPlan() != null ? c.getPlan().getId() : null);
        dto.setFechaInscripcion(c.getFechaInscripcion());

        model.addAttribute("clienteDTO", dto);
        model.addAttribute("planes", planService.listarActivos());
        model.addAttribute("editando", true);

        return "admin/cliente-form";
    }

    @PostMapping("/editar/{id}")
    public String actualizarCliente(@PathVariable Long id,
            @Valid @ModelAttribute("clienteDTO") ClienteDTO clienteDTO,
            BindingResult result,
            Model model,
            RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("planes", planService.listarActivos());
            model.addAttribute("editando", true);
            return "admin/cliente-form";
        }
        try {
            clienteService.actualizar(id, clienteDTO);
            flash.addFlashAttribute("exito", "✅ Cliente actualizado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "❌ Error: " + e.getMessage());
        }
        return "redirect:/admin/clientes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id, RedirectAttributes flash) {
        try {
            clienteService.eliminar(id);
            flash.addFlashAttribute("exito", "✅ Cliente eliminado.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "❌ No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/admin/clientes";
    }

    @PostMapping("/renovar/{id}")
    public String renovarCliente(@PathVariable Long id,
            @RequestParam Long planId,
            @RequestParam(defaultValue = "Efectivo") String metodoPago,
            RedirectAttributes flash) {
        try {
            clienteService.renovar(id, planId, metodoPago);
            flash.addFlashAttribute("exito", "✅ Membresía renovada exitosamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "❌ Error al renovar: " + e.getMessage());
        }
        return "redirect:/admin/clientes";
    }
}
