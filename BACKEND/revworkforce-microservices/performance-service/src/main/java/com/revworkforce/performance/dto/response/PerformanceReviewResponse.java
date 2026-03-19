package com.revworkforce.performance.dto.response;

import com.revworkforce.performance.enums.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PerformanceReviewResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String managerName;
    private int reviewYear;
    private String reviewPeriod;
    private String selfAssessment;
    private String managerFeedback;
    private Integer rating;
    private ReviewStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
}
