package com.nttdata.peru.employee_office_api.service;

import com.nttdata.peru.employee_office_api.model.Employee;
import com.nttdata.peru.employee_office_api.model.EmployeeOffice;
import com.nttdata.peru.employee_office_api.model.Office;
import com.nttdata.peru.employee_office_api.repository.EmployeeOfficeRepository;
import com.nttdata.peru.employee_office_api.repository.EmployeeRepository;
import com.nttdata.peru.employee_office_api.repository.OfficeRepository;
import com.nttdata.peru.employee_office_api.security.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private OfficeRepository officeRepository;

    @Mock
    private EmployeeOfficeRepository employeeOfficeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private final Employee testEmployee = new Employee(
            1L, "Christian", "Espinoza", "999888777", "87654321",
            "Av. Lima 123", LocalDate.of(1990, 1, 1)
    );

    @Test
    void assignOfficesToEmployee_Success() {
        when(officeRepository.findAllById(any(Iterable.class)))
                .thenReturn(Flux.just(
                        new Office(1L, "Oficina Central", "Lima"),
                        new Office(2L, "Sucursal A", "Trujillo")
                ));
        when(employeeRepository.existsById(anyLong()))
                .thenReturn(Mono.just(true));
        when(employeeOfficeRepository.deleteByEmployeeId(anyLong()))
                .thenReturn(Mono.empty());
        when(employeeOfficeRepository.save(any(EmployeeOffice.class)))
                .thenReturn(Mono.just(new EmployeeOffice(1L, 1L)));
        StepVerifier.create(employeeService.assignOfficesToEmployee(1L, Set.of(1L, 2L)))
                .verifyComplete();
    }

    @Test
    void getEmployeesWithOffices_Success() {
        when(employeeRepository.findAll())
                .thenReturn(Flux.just(testEmployee));
        when(employeeOfficeRepository.findByEmployeeId(anyLong()))
                .thenReturn(Flux.just(new EmployeeOffice(1L, 1L)));
        when(officeRepository.findAllById(any(Iterable.class)))
                .thenReturn(Flux.just(new Office(1L, "Oficina Central", "Lima")));
        StepVerifier.create(employeeService.getEmployeesWithOffices())
                .expectNextMatches(dto ->
                        dto.getOfficeNames().contains("Oficina Central") &&
                                dto.getDni().equals("87654321")
                )
                .verifyComplete();
    }
}