package com.example.demo.dtos.evaluation;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotBlank(message = "El tipo de evaluación es obligatorio")
    @Size(max = 50, message = "El tipo de evaluación no puede superar los 50 caracteres")
    private String evaluationType; // "EXAM", "QUIZ", "ASSIGNMENT", etc.
    
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;
    
    @NotBlank(message = "El estado es obligatorio")
    private String state; // "DRAFT", "PUBLISHED", "CLOSED"

    @NotNull(message = "El curso es obligatorio")
    private Long courseId;
}