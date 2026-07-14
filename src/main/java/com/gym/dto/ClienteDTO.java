package com.gym.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;


@Data
public class ClienteDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    private String dni;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @Email(message = "Ingresa un correo válido")
    private String correo;

    @NotNull(message = "Selecciona un plan")
    private Long planId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInscripcion;

    // No @NotBlank aquí — la validación se hace en el controller
    // según si es creación o edición
    private String metodoPago = "Efectivo";
}
