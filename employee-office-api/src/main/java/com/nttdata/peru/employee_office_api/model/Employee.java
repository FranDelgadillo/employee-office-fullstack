package com.nttdata.peru.employee_office_api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("employees")
public class Employee {

    @Id
    private Long id;

    @NotBlank(message = "Se requiere el nombre")
    private String firstName;

    @NotBlank(message = "Se requiere el apellido")
    private String lastName;

    @NotBlank(message = "Se requiere el número de teléfono")
    @Size(max = 9, message = "El teléfono debe tener máximo 9 caracteres")
    private String phone;

    @NotBlank(message = "Se requiere el número de DNI")
    @Size(max = 8, message = "El DNI debe tener máximo 8 caracteres")
    private String dni;

    @NotBlank(message = "Se requiere dirección")
    private String address;

    @NotNull(message = "Se requiere fecha de nacimiento")
    @Past(message = "Fecha de nacimiento debe ser del pasado")
    private LocalDate birthDate;
}