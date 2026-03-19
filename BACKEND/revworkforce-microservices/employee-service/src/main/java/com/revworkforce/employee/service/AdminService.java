package com.revworkforce.employee.service;

import com.revworkforce.employee.entity.User;
import com.revworkforce.employee.enums.EmployeeStatus;
import com.revworkforce.employee.enums.Role;
import com.revworkforce.employee.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEmployees", userRepository.count());
        stats.put("activeEmployees", userRepository.countByStatus(EmployeeStatus.ACTIVE));
        stats.put("inactiveEmployees", userRepository.countByStatus(EmployeeStatus.INACTIVE));
        stats.put("totalManagers", userRepository.countByRole(Role.MANAGER));
        stats.put("totalAdmins", userRepository.countByRole(Role.ADMIN));
        return stats;
    }
}
