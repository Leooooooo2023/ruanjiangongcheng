package com.property.controller;

import com.property.dto.BuildingDTO;
import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.dto.Result;
import com.property.entity.Building;
import com.property.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buildings")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @GetMapping
    public Result<PageResult<Building>> list(PageQuery query) {
        return Result.success(buildingService.queryPage(query));
    }

    /**
     * 获取全部楼栋（不分页，供下拉选择）
     */
    @GetMapping("/all")
    public Result<List<Building>> all() {
        return Result.success(buildingService.list());
    }

    @GetMapping("/{id}")
    public Result<Building> detail(@PathVariable Integer id) {
        return Result.success(buildingService.getDetail(id));
    }

    @PostMapping
    public Result<Void> add(@Validated @RequestBody BuildingDTO dto) {
        buildingService.add(dto);
        return Result.success("新增成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @Validated @RequestBody BuildingDTO dto) {
        buildingService.update(id, dto);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Integer id) {
        buildingService.remove(id);
        return Result.success("删除成功", null);
    }
}
