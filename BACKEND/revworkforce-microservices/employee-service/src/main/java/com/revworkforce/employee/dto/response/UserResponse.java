package com.revworkforce.employee.dto.response;

import com.revworkforce.employee.enums.EmployeeStatus;
import com.revworkforce.employee.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String department;
    private String designation;
    private String employeeCode;
    private Role role;
    private EmployeeStatus status;
    private LocalDate dateOfJoining;
    private ManagerInfo manager;
    private LocalDateTime createdAt;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ManagerInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String designation;
    }
}
