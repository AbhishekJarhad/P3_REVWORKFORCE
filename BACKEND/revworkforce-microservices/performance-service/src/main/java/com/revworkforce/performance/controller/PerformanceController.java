package com.revworkforce.performance.controller;

import com.revworkforce.performance.dto.request.GoalRequest;
import com.revworkforce.performance.dto.request.ManagerFeedbackRequest;
import com.revworkforce.performance.dto.request.PerformanceReviewRequest;
import com.revworkforce.performance.dto.response.ApiResponse;
import com.revworkforce.performance.dto.response.GoalResponse;
import com.revworkforce.performance.dto.response.PerformanceReviewResponse;
import com.revworkforce.performance.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse<PerformanceReviewResponse>> createReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PerformanceReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Review created", performanceService.createReview(userDetails.getUsername(), request)));
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<PerformanceReviewResponse>> updateReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody PerformanceReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Review updated", performanceService.updateReview(userDetails.getUsername(), id, request)));
    }

    @PutMapping("/reviews/{id}/submit")
    public ResponseEntity<ApiResponse<PerformanceReviewResponse>> submitReview(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Review submitted", performanceService.submitReview(userDetails.getUsername(), id)));
    }

    @GetMapping("/reviews/my")
    public ResponseEntity<ApiResponse<List<PerformanceReviewResponse>>> getMyReviews(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(performanceService.getMyReviews(userDetails.getUsername())));
    }

    @GetMapping("/reviews/team")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PerformanceReviewResponse>>> getTeamReviews(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(performanceService.getTeamReviews(userDetails.getUsername())));
    }

    @GetMapping("/reviews/team/pending")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PerformanceReviewResponse>>> getPendingTeamReviews(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(performanceService.getPendingTeamReviews(userDetails.getUsername())));
    }

    @PutMapping("/reviews/{id}/feedback")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PerformanceReviewResponse>> addFeedback(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody ManagerFeedbackRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Feedback added", performanceService.addFeedback(userDetails.getUsername(), id, request)));
    }

    @PostMapping("/goals")
    public ResponseEntity<ApiResponse<GoalResponse>> createGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody GoalRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Goal created", performanceService.createGoal(userDetails.getUsername(), request)));
    }

    @PutMapping("/goals/{id}")
    public ResponseEntity<ApiResponse<GoalResponse>> updateGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody GoalRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Goal updated", performanceService.updateGoal(userDetails.getUsername(), id, request)));
    }

    @GetMapping("/goals/my")
    public ResponseEntity<ApiResponse<List<GoalResponse>>> getMyGoals(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(performanceService.getMyGoals(userDetails.getUsername())));
    }

    @GetMapping("/goals/my/{year}")
    public ResponseEntity<ApiResponse<List<GoalResponse>>> getMyGoalsByYear(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable int year) {
        return ResponseEntity.ok(ApiResponse.success(performanceService.getMyGoalsByYear(userDetails.getUsername(), year)));
    }

    @DeleteMapping("/goals/{id}")
    public ResponseEntity<ApiResponse<String>> deleteGoal(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        performanceService.deleteGoal(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success("Goal deleted", null));
    }
}
