package com.academic.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScoreResponse {

    private Long id;

    private String courseName;

    private BigDecimal scoreTotal;

    private BigDecimal gradePoint;

    private BigDecimal credits;

    private String semester;

    private LocalDateTime createdAt;

}
