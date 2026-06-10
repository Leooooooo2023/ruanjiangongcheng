package com.property.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 业主注册请求
 */
@Data
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度为3-50位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度为6-50位")
    private String password;

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String phone;

    private String email;

    private Integer buildingId;

    private String unit;

    private String room;
}
