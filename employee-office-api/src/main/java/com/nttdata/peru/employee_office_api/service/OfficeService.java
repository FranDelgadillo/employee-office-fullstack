package com.nttdata.peru.employee_office_api.service;

import com.nttdata.peru.employee_office_api.model.Office;
import com.nttdata.peru.employee_office_api.repository.OfficeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Tag(name = "Office Service", description = "Servicios relacionados con las oficinas")
public class OfficeService {

    private final OfficeRepository officeRepository;

    public OfficeService(OfficeRepository officeRepository) {
        this.officeRepository = officeRepository;
    }

    @Operation(summary = "Crear una nueva oficina",
            description = "Crea una nueva oficina.")
    @ApiResponse(responseCode = "200", description = "Oficina creada con éxito")
    public Mono<Office> createOffice(Office office) {
        log.info("Intentando crear oficina: {}", office);
        return officeRepository.save(office)
                .doOnSuccess(savedOffice -> log.info("Oficina creada exitosamente - ID: {}", savedOffice.getId()))
                .doOnError(e -> log.error("Error creando oficina: {}", e.getMessage()));
    }

    @Operation(summary = "Obtener todas las oficinas",
            description = "Devuelve una lista de todas las oficinas registradas.")
    @ApiResponse(responseCode = "200", description = "Lista de oficinas")
    public Flux<Office> getAllOffices() {
        log.info("Solicitando todas las oficinas");
        return officeRepository.findAll()
                .doOnComplete(() -> log.info("Consulta de oficinas completada"))
                .doOnError(e -> log.error("Error obteniendo oficinas: {}", e.getMessage()));
    }

    @Operation(summary = "Actualizar una oficina",
            description = "Actualiza los detalles de una oficina existente.")
    @ApiResponse(responseCode = "200", description = "Oficina actualizada")
    @ApiResponse(responseCode = "404", description = "Oficina no encontrada")
    public Mono<Office> updateOffice(@Parameter(description = "ID de la oficina a actualizar") Long id,
                                     Office updatedOffice) {
        log.info("Iniciando actualización de oficina - ID: {}", id);
        return officeRepository.findById(id)
                .doOnNext(foundOffice -> log.info("Oficina encontrada para actualización - ID: {}", id))
                .flatMap(existingOffice -> {
                    log.info("Aplicando cambios a oficina ID: {}", id);
                    existingOffice.setName(updatedOffice.getName());
                    existingOffice.setLocation(updatedOffice.getLocation());
                    return officeRepository.save(existingOffice)
                            .doOnSuccess(saved -> log.info("Oficina actualizada exitosamente - ID: {}", saved.getId()))
                            .doOnError(e -> log.error("Error guardando cambios de oficina - ID: {}: {}", id, e.getMessage()));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Oficina no encontrada para actualización - ID: {}", id);
                    return Mono.error(new RuntimeException("Oficina no encontrada"));
                }))
                .doOnError(e -> log.error("Error en proceso de actualización - ID: {}: {}", id, e.getMessage()));
    }

    @Operation(summary = "Eliminar una oficina",
            description = "Elimina una oficina por su ID.")
    @ApiResponse(responseCode = "200", description = "Oficina eliminada")
    @ApiResponse(responseCode = "404", description = "Oficina no encontrada")
    public Mono<Void> deleteOffice(@Parameter(description = "ID de la oficina a eliminar") Long id) {
        log.info("Iniciando eliminación de oficina - ID: {}", id);
        return officeRepository.deleteById(id)
                .doOnSuccess(v -> log.info("Oficina eliminada correctamente - ID: {}", id))
                .doOnError(e -> log.error("Error eliminando oficina - ID: {}: {}", id, e.getMessage()));
    }
}