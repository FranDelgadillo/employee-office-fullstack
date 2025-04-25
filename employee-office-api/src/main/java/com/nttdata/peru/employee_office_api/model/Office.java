package com.nttdata.peru.employee_office_api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("offices")
public class Office {

    @Id
    private Long id;

    @NotBlank(message = "Se requiere nombre de oficina")
    private String name;

    @NotBlank(message = "Se requiere ubicación")
    private String location;

}