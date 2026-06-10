package com.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.entity.ParkingApplication;

import java.util.List;

public interface ParkingApplicationService extends IService<ParkingApplication> {

    /**
     * 业主提交申请
     */
    ParkingApplication apply(Integer parkingId, Integer ownerId);

    /**
     * 管理员查看所有申请
     */
    List<ParkingApplication> listAll();

    /**
     * 业主查看自己的申请
     */
    List<ParkingApplication> listByOwner(Integer ownerId);

    /**
     * 审核通过
     */
    void approve(Integer applicationId);

    /**
     * 审核拒绝
     */
    void reject(Integer applicationId);

    /**
     * 检查业主是否可以申请该车位
     * @return null 表示可以申请；否则返回不能申请的原因
     */
    String checkCanApply(Integer parkingId, Integer ownerId);
}
