package com.revworkforce.leave.dto.request;

import lombok.Data;

@Data
public class LeaveActionRequest {
    private String action;
    private String comment;
}
