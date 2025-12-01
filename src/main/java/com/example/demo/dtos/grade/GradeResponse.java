package com.example.demo.dtos.grade;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeResponse {

    private Long id;
    private Double valueDecimal;
    private String feedback;
    private Boolean passed;

    private Long studentId;
    private String studentName; // ✅ Nombre completo del estudiante
    private Long evaluationId;
    private String evaluationName; // ✅ Nombre de la evaluación
    private String courseName; // ✅ Curso al que pertenece la evaluación
}