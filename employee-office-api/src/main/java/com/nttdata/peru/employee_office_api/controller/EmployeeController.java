package com.nttdata.peru.employee_office_api.controller;

import com.nttdata.peru.employee_office_api.dto.EmployeeWithOfficesDTO;
import com.nttdata.peru.employee_office_api.model.Employee;
import com.nttdata.peru.employee_office_api.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Empleados", description = "Operaciones relacionadas con empleados")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Operation(summary = "Crear un nuevo empleado", responses = {
            @ApiResponse(responseCode = "201", description = "Empleado creado exitosamente")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        log.info("Intentando crear empleado: {}", employee);
        return employeeService.createEmployee(employee)
                .doOnSuccess(e -> log.info("Empleado creado exitosamente - ID: {}", e.getId()))
                .doOnError(e -> log.error("Error creando empleado: {}", e.getMessage()));
    }

    @Operation(summary = "Obtener todos los empleados", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de empleados obtenida correctamente")
    })
    @GetMapping
    public Flux<Employee> getAllEmployees() {
        log.info("Solicitando todos los empleados");
        return employeeService.getAllEmployees()
                .doOnComplete(() -> log.info("Obtenidos todos los empleados"))
                .doOnError(e -> log.error("Error obteniendo empleados: {}", e.getMessage()));
    }

    @Operation(summary = "Obtener empleado con oficinas asignadas", responses = {
            @ApiResponse(responseCode = "200", description = "Empleado con oficinas obtenido correctamente")
    })
    @GetMapping("/{id}/withOffices")
    public Mono<EmployeeWithOfficesDTO> getEmployeeWithOffices(@PathVariable Long id) {
        log.info("Buscando empleado con oficinas - ID: {}", id);
        return employeeService.getEmployeeWithOfficesById(id)
                .doOnSuccess(e -> log.info("Empleado encontrado - ID: {}", id))
                .doOnError(e -> log.error("Error obteniendo empleado con oficinas - ID: {} - Error: {}", id, e.getMessage()));
    }

    @Operation(summary = "Actualizar empleado", responses = {
            @ApiResponse(responseCode = "200", description = "Empleado actualizado correctamente")
    })
    @PutMapping("/{id}")
    public Mono<Employee> updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee employee) {
        log.info("Actualizando empleado - ID: {} - Datos: {}", id, employee);
        return employeeService.updateEmployee(id, employee)
                .doOnSuccess(e -> log.info("Empleado actualizado - ID: {}", id))
                .doOnError(e -> log.error("Error actualizando empleado - ID: {} - Error: {}", id, e.getMessage()));
    }

    @Operation(summary = "Eliminar empleado", responses = {
            @ApiResponse(responseCode = "200", description = "Empleado eliminado correctamente")
    })
    @DeleteMapping("/{id}")
    public Mono<Void> deleteEmployee(@PathVariable Long id) {
        log.info("Eliminando empleado - ID: {}", id);
        return employeeService.deleteEmployee(id)
                .doOnSuccess(v -> log.info("Empleado eliminado - ID: {}", id))
                .doOnError(e -> log.error("Error eliminando empleado - ID: {} - Error: {}", id, e.getMessage()));
    }

    @Operation(summary = "Asignar oficinas a un empleado", responses = {
            @ApiResponse(responseCode = "200", description = "Oficinas asignadas correctamente")
    })
    @PatchMapping("/{id}/assignOffices")
    public Mono<Void> assignOfficesToEmployee(@PathVariable Long id, @RequestBody Set<Long> officeIds) {
        log.info("Asignando oficinas al empleado - ID: {} - Oficinas: {}", id, officeIds);
        return employeeService.assignOfficesToEmployee(id, officeIds)
                .doOnSuccess(v -> log.info("Oficinas asignadas correctamente - ID Empleado: {} - IDs Oficinas: {}", id, officeIds))
                .doOnError(e -> log.error("Error asignando oficinas - ID: {} - Error: {}", id, e.getMessage()));
    }

    @Operation(summary = "Obtener empleados con nombres de oficinas", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de empleados con nombres de oficinas obtenida correctamente")
    })
    @GetMapping("/withOffices")
    public Flux<EmployeeWithOfficesDTO> getEmployeesWithOfficeNames() {
        log.info("Solicitando empleados con nombres de oficinas");
        return employeeService.getEmployeesWithOffices()
                .doOnComplete(() -> log.info("Obtenidos empleados con oficinas"))
                .doOnError(e -> log.error("Error obteniendo empleados con oficinas: {}", e.getMessage()));
    }
}