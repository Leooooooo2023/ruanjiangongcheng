package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.dao.ParkingApplicationMapper;
import com.property.entity.Parking;
import com.property.entity.ParkingApplication;
import com.property.exception.BusinessException;
import com.property.service.ParkingApplicationService;
import com.property.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParkingApplicationServiceImpl extends ServiceImpl<ParkingApplicationMapper, ParkingApplication>
        implements ParkingApplicationService {

    @Autowired
    private ParkingApplicationMapper parkingApplicationMapper;

    @Autowired
    private ParkingService parkingService;

    @Override
    @Transactional
    public ParkingApplication apply(Integer parkingId, Integer ownerId) {
        // 检查是否可以申请
        String reason = checkCanApply(parkingId, ownerId);
        if (reason != null) {
            throw new BusinessException(reason);
        }

        ParkingApplication app = new ParkingApplication();
        app.setParkingId(parkingId);
        app.setOwnerId(ownerId);
        app.setStatus(0); // 待审核
        this.save(app);
        return app;
    }

    @Override
    public List<ParkingApplication> listAll() {
        return parkingApplicationMapper.selectAllWithDetails();
    }

    @Override
    public List<ParkingApplication> listByOwner(Integer ownerId) {
        return parkingApplicationMapper.selectByOwnerId(ownerId);
    }

    @Override
    @Transactional
    public void approve(Integer applicationId) {
        ParkingApplication app = this.getById(applicationId);
        if (app == null) {
            throw new BusinessException("申请不存在");
        }
        if (app.getStatus() != 0) {
            throw new BusinessException("该申请已处理");
        }

        // 更新申请状态
        app.setStatus(1);
        this.updateById(app);

        // 分配车位给业主（默认租期：当前日期起一年）
        java.time.LocalDate startDate = java.time.LocalDate.now();
        java.time.LocalDate endDate = startDate.plusYears(1);
        parkingService.assign(app.getParkingId(), app.getOwnerId(), startDate, endDate);
    }

    @Override
    @Transactional
    public void reject(Integer applicationId) {
        ParkingApplication app = this.getById(applicationId);
        if (app == null) {
            throw new BusinessException("申请不存在");
        }
        if (app.getStatus() != 0) {
            throw new BusinessException("该申请已处理");
        }

        app.setStatus(2);
        this.updateById(app);
    }

    @Override
    public String checkCanApply(Integer parkingId, Integer ownerId) {
        // 1. 检查车位是否存在
        Parking parking = parkingService.getById(parkingId);
        if (parking == null) {
            return "车位不存在";
        }

        // 2. 检查车位是否已被占用
        if (parking.getStatus() == 1) {
            // 如果正好是该业主自己的车位，不允许再次申请
            if (parking.getOwnerId() != null && parking.getOwnerId().equals(ownerId)) {
                return "这是您的车位，无需重复申请";
            }
            return "该车位已被占用";
        }

        // 3. 检查业主是否已有车位
        Parking ownParking = parkingService.getByOwnerId(ownerId);
        if (ownParking != null) {
            return "您已拥有车位（" + ownParking.getNumber() + "），不可重复申请";
        }

        // 4. 检查业主是否已有待审核申请
        LambdaQueryWrapper<ParkingApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParkingApplication::getOwnerId, ownerId)
               .eq(ParkingApplication::getStatus, 0);
        long pendingCount = this.count(wrapper);
        if (pendingCount > 0) {
            return "您已有申请正在审核中，请等待审核结果";
        }

        // 5. 检查该车位是否已被他人申请（待审核）
        ParkingApplication pendingApp = parkingApplicationMapper.selectPendingByParkingId(parkingId);
        if (pendingApp != null && !pendingApp.getOwnerId().equals(ownerId)) {
            return "该车位已被他人申请，正在审核中";
        }

        return null; // 可以申请
    }
}
