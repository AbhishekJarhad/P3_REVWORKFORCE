package com.revworkforce.auth.config;

import com.revworkforce.auth.entity.User;
import com.revworkforce.auth.enums.EmployeeStatus;
import com.revworkforce.auth.enums.Role;
import com.revworkforce.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin@revworkforce.com")) {
            userRepository.save(User.builder()
                    .email("admin@revworkforce.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("System").lastName("Admin")
                    .role(Role.ADMIN).status(EmployeeStatus.ACTIVE).build());

            userRepository.save(User.builder()
                    .email("manager@revworkforce.com")
                    .password(passwordEncoder.encode("manager123"))
                    .firstName("James").lastName("Wilson")
                    .role(Role.MANAGER).status(EmployeeStatus.ACTIVE).build());

            userRepository.save(User.builder()
                    .email("employee@revworkforce.com")
                    .password(passwordEncoder.encode("employee123"))
                    .firstName("Sarah").lastName("Connor")
                    .role(Role.EMPLOYEE).status(EmployeeStatus.ACTIVE).build());

            userRepository.save(User.builder()
                    .email("john.doe@revworkforce.com")
                    .password(passwordEncoder.encode("employee123"))
                    .firstName("John").lastName("Doe")
                    .role(Role.EMPLOYEE).status(EmployeeStatus.ACTIVE).build());

            log.info("=== Auth Service Test data initialized ===");
            log.info("Admin: admin@revworkforce.com / admin123");
            log.info("Manager: manager@revworkforce.com / manager123");
            log.info("Employee: employee@revworkforce.com / employee123");
        }
    }
}
