package com.gym.controller;

import com.gym.entity.GalleryImage;
import com.gym.entity.Promotion;
import com.gym.service.GalleryImageService;
import com.gym.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AdminContentController {

    private final PromotionService promotionService;
    private final GalleryImageService galleryImageService;

    private static final List<String> EXTENSIONES_PERMITIDAS = List.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    // ─── PROMOCIONES ────────────────────────────────────────────────

    @GetMapping("/admin/promociones")
    public String promociones(Model model) {
        model.addAttribute("promociones", promotionService.listarTodos());
        return "admin/promociones";
    }

    @GetMapping("/admin/promociones/nuevo")
    public String nuevaPromocionForm(Model model) {
        model.addAttribute("promotion", new Promotion());
        model.addAttribute("editando", false);
        return "admin/promo-form";
    }

    @PostMapping("/admin/promociones/nuevo")
    public String guardarPromocion(@Valid @ModelAttribute Promotion promotion,
                                   BindingResult result, Model model,
                                   RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("editando", false);
            return "admin/promo-form";
        }
        promotionService.guardar(promotion);
        flash.addFlashAttribute("exito", "✅ Promoción guardada correctamente.");
        return "redirect:/admin/promociones";
    }

    @GetMapping("/admin/promociones/editar/{id}")
    public String editarPromocionForm(@PathVariable Long id, Model model) {
        model.addAttribute("promotion", promotionService.buscarPorId(id));
        model.addAttribute("editando", true);
        return "admin/promo-form";
    }

    @PostMapping("/admin/promociones/editar/{id}")
    public String actualizarPromocion(@PathVariable Long id,
                                      @Valid @ModelAttribute Promotion promotion,
                                      BindingResult result, Model model,
                                      RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("editando", true);
            return "admin/promo-form";
        }
        promotion.setId(id);
        promotionService.guardar(promotion);
        flash.addFlashAttribute("exito", "✅ Promoción actualizada correctamente.");
        return "redirect:/admin/promociones";
    }

    @PostMapping("/admin/promociones/eliminar/{id}")
    public String eliminarPromocion(@PathVariable Long id, RedirectAttributes flash) {
        try {
            promotionService.eliminar(id);
            flash.addFlashAttribute("exito", "✅ Promoción eliminada.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "❌ No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/admin/promociones";
    }

    // ─── GALERÍA ────────────────────────────────────────────────────

    @GetMapping("/admin/galeria")
    public String galeria(Model model) {
        model.addAttribute("galeria", galleryImageService.listarTodos());
        return "admin/galeria";
    }

    @PostMapping("/admin/galeria/subir")
    public String subirImagen(@RequestParam(value = "nombre", required = false) String nombre,
                              @RequestParam(value = "descripcion", required = false) String descripcion,
                              @RequestParam(value = "archivo", required = false) MultipartFile archivo,
                              RedirectAttributes flash) {
        // Validaciones básicas
        if (nombre == null || nombre.isBlank()) {
            flash.addFlashAttribute("error", "❌ El nombre de la imagen es obligatorio.");
            return "redirect:/admin/galeria";
        }
        if (archivo == null || archivo.isEmpty()) {
            flash.addFlashAttribute("error", "❌ Selecciona un archivo para subir.");
            return "redirect:/admin/galeria";
        }

        String extension = getExtension(archivo.getOriginalFilename()).toLowerCase();
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            flash.addFlashAttribute("error", "❌ Formato no permitido. Usa JPG, PNG, GIF o WEBP.");
            return "redirect:/admin/galeria";
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            Files.createDirectories(uploadPath);

            String filename = UUID.randomUUID() + extension;
            Path dest = uploadPath.resolve(filename);

            // Usar InputStream para mayor compatibilidad
            Files.copy(archivo.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

            GalleryImage image = new GalleryImage();
            image.setNombre(nombre.trim());
            image.setDescripcion(descripcion != null ? descripcion.trim() : "");
            image.setFilename(filename);
            image.setActivo(true);
            image.setOrden(0);
            galleryImageService.guardar(image);

            flash.addFlashAttribute("exito", "✅ Imagen subida correctamente.");
        } catch (MaxUploadSizeExceededException e) {
            flash.addFlashAttribute("error", "❌ El archivo es demasiado grande. Máximo permitido: 20MB.");
        } catch (IOException e) {
            flash.addFlashAttribute("error", "❌ No se pudo guardar la imagen: " + e.getMessage());
        }
        return "redirect:/admin/galeria";
    }

    @PostMapping("/admin/galeria/eliminar/{id}")
    public String eliminarImagen(@PathVariable Long id, RedirectAttributes flash) {
        try {
            GalleryImage image = galleryImageService.buscarPorId(id);
            Path filePath = Paths.get(uploadDir).toAbsolutePath().resolve(image.getFilename());
            Files.deleteIfExists(filePath);
            galleryImageService.eliminar(id);
            flash.addFlashAttribute("exito", "✅ Imagen eliminada correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "❌ No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/admin/galeria";
    }

    @GetMapping("/admin/galeria/editar/{id}")
    public String editarImagenForm(@PathVariable Long id, Model model) {
        model.addAttribute("galeriaItem", galleryImageService.buscarPorId(id));
        return "admin/galeria-form";
    }

    @PostMapping("/admin/galeria/editar/{id}")
    public String actualizarImagen(@PathVariable Long id,
                                   @RequestParam("nombre") String nombre,
                                   @RequestParam(value = "descripcion", required = false) String descripcion,
                                   @RequestParam(value = "activo", required = false) Boolean activo,
                                   @RequestParam(value = "archivo", required = false) MultipartFile archivo,
                                   RedirectAttributes flash) {
        try {
            GalleryImage image = galleryImageService.buscarPorId(id);
            image.setNombre(nombre.trim());
            image.setDescripcion(descripcion != null ? descripcion.trim() : "");
            image.setActivo(activo != null ? activo : image.getActivo());

            if (archivo != null && !archivo.isEmpty()) {
                String extension = getExtension(archivo.getOriginalFilename()).toLowerCase();
                if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
                    flash.addFlashAttribute("error", "❌ Formato no permitido. Usa JPG, PNG, GIF o WEBP.");
                    return "redirect:/admin/galeria";
                }

                Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
                Files.createDirectories(uploadPath);

                String filename = UUID.randomUUID() + extension;
                Path dest = uploadPath.resolve(filename);
                Files.copy(archivo.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

                // Borrar imagen anterior
                Path oldFile = uploadPath.resolve(image.getFilename());
                Files.deleteIfExists(oldFile);
                image.setFilename(filename);
            }

            galleryImageService.guardar(image);
            flash.addFlashAttribute("exito", "✅ Imagen actualizada correctamente.");
        } catch (IOException e) {
            flash.addFlashAttribute("error", "❌ No se pudo actualizar: " + e.getMessage());
        }
        return "redirect:/admin/galeria";
    }

    // ─── Utilidad ───────────────────────────────────────────────────

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.'));
    }
}
