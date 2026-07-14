package com.gym.config;

import com.gym.security.GymUserDetailsService;
import com.gym.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration: indica que el contenedor de Spring puede usar esta clase
// como fuente de definiciones de beans (clase de configuración del framework).
@Configuration
// @EnableWebSecurity: permite tener la configuración de Spring Security.
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final GymUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    // @Bean: marca este método de forma que su valor de retorno quede
    // disponible como bean para que Spring lo administre.
    // Aquí se crea el bean que indica que estamos usando BCrypt como
    // codificador de contraseñas (passwordEncoder).
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // @Bean + SecurityFilterChain: define una cadena de filtros que se compara
    // con cada HttpServletRequest para decidir si se le aplica seguridad.
    // HttpSecurity: permite configurar la seguridad basada en la web para
    // solicitudes http específicas (por defecto aplica a todas, pero se puede
    // restringir con .requestMatchers()).
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // .authorizeHttpRequests(): indica a Spring qué rutas se protegen
            // y cuáles no. .anyRequest().authenticated() equivale al concepto
            // de anyRequest().fullyAuthenticated(): toda solicitud no listada
            // arriba requiere que el usuario esté autenticado.
            .authorizeHttpRequests(auth -> auth
                
                .requestMatchers(
                    "/", "/index", "/home",
                    "/css/**", "/js/**", "/images/**", "/media/**",
                    "/webjars/**", "/favicon.ico",
                    "/error", "/error/**",
                    "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**"
                ).permitAll()
                // Endpoint público de solo lectura (Capacidad 2 y 3)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/planes/**").permitAll()
                // Login JWT: público, es donde el dueño envía sus credenciales
                .requestMatchers("/api/auth/**").permitAll()
                // Cualquier otro método sobre /api/** (POST, PUT, DELETE) requiere
                // un JWT válido de un usuario con rol ADMIN (el dueño)
                .requestMatchers("/api/**").hasRole("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/login", "/login/**").permitAll()
                .anyRequest().authenticated()
            )
            // .formLogin(): configura un inicio de sesión basado en formulario.
            // .loginPage(): especifica la ubicación de la página de login.
            // .failureUrl(): a dónde redirigir si falla el login.
            // .permitAll(): todos los usuarios deben poder acceder a esta página.
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            // .logout(): configura el cierre de sesión.
            // .logoutUrl(): URL que dispara el logout.
            // .logoutSuccessUrl(): URL a la que se redirige tras cerrar sesión
            // (por defecto sería "/login?logout").
            // .permitAll(): acceso libre a esta funcionalidad.
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(2) 
            )
            // Registra el filtro JWT para que se ejecute antes del filtro
            // estándar de usuario/contraseña, así puede autenticar las
            // solicitudes a la API que traigan un token válido.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // .passwordEncoder(passwordEncoder()) equivalente conceptual:
        // el AuthenticationProvider (ver arriba) ya usa el bean
        // passwordEncoder() para indicar que las contraseñas se validan
        // usando BCrypt.
        return http.build();
    }
}
