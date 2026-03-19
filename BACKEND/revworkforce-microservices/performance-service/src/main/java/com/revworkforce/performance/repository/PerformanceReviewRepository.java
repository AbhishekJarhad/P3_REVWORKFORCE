package com.revworkforce.performance.repository;

import com.revworkforce.performance.entity.PerformanceReview;
import com.revworkforce.performance.enums.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    List<PerformanceReview> findByEmployeeId(Long employeeId);

    @Query("SELECT pr FROM PerformanceReview pr WHERE pr.employee.manager.id = ?1")
    List<PerformanceReview> findByManagerId(Long managerId);

    @Query("SELECT pr FROM PerformanceReview pr WHERE pr.employee.manager.id = ?1 AND pr.status = ?2")
    List<PerformanceReview> findByManagerIdAndStatus(Long managerId, ReviewStatus status);
}
