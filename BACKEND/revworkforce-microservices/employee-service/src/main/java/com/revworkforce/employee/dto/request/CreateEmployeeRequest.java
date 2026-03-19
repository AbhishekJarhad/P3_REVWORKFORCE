package com.revworkforce.employee.dto.request;

import com.revworkforce.employee.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateEmployeeRequest {
    @NotBlank @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String phone;
    private String address;
    private String department;
    private String designation;
    private String employeeCode;
    @NotNull
    private Role role;
    private Long managerId;
    private LocalDate dateOfJoining;
}
