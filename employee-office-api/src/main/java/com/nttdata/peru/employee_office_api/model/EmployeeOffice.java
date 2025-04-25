package com.nttdata.peru.employee_office_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("employee_office")
public class EmployeeOffice {

    private Long employeeId;
    private Long officeId;

}