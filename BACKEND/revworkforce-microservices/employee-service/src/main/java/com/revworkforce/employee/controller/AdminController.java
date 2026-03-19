package com.revworkforce.employee.controller;

import com.revworkforce.employee.dto.request.CreateEmployeeRequest;
import com.revworkforce.employee.dto.response.ApiResponse;
import com.revworkforce.employee.dto.response.UserResponse;
import com.revworkforce.employee.service.AdminService;
import com.revworkforce.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EmployeeService employeeService;
    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getDashboardStats()));
    }

    @PostMapping("/employees")
    public ResponseEntity<ApiResponse<UserResponse>> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Employee created successfully", employeeService.createEmployee(request)));
    }

    @PutMapping("/employees/{id}/assign-manager/{managerId}")
    public ResponseEntity<ApiResponse<UserResponse>> assignManager(@PathVariable Long id, @PathVariable Long managerId) {
        return ResponseEntity.ok(ApiResponse.success("Manager assigned", employeeService.assignManager(id, managerId)));
    }

    @PutMapping("/employees/{id}/toggle-status")
    public ResponseEntity<ApiResponse<UserResponse>> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Status updated", employeeService.toggleStatus(id)));
    }
}
