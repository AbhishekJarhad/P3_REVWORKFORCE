package com.revworkforce.performance.feign.fallback;

import com.revworkforce.performance.feign.EmployeeClient;
import com.revworkforce.performance.feign.dto.EmployeeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class EmployeeClientFallback implements EmployeeClient {

    @Override
    public EmployeeDto getEmployeeById(Long id, String userEmail) {
        log.warn("Employee service unavailable. Returning fallback for employee id: {}", id);
        return EmployeeDto.builder()
                .id(id)
                .firstName("Unavailable")
                .lastName("(Service Down)")
                .email("unavailable@revworkforce.com")
                .build();
    }

    @Override
    public List<EmployeeDto> getMyTeam(String managerEmail) {
        log.warn("Employee service unavailable. Returning empty team list.");
        return Collections.emptyList();
    }
}
