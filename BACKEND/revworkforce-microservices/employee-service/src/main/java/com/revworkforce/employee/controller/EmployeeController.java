package com.revworkforce.employee.controller;

import com.revworkforce.employee.dto.request.UpdateProfileRequest;
import com.revworkforce.employee.dto.response.ApiResponse;
import com.revworkforce.employee.dto.response.UserResponse;
import com.revworkforce.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getMyProfile(userDetails.getUsername())));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", employeeService.updateProfile(userDetails.getUsername(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllEmployees() {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getAllEmployees()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getEmployee(id)));
    }

    @GetMapping("/managers")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getManagers() {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getManagers()));
    }

    @GetMapping("/my-team")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getMyTeam(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getMyTeam(userDetails.getUsername())));
    }
}
