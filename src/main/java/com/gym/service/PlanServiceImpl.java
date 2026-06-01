package com.gym.service;

import com.gym.entity.Plan;
import com.gym.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import java.util.Objects;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    @Override
    public List<Plan> listarActivos() {
        return planRepository.findByActivoTrue();
    }

    @Override
    public List<Plan> listarTodos() {
        return planRepository.findAll();
    }

    @Override
    public Plan buscarPorId(Long id) {
        return planRepository.findById(Objects.requireNonNull(id, "Plan id es obligatorio"))
            .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
    }

    @Override
    public Plan guardar(Plan plan) {
        return planRepository.save(Objects.requireNonNull(plan, "Plan no puede ser nulo"));
    }

    @Override
    public void eliminar(Long id) {
        planRepository.deleteById(Objects.requireNonNull(id, "Plan id es obligatorio"));
    }
}
