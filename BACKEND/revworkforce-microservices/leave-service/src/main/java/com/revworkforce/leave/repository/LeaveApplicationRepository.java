package com.revworkforce.leave.repository;

import com.revworkforce.leave.entity.LeaveApplication;
import com.revworkforce.leave.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByEmployeeId(Long employeeId);

    @Query("SELECT la FROM LeaveApplication la WHERE la.employee.manager.id = ?1")
    List<LeaveApplication> findByManagerId(Long managerId);

    @Query("SELECT la FROM LeaveApplication la WHERE la.employee.manager.id = ?1 AND la.status = ?2")
    List<LeaveApplication> findByManagerIdAndStatus(Long managerId, LeaveStatus status);
}
