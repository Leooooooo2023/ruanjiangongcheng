package com.property.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    /** 0=管理员, 1=业主 */
    private Integer role;

    /** 验证码 key */
    private String captchaKey;

    /** 验证码输入值 */
    private String captchaCode;
}
