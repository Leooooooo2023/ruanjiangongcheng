package com.property.service;

import com.property.dto.CaptchaVO;
import com.property.dto.LoginDTO;
import com.property.dto.PasswordDTO;
import com.property.dto.RegisterDTO;
import com.property.entity.Admin;
import com.property.entity.Owner;

import java.util.Map;

public interface AuthService {

    /**
     * 管理员登录
     */
    Map<String, Object> adminLogin(LoginDTO dto);

    /**
     * 业主登录
     */
    Map<String, Object> ownerLogin(LoginDTO dto);

    /**
     * 业主注册
     */
    void register(RegisterDTO dto);

    /**
     * 获取图形验证码
     */
    CaptchaVO getCaptcha();

    /**
     * 修改管理员密码
     */
    void updateAdminPassword(Integer adminId, PasswordDTO dto);

    /**
     * 修改业主密码
     */
    void updateOwnerPassword(Integer ownerId, PasswordDTO dto);

    /**
     * 获取管理员信息
     */
    Admin getAdminInfo(Integer adminId);

    /**
     * 获取业主信息
     */
    Owner getOwnerInfo(Integer ownerId);
}
