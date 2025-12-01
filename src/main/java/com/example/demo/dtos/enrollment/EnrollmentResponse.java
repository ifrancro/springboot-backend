package com.example.demo.dtos.enrollment;

import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponse {

    private Long id;
    private Date registrationDate;
    private String registrationState;

    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
}