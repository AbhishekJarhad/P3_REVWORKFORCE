package com.revworkforce.employee.repository;

import com.revworkforce.employee.entity.User;
import com.revworkforce.employee.enums.EmployeeStatus;
import com.revworkforce.employee.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByManagerId(Long managerId);
    List<User> findByStatus(EmployeeStatus status);
    long countByStatus(EmployeeStatus status);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = ?1")
    long countByRole(Role role);
}
