package com.revworkforce.leave.service;

import com.revworkforce.leave.dto.request.LeaveActionRequest;
import com.revworkforce.leave.dto.request.LeaveApplicationRequest;
import com.revworkforce.leave.dto.request.LeaveQuotaRequest;
import com.revworkforce.leave.dto.response.LeaveApplicationResponse;
import com.revworkforce.leave.dto.response.LeaveBalanceResponse;
import com.revworkforce.leave.entity.LeaveApplication;
import com.revworkforce.leave.entity.LeaveBalance;
import com.revworkforce.leave.entity.LeaveQuota;
import com.revworkforce.leave.entity.User;
import com.revworkforce.leave.enums.LeaveStatus;
import com.revworkforce.leave.enums.LeaveType;
import com.revworkforce.leave.repository.LeaveApplicationRepository;
import com.revworkforce.leave.repository.LeaveBalanceRepository;
import com.revworkforce.leave.repository.LeaveQuotaRepository;
import com.revworkforce.leave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveApplicationRepository leaveRepo;
    private final LeaveBalanceRepository balanceRepo;
    private final LeaveQuotaRepository quotaRepo;
    private final UserRepository userRepository;

    public List<LeaveBalanceResponse> getMyLeaveBalance(String email) {
        User user = getUser(email);
        int year = LocalDate.now().getYear();
        return balanceRepo.findByEmployeeIdAndYear(user.getId(), year).stream()
                .map(this::mapBalance).collect(Collectors.toList());
    }

    @Transactional
    public LeaveApplicationResponse applyLeave(String email, LeaveApplicationRequest request) {
        User user = getUser(email);
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }

        int days = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        int year = request.getStartDate().getYear();

        LeaveBalance balance = balanceRepo.findByEmployeeIdAndLeaveTypeAndYear(user.getId(), request.getLeaveType(), year)
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));

        if (balance.getRemainingDays() < days) {
            throw new RuntimeException("Insufficient leave balance. Available: " + balance.getRemainingDays() + " days");
        }

        LeaveApplication application = LeaveApplication.builder()
                .employee(user).leaveType(request.getLeaveType())
                .startDate(request.getStartDate()).endDate(request.getEndDate())
                .numberOfDays(days).reason(request.getReason())
                .status(LeaveStatus.PENDING).build();

        return mapApplication(leaveRepo.save(application));
    }

    public List<LeaveApplicationResponse> getMyLeaves(String email) {
        User user = getUser(email);
        return leaveRepo.findByEmployeeId(user.getId()).stream().map(this::mapApplication).collect(Collectors.toList());
    }

    @Transactional
    public LeaveApplicationResponse cancelLeave(String email, Long leaveId) {
        User user = getUser(email);
        LeaveApplication application = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
        if (!application.getEmployee().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
        if (application.getStatus() != LeaveStatus.PENDING) throw new RuntimeException("Only pending leaves can be cancelled");
        application.setStatus(LeaveStatus.CANCELLED);
        return mapApplication(leaveRepo.save(application));
    }

    public List<LeaveApplicationResponse> getTeamLeaves(String managerEmail) {
        User manager = getUser(managerEmail);
        return leaveRepo.findByManagerId(manager.getId()).stream().map(this::mapApplication).collect(Collectors.toList());
    }

    public List<LeaveApplicationResponse> getPendingTeamLeaves(String managerEmail) {
        User manager = getUser(managerEmail);
        return leaveRepo.findByManagerIdAndStatus(manager.getId(), LeaveStatus.PENDING).stream()
                .map(this::mapApplication).collect(Collectors.toList());
    }

    @Transactional
    public LeaveApplicationResponse processLeave(String managerEmail, Long leaveId, LeaveActionRequest request) {
        User manager = getUser(managerEmail);
        LeaveApplication application = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
        if (!application.getEmployee().getManager().getId().equals(manager.getId()))
            throw new RuntimeException("Unauthorized to process this leave");
        if (application.getStatus() != LeaveStatus.PENDING)
            throw new RuntimeException("Leave is not in pending status");

        if ("APPROVE".equalsIgnoreCase(request.getAction())) {
            application.setStatus(LeaveStatus.APPROVED);
            application.setApprovedBy(manager);
            LeaveBalance balance = balanceRepo.findByEmployeeIdAndLeaveTypeAndYear(
                            application.getEmployee().getId(), application.getLeaveType(),
                            application.getStartDate().getYear())
                    .orElseThrow(() -> new RuntimeException("Balance not found"));
            balance.setUsedDays(balance.getUsedDays() + application.getNumberOfDays());
            balanceRepo.save(balance);
        } else if ("REJECT".equalsIgnoreCase(request.getAction())) {
            application.setStatus(LeaveStatus.REJECTED);
            application.setApprovedBy(manager);
        } else {
            throw new RuntimeException("Invalid action: " + request.getAction());
        }

        application.setManagerComment(request.getComment());
        return mapApplication(leaveRepo.save(application));
    }

    public List<LeaveApplicationResponse> getAllLeaves() {
        return leaveRepo.findAll().stream().map(this::mapApplication).collect(Collectors.toList());
    }

    public LeaveQuota configureLeaveQuota(LeaveQuotaRequest request) {
        LeaveQuota quota = quotaRepo.findByLeaveType(request.getLeaveType())
                .orElse(LeaveQuota.builder().leaveType(request.getLeaveType()).build());
        quota.setDefaultDays(request.getDefaultDays());
        quota.setYear(request.getYear() > 0 ? request.getYear() : LocalDate.now().getYear());
        return quotaRepo.save(quota);
    }

    public List<LeaveQuota> getAllQuotas() {
        return quotaRepo.findAll();
    }

    @Transactional
    public void resetLeaveBalancesForNewYear(int year) {
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            for (LeaveType leaveType : LeaveType.values()) {
                int days = quotaRepo.findByLeaveType(leaveType).map(LeaveQuota::getDefaultDays)
                        .orElseGet(() -> switch (leaveType) {
                            case CASUAL -> 12; case SICK -> 10; case PAID -> 15;
                        });
                // Update existing balance OR create new one for every employee
                LeaveBalance balance = balanceRepo.findByEmployeeIdAndLeaveTypeAndYear(user.getId(), leaveType, year)
                        .orElse(LeaveBalance.builder()
                                .employee(user).leaveType(leaveType).year(year).build());
                balance.setTotalDays(days);
                balance.setUsedDays(0);
                balanceRepo.save(balance);
            }
        }
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private LeaveBalanceResponse mapBalance(LeaveBalance b) {
        return LeaveBalanceResponse.builder().id(b.getId()).leaveType(b.getLeaveType())
                .year(b.getYear()).totalDays(b.getTotalDays()).usedDays(b.getUsedDays())
                .remainingDays(b.getRemainingDays()).build();
    }

    private LeaveApplicationResponse mapApplication(LeaveApplication a) {
        return LeaveApplicationResponse.builder().id(a.getId())
                .employeeName(a.getEmployee().getFullName()).employeeCode(a.getEmployee().getEmployeeCode())
                .department(a.getEmployee().getDepartment()).leaveType(a.getLeaveType())
                .startDate(a.getStartDate()).endDate(a.getEndDate()).numberOfDays(a.getNumberOfDays())
                .reason(a.getReason()).managerComment(a.getManagerComment()).status(a.getStatus())
                .approvedByName(a.getApprovedBy() != null ? a.getApprovedBy().getFullName() : null)
                .appliedAt(a.getAppliedAt()).updatedAt(a.getUpdatedAt()).build();
    }
}