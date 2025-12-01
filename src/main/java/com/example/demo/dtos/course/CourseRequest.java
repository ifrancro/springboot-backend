package com.example.demo.dtos.course;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequest {

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre del curso debe tener entre 2 y 100 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    @Max(value = 100, message = "La capacidad no puede ser mayor a 100")
    private Integer capacity;

    @NotNull(message = "El profesor es obligatorio")
    private Long teacherId; // FK del profesor

    private String status; // OPEN, CLOSED, ARCHIVED - Permitir cambiar estado
}
