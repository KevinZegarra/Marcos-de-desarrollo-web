package com.gym.config;

import com.gym.entity.Plan;
import com.gym.entity.Promotion;
import com.gym.entity.Usuario;
import com.gym.repository.PlanRepository;
import com.gym.repository.PromotionRepository;
import com.gym.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;




/**
 * Se ejecuta al iniciar la app.
 * Crea los datos base: planes y usuario administrador.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PlanRepository planRepository;
    private final PromotionRepository promotionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        crearPlanes();
        crearPromociones();
        crearAdminPorDefecto();
    }

    private void crearPlanes() {
        if (planRepository.count() > 0) return; // Ya existen

        Plan basico = new Plan(
            "Básico",
            new BigDecimal("79.00"),
            30,
            "Plan de entrada ideal para comenzar",
            "Acceso a sala de pesas,Vestuarios y duchas,Evaluación inicial"
        );

        Plan pro = new Plan(
            "Pro",
            new BigDecimal("129.00"),
            30,
            "El más popular, con clases grupales incluidas",
            "Acceso a sala de pesas,Vestuarios y duchas,Evaluación inicial,Clases grupales ilimitadas,Acceso 24/7"
        );

        Plan elite = new Plan(
            "Elite",
            new BigDecimal("189.00"),
            30,
            "Experiencia premium con asesoría nutricional",
            "Acceso a sala de pesas,Vestuarios y duchas,Evaluación inicial,Clases grupales ilimitadas,Acceso 24/7,Asesoría nutricional personalizada"
        );

        planRepository.save(basico);
        planRepository.save(pro);
        planRepository.save(elite);

        log.info("✅ Planes creados: Básico (S/79), Pro (S/129), Elite (S/189)");
    }

    private void crearPromociones() {
        if (promotionRepository.count() > 0) return;

        Promotion promo1 = new Promotion();
        promo1.setTitulo("Inscripción Gratis");
        promo1.setDescripcion("Únete hoy y no pagues la inscripción de tu primer mes.");
        promo1.setBotonTexto("Quiero esta oferta");
        promo1.setEnlace("https://wa.me/51999000000");
        promo1.setActivo(true);
        promo1.setOrden(1);

        Promotion promo2 = new Promotion();
        promo2.setTitulo("Trae a un amigo");
        promo2.setDescripcion("Trae a un amigo y ambos obtienen dos semanas gratis.");
        promo2.setBotonTexto("Invitar amigo");
        promo2.setEnlace("https://wa.me/51999000000");
        promo2.setActivo(true);
        promo2.setOrden(2);

        promotionRepository.save(promo1);
        promotionRepository.save(promo2);
    }

    private void crearAdminPorDefecto() {
        if (usuarioRepository.existsByUsername("admin")) return;

        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123")); 
        admin.setRol(Usuario.Rol.ADMIN);
        admin.setActivo(true);
        usuarioRepository.save(admin);

        log.info("✅ Usuario admin creado — usuario: admin / contraseña: admin123");
        log.warn("⚠️  IMPORTANTE: Cambia la contraseña del admin en producción");
    }
}
