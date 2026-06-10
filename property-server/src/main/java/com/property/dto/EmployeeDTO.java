package com.property.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class EmployeeDTO {

    private Integer id;

    @NotBlank(message = "员工姓名不能为空")
    private String name;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号必须为11位且格式正确")
    private String phone;

    @NotBlank(message = "职位不能为空")
    private String position;

    @NotBlank(message = "部门不能为空")
    private String department;
}
