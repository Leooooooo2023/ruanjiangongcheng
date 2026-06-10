package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.property.dao.AdminMapper;
import com.property.dao.OwnerMapper;
import com.property.dto.*;
import com.property.entity.Admin;
import com.property.entity.Owner;
import com.property.exception.BusinessException;
import com.property.service.AuthService;
import com.property.util.BcryptUtil;
import com.property.util.CaptchaUtil;
import com.property.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    /** 最大登录失败次数 */
    private static final int MAX_FAIL_COUNT = 5;
    /** 锁定分钟数 */
    private static final int LOCK_MINUTES = 30;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private OwnerMapper ownerMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public CaptchaVO getCaptcha() {
        CaptchaUtil.CaptchaResult result = CaptchaUtil.generate();
        return new CaptchaVO(result.getKey(), result.getImageBase64());
    }

    @Override
    public void register(RegisterDTO dto) {
        // 用户名唯一检查（admin + owner 表）
        Long adminCount = adminMapper.selectCount(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, dto.getUsername()));
        Long ownerCount = ownerMapper.selectCount(
                new LambdaQueryWrapper<Owner>().eq(Owner::getUsername, dto.getUsername()));
        if (adminCount > 0 || ownerCount > 0) {
            throw new BusinessException("用户名已存在");
        }

        Owner owner = new Owner();
        owner.setUsername(dto.getUsername());
        owner.setPassword(BcryptUtil.encrypt(dto.getPassword()));
        owner.setName(dto.getName());
        owner.setPhone(dto.getPhone());
        owner.setEmail(dto.getEmail());
        owner.setBuildingId(dto.getBuildingId());
        owner.setUnit(dto.getUnit());
        owner.setRoom(dto.getRoom());
        ownerMapper.insert(owner);
    }

    @Override
    public Map<String, Object> adminLogin(LoginDTO dto) {
        // 验证码校验
        if (!CaptchaUtil.verify(dto.getCaptchaKey(), dto.getCaptchaCode())) {
            throw new BusinessException(400, "验证码错误或已过期");
        }

        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, dto.getUsername()));

        if (admin == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 检查锁定状态
        checkLock(admin.getLockUntil());
        if (admin.getLoginFailCount() != null && admin.getLoginFailCount() >= MAX_FAIL_COUNT
                && admin.getLockUntil() != null && admin.getLockUntil().isAfter(LocalDateTime.now())) {
            throw new BusinessException(403, "账户已被锁定，请30分钟后再试");
        }

        if (!BcryptUtil.check(dto.getPassword(), admin.getPassword())) {
            // 失败计数
            int newCount = (admin.getLoginFailCount() == null ? 0 : admin.getLoginFailCount()) + 1;
            admin.setLoginFailCount(newCount);
            if (newCount >= MAX_FAIL_COUNT) {
                admin.setLockUntil(LocalDateTime.now().plusMinutes(LOCK_MINUTES));
                adminMapper.updateById(admin);
                throw new BusinessException(403, "密码错误已达" + MAX_FAIL_COUNT + "次，账户已锁定30分钟");
            }
            adminMapper.updateById(admin);
            throw new BusinessException(401, "用户名或密码错误，还剩" + (MAX_FAIL_COUNT - newCount) + "次机会");
        }

        // 登录成功：重置锁定状态
        resetLock(admin);
        adminMapper.updateById(admin);

        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), 0);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("id", admin.getId());
        result.put("username", admin.getUsername());
        result.put("name", admin.getName());
        result.put("role", 0);
        return result;
    }

    @Override
    public Map<String, Object> ownerLogin(LoginDTO dto) {
        // 验证码校验
        if (!CaptchaUtil.verify(dto.getCaptchaKey(), dto.getCaptchaCode())) {
            throw new BusinessException(400, "验证码错误或已过期");
        }

        Owner owner = ownerMapper.selectOne(
                new LambdaQueryWrapper<Owner>().eq(Owner::getUsername, dto.getUsername()));

        if (owner == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 检查锁定状态
        if (owner.getLockUntil() != null && owner.getLockUntil().isAfter(LocalDateTime.now())) {
            throw new BusinessException(403, "账户已被锁定，请30分钟后再试");
        }

        if (!BcryptUtil.check(dto.getPassword(), owner.getPassword())) {
            // 失败计数
            int newCount = (owner.getLoginFailCount() == null ? 0 : owner.getLoginFailCount()) + 1;
            owner.setLoginFailCount(newCount);
            if (newCount >= MAX_FAIL_COUNT) {
                owner.setLockUntil(LocalDateTime.now().plusMinutes(LOCK_MINUTES));
                ownerMapper.updateById(owner);
                throw new BusinessException(403, "密码错误已达" + MAX_FAIL_COUNT + "次，账户已锁定30分钟");
            }
            ownerMapper.updateById(owner);
            throw new BusinessException(401, "用户名或密码错误，还剩" + (MAX_FAIL_COUNT - newCount) + "次机会");
        }

        // 登录成功：重置锁定状态
        resetLock(owner);
        ownerMapper.updateById(owner);

        String token = jwtUtil.generateToken(owner.getId(), owner.getUsername(), 1);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("id", owner.getId());
        result.put("username", owner.getUsername());
        result.put("name", owner.getName());
        result.put("role", 1);
        return result;
    }

    @Override
    public void updateAdminPassword(Integer adminId, PasswordDTO dto) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException("用户不存在");
        }
        if (!BcryptUtil.check(dto.getOldPassword(), admin.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        admin.setPassword(BcryptUtil.encrypt(dto.getNewPassword()));
        adminMapper.updateById(admin);
    }

    @Override
    public void updateOwnerPassword(Integer ownerId, PasswordDTO dto) {
        Owner owner = ownerMapper.selectById(ownerId);
        if (owner == null) {
            throw new BusinessException("用户不存在");
        }
        if (!BcryptUtil.check(dto.getOldPassword(), owner.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        owner.setPassword(BcryptUtil.encrypt(dto.getNewPassword()));
        ownerMapper.updateById(owner);
    }

    @Override
    public Admin getAdminInfo(Integer adminId) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException("用户不存在");
        }
        admin.setPassword(null);
        return admin;
    }

    @Override
    public Owner getOwnerInfo(Integer ownerId) {
        Owner owner = ownerMapper.selectById(ownerId);
        if (owner == null) {
            throw new BusinessException("用户不存在");
        }
        owner.setPassword(null);
        return owner;
    }

    // ---------- 私有方法 ----------

    /** 检查锁定是否过期 */
    private void checkLock(LocalDateTime lockUntil) {
        // admin 可能没有 lockUntil (null)
    }

    /** 重置锁定状态 */
    private void resetLock(Object entity) {
        if (entity instanceof Admin) {
            Admin admin = (Admin) entity;
            admin.setLoginFailCount(0);
            admin.setLockUntil(null);
        } else if (entity instanceof Owner) {
            Owner owner = (Owner) entity;
            owner.setLoginFailCount(0);
            owner.setLockUntil(null);
        }
    }
}
