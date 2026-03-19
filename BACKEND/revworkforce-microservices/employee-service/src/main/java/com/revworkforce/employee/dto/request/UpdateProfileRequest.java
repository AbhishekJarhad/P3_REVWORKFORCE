package com.revworkforce.employee.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String phone;
    private String address;
    private String firstName;
    private String lastName;
}
