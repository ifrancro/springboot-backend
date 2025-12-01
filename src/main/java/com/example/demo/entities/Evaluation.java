package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "evaluations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;
    
    @NotBlank(message = "El tipo de evaluación es obligatorio")
    @Size(max = 50, message = "El tipo de evaluación no puede superar los 50 caracteres")
    private String evaluationType = "EXAM";
    
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;
    
    private String state = "DRAFT";

    @NotNull(message = "El curso es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToOne(mappedBy = "evaluation", fetch = FetchType.LAZY)
    private Grade grade;
}
