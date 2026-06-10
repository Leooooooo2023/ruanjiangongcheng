package com.property.controller;

import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.dto.Result;
import com.property.entity.Parking;
import com.property.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/parkings")
public class ParkingController {

    @Autowired
    private ParkingService parkingService;

    @GetMapping
    public Result<PageResult<Parking>> list(PageQuery query) {
        return Result.success(parkingService.queryPage(query));
    }

    @GetMapping("/{id}")
    public Result<Parking> detail(@PathVariable Integer id) {
        return Result.success(parkingService.getDetail(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Parking parking) {
        parkingService.add(parking);
        return Result.success("新增成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody Parking parking) {
        parkingService.update(id, parking);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Integer id) {
        parkingService.remove(id);
        return Result.success("删除成功", null);
    }

    /**
     * 分配车位给业主
     */
    @PutMapping("/{id}/assign")
    public Result<Void> assign(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        Integer ownerId = (Integer) params.get("ownerId");
        String startDateStr = (String) params.get("startDate");
        String endDateStr = (String) params.get("endDate");
        LocalDate startDate = startDateStr != null ? LocalDate.parse(startDateStr) : null;
        LocalDate endDate = endDateStr != null ? LocalDate.parse(endDateStr) : null;
        parkingService.assign(id, ownerId, startDate, endDate);
        return Result.success("分配成功", null);
    }

    /**
     * 收回车位
     */
    @PutMapping("/{id}/revoke")
    public Result<Void> revoke(@PathVariable Integer id) {
        parkingService.revoke(id);
        return Result.success("收回成功", null);
    }
}
