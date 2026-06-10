package com.property.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class RepairDTO {

    private Integer id;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String image;
}
