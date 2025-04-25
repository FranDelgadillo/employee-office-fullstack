package com.nttdata.peru.employee_office_api.repository;

import com.nttdata.peru.employee_office_api.model.Employee;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface EmployeeRepository extends ReactiveCrudRepository<Employee, Long> {
    Mono<Employee> findByDni(String dni);
}