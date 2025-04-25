package com.nttdata.peru.employee_office_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para creación o edición de empleados")
public class EmployeeDTO {

    @Schema(description = "ID del empleado", example = "1")
    private Long id;

    @Schema(description = "Nombre del empleado", example = "Christian")
    private String firstName;

    @Schema(description = "Apellido del empleado", example = "Espinoza")
    private String lastName;

    @Schema(description = "Teléfono del empleado", example = "987654321")
    private String phone;

    @Schema(description = "DNI del empleado", example = "12345678")
    private String dni;

    @Schema(description = "Dirección del empleado", example = "Av. Javier Prado 2020")
    private String address;

    @Schema(description = "Fecha de nacimiento del empleado", example = "1990-01-01")
    private LocalDate birthDate;

    @Schema(description = "IDs de oficinas asociadas", example = "[1, 2, 3]")
    private Set<Long> officeIds;
}