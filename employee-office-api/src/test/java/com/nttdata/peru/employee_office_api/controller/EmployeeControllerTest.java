package com.nttdata.peru.employee_office_api.controller;

import com.nttdata.peru.employee_office_api.dto.EmployeeWithOfficesDTO;
import com.nttdata.peru.employee_office_api.model.Employee;
import com.nttdata.peru.employee_office_api.security.TestSecurityConfig;
import com.nttdata.peru.employee_office_api.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@WebFluxTest(EmployeeController.class)
@Import(TestSecurityConfig.class)
class EmployeeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private EmployeeService employeeService;

    private final Employee mockEmployee = new Employee(
            1L, "Christian", "Espinoza", "999888777", "87654321",
            "Av. Lima 123", LocalDate.of(1990, 1, 1)
    );

    @Test
    void createEmployee_Success() {
        Mockito.when(employeeService.createEmployee(Mockito.any(Employee.class)))
                .thenReturn(Mono.just(mockEmployee));

        webTestClient.post()
                .uri("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mockEmployee)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Employee.class)
                .consumeWith(resp -> {
                    Employee body = resp.getResponseBody();
                    assert body != null && body.getId().equals(mockEmployee.getId());
                });
    }

    @Test
    void createEmployee_DuplicateDni_Error() {
        String errorMessage = "DNI ya está registrado";
        Mockito.when(employeeService.createEmployee(Mockito.any(Employee.class)))
                .thenReturn(Mono.error(new IllegalArgumentException(errorMessage)));

        webTestClient.post()
                .uri("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mockEmployee)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(errorMessage);
    }

    @Test
    void getEmployeeWithOffices_Success() {
        EmployeeWithOfficesDTO dto = new EmployeeWithOfficesDTO(
                1L, "Christian", "Espinoza", "999888777", "87654321",
                "Av. Lima 123", LocalDate.of(1990, 1, 1),
                List.of("Oficina Central")
        );
        Mockito.when(employeeService.getEmployeeWithOfficesById(1L))
                .thenReturn(Mono.just(dto));

        webTestClient.get()
                .uri("/api/v1/employees/1/withOffices")
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeWithOfficesDTO.class)
                .consumeWith(resp -> {
                    EmployeeWithOfficesDTO body = resp.getResponseBody();
                    assert body != null && body.getOfficeNames().contains("Oficina Central");
                });
    }
}