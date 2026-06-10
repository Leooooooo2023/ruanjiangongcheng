package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.dao.OwnerMapper;
import com.property.dto.OwnerDTO;
import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.entity.Owner;
import com.property.exception.BusinessException;
import com.property.entity.Building;
import com.property.service.BuildingService;
import com.property.service.OwnerService;
import com.property.util.BcryptUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerServiceImpl extends ServiceImpl<OwnerMapper, Owner> implements OwnerService {

    @Autowired
    private OwnerMapper ownerMapper;

    @Autowired
    private BuildingService buildingService;

    @Override
    public PageResult<Owner> queryPage(PageQuery query) {
        int offset = (query.getPage() - 1) * query.getSize();
        List<Owner> list = ownerMapper.selectOwnerWithBuildingName(query.getKeyword(), offset, query.getSize());
        Long total = ownerMapper.countOwnerWithBuildingName(query.getKeyword());
        // 隐藏密码
        list.forEach(o -> o.setPassword(null));
        return new PageResult<>(total, list);
    }

    @Override
    public Owner getDetail(Integer id) {
        Owner owner = ownerMapper.selectOwnerDetailWithBuildingName(id);
        if (owner == null) {
            throw new BusinessException("业主不存在");
        }
        owner.setPassword(null);
        return owner;
    }

    @Override
    public void add(OwnerDTO dto) {
        // 检查用户名唯一
        Long count = this.count(new LambdaQueryWrapper<Owner>().eq(Owner::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("登录账号已存在");
        }
        // 检查手机号唯一
        if (dto.getPhone() != null && !dto.getPhone().isEmpty()) {
            Long phoneCount = this.count(new LambdaQueryWrapper<Owner>().eq(Owner::getPhone, dto.getPhone()));
            if (phoneCount > 0) {
                throw new BusinessException("手机号已存在");
            }
        }
        Owner owner = new Owner();
        BeanUtils.copyProperties(dto, owner);
        // 密码加密，默认密码 owner123
        String pwd = dto.getPassword() != null && !dto.getPassword().isEmpty() ? dto.getPassword() : "owner123";
        owner.setPassword(BcryptUtil.encrypt(pwd));
        this.save(owner);
    }

    @Override
    public void update(Integer id, OwnerDTO dto) {
        Owner owner = this.getById(id);
        if (owner == null) {
            throw new BusinessException("业主不存在");
        }
        // 检查用户名唯一（排除自己）
        if (dto.getUsername() != null && !dto.getUsername().equals(owner.getUsername())) {
            Long count = this.count(new LambdaQueryWrapper<Owner>()
                    .eq(Owner::getUsername, dto.getUsername())
                    .ne(Owner::getId, id));
            if (count > 0) {
                throw new BusinessException("登录账号已存在");
            }
        }
        // 检查手机号唯一（排除自己）
        if (dto.getPhone() != null && !dto.getPhone().isEmpty() && !dto.getPhone().equals(owner.getPhone())) {
            Long phoneCount = this.count(new LambdaQueryWrapper<Owner>()
                    .eq(Owner::getPhone, dto.getPhone())
                    .ne(Owner::getId, id));
            if (phoneCount > 0) {
                throw new BusinessException("手机号已存在");
            }
        }
        BeanUtils.copyProperties(dto, owner);
        owner.setId(id);
        // 如果传了新密码则加密更新
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            owner.setPassword(BcryptUtil.encrypt(dto.getPassword()));
        }
        this.updateById(owner);
    }

    @Override
    public void remove(Integer id) {
        Owner owner = this.getById(id);
        if (owner == null) {
            throw new BusinessException("业主不存在");
        }
        this.removeById(id);
    }

    @Override
    public List<Owner> getAllOwners() {
        LambdaQueryWrapper<Owner> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Owner::getCreateTime);
        List<Owner> list = this.list(wrapper);
        // 同时查询楼栋名称
        list.forEach(o -> {
            o.setPassword(null);
            if (o.getBuildingId() != null) {
                Building building = buildingService.getById(o.getBuildingId());
                if (building != null) {
                    o.setBuildingName(building.getName());
                }
            }
        });
        return list;
    }

    @Override
    public void updateProfile(Integer ownerId, OwnerDTO dto) {
        Owner owner = this.getById(ownerId);
        if (owner == null) {
            throw new BusinessException("业主不存在");
        }
        owner.setName(dto.getName());
        owner.setPhone(dto.getPhone());
        owner.setEmail(dto.getEmail());
        this.updateById(owner);
    }
}
