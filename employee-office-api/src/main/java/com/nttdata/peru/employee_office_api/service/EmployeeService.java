package com.nttdata.peru.employee_office_api.service;

import com.nttdata.peru.employee_office_api.dto.EmployeeWithOfficesDTO;
import com.nttdata.peru.employee_office_api.model.Employee;
import com.nttdata.peru.employee_office_api.model.EmployeeOffice;
import com.nttdata.peru.employee_office_api.model.Office;
import com.nttdata.peru.employee_office_api.repository.EmployeeOfficeRepository;
import com.nttdata.peru.employee_office_api.repository.EmployeeRepository;
import com.nttdata.peru.employee_office_api.repository.OfficeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Tag(name = "Employee Service", description = "Servicios relacionados con los empleados")
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final OfficeRepository officeRepository;
    private final EmployeeOfficeRepository employeeOfficeRepository;

    public EmployeeService(EmployeeRepository employeeRepository, OfficeRepository officeRepository, EmployeeOfficeRepository employeeOfficeRepository) {
        this.employeeRepository = employeeRepository;
        this.officeRepository = officeRepository;
        this.employeeOfficeRepository = employeeOfficeRepository;
    }

    @Operation(summary = "Crear un nuevo empleado",
            description = "Crea un nuevo empleado, si no existe ya un empleado con el mismo DNI.")
    @ApiResponse(responseCode = "200", description = "Empleado creado con éxito")
    @ApiResponse(responseCode = "400", description = "DNI ya registrado")
    public Mono<Employee> createEmployee(Employee employee) {
        log.info("Iniciando creación de empleado con DNI: {}", employee.getDni());
        return employeeRepository.findByDni(employee.getDni())
                .doOnNext(existing -> log.warn("Intento de crear empleado con DNI existente: {}", employee.getDni()))
                .flatMap(existingEmployee -> Mono.<Employee>error(new IllegalArgumentException("El DNI ya está registrado")))
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("DNI no registrado, procediendo a guardar empleado");
                    return employeeRepository.save(employee)
                            .doOnSuccess(saved -> log.info("Empleado creado exitosamente - ID: {}", saved.getId()))
                            .doOnError(e -> log.error("Error guardando empleado: {}", e.getMessage()));
                }))
                .doOnError(e -> log.error("Error en proceso de creación de empleado: {}", e.getMessage()));
    }

    @Operation(summary = "Obtener todos los empleados",
            description = "Devuelve una lista de todos los empleados registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de empleados")
    public Flux<Employee> getAllEmployees() {
        log.info("Obteniendo todos los empleados desde la base de datos");
        return employeeRepository.findAll()
                .doOnComplete(() -> log.info("Consulta de todos los empleados completada"))
                .doOnError(e -> log.error("Error obteniendo lista de empleados: {}", e.getMessage()));
    }

    @Operation(summary = "Obtener un empleado con sus oficinas",
            description = "Devuelve un empleado y los nombres de las oficinas asociadas.")
    @ApiResponse(responseCode = "200", description = "Empleado con oficinas")
    @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    public Mono<EmployeeWithOfficesDTO> getEmployeeWithOfficesById(@Parameter(description = "ID del empleado") Long id) {
        log.info("Buscando empleado con oficinas - ID: {}", id);
        return employeeRepository.findById(id)
                .doOnNext(employee -> log.info("Empleado base encontrado - ID: {}", employee.getId()))
                .flatMap(employee ->
                        employeeOfficeRepository.findByEmployeeId(employee.getId())
                                .doOnSubscribe(s -> log.info("Buscando relaciones de oficinas para empleado ID: {}", employee.getId()))
                                .collectList()
                                .flatMap(relations -> {
                                    log.info("Encontradas {} oficinas relacionadas al empleado ID: {}", relations.size(), employee.getId());
                                    List<Long> officeIds = relations.stream()
                                            .map(EmployeeOffice::getOfficeId)
                                            .toList();

                                    return officeRepository.findAllById(officeIds)
                                            .doOnSubscribe(s -> log.info("Buscando detalles de {} oficinas", officeIds.size()))
                                            .map(Office::getName)
                                            .collectList()
                                            .map(names -> {
                                                log.debug("Construyendo DTO para empleado ID: {}", employee.getId());
                                                return createDTO(employee, names);
                                            });
                                })
                )
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Empleado no encontrado - ID: {}", id);
                    return Mono.error(new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Empleado no encontrado"
                    ));
                }))
                .doOnSuccess(dto -> log.info("Proceso de obtención de empleado con oficinas completado - ID: {}", id))
                .doOnError(e -> log.error("Error obteniendo empleado con oficinas - ID: {}: {}", id, e.getMessage()));
    }

    @Operation(summary = "Actualizar un empleado",
            description = "Actualiza los detalles de un empleado existente.")
    @ApiResponse(responseCode = "200", description = "Empleado actualizado")
    @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    public Mono<Employee> updateEmployee(@Parameter(description = "ID del empleado a actualizar") Long id, Employee updated) {
        log.info("Iniciando actualización de empleado - ID: {}", id);
        return employeeRepository.findById(id)
                .doOnNext(existing -> log.info("Empleado encontrado para actualización - ID: {}", existing.getId()))
                .flatMap(existing -> {
                    log.info("Aplicando cambios al empleado - ID: {}", id);
                    existing.setFirstName(updated.getFirstName());
                    existing.setLastName(updated.getLastName());
                    existing.setPhone(updated.getPhone());
                    existing.setDni(updated.getDni());
                    existing.setAddress(updated.getAddress());
                    existing.setBirthDate(updated.getBirthDate());
                    return employeeRepository.save(existing)
                            .doOnSuccess(saved -> log.info("Empleado actualizado exitosamente - ID: {}", saved.getId()))
                            .doOnError(e -> log.error("Error guardando cambios del empleado - ID: {}: {}", id, e.getMessage()));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Empleado no encontrado para actualización - ID: {}", id);
                    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
                }))
                .doOnError(e -> log.error("Error en proceso de actualización - ID: {}: {}", id, e.getMessage()));
    }

    @Operation(summary = "Eliminar un empleado",
            description = "Elimina un empleado por su ID.")
    @ApiResponse(responseCode = "200", description = "Empleado eliminado")
    @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    public Mono<Void> deleteEmployee(@Parameter(description = "ID del empleado a eliminar") Long id) {
        log.info("Iniciando eliminación de empleado - ID: {}", id);
        return employeeRepository.deleteById(id)
                .doOnSuccess(v -> log.info("Empleado eliminado correctamente - ID: {}", id))
                .doOnError(e -> log.error("Error eliminando empleado - ID: {}: {}", id, e.getMessage()));
    }

    @Operation(summary = "Asignar oficinas a un empleado",
            description = "Asigna un conjunto de oficinas a un empleado.")
    @ApiResponse(responseCode = "200", description = "Oficinas asignadas correctamente")
    @ApiResponse(responseCode = "400", description = "Oficinas no encontradas")
    @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    public Mono<Void> assignOfficesToEmployee(@Parameter(description = "ID del empleado") Long employeeId,
                                              @Parameter(description = "Conjunto de IDs de oficinas") Set<Long> officeIds) {
        log.info("Iniciando asignación de {} oficinas al empleado ID: {}", officeIds.size(), employeeId);
        return officeRepository.findAllById(officeIds).collectList()
                .doOnNext(existingOffices -> log.debug("Oficinas encontradas: {}/{}", existingOffices.size(), officeIds.size()))
                .flatMap(existingOffices -> {
                    if (existingOffices.size() != officeIds.size()) {
                        Set<Long> missingIds = officeIds.stream()
                                .filter(id -> existingOffices.stream().noneMatch(o -> o.getId().equals(id)))
                                .collect(Collectors.toSet());
                        log.error("Oficinas no encontradas: {}", missingIds);
                        return Mono.error(new IllegalArgumentException("Oficinas no encontradas: " + missingIds));
                    }

                    return employeeRepository.existsById(employeeId)
                            .flatMap(exists -> {
                                if (!exists) {
                                    log.error("Empleado no existe - ID: {}", employeeId);
                                    return Mono.error(new IllegalArgumentException("Empleado no encontrado con ID: " + employeeId));
                                }

                                return employeeOfficeRepository.deleteByEmployeeId(employeeId)
                                        .doOnSuccess(v -> log.info("Relaciones anteriores eliminadas - Empleado ID: {}", employeeId))
                                        .thenMany(Flux.fromIterable(officeIds)
                                                .flatMap(officeId -> {
                                                    log.debug("Asignando oficina ID: {} al empleado ID: {}", officeId, employeeId);
                                                    return employeeOfficeRepository.save(new EmployeeOffice(employeeId, officeId));
                                                })
                                                .doOnNext(relation -> log.trace("Relación guardada: {}", relation))
                                        )
                                        .then()
                                        .doOnSuccess(v -> log.info("Asignación completada para empleado ID: {}", employeeId));
                            });
                })
                .doOnError(e -> log.error("Error en asignación de oficinas - Empleado ID: {}: {}", employeeId, e.getMessage()));
    }

    @Operation(summary = "Obtener todos los empleados con oficinas asociadas",
            description = "Devuelve una lista de empleados con los nombres de las oficinas asociadas.")
    @ApiResponse(responseCode = "200", description = "Lista de empleados con oficinas")
    public Flux<EmployeeWithOfficesDTO> getEmployeesWithOffices() {
        log.info("Obteniendo todos los empleados con sus oficinas");
        return employeeRepository.findAll()
                .doOnNext(employee -> log.debug("Procesando empleado ID: {}", employee.getId()))
                .flatMap(employee ->
                        employeeOfficeRepository.findByEmployeeId(employee.getId())
                                .collectList()
                                .flatMap(relations -> {
                                    log.debug("Encontradas {} relaciones para empleado ID: {}", relations.size(), employee.getId());
                                    if (relations.isEmpty()) {
                                        log.info("Empleado ID: {} no tiene oficinas asignadas", employee.getId());
                                        return Mono.just(createDTO(employee, List.of()));
                                    }

                                    List<Long> officeIds = relations.stream()
                                            .map(EmployeeOffice::getOfficeId)
                                            .toList();

                                    log.info("Buscando {} oficinas para empleado ID: {}", officeIds.size(), employee.getId());
                                    return officeRepository.findAllById(officeIds)
                                            .map(Office::getName)
                                            .collectList()
                                            .map(names -> {
                                                log.trace("Construyendo DTO con {} oficinas para empleado ID: {}", names.size(), employee.getId());
                                                return createDTO(employee, names);
                                            });
                                })
                )
                .doOnComplete(() -> log.info("Proceso de obtención de empleados con oficinas completado"))
                .doOnError(e -> log.error("Error obteniendo empleados con oficinas: {}", e.getMessage()));
    }

    private EmployeeWithOfficesDTO createDTO(Employee employee, List<String> officeNames) {
        return new EmployeeWithOfficesDTO(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getPhone(),
                employee.getDni(),
                employee.getAddress(),
                employee.getBirthDate(),
                officeNames
        );
    }
}