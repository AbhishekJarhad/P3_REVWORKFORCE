package com.revworkforce.leave.dto.response;

import com.revworkforce.leave.enums.LeaveStatus;
import com.revworkforce.leave.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LeaveApplicationResponse {
    private Long id;
    private String employeeName;
    private String employeeCode;
    private String department;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numberOfDays;
    private String reason;
    private String managerComment;
    private LeaveStatus status;
    private String approvedByName;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}
