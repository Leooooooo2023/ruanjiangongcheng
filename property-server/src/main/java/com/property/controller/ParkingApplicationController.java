package com.property.controller;

import com.property.dto.ParkingApplicationDTO;
import com.property.dto.Result;
import com.property.entity.ParkingApplication;
import com.property.service.ParkingApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 停车位申请 — 管理员端接口
 */
@RestController
@RequestMapping("/api/parking-applications")
public class ParkingApplicationController {

    @Autowired
    private ParkingApplicationService parkingApplicationService;

    /**
     * 管理员查看所有申请
     */
    @GetMapping
    public Result<List<ParkingApplication>> list() {
        return Result.success(parkingApplicationService.listAll());
    }

    /**
     * 审核通过
     */
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Integer id) {
        parkingApplicationService.approve(id);
        return Result.success("审核通过，车位已分配", null);
    }

    /**
     * 审核拒绝
     */
    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Integer id) {
        parkingApplicationService.reject(id);
        return Result.success("已拒绝", null);
    }
}
