package com.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.dto.BuildingDTO;
import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.entity.Building;

public interface BuildingService extends IService<Building> {

    PageResult<Building> queryPage(PageQuery query);

    Building getDetail(Integer id);

    void add(BuildingDTO dto);

    void update(Integer id, BuildingDTO dto);

    void remove(Integer id);
}
