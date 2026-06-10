package com.property.controller;

import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.dto.ParkingApplicationDTO;
import com.property.dto.Result;
import com.property.entity.Building;
import com.property.entity.Owner;
import com.property.entity.Parking;
import com.property.entity.ParkingApplication;
import com.property.service.BuildingService;
import com.property.service.OwnerService;
import com.property.service.ParkingApplicationService;
import com.property.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private ParkingApplicationService parkingApplicationService;

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

    // ==================== 停车位申请相关 ====================

    /**
     * 业主查看所有停车位（地图展示用）
     */
    @GetMapping("/parkings")
    public Result<PageResult<Parking>> allParkings(PageQuery query) {
        return Result.success(parkingService.queryPage(query));
    }

    /**
     * 业主检查某个车位是否可以申请（含车位详情和申请状态判断）
     */
    @GetMapping("/parkings/{id}/check")
    public Result<Map<String, Object>> checkParking(@PathVariable Integer id, HttpServletRequest request) {
        Integer ownerId = (Integer) request.getAttribute("userId");
        Parking parking = parkingService.getById(id);
        if (parking == null) {
            return Result.error("车位不存在");
        }

        String reason = parkingApplicationService.checkCanApply(id, ownerId);
        boolean canApply = (reason == null);

        // 检查该车位的申请中状态（该车位是否有人申请）
        ParkingApplication pendingApp = null;
        try {
            // 通过 mapper 直接查（如果 service 层没有暴露这个方法）
            pendingApp = parkingApplicationService.getBaseMapper().selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ParkingApplication>()
                    .eq(ParkingApplication::getParkingId, id)
                    .eq(ParkingApplication::getStatus, 0)
            ).stream().findFirst().orElse(null);
        } catch (Exception e) {
            // ignore
        }

        Map<String, Object> data = new HashMap<>();
        data.put("parking", parking);
        data.put("canApply", canApply);
        data.put("reason", reason == null ? "" : reason);
        // 申请状态：pending=待审核, approved=该业主已通过, rejected=该业主被拒绝, none=无申请记录
        String applyStatus = "none";
        if (pendingApp != null && pendingApp.getOwnerId().equals(ownerId)) {
            applyStatus = "pending";
        }
        data.put("applyStatus", applyStatus);
        data.put("pendingApp", pendingApp);

        return Result.success(data);
    }

    /**
     * 业主提交停车位申请
     */
    @PostMapping("/parking-applications")
    public Result<ParkingApplication> apply(@RequestBody ParkingApplicationDTO dto, HttpServletRequest request) {
        Integer ownerId = (Integer) request.getAttribute("userId");
        ParkingApplication app = parkingApplicationService.apply(dto.getParkingId(), ownerId);
        return Result.success("申请已提交，请等待管理员审核", app);
    }

    /**
     * 业主查看自己的申请列表
     */
    @GetMapping("/parking-applications")
    public Result<List<ParkingApplication>> myApplications(HttpServletRequest request) {
        Integer ownerId = (Integer) request.getAttribute("userId");
        return Result.success(parkingApplicationService.listByOwner(ownerId));
    }
}
