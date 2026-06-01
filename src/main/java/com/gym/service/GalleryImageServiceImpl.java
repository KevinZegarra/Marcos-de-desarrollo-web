package com.gym.service;

import com.gym.entity.GalleryImage;
import com.gym.repository.GalleryImageRepository;
import lombok.RequiredArgsConstructor;
import java.util.Objects;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GalleryImageServiceImpl implements GalleryImageService {

    private final GalleryImageRepository galleryImageRepository;

    @Override
    public List<GalleryImage> listarActivas() {
        return galleryImageRepository.findByActivoTrueOrderByOrdenAsc();
    }

    @Override
    public List<GalleryImage> listarTodos() {
        return galleryImageRepository.findAll();
    }

    @Override
    public GalleryImage buscarPorId(Long id) {
        return galleryImageRepository.findById(Objects.requireNonNull(id, "Imagen id es obligatorio"))
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada: " + id));
    }

    @Override
    public GalleryImage guardar(GalleryImage galleryImage) {
        return galleryImageRepository.save(Objects.requireNonNull(galleryImage, "Imagen no puede ser nula"));
    }

    @Override
    public void eliminar(Long id) {
        galleryImageRepository.deleteById(Objects.requireNonNull(id, "Imagen id es obligatorio"));
    }
}
