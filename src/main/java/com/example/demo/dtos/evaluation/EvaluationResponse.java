package com.example.demo.dtos.evaluation;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationResponse {

    private Long id;
    private String name;
    private String evaluationType;
    private String description;
    private String state;

    private Long courseId;
    private String courseName;
}