package com.example.demo.dtos.student;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {

    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private Boolean active; // ✅ Estado activo/inactivo para Soft Delete

    private List<String> enrolledCourses; // ✅ Cursos en los que está inscrito
}

