package com.property.controller;

import com.property.dto.*;
import com.property.entity.Admin;
import com.property.entity.Owner;
import com.property.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 获取图形验证码
     */
    @GetMapping("/captcha")
    public Result<CaptchaVO> captcha() {
        CaptchaVO vo = authService.getCaptcha();
        return Result.success(vo);
    }

    /**
     * 业主注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return Result.success("注册成功", null);
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginDTO dto) {
        Map<String, Object> data;
        if (dto.getRole() != null && dto.getRole() == 1) {
            data = authService.ownerLogin(dto);
        } else {
            data = authService.adminLogin(dto);
        }
        return Result.success("登录成功", data);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success("退出成功", null);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@Validated @RequestBody PasswordDTO dto, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Integer role = (Integer) request.getAttribute("role");
        if (role == 0) {
            authService.updateAdminPassword(userId, dto);
        } else {
            authService.updateOwnerPassword(userId, dto);
        }
        return Result.success("密码修改成功", null);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<?> getInfo(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Integer role = (Integer) request.getAttribute("role");
        if (role == 0) {
            Admin admin = authService.getAdminInfo(userId);
            return Result.success(admin);
        } else {
            Owner owner = authService.getOwnerInfo(userId);
            return Result.success(owner);
        }
    }
}
