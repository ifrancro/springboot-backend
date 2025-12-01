package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grades")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "El valor de la calificación es obligatorio")
    @DecimalMin(value = "0.0", message = "La calificación no puede ser negativa")
    @DecimalMax(value = "100.0", message = "La calificación no puede ser mayor a 100")
    private Double valueDecimal;
    
    @Size(max = 255, message = "El feedback no puede superar los 255 caracteres")
    private String feedback;
    
    private boolean passed;

    @NotNull(message = "El estudiante es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @NotNull(message = "La evaluación es obligatoria")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_id")
    private Evaluation evaluation;
}
