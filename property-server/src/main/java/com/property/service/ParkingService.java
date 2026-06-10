package com.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.dto.PageQuery;
import com.property.dto.PageResult;
import com.property.entity.Parking;

import java.time.LocalDate;

public interface ParkingService extends IService<Parking> {

    PageResult<Parking> queryPage(PageQuery query);

    Parking getDetail(Integer id);

    void add(Parking parking);

    void update(Integer id, Parking parking);

    void remove(Integer id);

    /**
     * 分配车位给业主
     */
    void assign(Integer parkingId, Integer ownerId, LocalDate startDate, LocalDate endDate);

    /**
     * 收回车位
     */
    void revoke(Integer parkingId);

    /**
     * 业主查看自己的车位
     */
    Parking getByOwnerId(Integer ownerId);
}
