package com.revworkforce.leave.dto.request;

import com.revworkforce.leave.enums.LeaveType;
import lombok.Data;

@Data
public class LeaveQuotaRequest {
    private LeaveType leaveType;
    private int defaultDays;
    private int year;
}
