package com.property.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class BuildingDTO {

    private Integer id;

    @NotBlank(message = "楼栋名称不能为空")
    private String name;

    @NotNull(message = "单元数不能为空")
    private Integer units;

    @NotNull(message = "楼层数不能为空")
    private Integer floors;

    private String description;
}
