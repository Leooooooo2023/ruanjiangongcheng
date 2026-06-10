package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.dao.BuildingMapper;
import com.property.dto.BuildingDTO;
import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.entity.Building;
import com.property.exception.BusinessException;
import com.property.service.BuildingService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements BuildingService {

    @Override
    public PageResult<Building> queryPage(PageQuery query) {
        Page<Building> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(Building::getName, query.getKeyword());
        }
        wrapper.orderByDesc(Building::getCreateTime);
        Page<Building> result = this.page(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    public Building getDetail(Integer id) {
        Building building = this.getById(id);
        if (building == null) {
            throw new BusinessException("楼栋不存在");
        }
        return building;
    }

    @Override
    public void add(BuildingDTO dto) {
        // 检查楼栋名称唯一
        Long count = this.count(new LambdaQueryWrapper<Building>().eq(Building::getName, dto.getName()));
        if (count > 0) {
            throw new BusinessException("楼栋名称已存在");
        }
        Building building = new Building();
        BeanUtils.copyProperties(dto, building);
        this.save(building);
    }

    @Override
    public void update(Integer id, BuildingDTO dto) {
        Building building = this.getById(id);
        if (building == null) {
            throw new BusinessException("楼栋不存在");
        }
        // 检查楼栋名称唯一（排除自己）
        if (dto.getName() != null && !dto.getName().equals(building.getName())) {
            Long count = this.count(new LambdaQueryWrapper<Building>()
                    .eq(Building::getName, dto.getName())
                    .ne(Building::getId, id));
            if (count > 0) {
                throw new BusinessException("楼栋名称已存在");
            }
        }
        BeanUtils.copyProperties(dto, building);
        building.setId(id);
        this.updateById(building);
    }

    @Override
    public void remove(Integer id) {
        Building building = this.getById(id);
        if (building == null) {
            throw new BusinessException("楼栋不存在");
        }
        this.removeById(id);
    }
}
