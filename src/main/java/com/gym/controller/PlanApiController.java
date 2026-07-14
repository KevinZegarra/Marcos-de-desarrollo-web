package com.gym.controller;

import com.gym.entity.Plan;
import com.gym.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Capacidad 2: @RestController con endpoints GET usando @PathVariable y @RequestParam
// Capacidad 3: retornar objetos Java como JSON y manejar casos 404 con ResponseEntity
@RestController
@RequestMapping("/api/planes")
@RequiredArgsConstructor
public class PlanApiController {

    private final PlanRepository planRepository;

    // GET /api/planes            -> lista todos los planes (o filtra por nombre si se envía @RequestParam)
    // GET /api/planes?nombre=Oro -> filtra por nombre exacto
    @GetMapping
    public ResponseEntity<List<Plan>> listarPlanes(
            @RequestParam(required = false) String nombre) {

        if (nombre != null && !nombre.isBlank()) {
            return planRepository.findByNombre(nombre)
                    .map(plan -> ResponseEntity.ok(List.of(plan)))
                    .orElse(ResponseEntity.notFound().build());
        }

        return ResponseEntity.ok(planRepository.findAll());
    }

    // GET /api/planes/{id} -> retorna un plan como JSON, o 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<Plan> obtenerPlanPorId(@PathVariable Long id) {
        return planRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/planes -> crea un plan nuevo.
    // Protegido en SecurityConfig con hasRole("ADMIN"): solo el dueño,
    // autenticado con un JWT válido (obtenido en /api/auth/login), puede usarlo.
    @PostMapping
    public ResponseEntity<Plan> crearPlan(@RequestBody Plan plan) {
        Plan guardado = planRepository.save(plan);
        return ResponseEntity.ok(guardado);
    }
}
