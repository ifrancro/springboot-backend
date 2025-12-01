package com.example.demo.dtos.enrollment;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentRequest {

    @NotNull(message = "El estudiante es obligatorio")
    private Long studentId;

    @NotNull(message = "El curso es obligatorio")
    private Long courseId;
    
    private String registrationState; // "ACTIVE", "INACTIVE", "RETIRED"
}
