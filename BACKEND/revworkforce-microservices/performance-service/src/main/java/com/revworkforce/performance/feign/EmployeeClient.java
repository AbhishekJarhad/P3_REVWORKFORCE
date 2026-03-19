package com.revworkforce.performance.feign;

import com.revworkforce.performance.feign.dto.EmployeeDto;
import com.revworkforce.performance.feign.fallback.EmployeeClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(
    name = "employee-service",
    fallback = EmployeeClientFallback.class
)
public interface EmployeeClient {

    @GetMapping("/api/employees/{id}")
    EmployeeDto getEmployeeById(
            @PathVariable("id") Long id,
            @RequestHeader("X-User-Email") String userEmail
    );

    @GetMapping("/api/employees/my-team")
    List<EmployeeDto> getMyTeam(
            @RequestHeader("X-User-Email") String managerEmail
    );
}
