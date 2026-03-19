package com.revworkforce.leave.repository;

import com.revworkforce.leave.entity.LeaveQuota;
import com.revworkforce.leave.enums.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaveQuotaRepository extends JpaRepository<LeaveQuota, Long> {
    Optional<LeaveQuota> findByLeaveType(LeaveType leaveType);
}
