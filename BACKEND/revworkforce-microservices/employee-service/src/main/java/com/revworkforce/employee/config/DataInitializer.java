package com.revworkforce.employee.config;

import com.revworkforce.employee.entity.User;
import com.revworkforce.employee.enums.EmployeeStatus;
import com.revworkforce.employee.enums.Role;
import com.revworkforce.employee.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin@revworkforce.com")) {
            User admin = userRepository.save(User.builder()
                    .email("admin@revworkforce.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("System").lastName("Admin")
                    .employeeCode("EMP001").department("HR").designation("HR Administrator")
                    .role(Role.ADMIN).status(EmployeeStatus.ACTIVE)
                    .dateOfJoining(LocalDate.of(2020, 1, 1)).build());

            User manager = userRepository.save(User.builder()
                    .email("manager@revworkforce.com")
                    .password(passwordEncoder.encode("manager123"))
                    .firstName("James").lastName("Wilson")
                    .phone("+91-9876543210").employeeCode("EMP002")
                    .department("Engineering").designation("Engineering Manager")
                    .role(Role.MANAGER).status(EmployeeStatus.ACTIVE).manager(admin)
                    .dateOfJoining(LocalDate.of(2021, 3, 15)).build());

            userRepository.save(User.builder()
                    .email("employee@revworkforce.com")
                    .password(passwordEncoder.encode("employee123"))
                    .firstName("Sarah").lastName("Connor")
                    .phone("+91-9876543211").address("123 Main St, Pune, Maharashtra")
                    .employeeCode("EMP003").department("Engineering").designation("Senior Software Engineer")
                    .role(Role.EMPLOYEE).status(EmployeeStatus.ACTIVE).manager(manager)
                    .dateOfJoining(LocalDate.of(2022, 6, 1)).build());

            userRepository.save(User.builder()
                    .email("john.doe@revworkforce.com")
                    .password(passwordEncoder.encode("employee123"))
                    .firstName("John").lastName("Doe")
                    .phone("+91-9876543212").address("456 Park Ave, Mumbai, Maharashtra")
                    .employeeCode("EMP004").department("Engineering").designation("Software Engineer")
                    .role(Role.EMPLOYEE).status(EmployeeStatus.ACTIVE).manager(manager)
                    .dateOfJoining(LocalDate.of(2023, 1, 10)).build());

            log.info("=== Employee Service test data initialized ===");
        }
    }
}
