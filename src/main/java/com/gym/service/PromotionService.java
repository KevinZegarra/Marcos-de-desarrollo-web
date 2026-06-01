package com.gym.service;

import com.gym.entity.Promotion;

import java.util.List;

public interface PromotionService {

    List<Promotion> listarActivas();

    List<Promotion> listarTodos();

    Promotion buscarPorId(Long id);

    Promotion guardar(Promotion promotion);

    void eliminar(Long id);
}
