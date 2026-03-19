package com.revworkforce.performance.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmployeeDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String department;
    private String employeeCode;
    private String designation;
    private String role;
    private String status;
    private ManagerInfo manager;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ManagerInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
    }
}
