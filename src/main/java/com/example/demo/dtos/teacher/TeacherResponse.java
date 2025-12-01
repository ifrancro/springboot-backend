package com.example.demo.dtos.teacher;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherResponse {

    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String specialty;
    private Boolean active; // ✅ Estado activo/inactivo para Soft Delete

    private List<String> courseNames;
}
