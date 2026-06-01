package com.gym.service;

import com.gym.entity.Plan;

import java.util.List;

public interface PlanService {

    List<Plan> listarActivos();

    List<Plan> listarTodos();

    Plan buscarPorId(Long id);

    Plan guardar(Plan plan);

    void eliminar(Long id);
}
