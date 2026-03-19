package com.revworkforce.leave.dto.response;

import com.revworkforce.leave.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LeaveBalanceResponse {
    private Long id;
    private LeaveType leaveType;
    private int year;
    private int totalDays;
    private int usedDays;
    private int remainingDays;
}
