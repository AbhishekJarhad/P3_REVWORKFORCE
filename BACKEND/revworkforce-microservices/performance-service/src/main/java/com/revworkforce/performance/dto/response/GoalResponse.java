package com.revworkforce.performance.dto.response;

import com.revworkforce.performance.enums.GoalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GoalResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String title;
    private String description;
    private int year;
    private LocalDate targetDate;
    private int progressPercentage;
    private GoalStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
