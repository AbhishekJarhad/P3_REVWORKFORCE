package com.revworkforce.leave.config;

import com.revworkforce.leave.entity.LeaveBalance;
import com.revworkforce.leave.entity.LeaveQuota;
import com.revworkforce.leave.entity.User;
import com.revworkforce.leave.enums.LeaveType;
import com.revworkforce.leave.repository.LeaveBalanceRepository;
import com.revworkforce.leave.repository.LeaveQuotaRepository;
import com.revworkforce.leave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final LeaveQuotaRepository leaveQuotaRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        initLeaveQuotas();
        initLeaveBalances();
    }

    private void initLeaveQuotas() {
        if (leaveQuotaRepository.count() == 0) {
            leaveQuotaRepository.save(LeaveQuota.builder().leaveType(LeaveType.CASUAL).defaultDays(12).year(LocalDate.now().getYear()).build());
            leaveQuotaRepository.save(LeaveQuota.builder().leaveType(LeaveType.SICK).defaultDays(10).year(LocalDate.now().getYear()).build());
            leaveQuotaRepository.save(LeaveQuota.builder().leaveType(LeaveType.PAID).defaultDays(15).year(LocalDate.now().getYear()).build());
            log.info("Leave quotas initialized");
        }
    }

    private void initLeaveBalances() {
        // Initialize leave balances for all existing employees if they don't have any
        List<User> users = userRepository.findAll();
        int year = LocalDate.now().getYear();
        for (User user : users) {
            for (LeaveType leaveType : LeaveType.values()) {
                boolean exists = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeAndYear(user.getId(), leaveType, year).isPresent();
                if (!exists) {
                    int days = switch (leaveType) {
                        case CASUAL -> 12;
                        case SICK -> 10;
                        case PAID -> 15;
                    };
                    leaveBalanceRepository.save(LeaveBalance.builder()
                            .employee(user).leaveType(leaveType).year(year).totalDays(days).usedDays(0).build());
                }
            }
        }
        log.info("Leave balances initialized for {} employees", users.size());
    }
}
