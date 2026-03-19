package com.revworkforce.leave.controller;

import com.revworkforce.leave.dto.request.LeaveActionRequest;
import com.revworkforce.leave.dto.request.LeaveApplicationRequest;
import com.revworkforce.leave.dto.request.LeaveQuotaRequest;
import com.revworkforce.leave.dto.response.ApiResponse;
import com.revworkforce.leave.dto.response.LeaveApplicationResponse;
import com.revworkforce.leave.dto.response.LeaveBalanceResponse;
import com.revworkforce.leave.entity.LeaveQuota;
import com.revworkforce.leave.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<List<LeaveBalanceResponse>>> getBalance(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(leaveService.getMyLeaveBalance(userDetails.getUsername())));
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> applyLeave(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody LeaveApplicationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Leave applied", leaveService.applyLeave(userDetails.getUsername(), request)));
    }

    @GetMapping("/my-leaves")
    public ResponseEntity<ApiResponse<List<LeaveApplicationResponse>>> getMyLeaves(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(leaveService.getMyLeaves(userDetails.getUsername())));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> cancelLeave(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Leave cancelled", leaveService.cancelLeave(userDetails.getUsername(), id)));
    }

    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LeaveApplicationResponse>>> getTeamLeaves(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(leaveService.getTeamLeaves(userDetails.getUsername())));
    }

    @GetMapping("/team/pending")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LeaveApplicationResponse>>> getPendingTeamLeaves(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(leaveService.getPendingTeamLeaves(userDetails.getUsername())));
    }

    @PutMapping("/{id}/process")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> processLeave(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody LeaveActionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Leave processed", leaveService.processLeave(userDetails.getUsername(), id, request)));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<LeaveApplicationResponse>>> getAllLeaves() {
        return ResponseEntity.ok(ApiResponse.success(leaveService.getAllLeaves()));
    }

    // Admin endpoints for leave quotas
    @PostMapping("/admin/quotas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LeaveQuota>> configureLeaveQuota(@RequestBody LeaveQuotaRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Leave quota configured", leaveService.configureLeaveQuota(request)));
    }

    @GetMapping("/admin/quotas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<LeaveQuota>>> getLeaveQuotas() {
        return ResponseEntity.ok(ApiResponse.success(leaveService.getAllQuotas()));
    }

    @PostMapping("/admin/reset-balances/{year}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> resetLeaveBalances(@PathVariable int year) {
        leaveService.resetLeaveBalancesForNewYear(year);
        return ResponseEntity.ok(ApiResponse.success("Leave balances reset for year " + year, null));
    }
}
