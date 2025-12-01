package com.example.demo.dtos.course;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponse {

    private Long id;
    private String name;
    private String description;
    private Integer capacity;
    private String status; // ✅ Estado del curso (OPEN, CLOSED, ARCHIVED)

    private Long teacherId;
    private String teacherName;
    private List<String> enrolledStudents;
}
