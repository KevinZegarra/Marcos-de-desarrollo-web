package com.gym.service;

import com.gym.entity.Promotion;
import com.gym.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import java.util.Objects;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    public List<Promotion> listarActivas() {
        return promotionRepository.findByActivoTrueOrderByOrdenAsc();
    }

    @Override
    public List<Promotion> listarTodos() {
        return promotionRepository.findAll();
    }

    @Override
    public Promotion buscarPorId(Long id) {
        return promotionRepository.findById(Objects.requireNonNull(id, "Promoción id es obligatorio"))
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada: " + id));
    }

    @Override
    public Promotion guardar(Promotion promotion) {
        return promotionRepository.save(Objects.requireNonNull(promotion, "Promoción no puede ser nula"));
    }

    @Override
    public void eliminar(Long id) {
        promotionRepository.deleteById(Objects.requireNonNull(id, "Promoción id es obligatorio"));
    }
}
