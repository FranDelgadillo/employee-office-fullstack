package com.nttdata.peru.employee_office_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para registro y login de usuarios")
public class UserDTO {

    @NotBlank(message = "Username requerido")
    @Schema(description = "Nombre de usuario", example = "frandelgadillo")
    private String username;

    @NotBlank(message = "Password requerido")
    @Schema(description = "Contraseña del usuario", example = "YourPass2024.")
    private String password;
}