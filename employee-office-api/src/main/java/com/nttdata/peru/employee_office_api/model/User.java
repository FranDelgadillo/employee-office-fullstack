package com.nttdata.peru.employee_office_api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("users")
public class User {
    @Id
    private Long id;

    @NotBlank(message = "Se requiere el username")
    private String username;

    @NotBlank(message = "Se requiere el password")
    private String password;
}