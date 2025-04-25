package com.nttdata.peru.employee_office_api.repository;

import com.nttdata.peru.employee_office_api.model.EmployeeOffice;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface EmployeeOfficeRepository extends ReactiveCrudRepository<EmployeeOffice, Long> {
    @Query("DELETE FROM employee_office WHERE employee_id = :employeeId")
    Mono<Void> deleteByEmployeeId(Long employeeId);

    @Query("SELECT * FROM employee_office WHERE employee_id = :employeeId")
    Flux<EmployeeOffice> findByEmployeeId(Long employeeId);
}
