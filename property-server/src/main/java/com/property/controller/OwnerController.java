package com.property.controller;

import com.property.dto.OwnerDTO;
import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.dto.Result;
import com.property.entity.Owner;
import com.property.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    @GetMapping
    public Result<PageResult<Owner>> list(PageQuery query) {
        return Result.success(ownerService.queryPage(query));
    }

    /**
     * 获取全部业主（不分页，含楼栋名称，供下拉搜索选择）
     */
    @GetMapping("/all")
    public Result<List<Owner>> all() {
        List<Owner> list = ownerService.getAllOwners();
        // 隐藏密码
        list.forEach(o -> o.setPassword(null));
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Owner> detail(@PathVariable Integer id) {
        return Result.success(ownerService.getDetail(id));
    }

    @PostMapping
    public Result<Void> add(@Validated @RequestBody OwnerDTO dto) {
        ownerService.add(dto);
        return Result.success("新增成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @Validated @RequestBody OwnerDTO dto) {
        ownerService.update(id, dto);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Integer id) {
        ownerService.remove(id);
        return Result.success("删除成功", null);
    }
}
