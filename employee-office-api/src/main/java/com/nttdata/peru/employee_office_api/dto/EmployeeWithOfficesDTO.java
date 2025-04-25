package com.nttdata.peru.employee_office_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para empleado con nombres de oficinas asociadas")
public class EmployeeWithOfficesDTO {

    @Schema(description = "ID del empleado", example = "1")
    private Long id;

    @Schema(description = "Nombre del empleado", example = "Jerson")
    private String firstName;

    @Schema(description = "Apellido del empleado", example = "Ramirez")
    private String lastName;

    @Schema(description = "Teléfono del empleado", example = "987654321")
    private String phone;

    @Schema(description = "DNI del empleado", example = "12345678")
    private String dni;

    @Schema(description = "Dirección del empleado", example = "Av. Javier Prado 2020")
    private String address;

    @Schema(description = "Fecha de nacimiento del empleado", example = "1990-01-01")
    private LocalDate birthDate;

    @Schema(description = "Lista de nombres de oficinas", example = "[\"Dean Valdivia\", \"Bloom\"]")
    private List<String> officeNames;
}