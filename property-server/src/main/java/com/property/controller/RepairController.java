package com.property.controller;

import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.dto.RepairDTO;
import com.property.dto.Result;
import com.property.entity.Repair;
import com.property.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RepairController {

    @Autowired
    private RepairService repairService;

    // ===== 管理员接口 =====

    @GetMapping("/repairs")
    public Result<PageResult<Repair>> list(PageQuery query) {
        return Result.success(repairService.queryPage(query));
    }

    @GetMapping("/repairs/{id}")
    public Result<Repair> detail(@PathVariable Integer id) {
        return Result.success(repairService.getDetail(id));
    }

    @PutMapping("/repairs/{id}/assign")
    public Result<Void> assignEmployee(@PathVariable Integer id, @RequestBody Map<String, Integer> params) {
        repairService.assignEmployee(id, params.get("employeeId"));
        return Result.success("分配成功", null);
    }

    @PutMapping("/repairs/{id}/status")
    public Result<Void> updateStatus(@PathVariable Integer id, @RequestBody Map<String, Integer> params) {
        repairService.updateStatus(id, params.get("status"));
        return Result.success("状态更新成功", null);
    }

    // ===== 业主接口 =====

    @GetMapping("/owner/repairs")
    public Result<PageResult<Repair>> ownerList(PageQuery query, HttpServletRequest request) {
        Integer ownerId = (Integer) request.getAttribute("userId");
        return Result.success(repairService.queryByOwner(ownerId, query));
    }

    @PostMapping("/owner/repairs")
    public Result<Void> submit(@Validated @RequestBody RepairDTO dto, HttpServletRequest request) {
        Integer ownerId = (Integer) request.getAttribute("userId");
        repairService.submit(ownerId, dto);
        return Result.success("提交成功", null);
    }

    @PutMapping("/owner/repairs/{id}/rate")
    public Result<Void> rate(@PathVariable Integer id, @RequestBody Map<String, Integer> params, HttpServletRequest request) {
        Integer ownerId = (Integer) request.getAttribute("userId");
        repairService.rate(ownerId, id, params.get("rating"));
        return Result.success("评价成功", null);
    }
}
