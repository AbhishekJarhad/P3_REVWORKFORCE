package com.revworkforce.leave.dto.request;

import com.revworkforce.leave.enums.LeaveType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LeaveApplicationRequest {
    @NotNull private LeaveType leaveType;
    @NotNull private LocalDate startDate;
    @NotNull private LocalDate endDate;
    private String reason;
}
