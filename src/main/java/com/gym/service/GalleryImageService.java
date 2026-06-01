package com.gym.service;

import com.gym.entity.GalleryImage;

import java.util.List;

public interface GalleryImageService {

    List<GalleryImage> listarActivas();

    List<GalleryImage> listarTodos();

    GalleryImage buscarPorId(Long id);

    GalleryImage guardar(GalleryImage galleryImage);

    void eliminar(Long id);
}
