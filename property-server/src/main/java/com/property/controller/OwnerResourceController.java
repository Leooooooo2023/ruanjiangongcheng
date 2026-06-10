package com.property.controller;

import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.dto.Result;
import com.property.entity.Building;
import com.property.entity.Owner;
import com.property.entity.Parking;
import com.property.service.BuildingService;
import com.property.service.OwnerService;
import com.property.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 业主端专属接口 — 查看自己的车位、楼栋信息（只读）
 */
@RestController
@RequestMapping("/api/owner")
public class OwnerResourceController {

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private OwnerService ownerService;

    /**
     * 业主查看自己的车位
     */
    @GetMapping("/parking")
    public Result<Parking> myParking(HttpServletRequest request) {
        Integer ownerId = (Integer) request.getAttribute("userId");
        Parking parking = parkingService.getByOwnerId(ownerId);
        return Result.success(parking);
    }

    /**
     * 业主查看自己的楼栋 + 住房信息
     */
    @GetMapping("/building")
    public Result<Map<String, Object>> myBuilding(HttpServletRequest request) {
        Integer ownerId = (Integer) request.getAttribute("userId");
        Owner owner = ownerService.getDetail(ownerId);
        Map<String, Object> data = new HashMap<>();
        data.put("unit", owner.getUnit());
        data.put("room", owner.getRoom());
        if (owner.getBuildingId() != null) {
            Building building = buildingService.getDetail(owner.getBuildingId());
            data.put("building", building);
        }
        return Result.success(data);
    }
}
