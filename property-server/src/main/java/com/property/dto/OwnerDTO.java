package com.property.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
public class OwnerDTO {

    private Integer id;

    @NotBlank(message = "业主姓名不能为空")
    private String name;

    @NotBlank(message = "登录账号不能为空")
    private String username;

    private String password;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private String email;

    @NotNull(message = "楼栋ID不能为空")
    private Integer buildingId;

    @NotBlank(message = "单元号不能为空")
    private String unit;

    @NotBlank(message = "房号不能为空")
    private String room;
}
