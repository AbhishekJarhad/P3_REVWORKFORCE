package com.revworkforce.performance.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ManagerFeedbackRequest {
    private String feedback;
    @Min(1) @Max(5)
    private Integer rating;
}
