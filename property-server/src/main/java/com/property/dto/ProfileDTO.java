package com.property.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 业主修改个人信息请求体（仅允许修改姓名、手机号、邮箱）
 */
@Data
public class ProfileDTO {

    @NotBlank(message = "姓名不能为空")
    private String name;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private String email;
}
