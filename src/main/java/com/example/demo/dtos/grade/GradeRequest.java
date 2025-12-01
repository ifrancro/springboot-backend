package com.example.demo.dtos.grade;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeRequest {

    @NotNull(message = "El valor de la calificación es obligatorio")
    @DecimalMin(value = "0.0", message = "La calificación no puede ser negativa")
    @DecimalMax(value = "100.0", message = "La calificación no puede ser mayor a 100")
    private Double valueDecimal;

    @Size(max = 255, message = "El feedback no puede superar los 255 caracteres")
    private String feedback;

    @NotNull(message = "El estudiante es obligatorio")
    private Long studentId;

    @NotNull(message = "La evaluación es obligatoria")
    private Long evaluationId;
}