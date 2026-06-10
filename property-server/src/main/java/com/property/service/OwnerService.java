package com.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.dto.OwnerDTO;
import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.entity.Owner;

import java.util.List;

public interface OwnerService extends IService<Owner> {

    PageResult<Owner> queryPage(PageQuery query);

    Owner getDetail(Integer id);

    void add(OwnerDTO dto);

    void update(Integer id, OwnerDTO dto);

    void remove(Integer id);

    /**
     * 获取全部业主（不分页）
     */
    List<Owner> getAllOwners();

    /**
     * 业主修改个人信息
     */
    void updateProfile(Integer ownerId, OwnerDTO dto);
}
