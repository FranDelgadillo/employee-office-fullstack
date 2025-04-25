package com.nttdata.peru.employee_office_api.controller;

import com.nttdata.peru.employee_office_api.model.Office;
import com.nttdata.peru.employee_office_api.service.OfficeService;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/offices")
@Tag(name = "Oficinas", description = "Operaciones relacionadas con oficinas")
class OfficeController {

    @Autowired
    private OfficeService officeService;

    @Operation(summary = "Crear una nueva oficina", responses = {
            @ApiResponse(responseCode = "201", description = "Oficina creada exitosamente")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Office> createOffice(@Valid @RequestBody Office office) {
        log.info("Intentando crear oficina: {}", office);
        return officeService.createOffice(office)
                .doOnSuccess(o -> log.info("Oficina creada exitosamente - ID: {}", o.getId()))
                .doOnError(e -> log.error("Error creando oficina: {}", e.getMessage()));
    }

    @Operation(summary = "Obtener todas las oficinas", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de oficinas obtenida correctamente")
    })
    @GetMapping
    public Flux<Office> getAllOffices() {
        log.info("Solicitando todas las oficinas");
        return officeService.getAllOffices()
                .doOnComplete(() -> log.info("Obtenidas todas las oficinas"))
                .doOnError(e -> log.error("Error obteniendo oficinas: {}", e.getMessage()));
    }

    @Operation(summary = "Actualizar oficina", responses = {
            @ApiResponse(responseCode = "200", description = "Oficina actualizada correctamente")
    })
    @PutMapping("/{id}")
    public Mono<Office> updateOffice(@PathVariable Long id, @Valid @RequestBody Office office) {
        log.info("Actualizando oficina - ID: {} - Datos: {}", id, office);
        return officeService.updateOffice(id, office)
                .doOnSuccess(o -> log.info("Oficina actualizada - ID: {}", id))
                .doOnError(e -> log.error("Error actualizando oficina - ID: {} - Error: {}", id, e.getMessage()));
    }

    @Operation(summary = "Eliminar oficina", responses = {
            @ApiResponse(responseCode = "200", description = "Oficina eliminada correctamente")
    })
    @DeleteMapping("/{id}")
    public Mono<Void> deleteOffice(@PathVariable Long id) {
        log.info("Eliminando oficina - ID: {}", id);
        return officeService.deleteOffice(id)
                .doOnSuccess(__ -> log.info("Oficina eliminada - ID: {}", id))
                .doOnError(e -> log.error("Error eliminando oficina - ID: {} - Error: {}", id, e.getMessage()));
    }
}