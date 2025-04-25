package com.nttdata.peru.employee_office_api.repository;

import com.nttdata.peru.employee_office_api.model.Office;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficeRepository extends ReactiveCrudRepository<Office, Long> {
}
