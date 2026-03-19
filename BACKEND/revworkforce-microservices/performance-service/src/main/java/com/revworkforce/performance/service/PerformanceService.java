package com.revworkforce.performance.service;

import com.revworkforce.performance.dto.request.GoalRequest;
import com.revworkforce.performance.dto.request.ManagerFeedbackRequest;
import com.revworkforce.performance.dto.request.PerformanceReviewRequest;
import com.revworkforce.performance.dto.response.GoalResponse;
import com.revworkforce.performance.dto.response.PerformanceReviewResponse;
import com.revworkforce.performance.entity.Goal;
import com.revworkforce.performance.entity.PerformanceReview;
import com.revworkforce.performance.entity.User;
import com.revworkforce.performance.enums.ReviewStatus;
import com.revworkforce.performance.repository.GoalRepository;
import com.revworkforce.performance.repository.PerformanceReviewRepository;
import com.revworkforce.performance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceReviewRepository reviewRepo;
    private final GoalRepository goalRepo;
    private final UserRepository userRepository;

    public PerformanceReviewResponse createReview(String email, PerformanceReviewRequest request) {
        User user = getUser(email);
        PerformanceReview review = PerformanceReview.builder()
                .employee(user).manager(user.getManager())
                .reviewYear(request.getReviewYear()).reviewPeriod(request.getReviewPeriod())
                .selfAssessment(request.getSelfAssessment()).status(ReviewStatus.DRAFT).build();
        return mapReview(reviewRepo.save(review));
    }

    @Transactional
    public PerformanceReviewResponse updateReview(String email, Long reviewId, PerformanceReviewRequest request) {
        User user = getUser(email);
        PerformanceReview review = reviewRepo.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
        if (!review.getEmployee().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
        if (request.getSelfAssessment() != null) review.setSelfAssessment(request.getSelfAssessment());
        if (request.getReviewPeriod() != null) review.setReviewPeriod(request.getReviewPeriod());
        return mapReview(reviewRepo.save(review));
    }

    @Transactional
    public PerformanceReviewResponse submitReview(String email, Long reviewId) {
        User user = getUser(email);
        PerformanceReview review = reviewRepo.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
        if (!review.getEmployee().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
        if (review.getStatus() != ReviewStatus.DRAFT) throw new RuntimeException("Review already submitted");
        review.setStatus(ReviewStatus.SUBMITTED);
        review.setSubmittedAt(LocalDateTime.now());
        return mapReview(reviewRepo.save(review));
    }

    public List<PerformanceReviewResponse> getMyReviews(String email) {
        User user = getUser(email);
        return reviewRepo.findByEmployeeId(user.getId()).stream().map(this::mapReview).collect(Collectors.toList());
    }

    public List<PerformanceReviewResponse> getTeamReviews(String managerEmail) {
        User manager = getUser(managerEmail);
        return reviewRepo.findByManagerId(manager.getId()).stream().map(this::mapReview).collect(Collectors.toList());
    }

    public List<PerformanceReviewResponse> getPendingTeamReviews(String managerEmail) {
        User manager = getUser(managerEmail);
        return reviewRepo.findByManagerIdAndStatus(manager.getId(), ReviewStatus.SUBMITTED).stream()
                .map(this::mapReview).collect(Collectors.toList());
    }

    @Transactional
    public PerformanceReviewResponse addFeedback(String managerEmail, Long reviewId, ManagerFeedbackRequest request) {
        User manager = getUser(managerEmail);
        PerformanceReview review = reviewRepo.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
        if (review.getEmployee().getManager() == null || !review.getEmployee().getManager().getId().equals(manager.getId()))
            throw new RuntimeException("Unauthorized");
        review.setManagerFeedback(request.getFeedback());
        review.setRating(request.getRating());
        review.setStatus(ReviewStatus.REVIEWED);
        review.setReviewedAt(LocalDateTime.now());
        return mapReview(reviewRepo.save(review));
    }

    public GoalResponse createGoal(String email, GoalRequest request) {
        User user = getUser(email);
        Goal goal = Goal.builder().employee(user).title(request.getTitle()).description(request.getDescription())
                .year(request.getYear()).targetDate(request.getTargetDate())
                .progressPercentage(request.getProgressPercentage()).status(request.getStatus()).build();
        return mapGoal(goalRepo.save(goal));
    }

    @Transactional
    public GoalResponse updateGoal(String email, Long goalId, GoalRequest request) {
        User user = getUser(email);
        Goal goal = goalRepo.findById(goalId).orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getEmployee().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
        if (request.getTitle() != null) goal.setTitle(request.getTitle());
        if (request.getDescription() != null) goal.setDescription(request.getDescription());
        if (request.getTargetDate() != null) goal.setTargetDate(request.getTargetDate());
        goal.setProgressPercentage(request.getProgressPercentage());
        if (request.getStatus() != null) goal.setStatus(request.getStatus());
        return mapGoal(goalRepo.save(goal));
    }

    public List<GoalResponse> getMyGoals(String email) {
        User user = getUser(email);
        return goalRepo.findByEmployeeId(user.getId()).stream().map(this::mapGoal).collect(Collectors.toList());
    }

    public List<GoalResponse> getMyGoalsByYear(String email, int year) {
        User user = getUser(email);
        return goalRepo.findByEmployeeIdAndYear(user.getId(), year).stream().map(this::mapGoal).collect(Collectors.toList());
    }

    public void deleteGoal(String email, Long goalId) {
        User user = getUser(email);
        Goal goal = goalRepo.findById(goalId).orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getEmployee().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
        goalRepo.delete(goal);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private PerformanceReviewResponse mapReview(PerformanceReview r) {
        return PerformanceReviewResponse.builder().id(r.getId()).employeeId(r.getEmployee().getId())
                .employeeName(r.getEmployee().getFullName())
                .managerName(r.getManager() != null ? r.getManager().getFullName() : null)
                .reviewYear(r.getReviewYear()).reviewPeriod(r.getReviewPeriod())
                .selfAssessment(r.getSelfAssessment()).managerFeedback(r.getManagerFeedback())
                .rating(r.getRating()).status(r.getStatus()).createdAt(r.getCreatedAt())
                .submittedAt(r.getSubmittedAt()).reviewedAt(r.getReviewedAt()).build();
    }

    private GoalResponse mapGoal(Goal g) {
        return GoalResponse.builder().id(g.getId()).employeeId(g.getEmployee().getId())
                .employeeName(g.getEmployee().getFullName()).title(g.getTitle()).description(g.getDescription())
                .year(g.getYear()).targetDate(g.getTargetDate()).progressPercentage(g.getProgressPercentage())
                .status(g.getStatus()).createdAt(g.getCreatedAt()).updatedAt(g.getUpdatedAt()).build();
    }
}
