package com.flexispot.usage_analytics.repository;

import com.flexispot.usage_analytics.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
