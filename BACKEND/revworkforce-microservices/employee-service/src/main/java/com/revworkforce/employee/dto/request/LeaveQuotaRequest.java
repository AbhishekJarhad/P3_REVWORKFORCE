package com.revworkforce.employee.dto.request;

import com.revworkforce.employee.enums.LeaveType;
import lombok.Data;

@Data
public class LeaveQuotaRequest {
    private LeaveType leaveType;
    private int defaultDays;
    private int year;
}
